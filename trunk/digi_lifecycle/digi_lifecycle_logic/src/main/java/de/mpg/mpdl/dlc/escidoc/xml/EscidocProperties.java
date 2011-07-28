package de.mpg.mpdl.dlc.escidoc.xml;

import org.w3.x1999.xlink.TypeAttribute;

import de.escidoc.core.properties.OrganizationalUnitsDocument.OrganizationalUnits;
import de.escidoc.schemas.commontypes.LinkForCreate;
import de.escidoc.schemas.commontypes.LinkRequired;

public class EscidocProperties {
	
	private static de.escidoc.schemas.item.PropertiesDocument.Properties itemProperties = null;
	private static de.escidoc.schemas.components.PropertiesDocument.Properties componentProperties = null;
	private static de.escidoc.schemas.contentmodel.PropertiesDocument.Properties contentModelProperties = null;
	private static de.escidoc.schemas.context.PropertiesDocument.Properties contextProperties = null;
	private static de.escidoc.schemas.grants.GrantDocument.Grant.Properties grantProperties = null;
	
	private EscidocProperties() {}
	
	public enum Href {
		ITEM("/ir/item/"),
		CONTEXT("/ir/context/"),
		CMODEL("/cmm/content-model/"),
		OU("/oum/organizational-unit/"),
		ROLE("/aa/role/");
		private final String href;
		Href(String href) {
			this.href = href;
		}
		@Override public String toString() {
			return href;
		}
	}
	
	public enum ContentCategory {
		DLC_METS("dlc_mets"),
		DLC_TEI_FULLTEXT("dlc_tei_fulltext"),
		DLC_TEI_STRUCTURE("dlc_tei_structure"),
		DLC_TEI_TO_XHTML("dlc_tei_to_xhtml");
		private final String category;
		ContentCategory(String category) {
			this.category = category;
		}
		@Override public String toString() {
			return category;
		}
	}
	
	public enum Visibility {
		PUBLIC("public"),
		PRIVATE("private");
		private final String visibility;
		Visibility(String visibility) {
			this.visibility = visibility;
		}
		@Override public String toString() {
			return visibility;
		}
	}
	
	public enum MimeType {
		TEXT_XML("text/xml"),
		APPLICATION_XML("application/xml"),
		APPLICATION_XSL("application/xslt+xml");
		private final String mimeType;
		MimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		@Override public String toString() {
			return mimeType;
		}
	}
	
	public static de.escidoc.schemas.item.PropertiesDocument.Properties item(String contextId, String cmodelId)
	{
		itemProperties = de.escidoc.schemas.item.PropertiesDocument.Properties.Factory.newInstance();
		LinkForCreate contextLink = itemProperties.addNewContentModel();
		contextLink.setHref(Href.CONTEXT + contextId);
		LinkForCreate contentModelLink = itemProperties.addNewContentModel();
		contentModelLink.setHref(Href.CMODEL + cmodelId);
		return itemProperties;
	}
	
	public static de.escidoc.schemas.components.PropertiesDocument.Properties component(Visibility v, ContentCategory cc, MimeType mt)
	{
		componentProperties = de.escidoc.schemas.components.PropertiesDocument.Properties.Factory.newInstance();
		componentProperties.setVisibility(v.toString());
		componentProperties.setContentCategory(cc.toString());
		componentProperties.setMimeType(mt.toString());
		return componentProperties;
	}
	
	public static de.escidoc.schemas.contentmodel.PropertiesDocument.Properties contentModel(String cmodelName, String cmodelDescription)
	{
		contentModelProperties = de.escidoc.schemas.contentmodel.PropertiesDocument.Properties.Factory.newInstance();
		contentModelProperties.setName(cmodelName);
		contentModelProperties.setDescription(cmodelDescription);
		return contentModelProperties;
	}
	
	public static de.escidoc.schemas.context.PropertiesDocument.Properties context(String contextName, String contextDescription, String contextType, String ouId)
	{
		contextProperties = de.escidoc.schemas.context.PropertiesDocument.Properties.Factory.newInstance();
		contextProperties.setName(contextName);
		contextProperties.setDescription(contextDescription);
		contextProperties.setType(contextType);
		OrganizationalUnits ous = contextProperties.addNewOrganizationalUnits();
		LinkRequired ou = ous.addNewOrganizationalUnit();
		ou.setHref(Href.OU + ouId);
		return contextProperties;
	}
	
	public static de.escidoc.schemas.grants.GrantDocument.Grant.Properties grant(String roleId, String contextId)
	{
		grantProperties = de.escidoc.schemas.grants.GrantDocument.Grant.Properties.Factory.newInstance();
		LinkRequired role = grantProperties.addNewRole();
		role.setHref(Href.ROLE + roleId);
		LinkRequired ctx = grantProperties.addNewAssignedOn();
		ctx.setHref(Href.CONTEXT + contextId);
		return grantProperties;
	}

}
