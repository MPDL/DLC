package de.mpg.mpdl.dlc.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;

import sun.tools.tree.ArrayAccessExpression;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.reference.ItemRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.om.item.Item;
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
	
	
	public CreateVolumeThread(String operation, String contentModel, String contextId, String multiVolumeId,
			String userHandle, ModsMetadata modsMetadata,
			List<IngestImage> images, DiskFileItem footer,
			DiskFileItem teiFile, DiskFileItem codicologicalFile) 
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
	}
	
	
	
	public void run()
	{
		logger.info("Creating new volume/monograph");
		
		Volume volume = new Volume();
		Item item = vsb.createNewEmptyItem(contentModel,contextId, userHandle, modsMetadata);
		
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
					parent = vsb.releaseVolume(parent.getItem().getObjid(), userHandle);
				
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
				
				vsb.uploadImagesAndCreateMets(images, footer, item.getObjid(), volume);
				
				long time = System.currentTimeMillis()-start;
				logger.info("Time to upload images: " + time);
			
			
				/*
				InputStream teiInputStream = null;
				if(teiFile != null)
					teiInputStream = teiFile.getInputStream();
					*/
				volume = vsb.updateVolume(volume, userHandle, teiFile, codicologicalFile, true);
			
				if(operation.equalsIgnoreCase("release"))
					volume = vsb.releaseVolume(volume.getItem().getObjid(), userHandle);
				
				
				//Delete temp files
				try
				{
					if(images!=null)
					{
						for(IngestImage image : images)
						{
							image.getDiskFileItem().getStoreLocation().delete();
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
		
			catch (Exception e) 
			{
				logger.error("Error while creating Volume. Trying to rollback", e);
				vsb.rollbackCreation(volume, userHandle);
				//throw new Exception(e);
			}
			
			//return volume;
	}

}
