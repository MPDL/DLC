package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.transform.dom.DOMSource;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.export.Export;
import de.mpg.mpdl.dlc.export.Export.ExportTypes;
import de.mpg.mpdl.dlc.export.ExportBean;
import de.mpg.mpdl.dlc.export.ExportServlet;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.MetsDiv;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

@ManagedBean
@ViewScoped
@URLMappings
	(mappings = {
			@URLMapping(id = "viewPagesWithoutNumber", pattern = "/view/#{viewPages.volumeId}/#{viewPages.viewTypeText}", viewId = "/viewPages.xhtml", onPostback=false),
			@URLMapping(id = "viewPages", pattern = "/view/#{viewPages.volumeId}/#{viewPages.viewTypeText}/#{viewPages.selectedPageNumber}", viewId = "/viewPages.xhtml", onPostback=false)
		
	})

public class ViewPages implements Observer{
	
	@URLQueryParameter("fm")
	private String fulltextMatches;
	
	@URLQueryParameter("dl")
	private String digilibQueryString;
	
	enum ViewType{
		SINGLE, RECTO_VERSO, FULLTEXT
	}
	
	private String volumeId;
	
	private Volume volume;
	
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private OrganizationalUnitServiceBean orgServiceBean = new OrganizationalUnitServiceBean();

	
	private Context context;
	
	private static Logger logger = Logger.getLogger(ViewPages.class);

	private int selectedPageNumber=0;
	
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
	
	private Map<PbOrDiv, Boolean> treeExpansionStateMap = new HashMap<PbOrDiv, Boolean>();
	
	private Organization volumeOu;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	private List<XdmNode> pbList;
	
	@PostConstruct
	public void postConstruct()
	{
		internationalizationHelper.addObserver(this);
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{     
 

		if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
		{   
			try {
				
				this.volume = volServiceBean.loadCompleteVolume(volumeId, getLoginBean().getUserHandle());
				this.context = contextServiceBean.retrieveContext(volume.getItem().getProperties().getContext().getObjid(), null);

				//Set the logo of application to collection logo
				volumeOu = orgServiceBean.retrieveOrganization(this.context.getProperties().getOrganizationalUnitRefs().getFirst().getObjid());
				if (volumeOu.getDlcMd().getFoafOrganization().getImgURL() != null && !volumeOu.getDlcMd().getFoafOrganization().getImgURL().equals(""))
					{sessionBean.setLogoLink(volumeOu.getId());
					sessionBean.setLogoUrl(volumeOu.getDlcMd().getFoafOrganization().getImgURL());
					sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home")
							.replace("$1", volumeOu.getEscidocMd().getTitle()));}
				
				initPageListMenu();
				if(volume.getTeiSd()!=null)
				{
					fillExpansionMap(getTreeExpansionStateMap(), volume.getTeiSd().getPbOrDiv());
					this.pbList = VolumeServiceBean.getAllPbs(new DOMSource(volume.getTeiSdXml()));
				}
				
				//If no Page Number is given, try to go to title page, if available
				if(this.selectedPageNumber==0)
				{
					if(volume.getTeiSd()!=null)
					{
						Page p = VolumeUtilBean.getTitlePage(volume);
						this.setSelectedPageNumber(volume.getPages().indexOf(p) + 1);
					}
					else
					{
						this.selectedPageNumber = 1;
					}
					
				}
				
				
			} catch (Exception e) {
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_loadVolume"));
				logger.error("Error while loading volume", e);
			}
		}
		volumeLoaded();
	}
	
	private void initPageListMenu()
	{
		this.pageListMenu.clear();
		for(Page p : volume.getPages())
		{
			pageListMenu.add(new SelectItem(p.getOrder()+1, InternationalizationHelper.getLabel("list_Page") + " " + p.getOrderLabel() + " / " + InternationalizationHelper.getLabel("list_Scan") +" " + (p.getOrder()+1)));
		}
	}
	
	public void pageNumberChanged()
	{
		System.out.println("Go to page changed!!!!");
		loadVolume();
	}
	
