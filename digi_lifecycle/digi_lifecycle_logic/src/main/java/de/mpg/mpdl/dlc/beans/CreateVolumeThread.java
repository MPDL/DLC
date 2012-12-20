package de.mpg.mpdl.dlc.beans;

import java.util.ArrayList;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.reference.ItemRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;
import de.mpg.mpdl.dlc.vo.IngestImage;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Mets;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;

public class CreateVolumeThread extends Thread implements Runnable{
	
	private static Logger logger = Logger.getLogger(CreateVolumeThread.class);
	private VolumeServiceBean vsb = new VolumeServiceBean();
	
	
	
	
	private String contentModel;
	private String contextId;
	private String multiVolumeId;
	private String userHandle;
	private ModsMetadata modsMetadata;
	private List<IngestImage> images;
	private DiskFileItem footer;
	private DiskFileItem teiFile;
	private DiskFileItem codicologicalFile;
	private String operation;
	
	private DatabaseItem ingestMainProcess;
	private EntityManager em;
	
	

	
	public CreateVolumeThread(String operation, String contentModel, String contextId, String multiVolumeId,
			String userHandle, ModsMetadata modsMetadata,
			List<IngestImage> images, DiskFileItem footer,
			DiskFileItem teiFile, DiskFileItem codicologicalFile, DatabaseItem dbItem) 
	{
		super();
		this.operation = operation;
		this.contentModel = contentModel;
		this.contextId = contextId;
		this.multiVolumeId = multiVolumeId;
		this.userHandle = userHandle;
		this.modsMetadata = modsMetadata;
		this.images = images;
		this.footer = footer;
		this.teiFile = teiFile;
		this.codicologicalFile = codicologicalFile;
		this.ingestMainProcess = dbItem;
		
		
		EntityManagerFactory emf = VolumeServiceBean.getEmf();
		this.em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(ingestMainProcess);
		em.getTransaction().commit();
		this.setUncaughtExceptionHandler(new IngestExceptionHandler(em, ingestMainProcess));
	}
	
	
	
	public void run()
	{
		CreateVolumeServiceBean cvsb = new CreateVolumeServiceBean(ingestMainProcess, em);
		
		logger.info("Creating new volume/monograph");

		
		
		Volume volume = new Volume();
		
		Item item = cvsb.createNewEmptyItem(contentModel,contextId, userHandle, modsMetadata);
		
		this.setName(item.getObjid());
		
		MetadataRecords mdRecs = item.getMetadataRecords();
		
		if(mdRecs==null)
		{
			mdRecs = new MetadataRecords();
			item.setMetadataRecords(mdRecs);
		}
		
		volume.setItem(item);
		volume.setProperties(volume.getItem().getProperties());
		volume.setModsMetadata(modsMetadata);
		
		Mets metsData = new Mets();
		volume.setMets(metsData);
		
		try{

			
			Volume parent = null;

			if(multiVolumeId != null)
			{
				parent = vsb.retrieveVolume(multiVolumeId, userHandle);
				List<String> volumeList = new ArrayList<String>();
				volumeList.add(item.getObjid());
				vsb.updateMultiVolumeFromId(parent.getItem().getObjid(), volumeList, userHandle);
				parent = vsb.retrieveVolume(multiVolumeId, userHandle);
				if(operation.equalsIgnoreCase("release"))
					parent = cvsb.releaseVolume(parent.getItem().getObjid(), userHandle);
				
				//Also add the md record of the multivolume to each volume for indexing etc.
				MetadataRecord mdRecMv = new MetadataRecord("multivolume");
				mdRecMv.setContent(parent.getItem().getMetadataRecords().get("escidoc").getContent());
				item.getMetadataRecords().add(mdRecMv);
				
				//Add the relation to the multivolume
				Relations relations = new Relations();
				Reference ref = new ItemRef("/ir/item/"+parent.getItem().getObjid(),"Item "+ parent.getItem().getObjid());
				
				Relation relation = new Relation(ref);
				relation.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isPartOf");
				relations.add(relation);
				item.setRelations(relations);
				
			}

				//Convert and upload images 
				long start = System.currentTimeMillis();
				IngestImage ingestImageFooter = null;
				if(footer!=null)
				{
					ingestImageFooter = new IngestImage(footer);
				}
				
				cvsb.uploadImagesAndCreateMets(images, ingestImageFooter, item.getObjid(), volume, userHandle);
				
				long time = System.currentTimeMillis()-start;
				logger.info("Time to upload images: " + time);
			
			
				//new ArrayList<String>().get(5);
				
				/*
				InputStream teiInputStream = null;
				if(teiFile != null)
					teiInputStream = teiFile.getInputStream();
					*/
				volume = cvsb.updateVolume(volume, userHandle, new IngestImage(teiFile), new IngestImage(codicologicalFile), true);
			
				if(operation.equalsIgnoreCase("release"))
					volume = cvsb.releaseVolume(volume.getItem().getObjid(), userHandle);
				
				
				
				ingestMainProcess.setIngestStatus(IngestStatus.READY);
				
				
			}
		
			catch (Exception e) 
			{
				ingestMainProcess.setIngestStatus(IngestStatus.ERROR);
				logger.error("Error while creating Volume. Trying to rollback", e);
				cvsb.rollbackCreation(volume, userHandle);
				//throw new Exception(e);
			}
			finally
			{
				em.getTransaction().begin();
				em.merge(ingestMainProcess);
				em.getTransaction().commit();
				//Delete temp files
				try
				{
					if(images!=null)
					{
						for(IngestImage image : images)
						{
							image.getFile().delete();
						}
					}
					
					if(codicologicalFile!=null)
					{
						codicologicalFile.getStoreLocation().delete();
					}
					if(footer!=null)
					{
						footer.getStoreLocation().delete();
					}
					if(teiFile!=null)
					{
						teiFile.getStoreLocation().delete();
					}
				}
				catch(Exception e)
				{
					
					logger.error("Error while deleting temp files", e);
				}
			}
			
			
	}
	
	
	public class IngestExceptionHandler implements Thread.UncaughtExceptionHandler
	{
		
		private EntityManager em;
		private DatabaseItem dbItem;

		public IngestExceptionHandler(EntityManager em, DatabaseItem dbItem)
		{
			this.em = em;
			this.dbItem = dbItem; 
		}

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			dbItem.setIngestStatus(IngestStatus.ERROR);
			em.getTransaction().begin();
			em.persist(dbItem);
			em.getTransaction().commit();
			em.close();
			
			
		}
		
	}
	

}
