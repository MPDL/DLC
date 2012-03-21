package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.criteria.Selection;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.editor.TeiElementWrapper.PositionType;
import de.mpg.mpdl.dlc.editor.TeiNode.Type;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.viewer.VolumeLoaderBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.Body;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.Figure;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;
import de.mpg.mpdl.dlc.vo.teisd.TitlePage;

@ManagedBean
@SessionScoped
@URLMapping(id = "structuralEditor", pattern = "/edit/#{structuralEditorBean.volumeId}", viewId = "/structuralEditor.xhtml")
public class StructuralEditorBean extends VolumeLoaderBean {

	private static Logger logger = Logger.getLogger(StructuralEditorBean.class);
	
	//Flat list of Treee
	private List<TeiElementWrapper> flatTeiElementList;
	
	private List<TreeWrapperNode> treeWrapperNodes;
	
	
	private TeiElementWrapper selectedPb;
	//private List<PagebreakWrapper> pagebreakWrapperList;
	
	
	private TeiElementWrapper selectedStructuralElement;
	
	
	private TeiElementWrapper currentEditElement;
	
	//private PagebreakWrapper selectedPbWrapper;
	

	private ElementType selectedStructuralType;
	
	
	public enum PaginationType
	{
		ALL_ARABIC, ALL_ROMAN, ALL_FREE, SELECTED_ARABIC, SELECTED_ROMAN, SELECTED_FREE
	}
	
	
	public StructuralEditorBean()
	{

		selectedStructuralType = ElementType.DIV;
		
		currentEditElement = new TeiElementWrapper();
		Div div = new Div();
		div.setType("chapter");
		div.getHead().add("");
		currentEditElement.setTeiElement(div);
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		super.loadVolume();
	}
	
	@Override
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
			this.flatTeiElementList = teiSdToFlatTeiElementList(volume.getTeiSd(), volume);
			
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
	
	
	
	
	
	
	
	
	