	protected void volumeLoaded() {
		if(FacesContext.getCurrentInstance().isPostback())
		{
			this.digilibQueryString = "";
		}
		Page pageforNumber = volume.getPages().get(getSelectedPageNumber()-1);
		this.setSelectedPage(pageforNumber);
		this.setSelectedRightPage(null);
		if(ViewType.RECTO_VERSO.equals(viewType))
		{
			
			Page nextPage = null;
			Page prevPage = null;
			
			String pageViewType = null;
			String nextPageViewType = null;
			String prevPageViewType = null;
			
			if(volume.getPages().size() > getSelectedPageNumber())
			{
				nextPage = volume.getPages().get(getSelectedPageNumber());
			}
			if(getSelectedPageNumber() > 1)
			{
				prevPage = volume.getPages().get(getSelectedPageNumber()-2);
			}
			
			
			
			if(volume.getTeiSdXml()!=null)
			{
				for(XdmNode node : pbList)
				{
					String id = node.getAttributeValue(new QName("http://www.w3.org/XML/1998/namespace", "id"));
					String type = node.getAttributeValue(new QName("type"));
					if(pageforNumber.getId().equals(id))
					{
						pageViewType = type;
					}
					else if (nextPage!=null && nextPage.getId().equals(id))
					{
						nextPageViewType = type;
					}
					else if (prevPage!=null && prevPage.getId().equals(id))
					{
						prevPageViewType = type;
					}
				}
			}
			
			
			
			if("single".equals(pageViewType))
			{
				this.setSelectedPage(pageforNumber);
				this.setSelectedRightPage(null);
			}
			else if ("left".equals(pageViewType))
			{
				this.setSelectedPage(pageforNumber);
				if(nextPage!=null && "right".equals(nextPageViewType))
				{
					this.setSelectedRightPage(nextPage);
				}
				else
				{
					this.setSelectedRightPage(null);
				}
				
				
			}
			else if ("right".equals(pageViewType))
			{
				this.setSelectedRightPage(pageforNumber);
				if(prevPage!=null && "left".equals(prevPageViewType))
				{
					this.setSelectedPage(prevPage);
				}
				else
				{
					this.setSelectedPage(null);
				}
				
			}
			else
			//automatic guess of position
			{
				if(getSelectedPageNumber() % 2 == 0)
				{
					this.setSelectedPage(pageforNumber);
					this.setSelectedRightPage(nextPage);
					
				}
				else
				{
					this.setSelectedRightPage(pageforNumber);
					this.setSelectedPage(prevPage);
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
			if(getVolume().getTeiSdXml() != null && hasSelected == false)
			{
				PbOrDiv oldSelectedDiv = selectedDiv;
				/*
			
				if(getSelectedPage()!=null)
				{
					this.selectedDiv = volServiceBean.getDivForPage(volume, getSelectedPage());
				}
				else if (getSelectedRightPage()!=null)
				{
					this.selectedDiv = volServiceBean.getDivForPage(volume, getSelectedRightPage());
				}
				*/
				this.selectedDiv = volServiceBean.getDivForPage(volume, pageforNumber);
				
			}
			else
			{
				hasSelected = false;
			}
		} 
		
		catch (Exception e) 
		{
			logger.error("Structural element cannot be selected for this page.", e);
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_wrongStructElem"));
		}
		
		
		
	}
	
	
	private static void fillExpansionMap(Map<PbOrDiv, Boolean> expansionMap, List<PbOrDiv> currentDivs)
	{
//		System.out.println("Fill exp map");
		for(PbOrDiv pbOrDiv : currentDivs)
		{
			expansionMap.put(pbOrDiv, true);
			fillExpansionMap(expansionMap, pbOrDiv.getPbOrDiv());
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
		
		
		/*
		Page nextPage = null;
    	if(volume.getPages().size() > getSelectedPageNumber())
    	{
    		nextPage = volume.getPages().get(getSelectedPageNumber());
    	}
		*/
		
    	int lastCurrentPageNumber = 0;
    	
        if(ViewType.RECTO_VERSO.equals(viewType))
        {
        	
        	//If both pages are displayed or only the right page, take the number of the right page
        	if(selectedRightPage!=null)
        	{
        		lastCurrentPageNumber = volume.getPages().indexOf(selectedRightPage) + 1;
        	}
        	//If only the left page is displayed, take the left one
        	else if (selectedPage!=null)
        	{
        		lastCurrentPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        	}

        }
        
        else 
        {
           lastCurrentPageNumber = selectedPageNumber;
        }
        
        if (volume.getPages().size()>= lastCurrentPageNumber + 1)
        {
        	selectedPageNumber = lastCurrentPageNumber + 1;
        	loadVolume();
        }
        
        return "";
	}
	
	public String goToPreviousPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) - 1;
		/*
		Page prevPage = null;
		if(getSelectedPageNumber() > 1)
		{
			prevPage = volume.getPages().get(getSelectedPageNumber()-2);
		}
		*/
		
		int firstCurrentPageNumber = 0;
		
		
		if(ViewType.RECTO_VERSO.equals(viewType))
        {

			//If both pages are displayed or only the left page, take the number of the left page
        	if(selectedPage!=null)
        	{
        		firstCurrentPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        	}
        	//If only the left page is displayed, take the right one
        	else if (selectedRightPage!=null)
        	{
        		firstCurrentPageNumber = volume.getPages().indexOf(selectedRightPage) + 1;
        	}
        	
			
        }
		
		else 
        {
			firstCurrentPageNumber = selectedPageNumber;
        }
		
		if (firstCurrentPageNumber -1 > 0)
		{
			selectedPageNumber = firstCurrentPageNumber - 1;
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
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_ftDisplay"));
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
	
	public Page getSelectedRightPage() {
		return selectedRightPage;
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
		return "";
	}
	
	public String switchViewType(String viewType, Page pageToGoTo)
	{
		setViewType(ViewType.valueOf(viewType));
		this.selectedPageNumber = pageToGoTo.getOrder() + 1;
		loadVolume();
		return "";
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
	
	public String getVolumeIdWithoutObjId() {
		String id = this.getVolumeId();
		if (id.split(":").length >= 2)
		{
			id = "escidoc:" + id.split(":")[1];
		}
		return id;
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

	public Map<PbOrDiv, Boolean> getTreeExpansionStateMap() {
		return treeExpansionStateMap;
	}

	public void setTreeExpansionStateMap(Map<PbOrDiv, Boolean> treeExpansionStateMap) {
		this.treeExpansionStateMap = treeExpansionStateMap;
	}

	public Organization getVolumeOu() {
		return volumeOu;
	}

	public void setVolumeOu(Organization volumeOu) {
		this.volumeOu = volumeOu;
	}

	public String export(String type)
	{
		byte[] pdf = null;
		try
		{
			if (type.equals(Export.ExportTypes.PDF.toString()))
			{
				pdf = ExportBean.doExport(this.volume, ExportTypes.PDF);
				ExportServlet servlet = new ExportServlet();
				servlet.createPdfResponse(pdf, this.volumeId);	
			}
			if (type.equals(Export.ExportTypes.PRINT.toString()))
			{
				//TODO
			}
		}
		catch (Exception e)
		{
			MessageHelper.errorMessage("The export could not be created due to an internal error.", e.getMessage());
		}
		return "";
	}

	public String getDigilibQueryString() {
		return digilibQueryString;
		/*
		if(digilibQueryString == null || digilibQueryString.length()<=1)
		{
			return "";
		}
		else
		{
			System.out.println("DLC query string: " + digilibQueryString);
			return digilibQueryString;
			
		}
		*/
		
	}

	public void setDigilibQueryString(String digilibQueryString) {
		this.digilibQueryString = digilibQueryString;
	}
	
	

	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}

	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		initPageListMenu();
		
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}


}
