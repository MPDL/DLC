package de.mpg.mpdl.dlc.viewer;


import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.search.SearchBean;
import de.mpg.mpdl.dlc.search.SearchCriterion;
import de.mpg.mpdl.dlc.search.SortCriterion;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.search.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.search.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.organization.Organization;


@ManagedBean
@SessionScoped
@URLMapping(id = "ou", viewId = "/ou.xhtml", pattern = "/ou/#{viewOU.id}")
public class ViewOU {

	
	private static Logger logger = Logger.getLogger(ViewOU.class);
	
	private String id;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{applicationBean}")
	private ApplicationBean applicationBean;
	

	private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private Organization orga;
	
	private List<Volume> vols_Carousel = new ArrayList<Volume>();
	
	@URLAction(onPostback=false)
	public void loadOu()
	{    
		try{
			
//			if(orga==null || !id.equals(orga.getId()))
//			{
				this.orga = ouServiceBean.retrieveOrganization(id);
				if(vols_Carousel !=null)
					{this.vols_Carousel.clear();}
				
				SearchCriterion sc = null;
				List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
				List <Context> contextL = contextServiceBean.retrieveOUContexts(id);
				if (contextL != null && contextL.size()>0)
				{
					for(Context context : contextL)
					{
						sc = new SearchCriterion(SearchType.CONTEXT_ID, context.getObjid());
						scList.add(sc);
					}
					List<SortCriterion> sortList = new ArrayList<SortCriterion>();
					sortList.add(new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING));
					VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.VOLUME};
					SearchBean searchBean = new SearchBean();
					VolumeSearchResult res = searchBean.search(volTypes, scList, sortList, 10, 0);
					for(Volume vol : res.getVolumes())
					{ 
						
						if(vol.getPages() != null && vol.getPages().size()>0)
						{
							this.vols_Carousel.add(vol);
	
						}
					}
				}
			//}
			logger.info("load ou " + id);
		}catch(Exception e){
			logger.error("Problem with loading ou", e);
		}
		
	}
	
	public String save() throws Exception
	{  
		ouServiceBean.updateOU(orga, loginBean.getUserHandle());
		this.orga = ouServiceBean.retrieveOrganization(orga.getId());
		this.orga.setEditable(false);
		applicationBean.setOus(ouServiceBean.retrieveOUs());
		loginBean.getUser().setCreatedOrgas(ouServiceBean.retrieveOrgasCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
		return "pretty:ou";
	}
  
	public String edit()
	{  
		this.orga.setEditable(true);
		return "pretty:ou";
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


	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public Organization getOrga() {
		return orga;
	}

	public void setOrga(Organization orga) {
		this.orga = orga;
	}

	public List<Volume> getVols_Carousel() {
		return vols_Carousel;
	}

	public void setVols_Carousel(List<Volume> vols_Carousel) {
		this.vols_Carousel = vols_Carousel;
	}
	
	
	
}
