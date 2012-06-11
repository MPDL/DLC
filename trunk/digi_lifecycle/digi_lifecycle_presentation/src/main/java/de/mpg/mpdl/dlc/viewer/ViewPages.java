package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.MetsDiv;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewPages", pattern = "/view/#{viewPages.volumeId}/#{viewPages.viewTypeText}/#{viewPages.selectedPageNumber}", viewId = "/viewPages.xhtml", onPostback=false)
public class ViewPages{
	
	@URLQueryParameter("fm")
	private String fulltextMatches;
	
	enum ViewType{
		SINGLE, RECTO_VERSO, FULLTEXT
	}
	
	private String volumeId;
	
	private Volume volume;
	
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private OrganizationalUnitServiceBean orgServiceBean = new OrganizationalUnitServiceBean();
	
	private ApplicationBean appBean = new ApplicationBean();
	
	private Context context;
	
	private static Logger logger = Logger.getLogger(ViewPages.class);

	private int selectedPageNumber;
	
	private Page selectedPage;
	
	private Page selectedRightPage;
	
	private PbOrDiv selectedDiv;
	
	private List<Page> pageList = new ArrayList<Page>();
	
	private ViewType viewType = ViewType.RECTO_VERSO;
	
	private String viewTypeText = viewTypeToText(viewType);
	
	private boolean hasSelected = false;
	
	private boolean showTree = false;
	
	private Map<String, String> clientIdMap = new HashMap<String, String>();
	
	private List<SelectItem> pageListMenu = new ArrayList<SelectItem>();
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		
		
		if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
		{   
			try {
				
				this.volume = volServiceBean.loadCompleteVolume(volumeId, getLoginBean().getUserHandle());
				this.context = contextServiceBean.retrieveContext(volume.getItem().getProperties().getContext().getObjid(), null);

				//Set the logo of application to collection logo
				Organization volumeOu = orgServiceBean.retrieveOrganization(this.context.getProperties().getOrganizationalUnitRefs().getFirst().getObjid());
				if (volumeOu.getDlcMd().getFoafOrganization().getImgURL() != null && !volumeOu.getDlcMd().getFoafOrganization().getImgURL().equals(""))
					{appBean.setLogoLink(volumeOu.getId());
					appBean.setLogoUrl(volumeOu.getDlcMd().getFoafOrganization().getImgURL());
					appBean.setLogoTlt(appBean.getResource("Tooltips", "main_home")
							.replace("$1", volumeOu.getEscidocMd().getTitle()));}
				
				initPageListMenu();
				
			} catch (Exception e) {
				MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_loadVolume"));
			}
		}
		volumeLoaded();
	}
	
	private void initPageListMenu()
	{
		this.pageListMenu.clear();
		for(Page p : volume.getPages())
		{
			pageListMenu.add(new SelectItem(p.getOrder()+1, "Seite " + p.getOrderLabel() + " / Bild " + (p.getOrder()+1)));
		}
	}
	
	public void pageNumberChanged()
	{
		System.out.println("Go to page changed!!!!");
		loadVolume();
	}
	
	protected void volumeLoaded() {
		Page pageforNumber = volume.getPages().get(getSelectedPageNumber()-1);
		this.setSelectedPage(pageforNumber);
		this.setSelectedRightPage(null);
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
				PbOrDiv oldSelectedDiv = selectedDiv;
				if(getSelectedPage()!=null)
				{
					this.selectedDiv = volServiceBean.getDivForPage(volume, getSelectedPage());
				}
				else if (getSelectedRightPage()!=null)
				{
					this.selectedDiv = volServiceBean.getDivForPage(volume, getSelectedRightPage());
				}
				/*
				FacesContext fc = FacesContext.getCurrentInstance();
				if(fc.getPartialViewContext().isAjaxRequest())
				{
					if(selectedDiv!=null)
					{
						fc.getPartialViewContext().getRenderIds().add(clientIdMap.get(selectedDiv.getId()));
					}
					if(oldSelectedDiv!=null)
					{
						fc.getPartialViewContext().getRenderIds().add(clientIdMap.get(oldSelectedDiv.getId()));
					}
				}
				
				System.out.println(fc.getPartialViewContext().getRenderIds());
				*/
			}
			else
			{
				hasSelected = false;
			}
		} 
		
		catch (Exception e) 
		{
			logger.error("Structural element cannot be selected for this page.", e);
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongStructElem"));
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
	
	
	public String getXhtmlForPage()
	{
		try {
			String teiHtml = volServiceBean.getXhtmlForPage(getSelectedPage(), volume.getPagedTei());
			//logger.info(tei);
			return teiHtml;
		} catch (Exception e) {
			logger.error("Coulod not display fulltext for " + volumeId + " / Page " + selectedPageNumber, e);
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_ftDisplay"));
			return "";
		}
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
		this.viewTypeText = viewTypeToText(viewType);

	}
	
	public String getViewTypeText() {
		return viewTypeText;
	}

	public void setViewTypeText(String viewTypeText) {
		this.viewTypeText = viewTypeText;
		this.viewType = viewTypeTextToViewType(viewTypeText);
	}	
	
	private static String viewTypeToText(ViewType vt)
	{
		return vt.name().toLowerCase().replaceAll("_", "-");
	}
	
	private static ViewType viewTypeTextToViewType(String vt)
	{
		return ViewType.valueOf(vt.replaceAll("-", "_").toUpperCase());
	}
	
	public String switchViewType(String viewType)
	{
		setViewType(ViewType.valueOf(viewType));
		loadVolume();
		return "pretty:viewPages";
	}
	
	public String switchViewType(String viewType, Page pageToGoTo)
	{
		setViewType(ViewType.valueOf(viewType));
		this.selectedPageNumber = pageToGoTo.getOrder() + 1;
		loadVolume();
		return "pretty:viewPages";
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


	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		this.context = context;
	}

	public String addToClientIdMap(String divId, String clientId)
	{
		clientIdMap.put(divId, clientId);
		System.out.println("Added to client id map: " + divId + " " + clientId);
		return "";
	}


	public LoginBean getLoginBean() {
		return loginBean;
	}


	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}


	public List<SelectItem> getPageListMenu() {
		return pageListMenu;
	}


	public void setPageListMenu(List<SelectItem> pageListMenu) {
		this.pageListMenu = pageListMenu;
	}

}
