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
    <a href="getAllInstance?type=db2ha" title="IBM DB2HA" class="tip-bottom"><i class="icon-home"></i>IBM DB2HA</a>
    <a href="#" class="current">配置确认</a>
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
                      <div class="widget-content">实例信息配置确认页.</div>
                  </div>
              </div>
          </div>
    </div>
  </div>
	<div class="container-fluid">
        <div class="row-fluid">
          <div class="span12">
            <div class="columnauto">
              <h5>配置当前虚拟机的HA信息</h5>
              <div class="mainmodule">
                <h5 class="stairtit">拓扑结构</h5>
                <c:forEach items="${servers }" var="ser" varStatus="num">
	                <p class="twotit">节点<c:out  value="${num.count }"/></p>
	                <div class="column">
	                  <div class="span12">
	                    <p>
	                      <b>主机名:</b><span class="column_txt">${ser.name }</span>
	                      <b>IP地址:</b><span class="column_txtl"><c:forEach items="${ser.address}" var="addr">${addr.addr }&nbsp;&nbsp;</c:forEach></span>
	                      <b>镜像:</b><span><c:forEach items="${imageList }" var="img"><c:if test="${ser.image.id eq img.id }">${img.name }</c:if></c:forEach></span>
	                    </p>
	                    <p>
	                      <b>主机风格:</b><span class="column_txt"><c:forEach items="${flavors }" var="flav"><c:if test="${ser.flavor.id eq flav.id }">${flav.vcpus }C/${flav.ram }MB/${flav.disk }GB</c:if></c:forEach></span>
	                      <b>挂卷:</b><span class="column_txtl">volume_HB , volume_Data</span>
	                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
	                    </p>
	                  </div>
	                </div>
                </c:forEach>
                
                <div class="mainmodule">
                <h5></h5>
                <form action="installDb2haInfo" method="post" id="submits" class="form-horizontal processInfo">
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
                  <p class="twotit">ha_setup.lst</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      ${hasetup }
                    </div>
                  </div>
                  <p class="twotit">mkvg.lst</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                     	${mkvg }
                    </div>
                  </div>
                  <p class="twotit">importvg.lst</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      ${importvg }
                    </div>
                  </div>
                </form>
                </div>
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
    <a class="btn btn-info fr btn-next" onclick="CheckInput();">
        <span>创建</span>
      <i class="icon-btn-next"></i>
    </a>
  </div> 
<!--footer end-->

</body>
</html>
