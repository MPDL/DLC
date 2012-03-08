package de.mpg.mpdl.jsf.components.carousel;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class TestCarouselBean {
	private List<String> img = new ArrayList<String>();
	
	public TestCarouselBean() 
	{  
		img.add("http://localhost:8080/dlc/resources/images/carousell/p1.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p2.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p3.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p3.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p3.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p3.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p3.jpg");
		img.add("http://localhost:8080/dlc/resources/images/carousell/p3.jpg");


	}




	public List<String> getImg() 
	{
		return img;
	}

	public void setImg(List<String> img) {
		this.img = img;
	}

}