	private static List<TeiElementWrapper> teiSdToFlatTeiElementList(TeiSd teiSd, Volume v)
	{
		List<TeiElementWrapper> flatTeiElementList = new ArrayList<TeiElementWrapper>();
		recursiveTeiSdToFlat(flatTeiElementList, teiSd.getPbOrDiv(), v, new ArrayList<TeiElementWrapper>(), new ArrayList<TeiElementWrapper>());
		return flatTeiElementList;
		
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
	
	
	public void selectedStructuralElementTypeChanged()
	{
		switch (selectedStructuralType)
		{
			case DIV : 
			{
				currentEditElement = new TeiElementWrapper();
				Div div = new Div();
				div.setType("chapter");
				div.getHead().add("");
				
				currentEditElement.setTeiElement(div);
				break;
			}
			
			case FIGURE : 
			{
				currentEditElement = new TeiElementWrapper();
				Figure figure = new Figure();
				currentEditElement.setTeiElement(figure);
				break;
			}
			
			case TITLE_PAGE : 
			{
				currentEditElement = new TeiElementWrapper();
				TitlePage titlePage = new TitlePage();
				currentEditElement.setTeiElement(titlePage);
				break;
			}
		
		}
		
	
	}
	
	
	
	public void deleteStructuralElement(TeiElementWrapper elementToDelete)
	{
		flatTeiElementList.remove(elementToDelete);
		flatTeiElementList.remove(elementToDelete.getPartnerElement());
		updateTree();
	}
	
	public void createStructuralElementAsSiblingAfter()
	{
		
		createStructuralElementAsSiblingAfterOnPage(currentEditElement, selectedStructuralElement);
		setSelectedStructuralElement(currentEditElement);
		selectedStructuralElementTypeChanged();
	}
	
	public void createStructuralElement()
	{
		/*
		if(isFrontBodyBack(selectedStructuralElement))
		{
		*/
			createStructuralElementAtEndOfPage(currentEditElement, selectedPb);
			/*
		}
		else
		{
			createStructuralElementAsSiblingAfter(currentEditElement, selectedStructuralElement);
		}
		*/
		
		//int selectedPbIndex = flatTeiElementList.indexOf(selectedPb);
		
		/*
		currentEditElement.setPositionType(PositionType.START);
		currentEditElement.setPagebreakWrapper(selectedPb);
		//currentEditElement.setPagebreakId(selectedPb.getTeiElement().getId());
		
		
		TeiElementWrapper endEditElement = new TeiElementWrapper();
		endEditElement.setPositionType(PositionType.END);
		endEditElement.setPartnerElement(currentEditElement);
		endEditElement.setTeiElement(currentEditElement.getTeiElement());
		
		//endEditElement.setPagebreakId(selectedPb.getTeiElement().getId());
		currentEditElement.setPartnerElement(endEditElement);
		
		int nextPbIndex = flatTeiElementList.indexOf(selectedPb.getNextPagebreak());
		
		flatTeiElementList.add(nextPbIndex, currentEditElement);
		
		
		
		TeiElementWrapper lastParentElement = null;
		//TeiElementWrapper lastSiblingElement = null;
		int balanceCounter = 0;
		for(int i=nextPbIndex-1; i>=0; i--)
		{
			TeiElementWrapper currentElementWrapper = flatTeiElementList.get(i);
			System.out.println(currentElementWrapper.getPositionType() + " " + currentElementWrapper.getTeiElement().getElementType());
			if(PositionType.END.equals(currentElementWrapper.getPositionType()))
			{
				balanceCounter--;
				
			}
			else if(PositionType.START.equals(currentElementWrapper.getPositionType()))
			{
				
				if(balanceCounter==0)
				{
					lastParentElement = currentElementWrapper;
					break;
				}
				
				balanceCounter++;
				
			}
		}
		
		
		//Add end element directly before end of parent
		int lastParentIndex = flatTeiElementList.indexOf(lastParentElement.getPartnerElement());
		flatTeiElementList.add(lastParentIndex, endEditElement);
		endEditElement.setPagebreakWrapper(lastParentElement.getPartnerElement().getPagebreakWrapper());
		
		
		
		
		*/
		
		setSelectedStructuralElement(currentEditElement);
		
		selectedStructuralElementTypeChanged();

		
	}
	
	
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
		
		//Add end element directly before end of parent
		//int lastParentIndex = flatTeiElementList.indexOf(parent.getPartnerElement());
		
		
		updateTree();

	}
	
	
	
	
	
	
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
	
	
	public void moveElementToLeft2(TeiElementWrapper startElementWrapper)
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
	
	
	
