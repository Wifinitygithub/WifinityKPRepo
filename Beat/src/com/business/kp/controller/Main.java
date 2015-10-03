package com.business.kp.controller;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.business.kp.utill.BeanPopulate;
import com.business.kp.utill.MysqlConnector;
import com.business.kp.utill.RegistraionDetailsBean;

public class Main {
  public static void main(String arg[]) throws Exception{
    String xmlRecords = "<data><employee><name>A</name>"
        + "<title>Manager</title></employee><employee><name>A</name>"
        + "<title>Manager</title></employee></data>";
    String l="<?xml version=";
   String s="?>";
    String q="encoding=";
    String name2 = l+"\"1.0\""+q+"\"utf-8\""+s;
    
   // String j="<?xml version="\"1.0\""+"encoding="\"utf-8\"";
    System.out.println(name2);
    String d=name2+"<NFC_TABLE><row><TAG_ID>1660883920</TAG_ID><MOBILE_ID>352302071763514</MOBILE_ID><LONGITUDE>77.6319907</LONGITUDE><LATITUDE>12.9345425</LATITUDE><LOCATION>tvpm</LOCATION><CURR_DATETIME>01-09-2015 11:56:21</CURR_DATETIME></row><row><TAG_ID>1660883920</TAG_ID><MOBILE_ID>352302071763514</MOBILE_ID><LONGITUDE>77.6319907</LONGITUDE><LATITUDE>12.9345425</LATITUDE><LOCATION>tvpm</LOCATION><CURR_DATETIME>01-09-2015 11:56:21</CURR_DATETIME></row></NFC_TABLE>";

    System.out.println(d);
    d=d.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
    
    System.out.println(d);
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(d));

    Document doc = db.parse(is);
    NodeList nodes = doc.getElementsByTagName("row");

    /*for (int i = 0; i < nodes.getLength(); i++) {
      Element element = (Element) nodes.item(i);

      NodeList name = element.getElementsByTagName("MOBILE_ID");
      Element line = (Element) name.item(0);
      System.out.println("Name: " + getCharacterDataFromElement(line));

      NodeList title = element.getElementsByTagName("LONGITUDE");
      line = (Element) title.item(0);
      System.out.println("Title: " + getCharacterDataFromElement(line));
    }*/
    for (int i = 0; i < nodes.getLength(); i++) {
        Element element = (Element) nodes.item(i);

        NodeList mobileId = element.getElementsByTagName("MOBILE_ID");
        Element line = (Element) mobileId.item(0);
        String mobileIdData=getCharacterDataFromElement(line);
        System.out.println("Name: " + getCharacterDataFromElement(line));

        NodeList tagid = element.getElementsByTagName("TAG_ID");
        line = (Element) tagid.item(0);
        String NfcTag=getCharacterDataFromElement(line);
        
        NodeList lognitude = element.getElementsByTagName("LONGITUDE");
        line = (Element) lognitude.item(0);
        String lognitudeData=getCharacterDataFromElement(line);
        
        
        NodeList lattitude = element.getElementsByTagName("LATITUDE");
        line = (Element) lattitude.item(0);
        String lattitudeData=getCharacterDataFromElement(line);
        
        NodeList location = element.getElementsByTagName("LOCATION");
        line = (Element) location.item(0);
        String locationData=getCharacterDataFromElement(line);
        NodeList dateTime = element.getElementsByTagName("CURR_DATETIME");
        line = (Element) dateTime.item(0);
        String dateTimeData=getCharacterDataFromElement(line);
        SimpleDateFormat dateformat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        
        Date date=dateformat.parse(dateTimeData);
        
        SimpleDateFormat dateformat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateFormated=dateformat2.format(date);
        Connection con=MysqlConnector.getConnection();
        try{
      	 
      	 String query="insert into loaction_registration(location_name,longitude_map,latitude,device_id,status,createdtime) values('"+locationData+"','"+lognitudeData+"','"+lattitudeData+"','"+mobileIdData+"','0','"+dateFormated+"')";
      	  Statement statement=con.createStatement();
      	  int exec=statement.executeUpdate(query);
      	  
      	 if(exec>0){
   		  
   		  RegistraionDetailsBean bean=new RegistraionDetailsBean();
   		  bean.setCreatedtime(dateFormated);
   		  bean.setDeviceid(mobileIdData);
   		  bean.setLattitude(lattitudeData);
   		  bean.setLognitude(lognitudeData);
   		  bean.setTag(NfcTag);
   		  bean.setLocationName(locationData);
   		  BeanPopulate.getBeanList().add(bean);
   		  System.out.println(BeanPopulate.getBeanList().size());
   	  }
      	  
      	  
      	  
      	  
      	  
      	  
        }catch(Exception e){
      	  e.printStackTrace();
        }finally{
      	  try{
      		  con.close();
      	  }catch(Exception e){
      		  
      	  }
        }}


  }

  public static String getCharacterDataFromElement(Element e) {
    Node child = e.getFirstChild();
    if (child instanceof CharacterData) {
      CharacterData cd = (CharacterData) child;
      return cd.getData();
    }
    return "";
  }
}