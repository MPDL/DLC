package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import org.richfaces.model.TreeNodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TeiSdTreeNodeImpl {
	
	
	private static String[] doRender = new String[]{"figure", "div", "titlePage", "group", "text", "front", "body", "back"};
	
	private static List<String> doRenderList = Arrays.asList(doRender);
	private Element element;
	
	private String id;
	
	private boolean render = false;
	
	public List<TeiSdTreeNodeImpl> children;
	
	public TeiSdTreeNodeImpl(Element el)
	{
		if(doRenderList.contains(el.getLocalName()))
		{
			render=true;
		}
		this.setElement(el);
		this.setId(el.getAttributeNS("http://www.w3.org/XML/1998/namespace", "id"));
		
		children = new ArrayList<TeiSdTreeNodeImpl>();
		
		for(Node n = getElement().getFirstChild(); n!=null; n=n.getNextSibling())
		{
			if (n.getNodeType()==Node.ELEMENT_NODE)
			{
				children.add(new TeiSdTreeNodeImpl((Element) n));
			}
		}
		
	}
	
	public List<TeiSdTreeNodeImpl> getChildren()
	{
		return children;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getHead()
	{
		StringBuffer sb = new StringBuffer();
		
		int i=0;
		for(Node n = getElement().getFirstChild(); n!=null; n=n.getNextSibling())
		{
			if(n.getNodeType()==Node.ELEMENT_NODE && n.getLocalName().equals("head"))
			{
				if(n.getTextContent()!=null && !n.getTextContent().trim().isEmpty())
				{
					sb.append(n.getTextContent());
					sb.append(" ");
				}
				
			}
		}
		
		
		return sb.toString();
		
	}
	
	public String getDocTitle()
	{
		StringBuffer sb = new StringBuffer();
		//
		//NodeList nl = element.getElementsByTagNameNS("http://www.tei-c.org/ns/1.0", "docTitle");
		for(Node n = getElement().getFirstChild(); n!=null; n=n.getNextSibling())
		{
			if(n.getNodeType()==Node.ELEMENT_NODE && n.getLocalName().equals("docTitle"))
			{
				if(n.getTextContent()!=null && !n.getTextContent().trim().isEmpty())
				{
					sb.append(n.getTextContent());
					sb.append(" ");
				}
			}
		}
		return sb.toString();
		
	}
	
	public List<Element> getDocAuthors()
	{
		List<Element> docAuthorList = new ArrayList<Element>();
		NodeList nl = element.getElementsByTagNameNS("http://www.tei-c.org/ns/1.0", "docAuthor");
		for(int i=0; i<nl.getLength();i++)
		{
			docAuthorList.add((Element)nl.item(i));
		}
		return docAuthorList;
		
	}

	public boolean isRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}
	
	
	

}
