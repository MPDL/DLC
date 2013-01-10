package de.mpg.mpdl.dlc.search;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;


import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;

public class CodicologicalSearchCriterion extends SearchCriterion{
	
	private static Logger logger = Logger.getLogger(CodicologicalSearchCriterion.class);
	
	private String superElementName;
	
	private String elementName;
	
	private Type type;
	
	private List<SelectItem> enumSelectItems = new ArrayList<SelectItem>();
	
	private List<SelectItem> elementSelectItems = new ArrayList<SelectItem>();
	
	private final static String DEFAULT_CDC_INDEX_PREFIX = "dlc.cdc";
	
	public static Map<String, CodicologicalSearchCriterion> objectCdcMap;
	public static Map<String, CodicologicalSearchCriterion> bodyOfVolumeCdcMap;
	public static Map<String, CodicologicalSearchCriterion> provenanceCdcMap;
	public static Map<String, CodicologicalSearchCriterion> bindingCdcMap;
	
	
	
	
	static
	{
		try {
			objectCdcMap = CodicologicalSearchCriterion.getMap("object");
			bodyOfVolumeCdcMap = CodicologicalSearchCriterion.getMap("bodyOfVolume");
			provenanceCdcMap = CodicologicalSearchCriterion.getMap("provenance");
			bindingCdcMap = CodicologicalSearchCriterion.getMap("binding");
		} catch (Exception e) {
			logger.error("Could not parse codicological schema", e);
		}
	}
	
	
	public CodicologicalSearchCriterion(CodicologicalSearchCriterion toBeCloned)
	{
		super(toBeCloned);
		cloneValues(toBeCloned);
		
	}
	
	@Override
	public CodicologicalSearchCriterion clone()
	{
		return new CodicologicalSearchCriterion(this);
	}
	
	
	public CodicologicalSearchCriterion(boolean dummy)
	{
		if(!dummy)
		{
			this.superElementName = "object";
			superElementNameChanged();
		}
		
	}
	
	
	
	
	
	@Override
	public String[] getSearchIndexes()
	{
		return new String[]{DEFAULT_CDC_INDEX_PREFIX + "." + elementName};
	}
	
	private enum Type
	{
		ENUM, BOOLEAN, TEXT;
	}
	
	public void cloneValues(CodicologicalSearchCriterion toBeCloned)
	{
		this.superElementName = toBeCloned.getSuperElementName();
		this.elementName = toBeCloned.elementName;
		this.type = toBeCloned.type;
		this.enumSelectItems = new ArrayList<SelectItem>();
		
		for(SelectItem selItem : toBeCloned.enumSelectItems)
		{
			if (selItem.getValue()==null || selItem.getValue().toString().trim().isEmpty())
			{
				enumSelectItems.add(new SelectItem("", InternationalizationHelper.getLabel("cdc_all")));
			}
			else
			{
				String label = InternationalizationHelper.getLabel("cdc_" + selItem.getValue().toString().replaceAll("\\s", "_"));
				enumSelectItems.add(new SelectItem(selItem.getValue(), label));
			}	
		}
		
		if(this.type.equals(toBeCloned.type))
		{
			this.value = toBeCloned.value;
		}
		updateSelectItemsForSuperElement();
	}
	
	public void superElementNameChanged()
	{
		if (superElementName.equals("object") && objectCdcMap!=null && objectCdcMap.size()>0)
		{
			cloneValues(objectCdcMap.entrySet().iterator().next().getValue());
		}
		else if (superElementName.equals("bodyOfVolume") && bodyOfVolumeCdcMap!=null && bodyOfVolumeCdcMap.size()>0)
		{
			cloneValues(bodyOfVolumeCdcMap.entrySet().iterator().next().getValue());
		}
		else if (superElementName.equals("provenance") && provenanceCdcMap!=null && provenanceCdcMap.size()>0)
		{
			cloneValues(provenanceCdcMap.entrySet().iterator().next().getValue());
		}
		else if (superElementName.equals("binding") && bindingCdcMap!=null && bindingCdcMap.size()>0)
		{
			cloneValues(bindingCdcMap.entrySet().iterator().next().getValue());
		}
		
		
	}
	
