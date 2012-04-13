package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.search.SearchBean;
import de.mpg.mpdl.dlc.search.SearchCriterion;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.search.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.search.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.search.SortCriterion;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.mets.Page;

@ManagedBean
@SessionScoped
@URLMapping(id="collection", viewId="/collection.xhtml", pattern="/collection/#{viewCollection.id}")
public class ViewCollection {
	
	private static Logger logger = Logger.getLogger(ViewCollection.class);
	
	private String id;
	

	protected LoginBean loginBean;
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private Collection col;
	
	//volumes which schown in carousel
	private List<Volume> volumes = new ArrayList<Volume>();	


	
	@URLAction(onPostback=false)
	public void loadCollection()
	{
		
		try
		{
			if(col==null || !col.getId().equals(id))
			{
				this.col = contextServiceBean.retrieveCollection(id, null);
				if(volumes!=null)
					this.volumes.clear();

				SearchCriterion sc= new SearchCriterion(SearchType.CONTEXT_ID, id);
				List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
				scList.add(sc);
				

				List<SortCriterion> sortList = new ArrayList<SortCriterion>();
				sortList.add(new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING));
				VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.VOLUME};
				SearchBean searchBean = new SearchBean();
				VolumeSearchResult res = searchBean.search(volTypes, scList, sortList, 10, 0);
				
				for(Volume vol : res.getVolumes())
				{ 
					
					if(vol.getPages() != null && vol.getPages().size()>0)
					{
//						String pageUrl = vol.getPages().get(0).getContentIds();
//						this.pages.add(VolumeUtilBean.getImageServerUrl(pageUrl, "THUMBNAIL"));
						this.volumes.add(vol);

					}
				}
			}
			logger.info("load collection" + id);
		}catch(Exception e){
			logger.error("load collection", e);
		}
	}
	


	public List<Volume> getVolumes() {
		return volumes;
	}



	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}



	public Collection getCol() {
		return col;
	}

	public void setCol(Collection col) {
		this.col = col;
	}







	
	
	

}