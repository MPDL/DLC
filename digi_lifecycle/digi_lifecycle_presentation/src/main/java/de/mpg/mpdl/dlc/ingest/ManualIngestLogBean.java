package de.mpg.mpdl.dlc.ingest;



import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.CreateVolumeServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "ingestLog", viewId = "/ingestLog.xhtml", pattern = "/ingestlog")
public class ManualIngestLogBean extends BasePaginatorBean<DatabaseItem> {
	private static Logger logger = Logger.getLogger(ManualIngestLogBean.class);
	


	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	private CreateVolumeServiceBean createVolServiceBean = new CreateVolumeServiceBean();
	


	
	//private List<SortCriterion> sortCriterionList = new ArrayList<SortCriterion>();	
	
	private CombinedSortCriterion selectedSortCriterion; 
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;



	private List<DatabaseItem> uploadedItems;
	
	
	public ManualIngestLogBean()
	{		
		super();
	}

	
	
	
  

	



	public int getTotalNumberOfRecords() 
	{
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() 
	{
		return "pretty:ingestLog";
	}
	public LoginBean getLoginBean() 
	{
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) 
	{
		this.loginBean = loginBean;
	}
	
	
	
	
	
	
	
	
	
	public boolean checkUploadComplete()
	{ 
		for(DatabaseItem item : uploadedItems)
		{
			if(IngestStatus.RUNNING.equals(item.getIngestStatus()))
			{
				return true;
			}
		}
		
		return false;

	}
	
	public synchronized void checkUploadComplete(DatabaseItem dbItem, int rowKey)
	{ 
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		DatabaseItem dbItemInDb = em.find(DatabaseItem.class, dbItem.getId());	
		dbItem = dbItemInDb;
		getCurrentPartList().set(rowKey, dbItemInDb);
		em.close();

	}











	@Override
	public List<DatabaseItem> retrieveList(int offset, int limit)
	{
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		
		Query countQuery =  em.createNamedQuery(DatabaseItem.ALL_ITEMS_BY_USER_ID_COUNT);
		countQuery.setParameter("userId", loginBean.getUser().getId());
		this.totalNumberOfRecords = ((Number)countQuery.getSingleResult()).intValue();
		
		TypedQuery<DatabaseItem> query = em.createNamedQuery(DatabaseItem.ALL_ITEMS_BY_USER_ID, DatabaseItem.class);
		query.setParameter("userId", loginBean.getUser().getId());
		query.setFirstResult(offset-1);
		query.setMaxResults(limit);
		List<DatabaseItem> resList = query.getResultList();
		this.uploadedItems = resList;
		em.close();
		//this.totalNumberOfRecords = uploadedItems.size();
		return uploadedItems;
	}










	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}










	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}



	public void deleteDbItem(DatabaseItem dbItem)
	{
		
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		DatabaseItem dbItemInDb = em.find(DatabaseItem.class, dbItem.getId());
		em.getTransaction().begin();
		em.remove(dbItemInDb);
		em.getTransaction().commit();
		em.close();
	}
	
	
	
	
	
}
