package de.mpg.mpdl.dlc.util;

public final class BatchIngestLogs {
	
    public static final String PBS_NOTEQUAL_IMAGES_ERROR = "Error: pbs != images";
    
    public static final String TEI_SYNTAX_ERROR = "Error: cannot read the tei File ";
    
    public static final String MAB_TRANSFORM_ERROR = "Error: can not  transform the MAB file";
    
    public static final String NOMAB_ERROR ="Error: find volume without mab file";
    
    public static final String SINGLE_TEI_ERROR= "Error: tei File without images";
    
    public static final String SINGLE_MULTIVOLUME_ERROR = "Error: this Multivolume has no volume";
    
    public static final String SINGLE_VOLUME_ERROR = "Error: this volume has no multivolume";
    
    public static final String MULTIVOLUME_ROLLBACK = "Error: an internal exception occured, Rollback, stop ingest volume";
    
    public static final String MONOGRAPH_ROLLBACK = "Error: an internal exception occured, Rollback";
    
    public static final String VOLUME_ROLLBACK = "Error: an internal exception occured, Rollback";
    
    public static final String MULTIFOOTER_ERROR = "Error: More than one Footer exist";
    
    public static final String FTP_CONNECT_ERROR ="Error: cannot connect to ftp/ftps server";
    
    public static final String FTP_CONNECT_RETRY ="Cannot connect to ftp/ftps server, Retry.";
    
    public static final String IMAGE_DIRECTORY_ERROR = "Error: check images directory";
    
    public static final String TEI_DIRECTORY_ERROR ="Error: check tei directory";
    
    public static final String MAB_DIRECTORYE_RROR ="Error: check mab directory";
    
    public static final String DOWNLOAD_IMAGES_FTP="downloading images from FTP Server";
    
    public static final String DOWNLOAD_IMAGES_FTPS="downloading images from FTPS Server";
    
    public static final String NEW_MULTIVOLUME ="Creating new Multivolume";
    
    public static final String NEW_VOLUME ="Creating new volume";
    
    public static final String NEW_MONOGRAPH="Creating new monograph";
    
    public static final String FTP_LOGIN ="ftp Logged in";
    
    public static final String FTPS_Login = "ftps logged in";
    
    public static final String READ_FILE_ERROR = "can not read file: ";
    
    
    private BatchIngestLogs()
    {
    	
    }

}
