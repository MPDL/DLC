package de.mpg.mpdl.dlc.export;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.export.Export.ExportTypes;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;

@ManagedBean
@SessionScoped
public class ExportBean {
	
	private static Logger logger = Logger.getLogger(ExportBean.class);
	
	private ExportTypes exportType;
	private Volume volume;
	
	public ExportBean()
	{
		
	}

	public static byte[] doExport(Volume vol, ExportTypes type) throws Exception
	{	
		byte[] result = null;
		Export export = new Export();
		export.setExportType(type);
		
		if (export.getExportType().equals(Export.ExportTypes.PDF))
		{
			result = export.pdfExport(vol);
		}
		if (export.getExportType().equals(Export.ExportTypes.PRINT))
		{
			//result = export.print(page);
		}
	
		return result;
	}
	
	public static byte[] doExport(Page page, ExportTypes type) 
	{	

			byte[] result = null;
			Export export = new Export();
			export.setExportType(type);
			
			if (export.getExportType().equals(Export.ExportTypes.PDF))
			{
				result = export.pdfExport(page);
			}
			if (export.getExportType().equals(Export.ExportTypes.PRINT))
			{
				//result = export.print(page);
			}
		
		return result;
	}
	
	
	public ExportTypes getExportType() {
		return exportType;
	}

	public void setExportType(ExportTypes exportType) {
		this.exportType = exportType;
	}
	
	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

}
