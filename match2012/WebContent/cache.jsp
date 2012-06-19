
<%@page import="java.util.List"%><%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	out.println("TotalMemory :::: " + (Runtime.getRuntime().totalMemory()/(1024*1024) + "M")+"<br />");  
	out.println("Max Memory :::: " + (Runtime.getRuntime().maxMemory()/(1024*1024) + "M")+"<br />");  
	out.println("Free Memory :::: " + (Runtime.getRuntime().freeMemory()/(1024*1024) + "M")+"<br />"); 
%>
