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
		function CheckInput() {
			/* var disks = $("#disks").val();
		    var disks2 = $("#disks2").val();
		    if(disks == null || disks ==""){
		    	ymPrompt.alert("请选择HDisk!");
		    	return;
		    }
			if(disks2 == null || disks2 ==""){
		    	ymPrompt.alert("请选择HDisk!");
		    	return;
		    }
		    if(disks !="" && disks !=null && disks2 != null && disks2 !=""){
		    	if(disks == disks2){
		    		ymPrompt.alert("请选择不同的HDisk!");
		    		return;
		    	}else{
		    		$("#submits").submit();
		    	}
		    } */
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
	    <a class="current">实例配置详细</a>
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
	                      <div class="widget-content">实例信息配置一览.</div>
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
                  <h5>资源组信息</h5>
                  <form action="toNextPage?status=configConfirm" method="post" id="submits" class="form-horizontal processInfo">
                  	<input type="hidden" id="serId" name="serId" value="${serId }">
                     
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini">心跳盘选择</label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                         <select class="w80" id="lspvVolume" name="lspvVolume">
                              <c:forEach items="${allHdisk }" var="hd">
                              	 <option value="${hd.hdName}">${hd.hdName}&nbsp;&nbsp;&nbsp;&nbsp;${hd.hdType}</option>
                              </c:forEach>
                         </select>
                        </div>
                        <div class="inputb4">
                        	<input type="text" id="vgName" name="vgName" value="AA" readonly="readonly"/>
                        </div>
                        <div class="inputb4">
                        	<input type="text" id="vgName" name="vgName" value="rg_hln01" readonly="readonly"/>
                        </div>
                      </div>
                    </div>
                    
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini">共享VG信息</label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                        	<input type="text" id="vgName" name="vgName" placeholder="rg-hlno1" readonly="readonly"/>
                        </div>
                        <div class="inputb4">
                          <select class="w80" id="types" name="types">
                              <option multiple="" value="enhance currency big VG1">host1</option>
                              <option value="enhance currency big VG2">host2</option>
                              <option value="enhance currency big VG3">host3</option>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80" id="disks" name="disks">
                              <option multiple="" value="serverIp">serverIp</option>
                              <option value="serverIp">serverIp</option>
                              <option value="serverIp">serverIp</option>
                              <option value="serverIp">serverIp</option>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select multiple="" class="w80">
                              <option selected="">volume1</option>
                              <option>volume2</option>
                              <option>volume3</option>
                              <option>volume4</option>
                          </select>
                        </div>
                      </div>
                    </div>
                     
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini">AS应用程序</label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input type="text" id="asName" name="asName" placeholder="AS名称1" value="ap01" readonly="readonly"/></div>
                        <div class="inputb4">
                          <input type="text" class="w76" id="startScript" name="startScript" placeholder="启动脚本start/sh" value="/script/start.sh"/>
                        </div>
                        <div class="inputb4">
                          <input type="text" class="w76" id="stopScript" name="stopScript" placeholder="停止脚本stop/sh" value="/script/stop.sh"/>
                        </div>
                      </div>
                    </div>
                    
                    <!-- <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini">serviceIP信息</label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                          <div class="iPutsimul">
                            <input type="text" id="ip0" name="ip0" value="20" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()" onpaste="paste('ip')"/>.
                            <input type="text" id="ip1" name="ip1" value="211" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()"/>.
                            <input type="text" id="ip2" name="ip2" value="30" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()"/>.
                            <input type="text" id="ip3" name="ip3" value="245" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()"/>
                          </div>
                        </div>
                      </div>
                    </div> -->
                  </form>
                </div>
                <!-- <div class="columnblock" id="resourceGroup" style="display:none;">
                  <h5>资源组2(名称)</h5>
                  <form action="#" method="get" class="form-horizontal processInfo">
                    <p class="twotit">资源组共享VG信息</p>
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input type="text" value="VG名称1"/></div>
                        <div class="inputb4">
                          <select multiple="" class="w80">
                              <option selected="">HDisk1</option>
                              <option>HDisk2</option>
                              <option>HDisk3</option>
                              <option>HDisk4</option>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80">
                              <option multiple="">FS/RAW</option>
                              <option>FS/RAW2</option>
                              <option>FS/RAW2</option>
                              <option>FS/RAW2</option>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80">
                              <option multiple="">方式</option>
                              <option>enhance currency big VG</option>
                              <option>enhance currency big VG</option>
                              <option>enhance currency big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input type="text" value="VG名称1"/></div>
                        <div class="inputb4">
                          <select multiple="" class="w80">
                              <option selected="">HDisk1</option>
                              <option>HDisk2</option>
                              <option>HDisk3</option>
                              <option>HDisk4</option>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80">
                              <option multiple="">FS/RAW</option>
                              <option>FS/RAW2</option>
                              <option>FS/RAW2</option>
                              <option>FS/RAW2</option>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80">
                              <option multiple="">方式</option>
                              <option>enhance currency big VG</option>
                              <option>enhance currency big VG</option>
                              <option>enhance currency big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <p class="twotit">资源组AS应用程序信息</p>
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input type="text" value="AS名称1"/></div>
                        <div class="inputb4">
                          <input type="text" class="w76" value="启动脚本start/sh"/>
                        </div>
                        <div class="inputb4">
                          <input type="text" class="w76" value="停止脚本stop/sh"/>
                        </div>
                      </div>
                    </div>
                    <p class="twotit">资源组主节点</p>
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                          <select class="w85"><option>节点1</option><option>节点2</option></select>
                        </div>
                      </div>
                    </div>
                    <p class="twotit">资源组serviceIP信息</p>
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                          <div class="iPutsimul">
                            <input type="text" id="ip0" value="20" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()" onpaste="paste('ip')"/>.
                            <input type="text" id="ip1" value="211" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()"/>.
                            <input type="text" id="ip2" value="30" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()"/>.
                            <input type="text" id="ip3" value="245" maxlength="3" class="iPsimul" onKeyUp="mask(this)" onbeforepaste="mask_c()"/>
                          </div>
                        </div>
                      </div>
                    </div>
                  </form>
                </div> -->
                <!-- <p class="mt10 overhid">
                  <a class="btn btn-info fr" href="#" id="Additional">
                    <span>添加资源组RG</span>
                  </a>
                </p> -->
              </div>
            </div>   
          </div>
        </div>
      </div>
  </div>
  <div class="columnfoot" style="position:fixed;">
    <a class="btn btn-info btn-up" onclick="javascript:history.go(-1);">
      <i class="icon-btn-up"></i>
      <span>上一页</span>
    </a>
    <a class="btn btn-info fr btn-next" onclick="CheckInput();">
        <span>下一页</span>
      <i class="icon-btn-next"></i>
    </a>
  </div>
<!--content end-->

</body>
</html>
