package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

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
	
	
}
