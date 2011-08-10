package de.mpg.mpdl.dlc.ingest;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@ViewScoped
public class TestPaginatorBean extends BasePaginatorBean<String> {

	private List<String> list = new ArrayList<String>();
	
	public TestPaginatorBean()
	{
		super();
		
		for (int i=0; i<2000; i++)
		{
			list.add("test " + i);
		}
		
	}
	
	@Override
	public List<String> retrieveList(int offset, int limit) throws Exception {
		return list.subList(offset, offset+limit); 
		
	}

	@Override
	public int getTotalNumberOfRecords() {
		return list.size();
	}

	@Override
	public String getNavigationString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentPageNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
