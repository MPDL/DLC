package de.mpg.mpdl.dlc.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@ManagedBean
@RequestScoped
public class HierarchicalBreadcrumbHelper {

	
	private static Logger logger = Logger.getLogger(HierarchicalBreadcrumbHelper.class);
	
	
	public static Collection getVolumeCollection(Volume volume)
	{
		String colId = volume.getItem().getProperties().getContext().getObjid();
		volume.getItem().getProperties().getContext().getXLinkTitle();
		ContextServiceBean contextServiceBean = new ContextServiceBean();
		Collection col = contextServiceBean.retrieveCollection(colId, null);
		return col;
	}
	
}
