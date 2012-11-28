package de.mpg.mpdl.dlc.vo.teisd;

public class PersName {
	
	private String numeration;
	
	private String author;
	
	private String invertedAuthor;
	
	private String key;

	
	public PersName()
	{
		
	}
	
	public PersName(PersName original)
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
	
	public boolean isEmpty()
	{
		return ((numeration==null || numeration.trim().isEmpty()) &&
				(author==null || author.trim().isEmpty()) &&
				(invertedAuthor==null || invertedAuthor.trim().isEmpty()) &&
				(key==null || key.trim().isEmpty()));
		
	}
	
	public boolean isNotEmpty()
	{
		return !isEmpty();
		
	}

}
