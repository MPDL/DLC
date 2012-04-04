package de.mpg.mpdl.dlc.editor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.criteria.Selection;

import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.editor.TeiElementWrapper.PositionType;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.RomanNumberConverter;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.Body;
import de.mpg.mpdl.dlc.vo.teisd.Div;
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
	
	
	//private TeiElementWrapper selectedStructuralElement;
		
	/**
	 * Element that will be added next
	 */
	private TeiElementWrapper currentNewElement;
	
	/**
	 * Element that is currently edited
	 */
	private TeiElementWrapper currentEditElementWrapper;
	
	private PbOrDiv currentEditTeiElement;

	
	
	private ElementType selectedStructuralType;
	
	
	//Pagination values
	private String selectedPaginationEndPbId;
	
	private String selectedPageDisplayTypeEndPbId;
	
	private String selectedPageTypeEndPbId;
	
	private PaginationType selectedPaginationType;
	
	private String selectedPaginationStartValue;
	
	private String selectedPaginationPattern;
	
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	
	public enum PaginationType
	{
		ARABIC, ROMAN, FREE
	}
	
	
	public StructuralEditorBean()
	{

		selectedStructuralType = ElementType.DIV;
		
		currentNewElement = new TeiElementWrapper();
		Div div = new Div();
		div.setType("chapter");
		div.getHead().add("");
		currentNewElement.setTeiElement(div);
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		if(loginBean.isLogin())
		{
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{   
				try {
					this.volume = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle());
					volServiceBean.loadTeiSd(volume, loginBean.getUserHandle());
					
				} catch (Exception e) {
					MessageHelper.errorMessage("Problem while loading volume");
				}
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
			
			
			//Transform TEI-SD to flat element list
			List<TeiElementWrapper>[] flatLists = teiSdToFlatTeiElementList(volume.getTeiSd(), volume);
			this.flatTeiElementList = flatLists[0];
			this.pbList = flatLists[1];
			
			//Transform flat TEI to TreeWrapper
			this.treeWrapperNodes = flatTeiElementListToTreeWrapper(flatTeiElementList);
			
			
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
		
		
		System.out.println("volumeLoaded");

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
	
	
	
	private static List<TreeWrapperNode> flatTeiElementListToTreeWrapper(List<TeiElementWrapper> flatTeiElementList)
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
				System.out.print("Added Pagebreak: " +  realPage.getOrder());
				if(!pbList.isEmpty())
				{
					TeiElementWrapper lastPb = pbList.get(pbList.size()-1);
					System.out.println("--- last Pb: " + lastPb.getPage().getOrder());
					
					lastPb.setNextPagebreak(currentTeiStartElementWrapper);
					
					currentTeiStartElementWrapper.setLastPagebreak(lastPb);
				}
				pbList.add(currentTeiStartElementWrapper);
				
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
		
		
		try {
			flatTeiElementListToTeiSd(flatTeiElementList, volume.getTeiSd());
			this.volume = volServiceBean.updateVolume(volume, getLoginBean().getUserHandle(), null, true);
			volServiceBean.loadTeiSd(volume, loginBean.getUserHandle());
			
			MessageHelper.infoMessage("Structure saved successfully!");
		} catch (Exception e) {
			MessageHelper.errorMessage("Error while saving Structure");
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
	
	public void saveAndRelease()
	{
		
		
		try {
			flatTeiElementListToTeiSd(flatTeiElementList, volume.getTeiSd());
			this.volume = volServiceBean.updateVolume(volume, getLoginBean().getUserHandle(), null, true);
			this.volume = volServiceBean.releaseVolume(volume, getLoginBean().getUserHandle());
			volServiceBean.loadTeiSd(volume, loginBean.getUserHandle());
			
			MessageHelper.infoMessage("Structure saved and released successfully!");
		} catch (Exception e) {
			MessageHelper.errorMessage("Error while saving and releasing Structure");
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
	
	public void selectedStructuralElementTypeChanged()
	{
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
		
	
	}
	
	public void selectedStructuralEditElementTypeChanged()
	{
		switch (selectedStructuralType)
		{
			case DIV : 
			{
				
				Div div = new Div();
				div.setType("chapter");
				div.getHead().add("");
				
				currentEditElementWrapper.setTeiElement(div);
				currentEditElementWrapper.getPartnerElement().setTeiElement(div);
				break;
			}
			
			case FIGURE : 
			{
				
				Figure figure = new Figure();
				currentEditElementWrapper.setTeiElement(figure);
				currentEditElementWrapper.getPartnerElement().setTeiElement(figure);
				break;
			}
			
			case TITLE_PAGE : 
			{
				
				TitlePage titlePage = new TitlePage();
				titlePage.setDocTitles(new ArrayList<DocTitle>());
				titlePage.getDocTitles().add(new DocTitle());
				currentEditElementWrapper.setTeiElement(titlePage);
				currentEditElementWrapper.getPartnerElement().setTeiElement(titlePage);
				break;
			}
		
		}
		
	
	}
	
	
	
	public void applyPagination()
	{
		
		int startIndex = pbList.indexOf(selectedPb);
		
		int currentValue = -1;
		
		boolean lowerCase = false;
		
		if(selectedPaginationStartValue!=null && !selectedPaginationStartValue.isEmpty())
		{

			if(PaginationType.ARABIC.equals(selectedPaginationType))
			{
				try {
					
					currentValue = Integer.parseInt(selectedPaginationStartValue);
					
				} catch (NumberFormatException e) {
					MessageHelper.errorMessage("The given start value is invalid. Please provide an arabic number");
					return;
				} 
			}
			else if(PaginationType.ROMAN.equals(selectedPaginationType))
			{
				try {
					
					lowerCase = Character.isLowerCase(selectedPaginationStartValue.charAt(0)); 
					currentValue = RomanNumberConverter.convert(selectedPaginationStartValue);
					
				} catch (NumberFormatException e) {
					MessageHelper.errorMessage("The given start value is invalid. Please provide a roman number");
					return;
				}
			}
			
			if(!selectedPaginationPattern.isEmpty() && !selectedPaginationPattern.contains("?"))
			{
				MessageHelper.errorMessage("The given pattern is invalid. Please provide a pattern containing a '?' as placeholder");
				return;
			}
			
			
			for(int i=startIndex; i<pbList.size(); i++)
			{
				TeiElementWrapper pbWrapper = pbList.get(i);
				String pagination = null;
				
				if(PaginationType.ARABIC.equals(selectedPaginationType))
				{

						pagination = String.valueOf(currentValue);
						currentValue++;
					
				}
				else if(PaginationType.ROMAN.equals(selectedPaginationType))
				{
					pagination = RomanNumberConverter.convert(currentValue, lowerCase);
					currentValue++;
				}
				else if(PaginationType.FREE.equals(selectedPaginationType))
				{
					pagination = selectedPaginationStartValue;
				}
				
				
				if(!selectedPaginationPattern.isEmpty())
				{
					pagination = selectedPaginationPattern.replaceAll("\\?", pagination);
					
				}
				
				pbWrapper.getTeiElement().setNumeration(pagination);
				pbWrapper.getPage().setOrderLabel(pagination);
				
				
				if(pbWrapper.getTeiElement().getId().equals(selectedPaginationEndPbId))
				{
					return;
				}
			}
			
			}
		else
		{
			MessageHelper.errorMessage("Please provide a start pagination value!");
		}
		
		
	}
	
	public void applyPageDisplayType()
	{
		int startIndex = pbList.indexOf(selectedPb);
		
		String value = selectedPb.getTeiElement().getType();
		
		
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
			
			if(pbWrapper.getTeiElement().getId().equals(selectedPageDisplayTypeEndPbId))
			{
				return;
			}
		}
		
	}
	
	public void applyPageType()
	{
		int startIndex = pbList.indexOf(selectedPb);
		
		String value = ((Pagebreak)selectedPb.getTeiElement()).getSubtype();
		
		
		for(int i=startIndex; i<pbList.size(); i++)
		{
			TeiElementWrapper pbWrapper = pbList.get(i);
			Pagebreak pb = (Pagebreak)pbWrapper.getTeiElement();
			
			pb.setSubtype(value);
			
			
			
			if(pbWrapper.getTeiElement().getId().equals(selectedPageTypeEndPbId))
			{
				return;
			}
		}
		
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
	}
	
	public void editStructuralElement(TeiElementWrapper elementToEdit)
	{
		this.selectedStructuralType = elementToEdit.getTeiElement().getElementType();
		currentEditElementWrapper = elementToEdit;
		
		
		
		
	}
	
	
	public void updateEditedStructuralElement()
	{
		System.out.println("Update struct element");
	}
	
	
	
	public void createStructuralElement()
	{
		createStructuralElementAtEndOfPage(currentNewElement, selectedPb);
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
		siblingBeforeWrapperEnd.setPagebreakWrapper(startElementWrapper.getPartnerElement().getPagebreakWrapper());
		updateTree();
	}
	
	
	/** Moves an element up, that means setting it in front of its preceding sibling.
	 * The end elements of the preceding sibling are automatically adjusted to the next following structural element
	 * @param startElementWrapper
	 */
	public void moveElementUp(TeiElementWrapper startElementWrapper)
	{
		
		int indexInParent = startElementWrapper.getTreeWrapperNode().getParent().getChildren().indexOf(startElementWrapper.getTreeWrapperNode());
		
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
				wrapper.setPagebreakWrapper(wrapperToAddEnds.getPagebreakWrapper());
			}
			
		}
		
		
		updateTree();
		
		
		
		
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
		list.add(new SelectItem(currentPb.getTeiElement().getId(), currentPb.getPage().getOrder() + 1 + " / " + currentPb.getPage().getOrderLabel()));

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
		this.treeWrapperNodes = flatTeiElementListToTreeWrapper(flatTeiElementList);
	}
	
	
	
	public void selectPb(TeiElementWrapper pb)
	{
		this.setSelectedPb(pb);
		int pbIndex = flatTeiElementList.indexOf(pb);
		
		int nextPbIndex = flatTeiElementList.size(); 
		if(pb.getNextPagebreak()!=null)
		{
			nextPbIndex = flatTeiElementList.indexOf(pb.getNextPagebreak());
		}
				
		/*
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
		System.out.println("Selected Pb: " + selectedPb.getTeiElement().getId());
		//System.out.println("Selected Structural: " + selectedStructuralElement.getTeiElement().getElementType());
		
		
	}
	
	/*
	public void selectStructuralElement(TeiElementWrapper elementWrapper)
	{
		setSelectedStructuralElement(elementWrapper);
		if(selectedStructuralElement.getPagebreakWrapper()!= selectedPb)
		{
			setSelectedPb(selectedStructuralElement.getPagebreakWrapper());
		}

	}
	*/
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

	public ElementType getSelectedStructuralType() {
		return selectedStructuralType;
	}

	public void setSelectedStructuralType(ElementType selectedStructuralType) {
		this.selectedStructuralType = selectedStructuralType;
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
		return new SelectItem(pb.getTeiElement().getId(), pb.getPage().getOrder() + 1 + " / " + pb.getPage().getOrderLabel());
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

	

	public PbOrDiv getCurrentEditTeiElement() {
		return currentEditTeiElement;
	}

	public void setCurrentEditTeiElement(PbOrDiv currentEditTeiElement) {
		this.currentEditTeiElement = currentEditTeiElement;
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


	
	


}
