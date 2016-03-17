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
	                <p class="twotit" style="padding-left: 0px;">节点<c:out  value="${num.count }"/></p>
	                <div class="column">
	                  <div class="span12">
	                    <p>
	                      <b>主机名:</b><span class="column_txt">${ser.name }</span>
	                      <b>IP地址:</b><span class="column_txt"><c:forEach items="${ser.address}" var="addr">${addr.addr }&nbsp;&nbsp;</c:forEach></span>
	                      <b>镜像:</b><span><c:forEach items="${imageList }" var="img"><c:if test="${ser.image.id eq img.id }">${img.name }</c:if></c:forEach></span>
	                    </p>
	                    <p>
	                      <b>主机风格:</b><span class="column_txt"><c:forEach items="${flavors }" var="flav"><c:if test="${ser.flavor.id eq flav.id }">${flav.vcpus }C/${flav.ram }MB/${flav.disk }GB</c:if></c:forEach></span>
	                      <b>挂卷:</b><span class="column_txt">volume_HB , volume_Data</span>
	                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
	                    </p>
	                  </div>
	                </div>
                </c:forEach>
                
                <div class="mainmodule">
                <h5>资源组(rg_hln01)</h5>
                <form action="toNextPage?status=installPage" method="post" id="submits" class="form-horizontal processInfo">
                  <input type="hidden" id="serId" name="serId" value="${serId }">
                  <input type="hidden" id="vgName" name="vgName" value="${vgName }">
                  <input type="hidden" id="disks" name="disks" value="${disks }">
                  <input type="hidden" id="fsName" name="fsName" value="${fsName }">
                  <input type="hidden" id="types" name="types" value="${types }">
                  <input type="hidden" id="vgName2" name="vgName2" value="${vgName2 }">
                  <input type="hidden" id="disks2" name="disks2" value="${disks2 }">
                  <input type="hidden" id="fsName2" name="fsName2" value="${fsName2 }">
                  <input type="hidden" id="types2" name="types2" value="${types2 }">
                  <input type="hidden" id="asName" name="asName" value="${asName }">
                  <input type="hidden" id="startScript" name="startScript" value="${startScript }">
                  <input type="hidden" id="stopScript" name="stopScript" value="${stopScript }">
                  <input type="hidden" id="node" name="node" value="${node }">
                  <input type="hidden" id="ip0" name="ip0" value="${ip0 }">
                  <input type="hidden" id="ip1" name="ip1" value="${ip1 }">
                  <input type="hidden" id="ip2" name="ip2" value="${ip2 }">
                  <input type="hidden" id="ip3" name="ip3" value="${ip3 }">
                  <p class="twotit">资源组共享VG信息</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      <div class="inputb4"><span class="graytxt">${vgName }</span></div>
                      <div class="inputb4">
                        <span class="graytxt">${disks }</span>
                      </div>
                      <div class="inputb4">
                        <span class="graytxt">${fsName }</span>
                      </div>
                      <div class="inputb4">
                        <span class="graytxt">${types }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      <div class="inputb4"><span class="graytxt">${vgName2 }</span></div>
                      <div class="inputb4">
                        <span class="graytxt">${disks2 }</span>
                      </div>
                      <div class="inputb4">
                        <span class="graytxt">${fsName2 }</span>
                      </div>
                      <div class="inputb4">
                        <span class="graytxt">${types2 }</span>
                      </div>
                    </div>
                  </div>
                  <p class="twotit">资源组AS应用程序信息</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      <div class="inputb4">
                        <span class="graytxt">${asName }</span>
                      </div>
                      <div class="inputb4">
                        <span class="graytxt">${startScript }</span>
                      </div>
                      <div class="inputb4">
                        <span class="graytxt">${stopScript }</span>
                      </div>
                    </div>
                  </div>
                  <p class="twotit">资源组主节点</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      <div class="inputb4">
                        <span class="graytxt">${node }</span>
                      </div>
                    </div>
                  </div>
                  <p class="twotit">资源组serviceIP信息</p>
                  <div class="control-group groupborder mb0">
                    <label class="control-label c-lmini"></label>
                    <div class="controls controls-mini">
                      <div class="inputb4">
                        <span class="graytxt">${ip0}.${ip1}.${ip2}.${ip3}</span>
                      </div>
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
        <span>下一页</span>
      <i class="icon-btn-next"></i>
    </a>
  </div> 
<!--footer end-->

</body>
</html>