	public void elementNameChanged()
	{
		if (superElementName.equals("object"))
		{
			CodicologicalSearchCriterion dummy = objectCdcMap.get(elementName);
			cloneValues(dummy);
			//cloneValues(objectCdcMap.entrySet().iterator().next().getValue());
		}
		else if (superElementName.equals("bodyOfVolume"))
		{
			CodicologicalSearchCriterion dummy = bodyOfVolumeCdcMap.get(elementName);
			cloneValues(dummy);
		}
		else if (superElementName.equals("provenance"))
		{
			CodicologicalSearchCriterion dummy = provenanceCdcMap.get(elementName);
			cloneValues(dummy);
		}
		else if (superElementName.equals("binding"))
		{
			CodicologicalSearchCriterion dummy = bindingCdcMap.get(elementName);
			cloneValues(dummy);
		}
		
	}
	
	
	
	public void updateSelectItemsForSuperElement()
	{
		elementSelectItems.clear();
		
		if (superElementName.equals("object"))
		{
			for(Entry<String,CodicologicalSearchCriterion> csc : objectCdcMap.entrySet())
			{
				elementSelectItems.add(new SelectItem(csc.getValue().getElementName(), InternationalizationHelper.getLabel("cdc_" + csc.getValue().getElementName())));
			}
		}
		else if (superElementName.equals("bodyOfVolume"))
		{
			for(Entry<String,CodicologicalSearchCriterion> csc : bodyOfVolumeCdcMap.entrySet())
			{
				elementSelectItems.add(new SelectItem(csc.getValue().getElementName(), InternationalizationHelper.getLabel("cdc_" + csc.getValue().getElementName())));
			}
		}
		else if (superElementName.equals("provenance"))
		{
			for(Entry<String,CodicologicalSearchCriterion> csc : provenanceCdcMap.entrySet())
			{
				elementSelectItems.add(new SelectItem(csc.getValue().getElementName(), InternationalizationHelper.getLabel("cdc_" + csc.getValue().getElementName())));
			}
		}
		else if (superElementName.equals("binding"))
		{
			for(Entry<String,CodicologicalSearchCriterion> csc : bindingCdcMap.entrySet())
			{
				elementSelectItems.add(new SelectItem(csc.getValue().getElementName(), InternationalizationHelper.getLabel("cdc_" + csc.getValue().getElementName())));
			}
		}
	}
	
	
	
	

	public String getSuperElementName() {
		return superElementName;
	}

