<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="loginCheck.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
<head>
	<meta name="renderer" content="webkit|ie-comp|ie-stand">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<jsp:include page="header.jsp" flush="true"/>
	<title>自动化部署平台</title>
	<script language="javascript" type="text/javascript">
		//操作
		function CheckInput() {
			$("#submits").submit();
		}
	</script>
</head>

<body>
<!--header start-->
  <div class="header">
  	<jsp:include page="topinfo.jsp" flush="true"/>
  </div>
<!--header end--> 

 
<!--content start-->
  <div class="content">
  	<div class="breadcrumb">
      <a href="getAllInstance?type=db2ha" title="既有虚机+DB2HA" class="tip-bottom"><i class="icon-home"></i>既有虚机+DB2HA</a>
      <a href="#" class="current">DB安装信息确认</a>
    </div>
    <div class="container-fluid">
      <div class="row-fluid">
            <div class="span12">
              <div class="widget-box collapsible">
                  <div class="widget-title">
                      <a data-toggle="collapse" href="#collapseOne">
                          <span class="icon">
                              <i class="icon-arrow-right"></i>
                            </span>
                            <h5>说明：</h5>
                        </a>
                    </div>
                    <div id="collapseOne" class="collapse in">
                        <div class="widget-content">数据库安装确认页面</div>
                    </div>
                </div>
            </div>
      </div>
    </div>
		<div class="container-fluid">
        <div class="row-fluid">
          <div class="span12">
          	<div class="columnauto">
              <div class="mainmodule">
                <h5 class="stairtit" style="margin-left:10px;">拓扑结构</h5>
                <c:forEach items="${servers }" var="ser" varStatus="num">
                <p class="twotit"><em class="majornode">主</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;<b>serviceIP1：</b><span>255.255.255.255</span></p>
                <div class="column">
                  <div class="span12">
                     <p>
                      <b>主机名:</b><span class="column_txt">${ser.name }</span>
                      <b>IP地址:</b><span class="column_txtl"><c:forEach items="${ser.address}" var="addr">${addr.addr }&nbsp;&nbsp;</c:forEach></span>
                      <b>镜像:</b><span span class="column_txt2"><c:forEach items="${imageList }" var="img"><c:if test="${ser.image.id eq img.id }">${img.name }</c:if></c:forEach></span>
                      <b>资源组：</b><span class="mr10">${asName }</span>
                    </p>
                    <p>
                      <b>主机风格:</b><span class="column_txt"><c:forEach items="${flavors }" var="flav"><c:if test="${ser.flavor.id eq flav.id }">${flav.vcpus }C/${flav.ram }MB/${flav.disk }GB</c:if></c:forEach></span>
                      <b>挂卷:</b><span class="column_txtl">volume_HB , volume_Data</span>
                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
                      <b>VG信息：</b><span class="mr10">${vgName }</span>
                    </p>
                  </div>
                </div>
                </c:forEach>
              </div>
              <div class="mainmodule">
              <form action="toNextPage?status=db2haConfirm" id="submits" method="post" class="form-horizontal processInfo">
              	<input type="hidden" id="serId" name="serId" value="${serId }">
				<!-- HOST Begin -->
				<input type="hidden" id="haname" name="haname" value="${haname}">
				<input type="hidden" id="hostName" name="hostName" value="${hostName}">
				<input type="hidden" id="hostIp" name="hostIp" value="${hostIp }">
				<input type="hidden" id="serName" name="serName" value="${serName }">
				<input type="hidden" id="serIp" name="serIp" value="${serIp }">
				<input type="hidden" id="perName" name="perName" value="${perName }">
				<input type="hidden" id="perIp" name="perIp" value="${perIp }">
				<!-- HOST End -->
				<!-- VG BEGIN -->
				<input type="hidden" id="hdiskname" name="hdiskname" value="${hdiskname }">
				<input type="hidden" id="hdiskid" name="hdiskid" value="${hdiskid }">
				<input type="hidden" id="vgtype" name="vgtype" value="${vgtype }">
               	<!-- VG END -->
				<!-- DB2 BEGIN -->
               	<input type="hidden" id="dbPath" name="dbPath" value="${dbPath}">
				<input type="hidden" id="dataPath" name="dataPath" value="${dataPath }">
				<input type="hidden" id="userPath" name="userPath" value="${userPath }">
				<input type="hidden" id="dataName" name="dataName" value="${dataName }">
				<input type="hidden" id="installUname" name="installUname" value="${installUname }">
				<input type="hidden" id="portNum" name="portNum" value="${portNum }">
				<input type="hidden" id="dasgrp" name="dasgrp" value="${dasgrp }">
				<!-- DB2 END -->
					
                <div class="control-group">
                  <label class="control-label">DB2安装目录</label>
                  <div class="controls">
                    <span class="graytxt">${dbPath }</span>
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label">DB2数据目录</label>
                  <div class="controls">
                    <span class="graytxt">${dataPath }</span>
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label">DB2用户目录</label>
                  <div class="controls">
                    <span class="graytxt">${userPath }</span>
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label">DB2数据库名</label>
                  <div class="controls">
                    <span class="graytxt">${dataName }</span>
                  </div>
                </div>
                 <div class="control-group">
                  <label class="control-label">DB2安装用户名</label>
                  <div class="controls">
                    <span class="graytxt">${installUname }</span>
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label">DB2端口号</label>
                  <div class="controls">
                    <span class="graytxt">${portNum }</span>
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label">DB2分组</label>
                  <div class="controls">
                    <span class="graytxt">${dasgrp }</span>
                  </div>
                </div>
              </form>
              </div>
            </div> 
          </div>
        </div>
      </div>
  </div>
<!--content end-->

<!--footer start-->
  <div class="columnfoot">
  	<a class="btn btn-info btn-up" onclick="javascript:history.go(-1);">
      <i class="icon-btn-up"></i>
      <span>上一页</span>
    </a>
   	<a class="btn btn-info fr btn-down" onclick="CheckInput();">
      <span>下一页</span>
      <i class="icon-btn-next"></i>
    </a>
  </div>  
<!--footer end-->

</body>
</html>
