package de.mpg.mpdl.dlc.vo.teisd;

public class DocAuthor {
	
	private String numeration;
	
	private String author;
	
	private String invertedAuthor;
	
	private String key;

	
	public DocAuthor()
	{
		
	}
	
	public DocAuthor(DocAuthor original)
	{
		this.numeration = original.getNumeration();
		this.author = original.getAuthor();
		this.invertedAuthor = original.getInvertedAuthor();
		this.key = original.getKey();
	}
	
	public String getNumeration() {
		return numeration;
	}

	public void setNumeration(String numeration) {
		this.numeration = numeration;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getInvertedAuthor() {
		return invertedAuthor;
	}

	public void setInvertedAuthor(String invertedAuthor) {
		this.invertedAuthor = invertedAuthor;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