	public void setSuperElementName(String superElementName) {
		this.superElementName = superElementName;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<SelectItem> getEnumSelectItems() {
		return enumSelectItems;
	}

	public void setEnumSelectItems(List<SelectItem> enumSelectItems) {
		this.enumSelectItems = enumSelectItems;
	}


	public static Map<String, CodicologicalSearchCriterion> getMap(String superElement) throws Exception
	{
		Map<String, CodicologicalSearchCriterion> cdcList = new LinkedHashMap<String, CodicologicalSearchCriterion>();
		
		URL schemaUrl = CodicologicalSearchCriterion.class.getClassLoader().getResource("schemas/DLC-CDC.rng");
		
		Processor proc = new Processor(false);
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("rng", "http://relaxng.org/ns/structure/1.0");
        
        //Check if pagebreak is directly followed by an structural element like div, front, body...
        XPathExecutable xx = xpath.compile("/rng:grammar//rng:element[@name='" + superElement + "']//*[self::rng:element]");
        XPathSelector selector = xx.load();
        XdmNode xdmDoc = db.build(new StreamSource(schemaUrl.openStream()));
        selector.setContextItem(xdmDoc);
        if(selector.iterator().hasNext())
        {
        	for(XdmItem item : selector) 
        	{
               
        		if(!item.isAtomicValue())
        		{

	        		XdmNode elementNode = (XdmNode) item;
	        		String name = (elementNode.getAttributeValue(new QName("name")));
	        		CodicologicalSearchCriterion cse = new CodicologicalSearchCriterion(true);
	        		cdcList.put(name, cse);
	        		cse.setSuperElementName(superElement);
	        		cse.setElementName(name);

	        		parseElement(elementNode, cse, cdcList);

        		}
        	}
	     }
        
        return cdcList;
	}
       
        	
        	
        private static void parseElement(XdmNode item, CodicologicalSearchCriterion cse, Map<String, CodicologicalSearchCriterion> cdcList)
        {
        	
        	for(Iterator<XdmItem> iter = item.axisIterator(Axis.CHILD); iter.hasNext();)
	    	{
        		XdmItem childItem = iter.next();
	        	if(!childItem.isAtomicValue())
				{
	    			XdmNode childNode = (XdmNode) childItem;
	    			
	    			if(childNode.getNodeKind().equals(XdmNodeKind.ELEMENT))
	    			{
		    			if(childNode.getNodeName().getLocalName().equals("choice"))
		    			{
		    				cse.setType(Type.ENUM);
		    				for(Iterator<XdmItem> iterChoice = childNode.axisIterator(Axis.CHILD); iterChoice.hasNext();)
		    				{
		    					XdmItem choiceItem = iterChoice.next();
		    					if(!choiceItem.isAtomicValue())
		    					{
		        					XdmNode choiceNode = (XdmNode) choiceItem;
		        					
		        					if(choiceNode.getNodeKind().equals(XdmNodeKind.ELEMENT) && choiceNode.getNodeName().getLocalName().equals("value"))
		        					{
		        						
		        						if(choiceNode.getStringValue() == null  || choiceNode.getStringValue().trim().isEmpty())
		        						{
		        							cse.getEnumSelectItems().add(new SelectItem("", InternationalizationHelper.getLabel("cdc_all")));
		        							cse.setValue("");
		        						}
		        						else
		        						{
		        							String label = InternationalizationHelper.getLabel("cdc_" + choiceNode.getStringValue().replaceAll("\\s", "_"));
		        							cse.getEnumSelectItems().add(new SelectItem(choiceNode.getStringValue(), label));
		        						}
		        					}
		    					}
		    					
		    				}
		    			}
						
					
		    			else if(childNode.getNodeName().getLocalName().equals("data"))
		    			{
		    				String dataType = childNode.getAttributeValue(new QName("type"));
		    				if(dataType.equals("token"))
		    				{
		    					cse.setType(Type.TEXT);
		    				}
		    				else if (dataType.equals("boolean"))
		    				{
		    					cse.setType(Type.BOOLEAN);
		    					cse.setValue("false");
		    				}
		    			}
		    			else if(childNode.getNodeName().getLocalName().equals("optional"))
		    			{
		    				for(Iterator<XdmItem> iterAttr = childNode.axisIterator(Axis.CHILD); iterAttr.hasNext();)
		    				{
		    					XdmItem attributeItem = iterAttr.next();
		    					
		    					if(!attributeItem.isAtomicValue())
		    					{
		    						XdmNode attributeNode = (XdmNode) attributeItem;
		    						if(attributeNode.getNodeKind().equals(XdmNodeKind.ELEMENT) && attributeNode.getNodeName().getLocalName().equals("attribute"))
		    						{
		    							CodicologicalSearchCriterion cseAttr = new CodicologicalSearchCriterion(true);
		    							String name = cse.getElementName() + "." + attributeNode.getAttributeValue(new QName("name"));
		    							cseAttr.setSuperElementName(cse.getSuperElementName());
		    							cseAttr.setElementName(name);
		    							cdcList.put(name, cseAttr);
		    							parseElement(attributeNode, cseAttr, cdcList);
		    						}
		    					}
		    					
		    				}
		    			}
	    			}
	    			
				}
			
	        }
        

        }

		public List<SelectItem> getElementSelectItems() {
			return elementSelectItems;
		}

		public void setElementSelectItems(List<SelectItem> elementSelectItems) {
			this.elementSelectItems = elementSelectItems;
		}
	
	
	
        
	
	

}