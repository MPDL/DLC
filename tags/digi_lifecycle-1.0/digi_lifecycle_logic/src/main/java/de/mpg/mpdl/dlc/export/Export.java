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
package de.mpg.mpdl.dlc.export;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;
import org.jibx.runtime.IUnmarshallingContext;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;

import de.escidoc.core.common.exceptions.remote.application.notfound.ResourceNotFoundException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.component.Component;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsLocationSEC;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsPart;
import de.mpg.mpdl.dlc.vo.mods.ModsPublisher;
import de.mpg.mpdl.dlc.vo.mods.ModsRelatedItem;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.DocAuthor;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;
import de.mpg.mpdl.dlc.vo.teisd.TitlePage;



public class Export {

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	private static Logger logger = Logger.getLogger(Export.class);
	public ExportTypes exportType;
	public String level = "";
	
	public enum ExportTypes
	{
		PDF, MODS, TEI;
	}
	
	public Export()
	{
		
	}
	
	/**
	 * Returns the tei of an item. If a original tei exists it will be returned, otherwise the dlc created tei will be returned.
	 * @param itemId
	 * @return byte array of the tei of this item
	 * @throws Exception
	 */
	public byte[] teiExport(String itemId) throws Exception
	{	        
		String xml = null;
		Volume vol = volServiceBean.loadCompleteVolume(itemId, null);
		xml = vol.getTei();
		
		// If no original tei exists, load tei_sd
		if (xml == null)
		{
			for (int i = 0; i< vol.getItem().getComponents().size(); i++)
				{
					Component comp = vol.getItem().getComponents().get(i);
					if (comp.getProperties().getContentCategory().equals("tei-sd"))
					{
						xml = volServiceBean.documentToString(vol.getTeiSdXml());
					}
				}
		}
		
	    return xml.getBytes("UTF-8");
	}
	
	/**
	 * Export item in mets/mods format, using an xslt transformation
	 * @param itemId
	 * @return byte array of the item in mets/mods format (xml)
	 * @throws Exception
	 */
	public byte[] metsModsExport(String itemId, boolean oai) throws Exception, ResourceNotFoundException
	{
		String xsltUri = "";
		if (oai)
		{
			xsltUri ="export/dlczvddmets_oai.xsl";
		}
		else
		{
			xsltUri ="export/dlczvddmets.xsl";
		}
        String itemXml;
		try {
			itemXml = this.getEscidocItem(itemId);
		} catch (URISyntaxException e1) {
			throw new RuntimeException("Item could not be retrieved from coreservice for export.");
		}
		
		Volume vol = volServiceBean.loadCompleteVolume(itemId, null);
        String teiUrl = this.getTeiSdUrl(vol);
        String metsUrl = this.getMetsUrl(vol);
        
        TransformerFactory factory = new TransformerFactoryImpl();
        StringWriter writer = new StringWriter();
        
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = cl.getResourceAsStream(xsltUri);
            Transformer transformer = factory.newTransformer(new StreamSource(in));

            transformer.setParameter("itemId", itemId);  
            transformer.setParameter("teiUrl",PropertyReader.getProperty("escidoc.common.framework.url")+teiUrl); 
            transformer.setParameter("metsUrl",PropertyReader.getProperty("escidoc.common.framework.url")+metsUrl);
            transformer.setParameter("imageUrl", PropertyReader.getProperty("image-upload.url.download")); 
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //For oai export
            transformer.setParameter("itemDate", vol.getItem().getProperties().getCreationDate()); 
            transformer.setParameter("itemCollection", "context_"+vol.getItem().getProperties().getContext().getObjid().replace(":", "_"));  
            
            
            StringReader xmlSource = (new StringReader(itemXml));
            transformer.transform(new StreamSource(xmlSource), new StreamResult(writer));
        }
        catch (TransformerException e)
        {
            logger.error("An error occurred during the export format transformation.", e);
            throw new RuntimeException("An error occurred during the export format transformation.", e);
        }
        
