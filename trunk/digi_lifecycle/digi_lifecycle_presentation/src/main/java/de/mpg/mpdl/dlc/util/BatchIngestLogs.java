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
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.util;

public final class BatchIngestLogs {
	
    public static final String PBS_NOTEQUAL_IMAGES_ERROR = "Error: pbs != images";
    
    public static final String TEI_SYNTAX_ERROR = "Error: cannot read the tei File ";
    
    public static final String CDC_SYNTAX_ERROR = "Error: cannot read the codicological File ";
    
    public static final String MAB_TRANSFORM_ERROR = "Error: can not  transform the MAB file";
    
    public static final String NOMAB_ERROR ="Error: find volume without mab file";
    
    public static final String SINGLE_TEI_ERROR= "Error: tei File without images";
    
    public static final String SINGLE_CDC_ERROR= "Error: codicological File without images";
    
    public static final String SINGLE_MULTIVOLUME_ERROR = "Error: this Multivolume has no volume";
    
    public static final String SINGLE_VOLUME_ERROR = "Error: this volume has no multivolume";
    
    public static final String MULTIVOLUME_ROLLBACK = "Error: an internal exception occured, Rollback, stop ingest volume";
    
    public static final String MONOGRAPH_ROLLBACK = "Error: an internal exception occured, Rollback";
    
    public static final String VOLUME_ROLLBACK = "Error: an internal exception occured, Rollback";
    
    public static final String MULTIFOOTER_ERROR = "Error: More than one Footer exist";
    
    public static final String FTP_CONNECT_ERROR ="Error: cannot connect to ftp/ftps server";
    
    public static final String FTP_CONNECT_RETRY ="Cannot connect to ftp/ftps server, Retry.";
    
    public static final String IMAGE_DIRECTORY_ERROR = "Error: check images directory";
    
    public static final String MAB_DIRECTORYE_RROR ="Error: check mab directory";
    
    public static final String TEI_DIRECTORY_ERROR ="Error: check tei directory";    
    
    public static final String CDC_DIRECTORY_ERROR ="Error: check Codicological files directory";
    
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
