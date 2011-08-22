package de.mpg.mpdl.dlc.util;

import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class UtilBean {

	public static int getListSize(List list)
	{
		if(list!= null)
		{
			return list.size();
		}
		else
		{
			return 0;
		}
	}
}
