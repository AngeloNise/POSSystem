package com.microservice.systemservice.helper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {
    public static Properties pro = null;
    public static Properties loadProperty() throws Exception{
        Properties property = new Properties();
        InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
        property.load(is);
        return property;
    }
    public static String getPropertyValue(String param) throws Exception{
        Properties p = PropertiesUtils.loadProperty();
        return p.getProperty(param);
    }
    public static String getPropertyText(String param) throws Exception{
        if(pro==null){
            pro=PropertiesUtils.loadProperty();
        }
        return pro.getProperty(param);
    }
    public static void writeProperties(String p_key,String p_value) throws Exception{
        String filePath= PropertiesUtils.class.getClassLoader().getResource("").getPath() + "config.properties";
        PropertiesUtils.class.getClassLoader().getResource("").getPath();

        writeProperties(filePath,p_key,p_value);
    }
    public static void writeProperties(String p_path,String p_key,String p_value) throws Exception{
//		String filePath= "c:\\c.properties";
        Properties props = new Properties();
        InputStream is = new FileInputStream(p_path);

        props.load(is);

        OutputStream os = new FileOutputStream(p_path);
        props.setProperty(p_key,p_value);
        props.store(os, "append");
        os.close();
    }

    public static Properties loadPropertyByName(String fileName) throws Exception{
        Properties property = new Properties();
        InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
        property.load(is);
        return property;
    }

    public static String getPropertyValueFromFile(String fileName,String param) throws Exception{
        Properties p = PropertiesUtils.loadPropertyByName(fileName);
        String value = p.getProperty(param);
        value = new String(value.getBytes("ISO8859-1"),"UTF-8");
        return value;
    }

//	public static String loadXMLByName(String fileName) throws Exception{
//		InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
//		Document doc = Dom4jUtils.createDocumentFromFile(is);
//		return Dom4jUtils.toString(doc);
//	}

    public static Map getPropertiesValueByRoot(String root) throws Exception{
        Properties p = PropertiesUtils.loadProperty();
        if (root == null || root.length() == 0){
            return null;
        }
        Map map = new HashMap();
        for (Enumeration e = p.propertyNames(); e.hasMoreElements();){
            String param = (String)e.nextElement();
            if (param.indexOf(root+".") == 0){
                map.put(param.substring(root.length()+1), p.getProperty(param));
            }
        }
        return map;
    }
    public static String getPropertiesValueByRootToString(String root) throws Exception{
        Properties p = PropertiesUtils.loadProperty();
        if (root == null || root.length() == 0){
            return null;
        }
        String value = "";
        for (Enumeration e = p.propertyNames(); e.hasMoreElements();){
            String param = (String)e.nextElement();
            if (param.indexOf(root+".") == 0){
                value = value + param.substring(root.length()+1) + "=" + p.getProperty(param) + ",";
            }
        }
        value = value.substring(0,value.length()-1);
        return value;
    }

    public static void main(String[] args) throws Exception{
        //writeProperties("gg","2");
//		System.out.println(PropertiesUtils.class.getClassLoader().getResource("").getPath());
        System.out.println(PropertiesUtils.getPropertyValueFromFile("PSRType.properties", "0001000B0"));
    }
}
