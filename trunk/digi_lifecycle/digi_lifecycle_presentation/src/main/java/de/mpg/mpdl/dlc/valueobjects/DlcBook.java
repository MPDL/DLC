package de.mpg.mpdl.dlc.valueobjects;

import gov.loc.mods.v3.ModsDocument;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.core.client.ItemHandlerClient;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.util.BeanHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.escidoc.core.resources.om.item.Item;

public class DlcBook implements Comparable<DlcBook>
{
    private static Logger logger = Logger.getLogger(DlcBook.class);
    private LoginBean lb;
    private Item item;
    private DlcMetadata metadata = null;
    private DlcToc toc = null;
    
    private String objid = null;
    private String parentObjid = null;
    private String type = null;
    private ModsDocument mvMetadata = null;
    private String tocId = null;
    private String userHandle = null;
    private List<DlcBook> volumes = null;
    private String[] publicationDates;
    private String sorting = null;

	
	public DlcBook(String objid)
	{
		lb = (LoginBean) BeanHelper.getSessionBean(LoginBean.class);
		try {
			ItemHandlerClient ihc = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			ihc.setHandle(lb.getUserHandle());
			this.item = ihc.retrieve(objid);
			
		} catch (Exception e) {
            logger.error("Error getting item", e);
		}
	}

	public int compareTo(DlcBook dlc) {
		return 0;
	}

}
