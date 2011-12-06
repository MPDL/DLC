package de.mpg.mpdl.dlc.vo.util;


import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return v;
    }

    @Override
    public String marshal(String v) throws Exception {
        if(v != null && "".equals(v.trim()))
        {
        	
            return null;
        }
        return v;
    }

}