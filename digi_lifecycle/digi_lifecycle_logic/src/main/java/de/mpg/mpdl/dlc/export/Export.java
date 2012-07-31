package de.mpg.mpdl.dlc.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsNote;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.TitlePage;



public class Export {

	private static Logger logger = Logger.getLogger(Export.class);
	public ExportTypes exportType;
	public String level = "";

	public enum ExportTypes
	{
		PDF, PRINT, DFG;
	}
	
	public Export()
	{
		
	}
	
	/**
	 * convert a page in pdf format for export.
	 * @param page
	 * @return pdf
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public byte[] pdfExport(Page page)
	{	

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document();
		
		//TODO: check if necessary
		
		document.close();				
		
		return out.toByteArray();
	}
	
	/**
	 * convert a volume in pdf format for export.
	 * Additionally a metadata page (title, collection name, provenance info) will be created.
	 * @param volume
	 * @return pdf
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public byte[] pdfExport(Volume vol) throws RuntimeException, DocumentException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document();
		String imageurl = "";
		PdfWriter.getInstance(document, out);
		
		document.open();
		document.setPageSize(PageSize.A4);
		document.addCreator("Digitization Lifecycle");
		
		try 
		{		
			//write organizational logo to pdf
			document.newPage();
			document = this.generateBiblMd(vol, document);
		
			if (vol.getTeiSd() != null)
			{
				//write structure to pdf
				document.newPage();
			
				for (int i = 0; i< vol.getTeiSd().getPbOrDiv().size(); i++)			
				{
					PbOrDiv currentElem = vol.getTeiSd().getPbOrDiv().get(i);
					if (currentElem.getType()!= "PB")
					{
						if (currentElem.getElementType().name().equals("DIV") || currentElem.getElementType().name().equals("TITLE_PAGE"))
						{
								document = this.createTree(currentElem.getPbOrDiv(), document);
						}
						else 
							if (currentElem.getElementType().name().equals("FRONT") || currentElem.getElementType().name().equals("BACK") 
									|| currentElem.getElementType().name().equals("BODY"))
							{
								document.add(new Paragraph(currentElem.getElementType().name().toString()));
								level += "  ";
								document = this.createTree(currentElem.getPbOrDiv(), document);
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
					
				document.newPage();
					
				image.setAlignment(Image.MIDDLE);
				image.scaleAbsolute(PageSize.A4.getRight(), PageSize.A4.getHeight());
				image.setAbsolutePosition(0, 0);
				document.add(image);
			}
		}
		catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("An error occurred during the pdf creation for item " + vol.getObjidAndVersion() + ": " + e.getMessage());
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
	 * @param doc
	 * @return
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private Document generateBiblMd (Volume vol, Document document) throws DocumentException
	{	
		ModsMetadata md = vol.getModsMetadata();
		OrganizationalUnitServiceBean orgServiceBean = new OrganizationalUnitServiceBean();
		ContextServiceBean contextServiceBean = new ContextServiceBean();
		Context context = contextServiceBean.retrieveContext(vol.getItem().getProperties().getContext().getObjid(), null);
		Organization volumeOu = orgServiceBean.retrieveOrganization(context.getProperties().getOrganizationalUnitRefs().getFirst().getObjid());		

		try {
			Image ou;
			ou = Image.getInstance (volumeOu.getDlcMd().getFoafOrganization().getImgURL());
			ou.setAlignment(Image.MIDDLE);
			document.add(ou);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.warn("An organization could not be load for pdf export. " + e.getMessage());
		} 

		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		
		//write metadata to pdf
		if (md.getTitles() != null && md.getTitles().size() > 0)
		{
			document.addTitle(md.getTitles().get(0).getTitle());
			document.add(new Paragraph(md.getTitles().get(0).getTitle(),FontFactory.getFont(FontFactory.TIMES_BOLD, 16, Font.BOLD)));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
		}

		document.add(new Paragraph("Collection: " + context.getProperties().getName()));
		if (vol.getRelatedParentVolume() != null) //Item is a volume (not monograf)
			document.add(new Paragraph("Multivolume: " + vol.getRelatedParentVolume().getModsMetadata().getTitles().get(0).getTitle()));
		document.add(new Paragraph(" "));
		if (md.getCatalogueId_001() != null)
			document.add(new Paragraph("Catalog id: " + md.getCatalogueId_001()));
		if (md.getSignature_544() != null)
			document.add(new Paragraph("Signature: " + md.getSignature_544()));
		if (md.getNames() != null && md.getNames().size() > 0)
		{
			document.add(new Paragraph("Author(s): " ));
			for (int i = 0; i< md.getNames().size(); i++)
			{
				ModsName mn = md.getNames().get(i);
				document.add(new Paragraph("          " + mn.getDisplayLabel()));
				document.add(new Paragraph("          MAB id: " + mn.getMabId()));
				document.add(new Paragraph("          Type: " + mn.getType()));
				document.add(new Paragraph("          Name: " + mn.getName()));
				document.add(new Paragraph("          Role: " + mn.getRole()));
				document.add(new Paragraph("          Authority: " + mn.getRoleTermAuthority()));
			}
		}		
		if (md.getTitles()!= null && md.getTitles().size() > 1 && md.getTitles().get(1) != null && md.getTitles().get(1).getSubTitle() != null)
			document.add(new Paragraph("Subtitle: " + md.getTitles().get(1).getSubTitle()));
		if (md.getNotes() != null && md.getNotes().size() > 0)
			document.add(new Paragraph("Statement of responsibility: " + md.getNotes().get(0).getNote()));
		if (md.getPublishers() != null && md.getPublishers().get(0).getPlace() != null)
		document.add(new Paragraph("Publishing Place: " + md.getPublishers().get(0).getPlace()));
		if (md.getPublishers() != null && md.getPublishers().get(0).getPublisher() != null)
			document.add(new Paragraph("Publisher: "+ md.getPublishers().get(0).getPublisher()));
		if (md.getLanguage_037() !=null && md.getLanguage_037().getLanguage() != null)
			document.add(new Paragraph("Language: " + md.getLanguage_037().getLanguage()));
		if (md.getPhysicalDescriptions() != null && md.getPhysicalDescriptions().size()>0 && md.getPhysicalDescriptions().get(0) != null)
			document.add(new Paragraph("Extents: " + md.getPhysicalDescriptions().get(0).getExtent()));
		document.add(new Paragraph("Number of Scans: " + vol.getPages().size()));	
		if (md.getKeywords() != null && md.getKeywords().size() > 0)
		{
			document.add(new Paragraph("Keywords: "));	
			for (int i = 0; i<md.getKeywords().size(); i++)
			{
				document.add(new Paragraph("          " + md.getKeywords().get(i)));				
			}			
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
	private Document createTree(List <PbOrDiv> elems, Document doc) throws DocumentException
	{
		String entry = "";
		
		for (int i = 0; i < elems.size(); i++)
		{
				
				if (elems.get(i).getElementType().name().equals("DIV") || elems.get(i).getElementType().name().equals("TITLE_PAGE"))
				{
					Div elem = (Div) elems.get(i);
					if (elem.getType() != null)
					{
						entry += "[" + elem.getType() + "] ";
					}
					else
					{
						entry += "[Section] ";
					}
					if (elem.getNumeration() != null)
					{
						entry += elem.getNumeration();
					}
					if (elem.getAuthor1inv() != null)
					{
						entry += elem.getAuthor1inv();
					}
					if (elem.getAuthor2inv() != null)
					{
						entry += "; " + elem.getAuthor2inv();
					}
					if (elem.getAuthor3inv() != null)
					{
						entry += "; " + elem.getAuthor3inv();
					}
					if ((elem.getAuthor1inv() != null || elem.getAuthor2inv() != null || elem.getAuthor3inv() != null)
							&& elem.getHead().size() > 0)
					{
						entry += " : ";
					}
					if (elem.getHead().size() > 0)
					{
						entry += " [" + elem.getHead().get(0) + "]";
					}
					if (elem.getElementType().name().equals("TITLE_PAGE"))
					{
						TitlePage tp = (TitlePage) elem;
						entry += this.replaceLineBreaksWithBlanks(tp.getDocTitles().get(0).getTitle());
					}
					
					doc.add(new Paragraph(level + entry));
					entry = "";
				}
				if (elems.get(i).getElementType().name().equals("FIGURE"))
				{
					doc.add(new Paragraph(level + "[" + elems.get(i).getElementType().name()+ "]"));
				}
			
				
			if (elems.get(i).getPbOrDiv().size() > 0)
			{
				level+="  ";
				this.createTree(elems.get(i).getPbOrDiv(), doc);
				level = level.substring(level.length() -2);
			}	
		}
		
		return doc;
	}
	
	/**
	 * send a page to printer.
	 * @param page
	 * @return 
	 */
	public String print(Page page)
	{
		return null;
	}
	
	/**
	 * send a volume to printer.
	 * Additionally a metadata page will be created.
	 * @param volume
	 * @return 
	 */
	public String print(Volume vol)
	{
		return null;
	}
	
	public ExportTypes getExportType() {
		return exportType;
	}

	public void setExportType(ExportTypes type) {
		this.exportType = type;
	}
	
	public String replaceLineBreaksWithBlanks(String teiText)
	{
		
		String replacedText = teiText.replaceAll("<lb\\s*/>", " ");
		return replacedText;
		
	}
	
}
