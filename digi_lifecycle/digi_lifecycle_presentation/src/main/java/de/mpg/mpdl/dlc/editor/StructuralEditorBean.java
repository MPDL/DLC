package de.mpg.mpdl.dlc.editor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.editor.TeiElementWrapper.PositionType;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.RomanNumberConverter;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.Body;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.DocAuthor;
import de.mpg.mpdl.dlc.vo.teisd.DocTitle;
import de.mpg.mpdl.dlc.vo.teisd.Figure;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;
import de.mpg.mpdl.dlc.vo.teisd.TitlePage;

@ManagedBean
@SessionScoped
@URLMapping(id = "structuralEditor", pattern = "/edit/#{structuralEditorBean.volumeId}", viewId = "/structuralEditor.xhtml")
public class StructuralEditorBean {

	private static Logger logger = Logger.getLogger(StructuralEditorBean.class);

	protected VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private String volumeId;
	
	private Volume volume;
	
	//Flat list of Treee
	private List<TeiElementWrapper> flatTeiElementList;
	
	private List<TeiElementWrapper> pbList;
	
	
	//Hierarchical Tree
	private List<TreeWrapperNode> treeWrapperNodes;
	
	
	private TeiElementWrapper selectedPb;
	private String selectedPbType;
	private String selectedPbNumeration;
	private String selectedPbSubtype;
	
	
	
	//private TeiElementWrapper selectedStructuralElement;
		
	/**
	 * Element that will be added next
	 */
	private TeiElementWrapper currentNewElement;
	
	/**
	 * Element that is currently edited
	 */
	private TeiElementWrapper currentEditElementWrapper;
	
	/**
	 * Element that is currently edited 
	 */
	private TeiElementWrapper currentEditElementWrapperRestore;
	
	//private PbOrDiv currentEditTeiElement;

	
	
	private ElementType currentTeiElementType;
	
	//private ElementType currentTeiElementEditType;
	
	private String selectedStructuralType;
	
	private String selectedStructuralEditType;
	
	
	//Pagination values
	private String selectedPaginationEndPbId;
	
	private String selectedPageDisplayTypeEndPbId;
	
	private String selectedPageTypeEndPbId;
	
	private PaginationType selectedPaginationType;
	
	private String selectedPaginationStartValue;
	
	private String selectedPaginationPattern;
	
	private int selectedPaginationColumns = 1;
	
	private boolean paginationInBrackets = false;
	
	private boolean paginateEverySecondPage = false;
	
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	private List<ValidationWrapper> validationList = new ArrayList<ValidationWrapper>();
	
	
	private Map<String, String> clientIdMap = new HashMap<String, String>();
	
	
	private List<SelectItem> structureTypeSelectItems;
	
	private List<SelectItem> pageListMenu;
	
	private String selectedGoToBoxPageId;
	
