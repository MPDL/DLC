import java.util.ArrayList;
import java.util.List;


import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlManager;



public class BerkeleyXmlBean implements BerkeleyXmlService {

	@Override
	public int test(int a, int b) {
		
		
		
		return a+b;
	}
	
	public void createVolume(String userHandle)
	{
		
	}
	
	public static void main(String[] args) throws Exception
	{

		
		XmlManager xmlManager = new XmlManager();
		XmlContainer testContainer = xmlManager.createContainer("test.db.xml");
		//xmlManager.openContainer("test.dbxml");
		testContainer.putDocument("testDocument", "<test>Hallo</test>");
	}

}