package de.mpg.mpdl.dlc.viewer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.tei.TEITransformer;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.MetsDiv;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewPages", pattern = "/view/#{viewPages.volumeId}/#{viewPages.selectedPageNumber}", viewId = "/viewPages.xhtml")
public class ViewPages {
	
	enum ViewType{
		SINGLE, RECTO_VERSO, FULLTEXT
	}
	
	private static Logger logger = Logger.getLogger(ViewPages.class);
	@EJB
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private String volumeId;
	
	private Volume volume;
	
	private int selectedPageNumber;
	
	private Page selectedPage;
	private Page selectedRightPage;
	
	private MetsDiv selectedDiv;
	
	private List<Page> pageList = new ArrayList<Page>();
	
	private ViewType viewType = ViewType.SINGLE;
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		try { 
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{    
				this.volume = volServiceBean.retrieveVolume(volumeId, null);
				volServiceBean.loadTei(volume, null);
				volServiceBean.loadPagedTei(volume, null);
				
				logger.info("Load new book" + volumeId);
			}
			Page pageforNumber = volume.getPages().get(getSelectedPageNumber()-1);
			
			if(ViewType.RECTO_VERSO.equals(viewType))
			{
				if(pageforNumber.getType()==null || pageforNumber.getType().isEmpty() || pageforNumber.getType().equals("page"))
				{
					if(getSelectedPageNumber() % 2 == 0)
					{
						this.setSelectedPage(pageforNumber);
						
						try {
							this.setSelectedRightPage(volume.getPages().get(getSelectedPageNumber()));
						} catch (IndexOutOfBoundsException e) {
							this.setSelectedRightPage(null);
						}
					}
					else
					{
						this.setSelectedRightPage(pageforNumber);
						
						try {
							this.setSelectedPage(volume.getPages().get(getSelectedPageNumber()-2));
						} catch (IndexOutOfBoundsException e) {
							this.setSelectedPage(null);
						}
					}
				}
			
				
			}
			else
			{
				this.setSelectedPage(pageforNumber);
			}
			
			
			List<MetsDiv> divForPage = volume.getDivMap().get(pageforNumber);

			
			
			if(divForPage!=null && divForPage.size()>0)
			{	
				this.selectedDiv = divForPage.get(divForPage.size()-1).getParentDiv();
			}
			
			//this.selectedDiv
			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage("Problem while loading volume");
		}
		
	}
	
	public List<Page> getPageList() throws Exception
	{
		pageList = volume.getPages();
		return pageList;
	}
	public void setPageList(List<Page> pageList)
	{
		this.pageList = pageList;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setSelectedPage(Page selectedPage) {
		this.selectedPage = selectedPage;
	}

	public Page getSelectedPage() {
		return selectedPage;
	}
	
	

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setSelectedPageNumber(int selectedPageNumber) {
		this.selectedPageNumber = selectedPageNumber;
	}

	public int getSelectedPageNumber() {
		return selectedPageNumber;
	}
	
	public String goToNextPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        if(ViewType.RECTO_VERSO.equals(viewType) && volume.getPages().size()>= selectedPageNumber + 2)
        {
        	
        		selectedPageNumber += 2;
        		loadVolume();
        }
        
        else if (volume.getPages().size()>= selectedPageNumber + 1)
        {
           selectedPageNumber ++;
           loadVolume();
        }
        return "";
	}
	
	public String goToPreviousPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) - 1;
		
		if(ViewType.RECTO_VERSO.equals(viewType) && selectedPageNumber - 2 > 0)
        {
			selectedPageNumber -= 2;
			loadVolume();
        }
		
		else if (selectedPageNumber -1 > 0)
        {
           selectedPageNumber --;
           loadVolume();
        }
        return null;
	} 
	
	public String goToLastPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = volume.getPages().size();
        loadVolume();
        return null;
	}
	
	public String goToFirstPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = 1; 
        loadVolume();
        return null;
	}
	
	public void goTo(MetsDiv div)
	{
		logger.info("Go to div " + div.getId());
		MetsDiv nextPage = getNextPage(div);
		List<Page> pages = volume.getPageMap().get(nextPage);
		Page p = pages.get(0);
		selectedPageNumber = volume.getPages().indexOf(p) + 1 ;
		loadVolume();
	}
	
	public void goToPage(Page p)
	{
		logger.info("Go to page " + p.getId());
		selectedPageNumber = volume.getPages().indexOf(p) + 1 ;
		loadVolume();
	}
	
	public MetsDiv getNextPage(MetsDiv div)
	{
		if(div.getType()!=null && div.getType().equals("page"))
		{
			return div;
		}
		else
		{	
			
			for(MetsDiv subDiv : div.getDivs())
			{
				return getNextPage(subDiv);
			}
			
		}
		return null;
			
	}

	public MetsDiv getSelectedDiv() {
		return selectedDiv;
	}

	public void setSelectedDiv(MetsDiv selectedDiv) {
		this.selectedDiv = selectedDiv;
	}
	
	
	public String getXhtmlForPage() throws Exception
	{
		String tei = volServiceBean.getXhtmlForPage(getSelectedPage(), volume.getPagedTei());
		//logger.info(tei);
		return tei;
	}

	public Page getSelectedRightPage() {
		return selectedRightPage;
	}

	public void setSelectedRightPage(Page selectedRightPage) {
		this.selectedRightPage = selectedRightPage;
	}

	public ViewType getViewType() {
		return viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
		loadVolume();
		//PrettyContext.getCurrentInstance().g
	}

	
	

	
	
	

}
