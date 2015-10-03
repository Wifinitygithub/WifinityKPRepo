package com.business.kp.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

public class RegisterLocation extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println(request.getParameter("id"));
		System.out.println(request);
		String xml = null;
        try {
                byte[] xmlData = new byte[request.getContentLength()];

               
                InputStream sis = request.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(sis);

                bis.read(xmlData, 0, xmlData.length);

                if (request.getCharacterEncoding() != null) {
                        xml = new String(xmlData, request.getCharacterEncoding());
                        
                        xml=xml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
                        System.out.println(xml);
                        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        InputSource is = new InputSource();
                        is.setCharacterStream(new StringReader(xml));

                        Document doc = db.parse(is);
                        NodeList nodes = doc.getElementsByTagName("row");

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
                        		  
                        	  }
                          }catch(Exception e){
                        	  
                          }finally{
                        	  try{
                        		  con.close();
                        	  }catch(Exception e){
                        		  
                        	  }
                          }
                          
                          System.out.println("Title: " + getCharacterDataFromElement(line));
                        }
                } else {
                        xml = new String(xmlData);
                }
                
        } catch (Exception ioe) {
          
        }
		
		
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
