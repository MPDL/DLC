package de.mpg.mpdl.dlc.vo.teisd;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class DocTitle 
{

	
	private String type;
	
	private String title;
	
	public DocTitle()
	{
		
	}
	
	public DocTitle(DocTitle original)
	{
		this.setTitle(original.getTitle());
		this.setType(original.getType());
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	
}
