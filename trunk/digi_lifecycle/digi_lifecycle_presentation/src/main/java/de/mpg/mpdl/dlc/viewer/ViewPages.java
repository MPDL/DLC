/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.viewer;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.component.Component;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
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
	
	private ApplicationBean appBean = new ApplicationBean();

	
	private Collection collection;
	
	private static Logger logger = Logger.getLogger(ViewPages.class);

	private int selectedPageNumber=0;
	
	private Page selectedPage;
	
	private Page selectedRightPage;
	
	private String selectedDiv;
	
	private List<Page> pageList = new ArrayList<Page>();
	
	private ViewType viewType = ViewType.RECTO_VERSO;
	
	private String viewTypeText = viewTypeToText(viewType);
	
	private boolean hasSelected = false;
	
	private boolean showTree = false;
	
	private Map<String, String> clientIdMap = new HashMap<String, String>();
	
	private List<SelectItem> pageListMenu = new ArrayList<SelectItem>();
	
	private Map<String, Boolean> treeExpansionStateMap = new HashMap<String, Boolean>();
	
	private Organization volumeOu;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	private List<XdmNode> pbList;
	
	private Map<Page, String> viewTypeMap = new HashMap<Page, String>();
	
	private String codicologicalXhtml;
	
	private List<TeiSdTreeNodeImpl> teiSdRoots;

	@PostConstruct
	public void postConstruct()
	{
		internationalizationHelper.addObserver(this);
	}
	
	@PreDestroy
	public void preDestroy()
	{
		internationalizationHelper.deleteObserver(this);
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{     
 

		if(volume==null || ( (!volumeId.equals(volume.getObjidAndVersion())) && (!volumeId.equals(volume.getItem().getOriginObjid())) ) )
		{   
			
			try {
				
				this.volume = volServiceBean.loadCompleteVolume(volumeId, getLoginBean().getUserHandle());
				this.collection = contextServiceBean.retrieveCollection(volume.getItem().getProperties().getContext().getObjid(), null);
			
				//Set the logo of application to collection logo
				volumeOu = orgServiceBean.retrieveOrganization(this.collection.getContext().getProperties().getOrganizationalUnitRefs().getFirst().getObjid());
				if (volumeOu.getDlcMd().getFoafOrganization().getImgURL() != null && !volumeOu.getDlcMd().getFoafOrganization().getImgURL().equals(""))
					{sessionBean.setLogoLink(volumeOu.getId());
					sessionBean.setLogoUrl(volumeOu.getDlcMd().getFoafOrganization().getImgURL());
					sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home")
							.replace("$1", volumeOu.getEscidocMd().getTitle()));}
				
				initPageListMenu();
				if(volume.getTeiSdXml()!=null)
				{
					
					this.teiSdRoots = new ArrayList<TeiSdTreeNodeImpl>();
					for(Node n = volume.getTeiSdXml().getDocumentElement().getFirstChild(); n!=null; n=n.getNextSibling())
					{
						if (n.getNodeType()==Node.ELEMENT_NODE && n.getLocalName().equals("text"))
						{
							teiSdRoots.add(new TeiSdTreeNodeImpl((Element) n, volume, volServiceBean));
						}
					}
					
					//this.teiSdRoots.add(new TeiSdTreeNodeImpl(volume.getTeiSdXml().getDocumentElement()));
					fillExpansionMap(getTreeExpansionStateMap(), teiSdRoots);
					this.pbList = VolumeServiceBean.getAllPbs(new DOMSource(volume.getTeiSdXml()));
					
					for(int i=0; i<pbList.size(); i++)
					{
						try {
							XdmNode pb = pbList.get(i);
							String rend = pb.getAttributeValue(new QName("rend"));
							String id = pb.getAttributeValue(new QName("http://www.w3.org/XML/1998/namespace", "id"));
							
							Page pageDummy = new Page();
							pageDummy.setId(id);
							
							int index = volume.getPages().indexOf(pageDummy);
							if(index>=0)
							{
								viewTypeMap.put(volume.getPages().get(index), rend);
							}
							
						} catch (Exception e) {
							logger.warn("could not set viewType for page", e);
						}
					}
				}
				
				//If no Page Number is given, try to go to title page, if available
				if(this.selectedPageNumber==0)
				{
					if(volume.getTeiSdXml()!=null)
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
			
			
			
			if(volume.getPages().size() > getSelectedPageNumber())
			{
				nextPage = volume.getPages().get(getSelectedPageNumber());
			}
			if(getSelectedPageNumber() > 1)
			{
				prevPage = volume.getPages().get(getSelectedPageNumber()-2);
			}
			
			
			String pageViewType = viewTypeMap.get(pageforNumber);
			String nextPageViewType = viewTypeMap.get(nextPage);
			String prevPageViewType = viewTypeMap.get(prevPage);
			
			/*
			
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
			*/
			
			
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
				String oldSelectedDiv = selectedDiv;
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
	
	
	private static void fillExpansionMap(Map<String, Boolean> expansionMap, List<TeiSdTreeNodeImpl> currentRoots)
	{
//		System.out.println("Fill exp map");
		for(TeiSdTreeNodeImpl node : currentRoots)
		{
			expansionMap.put(node.getId(), true);
			fillExpansionMap(expansionMap, node.getChildren());
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
	
	public void goTo(String divId)
	{
		logger.info("Go to div " + divId);
		Page p;
		try {
			p = volServiceBean.getPageForDiv(volume, divId);
		} catch (Exception e) {
			p = volume.getPages().get(0);
		}
		selectedPageNumber = volume.getPages().indexOf(p) + 1 ;
		setSelectedDiv(divId);
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

	public String getSelectedDiv() {
		return selectedDiv;
	}

	public void setSelectedDiv(String selectedDiv) {
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

	public Map<String, Boolean> getTreeExpansionStateMap() {
		return treeExpansionStateMap;
	}

	public void setTreeExpansionStateMap(Map<String, Boolean> treeExpansionStateMap) {
		this.treeExpansionStateMap = treeExpansionStateMap;
	} 

	public Organization getVolumeOu() {
		return volumeOu;
	}

	public void setVolumeOu(Organization volumeOu) {
		this.volumeOu = volumeOu;
	}


	/**
	 * This method check if a volume has a tei component.
	 * @return true if volume has tei component, else false
	 */
	public boolean getHasTei()
	{
		for (int i = 0; i< this.volume.getItem().getComponents().size(); i++)
		{
			Component comp = this.volume.getItem().getComponents().get(i);
			if (comp.getProperties().getContentCategory().equals("tei"))
			{
				return true;
			}
			if (comp.getProperties().getContentCategory().equals("tei-sd"))
			{
				return true;
			}
		}
		
		return false;
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

	public List<XdmNode> getPbList() {
		return pbList;
	}

	public void setPbList(List<XdmNode> pbList) {
		this.pbList = pbList;
	}

	public Map<Page, String> getViewTypeMap() {
		return viewTypeMap;
	}

	public void setViewTypeMap(Map<Page, String> viewTypeMap) {
		this.viewTypeMap = viewTypeMap;
	}

	public String getCodicologicalXhtml() {
		
		try {
			if(codicologicalXhtml==null && volume.getCodicological()!=null)
			{
				 ExtensionFunction i18n = new ExtensionFunction() {
		                public QName getName() {
		                    return new QName("http://dlc.mpdl.mpg.de", "i18n");
		                }

		                public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
		                    String arg = ((XdmAtomicValue)arguments[0].itemAt(0)).getStringValue();
		                    String result = InternationalizationHelper.getCodicologicalLabel(arg);
		                    return new XdmAtomicValue(result);
		                }

						@Override
						public SequenceType[] getArgumentTypes() {
			                   return new SequenceType[]{
			                           SequenceType.makeSequenceType(
			                               ItemType.STRING, OccurrenceIndicator.ONE)};
						}

						@Override
						public SequenceType getResultType() {
							 return SequenceType.makeSequenceType(
				                        ItemType.STRING, OccurrenceIndicator.ONE
				                    );
						}
		            };

				
				this.codicologicalXhtml = VolumeServiceBean.transformCodicologicalToHtml(new StreamSource(new StringReader(volume.getCodicological())), i18n);
			}
		} catch (Exception e) {
			logger.error("error while transforming codicological md to xhtml", e);
		}
		
		return codicologicalXhtml;
	}

	public void setCodicologicalXhtml(String codicologicalXhtml) {
		this.codicologicalXhtml = codicologicalXhtml;
	}
	
	public String getDfgUrl() throws UnsupportedEncodingException
	{
		String url ="";
		url = appBean.getDfgUrl();
		url += appBean.getDomain();
		url += "/dlc/export";
		url += "/?id=" + this.volume.getObjidAndVersion() + "&format=MODS";
		return java.net.URLEncoder.encode(url, "UTF-8");
	}

	public List<TeiSdTreeNodeImpl> getTeiSdRoots() {
		return teiSdRoots;
	}

	public void setTeiSdRoots(List<TeiSdTreeNodeImpl> teiSdRoots) {
		this.teiSdRoots = teiSdRoots;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

}
