/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import org.apache.log4j.Logger;
import org.richfaces.model.TreeNodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;

public class TeiSdTreeNodeImpl {
	
	private Logger logger = Logger.getLogger(TeiSdTreeNodeImpl.class);
	
	private static String[] doRender = new String[]{"figure", "div", "titlePage", "group", "text", "front", "body", "back"};
	
	private static List<String> doRenderList = Arrays.asList(doRender);
	private Element element;
	
	private String id;
	
	private boolean render = false;
	
	public List<TeiSdTreeNodeImpl> children;

	private Page startPage;
	private Page endPage;
	
	public TeiSdTreeNodeImpl(Element el, Volume vol, VolumeServiceBean volServiceBean)
	{
		
		this.setElement(el);
		this.setId(el.getAttributeNS("http://www.w3.org/XML/1998/namespace", "id"));
		
		if(doRenderList.contains(el.getLocalName()))
		{
			render=true;
			
			if(this.id!=null && !this.id.isEmpty())
			{
				try {
					this.setStartPage(volServiceBean.getPageForDiv(vol, this.getId()));
				} catch (Exception e) {
					startPage = vol.getPages().get(0);
				}
				try {
					this.setEndPage(volServiceBean.getEndPageForDiv(vol, this.getId()));
				} catch (Exception e) {
					setEndPage(vol.getPages().get(0));
				}
			}
		}
		
		
		
		
		
		
		children = new ArrayList<TeiSdTreeNodeImpl>();
		
		for(Node n = getElement().getFirstChild(); n!=null; n=n.getNextSibling())
		{
			if (n.getNodeType()==Node.ELEMENT_NODE)
			{
				children.add(new TeiSdTreeNodeImpl((Element) n, vol, volServiceBean));
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
		for(Node n = getElement().getFirstChild(); n!=null; n=n.getNextSibling())
		{
			if(n.getNodeType()==Node.ELEMENT_NODE)
			{
				if(n.getLocalName().equals("docAuthor"))
				{
					docAuthorList.add((Element)n);
				}
				else if (n.getLocalName().equals("byline"))
				{
					NodeList nl = element.getElementsByTagNameNS("http://www.tei-c.org/ns/1.0", "docAuthor");
					for(int i=0; i<nl.getLength();i++)
					{
						docAuthorList.add((Element)nl.item(i));
					}
				}
			}

		}

		return docAuthorList;
		
	}

	public boolean isRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public Page getStartPage() {
		return startPage;
	}

	public void setStartPage(Page startPage) {
		this.startPage = startPage;
	}

	public Page getEndPage() {
		return endPage;
	}

	public void setEndPage(Page endPage) {
		this.endPage = endPage;
	}
	
	
	

}
