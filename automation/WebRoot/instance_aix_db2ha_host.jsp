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
		$(document).ready(function(){
			$("#selectmy").append("<option selected='selected' value=" + $("#ha_hostname1").val() + ">" + $("#ha_hostname1").val() + "</option>");
			var o1 = $("#selectmy").find("option:first");
			if(o1.attr("selected") == "selected"){
				$("#selectmy").prev().find("a:first").find("span:first").html($("#ha_hostname1").val());
			}
			$("#selectmy").append("<option value=" + $("#ha_hostname2").val() + ">" + $("#ha_hostname2").val() + "</option>");
		});
	
		function CheckInput() {
			
			 var re=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/;//正则表达式
			if($("#haname").val()== ""){
				ymPrompt.alert("请输入HA名称!");
		    	return;	
			}
					
			$('.hostName').each(function(){
				if($(this).val().trim()==""){
					ymPrompt.alert("请输入节点名称!");
			    	return;	
				}
				
			});
			
			$('.hostIp').each(function(){
				if($(this).val().trim()==""){
					ymPrompt.alert("请输入节点IP!");
			    	return;	
				}
				
			});
			

			if (re.test($("#HAserviceIP").val())) {
				if (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256
						&& RegExp.$4 < 256) {
				} else {
					ymPrompt.alert("请输入争取格式的服务IP!例(127.0.0.1)");
					return;
				}
			} else {
				ymPrompt.alert("请输入正确格式的服务IP!例:(127.0.0.1)");
				return;
			}

			if ($("#HAserviceName").val() == "") {
				ymPrompt.alert("请输入Service主机别名!");
				return;
			} else {
				if($("#HAserviceIP").val() == $("#ha_ip1").val() 
				|| $("#HAserviceIP").val() == $("#ha_bootip1").val()  
				|| $("#HAserviceIP").val() == $("#ha_ip2").val() 
				|| $("#HAserviceIP").val() == $("#ha_bootip2").val()){
					ymPrompt.alert("服务 IP 不能和节点 IP 或节点 boot IP 相同!");
					return;
				}
				$("#ha_bootalias1").val($("#ha_hostname1").val() + "-BOOT");
				$("#ha_bootalias2").val($("#ha_hostname2").val() + "-BOOT");
				$("#submits").submit();
			}
		}
	</script>
	<style>
	.inputb4{ width:40%;}
	.inputb25{ width:25%; display:inline-block;}
	</style>
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
	                      <div class="widget-content">配置当前虚拟机的HA信息.</div>
	                  </div>
	              </div>
	          </div>
	    </div>
	  </div>
		<div class="container-fluid">
        <div class="row-fluid">
          <div class="span12">
            <div class="columnauto">
             <!--  <h5>配置当前虚拟机的HA信息</h5> -->
              <div class="mainmodule">
                <h5 class="stairtit">拓扑结构</h5>
                
                <c:forEach items="${servers }" var="ser" varStatus="num">
	                <p class="twotit">节点<c:out  value="${num.count }"/></p>
	                <div class="column">
	                  <div class="span12">
	                    <p>
	                      <b>主机名:</b><span class="column_txt">${ser.name }</span>
	                      <b>IP地址:</b><span class="column_txtl">${ser.IP }</span>
	                      	<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
	                    </p>
	                    <p>
	                     <b>系统配置:</b><span class="column_txt">${ser.HConf }</span>
	                      <b>挂卷:</b><span class="column_txtl">volume_HB , volume_Data</span>
	                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
	                    </p>
	                  </div>
	                </div>
                </c:forEach>
                
                <div class="mainmodule">
                  <h5>Host信息完善</h5>
                  <form action="toNextPage?status=makeVgPage" method="post" id="submits" class="form-horizontal">
                  	<input type="hidden" id="serId" name="serId" value="${serId }">
	                
	                <div class="control-group">
	                    <label class="control-label">HA名称</label>
	                    <div class="controls">
	                      <div class="inputb2l"><input style="margin-top:-20px" class="w45" type="text" id="haname" name="haname" value=""/></div>
	                      <div class="inputb2l">
	                      	<span class="input140 mr20">资源组主节点</span>
	                        <select id="selectmy" class="w85" style="width:47.5%" name="ha_primaryNode">
	                          	  <%-- <c:forEach items="${hostNames}" varStatus="num">
	                          	  	<option value="${hostNames[num.count-1] }">${hostNames[num.count-1] }</option>
	                          	  </c:forEach>
	                          	  <option value="${ha_hostname1}">${ha_hostname1}</option>
	                          	  <option value="${ha_hostname2}">${ha_hostname2}</option> --%>
	                        </select> 
	                      </div>
	                    </div>
                    </div>
	                
                    <%
                    
                    String items = String.valueOf(request.getAttribute("hosts"));
                    System.out.println("jsp::items:"+items);
                    
                    ObjectMapper om = new ObjectMapper();
                    ArrayNode list = (ArrayNode) om.readTree(items);
                    System.out.println("jsp::list:"+list);
                    for(int i=0;i<list.size();i++){			
            			ObjectNode node = (ObjectNode) list.get(i);
            			String hostname = node.get("hostname").asText();
            			String addr = node.get("addr").asText();
            			String serip = node.get("serip").asText();
            			String sername = node.get("sername").asText();
            			String perip = node.get("perip").asText();
            			String pername = node.get("pername").asText();
            			String hostId = node.get("hostId").asText();
            		%>
            		<%if(i==0){ %>
            			<input type="hidden" name="hostId1" value="<%=hostId  %>"></input>
            		<%} %>
            		<%if(i==1){ %>
            			<input type="hidden" name="hostId2" value="<%=hostId  %>"></input>
            		<%} %>
                  <%if(i==0){%>
                  	<div class="control-group">
	                    <label class="control-label">节点 <%=i+1 %> IP</label>
	                    <div class="controls">
	                      <div class="inputb2l"><input id="ha_ip1" type="text" class="w45" name="ha_ip1" value="<%=addr  %>" readonly="readonly" /></div>
	                      <div class="inputb2l">
	                      	<span class="input140 mr20">节点 <%=i+1 %> 主机名</span>
	                        <input id="ha_hostname1" type="text" class="w45" name="ha_hostname1" value="<%=hostname  %>" onblur="changeBoot(1,this)"/>
	                      </div>
	                    </div>
	                  </div>
	                  
	                  <div class="control-group">
	                    <label class="control-label">节点 <%=i+1 %> Boot IP</label>
	                    <div class="controls">
	                      <%-- <div class="inputb2l"><input type="text" class="w45" name="ha_bootip1" value="<%=perip %>" readonly="readonly" /></div> --%>
	                      <div class="inputb2l"><input id="ha_bootip1" type="text" class="w45" name="ha_bootip1" value="<%=addr %>" readonly="readonly" /></div>
	                      <div class="inputb2l">
	                      	<span class="input140 mr20">节点 <%=i+1 %> Boot主机别名</span>
	                        <input type="text" class="w45"  id="ha_bootalias1"  name="ha_bootalias1" value="<%=pername %>"/>
	                      </div>
	                    </div>
	                  </div>
                  <% } else{%>
                  	<div class="control-group">
	                    <label class="control-label">节点 <%=i+1 %> IP</label>
	                    <div class="controls">
	                      <div class="inputb2l"><input id="ha_ip2" type="text" class="w45" name="ha_ip2" value="<%=addr  %>" readonly="readonly" /></div>
	                      <div class="inputb2l">
	                      	<span class="input140 mr20">节点 <%=i+1 %> 主机名</span>
	                        <input id="ha_hostname2" type="text" class="w45" name="ha_hostname2" value="<%=hostname  %>"  onblur="changeBoot(2,this)"/>
	                      </div>
	                    </div>
	                  </div>
	                  
	                  <div class="control-group">
	                    <label class="control-label">节点 <%=i+1 %> Boot IP</label>
	                    <div class="controls">
	                      <%-- <div class="inputb2l"><input type="text" class="w45" name="ha_bootip2" value="<%=perip %>" readonly="readonly" /></div> --%>
	                      <div class="inputb2l"><input id="ha_bootip2" type="text" class="w45" name="ha_bootip2" value="<%=addr %>" readonly="readonly" /></div>
	                      <div class="inputb2l">
	                      	<span class="input140 mr20">节点 <%=i+1 %> Boot主机别名</span>
	                        <input type="text" class="w45" id="ha_bootalias2"  name="ha_bootalias2" value="<%=pername %>" />
	                      </div>
	                    </div>
	                  </div>
                  <%} %>
                    <% } %>
                    <div class="control-group">
	                    <label class="control-label">HA Service IP</label>
	                    <div class="controls">
	                      <div class="inputb2l"><input id="HAserviceIP" type="text" style="color: #C0C0C0" class="w45" name="ha_svcip" value="请确保设置的IP已在PowerVC IP池中被锁定 !"
	                       onfocus='if(this.value=="请确保设置的IP已在PowerVC IP池中被锁定 !"){this.value="";$("#HAserviceIP").attr("style","color: #000000");}else{$("#HAserviceIP").attr("style","color: #000000");}' onblur='if(this.value==""){this.value="请确保设置的IP已在PowerVC IP池中被锁定 !";$("#HAserviceIP").attr("style","color: #C0C0C0");};'/></div>
	                      <div class="inputb2l">
	                      	<span class="input140 mr20">Service主机别名</span>
	                        <input id="HAserviceName" type="text" class="w45" name="ha_svcalias" value="" />
	                      </div>
	                    </div>
                    </div>
                     <input id="ptype" type="hidden" class="w45" name="ptype" value="${ptype }" />
                  </form>
                </div>
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
  <script type="text/javascript">
		 function changeBoot(flag,q){
			 if("1"==flag){
				var a =  q.value+"-BOOT";
				 $("#ha_bootalias1").val(a);
				var o1 = $("#selectmy").find("option:first");
				o1.val($("#ha_hostname1").val());
				o1.html($("#ha_hostname1").val());
				if(o1.attr("selected") == "selected"){
					$("#selectmy").prev().find("a:first").find("span:first").html($("#ha_hostname1").val());
				}
			 }else if ("2"==flag){
				 var a =  q.value+"-BOOT";
				 $("#ha_bootalias2").val(a);
				 var o2 = $("#selectmy").find("option:last");
				 o2.val($("#ha_hostname2").val());
				 o2.html($("#ha_hostname2").val());
				 if(o2.attr("selected") == "selected"){
					$("#selectmy").prev().find("a:first").find("span:first").html($("#ha_hostname2").val());
				 }
			 }
					
			
		} ;
		
  </script>
</body>
</html>
