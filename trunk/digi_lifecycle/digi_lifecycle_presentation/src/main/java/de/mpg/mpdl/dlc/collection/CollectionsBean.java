package de.mpg.mpdl.dlc.collection;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.sun.tools.javac.comp.Todo;

import de.mpg.mpdl.dlc.valueobjects.DlcBook;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
public class CollectionsBean extends BasePaginatorBean<DlcBook> {
	
	private String selectedState;
	private List<DlcBook> list = new ArrayList<DlcBook>();
	
	public CollectionsBean()
	{
		super();
		this.selectedState = "all";
		
		//TODO

	}

	//TODO
	public List<DlcBook> retrieveList(int offset, int limit)throws Exception {
		return list;
	}

	@Override
	public int getTotalNumberOfRecords() {
		return 0;
	}

	@Override
	public String getNavigationString() {
		return "pretty:collections";
	}

	public void setSelectedState(String selectedState) { 
		this.selectedState = selectedState;
	}

	public String getSelectedState() {
		return selectedState;
	}
	
}