	public enum PaginationType
	{
		ARABIC, ROMAN, ROMAN_MINUSCULE, RECTO_VERSO, FREE
	}
	
	
	public StructuralEditorBean()
	{
		init();
		
	}
	
	
	public void init()
	{
		this.flatTeiElementList = null;
		this.pbList = null;
		this.treeWrapperNodes = null;
		this.currentEditElementWrapper = null;
		this.currentEditElementWrapperRestore = null;
		this.selectedStructuralEditType = null;
		this.selectedPb = null;
		
		this.currentTeiElementType=ElementType.DIV;
		this.selectedStructuralType="chapter";
		
		selectedStructuralElementTypeChanged();
		
		this.selectedPaginationType = PaginationType.ARABIC;
		this.selectedPaginationStartValue = "1";
		this.selectedPaginationEndPbId = null;
		this.selectedPageDisplayTypeEndPbId = null;
		this.selectedPageTypeEndPbId = null;
		this.selectedPaginationPattern = null;
		this.selectedPaginationColumns = 1;
		this.paginationInBrackets = false;
		this.paginateEverySecondPage = false;
		
		this.pageListMenu = new ArrayList<SelectItem>();
		
		this.selectedGoToBoxPageId = null;

	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		if(loginBean.isLogin())
		{
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{   logger.info("Load new volume for structural editing " + volumeId);
				try {
					this.volume = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle());
					init();
					volServiceBean.loadTeiSd(volume, loginBean.getUserHandle());
					
				} catch (Exception e) {
					logger.error("Could not load volume " + volumeId, e );
					MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_loadVolume"));
					volume = null;
				}
				
				structureTypeSelectItems = internationalizationHelper.getStructureTypeSelectItems();
			}
			volumeLoaded();
		}
		else
		{
			logger.warn("Trying to edit structure of volume " + volumeId + ", but not logged in!");
		}

	}
	

	protected void volumeLoaded() {
		
		if(volume.getTeiSd() == null)
		{
			//create a new TEI SD with Pagebreaks only
			TeiSd teiSd = new TeiSd();
			Body body = new Body();
			teiSd.getPbOrDiv().add(body);
			for(Page p : volume.getMets().getPages())
			{
				Pagebreak pb = new Pagebreak();
				pb.setFacs(p.getContentIds());
				pb.setParent(body);
				pb.setId(p.getId());
				body.getPbOrDiv().add(pb);
			}
			volume.setTeiSd(teiSd);
		}
		
		if(flatTeiElementList == null)
		{
			flatTeiElementList = new ArrayList<TeiElementWrapper>();
			treeWrapperNodes = new ArrayList<TreeWrapperNode>();
			pbList = new ArrayList<TeiElementWrapper>();
			
			
			//Transform TEI-SD to flat element list
			List<TeiElementWrapper>[] flatLists = teiSdToFlatTeiElementList(volume.getTeiSd(), volume);
			this.flatTeiElementList = flatLists[0];
			this.pbList = flatLists[1];
			initPageListMenu();
			
			//Transform flat TEI to TreeWrapper
			validationList = new ArrayList<ValidationWrapper>();
			this.treeWrapperNodes = flatTeiElementListToTreeWrapper(flatTeiElementList, validationList);
			
			
			for(TeiElementWrapper teiElWrapper : flatTeiElementList)
			{
				if(ElementType.PB.equals(teiElWrapper.getTeiElement().getElementType()))
				{
					selectPb(teiElWrapper);
					//this.selectedPb = teiElWrapper;
					break;
				}
			}
		}
		

	}
	
	private void initPageListMenu() {
		pageListMenu.clear();
		for(TeiElementWrapper wrapper : pbList)
		{
			pageListMenu.add(getSelectItemForPb(wrapper));
		}
		
	}


	public void revert()
	{
		this.volume = null;
		loadVolume();
		MessageHelper.infoMessage(ApplicationBean.getResource("Messages","edit_changesReverted"));
	}
	

	private static TeiSd flatTeiElementListToTeiSd(List<TeiElementWrapper> flatTeiElementList, TeiSd teiSd)
	{
		
		
		teiSd.getPbOrDiv().clear();
		PbOrDiv parentTeiElement = null;
		
		//clear old children
		for(TeiElementWrapper teiElementWrapper : flatTeiElementList)
		{
			teiElementWrapper.getTeiElement().getPbOrDiv().clear();
		}
		
		
		
		for(TeiElementWrapper teiElementWrapper : flatTeiElementList)
		{
			
			
			if(PositionType.START.equals(teiElementWrapper.getPositionType()) || PositionType.EMPTY.equals(teiElementWrapper.getPositionType()))
			{
				if(parentTeiElement == null)
				{
					teiSd.getPbOrDiv().add(teiElementWrapper.getTeiElement());
				}
				else
				{
					parentTeiElement.getPbOrDiv().add(teiElementWrapper.getTeiElement());
				}
			}
			
			
			
			if(PositionType.START.equals(teiElementWrapper.getPositionType()))
			{
				teiElementWrapper.getTeiElement().setParent(parentTeiElement);
				parentTeiElement = teiElementWrapper.getTeiElement();
			}
			else if (PositionType.END.equals(teiElementWrapper.getPositionType()))
			{
				parentTeiElement = teiElementWrapper.getPartnerElement().getTeiElement().getParent();
			}
		}
		
		return teiSd;
		
	}
	
	
	
	private static List<TreeWrapperNode> flatTeiElementListToTreeWrapper(List<TeiElementWrapper> flatTeiElementList, List<ValidationWrapper> validationList)
	{
		
		List<TreeWrapperNode> wrapperNodeList = new ArrayList<TreeWrapperNode>();
		
		
		TreeWrapperNode parent = null;
		PbOrDiv parentTeiElement = null;
		
		//clear old children
		for(TeiElementWrapper teiElementWrapper : flatTeiElementList)
		{
			teiElementWrapper.getTeiElement().getPbOrDiv().clear();
		}
		
		
		
		for(TeiElementWrapper teiElementWrapper : flatTeiElementList)
		{
			
			
			if((!ElementType.PB.equals(teiElementWrapper.getTeiElement().getElementType())) && (PositionType.START.equals(teiElementWrapper.getPositionType()) || PositionType.EMPTY.equals(teiElementWrapper.getPositionType())))
			{
				TreeWrapperNode treeWrapperNode = new TreeWrapperNode();
				treeWrapperNode.setTeiElementWrapper(teiElementWrapper);
				treeWrapperNode.setParent(parent);
				
				teiElementWrapper.getTeiElement().setParent(parentTeiElement);
				teiElementWrapper.setTreeWrapperNode(treeWrapperNode);
				
				if(parent == null)
				{
					wrapperNodeList.add(treeWrapperNode);
					
				}
				else
				{
					parent.getChildren().add(treeWrapperNode);
					parentTeiElement.getPbOrDiv().add(teiElementWrapper.getTeiElement());
				}
				
				
				if(PositionType.START.equals(teiElementWrapper.getPositionType()))
				{
					parentTeiElement = teiElementWrapper.getTeiElement();
					parent = treeWrapperNode;
				}
				
				if(treeWrapperNode.getInvalid())
				{
					validationList.add(new ValidationWrapper(treeWrapperNode));
				}
				
			}

			else if (PositionType.END.equals(teiElementWrapper.getPositionType()))
			{
				parentTeiElement = teiElementWrapper.getPartnerElement().getTeiElement().getParent();
				parent = teiElementWrapper.getPartnerElement().getTreeWrapperNode().getParent();
			}
		}
		
		return wrapperNodeList;
		
	}
	
	
	
	
	
	
	
	
	
	private static List<TeiElementWrapper>[] teiSdToFlatTeiElementList(TeiSd teiSd, Volume v)
	{
		List<TeiElementWrapper> flatTeiElementList = new ArrayList<TeiElementWrapper>();
		List<TeiElementWrapper> pbList = new ArrayList<TeiElementWrapper>();
		recursiveTeiSdToFlat(flatTeiElementList, teiSd.getPbOrDiv(), v, pbList, new ArrayList<TeiElementWrapper>());
		String[] s = new String[2];
		return new List[]{flatTeiElementList, pbList};
		
	}
	

	private static void recursiveTeiSdToFlat(List<TeiElementWrapper> flatTeiElementList, List<PbOrDiv> currentTeiElementList, Volume volume, List<TeiElementWrapper> pbList, List<TeiElementWrapper> subList)
	{
		for(PbOrDiv currentTeiElement : currentTeiElementList)
		{
			TeiElementWrapper currentTeiStartElementWrapper = new TeiElementWrapper();
			flatTeiElementList.add(currentTeiStartElementWrapper);
			currentTeiStartElementWrapper.setTeiElement(currentTeiElement);
			
			
			
			
			if (!ElementType.PB.equals(currentTeiElement.getElementType()))
			{
				currentTeiStartElementWrapper.setPositionType(PositionType.START);
				//subList.add(currentTeiStartElementWrapper);
				if(!pbList.isEmpty())
				{
					currentTeiStartElementWrapper.setPagebreakWrapper(pbList.get(pbList.size()-1));
					//currentTeiStartElementWrapper.setPagebreakId(lastPb.getTeiElement().getId());
				}
			}
			else
			{
				currentTeiStartElementWrapper.setPositionType(PositionType.EMPTY);
				
				/*
				if(lastPb!=null)
				{
					lastPb.setElementsToNextPb(subList);
					System.out.println("PB" + lastPb.getTeiElement().getId() + "got sublist with " + subList.size());
					
				}
				subList = new ArrayList<TeiElementWrapper>();
				
				if(lastPb!=null)
				{
					System.out.println("PB" + lastPb.getTeiElement().getId() + "has sublist with " + lastPb.getElementsToNextPb().size());
				}
				
				lastPb = currentTeiStartElementWrapper;
				*/
				Page dummy = new Page();
				dummy.setId(currentTeiElement.getId());
				Page realPage = volume.getPages().get(volume.getPages().indexOf(dummy));
				currentTeiStartElementWrapper.setPage(realPage);
				if(!pbList.isEmpty())
				{
					TeiElementWrapper lastPb = pbList.get(pbList.size()-1);
					//System.out.println("--- last Pb: " + lastPb.getPage().getOrder());
					
					lastPb.setNextPagebreak(currentTeiStartElementWrapper);
					
					currentTeiStartElementWrapper.setLastPagebreak(lastPb);
				}
				pbList.add(currentTeiStartElementWrapper);
				
				//If the last element was one of Front, Body or Back, then set this pagebreak as this element's page.
				if(flatTeiElementList.size()>=2)
				{
					TeiElementWrapper lastElement = flatTeiElementList.get(flatTeiElementList.size()-2);
					
					if((ElementType.FRONT.equals(lastElement.getTeiElement().getElementType()) ||
							ElementType.BODY.equals(lastElement.getTeiElement().getElementType()) ||
							ElementType.BACK.equals(lastElement.getTeiElement().getElementType())) &&
							PositionType.START.equals(lastElement.getPositionType()))
					{
						lastElement.setPagebreakWrapper(currentTeiStartElementWrapper);
				
					}
				}
				
				
				
			}
			
	
			/*
			else if(!(ElementType.FRONT.equals(currentTeiElement.getElementType()) || ElementType.BODY.equals(currentTeiElement.getElementType())   
					|| ElementType.BACK.equals(currentTeiElement.getElementType())))
			{
				
				System.out.println(currentTeiElement.getId() + "  added to sublist" );
			}
			*/
			
			
			recursiveTeiSdToFlat(flatTeiElementList, currentTeiElement.getPbOrDiv(), volume, pbList, subList);
			
			if(PositionType.START.equals(currentTeiStartElementWrapper.getPositionType()))
			{
				TeiElementWrapper currentTeiEndElementWrapper = new TeiElementWrapper();
				flatTeiElementList.add(currentTeiEndElementWrapper);
				currentTeiEndElementWrapper.setTeiElement(currentTeiElement);
				currentTeiEndElementWrapper.setPositionType(PositionType.END);
				
				currentTeiEndElementWrapper.setPartnerElement(currentTeiStartElementWrapper);
				currentTeiStartElementWrapper.setPartnerElement(currentTeiEndElementWrapper);
				
				
				if(!pbList.isEmpty())
				{
					TeiElementWrapper lastPb = pbList.get(pbList.size()-1);
					currentTeiEndElementWrapper.setPagebreakWrapper(lastPb);
					//currentTeiEndElementWrapper.setPagebreakId(lastPb.getTeiElement().getId());
				}
				
				//subList.add(currentTeiEndElementWrapper);
				
				/*
				if(!(ElementType.FRONT.equals(currentTeiElement.getElementType()) || ElementType.BODY.equals(currentTeiElement.getElementType())   
						|| ElementType.BACK.equals(currentTeiElement.getElementType())))
				{
					subList.add(currentTeiEndElementWrapper);
				}
				*/
			}
		

		}
		
	}
	
	
	public void save()
	{
		
		
		if(validationList.size()>0)
		{
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages","edit_notSavedValidationErrors"));
		}
		else
		{
			try {
				flatTeiElementListToTeiSd(flatTeiElementList, volume.getTeiSd());
				this.volume = volServiceBean.updateVolume(volume, getLoginBean().getUserHandle(), null, true);
				volServiceBean.loadTeiSd(volume, loginBean.getUserHandle());
				
				MessageHelper.infoMessage(ApplicationBean.getResource("Messages","edit_savedSuccessfully"));
			} catch (Exception e) {
				MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_saveStruct"));
				logger.error("Error while saving created TEI-SD.", e);
				
			}
			
			try {
				TeiSd teiToSave = new TeiSd();
				flatTeiElementListToTeiSd(flatTeiElementList, teiToSave);
				IBindingFactory bfactTei = BindingDirectory.getFactory(TeiSd.class);
				IMarshallingContext mc = bfactTei.createMarshallingContext();
				mc.setIndent(3);
				StringWriter sw = new StringWriter();
				mc.marshalDocument(teiToSave, "utf-8", null, sw);
				
				System.out.println(sw.toString());
				
			} catch (JiBXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void saveAndRelease()
	{
		if(validationList.size()>0)
		{
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages","edit_notSavedValidationErrors"));
		}
		else
		{
			try {
				flatTeiElementListToTeiSd(flatTeiElementList, volume.getTeiSd());
				this.volume = volServiceBean.updateVolume(volume, getLoginBean().getUserHandle(), null, true);
				this.volume = volServiceBean.releaseVolume(volume, getLoginBean().getUserHandle());
				volServiceBean.loadTeiSd(volume, loginBean.getUserHandle());
				MessageHelper.infoMessage(ApplicationBean.getResource("Messages", "edit_savedAndReleasedSuccessfully"));
			} catch (Exception e) {
				MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_saveAndReleaseStruct"));
				logger.error("Error while saving and releasing structure.", e);
				
			}
			
			try {
				TeiSd teiToSave = new TeiSd();
				flatTeiElementListToTeiSd(flatTeiElementList, teiToSave);
				IBindingFactory bfactTei = BindingDirectory.getFactory(TeiSd.class);
				IMarshallingContext mc = bfactTei.createMarshallingContext();
				mc.setIndent(3);
				StringWriter sw = new StringWriter();
				mc.marshalDocument(teiToSave, "utf-8", null, sw);
				
				System.out.println(sw.toString());
				
			} catch (JiBXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	public void selectedStructuralElementTypeChanged()
	{
		
		
		if("FIGURE".equals(selectedStructuralType))
		{
			currentTeiElementType = ElementType.FIGURE;
			currentNewElement = new TeiElementWrapper();
			Figure figure = new Figure();
			figure.getHead().add("");
			currentNewElement.setTeiElement(figure);
			
		}
		else if ("TITLE_PAGE".equals(selectedStructuralType))
		{
			currentTeiElementType = ElementType.TITLE_PAGE;
			currentNewElement = new TeiElementWrapper();
			TitlePage titlePage = new TitlePage();
			titlePage.setDocTitles(new ArrayList<DocTitle>());
			titlePage.getDocTitles().add(new DocTitle());
			currentNewElement.setTeiElement(titlePage);
		}
		else //DIV type
		{
			
			currentTeiElementType = ElementType.DIV;
			currentNewElement = new TeiElementWrapper();
			Div div = new Div();
			
			if(!"free".equals(selectedStructuralType)) 
			{
				div.setType(selectedStructuralType);
			}
			
			div.getHead().add("");
			
			div.getDocAuthors().add(new DocAuthor());
			
			currentNewElement.setTeiElement(div);
		}
		
		/*
		switch (selectedStructuralType)
		{
			case DIV : 
			{
				currentNewElement = new TeiElementWrapper();
				Div div = new Div();
				div.setType("chapter");
				div.getHead().add("");
				
				currentNewElement.setTeiElement(div);
				break;
			}
			
			case FIGURE : 
			{
				currentNewElement = new TeiElementWrapper();
				Figure figure = new Figure();
				currentNewElement.setTeiElement(figure);
				break;
			}
			
			case TITLE_PAGE : 
			{
				currentNewElement = new TeiElementWrapper();
				TitlePage titlePage = new TitlePage();
				titlePage.setDocTitles(new ArrayList<DocTitle>());
				titlePage.getDocTitles().add(new DocTitle());
				currentNewElement.setTeiElement(titlePage);
				break;
			}
		
		}
		*/
		
	
	}
	
	public void selectedStructuralEditElementTypeChanged()
	{

		
		if("FIGURE".equals(selectedStructuralEditType))
		{
			//currentTeiElementEditType = ElementType.FIGURE;
			Figure figure = new Figure();
			figure.getHead().add("");
			currentEditElementWrapper.setTeiElement(figure);
			
		}
		else if ("TITLE_PAGE".equals(selectedStructuralEditType))
		{
			
			//currentTeiElementEditType = ElementType.TITLE_PAGE;
			
			TitlePage titlePage = new TitlePage();
			titlePage.setDocTitles(new ArrayList<DocTitle>());
			titlePage.getDocTitles().add(new DocTitle());
			currentEditElementWrapper.setTeiElement(titlePage);
		}
		else //DIV type
		{
			
			//currentTeiElementEditType = ElementType.DIV;
			Div div = new Div();
			
			if(!"free".equals(selectedStructuralEditType)) 
			{
				div.setType(selectedStructuralEditType);
			}
			
			div.getHead().add("");
			div.getDocAuthors().add(new DocAuthor());
			
			currentEditElementWrapper.setTeiElement(div);
		}
		
		/*
		switch (currentTeiElementEditType)
		{
			
			
			case FIGURE : 
			{
				
				Figure figure = new Figure();
				currentEditElementWrapper.setTeiElement(figure);
				//currentEditElementWrapper.getPartnerElement().setTeiElement(figure);
				break;
			}
			
			case TITLE_PAGE : 
			{
				
				TitlePage titlePage = new TitlePage();
				titlePage.setDocTitles(new ArrayList<DocTitle>());
				titlePage.getDocTitles().add(new DocTitle());
				currentEditElementWrapper.setTeiElement(titlePage);
				//currentEditElementWrapper.getPartnerElement().setTeiElement(titlePage);
				break;
			}
		
		}
		*/
		
	
	}
	
	public void selectedPaginationTypeChanged()
	{

		switch (selectedPaginationType)
		{
			case ARABIC : 
			{
				selectedPaginationStartValue = "1";
				break;
			}
		
			case ROMAN : 
			{
				selectedPaginationStartValue = "I";
				break;
			}
			
			case ROMAN_MINUSCULE : 
			{
				
				selectedPaginationStartValue = "i";
				break;
			}
			
			case RECTO_VERSO : 
			{
				
				selectedPaginationStartValue = "1r";
				break;
			}
			case FREE : 
			{
				
				selectedPaginationStartValue = "";
				break;
			}
		
		}
	}
	
	public void applyPageValues()
	{
		Pagebreak selectedPagebreak = (Pagebreak)selectedPb.getTeiElement();
		selectedPagebreak.setType(selectedPbType);
		selectedPagebreak.setNumeration(selectedPbNumeration);
		selectedPagebreak.setSubtype(selectedPbSubtype);
		rerenderThumbnailPage(new TeiElementWrapper[]{selectedPb});
		initPageListMenu();
		MessageHelper.infoMessage(internationalizationHelper.getMessage("edit_actionSuccessful"));
  
	}
	
	public void applyPagination()
	{
		
		int startIndex = pbList.indexOf(selectedPb);
		
		int currentValue = -1;
		
		boolean recto = true;
		
		boolean lowerCase = false;
		
		List<TeiElementWrapper> paginatedPages = new ArrayList<TeiElementWrapper>();
		
		if(selectedPaginationStartValue!=null && !selectedPaginationStartValue.isEmpty())
		{

			if(PaginationType.ARABIC.equals(selectedPaginationType))
			{
				try {
					
					currentValue = Integer.parseInt(selectedPaginationStartValue);
					
				} catch (NumberFormatException e) {
					MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongStartArabic"));
					return;
				} 
			}
			else if(PaginationType.ROMAN.equals(selectedPaginationType) || PaginationType.ROMAN_MINUSCULE.equals(selectedPaginationType))
			{
				try {
					
					//lowerCase = Character.isLowerCase(selectedPaginationStartValue.charAt(0)); 
					currentValue = RomanNumberConverter.convert(selectedPaginationStartValue);
					
				} catch (NumberFormatException e) {
					MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongStartRoman"));
					return;
				}
			}
			else if (PaginationType.RECTO_VERSO.equals(selectedPaginationType))
			{
				
				try {
					
					currentValue = Integer.parseInt(String.valueOf(selectedPaginationStartValue.trim().charAt(0)));

					String rectoVerso = String.valueOf(selectedPaginationStartValue.trim().charAt(1));
					if("r".equals(rectoVerso))
					{
						recto = true;
					}
					else if(("v".equals(rectoVerso)))
					{
						recto = false;
					}
					else
					{
						MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongStartRectoVerso"));
						return;
					}
					
				} catch (NumberFormatException e) {
					MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongStartArabic"));
					return;
				} 
				catch (Exception e)
				{
					MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongStartRectoVerso"));
					return;
				}
				
			}
			
			/*
			if(!selectedPaginationPattern.isEmpty() && !selectedPaginationPattern.contains("?"))
			{
				MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_wrongPaginationPattern"));
				return;
			}
			*/
			
			for(int i=startIndex; i<pbList.size(); i++)
			{
				TeiElementWrapper pbWrapper = pbList.get(i);
				String pagination = null;
				
				if(PaginationType.ARABIC.equals(selectedPaginationType))
				{

						pagination = String.valueOf(currentValue);
						currentValue = currentValue + selectedPaginationColumns;
					
				}
				
				else if(PaginationType.ROMAN.equals(selectedPaginationType) || PaginationType.ROMAN_MINUSCULE.equals(selectedPaginationType))
				{
					lowerCase = PaginationType.ROMAN_MINUSCULE.equals(selectedPaginationType);
					pagination = RomanNumberConverter.convert(currentValue, lowerCase);
					currentValue = currentValue + selectedPaginationColumns;
				}
				
				else if(PaginationType.RECTO_VERSO.equals(selectedPaginationType))
				{
					if(recto)
					{
						pagination = String.valueOf(currentValue) + "r.";
						recto = false;
					}
					else
					{
						pagination = String.valueOf(currentValue) + "v.";
						recto = true;
						currentValue++;
					}
					
					
				}
				
				else if(PaginationType.FREE.equals(selectedPaginationType))
				{
					pagination = selectedPaginationStartValue;
				}
				
				
				/*
				if(!selectedPaginationPattern.isEmpty())
				{
					pagination = selectedPaginationPattern.replaceAll("\\?", pagination);
					
				}
				*/
				
				if(paginationInBrackets)
				{
					pagination = "[" + pagination + "]";
				}
				pbWrapper.getTeiElement().setNumeration(pagination);
				pbWrapper.getPage().setOrderLabel(pagination);
				//paginatedPages.add(pbWrapper);
				rerenderThumbnailPage(new TeiElementWrapper[]{pbWrapper});
				
				if(paginateEverySecondPage)
				{
					if(i+2 < pbList.size())
					{
						i++;
					}
					else
					{
						break;
					}
					
				}
				
				
				if(pbWrapper.getTeiElement().getId().equals(selectedPaginationEndPbId))
				{
					break;
				}
			}
			initPageListMenu();
			selectPb(selectedPb);
			MessageHelper.infoMessage(internationalizationHelper.getMessage("edit_actionSuccessful"));
			
			}
		else
		{
			MessageHelper.errorMessage(ApplicationBean.getResource("message", "error_wrongStart"));
		}
		
		
	}
	
	public void applyPageDisplayType()
	{
		int startIndex = pbList.indexOf(selectedPb);
		
		String value = selectedPb.getTeiElement().getType();
		List<TeiElementWrapper> paginatedPages = new ArrayList<TeiElementWrapper>();
		
		for(int i=startIndex; i<pbList.size(); i++)
		{
			TeiElementWrapper pbWrapper = pbList.get(i);
			
			pbWrapper.getTeiElement().setType(value);
			
			if(value.equals("left"))
			{
				value="right";
			}
			else if (value.equals("right"))
			{
				value="left";
			}
			
			paginatedPages.add(pbWrapper);
			
			if(pbWrapper.getTeiElement().getId().equals(selectedPageDisplayTypeEndPbId))
			{
				break;
			}
		}
		rerenderThumbnailPage(paginatedPages.toArray(new TeiElementWrapper[0]));
		selectPb(selectedPb);
		MessageHelper.infoMessage(internationalizationHelper.getMessage("edit_actionSuccessful"));
		
		
		
	}
	
	public void applyPageType()
	{
		int startIndex = pbList.indexOf(selectedPb);
		
		String value = ((Pagebreak)selectedPb.getTeiElement()).getSubtype();
		List<TeiElementWrapper> paginatedPages = new ArrayList<TeiElementWrapper>();
		
		for(int i=startIndex; i<pbList.size(); i++)
		{
			TeiElementWrapper pbWrapper = pbList.get(i);
			Pagebreak pb = (Pagebreak)pbWrapper.getTeiElement();
			
			pb.setSubtype(value);
			paginatedPages.add(pbWrapper);
			
			
			if(pbWrapper.getTeiElement().getId().equals(selectedPageTypeEndPbId))
			{
				break;
			}
		}
		
		rerenderThumbnailPage(paginatedPages.toArray(new TeiElementWrapper[0]));
		selectPb(selectedPb);
		MessageHelper.infoMessage(internationalizationHelper.getMessage("edit_actionSuccessful"));
	}
	
	public List<SelectItem> getPaginationEndPbSelectItems()
	{
		List<SelectItem> returnList = new ArrayList<SelectItem>();
		int startIndex = pbList.indexOf(selectedPb);
		for(int i=startIndex; i<pbList.size(); i++)
		{
			returnList.add(getSelectItemForPb(pbList.get(i)));
		}
		return returnList;
	}
	
	public void deleteStructuralElement(TeiElementWrapper elementToDelete)
	{
		flatTeiElementList.remove(elementToDelete);
		flatTeiElementList.remove(elementToDelete.getPartnerElement());
		updateTree();
		rerenderThumbnailPage(new TeiElementWrapper[]{elementToDelete.getPagebreakWrapper()});
	}
	
	public void editStructuralElement(TeiElementWrapper elementToEdit)
	{
		//this.currentTeiElementEditType = elementToEdit.getTeiElement().getElementType();
		if(ElementType.DIV.equals(elementToEdit.getTeiElement().getElementType()))
		{
			this.selectedStructuralEditType = elementToEdit.getTeiElement().getType();
		}
		else
		{
			this.selectedStructuralEditType = elementToEdit.getTeiElement().getElementType().name();
		}
		
		
		currentEditElementWrapperRestore = elementToEdit;
		currentEditElementWrapper = new TeiElementWrapper();
		
		PbOrDiv cloned = null;
		switch(elementToEdit.getTeiElement().getElementType())
		{
			case DIV :
			{
				cloned = new Div((Div)elementToEdit.getTeiElement());
				break;
			}
			case FIGURE :
			{
				cloned = new Figure((Figure)elementToEdit.getTeiElement());
				break;
			}
			case TITLE_PAGE :
			{
				cloned = new TitlePage((TitlePage)elementToEdit.getTeiElement());
				break;
			}
		
		
		
		}
		
		
		currentEditElementWrapper.setTeiElement(cloned);
		
	}
	
	public void cancelEditStructuralElement()
	{
		this.currentEditElementWrapper = null;
		this.currentEditElementWrapperRestore = null;
		this.selectedStructuralEditType = null;
	
	}
	
	

	
	
	public void updateEditedStructuralElement()
	{
		currentEditElementWrapperRestore.setTeiElement(currentEditElementWrapper.getTeiElement());
		currentEditElementWrapperRestore.getPartnerElement().setTeiElement(currentEditElementWrapper.getTeiElement());
		rerenderThumbnailPage(new TeiElementWrapper[]{currentEditElementWrapperRestore.getPagebreakWrapper()});
		cancelEditStructuralElement();
		
	}
	
	
	
	public void createStructuralElement()
	{
		
		createStructuralElementAtEndOfPage(currentNewElement, selectedPb);
		//create as sibling, if parent is not body
		ElementType parentType = currentNewElement.getTreeWrapperNode().getParent().getTeiElementWrapper().getTeiElement().getElementType();
		if(!ElementType.BODY.equals(parentType) &&
			!ElementType.FRONT.equals(parentType) &&
			!ElementType.BACK.equals(parentType))
		{
			moveElementToLeft(currentNewElement);
		}
		
		//setSelectedStructuralElement(currentEditElement);
		
		//add as sibling if element is of type titlePage or figure
		/*
		if(ElementType.FIGURE.equals(currentEditElement.getTeiElement().getElementType()) || 
				ElementType.TITLE_PAGE.equals(currentEditElement.getTeiElement().getElementType()))
		{
			moveElementToLeft(currentEditElement);
		}
		*/
		selectedStructuralElementTypeChanged();
		rerenderThumbnailPage(new TeiElementWrapper[]{selectedPb});
	}
	
	
	/**
	 * Adds a structural element to the tree, at the end of the given page
	 * @param elementToAdd The structural element to be added
	 * @param pb The given page
	 */
	public void createStructuralElementAtEndOfPage(TeiElementWrapper elementToAdd, TeiElementWrapper pb)
	{
		
		
		//int selectedPbIndex = flatTeiElementList.indexOf(selectedPb);
		
		elementToAdd.setPositionType(PositionType.START);
		elementToAdd.setPagebreakWrapper(pb);
		//currentEditElement.setPagebreakId(selectedPb.getTeiElement().getId());
		
		
		TeiElementWrapper endEditElement = new TeiElementWrapper();
		endEditElement.setPositionType(PositionType.END);
		endEditElement.setPartnerElement(elementToAdd);
		endEditElement.setTeiElement(elementToAdd.getTeiElement());
		
		//endEditElement.setPagebreakId(selectedPb.getTeiElement().getId());
		elementToAdd.setPartnerElement(endEditElement);
		
		int nextPbIndex = flatTeiElementList.indexOf(pb.getNextPagebreak());
		
		//if it is the last page, use the last element
		if(nextPbIndex == -1)
		{
			nextPbIndex = flatTeiElementList.size() - 1;
		}
		
		//Add start directly before pagebreak
		flatTeiElementList.add(nextPbIndex, elementToAdd);
		
		
		
		/*
		if(ElementType.FIGURE.equals(currentEditElement.getTeiElement().getElementType()) || 
				ElementType.TITLE_PAGE.equals(currentEditElement.getTeiElement().getElementType()))
		{
			//Add end of figures and title pages to the same page
			endEditElement.setPagebreakWrapper(pb);
			flatTeiElementList.add(nextPbIndex+1, endEditElement);
		}
		
		else
		{
		*/
			//Add end before next start or end
			for(int i = nextPbIndex + 1; i<flatTeiElementList.size(); i++)
			{
				TeiElementWrapper wrapper = flatTeiElementList.get(i);
				if(PositionType.START.equals(wrapper.getPositionType()) || PositionType.END.equals(wrapper.getPositionType()))
				{
					flatTeiElementList.add(i, endEditElement);
					endEditElement.setPagebreakWrapper(wrapper.getPagebreakWrapper());
					break;
				}
			}
		//}
		
		//Add end element directly before end of parent
		//int lastParentIndex = flatTeiElementList.indexOf(parent.getPartnerElement());
		
		
		updateTree();

	}
	
	
	
	
	
	/*
	public void createStructuralElementAsSiblingAfterOnPage(TeiElementWrapper elementToAdd, TeiElementWrapper selectedElement)
	{
		//Only possible if
		// 1. there is currently no sibling after the current element
		// 2. there is an sibling after which starts on the same page
		
		// -> Reset end of selected element and of all its children to start page of selected element
		// -> add to end of page
		
		
		
		//TODO MAke method create structuralelement after
		
		elementToAdd.setPositionType(PositionType.START);

		TeiElementWrapper endEditElement = new TeiElementWrapper();
		endEditElement.setPositionType(PositionType.END);
		endEditElement.setPartnerElement(elementToAdd);
		endEditElement.setTeiElement(elementToAdd.getTeiElement());
		elementToAdd.setPartnerElement(endEditElement);
		
		
		int selectedIndex = flatTeiElementList.indexOf(selectedElement);
		int selectedEndIndex = flatTeiElementList.indexOf(selectedElement.getPartnerElement());

		List<TeiElementWrapper> sublist = new ArrayList<TeiElementWrapper>();
		for(int i = selectedIndex; i<=selectedEndIndex; i++)
		{
			TeiElementWrapper wrapper = flatTeiElementList.get(i);
			
			if(!ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
			{ 
				//flatTeiElementList.remove(wrapper);
				wrapper.setPagebreakWrapper(selectedElement.getPagebreakWrapper());
				sublist.add(wrapper);
				
			}
		}
		
		flatTeiElementList.removeAll(sublist);
		flatTeiElementList.addAll(selectedIndex, sublist);
		
		//Update selectedEndIndex
		selectedEndIndex = flatTeiElementList.indexOf(selectedElement.getPartnerElement());
		
		//Add start element directly after selected end
		flatTeiElementList.add(selectedEndIndex+1, elementToAdd);
		elementToAdd.setPagebreakWrapper(selectedElement.getPartnerElement().getPagebreakWrapper());
		
		
		
		//Add end before next start or end
		for(int i = selectedEndIndex + 2; i<flatTeiElementList.size(); i++)
		{
			TeiElementWrapper wrapper = flatTeiElementList.get(i);
			if(PositionType.START.equals(wrapper.getPositionType()) || PositionType.END.equals(wrapper.getPositionType()))
			{
				flatTeiElementList.add(i, endEditElement);
				endEditElement.setPagebreakWrapper(wrapper.getPagebreakWrapper());
				break;
			}
		}
				
		
		updateTree();
		

	}
	
	
	public void createStructuralElementAsSiblingBefore(TeiElementWrapper elementToAdd, TeiElementWrapper currentElement)
	{

	}
	
	*/

	/**
	 * Moves the given structural element one hierarchical level up, means the former parent becomes the new sibling-before
	 * @param startElementWrapper
	 */
	public void moveElementToLeft(TeiElementWrapper startElementWrapper)
	{
		//end of parentElement to be moved directly before start of startelement	
		TeiElementWrapper parentWrapper = startElementWrapper.getTreeWrapperNode().getParent().getTeiElementWrapper();
		TeiElementWrapper parentEndWrapper = parentWrapper.getPartnerElement();
		
		flatTeiElementList.remove(parentEndWrapper);
		int startIndex = flatTeiElementList.indexOf(startElementWrapper);
		
		flatTeiElementList.add(startIndex, parentEndWrapper);
		TeiElementWrapper oldPagebreakWrapper = parentEndWrapper.getPagebreakWrapper();
		parentEndWrapper.setPagebreakWrapper(startElementWrapper.getPagebreakWrapper());
		
		/*
		//end of start element to be moved directly after end element of last sibling
		List<TreeWrapperNode> siblings = startElementWrapper.getTreeWrapperNode().getParent().getChildren();
		TreeWrapperNode lastSibling = siblings.get(siblings.size()-1);
		
		//if the sibling is not the same as the element to move
		if(!lastSibling.getTeiElementWrapper().equals(startElementWrapper))
		{

			TeiElementWrapper endElement = startElementWrapper.getPartnerElement();
			flatTeiElementList.remove(endElement);
			
			TeiElementWrapper lastSiblingEndElement = lastSibling.getTeiElementWrapper().getPartnerElement();
			int siblingEndIndex = flatTeiElementList.indexOf(lastSiblingEndElement);
			flatTeiElementList.add(siblingEndIndex+1, endElement);
			
			endElement.setPagebreakWrapper(lastSiblingEndElement.getPagebreakWrapper());
		}
		*/
		updateTree();
		rerenderThumbnailPage(new TeiElementWrapper[]{startElementWrapper.getPagebreakWrapper(), oldPagebreakWrapper});
	}
	
	
	
	/**
	 * Moves the given structural element one hierarchical level down, means the former sibling-before is the new parent
	 * @param startElementWrapper
	 */
	public void moveElementToRight(TeiElementWrapper startElementWrapper)
	{
		//end of sibling-before to be moved directly after end of startElement
		int startElementIndexInParent = startElementWrapper.getTreeWrapperNode().getParent().getChildren().indexOf(startElementWrapper.getTreeWrapperNode());
		TeiElementWrapper siblingBeforeWrapperEnd = startElementWrapper.getTreeWrapperNode().getParent().getChildren().get(startElementIndexInParent - 1).getTeiElementWrapper().getPartnerElement();
		
		flatTeiElementList.remove(siblingBeforeWrapperEnd);
		int endIndex = flatTeiElementList.indexOf(startElementWrapper.getPartnerElement());
		
		flatTeiElementList.add(endIndex+1, siblingBeforeWrapperEnd);
		TeiElementWrapper oldPagebreakWrapper = siblingBeforeWrapperEnd.getPagebreakWrapper();
		siblingBeforeWrapperEnd.setPagebreakWrapper(startElementWrapper.getPartnerElement().getPagebreakWrapper());
		updateTree();
		rerenderThumbnailPage(new TeiElementWrapper[]{startElementWrapper.getPagebreakWrapper(), oldPagebreakWrapper});
	}
	
	
	/** Moves an element up, that means setting it in front of its preceding sibling.
	 * The end elements of the preceding sibling are automatically adjusted to the next following structural element
	 * @param startElementWrapper
	 */
	public void moveElementUp(TeiElementWrapper startElementWrapper)
	{
		
		int indexInParent = startElementWrapper.getTreeWrapperNode().getParent().getChildren().indexOf(startElementWrapper.getTreeWrapperNode());
		List<TeiElementWrapper> pbThumbnailsToBeUpdated = new ArrayList<TeiElementWrapper>();
		//if element has a sibling before
		if(indexInParent > 0)
		{
			TeiElementWrapper siblingBeforeWrapper = startElementWrapper.getTreeWrapperNode().getParent().getChildren().get(indexInParent - 1).getTeiElementWrapper();
			
			int startIndex = flatTeiElementList.indexOf(startElementWrapper);
			int endIndex = flatTeiElementList.indexOf(startElementWrapper.getPartnerElement());

			
			//Get start element wrapper with all its children
			List<TeiElementWrapper> sublist = new ArrayList<TeiElementWrapper>();
			for(int i = startIndex; i<=endIndex; i++)
			{
				TeiElementWrapper wrapper = flatTeiElementList.get(i);
				
				if(!ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
				{
					//flatTeiElementList.remove(wrapper);
					pbThumbnailsToBeUpdated.add(wrapper.getPagebreakWrapper());
					wrapper.setPagebreakWrapper(siblingBeforeWrapper.getPagebreakWrapper());
					sublist.add(wrapper);
					
				}
			}
			

			flatTeiElementList.removeAll(sublist);
			
			int siblingBeforeStartIndex = flatTeiElementList.indexOf(siblingBeforeWrapper);
			flatTeiElementList.addAll(siblingBeforeStartIndex, sublist);
			

			
			//now extend the ends of the sibling to the next div element...
			int siblingBeforeEndIndex = flatTeiElementList.indexOf(siblingBeforeWrapper.getPartnerElement());

			//..at first, get all end elements of the sibling and its last childs...
			List<TeiElementWrapper> siblingsEndElements = new ArrayList<TeiElementWrapper>();
			for(int i = siblingBeforeEndIndex; i>=0; i--)
			{
				TeiElementWrapper wrapper = flatTeiElementList.get(i);
				
				if(PositionType.END.equals(wrapper.getPositionType()))
				{
					siblingsEndElements.add(0, wrapper);
				}
				else if(!ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
				{
					break;
				}
				
			}
			
			
		
			//...then find the next start or end div
			TeiElementWrapper wrapperToAddEnds = null;
			for(int i = siblingBeforeEndIndex + 1; i<flatTeiElementList.size(); i++)
			{
				TeiElementWrapper wrapper = flatTeiElementList.get(i);
				
				if(PositionType.START.equals(wrapper.getPositionType()) || PositionType.END.equals(wrapper.getPositionType()))
				{
					wrapperToAddEnds = wrapper;
					break;
				}

			}
			
		
			
			//... and move them there
			flatTeiElementList.removeAll(siblingsEndElements);
			flatTeiElementList.addAll(flatTeiElementList.indexOf(wrapperToAddEnds), siblingsEndElements);
			
			//...and set their endpoint
			for(TeiElementWrapper wrapper : siblingsEndElements)
			{
				pbThumbnailsToBeUpdated.add(wrapper.getPagebreakWrapper());
				wrapper.setPagebreakWrapper(wrapperToAddEnds.getPagebreakWrapper());
			}
			
		}
		
		
		updateTree();
		
		rerenderThumbnailPage(pbThumbnailsToBeUpdated.toArray(new TeiElementWrapper[0]));
		
		
		//Just exchange the tei element of the start and end with the ones of the sibling before
		/*
		int startElementIndexInParent = startElementWrapper.getTreeWrapperNode().getParent().getChildren().indexOf(startElementWrapper.getTreeWrapperNode());
		TeiElementWrapper endElementWrapper = startElementWrapper.getPartnerElement();
		
		TeiElementWrapper siblingBeforeElementStart = startElementWrapper.getTreeWrapperNode().getParent().getChildren().get(startElementIndexInParent-1).getTeiElementWrapper();
		TeiElementWrapper siblingBeforeElementEnd = siblingBeforeElementStart.getPartnerElement();
		
		
		PbOrDiv startTeiElement  = startElementWrapper.getTeiElement();
		
		
		PbOrDiv siblingStartTeiElement = siblingBeforeElementStart.getTeiElement();
		
		
		startElementWrapper.setTeiElement(siblingStartTeiElement);
		endElementWrapper.setTeiElement(siblingStartTeiElement);
		siblingBeforeElementStart.setTeiElement(startTeiElement);
		siblingBeforeElementEnd.setTeiElement(startTeiElement);
		*/
		
		
		
	}
	
	public void moveElementDown(TeiElementWrapper startElementWrapper)
	{

		int indexInParent = startElementWrapper.getTreeWrapperNode().getParent().getChildren().indexOf(startElementWrapper.getTreeWrapperNode());
		
		//if node has sibling after
		if(indexInParent + 1 <= startElementWrapper.getTreeWrapperNode().getParent().getChildren().size())
		{
			TeiElementWrapper siblingAfter = startElementWrapper.getTreeWrapperNode().getParent().getChildren().get(indexInParent + 1).getTeiElementWrapper();
			
			//move up sibling-after
			moveElementUp(siblingAfter);
		}

	}
	
	
	public List<TeiElementWrapper> getElementsToNextPb(TeiElementWrapper wrapper)
	{
		int index = flatTeiElementList.indexOf(wrapper);
		int endIndex = -1;
		for(int i = index + 1; i < flatTeiElementList.size(); i++)
		{
			if(ElementType.PB.equals(flatTeiElementList.get(i).getTeiElement().getElementType()))
			{
				endIndex = i;
				break;
			}
		}
		
		if(endIndex==-1)
		{
			endIndex=flatTeiElementList.size();
		}
		return flatTeiElementList.subList(index+1, endIndex);
	}
	

	public TeiElementWrapper getEndPageForElement(TeiElementWrapper wrapper)
	{
		int index = flatTeiElementList.indexOf(wrapper);
		int endIndex = flatTeiElementList.indexOf(wrapper.getPartnerElement());
		
		for(int i=endIndex; i==0; i--)
		{
			if(ElementType.PB.equals(flatTeiElementList.get(i).getTeiElement().getElementType()))
			{
				return flatTeiElementList.get(i);
			}
		}
		
		return null;
		
	}
	
	public TeiElementWrapper getEndPageForPagebreak(TeiElementWrapper pbWrapper)
	{
		int index = flatTeiElementList.indexOf(pbWrapper);
		
		
		for(int i=index+1; i<flatTeiElementList.size(); i++)
		{
			if(ElementType.PB.equals(flatTeiElementList.get(i).getTeiElement().getElementType()))
			{
				return flatTeiElementList.get(i);
			}
		}
		
		return pbWrapper;
		
	}
	
	
	public List<SelectItem> getEndPagesSelectItemsForElement(TeiElementWrapper startTeiElementWrapper)
	{
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		
		int currentEndIndex = flatTeiElementList.indexOf(startTeiElementWrapper.getPartnerElement());
		
		
		//Add pages before
		for(int i = currentEndIndex - 1; i>=0; i--)
		{
			TeiElementWrapper wrapper =  flatTeiElementList.get(i);
			if(PositionType.START.equals(wrapper.getPositionType()) || PositionType.END.equals(wrapper.getPositionType()))
			{
				break;
			}
			else if(ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
			{
				TeiElementWrapper lastPb = wrapper.getLastPagebreak();
				if(lastPb != null)
				{
					list.add(0, getSelectItemForPb(lastPb));
				}
				
			}
		}
		
		//Add current page
		TeiElementWrapper currentPb = startTeiElementWrapper.getPartnerElement().getPagebreakWrapper();
		list.add(getSelectItemForPb(currentPb));

		//Add pages after
		for(int i = currentEndIndex + 1; i<flatTeiElementList.size(); i++)
		{
			TeiElementWrapper wrapper =  flatTeiElementList.get(i);
			if(PositionType.START.equals(wrapper.getPositionType()) || PositionType.END.equals(wrapper.getPositionType()))
			{
				break;
			}
			else if(ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
			{
				
				list.add(getSelectItemForPb(wrapper));

			}
		}

		
		
		/*
		
		
		int startIndex = flatTeiElementList.indexOf(teiElementWrapper.getPagebreakWrapper());
		if(startIndex == -1)
		{
			startIndex = 0;
		}
		
		for(int i = startIndex; i< flatTeiElementList.size(); i++)
		{
			TeiElementWrapper wrapper = flatTeiElementList.get(i);
			
			if(ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
			{
				list.add(new SelectItem(wrapper.getTeiElement().getId(), wrapper.getPage().getOrder() + 1 + " / " + wrapper.getPage().getOrderLabel()));
			}
			else
			{
				if(PositionType.START.equals(wrapper.getPositionType()))
				{
					//Jump to end of Element
					i = flatTeiElementList.indexOf(wrapper.getPartnerElement());
				}
				
			}
		}
		*/
		return list;
	}
	
	/**
	 * Returns the last pagebreak before the currently selectedPage, which has a structural element applied
	 * @return
	 */
	public TeiElementWrapper getTreeScrollPositionPagebreak()
	{
		int pos = flatTeiElementList.indexOf(selectedPb);
		
		
		for(int i=pos; i>=0; i--)
		{
			TeiElementWrapper wrapper = flatTeiElementList.get(i);
			if(!ElementType.PB.equals(wrapper.getTeiElement().getElementType()))
			{
				if (PositionType.START.equals(wrapper.getPositionType()))
				{
					//System.out.println(wrapper.getPagebreakWrapper().getTeiElement().getId());
					return wrapper.getPagebreakWrapper();
				}
			}
		}
		//System.out.println("null");
		return null;
		
		
			
		
	}
	
	public List<TreeWrapperNode> getSubTree(TeiElementWrapper pbWrapper)
	{
		
		List<TreeWrapperNode> subTreeList = new ArrayList<TreeWrapperNode>();
		
		TreeWrapperNode firstParent = null;
		int index = flatTeiElementList.indexOf(pbWrapper);
		
		for(int i = index + 1; i < flatTeiElementList.size(); i++)
		{
			TeiElementWrapper currentTeiElementWrapper = flatTeiElementList.get(i);
			if((!ElementType.PB.equals(currentTeiElementWrapper.getTeiElement().getElementType())) && PositionType.START.equals(currentTeiElementWrapper.getPositionType()) )
			{
				if(firstParent == null)
				{
					firstParent = currentTeiElementWrapper.getTreeWrapperNode().getParent();
				}
				else
				{
					if(flatTeiElementList.indexOf(firstParent) > flatTeiElementList.indexOf(currentTeiElementWrapper.getTreeWrapperNode().getParent()))
					{
						firstParent = currentTeiElementWrapper.getTreeWrapperNode().getParent();
					}
				}
				
			}
			else
			{
				//Stop when PB is reached
				break;
			}

		}
		
		if(firstParent == null)
		{
			return treeWrapperNodes;
		}
		
		subTreeList.add(firstParent);
		return subTreeList;
		
	}
	
	public boolean getIsEditable(TeiElementWrapper wrapper)
	{
		List<TeiElementWrapper> elementsToNextPb = getElementsToNextPb(selectedPb);
		return elementsToNextPb.contains(wrapper);
		
	}
	
	
	public void moveEndPageTo(TeiElementWrapper startElementWrapper, String pageBreakToMoveToId)
	{
		logger.debug("Move structural element end page" + startElementWrapper.getTeiElement().getType() + " to page "  + pageBreakToMoveToId);
		
		TeiElementWrapper endElement = startElementWrapper.getPartnerElement();
		TeiElementWrapper pageBreakToMoveTo = getPbWrapperforId(pageBreakToMoveToId);
		
		
		
		endElement.setPagebreakWrapper(pageBreakToMoveTo);
		int startElementIndex = flatTeiElementList.indexOf(startElementWrapper.getPagebreakWrapper());
		
		
		flatTeiElementList.remove(endElement);
		
		int balanceCounter = 0;
		boolean set = false;
		boolean started = false;
		for(int i = startElementIndex; i<flatTeiElementList.size(); i++)
		{
			TeiElementWrapper currentTeiElementWrapper = flatTeiElementList.get(i);
			if(currentTeiElementWrapper.equals(startElementWrapper))
			{
				
				started=true;
				balanceCounter--;
			}
			
			if(currentTeiElementWrapper.equals(pageBreakToMoveTo))
			{
				set=true;
			}
			else if (started && PositionType.START.equals(currentTeiElementWrapper.getPositionType()))
			{
				balanceCounter++;
			}
			else if (started && PositionType.END.equals(currentTeiElementWrapper.getPositionType()))
			{
				balanceCounter--;
			}
			
			if(started && set && balanceCounter==0)
			{
				System.out.println("Set end element to pos " + i+1);
				flatTeiElementList.add(i+1, endElement);
				//endElement.s
				break;
			}
			
		}
		updateTree();
	
	}
	/*
	public TreeWrapperNode getSubTreeForPb(TeiElementWrapper pbWrapper)
	{
		int index = flatTeiElementList.indexOf(pbWrapper);
		
		
	}
	*/
	
	private void updateTree()
	{
		validationList = new ArrayList<ValidationWrapper>();
		this.treeWrapperNodes = flatTeiElementListToTreeWrapper(flatTeiElementList, validationList);
	}
	
	
	
	public void selectPb(TeiElementWrapper pb)
	{
		TeiElementWrapper oldSelectedPb = selectedPb;
		this.setSelectedPb(pb);
		this.selectedPbType = pb.getTeiElement().getType();
		this.selectedPbNumeration = pb.getTeiElement().getNumeration();
		this.selectedPbSubtype = ((Pagebreak)pb.getTeiElement()).getSubtype();
		
		this.selectedGoToBoxPageId = pb.getTeiElement().getId();
		rerenderThumbnailPage(new TeiElementWrapper[]{oldSelectedPb, selectedPb});
		
		/*
		int pbIndex = flatTeiElementList.indexOf(pb);
		
		int nextPbIndex = flatTeiElementList.size(); 
		if(pb.getNextPagebreak()!=null)
		{
			nextPbIndex = flatTeiElementList.indexOf(pb.getNextPagebreak());
		}
		
				
		
		for (int i=nextPbIndex; i>=0; i--)
		{
			TeiElementWrapper currentElementWrapper = flatTeiElementList.get(i); 
			if(PositionType.START.equals(currentElementWrapper.getPositionType()))
			{
				selectedStructuralElement = currentElementWrapper;
				break;
			}
		}
		*/
		
		//System.out.println("Selected Structural: " + selectedStructuralElement.getTeiElement().getElementType());
		
		
	}
	
	
	public void selectStructuralElement(TeiElementWrapper elementWrapper)
	{
		//setSelectedStructuralElement(elementWrapper);
		//if(selectedStructuralElement.getPagebreakWrapper()!= selectedPb)
		//{
			TeiElementWrapper oldSelectedPb = selectedPb;
			setSelectedPb(elementWrapper.getPagebreakWrapper());
			rerenderThumbnailPage(new TeiElementWrapper[]{oldSelectedPb, selectedPb});
		//}

	}
	
	public List<TeiElementWrapper> getFlatTeiElementList() {
		return flatTeiElementList;
	}

	public void setFlatTeiElementList(List<TeiElementWrapper> flatTeiElementList) {
		this.flatTeiElementList = flatTeiElementList;
	}
	
	

	public TeiElementWrapper getSelectedPb() {
		return selectedPb;
	}

	public void setSelectedPb(TeiElementWrapper selectedPb) {
		this.selectedPb = selectedPb;
	}

	

	public List<TreeWrapperNode> getTreeWrapperNodes() {
		return treeWrapperNodes;
	}

	public void setTreeWrapperNodes(List<TreeWrapperNode> treeWrapperNodes) {
		this.treeWrapperNodes = treeWrapperNodes;
	}

	/*
	public TeiElementWrapper getSelectedStructuralElement() {
		return selectedStructuralElement;
	}

	public void setSelectedStructuralElement(TeiElementWrapper selectedStructuralElement) {
		this.selectedStructuralElement = selectedStructuralElement;
	}
	*/
	
	public static boolean isFrontBodyBack(TeiElementWrapper wrapper)
	{
		return ElementType.BODY.equals(wrapper.getTeiElement().getElementType()) ||
				   ElementType.FRONT.equals(wrapper.getTeiElement().getElementType()) ||
				   ElementType.BACK.equals(wrapper.getTeiElement().getElementType());
	}

	public String getSelectedPaginationEndPbId() {
		return selectedPaginationEndPbId;
	}

	public void setSelectedPaginationEndPbId(String selectedPaginationEndPbId) {
		this.selectedPaginationEndPbId = selectedPaginationEndPbId;
	}
	
	private TeiElementWrapper getPbWrapperforId(String pbId)
	{
		for(TeiElementWrapper teiElementWrapper : pbList)
		{
			String id = teiElementWrapper.getTeiElement().getId();
			
			if(id!=null && pbId.equals(id))
			{
					return teiElementWrapper;
			}

		}
		return null;
	}
	
	private SelectItem getSelectItemForPb(TeiElementWrapper pb)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(pb.getPage().getOrder() + 1);
		if(pb.getTeiElement().getNumeration()!=null && !pb.getTeiElement().getNumeration().isEmpty())
		{
			sb.append(" / " + pb.getTeiElement().getNumeration());
		}
		return new SelectItem(pb.getTeiElement().getId(), sb.toString());
	}

	public PaginationType getSelectedPaginationType() {
		return selectedPaginationType;
	}

	public void setSelectedPaginationType(PaginationType selectedPaginationType) {
		this.selectedPaginationType = selectedPaginationType;
	}

	public String getSelectedPaginationStartValue() {
		return selectedPaginationStartValue;
	}

	public void setSelectedPaginationStartValue(
			String selectedPaginationStartValue) {
		this.selectedPaginationStartValue = selectedPaginationStartValue;
	}

	public String getSelectedPaginationPattern() {
		return selectedPaginationPattern;
	}

	public void setSelectedPaginationPattern(String selectedPaginationPattern) {
		this.selectedPaginationPattern = selectedPaginationPattern;
	}

	public String getSelectedPageDisplayTypeEndPbId() {
		return selectedPageDisplayTypeEndPbId;
	}

	public void setSelectedPageDisplayTypeEndPbId(
			String selectedPageDisplayTypeEndPbId) {
		this.selectedPageDisplayTypeEndPbId = selectedPageDisplayTypeEndPbId;
	}

	public String getSelectedPageTypeEndPbId() {
		return selectedPageTypeEndPbId;
	}

	public void setSelectedPageTypeEndPbId(String selectedPageTypeEndPbId) {
		this.selectedPageTypeEndPbId = selectedPageTypeEndPbId;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public TeiElementWrapper getCurrentNewElement() {
		return currentNewElement;
	}

	public void setCurrentNewElement(TeiElementWrapper currentNewElement) {
		this.currentNewElement = currentNewElement;
	}

	

	
	public TeiElementWrapper getCurrentEditElementWrapper() {
		return currentEditElementWrapper;
	}

	public void setCurrentEditElementWrapper(TeiElementWrapper currentEditElementWrapper) {
		this.currentEditElementWrapper = currentEditElementWrapper;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public List<ValidationWrapper> getValidationList() {
		return validationList;
	}

	public void setValidationList(List<ValidationWrapper> validationList) {
		this.validationList = validationList;
	}


	
	public void goToNextPage()
	{
		int selected = pbList.indexOf(selectedPb);
		if(selected<pbList.size()-1)
		{
			selectPb(pbList.get(selected+1));
		}
		
	}
	
	public void goToPreviousPage()
	{
		int selected = pbList.indexOf(selectedPb);
		if(selected>0)
		{
			selectPb(pbList.get(selected-1));
		}
		
        
	} 
	
	public void goToLastPage()
	{
		selectPb(pbList.get(pbList.size()-1));
	}
	
	public void goToFirstPage()
	{
		selectPb(pbList.get(0));
	}
	
	public void pageNumberChanged(AjaxBehaviorEvent evt)
	{
		selectPb(getPbWrapperforId(selectedGoToBoxPageId));
	}


	public List<SelectItem> getStructTypeList()
	{
		 List<SelectItem> result = new ArrayList<SelectItem>();
		 result.add(new SelectItem("acknowledgement", ApplicationBean.getResource("Label", "structuretype_acknowledgement")));
		 result.add(new SelectItem("additional", ApplicationBean.getResource("Label", "structuretype_additional")));
		 result.add(new SelectItem("advertisement", ApplicationBean.getResource("Label", "structuretype_advertisement")));
		 result.add(new SelectItem("appendix", ApplicationBean.getResource("Label", "structuretype_appendix")));
		 result.add(new SelectItem("article", ApplicationBean.getResource("Label", "structuretype_article")));
		 result.add(new SelectItem("bibliography", ApplicationBean.getResource("Label", "structuretype_bibliography")));
		 result.add(new SelectItem("book", ApplicationBean.getResource("Label", "structuretype_book")));
		 result.add(new SelectItem("chapter", ApplicationBean.getResource("Label", "structuretype_chapter")));
		 result.add(new SelectItem("content", ApplicationBean.getResource("Label", "structuretype_content")));
		 result.add(new SelectItem("corrigenda", ApplicationBean.getResource("Label", "structuretype_corrigenda")));
		 result.add(new SelectItem("curriculum vitae", ApplicationBean.getResource("Label", "structuretype_curriculum_vitae")));
		 result.add(new SelectItem("dedication", ApplicationBean.getResource("Label", "structuretype_dedication")));
		 result.add(new SelectItem("imprimatur", ApplicationBean.getResource("Label", "structuretype_imprimatur")));
		 result.add(new SelectItem("imprint", ApplicationBean.getResource("Label", "structuretype_imprint")));
		 result.add(new SelectItem("index", ApplicationBean.getResource("Label", "structuretype_index")));
		 result.add(new SelectItem("letter", ApplicationBean.getResource("Label", "structuretype_letter")));
		 result.add(new SelectItem("miscellaneous", ApplicationBean.getResource("Label", "structuretype_miscellaneous")));
		 result.add(new SelectItem("musical notation", ApplicationBean.getResource("Label", "structuretype_musical_notation")));
		 result.add(new SelectItem("part", ApplicationBean.getResource("Label", "structuretype_part")));
		 result.add(new SelectItem("preface", ApplicationBean.getResource("Label", "structuretype_preface")));
		 result.add(new SelectItem("privileges", ApplicationBean.getResource("Label", "structuretype_privileges")));
		 result.add(new SelectItem("provenance", ApplicationBean.getResource("Label", "structuretype_provenance")));
		 result.add(new SelectItem("review", ApplicationBean.getResource("Label", "structuretype_review")));
		 result.add(new SelectItem("section", ApplicationBean.getResource("Label", "structuretype_section")));

		 return result;
	}

	
	public ElementType getCurrentTeiElementType() {
		return currentTeiElementType;
	}

	public void setCurrentTeiElementType(ElementType currentTeiElementType) {
		this.currentTeiElementType = currentTeiElementType;
	}

	/*
	public ElementType getCurrentTeiElementEditType() {
		return currentTeiElementEditType;
	}

	public void setCurrentTeiElementEditType(ElementType currentTeiElementEditType) {
		this.currentTeiElementEditType = currentTeiElementEditType;
	}
	*/

	public String getSelectedStructuralType() {
		return selectedStructuralType;
	}

	public void setSelectedStructuralType(String selectedStructuralType) {
		this.selectedStructuralType = selectedStructuralType;
	}

	public boolean isPaginationInBrackets() {
		return paginationInBrackets;
	}

	public void setPaginationInBrackets(boolean paginationInBrackets) {
		this.paginationInBrackets = paginationInBrackets;
	}

	public boolean isPaginateEverySecondPage() {
		return paginateEverySecondPage;
	}

	public void setPaginateEverySecondPage(boolean paginateEverySecondPage) {
		this.paginateEverySecondPage = paginateEverySecondPage;
	}

	public String getSelectedStructuralEditType() {
		return selectedStructuralEditType;
	}

	public void setSelectedStructuralEditType(String selectedStructuralEditType) {
		this.selectedStructuralEditType = selectedStructuralEditType;
	}
	
	
	public void rerenderThumbnailPage(TeiElementWrapper[] pagesToRerender)
	{
		
		FacesContext fc = FacesContext.getCurrentInstance();
		if(fc.getPartialViewContext().isAjaxRequest())
		{
			for(TeiElementWrapper wrapper : pagesToRerender)
			{
				if(wrapper!=null && !fc.getPartialViewContext().getRenderIds().contains(wrapper.getTeiElement().getId()));
				{
					fc.getPartialViewContext().getRenderIds().add(clientIdMap.get(wrapper.getTeiElement().getId()));
				}
				
			}
		}
		//fc.getPartialViewContext().getExecuteIds().add("thumbsRepeater");
		//System.out.println(fc.getPartialViewContext().getRenderIds());
		
		
	}
	
	public String addToClientIdMap(String divId, String clientId)
	{
		clientIdMap.put(divId, clientId);
		//System.out.println("Added to client id map: " + divId + " " + clientId);
		return "";
	}

	public List<TeiElementWrapper> getPbList() {
		return pbList;
	}

	public void setPbList(List<TeiElementWrapper> pbList) {
		this.pbList = pbList;
	}

	public int getSelectedPaginationColumns() {
		return selectedPaginationColumns;
	}

	public void setSelectedPaginationColumns(int selectedPaginationColumns) {
		this.selectedPaginationColumns = selectedPaginationColumns;
	}

	

	public List<SelectItem> getStructureTypeSelectItems() {
		return structureTypeSelectItems;
	}

	public void setStructureTypeSelectItems(List<SelectItem> structureTypeSelectItems) {
		this.structureTypeSelectItems = structureTypeSelectItems;
	}

	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}

	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}


	public List<SelectItem> getPageListMenu() {
		return pageListMenu;
	}


	public void setPageListMenu(List<SelectItem> pageListMenu) {
		this.pageListMenu = pageListMenu;
	}


	public String getSelectedGoToBoxPageId() {
		return selectedGoToBoxPageId;
	}


	public void setSelectedGoToBoxPageId(String selectedGoToBoxPageId) {
		this.selectedGoToBoxPageId = selectedGoToBoxPageId;
	}


	public String getSelectedPbType() {
		return selectedPbType;
	}


	public void setSelectedPbType(String selectedPbType) {
		this.selectedPbType = selectedPbType;
	}


	public String getSelectedPbNumeration() {
		return selectedPbNumeration;
	}


	public void setSelectedPbNumeration(String selectedPbNumeration) {
		this.selectedPbNumeration = selectedPbNumeration;
	}


	public String getSelectedPbSubtype() {
		return selectedPbSubtype;
	}


	public void setSelectedPbSubtype(String selectedPbSubtype) {
		this.selectedPbSubtype = selectedPbSubtype;
	}

	
	


}
