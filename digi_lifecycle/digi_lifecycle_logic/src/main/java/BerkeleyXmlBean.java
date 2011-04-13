import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlManager;


@Stateless
public class BerkeleyXmlBean implements BerkeleyXmlService {

	@Override
	public int test(int a, int b) {
		
		
		
		return a+b;
	}
	
	public static void main(String[] args) throws Exception
	{

		
		XmlManager xmlManager = new XmlManager();
		XmlContainer testContainer = xmlManager.openContainer("test.dbxml");
		testContainer.putDocument("testDocument", "<test>Hallo</test>");
		testContainer.g
	}

}
