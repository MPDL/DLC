package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;




public class TeiNode  {

	
	public enum Type
	{
		START, END, EMPTY;
	}
	
	
	
	private PbOrDiv pbOrDiv;
	
	

	private Type type;
	
	private int level;
	
	private PagebreakWrapperBean parentPb;
	
	private TeiNode partnerNode;
	
	
	
	public TeiNode(PbOrDiv pbOrDiv, int level, Type type)
	{
		this.pbOrDiv = pbOrDiv;
		this.level = level;
		this.type = type;
	}
	
	
	public static List<TeiNode> teiSdToTreeNodes(TeiSd teiSd)
	{
		List<TeiNode> list = new ArrayList<TeiNode>();
		createTeiTree(teiSd.getPbOrDiv(), 0, list);
		return list;
	}
	
	private static void createTeiTree(List<PbOrDiv> divList, int level, List<TeiNode> list)
	{
		
		for(PbOrDiv pbOrDiv : divList)
		{
			TeiNode start;
			if(pbOrDiv instanceof Pagebreak)
			{
				start = new TeiNode(pbOrDiv, level, Type.EMPTY);
				list.add(start);
			}
			else
			{
				start = new TeiNode(pbOrDiv, level, Type.START);
				list.add(start);
			}
			
			createTeiTree(pbOrDiv.getPbOrDiv(), level + 1, list);
			if(!(pbOrDiv instanceof Pagebreak))
			{
				TeiNode end = new TeiNode(pbOrDiv, level, Type.END);
				end.setPartnerNode(start);
				start.setPartnerNode(end);
				list.add(end);
				
			}
		}
		
		
	}
	
	
	public PbOrDiv getPbOrDiv() {
		return pbOrDiv;
	}


	public void setPbOrDiv(PbOrDiv pbOrDiv) {
		this.pbOrDiv = pbOrDiv;
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public PagebreakWrapperBean getParentPb() {
		return parentPb;
	}


	public void setParentPb(PagebreakWrapperBean parentPb) {
		this.parentPb = parentPb;
	}


	public TeiNode getPartnerNode() {
		return partnerNode;
	}


	public void setPartnerNode(TeiNode partnerNode) {
		this.partnerNode = partnerNode;
	}
	
	
	
}
