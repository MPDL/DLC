package de.mpg.mpdl.dlc.vo.teisd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Figure extends Div {
	
	
	private String figDesc;
	
	private String caption;
	
	
	private List<PersName> persNames = new ArrayList<PersName>();
	

	public Figure() {
		super(ElementType.FIGURE);
	}

	public Figure(Figure original)
	{
		super(original);
		this.setElementType(ElementType.FIGURE);
		this.setFigDesc(original.getFigDesc());
		List<String> head = new ArrayList<String>();
		head.addAll(original.getHead());
		this.setHead(head);
		
		List<PersName> persNameList = new ArrayList<PersName>();
		
		for(PersName originalPersName : original.getPersNames())
		{
			persNameList.add(new PersName(originalPersName));
		}
		this.setPersNames(persNameList);
		
		this.setCaption(original.getCaption());
	}
	
	public String getFigDesc() {
		return figDesc;
	}

	public void setFigDesc(String figureDesc) {
		this.figDesc = figureDesc;
	}

	public List<PersName> getPersNames() {
		return persNames;
	}

	public void setPersNames(List<PersName> persNames) {
		this.persNames = persNames;
	}

	public static List<PersName> persNameListFactory()
	{
		return new ArrayList<PersName>();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
	public boolean isNotEmptyFigDesc()
	{
		
		
		return (!(figDesc==null || figDesc.trim().isEmpty())) || isNotEmptyPersNames();
	}
	
	public boolean isNotEmptyPersNames()
	{
		boolean notEmptyPersNames = false;
		if(persNames!=null)
		{
			for(PersName persName : persNames)
			{
				if(!persName.isEmpty())
				{
					notEmptyPersNames = true;
				}
			}
		}
		
		return notEmptyPersNames;
	}

	

}
