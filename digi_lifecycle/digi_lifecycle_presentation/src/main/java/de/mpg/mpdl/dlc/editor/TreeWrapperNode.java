package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class TreeWrapperNode {

	
	private TeiElementWrapper teiElementWrapper;
	
	private List<TreeWrapperNode> children = new ArrayList<TreeWrapperNode>();
	
	private TreeWrapperNode parent;
	
	
	
	private boolean editable;
	

	public TeiElementWrapper getTeiElementWrapper() {
		return teiElementWrapper;
	}

	public void setTeiElementWrapper(TeiElementWrapper teiElementWrapper) {
		this.teiElementWrapper = teiElementWrapper;
	}

	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public List<TreeWrapperNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeWrapperNode> children) {
		this.children = children;
	}

	public TreeWrapperNode getParent() {
		return parent;
	}

	public void setParent(TreeWrapperNode parent) {
		this.parent = parent;
	}
	
	public boolean getMovableToLeft()
	{
		//return parent.getChildren().indexOf(this) == 0 &&
		if(parent!=null)
		{
			return  !ElementType.BODY.equals(parent.getTeiElementWrapper().getTeiElement().getElementType()) &&
				   !ElementType.FRONT.equals(parent.getTeiElementWrapper().getTeiElement().getElementType()) &&
				   !ElementType.BACK.equals(parent.getTeiElementWrapper().getTeiElement().getElementType());
		}
		else
		{
			return false;
		}
	}
	
	public boolean getMovableToRight()
	{
		if(parent!=null)
		{
			return parent.getChildren().indexOf(this) > 0;
		}
		else
		{
			return false;
		}
	}
	
	public boolean getMovableUp()
	{
		//Only allow if this element has a sibling before itself and the same start and endpage
		if(parent!=null)
		{
			int indexInParent = parent.getChildren().indexOf(this);
			if(indexInParent -1  >= 0)
			{
				TeiElementWrapper siblingBefore = parent.getChildren().get(indexInParent -1).getTeiElementWrapper();
				return siblingBefore.getPagebreakWrapper().equals(siblingBefore.getPartnerElement().getPagebreakWrapper()) &&
						siblingBefore.getPartnerElement().getPagebreakWrapper().equals(this.getTeiElementWrapper().getPagebreakWrapper());
				
			}
			
			
		}
		return false;
		
	}
	
	public boolean getMovableDown()
	{
		
		
		if(this.getTeiElementWrapper().getPagebreakWrapper().equals(this.getTeiElementWrapper().getPartnerElement().getPagebreakWrapper()))
		{
			
			int indexInParent = parent.getChildren().indexOf(this);
			if(indexInParent + 1 < parent.getChildren().size())
			{
				TeiElementWrapper siblingAfter = parent.getChildren().get(indexInParent + 1).getTeiElementWrapper();
				return siblingAfter.getPagebreakWrapper().equals(this.getTeiElementWrapper().getPartnerElement().getPagebreakWrapper());
						
			}
			
		}

		return false;
		
	}
	
	public boolean getInvalid()
	{
		ElementType elType = this.getTeiElementWrapper().getTeiElement().getElementType();
		ElementType parentElType = null;
		if(this.getParent()!=null)
		{
			parentElType = this.getParent().getTeiElementWrapper().getTeiElement().getElementType();
		}
		
		return (
				
				(ElementType.TITLE_PAGE.equals(elType) && (ElementType.FIGURE.equals(parentElType) || ElementType.DIV.equals(parentElType)) || 
				
				(ElementType.DIV.equals(elType) && (ElementType.FIGURE.equals(parentElType) || ElementType.TITLE_PAGE.equals(parentElType)))));
	}
	
	public static boolean hasChildrenWithDifferentStartpage(TreeWrapperNode nodeToCompare, TreeWrapperNode root)
	{
		if(!nodeToCompare.getTeiElementWrapper().getPagebreakWrapper().equals(root.getTeiElementWrapper().getPagebreakWrapper()))
		{
			return false;
		}
		else
		{
			if(root.getChildren()!=null)
			{
				for(TreeWrapperNode child: root.getChildren())
				{

					return hasChildrenWithDifferentStartpage(nodeToCompare, child);

				}
			}
		}
		
		
		return true;
	}
	
	

	
	
	
			
	
	
	
	
	
}
