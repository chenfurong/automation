<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html>
<head>
	<meta name="renderer" content="webkit|ie-comp|ie-stand">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<jsp:include page="../header.jsp" flush="true"/>
	<title>自动化部署平台</title>
</head>

<body>
<!--header start-->
  <div class="header">
  	<jsp:include page="../topinfo.jsp" flush="true"/>
  </div>
<!--header end-->  

<!--content start-->
  <div class="content">
  	<div class="warnning">
    <p class="warningtxt"><b>异常提醒：</b>${message}</p>
    </div>
  </div>
<!--content end-->

<!--footer start-->
  <div class="columnfoot">
    <a class="btn btn-info fr btn-next" onclick="javascript:history.go(-1);">
      <span>返回主页</span>
      <i class="icon-btn-next"></i>
    </a>
  </div>  
<!--footer end-->
</body>
</html>
