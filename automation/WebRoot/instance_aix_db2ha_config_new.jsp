<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="loginCheck.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.fasterxml.jackson.databind.*"%>
<%@ page import="com.fasterxml.jackson.databind.node.*"%>
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
			
			
			if ($("#db2_db2insusr").val().trim() == "") {
				ymPrompt.alert("请输入DB2实例名!");
				return;
			}
			
			if ($("#db2_svcename").val().trim() == "") {
				ymPrompt.alert("请输入DB2监听端口!");
				return;
			}
			
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
	    <a href="getAllServers" title="IBM DB2HA" class="tip-bottom"><i class="icon-home"></i>IBM DB2HA</a>
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
                      <div class="widget-content">配置当前虚拟机的DB2HA参数信息</div>
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
                 <h5 class="stairtit swapcon"><!-- <i class="icon-chevron-down"></i> -->拓扑结构</h5>
                <c:forEach items="${servers }" var="ser" varStatus="num">
                <p class="twotit">
                	<c:forEach items="${ser.IP}" var="addr">
                   	  <c:if test="${ha_ip1==addr}">
                   	  	<c:if test="${ha_hostname1==ha_primaryNode}">
			                <em class="majornode">主</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;
               	        </c:if>
               	        <c:if test="${ha_hostname1!=ha_primaryNode}">
			                <em class="vicenode">备</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;
               	        </c:if>
               	      </c:if>
               	      <c:if test="${ha_ip2==addr}">
               	      	<c:if test="${ha_hostname2==ha_primaryNode}">
			                <em class="majornode">主</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;
               	        </c:if>
               	        <c:if test="${ha_hostname2!=ha_primaryNode}">
			                <em class="vicenode">备</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;
               	        </c:if>
               	      </c:if>
                    </c:forEach>
	               <b>serviceIP：</b><span>${ha_svcip }</span> 
                </p>
                <div class="column">
                  <div class="span12">
                     <p>
                      <b>主机名:</b>
                      <span class="column_txt">
	                      <c:forEach items="${ser.IP}" var="addr">
	                     	  <c:if test="${ha_ip1==addr}">
	                    	  	${ha_hostname1 }
	                 	      </c:if>
	                 	      <c:if test="${ha_ip2==addr}">
	                    	  	${ha_hostname2 }
	                 	      </c:if>
	                      </c:forEach>
					  </span>
                      <b>IP地址:</b><span class="column_txtl">${ser.IP }</span>
              	<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
                      <b>资源组：</b><span class="mr10">${ha_RGNmae }</span>
                    </p>
                    <p>
                        <b>系统配置:</b><span class="column_txt">${ser.HConf }</span>
                      <b>挂卷:</b><span class="column_txtl">volume_HB , volume_Data</span>
                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
                      <b>AS名称：</b><span class="mr10">${ha_ASName }</span>
                    </p>
                  </div>
                </div>
                </c:forEach>
              </div>
               <form action="toNextPage?status=installConfirmNew" method="post" id="submits" class="form-horizontal">
             	    <input type="hidden" id="serId" name="serId" value="${serId }">
             	    
             	    <!-- 基本 Begin -->
					<input type="hidden" id="haname" name="haname" value="${haname}">
					<input type="hidden" id="ha_RGNmae" name="ha_RGNmae" value="${ha_RGNmae}">
					<input type="hidden" id="ha_ASName" name="ha_ASName" value="${ha_ASName}">
					<input type="hidden" id="ha_primaryNode" name="ha_primaryNode" value="${ha_primaryNode}">
					<!-- 基本 End -->
	                <input id="ptype" type="hidden" class="w45" name="ptype" value="${ptype }" />
					<!-- IP Begin -->
					<input type="hidden" id="hostId1" name="hostId1" value="${hostId1}">
                  	<input type="hidden" id="hostId2" name="hostId2" value="${hostId2}">
					<input type="hidden" id="ha_ip1" name="ha_ip1" value="${ha_ip1}">
					<input type="hidden" id="ha_ip2" name="ha_ip2" value="${ha_ip2}">
					<input type="hidden" id="ha_bootip1" name="ha_bootip1" value="${ha_bootip1}">
					<input type="hidden" id="ha_bootip2" name="ha_bootip2" value="${ha_bootip2}">
					<input type="hidden" id="ha_svcip" name="ha_svcip" value="${ha_svcip}">
					<!-- IP End -->
	
					<!-- 主机别名 Begin -->
					<input type="hidden" id="ha_hostname1" name="ha_hostname1" value="${ha_hostname1}">
					<input type="hidden" id="ha_hostname2" name="ha_hostname2" value="${ha_hostname2}">
					<input type="hidden" id="ha_bootalias1" name="ha_bootalias1" value="${ha_bootalias1}">
					<input type="hidden" id="ha_bootalias2" name="ha_bootalias2" value="${ha_bootalias2}">
					<input type="hidden" id="ha_svcalias" name="ha_svcalias" value="${ha_svcalias}">
					<!-- 主机别名 End -->
					
					<!-- HOST Begin -->
					<input type="hidden" id="hostName" name="hostName" value="${hostName}">
					<input type="hidden" id="hostIp" name="hostIp" value="${hostIp }">
					<input type="hidden" id="serName" name="serName" value="${serName }">
					<input type="hidden" id="serIp" name="serIp" value="${serIp }">
					<input type="hidden" id="perName" name="perName" value="${perName }">
					<input type="hidden" id="perIp" name="perIp" value="${perIp }">
					<!-- HOST End -->
					
					<!--old VG BEGIN -->
					<input type="hidden" id="hdiskname" name="hdiskname" value="${hdisknames }">
					<input type="hidden" id="hdiskid" name="hdiskid" value="${hdiskids }">
					<input type="hidden" id="vgtype" name="vgtype" value="${vgtypes }">
                  	<!--old VG END --> 
                  	
                  	<!-- VG BEGIN -->
                  	<input type="hidden" id="ha_vgdb2home" name="ha_vgdb2home" value="${ha_vgdb2home }">
                  	<input type="hidden" id="ha_vgdb2log" name="ha_vgdb2log" value="${ha_vgdb2log }">
                  	<input type="hidden" id="ha_vgdb2archlog" name="ha_vgdb2archlog" value="${ha_vgdb2archlog }">
                  	<%-- <input type="hidden" id="ha_vgdb2backup" name="ha_vgdb2backup" value="${ha_vgdb2backup }"> --%>
                  	<input type="hidden" id="ha_vgdataspace1" name="ha_vgdataspace" value="${ha_vgdataspace }">
                  	<%-- <input type="hidden" id="ha_vgdataspace2" name="ha_vgdataspace2" value="${ha_vgdataspace2 }">
                  	<input type="hidden" id="ha_vgdataspace3" name="ha_vgdataspace3" value="${ha_vgdataspace3 }">
                  	<input type="hidden" id="ha_vgdataspace4" name="ha_vgdataspace4" value="${ha_vgdataspace4 }"> --%>
                  	<input type="hidden" id="ha_vgcaap" name="ha_vgcaap" value="${ha_vgcaap }">
                  	<!-- VG END --> 
                  	
                  	<!-- PV BEGIN -->
                  	<input type="hidden" id="ha_db2homepv" name="ha_db2homepv" value="${ha_db2homepv }">
                  	<input type="hidden" id="ha_db2logpv" name="ha_db2logpv" value="${ha_db2logpv }">
                  	<input type="hidden" id="ha_db2archlogpv" name="ha_db2archlogpv" value="${ha_db2archlogpv }">
                  	<input type="hidden" id="ha_db2backuppv" name="ha_db2backuppv" value="${ha_db2backuppv }">
                  	<input type="hidden" id="ha_dataspace1pv" name="ha_dataspace1pv" value="${ha_dataspace1pv }">
                  	<input type="hidden" id="ha_dataspace2pv" name="ha_dataspace2pv" value="${ha_dataspace2pv }">
                  	<input type="hidden" id="ha_dataspace3pv" name="ha_dataspace3pv" value="${ha_dataspace3pv }">
                  	<input type="hidden" id="ha_dataspace4pv" name="ha_dataspace4pv" value="${ha_dataspace4pv }">
                  	<input type="hidden" id="ha_caappv" name="ha_caappv" value="${ha_caappv }">
                  	<!-- PV END -->
                  	
                  	<!-- VG 创建方式  BEGIN -->
                  	<input type="hidden" id="ha_db2homemode" name="ha_db2homemode" value="${ha_db2homemode }">
                  	<input type="hidden" id="ha_db2logmode" name="ha_db2logmode" value="${ha_db2logmode }">
                  	<input type="hidden" id="ha_db2archlogmode" name="ha_db2archlogmode" value="${ha_db2archlogmode }">
                  <%-- 	<input type="hidden" id="ha_db2backupmode" name="ha_db2backupmode" value="${ha_db2backupmode }"> --%>
                  	<input type="hidden" id="ha_dataspace1mode" name="ha_dataspacemode" value="${ha_dataspacemode }">
                  	<%-- <input type="hidden" id="ha_dataspace2mode" name="ha_dataspace2mode" value="${ha_dataspace2mode }">
                  	<input type="hidden" id="ha_dataspace3mode" name="ha_dataspace3mode" value="${ha_dataspace3mode }">
                  	<input type="hidden" id="ha_dataspace4mode" name="ha_dataspace4mode" value="${ha_dataspace4mode }"> --%>
                  	<input type="hidden" id="ha_caapmode" name="ha_caapmode" value="${ha_caapmode }">
                  	<!-- VG 创建方式 END -->
                  	
                  	<!-- HA切换策略 BEGIN -->
                  	<input type="hidden" id="ha_startpolicy" name="ha_startpolicy" value="${ha_startpolicy}">
                  	<input type="hidden" id="ha_fopolicy" name="ha_fopolicy" value="${ha_fopolicy}">
                  	<input type="hidden" id="ha_fbpolicy" name="ha_fbpolicy" value="${ha_fbpolicy}">
                  	<!-- HA切换策略 END -->
                  	
                  	<!-- NFS BEGIN -->
                  	<input type="hidden" id="ha_nfsON" name="ha_nfsON" value="${ha_nfsON}">
                  	
                  	<input type="hidden" id="ha_nfsIP1" name="ha_nfsIP1" value="${ha_nfsIP1}">
                  	<input type="hidden" id="ha_nfsSPoint1" name="ha_nfsSPoint1" value="${ha_nfsSPoint1}">
                  	<input type="hidden" id="ha_nfsCPoint1" name="ha_nfsCPoint1" value="${ha_nfsCPoint1}">
                  	<input type="hidden" id="ha_nfsIP2" name="ha_nfsIP2" value="${ha_nfsIP2}">
                  	<input type="hidden" id="ha_nfsSPoint2" name="ha_nfsSPoint2" value="${ha_nfsSPoint2}">
                  	<input type="hidden" id="ha_nfsCPoint2" name="ha_nfsCPoint2" value="${ha_nfsCPoint2}">
                  	<input type="hidden" id="ha_nfsIP3" name="ha_nfsIP3" value="${ha_nfsIP3}">
                  	<input type="hidden" id="ha_nfsSPoint3" name="ha_nfsSPoint3" value="${ha_nfsSPoint3}">
                  	<input type="hidden" id="ha_nfsCPoint3" name="ha_nfsCPoint3" value="${ha_nfsCPoint3}">
                  	<input type="hidden" id="ha_nfsIP4" name="ha_nfsIP4" value="${ha_nfsIP4}">
                  	<input type="hidden" id="ha_nfsSPoint4" name="ha_nfsSPoint4" value="${ha_nfsSPoint4}">
                  	<input type="hidden" id="ha_nfsCPoint4" name="ha_nfsCPoint4" value="${ha_nfsCPoint4}">
                  	<input type="hidden" id="ha_nfsIP5" name="ha_nfsIP5" value="${ha_nfsIP5}">
                  	<input type="hidden" id="ha_nfsSPoint5" name="ha_nfsSPoint5" value="${ha_nfsSPoint5}">
                  	<input type="hidden" id="ha_nfsCPoint5" name="ha_nfsCPoint5" value="${ha_nfsCPoint5}">
                  	
                  	<input type="hidden" id="ha_nfsIP6" name="ha_nfsIP6" value="${ha_nfsIP6}">
                  	<input type="hidden" id="ha_nfsSPoint6" name="ha_nfsSPoint6" value="${ha_nfsSPoint6}">
                  	<input type="hidden" id="ha_nfsCPoint6" name="ha_nfsCPoint6" value="${ha_nfsCPoint6}">
                  	<input type="hidden" id="ha_nfsIP7" name="ha_nfsIP7" value="${ha_nfsIP7}">
                  	<input type="hidden" id="ha_nfsSPoint7" name="ha_nfsSPoint7" value="${ha_nfsSPoint7}">
                  	<input type="hidden" id="ha_nfsCPoint7" name="ha_nfsCPoint7" value="${ha_nfsCPoint7}">
                  	<input type="hidden" id="ha_nfsIP8" name="ha_nfsIP8" value="${ha_nfsIP8}">
                  	<input type="hidden" id="ha_nfsSPoint8" name="ha_nfsSPoint8" value="${ha_nfsSPoint8}">
                  	<input type="hidden" id="ha_nfsCPoint8" name="ha_nfsCPoint8" value="${ha_nfsCPoint8}">
                  	<input type="hidden" id="ha_nfsIP9" name="ha_nfsIP9" value="${ha_nfsIP9}">
                  	<input type="hidden" id="ha_nfsSPoint9" name="ha_nfsSPoint9" value="${ha_nfsSPoint9}">
                  	<input type="hidden" id="ha_nfsCPoint9" name="ha_nfsCPoint9" value="${ha_nfsCPoint9}">
                  	<input type="hidden" id="ha_nfsIP10" name="ha_nfsIP10" value="${ha_nfsIP10}">
                  	<input type="hidden" id="ha_nfsSPoint10" name="ha_nfsSPoint10" value="${ha_nfsSPoint10}">
                  	<input type="hidden" id="ha_nfsCPoint10" name="ha_nfsCPoint10" value="${ha_nfsCPoint10}">
                  	
                  	<input type="hidden" id="ha_nfsIP11" name="ha_nfsIP11" value="${ha_nfsIP11}">
                  	<input type="hidden" id="ha_nfsSPoint11" name="ha_nfsSPoint11" value="${ha_nfsSPoint11}">
                  	<input type="hidden" id="ha_nfsCPoint11" name="ha_nfsCPoint11" value="${ha_nfsCPoint11}">
                  	<input type="hidden" id="ha_nfsIP12" name="ha_nfsIP12" value="${ha_nfsIP12}">
                  	<input type="hidden" id="ha_nfsSPoint12" name="ha_nfsSPoint12" value="${ha_nfsSPoint12}">
                  	<input type="hidden" id="ha_nfsCPoint12" name="ha_nfsCPoint12" value="${ha_nfsCPoint12}">
                  	<input type="hidden" id="ha_nfsIP13" name="ha_nfsIP13" value="${ha_nfsIP13}">
                  	<input type="hidden" id="ha_nfsSPoint13" name="ha_nfsSPoint13" value="${ha_nfsSPoint13}">
                  	<input type="hidden" id="ha_nfsCPoint13" name="ha_nfsCPoint13" value="${ha_nfsCPoint13}">
                  	<input type="hidden" id="ha_nfsIP14" name="ha_nfsIP14" value="${ha_nfsIP14}">
                  	<input type="hidden" id="ha_nfsSPoint14" name="ha_nfsSPoint14" value="${ha_nfsSPoint14}">
                  	<input type="hidden" id="ha_nfsCPoint14" name="ha_nfsCPoint14" value="${ha_nfsCPoint14}">
                  	<input type="hidden" id="ha_nfsIP15" name="ha_nfsIP15" value="${ha_nfsIP15}">
                  	<input type="hidden" id="ha_nfsSPoint15" name="ha_nfsSPoint15" value="${ha_nfsSPoint15}">
                  	<input type="hidden" id="ha_nfsCPoint15" name="ha_nfsCPoint15" value="${ha_nfsCPoint15}">
                  	<!-- NFS END -->
                  	
                  	<%
                    
                    String item = String.valueOf(request.getAttribute("basicInfo"));
                    
                    ObjectMapper om = new ObjectMapper();
           			ObjectNode node = (ObjectNode)om.readTree(item);
           		 	System.out.println("jsp::node:"+node);
           		
           			String db2_db2base = node.get("db2_db2base").asText();
           			String db2_dbpath = node.get("db2_dbpath").asText();
           			String db2_db2insusr = node.get("db2_db2insusr").asText();
           			String db2_svcename = node.get("db2_svcename").asText();
           			String db2_dbname = node.get("db2_dbname").asText();
           			String db2_dbdatapath = node.get("db2_dbdatapath").asText();
           			
           		
            		%>
            		
              <div class="mainmodule">
              	<h5 class="swapcon"><i class="icon-chevron-down"></i>基本信息</h5>
                <!-- <form action="#" method="get" class="form-horizontal"> -->
                 <div class="form-horizontal">
                  <div class="control-group">
                    <label class="control-label">DB2安装版本</label>
                    <div class="controls">
                      <div class="inblock mr20"><label><input type="radio" name="db2_version" value="v10.5" onClick="sortRadio(3)" />v10.5</label></div>
                      <div class="inblock mr20"><label><input type="radio" name="db2_version" value="v10.1" onClick="sortRadio(2)" checked/>v10.1</label></div>
                      <div class="inblock mr20"><label><input type="radio" name="db2_version" value="v9.7" onClick="sortRadio(1)" />v9.7</label></div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2安装路径</label>
                    <div class="controls">
                   		<input type="text" id="db2_db2base" name="db2_db2base" style="width:78%" value="<%=db2_db2base %>v10.1"  readonly="readonly"/>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2实例目录</label>
                    <div class="controls">
                      <input type="text" id="db2_dbpath" name="db2_dbpath" style="width:78%" value="<%=db2_dbpath %>" readonly="readonly"/>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2实例名</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" class="w45" id="db2_db2insusr" name="db2_db2insusr" value="<%=db2_db2insusr %>" />
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">监听端口</span>
                        <input type="text" class="w45" id="db2_svcename" name="db2_svcename" value="<%=db2_svcename %>" />
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2数据库名</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" style="margin-top:-20px;" class="w45" id="db2_dbname" name="db2_dbname" value="<%=db2_dbname %>" />
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">字符集</span>
                        <select id="styleSelect" class="w48" onChange="changeDivShow('styleSelect')" name="db2_codeset">
                        	<option value="GBK" selected="selected">GBK</option>
                          <option value="UTF-8">UTF-8</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2数据目录</label>
                    <div class="controls">
                      <input style="width:78%" type="text" id="db2_dbdatapath" name="db2_dbdatapath" value=${db2_dbdatapath } readonly="readonly"/> 
                    </div>
                  </div>
               <!--  </form> -->
              </div>
               </div>
              
              
              
               <%
                    
                    String item1 = String.valueOf(request.getAttribute("instProp"));
                    
                    ObjectMapper om1 = new ObjectMapper();
           			ObjectNode node1 = (ObjectNode)om1.readTree(item1);
           		 	System.out.println("jsp::node1:"+node1);
           		
           		 	
           			/* String db2insusr = node1.get("db2insusr").asText(); */
           			String db2_db2insgrp = node1.get("db2_db2insgrp").asText();
           			String db2_db2fenusr = node1.get("db2_db2fenusr").asText();
           			String db2_db2fengrp = node1.get("db2_db2fengrp").asText();
           			String db2_db2comm = node1.get("db2_db2comm").asText();
           			String db2_db2codepage = node1.get("db2_db2codepage").asText();
           			String db2_initagents = node1.get("db2_initagents").asText();
           			String db2_poolagents = node1.get("db2_poolagents").asText();
           			String db2_diagsize = node1.get("db2_diagsize").asText();
           			String db2_mon_heap = node1.get("db2_mon_heap").asText();
           			String db2_max_coordagents = node1.get("db2_max_coordagents").asText();
           			String db2_max_connectings = node1.get("db2_max_connectings").asText();
           		
            		%>
            		
              <div class="mainmodule">
              	<h5 class="swapcon"><i class="icon-chevron-down icon-chevron-right"></i>实例高级属性</h5>
               <!--  <form action="#" method="get" class="form-horizontal" style="display:none;"> -->
                <div class="form-horizontal" style="display:none;">
               
                  <div class="control-group">
                    <label class="control-label">实例用户</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_db2insusr" name="db2_db2insusr" value="<%=db2_db2insusr %>" readonly="readonly" class="w45"/>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">实例用户组</span>
                        <input type="text" id="db2_db2insgrp" name="db2_db2insgrp" value="<%=db2_db2insgrp %>" readonly="readonly" class="w45"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">fence用户</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_db2fenusr" class="w45" name="db2_db2fenusr" value="<%=db2_db2fenusr %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">fence用户组</span>
                        <input type="text" id="db2_db2fengrp" class="w45"  name="db2_db2fengrp" value="<%=db2_db2fengrp %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2COMM</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_db2comm" class="w45" name="db2_db2comm" value="<%=db2_db2comm %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DB2CODEPAGE</span>
                        <input type="text" id="db2_db2codepage" class="w45 codeVal" name="db2_db2codepage" value="<%=db2_db2codepage %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">NUM_INITAGENTS</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_initagents" class="w45" name="db2_initagents" value="<%=db2_initagents %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">NUM_POOLAGENTS</span>
                        <input type="text" id="db2_poolagents" class="w45" name="db2_poolagents" value="<%=db2_poolagents %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  
                  
                   <div class="control-group">
                    <label class="control-label">MAX_COORDAGENTS</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_max_coordagents" class="w45" name="db2_max_coordagents" value="<%=db2_max_coordagents %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">MAX_CONNECTIONS</span>
                        <input type="text" id="db2_max_connectings" class="w45" name="db2_max_connectings" value="<%=db2_max_connectings %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  
                  
                  <div class="control-group">
                    <label class="control-label">DIAGSIZE</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_diagsize" style="margin-top:-20px;" class="w45"name="db2_diagsize" value="<%=db2_diagsize %>" /></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_BUFPOOL</span>
                        <select class="w48" name="db2_mon_buf">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DFT_MON_LOCK</label>
                    <div class="controls">
                      <div class="inputb2l">
                      	<select class="w48" name="db2_mon_lock">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_SORT</span>
                        <select class="w48" name="db2_mon_sort">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DFT_MON_STMT</label>
                    <div class="controls">
                      <div class="inputb2l">
                      	<select class="w48" name="db2_mon_stmt">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_TABLE</span>
                        <select class="w48" name="db2_mon_table">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DFT_MON_UOW</label>
                    <div class="controls">
                      <div class="inputb2l">
                      	<select class="w48" name="db2_mon_uow">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">HEACTH_MON</span>
                        <select class="w48" name="db2_health_mon">
                        	<option value="ON">ON</option>
                          <option value="OFF" selected="selected">OFF</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MON_HEAP_SZ</label>
                    <div class="controls">
                      <input style="width:78%" type="text" id="db2_mon_heap" name="db2_mon_heap" value="<%=db2_mon_heap %>" readonly="readonly"/>
                    </div>
                  </div>
                </div>
              </div>
              
                     <%
                    
                    String item2 = String.valueOf(request.getAttribute("dbProp"));
                    
                    ObjectMapper om2 = new ObjectMapper();
           			ObjectNode node2 = (ObjectNode)om2.readTree(item2);
           		 	System.out.println("jsp::node2:"+node2);
           				
           			String db2_db2log = node2.get("db2_db2log").asText();
           		    String db2_logarchpath = node2.get("db2_logarchpath").asText();
           			String db2_backuppath = node2.get("db2_backuppath").asText();
           			String db2_locklist = node2.get("db2_locklist").asText();
           			String db2_maxlocks = node2.get("db2_maxlocks").asText();
           			String db2_locktimeout = node2.get("db2_locktimeout").asText();
           			String db2_sortheap = node2.get("db2_sortheap").asText();
           			String db2_logprimary = node2.get("db2_logprimary").asText();
           			String db2_logsecond = node2.get("db2_logsecond").asText();
           			String db2_logbuff = node2.get("db2_logbuff").asText();
           			String db2_softmax = node2.get("db2_softmax").asText();
            		%>
            		
              <div class="mainmodule">
              	<h5 class="swapcon"><i class="icon-chevron-down icon-chevron-right"></i>数据库高级属性</h5>
                <div class="form-horizontal" style="display:none;">
                  <div class="control-group">
                    <label class="control-label">DB2日志路径</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" class="w45" id="db2_db2log" name="db2_db2log" value="<%=db2_db2log %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">归档日志路径</span>
                        <input type="text" id="db2_logarchpath"  class="w45" name="db2_logarchpath" value="<%=db2_logarchpath %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2备份路径</label>
                    <div class="controls">
                      <input style="width:78%" type="text" id="db2_backuppath" name="db2_backuppath" value="<%=db2_backuppath %>" readonly="readonly"/>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">SELF_TUNING_MEM</label>
                    <div class="controls">
                      <div class="inputb2l">
                      	<select class="w48" name="db2_stmm" >
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF">OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20" >LOCKLIST</span>
                        <input type="text"  id="db2_locklist" class="w45"  name="db2_locklist" value="<%=db2_locklist %>" readonly="readonly"/></div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MAXLOCKS</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_maxlocks" name="db2_maxlocks" class="w45"  value="<%=db2_maxlocks %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOCKTIMEOUT</span>
                        <input type="text" id="db2_locktimeout" name="db2_locktimeout" class="w45"  value="<%=db2_locktimeout %>" />
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">SORTHEAP</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" style="margin-top:-20px;" id="db2_sortheap" name="db2_sortheap" class="w45" value="<%=db2_sortheap %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOGFILESIZ</span>
                        <select class="w48" name="db2_logfilesize">
                        	<option value="200">200</option>
                            <option value="500">500</option>
                        </select>
                        MB
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">LOGPRIMARY</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_logprimary" class="w45" name="db2_logprimary" value="<%=db2_logprimary %>"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOGSECOND</span>
                        <input type="text" id="db2_logsecond" class="w45"  name="db2_logsecond" value="<%=db2_logsecond %>" />
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">LOGBUFSZ</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" id="db2_logbuff" class="w45" name="db2_logbuff" value="<%=db2_logbuff %>" />&nbsp;MB</div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">SOFTMAX</span>
                        <input type="text" id="db2_softmax" class="w45" name="db2_softmax" value="<%=db2_softmax %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  
                   <div class="control-group">
                    <label class="control-label">TRACKMOD</label>
                    <div class="controls">
                      <div class="inputb2l">
                      	<select class="w48" name="db2_trackmod">
                        	<option value="YES" selected="selected">YES</option>
                          <option value="NO">NO</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">PAGESIZE</span>
                        <select class="w48" name="db2_pagesize">
                          <option value="4">4</option>
                          <option value="8" selected="selected">8</option>
                          <option value="16">16</option>
                          <option value="32">32</option>
                        </select>
                       </div>
                    </div>
                  </div>
              </div>
              </div>
              </form>
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
  <script>
		function sortRadio(val){
				if(val=='1'){
					 $("#db2_db2base").val("/opt/IBM/db2/v9.7");
					 
				}else if(val=='2'){
					$("#db2_db2base").val("/opt/IBM/db2/v10.1");
				}
				else{
					$("#db2_db2base").val("/opt/IBM/db2/v10.5");
					}
		}
  </script>
</body>
</html>
