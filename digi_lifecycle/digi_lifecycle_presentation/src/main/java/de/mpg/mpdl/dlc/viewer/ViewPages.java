package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.richfaces.component.UITree;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.MetsDiv;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewPages", pattern = "/view/#{viewPages.volumeId}/#{viewPages.selectedPageNumber}/#{viewPages.viewType}", viewId = "/viewPages.xhtml")
public class ViewPages{
	
	@URLQueryParameter("fm")
	private String fulltextMatches;
	
	enum ViewType{
		SINGLE, RECTO_VERSO, FULLTEXT
	}
	
	private String volumeId;
	
	private Volume volume;
	
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private static Logger logger = Logger.getLogger(ViewPages.class);

	private int selectedPageNumber;
	
	private Page selectedPage;
	
	private Page selectedRightPage;
	
	private PbOrDiv selectedDiv;
	
	private List<Page> pageList = new ArrayList<Page>();
	
	private ViewType viewType = ViewType.RECTO_VERSO;
	private boolean hasSelected = false;
	
	private boolean showTree = false;
	
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
		{   
			try {
				this.volume = volServiceBean.loadCompleteVolume(volumeId, null);
				
			} catch (Exception e) {
				MessageHelper.errorMessage("Problem while loading volume");
			}
		}
		volumeLoaded();
	}
	
	
	protected void volumeLoaded() {
		Page pageforNumber = volume.getPages().get(getSelectedPageNumber()-1);
		this.setSelectedPage(pageforNumber);
		 
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
			
			/*
			teiTree.get.
			componentState = (TreeState) sampleTreeBinding.getComponentState();
			try {
				componentState.expandAll(sampleTreeBinding);
			} catch (IOException e) {
				e.printStackTrace();
			}
		*/
			
		}

			
		
		
		
		
		try
		{
			if(getVolume().getPagedTei() != null && hasSelected == false)
			{
				if(getSelectedPage()!=null)
				{
					this.selectedDiv = volServiceBean.getDivForPage(volume, getSelectedPage());
				}
				else if (getSelectedRightPage()!=null)
				{
					this.selectedDiv = volServiceBean.getDivForPage(volume, getSelectedRightPage());
				}
				
			}
			else
			{
				hasSelected = false;
			}
		} 
		
		catch (Exception e) 
		{
			logger.error("Structural element cannot be selected for this page.", e);
			MessageHelper.errorMessage("Structural element cannot be selected for this page.");
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


	public void setSelectedPage(Page selectedPage) {
		this.selectedPage = selectedPage;
	}

	public Page getSelectedPage() {
		return selectedPage;
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
	
	public void goTo(PbOrDiv div)
	{
		logger.info("Go to div " + div.getId());
		Page p;
		try {
			p = volServiceBean.getPageForDiv(volume, div);
		} catch (Exception e) {
			p = volume.getPages().get(0);
		}
		selectedPageNumber = volume.getPages().indexOf(p) + 1 ;
		setSelectedDiv(div);
		setHasSelected(true);
		loadVolume();
		
	}
	
	public boolean isHasSelected() {
		return hasSelected;
	}

	public void setHasSelected(boolean hasSelected) {
		this.hasSelected = hasSelected;
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

	public PbOrDiv getSelectedDiv() {
		return selectedDiv;
	}

	public void setSelectedDiv(PbOrDiv selectedDiv) {
		this.selectedDiv = selectedDiv;
	}
	
	
	public String getXhtmlForPage() throws Exception
	{
		String teiHtml = volServiceBean.getXhtmlForPage(getSelectedPage(), volume.getPagedTei());
		//logger.info(tei);
		return teiHtml;
	}
	
	public String[] getFulltextMatchesAsArray()
	{
		String[] matches = new String[0];
		if(fulltextMatches!=null)
		{
			matches = fulltextMatches.split(",");
		}
		
		return matches;
		//return new String[]{"folgenden Stücke", "erhält"};
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

	}
	
	public void switchViewType(String viewType)
	{
		this.viewType =ViewType.valueOf(viewType);
		loadVolume();	
	}

	public String getFulltextMatches() {
		return fulltextMatches;
	}

	public void setFulltextMatches(String fulltextMatches) {
		this.fulltextMatches = fulltextMatches;
	}

	
	public boolean isShowTree() {
		return showTree;
	}

	public void setShowTree(boolean showTree) {
		this.showTree = showTree;
	}


	public Volume getVolume() {
		return volume;
	}


	public void setVolume(Volume volume) {
		this.volume = volume;
	}


	public String getVolumeId() {
		return volumeId;
	}


	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}	

}