        return writer.toString().getBytes("UTF-8");
	}
	
	
	/**
	 * convert a volume in pdf format for export.
	 * Additionally a metadata page (title, collection name, provenance info) will be created.
	 * @param volume
	 * @return pdf
	 * @throws Exception 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public byte[] pdfExport(String itemId) throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Volume vol = volServiceBean.loadCompleteVolume(itemId, null);
		Document document = new Document();
		String imageurl = "";
		PdfWriter writer = PdfWriter.getInstance(document, out);
		
		document.open();
		
		PdfContentByte cb = writer.getDirectContent();
		PdfOutline root = cb.getRootOutline();
		writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
		//writer.setViewerPreferences(PdfWriter.PageLayoutTwoPageRight);
		document.setPageSize(PageSize.A4);
		document.addCreator("DLC - Digital Libraries Connected");
		
		try 
		{		
			//write organizational logo to pdf
			document.newPage();
			document = this.generateBiblMd(vol, document, root);
		
			if (vol.getTeiSdXml() != null)
			{
				
				
				IUnmarshallingContext uctx = VolumeServiceBean.bfactTei.createUnmarshallingContext();
				String teiSdXmlString = volServiceBean.documentToString(vol.getTeiSdXml());
				TeiSd teiSd = (TeiSd) uctx.unmarshalDocument(new StringReader(teiSdXmlString), null);
				
				//write structure to pdf
				document.newPage();
				//Set toc link for outline
				document.add(new Chunk("Table of contents").setLocalDestination("toc"));
				document.add(Chunk.NEWLINE);
				document.add(Chunk.NEWLINE);
				PdfOutline tocout = new PdfOutline(root, PdfAction.gotoLocalPage("toc", false), "Table of contents");
			
				for (int i = 0; i< teiSd.getText().getPbOrDiv().size(); i++)			
				{
					PbOrDiv currentElem = teiSd.getText().getPbOrDiv().get(i);
					if (currentElem.getType()!= "PB")
					{
						if (currentElem.getElementType() == ElementType.DIV || currentElem.getElementType() == ElementType.TITLE_PAGE)
						{
								document = this.createTree(currentElem.getPbOrDiv(), document, root, writer);
						}
						else 
							if (currentElem.getElementType() == ElementType.FRONT || currentElem.getElementType() == ElementType.BACK
									|| currentElem.getElementType() == ElementType.BODY || currentElem.getElementType() == ElementType.GROUP
									|| currentElem.getElementType() == ElementType.TEXT )
							{
								document.add(new Paragraph(currentElem.getElementType().name().toString()));
								level += "  ";
								document = this.createTree(currentElem.getPbOrDiv(), document, root, writer);
							}
					}
				}	
			}
			//write images to pdf
			for (int i = 0; i< vol.getPages().size(); i++)
			{	
				Page page = vol.getPages().get(i);
				Image image;
				
				imageurl = PropertyReader.getProperty("image-upload.url.download") + "web/" + page.getContentIds();
				image = Image.getInstance (imageurl);
	
				//Set image link for outline
				document.add(new Chunk("   ").setLocalDestination(page.getContentIds()));
				
				document.newPage();
					
				image.setAlignment(Image.MIDDLE);
				image.scaleAbsolute(PageSize.A4.getRight(), PageSize.A4.getHeight());
				image.setAbsolutePosition(0, 0);
				document.add(image);
			}
		}
		catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("An error occurred during the pdf creation for item " + itemId + ": " + e.getMessage());
			}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Image file could not be fetched from url: " + imageurl);
			}
		
		document.close();						
		return out.toByteArray();
	}


	/**
	 * Generates the bibliografic metadata for pdf export
	 * Please note: as tei md is so generic I have to catch every possible error, this results in many many if(...) phrases
	 * @param doc
	 * @return
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private Document generateBiblMd (Volume vol, Document document, PdfOutline root) throws DocumentException
	{	
		ModsMetadata md = vol.getModsMetadata();
		OrganizationalUnitServiceBean orgServiceBean = new OrganizationalUnitServiceBean();
		ContextServiceBean contextServiceBean = new ContextServiceBean();
		Context context = contextServiceBean.retrieveContext(vol.getItem().getProperties().getContext().getObjid(), null);
		Organization volumeOu = orgServiceBean.retrieveOrganization(context.getProperties().getOrganizationalUnitRefs().getFirst().getObjid());		

		Paragraph plain = new Paragraph(" ");
		Paragraph para = new Paragraph();
		Phrase phrase1 = new Phrase();
		Phrase phrase2 = new Phrase();
		
		//Set md link for outline
//		document.add(new Chunk("Title information").setLocalDestination("md"));
//		document.add(Chunk.NEWLINE);
//		document.add(Chunk.NEWLINE);
//		PdfOutline tocout = new PdfOutline(root, PdfAction.gotoLocalPage("md", false), "Title information");
		
		try {
			Image ou;
			ou = Image.getInstance (volumeOu.getDlcMd().getFoafOrganization().getImgURL());
			ou.setAlignment(Image.MIDDLE);
			document.add(ou);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("An organization could not be load for pdf export. " + e.getMessage());
		} 

		document.add(plain);
		document.add(plain);
		
		Font font = FontFactory.getFont("Verdana");
		Font fontbold = FontFactory.getFont("Verdana", 12, Font.BOLD);		
		Font fontColor = FontFactory.getFont("Verdana", 14);
		fontColor.setColor(Color.decode("#EA7125"));
		Font fontitalic = FontFactory.getFont("Verdana", 12, Font.ITALIC);
		
		//write metadata to pdf		
		para = new Paragraph("Sammlung: " + context.getProperties().getName(), fontColor);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		
		document.add(plain);
		document.add(plain);

		//Write bibl. md for volume
		if (vol.getRelatedParentVolume() != null) 
		{
			document = this.mdVolume(document, vol, md);
		}
		//Write bibl. md for monograph
		else
		{
			document = this.mdMonograph(document, vol, md);
		}

		return document;
	}
	
	/**
	 * TODO
	 * @param doc
	 * @param vol
	 * @param volMd
	 * @return
	 * @throws DocumentException
	 */
	private Document mdMonograph (Document doc, Volume vol, ModsMetadata volMd) throws DocumentException
	{
		Font fontbold = FontFactory.getFont("Verdana", 12, Font.BOLD);	
		Phrase phrase1 = new Phrase();
		Phrase phrase2 = new Phrase();
		Paragraph para1 = new Paragraph();
		Paragraph para2 = new Paragraph();
		
		//Authors
		if (volMd.getNames() != null && volMd.getNames().size() > 0)
		{
			String name = "";
			for(ModsName mn : volMd.getNames())
			{
				if("author1".equalsIgnoreCase(mn.getDisplayLabel()))
					name = mn.getName();
			}
			
			if (name != "")
			{
				phrase1 = new Phrase("Autor", fontbold);
				phrase2 = new Phrase(name);
				para1 = new Paragraph();
				para2 = new Paragraph();
				para1.add(phrase1);
				para2.add(phrase2);
				doc.add(para1);
				doc.add(para2);
				//doc.add(new Paragraph( ));
			}
		}		
		//Title
		if (volMd.getTitles() != null && volMd.getTitles().size() > 0 &&
			volMd.getTitles().get(0).getTitle() != null)
		{
			phrase1 = new Phrase("Titel", fontbold);
			phrase2 = new Phrase(volMd.getTitles().get(0).getTitle());
			para1 = new Paragraph();
			para2 = new Paragraph();
			para1.add(phrase1);
			para2.add(phrase2);
			doc.add(para1);
			doc.add(para2);
		}
		//Band Impressum
		if (volMd.getPublishers() != null && volMd.getPublishers().size() >0
				&& volMd.getPublishers().get(0) != null)
		{
			String output = "";
			if(volMd.getPublishers().size() >0)
			{
				for(ModsPublisher publisher : volMd.getPublishers())
				{
					if(publisher.getDisplayLabel().startsWith("publisher"))
					{
						if(publisher.getPlace() != null)
							output += publisher.getPlace() + " : ";
						if(publisher.getPublisher() != null)
							output += publisher.getPublisher() + ", ";
						if(publisher.getDateIssued_425()!=null && publisher.getDateIssued_425().getDate() != null)
							output += publisher.getDateIssued_425().getDate();
					}
				}
			}
			
			phrase1 = new Phrase("Impressum: ", fontbold);
			phrase2 = new Phrase( output);
			para1 = new Paragraph();
			para2 = new Paragraph();
			para1.add(phrase1);
			para2.add(phrase2);
			doc.add(para1);
			doc.add(para2);
		}
		//Impressum Digitalisat (secondary edition)
		if (volMd.getRelatedItems() != null)
		{
			String output = "";
			for(ModsRelatedItem ri : volMd.getRelatedItems())
			{
				for(ModsPublisher publisher : ri.getSec_Publisher())
				{
					if("publisher3".equalsIgnoreCase(publisher.getDisplayLabel()))
					{
						if(publisher.getPlace() != null)
							output += publisher.getPlace() + " : ";
						if(publisher.getPublisher() != null)
							output += publisher.getPublisher() + ", ";
						if(publisher.getDateCaptured()!=null )
							output += publisher.getDateCaptured();
					}
				}
			}

			if (output != "")
			{
				phrase1 = new Phrase("Impressum (Digitalisat)", fontbold);
				phrase2 = new Phrase( output);
				para1 = new Paragraph();
				para2 = new Paragraph();
				para1.add(phrase1);
				para2.add(phrase2);
				doc.add(para1);
				doc.add(para2);
			}
		}
		//Provenance
		if (volMd.getRelatedItems() != null)
		{
			String loc = "";
			for(ModsRelatedItem ri : volMd.getRelatedItems())
			{
				for(ModsLocationSEC sec_location : ri.getSec_location())
				{
					if(sec_location.getSec_signature_646() != null)
						loc = sec_location.getSec_signature_646();
				}
			}
				
			if (loc != "")
			{
				phrase1 = new Phrase("Besitznachweis der Digitalisierungsvorlage", fontbold);
				phrase2 = new Phrase(loc);
				para1 = new Paragraph();
				para2 = new Paragraph();
				para1.add(phrase1);
				para2.add(phrase2);
				doc.add(para1);
				doc.add(para2);
			}
		}
	
		
		return doc;
	}
	
	/**
	 * TODO
	 * @param doc
	 * @param vol
	 * @param volMd
	 * @return
	 * @throws DocumentException
	 */
	private Document mdVolume (Document doc, Volume vol, ModsMetadata volMd) throws DocumentException
	{
		Font fontbold = FontFactory.getFont("Verdana", 12, Font.BOLD);	
		Phrase phrase1 = new Phrase();
		Phrase phrase2 = new Phrase();
		Paragraph para1 = new Paragraph();
		Paragraph para2 = new Paragraph();
		
		Volume parentVol = vol.getRelatedParentVolume();
		ModsMetadata parentVolMd = parentVol.getModsMetadata();
		
		//Title
		if (parentVol.getModsMetadata() != null &&
			parentVol.getModsMetadata().getTitles() != null &&
			parentVol.getModsMetadata().getTitles().size() > 0 &&
			parentVol.getModsMetadata().getTitles().get(0).getTitle() != null)
		{
			phrase1 = new Phrase("Titel: ", fontbold);
			phrase2 = new Phrase(vol.getRelatedParentVolume().getModsMetadata().getTitles().get(0).getTitle());
			para1 = new Paragraph();
			para2 = new Paragraph();
			para1.add(phrase1);
			para2.add(phrase2);
			doc.add(para1);
			doc.add(para2);
		}		
		//Impressum
		if (parentVolMd.getPublishers() != null && parentVolMd.getPublishers().size() >0
				&& parentVolMd.getPublishers().get(0) != null)
		{
			String publisher = "";
			String place = "";
			String date = "";
			
			if (parentVolMd.getPublishers().get(0).getPublisher() != null)
			{
				publisher = parentVolMd.getPublishers().get(0).getPublisher();
				publisher += ", ";
			}
			if (parentVolMd.getPublishers().get(0).getPlace() != null)
			{
				place = parentVolMd.getPublishers().get(0).getPlace();
				place += ", ";
			}
			if (parentVolMd.getPublishers().get(0).getDateIssued_425() != null)
			{
				date = parentVolMd.getPublishers().get(0).getDateIssued_425().getDate();
			}
			
			phrase1 = new Phrase("Impressum: ", fontbold);
			phrase2 = new Phrase( place + publisher + date);
			para1 = new Paragraph();
			para2 = new Paragraph();
			para1.add(phrase1);
			para2.add(phrase2);
			doc.add(para1);
			doc.add(para2);
		}
		//Band
		String volNr = "";
		for(ModsPart mp : volMd.getParts())
			if(mp.getVolumeDescriptive_089()!="" || mp.getVolumeDescriptive_089()!=null)
				volNr = mp.getVolumeDescriptive_089();
			
		phrase1 = new Phrase("Band: ", fontbold);
		phrase2 = new Phrase(volNr);
		para1 = new Paragraph();
		para2 = new Paragraph();
		para1.add(phrase1);
		para2.add(phrase2);
		doc.add(para1);
		doc.add(para2);
		
		//Band Impressum
		if (volMd.getPublishers() != null && volMd.getPublishers().size() >0
				&& volMd.getPublishers().get(0) != null)
		{
			String output = "";
			if(volMd.getPublishers().size() >0)
			{
				for(ModsPublisher publisher : volMd.getPublishers())
				{
					if(publisher.getDisplayLabel().startsWith("publisher"))
					{
						if(publisher.getPlace() != null)
							output += publisher.getPlace() + " : ";
						if(publisher.getPublisher() != null)
							output += publisher.getPublisher() + ", ";
						if(publisher.getDateIssued_425()!=null && publisher.getDateIssued_425().getDate() != null)
							output += publisher.getDateIssued_425().getDate();
					}
				}
			}
			
			phrase1 = new Phrase("Impressum: ", fontbold);
			phrase2 = new Phrase( output);
			para1 = new Paragraph();
			para2 = new Paragraph();
			para1.add(phrase1);
			para2.add(phrase2);
			doc.add(para1);
			doc.add(para2);
		}
		//Impressum Digitalisat (secondary edition)
		if (volMd.getRelatedItems() != null)
		{
			String output = "";
			for(ModsRelatedItem ri : volMd.getRelatedItems())
			{
				for(ModsPublisher publisher : ri.getSec_Publisher())
				{
					if("publisher3".equalsIgnoreCase(publisher.getDisplayLabel()))
					{
						if(publisher.getPlace() != null)
							output += publisher.getPlace() + " : ";
						if(publisher.getPublisher() != null)
							output += publisher.getPublisher() + ", ";
						if(publisher.getDateCaptured()!=null )
							output += publisher.getDateCaptured();
					}
				}
			}

			if (output != "")
			{
				phrase1 = new Phrase("Impressum (Digitalisat)", fontbold);
				phrase2 = new Phrase( output);
				para1 = new Paragraph();
				para2 = new Paragraph();
				para1.add(phrase1);
				para2.add(phrase2);
				doc.add(para1);
				doc.add(para2);
			}
		}
		//Provenance
		if (volMd.getRelatedItems() != null)
		{
			String loc = "";
			for(ModsRelatedItem ri : volMd.getRelatedItems())
			{
				for(ModsLocationSEC sec_location : ri.getSec_location())
				{
					if(sec_location.getSec_signature_646() != null)
						loc = sec_location.getSec_signature_646();
				}
			}
				
			if (loc != "")
			{
				phrase1 = new Phrase("Besitznachweis der Digitalisierungsvorlage", fontbold);
				phrase2 = new Phrase(loc);
				para1 = new Paragraph();
				para2 = new Paragraph();
				para1.add(phrase1);
				para2.add(phrase2);
				doc.add(para1);
				doc.add(para2);
			}
		}
		
		
		return doc;
	}
	
	/**
	 * Generates the table of contents for the pdf export
	 * @param elems
	 * @param doc
	 * @return
	 * @throws DocumentException
	 */
	private Document createTree(List <PbOrDiv> elems, Document doc, PdfOutline root, PdfWriter writer) throws DocumentException
	{
		Paragraph para = new Paragraph();
		Font font = FontFactory.getFont("Verdana");
		Font fontbold = FontFactory.getFont("Verdana", 12, Font.BOLD);
		Font fontitalic = FontFactory.getFont("Verdana", 12, Font.ITALIC);
		PdfOutline oline = root;
		String titleStr = "";
		String type = "";
		
		for (int i = 0; i < elems.size(); i++)
		{
				//Create Outline and TOC for div and title elements
				if (elems.get(i).getElementType() == ElementType.DIV || 
						elems.get(i).getElementType() == ElementType.TITLE_PAGE)
				{
					para.add(level);
					Div elem = (Div) elems.get(i);
					if (elem.getType() != null)
					{
						type = "[" + elem.getType() + "] ";
					}
					else
					{
						type = "[section] ";
					}
					if (elem.getNumeration() != null && elem.getNumeration() != "")
					{
						Phrase num = new Phrase(elem.getNumeration(), fontbold);
						para.add(num);
						para.add(": ");
					}
					
					if(elem.getDocAuthors()!=null && elem.getDocAuthors().size()>0)
					{
						int n=0;
						for(DocAuthor docAuthor : elem.getDocAuthors())
						{
							if(n>0)
							{
								para.add("; ");
							}
							
							para.add(new Phrase(docAuthor.getAuthor(), fontitalic));
							n++;						
						}
						if (n == elem.getDocAuthors().size())
						{
							para.add(": ");
						}
					}

					//Element title
					if (elem.getHead().size() > 0 && elem.getHead().get(0) != "")
					{
						titleStr = elem.getHead().get(0);
						para.add(titleStr);						
					}
					else
					{
						para.add(type);
						titleStr = type;
						type = "";
					}
					
					if (elem.getElementType() == ElementType.TITLE_PAGE)
					{
						TitlePage tp = (TitlePage) elem;
						titleStr = this.replaceLineBreaksWithBlanks(tp.getDocTitles().get(0).getTitle());
						para.add(titleStr);
					}

					List<PbOrDiv> pd = elem.getPbOrDiv();
					if (pd!= null && pd.size() > 0)
					{
						//Outline to a Page
						if (pd.get(0)!= null && pd.get(0).getElementType()!= null && pd.get(0).getElementType() == ElementType.PB)
						{
							Pagebreak pb = (Pagebreak) pd.get(0);
							if (pb != null && pb.getFacs()!= null && pb.getFacs()!="" && oline != null)
							{
								PdfOutline out = new PdfOutline(oline, PdfAction.gotoLocalPage(pb.getFacs(), false), titleStr);	
								oline = out;
							}
						}
						else 
							{
							//Outline to a titlepage
							if (elem.getElementType() == ElementType.TITLE_PAGE)
								{
									TitlePage tp = (TitlePage) elem;
									PdfOutline out = new PdfOutline(oline, PdfAction.gotoLocalPage(tp.getId(), false), titleStr);	
									oline = out;
								}
								//Outline to other elements
								else if (elem.getElementType() == ElementType.DIV)
								{								
									Div div = (Div) elem;
									if (div.getHead()!= null && div.getHead().size()>0)
									{
										titleStr = div.getHead().get(0);
									}
									else 
									{
										titleStr = div.getType();
									}
									PdfOutline out = new PdfOutline(oline, PdfAction.gotoLocalPage(div.getId(), false), titleStr);	
									oline = out;
								}
						}
					}
				}
				
				//Create Outline and TOC for FIGURE elements
				if (elems.get(i).getElementType() == ElementType.FIGURE)
				{
					//Add to toc
					doc.add(new Paragraph(level + "[figure]"));
					
					//Add to outline						
					Div div = (Div) elems.get(i);
					if (div.getHead()!= null && div.getHead().size()>0)
					{
						titleStr = div.getHead().get(0);
					}						
					PdfOutline out = new PdfOutline(oline, PdfAction.gotoLocalPage(div.getId(), false), titleStr);	
					oline = out;
				}		
				
			doc.add(para);
			para = new Paragraph();
			
			if (elems.get(i).getPbOrDiv().size() > 0)
			{
				level+="  ";
				this.createTree(elems.get(i).getPbOrDiv(), doc, oline, writer);
				if (oline != null)
				{
					oline = oline.parent();
				}
				level = level.substring(0, level.length() - 2);
			}	
			
		}
		
		return doc;
	}
	
	public ExportTypes getExportType() {
		return exportType;
	}

	public void setExportType(ExportTypes type) {
		this.exportType = type;
	}
	
	public void setExportType(String type) {
		if (type.equalsIgnoreCase("PDF")) 
			this.exportType = ExportTypes.PDF;
		if (type.equalsIgnoreCase("MODS")) 
			this.exportType = ExportTypes.MODS;	
		if (type.equalsIgnoreCase("TEI")) 
			this.exportType = ExportTypes.TEI;	
	}
	
	
	public String replaceLineBreaksWithBlanks(String teiText)
	{		
		String replacedText = teiText.replaceAll("<lb\\s*/>", " ");
		return replacedText;		
	}
	
	public String getEscidocItem(String identifier) throws MalformedURLException, IOException, URISyntaxException 
	{
		//Variables
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;

    	URL url = new URL(PropertyReader.getProperty("escidoc.common.framework.url") + "ir/item/"+identifier+"/md-records/md-record/escidoc");
        conn = url.openConnection();

        HttpURLConnection httpConn = (HttpURLConnection) conn;
        int responseCode = httpConn.getResponseCode();
        switch (responseCode)
        {
        	case 200:
            // Get XML
            isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
            bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
            	resultXml += line + "\n";
            }
            httpConn.disconnect();  
            break;
        }    
		return resultXml;
	}
	
	private String getTeiSdUrl(Volume vol )
	{
		String teiSdUrl = "";
		for (int i = 0; i< vol.getItem().getComponents().size(); i++)
			{
				Component comp = vol.getItem().getComponents().get(i);
				if (comp.getProperties().getContentCategory().equals("tei-sd"))
				{
					teiSdUrl = comp.getXLinkHref();
				}
			}
		
		if (teiSdUrl.startsWith("/"))
		{
			teiSdUrl = teiSdUrl.substring(1);
		}
		
		return teiSdUrl+"/content";
	}
	
	private String getMetsUrl(Volume vol )
	{
		String metsUrl = "";
		for (int i = 0; i< vol.getItem().getMetadataRecords().size(); i++)
			{
				MetadataRecord md = vol.getItem().getMetadataRecords().get(i);
				if (md.getName().equalsIgnoreCase("mets"))
				{
					metsUrl = md.getXLinkHref();
				}
			}
		
		if (metsUrl.startsWith("/"))
		{
			metsUrl = metsUrl.substring(1);
		}
		
		return metsUrl;
	}
	
}
