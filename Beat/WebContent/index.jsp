<%@page import="com.business.kp.utill.BeanPopulate"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.business.kp.utill.RegistraionDetailsBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="jquery-latest.js"></script> 

<link rel="stylesheet" href="themes/green/style.css" type="text/css" media="print, projection, screen" />
<script type="text/javascript" src="jquery.tablesorter.js"></script> 
<script type="text/javascript">
$(document).ready(function() 
    { 
        $("#myTable").tablesorter(); 
    } 
); 
</script>

</head>
<body>
<table id="myTable" class="tablesorter" align="center"> 
<thead> 
<tr> 
    <th>Tag ID</th> 
    <th>Location Name</th> 
    <th>longitude</th> 
    <th>latitude</th> 
    <th>CreatedTime</th> 
    <th>Mobile Id</th> 
</tr> 
</thead> 
<tbody> 
<%

List<RegistraionDetailsBean> datalist=BeanPopulate.getBeanList();

System.out.println("size"+datalist.size());
if(datalist.size()>0){
for(RegistraionDetailsBean bean:datalist){
	
	

%>
<tr> 
<td><%=bean.getTag()%></td> 
    <td><%=bean.getLocationName() %></td> 
    <td><%=bean.getLattitude()%></td> 
    <td><%=bean.getLognitude()%></td> 
    <td><%=bean.getCreatedtime()%></td> 
     <td><%=bean.getDeviceid()%></td>
</tr>
<%} }%>
</tbody> 
</table>
</body>
</html>