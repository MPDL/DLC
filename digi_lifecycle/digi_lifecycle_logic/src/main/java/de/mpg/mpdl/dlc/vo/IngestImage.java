package de.mpg.mpdl.dlc.vo;

import java.io.File;

import org.apache.commons.fileupload.disk.DiskFileItem;

import de.mpg.mpdl.dlc.vo.mets.Page;

public class IngestImage implements Comparable<IngestImage> {

	
	public enum Type {
		DISK, ONLINE;
	}
	
	private Type type;
	private DiskFileItem diskFileItem;
	
	
	private Page page;
	private String url;
	
	private String name;
	

	public IngestImage(DiskFileItem diskFileItem)
	{
		this.diskFileItem = diskFileItem;
		this.name = diskFileItem.getName();
		this.type = Type.DISK;
	}
	
	public IngestImage(File f)
	{
		this.name = f.getName();
		new DiskFileItem(f.getName(), "image", true, f.getName(), 0, f);
		this.type = Type.DISK;
	}
	
	public IngestImage(String url, String name)
	{
		this.url = url;
		if(name==null)
		{
			int beginIndex = url.lastIndexOf("/");
			this.name = url.substring(beginIndex+1);
			
			if(this.name.matches(".*.\\.*jpg$"))
			{
				this.name = this.name.substring(0, this.name.length()-4);
			}
		}
		else
		{
			this.name = name;
		}
		this.type = Type.ONLINE;
	}
	
	
	public IngestImage(Page p)
	{
		this(p.getContentIds(), p.getLabel());
	}
	
	

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public DiskFileItem getDiskFileItem() {
		return diskFileItem;
	}

	public void setDiskFileItem(DiskFileItem diskFileItem) {
		this.diskFileItem = diskFileItem;
	}

	@Override
	public int compareTo(IngestImage o) {
		
		return this.getName().compareTo(o.getName());
	}
	
	@Override
	public boolean equals(Object o)
	{
		return getName().equals(((IngestImage)o).getName());
	}


	
	
}
