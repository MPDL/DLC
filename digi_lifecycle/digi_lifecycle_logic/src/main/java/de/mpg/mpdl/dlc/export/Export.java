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
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.component.Component;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.DocAuthor;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
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
	
	public byte[] metsModsExport(String itemId) throws Exception
	{
        String xsltUri ="export/dlczvddmets.xsl";
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
		writer.setViewerPreferences(PdfWriter.PageLayoutTwoPageRight);
		document.setPageSize(PageSize.A4);
		document.addCreator("Digitization Lifecycle");
		
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
						if (currentElem.getElementType().name().equals("DIV") || currentElem.getElementType().name().equals("TITLE_PAGE"))
						{
								document = this.createTree(currentElem.getPbOrDiv(), document, root, writer);
						}
						else 
							if (currentElem.getElementType().name().equals("FRONT") || currentElem.getElementType().name().equals("BACK") 
									|| currentElem.getElementType().name().equals("BODY")|| currentElem.getElementType().name().equals("GROUP")
									|| currentElem.getElementType().name().equals("TEXT"))
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
		document.add(new Chunk("Title information").setLocalDestination("md"));
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		PdfOutline tocout = new PdfOutline(root, PdfAction.gotoLocalPage("md", false), "Title information");
		
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
		if (md.getTitles() != null && md.getTitles().size() > 0)
		{
			if (md.getTitles().get(0) != null && md.getTitles().get(0).getTitle() != null)
			{
				document.addTitle(md.getTitles().get(0).getTitle());
				para = new Paragraph(md.getTitles().get(0).getTitle(),FontFactory.getFont("Verdana", 16, Font.BOLD));
				para.setAlignment(Element.ALIGN_CENTER);
				document.add(para);
			}
		}
		para = new Paragraph("Collection: " + context.getProperties().getName(), fontColor);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		
		if (vol.getRelatedParentVolume() != null) //Item is a volume (not monograph)
		{
			if (vol.getRelatedParentVolume().getModsMetadata() != null &&
					vol.getRelatedParentVolume().getModsMetadata().getTitles() != null &&
					vol.getRelatedParentVolume().getModsMetadata().getTitles().size() > 0 &&
					vol.getRelatedParentVolume().getModsMetadata().getTitles().get(0).getTitle() != null)
			{
				phrase1 = new Phrase("Multivolume: ", fontbold);
				phrase2 = new Phrase(vol.getRelatedParentVolume().getModsMetadata().getTitles().get(0).getTitle());
				para = new Paragraph();
				para.add(phrase1);
				para.add(phrase2);
				document.add(para);
			}
		}
		document.add(new Paragraph(" "));
		if (md.getCatalogueId_001() != null)
		{
			phrase1 = new Phrase("Catalog id: ", fontbold);
			phrase2 = new Phrase(md.getCatalogueId_001());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getSignature_544() != null)
		{	
			phrase1 = new Phrase("Signature: ", fontbold);
			phrase2 = new Phrase(md.getSignature_544());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getNames() != null && md.getNames().size() > 0)
		{
			phrase1 = new Phrase("Author(s): ", fontbold);
			phrase2 = new Phrase(md.getSignature_544());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
			document.add(new Paragraph( ));
			for (int i = 0; i< md.getNames().size(); i++)
			{
					ModsName mn = md.getNames().get(i);
					if (mn.getDisplayLabel() != null)
					{para = new Paragraph("          " + mn.getDisplayLabel(),fontbold); document.add(para);}
					if (mn.getMabId() != null)
					{para = new Paragraph("          MAB id: " + mn.getMabId(),fontitalic); document.add(para);}
					if (mn.getType() != null)
					{para = new Paragraph("          Type: " + mn.getType(),fontitalic); document.add(para);}
					if (mn.getName() != null)
					{para = new Paragraph("          Name: " + mn.getName(),fontitalic); document.add(para);}
					if (mn.getRole() != null)
					{para = new Paragraph("          Role: " + mn.getRole(),fontitalic); document.add(para);}
					if (mn.getRole() != null)
					{para = new Paragraph("          Authority: " + mn.getRole(),fontitalic); document.add(para);}
				
			}
			document.add(plain);
		}		
		if (md.getTitles()!= null && md.getTitles().size() > 1 && md.getTitles().get(1) != null && md.getTitles().get(1).getSubTitle() != null)
		{			
			phrase1 = new Phrase("Subtitle: ", fontbold);
			phrase2 = new Phrase(md.getTitles().get(1).getSubTitle());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getNotes() != null && md.getNotes().size() > 0 && md.getNotes().get(0).getNote()!= null)
		{
			phrase1 = new Phrase("Statement of responsibility: ", fontbold);
			phrase2 = new Phrase(md.getNotes().get(0).getNote());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getPublishers() != null && md.getPublishers().size() >0 && md.getPublishers().get(0).getPlace() != null)
		{
			phrase1 = new Phrase("Publishing Place: ", fontbold);
			phrase2 = new Phrase(md.getPublishers().get(0).getPlace());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getPublishers() != null && md.getPublishers().size() >0 && md.getPublishers().get(0).getPublisher() != null)
		{
			phrase1 = new Phrase("Publisher: ", fontbold);
			phrase2 = new Phrase(md.getPublishers().get(0).getPublisher());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getLanguage_037() !=null && md.getLanguage_037().getLanguage() != null)
		{
			phrase1 = new Phrase("Language: ", fontbold);
			phrase2 = new Phrase(md.getLanguage_037().getLanguage());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getPhysicalDescriptions() != null && md.getPhysicalDescriptions().size()>0 && md.getPhysicalDescriptions().get(0) != null)
		{
			if (md.getPhysicalDescriptions().get(0).getExtent() != null)
			{
				phrase1 = new Phrase("Extents: ", fontbold);
				phrase2 = new Phrase("" + md.getPhysicalDescriptions().get(0).getExtent());
				para = new Paragraph();
				para.add(phrase1);
				para.add(phrase2);
				document.add(para);
			}
		}
		if(vol.getMets().getPages() != null && vol.getMets().getPages().size() > 0)
		{
			phrase1 = new Phrase("Number of Scans: ", fontbold);
			phrase2 = new Phrase("" + vol.getMets().getPages().size());
			para = new Paragraph();
			para.add(phrase1);
			para.add(phrase2);
			document.add(para);
		}
		if (md.getKeywords() != null && md.getKeywords().size() > 0)
		{
			phrase1 = new Phrase("Keywords: ", fontbold);		
			para = new Paragraph();
			para.add(phrase1);
			for (int i = 0; i<md.getKeywords().size(); i++)
			{
				phrase2 = new Phrase("          " + md.getKeywords().get(i));
				para.add(phrase2);
			}	
			document.add(para);	
		}
		
		return document;
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
				if (elems.get(i).getElementType().name().equals("DIV") || elems.get(i).getElementType().name().equals("TITLE_PAGE"))
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
					
					if (elem.getElementType().name().equals("TITLE_PAGE"))
					{
						TitlePage tp = (TitlePage) elem;
						titleStr = this.replaceLineBreaksWithBlanks(tp.getDocTitles().get(0).getTitle());
						para.add(titleStr);
					}

					List<PbOrDiv> pd = elem.getPbOrDiv();
					if (pd!= null && pd.size() > 0)
					{
						//Outline to a Page
						if (pd.get(0)!= null && pd.get(0).getElementType()!= null && pd.get(0).getElementType().name().equalsIgnoreCase("pb"))
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
							if (elem.getElementType().name().equalsIgnoreCase("TITLE_PAGE"))
								{
									TitlePage tp = (TitlePage) elem;
									PdfOutline out = new PdfOutline(oline, PdfAction.gotoLocalPage(tp.getId(), false), titleStr);	
									oline = out;
								}
								//Outline to other elements
								else if (elem.getElementType().name().equalsIgnoreCase("div"))
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
				if (elems.get(i).getElementType().name().equals("FIGURE"))
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
