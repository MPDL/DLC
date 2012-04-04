package de.mpg.mpdl.dlc.editor;

public class ValidationWrapper {

	

	
	private TreeWrapperNode affectedNode;
	
	public ValidationWrapper(TreeWrapperNode affectedNode) {
		this.setAffectedNode(affectedNode);
	}

	public TreeWrapperNode getAffectedNode() {
		return affectedNode;
	}

	public void setAffectedNode(TreeWrapperNode affectedNode) {
		this.affectedNode = affectedNode;
	}
	
}
