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
			ymPrompt.win({message:'&nbsp;提交任务后是否在目标主机立即运行脚本，创建环境？',title:'创建提示！',handler:function(tp){
							if(tp == "no"){
								window.history.go(0);
								//alert("已经创建脚本！");
								var type = document.getElementById("type");
								type.setAttribute("value", "no");
							}	
							else{
							$("#submits").submit();
						   }
						},btn:[['是','yes'],['否','no']],icoCls:'ymPrompt_alert'});
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
                      <div class="widget-content">确认当前虚拟机的DB2HA参数信息</div>
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
              <form action="installDb2haInfo" method="post" id="submits" class="form-horizontal">
             	    <input type="hidden" id="serId" name="serId" value="${serId }">
             	    
             	    <!-- 是否立即执行标识 -->
             	    <input type="hidden" id="type" name="type" value="yes">
             	    <!-- 基本 Begin -->
					<input type="hidden" id="haname" name="ha_clusterName" value="${haname}">
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
					<input type="hidden" id="hdiskname" name="hdiskname" value="${hdiskname }">
					<input type="hidden" id="hdiskid" name="hdiskid" value="${hdiskid }">
					<input type="hidden" id="vgtype" name="vgtype" value="${vgtype }">
                  	<!--old VG END -->
                  	
                  	<!-- VG BEGIN -->
                  	<input type="hidden" id="ha_vgdb2home" name="ha_vgdb2home" value="${ha_vgdb2home }">
                  	<input type="hidden" id="ha_vgdb2log" name="ha_vgdb2log" value="${ha_vgdb2log }">
                  	<input type="hidden" id="ha_vgdb2archlog" name="ha_vgdb2archlog" value="${ha_vgdb2archlog }">
                  	<%-- <input type="hidden" id="ha_vgdb2backup" name="ha_vgdb2backup" value="${ha_vgdb2backup }"> --%>
                  	<input type="hidden" id="ha_vgdataspace" name="ha_vgdataspace" value="${ha_vgdataspace }">
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
                  	<%-- <input type="hidden" id="ha_db2backupmode" name="ha_db2backupmode" value="${ha_db2backupmode }"> --%>
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
                  	
                  	<!-- DB2 Begin -->
                  	<!-- 基本信息 -->
					<input type="hidden" id="db2_version" name="db2_version" value="${db2_version }">
					<input type="hidden" id="db2_db2base" name="db2_db2base" value="${db2_db2base }">
					<input type="hidden" id="db2_dbpath" name="db2_dbpath" value="${db2_dbpath }">
					<input type="hidden" id="db2_svcename" name="db2_svcename" value="${db2_svcename }">
					<input type="hidden" id="db2_db2insusr" name="db2_db2insusr" value="${db2_db2insusr }">
					<input type="hidden" id="db2_dbname" name="db2_dbname" value="${db2_dbname }">
					<input type="hidden" id="db2_codeset" name="db2_codeset" value="${db2_codeset }">
					<input type="hidden" id="db2_dbdatapath" name="db2_dbdatapath" value="${db2_dbdatapath }">
					<!-- 实例高级属性 -->
					<input type="hidden" id="db2_db2insgrp" name="db2_db2insgrp" value="${db2_db2insgrp }">
					<input type="hidden" id="vgdb2_db2fenusrtype" name="db2_db2fenusr" value="${db2_db2fenusr }">
					<input type="hidden" id="db2_db2fengrp" name="db2_db2fengrp" value="${db2_db2fengrp }">
					<input type="hidden" id="db2_db2comm" name="db2_db2comm" value="${db2_db2comm }">
					<input type="hidden" id="db2_db2codepage" name="db2_db2codepage" value="${db2_db2codepage }">
					<input type="hidden" id="db2_initagents" name="db2_initagents" value="${db2_initagents }">
					<input type="hidden" id="db2_max_coordagents" name="db2_max_coordagents" value="${db2_max_coordagents }">
					<input type="hidden" id="db2_max_connectings" name="db2_max_connectings" value="${db2_max_connectings }">
					<input type="hidden" id="db2_poolagents" name="db2_poolagents" value="${db2_poolagents }">
					<input type="hidden" id="db2_diagsize" name="db2_diagsize" value="${db2_diagsize }">
					<input type="hidden" id="db2_mon_buf" name="db2_mon_buf" value="${db2_mon_buf }">
					<input type="hidden" id="db2_mon_lock" name="db2_mon_lock" value="${db2_mon_lock }">
					<input type="hidden" id="db2_mon_sort" name="db2_mon_sort" value="${db2_mon_sort }">
					<input type="hidden" id="db2_mon_stmt" name="db2_mon_stmt" value="${db2_mon_stmt }">
					<input type="hidden" id="db2_mon_table" name="db2_mon_table" value="${db2_mon_table }">
					<input type="hidden" id="db2_mon_uow" name="db2_mon_uow" value="${db2_mon_uow }">
					<input type="hidden" id="db2_health_mon" name="db2_health_mon" value="${db2_health_mon }">
					<input type="hidden" id="db2_mon_heap" name="db2_mon_heap" value="${db2_mon_heap }">
					<!-- 数据库高级属性 -->
					<input type="hidden" id="db2_db2log" name="db2_db2log" value="${db2_db2log }">
					<input type="hidden" id="db2_backuppath" name="db2_backuppath" value="${db2_backuppath }">
					<input type="hidden" id="db2_logarchpath" name="db2_logarchpath" value="${db2_logarchpath }">
					<input type="hidden" id="db2_stmm" name="db2_stmm" value="${db2_stmm }">
					<input type="hidden" id="db2_locklist" name="db2_locklist" value="${db2_locklist }">
					<input type="hidden" id="db2_maxlocks" name="db2_maxlocks" value="${db2_maxlocks }">
					<input type="hidden" id="db2_locktimeout" name="db2_locktimeout" value="${db2_locktimeout }">
					<input type="hidden" id="db2_sortheap" name="db2_sortheap" value="${db2_sortheap }">
					<input type="hidden" id="db2_logfilesize" name="db2_logfilesize" value="${db2_logfilesize }">
					<input type="hidden" id="db2_logprimary" name="db2_logprimary" value="${db2_logprimary }">
					<input type="hidden" id="db2_logsecond" name="db2_logsecond" value="${db2_logsecond }">
					<input type="hidden" id="db2_logbuff" name="db2_logbuff" value="${db2_logbuff }">
					<input type="hidden" id="db2_softmax" name="db2_softmax" value="${db2_softmax }">
					<input type="hidden" id="db2_trackmod" name="db2_trackmod" value="${db2_trackmod }">
					<input type="hidden" id="db2_pagesize" name="db2_pagesize" value="${db2_pagesize }">
                  	<!-- DB2 end -->
                  	
              <div class="mainmodule">
              	<h5 class="swapcon"><i class="icon-chevron-down"></i>基本信息</h5>
                <!-- <form action="#" method="get" class="form-horizontal"> -->
                 <div class="form-horizontal">
                    <div class="control-group">
                    <label class="control-label">DB2安装版本</label>
                    <div class="controls">
                      <span class="graytxt">${db2_version }</span>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2安装路径</label>
                    <div class="controls">
                      <span class="graytxt">${db2_db2base }</span>
                    </div>
                  </div>
                  
                  <div class="control-group">
                    <label class="control-label">DB2实例目录</label>
                    <div class="controls">
                      <%-- <input type="text" id="dbpath" name="dbpath" value="<%=dbpath %>" readonly="readonly"/> --%>
                      <span class="graytxt">${db2_dbpath }</span>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2实例名</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_db2insusr }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">监听端口</span>
                        <span class="graytxt">${db2_svcename }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2数据库名</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_dbname }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">字符集</span>
                        <span class="graytxt">${db2_codeset }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2数据目录</label>
                    <div class="controls">
                      <span class="graytxt">${db2_dbdatapath }</span>
                    </div>
                  </div>
               <!--  </form> -->
              </div>
               </div>
              
              <div class="mainmodule">
              	<h5 class="swapcon"><i class="icon-chevron-down icon-chevron-right"></i>实例高级属性</h5>
               <!--  <form action="#" method="get" class="form-horizontal" style="display:none;"> -->
                <div class="form-horizontal" style="display:none;">
               
                  <div class="control-group">
                    <label class="control-label">实例用户</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="db2insist1" readonly/> -->
                      <span class="graytxt">${db2_db2insusr }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">实例用户组</span>
                        <span class="graytxt">${db2_db2insgrp }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">fence用户</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_db2fenusr }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">fence用户组</span>
                        <span class="graytxt">${db2_db2fengrp }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2COMM</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_db2comm }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DB2CODEPAGE</span>
                        <span class="graytxt">${db2_db2codepage }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">NUM_INITAGENI</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_initagents }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">NUM_POOLAGENTS</span>
                        <span class="graytxt">${db2_poolagents }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MAX_COORDAGENTS</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_max_coordagents }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">MAX_CONNECTIONS</span>
                        <span class="graytxt">${db2_max_connectings }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DIAGSIZE</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_diagsize }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_BUFPOOL</span>
                        <span class="graytxt">${db2_mon_buf }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DFT_MON_LOCK</label>
                    <div class="controls">
                      <div class="inputb2l">
                        <span class="graytxt">${db2_mon_lock }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_SORT</span>
                        <span class="graytxt">${db2_mon_sort }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DFT_MON_STMT</label>
                    <div class="controls">
                      <div class="inputb2l">
                        <span class="graytxt">${db2_mon_stmt }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_TABLE</span>
                        <span class="graytxt">${db2_mon_table }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DFT_MON_UOW</label>
                    <div class="controls">
                      <div class="inputb2l">
                        <span class="graytxt">${db2_mon_uow }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">HEACTH_MON</span>
                        <span class="graytxt">${db2_health_mon }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MON_HEAP_SZ</label>
                    <div class="controls">
                      <span class="graytxt">${db2_mon_heap }</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="mainmodule">
              	<!-- <h5 class="swapcon"><i class="icon-chevron-right"></i>数据库高级属性</h5> -->
              	<h5 class="swapcon"><i class="icon-chevron-down icon-chevron-right"></i>数据库高级属性</h5>
                <div class="form-horizontal" style="display:none;">
                  <div class="control-group">
                    <label class="control-label">DB2日志路径</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_db2log }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">归档日志路径</span>
                        <span class="graytxt">${db2_logarchpath }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2备份路径</label>
                    <div class="controls">
                      <span class="graytxt">${db2_backuppath }</span>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">SELF_TUNING_MEM</label>
                    <div class="controls">
                      <div class="inputb2l">
                        <span class="graytxt">${db2_stmm }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOCKLIST</span>
                        <span class="graytxt">${db2_locklist }</span>
                        </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MAXLOCKS</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_maxlocks }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOCKTIMEOUT</span>
                        <span class="graytxt">${db2_locktimeout }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">SORTHEAP</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_sortheap }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOGFILESIZ</span>
                        <span class="graytxt">${db2_logfilesize } MB</span>
                        
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">LOGPRIMARY</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_logprimary }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOGSECOND</span>
                        <span class="graytxt">${db2_logsecond }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">LOGBUFSZ</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_logbuff } MB</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">SOFTMAX</span>
                        <span class="graytxt">${db2_softmax }</span>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">TRACKMOD</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <span class="graytxt">${db2_trackmod }</span>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">PAGESIZE</span>
                        <span class="graytxt">${db2_pagesize }</span>
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
      <span>创建</span>
      <i class="icon-btn-next"></i>
    </a>
  </div> 
</body>
</html>
