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
			if($("#portNum").val().trim()==""){
				ymPrompt.alert("请输入DB2端口号!");
		    	return;	
			}else{
				$("#submits").submit();
			}
			
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
    <a href="getAllInstance?type=db2ha" title="服务编排管理平台" class="tip-bottom"><i class="icon-home"></i>服务编排管理平台</a>
    <a href="#" class="current">HA配置</a>
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
                      <div class="widget-content">配置当前虚拟机的HA信息</div>
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
              
             	    <%--  <input type="hidden" id="serId" name="serId" value="${serId }">
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
					<input type="hidden" id="hdiskname" name="hdiskname" value="${hdisknames }">
					<input type="hidden" id="hdiskid" name="hdiskid" value="${hdiskids }">
					<input type="hidden" id="vgtype" name="vgtype" value="${vgtypes }">
                  	<!-- VG END -->  --%>
                  	
                  	
                  	<%
                    
                    String item = String.valueOf(request.getAttribute("basicInfo"));
                    
                    ObjectMapper om = new ObjectMapper();
           			ObjectNode node = (ObjectNode)om.readTree(item);
           		 	System.out.println("jsp::node:"+node);
           		
           			String db2base = node.get("db2base").asText();
           			String dbpath = node.get("dbpath").asText();
           			String db2insusr = node.get("db2insusr").asText();
           			String svcename = node.get("svcename").asText();
           			String dbname = node.get("dbname").asText();
           		
            		%>
            		
              <div class="mainmodule">
              	<h5 class="swapcon">基本信息</h5>
                <form action="#" method="get" class="form-horizontal">
                  <div class="control-group">
                    <label class="control-label">DB2安装版本</label>
                    <div class="controls">
                      <div class="inblock mr20"><label><input type="radio" name="version" value="3" onClick="sortRadio(3)"/>v10.5</label></div>
                      <div class="inblock mr20"><label><input type="radio" name="version" value="2" onClick="sortRadio(2)"/>v10.1</label></div>
                      <div class="inblock mr20"><label><input type="radio" name="version" value="1" onClick="sortRadio(1)" checked/>v9.7</label></div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2安装路径</label>
                    <div class="controls">
                   		<input type="text" id="db2base" name="db2base" value="<%=db2base %>" readonly="readonly"/>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2实例目录</label>
                    <div class="controls">
                      <input type="text" id="dbpath" name="dbpath" value="<%=dbpath %>" readonly="readonly"/>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2实例名</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" class="w45" id="db2insusr" name="db2insusr" value="<%=db2insusr %>" />
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">监听端口</span>
                        <input type="text" class="w45" id="svcename" name="svcename" value="<%=svcename %>" />
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2数据库名</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" class="w45" id="dbname" name="dbname" value="<%=dbname %>" readonly="readonly"/>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">字符集</span>
                        <select id="styleSelect" class="w48" onChange="changeDivShow('styleSelect')" name="codeset">
                        	<option value="gbk" selected="selected">GBK</option>
                          <option value="UTF-8">UTF-8</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2数据目录</label>
                    <div class="controls">
                      <%-- <input type="text" value="/db2space1, /db2space2, /db2space3, /db2space4"/>
                      <input type="text" id="db2base" name="db2base" value="<%=db2base %>" readonly="readonly"/> --%>
                    </div>
                  </div>
                </form>
              </div>
              
              
              
               <%
                    
                    String item1 = String.valueOf(request.getAttribute("instProp"));
                    
                    ObjectMapper om1 = new ObjectMapper();
           			ObjectNode node1 = (ObjectNode)om1.readTree(item1);
           		 	System.out.println("jsp::node1:"+node1);
           		
           		 	
           			/* String db2insusr = node1.get("db2insusr").asText(); */
           			String db2insgrp = node1.get("db2insgrp").asText();
           			String db2fenusr = node1.get("db2fenusr").asText();
           			String db2fengrp = node1.get("db2fengrp").asText();
           			String db2comm = node1.get("db2comm").asText();
           			String db2codepage = node1.get("db2codepage").asText();
           			String initagents = node1.get("initagents").asText();
           			String poolagents = node1.get("poolagents").asText();
           			String diagsize = node1.get("diagsize").asText();
           			String mon_heap = node1.get("mon_heap").asText();
           		
            		%>
            		
              <div class="mainmodule">
              	<h5 class="swapcon">实例高级属性</h5>
                <form action="#" method="get" class="form-horizontal" style="display:none;">
                  <div class="control-group">
                    <label class="control-label">实例用户</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <input type="text" class="w45" value="db2insist1" readonly/>
                      </div>
                      <%-- <input type="text" id="db2insusr" name="db2insusr" value="<%=db2insusr %>" readonly="readonly"/></div> --%>
                      <div class="inputb2l">
                      	<span class="input140 mr20">实例用户组</span>
                        <!-- <input type="text" class="w45" value="db2insist"/> -->
                        <input type="text" class="w45" id="db2insgrp" name="db2insgrp" value="<%=db2insgrp %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">fence用户</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="db2fenc" /> -->
                      <input type="text" id="db2fenusr" class="w45" name="db2fenusr" value="<%=db2fenusr %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">fence用户组</span>
                        <!-- <input type="text" class="w45" value="db2fgrp"/> -->
                        <input type="text" id="db2fengrp" class="w45"  name="db2fengrp" value="<%=db2fengrp %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2COMM</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="TCPIP" /> -->
                      <input type="text" id="db2comm" class="w45" name="db2comm" value="<%=db2comm %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DB2CODEPAGE</span>
                        <!-- <input type="text" class="w45 codeVal" readonly value="1386"/> -->
                        <input type="text" id="db2codepage" class="w45 name="db2codepage" value="<%=db2codepage %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">NUM_INITAGENI</label>
                    <div class="controls">
                      <div class="inputb2l">
                     <!--  <input type="text" class="w45" value="Automatic" /> -->
                      <input type="text" id="initagents" class="w45" name="initagents" value="<%=initagents %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">NUM_POOLAGENTS</span>
                        <!-- <input type="text" class="w45" value="Automatic"/> -->
                        <input type="text" id="poolagents" class="w45" name="poolagents" value="<%=poolagents %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DIAGSIZE</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="50"/> -->
                      <input type="text" id="diagsize" class="w45"name="diagsize" value="<%=diagsize %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_BUFPOOL</span>
                        <select class="w48" name="mon_buf">
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
                      	<select class="w48" name="mon_lock">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_SORT</span>
                        <select class="w48" name="mon_sort">
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
                      	<select class="w48" name="mon_stmt">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">DFT_MON_TABLE</span>
                        <select class="w48" name="mon_table">
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
                      	<select class="w48" name="mon_uow">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF" >OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">HEACTH_MON</span>
                        <select class="w48" name="health_mon">
                        	<option value="ON">ON</option>
                          <option value="OFF" selected="selected">OFF</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MON_HEAP_SZ</label>
                    <div class="controls">
                      <!-- <input type="text" value="Automatic(500)"/> -->
                      <input type="text" id="mon_heap" name="mon_heap" value="<%=mon_heap %>" readonly="readonly"/>
                    </div>
                  </div>
                </form>
              </div>
              
              
              
                            <%
                    
                    String item2 = String.valueOf(request.getAttribute("dbProp"));
                    
                    ObjectMapper om2 = new ObjectMapper();
           			ObjectNode node2 = (ObjectNode)om2.readTree(item2);
           		 	System.out.println("jsp::node2:"+node2);
           		
           				
           			String db2log = node2.get("db2log").asText();
           		    String logarchpath = node2.get("logarchpath").asText();
           			String backuppath = node2.get("backuppath").asText();
           			String locklist = node2.get("locklist").asText();
           			String maxlocks = node2.get("maxlocks").asText();
           			String locktimeout = node2.get("locktimeout").asText();
           			String sortheap = node2.get("sortheap").asText();
           			String logprimary = node2.get("logprimary").asText();
           			String logsecond = node2.get("logsecond").asText();
           			String logbuff = node2.get("logbuff").asText();
           			String softmax = node2.get("softmax").asText();
           			
           			
           		
            		%>
            		
              <div class="mainmodule">
              	<h5 class="swapcon">数据库高级属性</h5>
                <form action="#" method="get" class="form-horizontal" style="display:none;">
                  <div class="control-group">
                    <label class="control-label">DB2日志路径</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="/db2log"/> -->
                      <input type="text" id="db2log" name="db2log" value="<%=db2log %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">归档日志路径</span>
                        <!-- <input type="text" class="w45" value="/db2archlog"/> -->
                        <input type="text" id="logarchpath"  class="w45" name="logarchpath" value="<%=logarchpath %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">DB2备份路径</label>
                    <div class="controls">
                     <!--  <input type="text"  value="/db2backup" /> -->
                      <input type="text" id="backuppath" name="backuppath" value="<%=backuppath %>" readonly="readonly"/>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">SELF_TUNING_MEM</label>
                    <div class="controls">
                      <div class="inputb2l">
                      	<select class="w48" name="stmm">
                        	<option value="ON" selected="selected">ON</option>
                          <option value="OFF">OFF</option>
                        </select>
                      </div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOCKLIST</span>
                       <!--  <input type="text" class="w45" value="Automatic"/> -->
                        <input type="text" id="locklist" class="w45"  name="locklist" value="<%=locklist %>" readonly="readonly"/></div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">MAXLOCKS</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="Automatic" /> -->
                      <input type="text" id="maxlocks" name="maxlocks" class="w45"  value="<%=maxlocks %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOCKTIMEOUT</span>
                        <!-- <input type="text" class="w45" value="60"/> -->
                        <input type="text" id="locktimeout" name="locktimeout" class="w45"  value="<%=locktimeout %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">SORTHEAP</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="Automatic"/> -->
                      <input type="text" id="sortheap" name="sortheap" class="w45" value="<%=sortheap %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOGFILESIZ</span>
                        <select class="w48" name="logfilesize">
                        	<option value="200">200</option>
                            <option value="500">500</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">LOGPRIMARY</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="30" /> -->
                      <input type="text" id="logprimary" class="w45" name="logprimary" value="<%=logprimary %>" readonly="readonly"/></div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">LOGSECOND</span>
                        <!-- <input type="text" class="w45" value="20"/> -->
                        <input type="text" id="logsecond" class="w45"  name="logsecond" value="<%=logsecond %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">LOGBUFSZ</label>
                    <div class="controls">
                      <div class="inputb2l">
                      <!-- <input type="text" class="w45" value="200" /> -->
                      <input type="text" id="logbuff" class="w45" name="logbuff" value="<%=logbuff %>" readonly="readonly"/>MB</div>
                      <div class="inputb2l">
                      	<span class="input140 mr20">SOFTMAX</span>
                        <!-- <input type="text" class="w45" value="100"/> -->
                        <input type="text" id="softmax" class="w45" name="softmax" value="<%=softmax %>" readonly="readonly"/>
                      </div>
                    </div>
                  </div>
                  <div class="control-group">
                    <label class="control-label">TRACKMOD</label>
                    <div class="controls">
                      <select class="w81" name="trackmod">
                        	<option value="YES" selected="selected">YES</option>
                          <option value="NO">NO</option>
                        </select>
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
  <script>
		function sortRadio(val){
				if(val==1){
					 $(".changeVal").val("/opt/IBM/db2/v9.7")
					 
				}else if(val==2){
					$(".changeVal").val("/opt/IBM/db2/v10.1")
				}
				else{
					$(".changeVal").val("/opt/IBM/db2/v10.5")
					}
		}
  </script>
</body>
</html>