	public void moveElementToRight(TeiElementWrapper startElementWrapper)
	{
		//end of sibling-before to be moved directly after end of startElement
		
		int startElementIndexInParent = startElementWrapper.getTreeWrapperNode().getParent().getChildren().indexOf(startElementWrapper.getTreeWrapperNode());
		
/*
		while(startElementWrapper.getTreeWrapperNode().getParent().getChildren().get(startElementIndexInParent - 1).getTeiElementWrapper().getTeiElement().getElementType().equals(ElementType.PB))
		{
			startElementIndexInParent--;
			
		}
		
		*/
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
		
		int startPbIndex = flatTeiElementList.indexOf(startTeiElementWrapper.getPagebreakWrapper());
		
		//End page can only be mobved inside the parent element
		
		TeiElementWrapper endWrapper = null;
		int endIndex = flatTeiElementList.size();
		if(startTeiElementWrapper.getTreeWrapperNode().getParent()!=null)
		{
			 endWrapper = startTeiElementWrapper.getTreeWrapperNode().getParent().getTeiElementWrapper().getPartnerElement();
			 endIndex = flatTeiElementList.indexOf(endWrapper);
		}
		
		
		
		if(startPbIndex == -1)
		{
			startPbIndex = 0; 
		}
		
		
		boolean started = false;
		for(int i = startPbIndex; i<=endIndex; i++)
		{
			TeiElementWrapper currentTeiElementWrapper = flatTeiElementList.get(i);
			
			if(currentTeiElementWrapper.equals(startTeiElementWrapper))
			{
				System.out.println("Found start element");
				started=true;
				
			}

			else if(ElementType.PB.equals(currentTeiElementWrapper.getTeiElement().getElementType()))
			{
				list.add(new SelectItem(currentTeiElementWrapper.getTeiElement().getId(), currentTeiElementWrapper.getPage().getOrder() + 1 + " / " + currentTeiElementWrapper.getPage().getOrderLabel()));
			}
			
			else if (started && PositionType.START.equals(currentTeiElementWrapper.getPositionType()))
			{
				TeiElementWrapper nextPagebreakWrapper = currentTeiElementWrapper.getPartnerElement().getPagebreakWrapper();
				if(flatTeiElementList.indexOf(nextPagebreakWrapper) > i)
				{
					i = flatTeiElementList.indexOf(currentTeiElementWrapper.getPartnerElement().getPagebreakWrapper());
				}
				
				
			
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
		System.out.println("Move " + startElementWrapper.getTeiElement().getType() + " to "  + pageBreakToMoveToId);
		
		TeiElementWrapper endElement = startElementWrapper.getPartnerElement();
		TeiElementWrapper pageBreakToMoveTo = null;;
		
		for(TeiElementWrapper teiElementWrapper : flatTeiElementList)
		{
			String id = teiElementWrapper.getTeiElement().getId();
			try {
					if(id!=null && pageBreakToMoveToId.equals(id))
					{
						
							pageBreakToMoveTo = teiElementWrapper;
						
					}
				} catch (NullPointerException e) {
					System.out.println("NPE for " + teiElementWrapper.getTeiElement());
				}
			
		}
		
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
				System.out.println("Found start element");
				started=true;
				balanceCounter--;
			}
			
			if(currentTeiElementWrapper.equals(pageBreakToMoveTo))
			{
				System.out.println("Found pagebreak to move to: " + currentTeiElementWrapper.getTeiElement().getId());
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
	
	private void updateFlatList()
	{
		this.flatTeiElementList = teiSdToFlatTeiElementList(volume.getTeiSd(), volume);
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
				
		
		for (int i=nextPbIndex; i>=0; i--)
		{
			TeiElementWrapper currentElementWrapper = flatTeiElementList.get(i); 
			if(PositionType.START.equals(currentElementWrapper.getPositionType()))
			{
				selectedStructuralElement = currentElementWrapper;
				break;
			}
		}
		System.out.println("Selected Pb: " + selectedPb.getTeiElement().getId());
		System.out.println("Selected Structural: " + selectedStructuralElement.getTeiElement().getElementType());
		
		
	}
	
	public void selectStructuralElement(TeiElementWrapper elementWrapper)
	{
		setSelectedStructuralElement(elementWrapper);
		if(selectedStructuralElement.getPagebreakWrapper()!= selectedPb)
		{
			setSelectedPb(selectedStructuralElement.getPagebreakWrapper());
		}

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

	public ElementType getSelectedStructuralType() {
		return selectedStructuralType;
	}

	public void setSelectedStructuralType(ElementType selectedStructuralType) {
		this.selectedStructuralType = selectedStructuralType;
	}

	public TeiElementWrapper getCurrentEditElement() {
		return currentEditElement;
	}

	public void setCurrentEditElement(TeiElementWrapper currentEditElement) {
		this.currentEditElement = currentEditElement;
	}

	public List<TreeWrapperNode> getTreeWrapperNodes() {
		return treeWrapperNodes;
	}

	public void setTreeWrapperNodes(List<TreeWrapperNode> treeWrapperNodes) {
		this.treeWrapperNodes = treeWrapperNodes;
	}

	public TeiElementWrapper getSelectedStructuralElement() {
		return selectedStructuralElement;
	}

	public void setSelectedStructuralElement(TeiElementWrapper selectedStructuralElement) {
		this.selectedStructuralElement = selectedStructuralElement;
	}
	
	public static boolean isFrontBodyBack(TeiElementWrapper wrapper)
	{
		return ElementType.BODY.equals(wrapper.getTeiElement().getElementType()) ||
				   ElementType.FRONT.equals(wrapper.getTeiElement().getElementType()) ||
				   ElementType.BACK.equals(wrapper.getTeiElement().getElementType());
	}
	
	


}
