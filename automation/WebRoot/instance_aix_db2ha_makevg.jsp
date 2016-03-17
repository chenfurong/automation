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
	<style type="text/css">
	.vgcss{
		width:100%;
		height:40px;
		background:#2592e0;
		color:#fff;
		font-size:16px;
		font-weight:bold;
		border:1px solid #2592e0;
		-moz-border-radius: 4px;      /* Gecko browsers */
	    -webkit-border-radius: 4px;   /* Webkit browsers */
	    border-radius:4px;            /* W3C syntax */
	}
	.vgcsst{
		width:100%;
		height:40px;
		background:#b0b0b0;
		color:#fff;
		font-size:16px;
		font-weight:bold;
		border:1px solid #fff;
		-moz-border-radius: 4px;      /* Gecko browsers */
	    -webkit-border-radius: 4px;   /* Webkit browsers */
	    border-radius:4px;            /* W3C syntax */
	}
	</style>
	<script language="javascript">
  	//监听创建div
	var a = [0,0,0,0];
	$(document).ready(function(){
		//alert($(".select2-container.select2-container-multi.w85").length + "-----a-----");
		
		$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				a[index] = $(ele).find("ul").find("li").length;
		});
		
		$("#alldisks").val(a);
		
		$("#divnfs1").find(":input").each(function(index, e){
			$(this).val("");
		});
		$(".divnfs").each(function (){
			$(this).find(":input").each(function(index, e){
				$(this).val("");
			});
		});
		
	});	
	
	//vg数目判断
	var vgnum = 0;
 	function getVGnum(){
		if($("#pvs9").val() == '' || $("#pvs9").val().split(',').length < 5){
//			alert("VG不足！");
 			ymPrompt.win({message:'&nbsp;虚机当前挂载的Hdisk数量小于5，请至少挂载5块！',title:'创建提示！',handler:function(tp){
				//javascript:history.go(-1);
				window.location.href="getAllServers";
				
			},btn:[['返回主页','yes']],closeBtn:false,icoCls:'ymPrompt_alert'}); 
		}
	} 
	
	var flag = true;
	
	var b;
	var t = 0;
	function test222(){
 		if(vgnum == 1){
			getVGnum();
		}
		vgnum = vgnum + 1; 
		b = [0,0,0,0];
		//alert($(".select2-container.select2-container-multi.w85").length + "----b------");
		
		$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				b[index] = $(ele).find("ul").find("li").length;
		});
		
		$("#alldisksb").val(b);
		

		
		flag = true;
		for(var i=0; i<b.length; i++){
			if(a[i] != b[i]){
				//alert(a[i] + "-------" + b[i]);
				flag = false;
			}
		}
		//alert(a + "===========" + b);
		//alert(flag);
		if(t == 0){
			$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				$(ele).next().empty();
			});//进入页面先删除所有的option
			var tt = [];//存放初始化页面时已经选择的选项
			$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				$(ele).find("ul").find("li").each(function(index3,ele3){
					if($(ele3).find("div").text() != ""){
						tt.push($(ele3).find("div").text());
					}
				});//
				//alert("添加了");
			});
			var ttt = $("#pvs9").val().split(",");//存放所有选项
			var tttt = [];//存放所有选项是否被选择标记
			for(var i=0; i<ttt.length; i++){
				if(ttt[i] == tt[i]){
					tttt.push(true);
				}else{
					tttt.push(false);
				}
			}
			var ttemp = [];//存放未选择选项
			for(var i=0; i<ttt.length; i++){
				if(tttt[i] == false){
					ttemp.push(ttt[i]);
				}
			}
			
			$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				for(var j=0; j<ttemp.length; j++){
					var tab = "<option>" + ttemp[j] + "</option>";
					$(ele).next().append(tab);
				}
			});
		}
		if(!flag){
			$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				$(ele).next().empty();
			});
			$("#alldisks").val($("#alldisksb").val());
			a = b;
			//alert("删完了");
			var c = [];
			$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				$(ele).find("ul").find("li").each(function(index3,ele3){
					if($(ele3).find("div").text() != ""){
						c.push($(ele3).find("div").text());
					}
				});//
				//alert("添加了");
			});
			
			if(t == 0){
				$("#alldisksc").val(c);
				t = 1;
			}else{
				$("#alldisksd").val(c);
				$("#alldisksc").val(c);
			}
			if($("#alldisksc").val() !=$("#pvs9").val() || $("#pvs9").val() != $("#alldisksd").val()){
				var la = $("#pvs9").val().split(",");
				var ld = $("#alldisksc").val().split(",");
				var dd = [];
				//alert(la.length);
				//alert(ld.length);
				for(var m=0; m<la.length; m++){
					for(var n=0; n<ld.length; n++){
						if(la[m] == ld[n]){
							dd.push(m);
						}
					}
				}
				//alert("dd=" + dd);
				//alert("la=" + la);
				for(var k=0; k<dd.length; k++){
					if(k==0){
						la.splice(dd[k],1);
					}else{
						la.splice(dd[k]-k,1);
					}
				}
				//alert("la=" + la);
				$(".select2-container.select2-container-multi.w85").each(function(index, ele){
					for(var j=0; j<la.length; j++){
						var tab = "<option>" + la[j] + "</option>";
						$(ele).next().append(tab);
					}
				});
				$("#alldisksd").val($("#pvs9").val());
				$("#alldisksc").val($("#pvs9").val());
			}
		}
	}
	setInterval('test222()', 500);
	</script>
	<script language="javascript" type="text/javascript">
		//是否为空标记
		var flagN = true;
		function CheckInput() {
			
			if ($("#ha_RGNmae").val().trim() == "") {
				ymPrompt.alert("请输入资源组名称!");
				return;
			}
			
			if ($("#ha_ASName").val().trim() == "") {
				ymPrompt.alert("请输入AS名称!");
				return;
			}
			
/* 			var ids = [];
			$('.hdiskid').each(function(){
				ids.push($(this).val());
			});
			console.log(ids);	
			
			var types = [];
			var vgtypes = '';
			$("select[name='vgtypes']").each(function(){
				types.push($(this).val());
				vgtypes = vgtypes +$(this).val()+",";	
			});
			console.log(types);
			console.log(vgtypes);
			
			var falg = $("#falg").val();
			if(falg==false||falg=='false'){
				ymPrompt.alert("请重新选择实例!");
		    	return;	
			}else{
				for(var i=0;i<ids.length;i++){
					for(var j=i+1;j<ids.length;j++){
						if(ids[i]==ids[j]){
							console.log(true);
							if(types[i]!=types[j]){
								ymPrompt.alert("主备机VG信息必须一致!");
						    	return;	
							}
						}
					}				
				}
				
				var find = "caavg-private";
				var reg = new RegExp(find,"g");
				var c = vgtypes.match(reg);
				if(parseInt(c?c.length:0) != 2){
					ymPrompt.alert("caavg-private只能在主备机中各选择一次!");
			    	return;
				}else{
					$("#submits").submit();
				}
			} */
			$(".select2-container.select2-container-multi.w85").each(function(index, ele){
				if($(ele).next().attr("name") == "ha_db2homepv_1"){
					/*var v1 = [];
					$(ele).find("ul").find("li").each(function(index3,ele3){
						if($(ele3).find("div").text() != ""){
							v1.push($(ele3).find("div").text());
						}
					}); */
					$("#ha_db2homepv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_db2logpv_2"){
					$("#ha_db2logpv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_db2archlogpv_3"){
					$("#ha_db2archlogpv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_db2backuppv_4"){
					$("#ha_db2backuppv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_dataspace1pv_5"){
					$("#ha_dataspace1pv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_dataspace2pv_6"){
					$("#ha_dataspace2pv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_dataspace3pv_7"){
					$("#ha_dataspace3pv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_dataspace4pv_8"){
					$("#ha_dataspace4pv").val(addMsg(ele));
				}
				if($(ele).next().attr("name") == "ha_caappv_9"){
					$("#ha_caappv").val(addMsg(ele));
				}
			});
			if(!flagN){
				ymPrompt.alert("每个VG请至少选择一个PV!");
				flagN = true;
				return;
			}
				
			//NFS参数非空验证
			//alert($("#divnfs1").find("[style!='display: none;']").length);
			//alert($(".divnfs").find("[style='']").find(":input").length);
			var returnnfs = 0;
			if($("#divnfs1").attr("style") == ""){
				$("#divnfs1").find(":input").each(function(index, e){
					if($(this).val() == ''){
						returnnfs++;
					}
				});
			}
			
			$(".divnfs").each(function (){
				if($(this).attr("style") == ""){
					$(this).find(":input").each(function(index, e){
						if($(this).val() == ''){
							returnnfs++;
						}
					});
				}
			});
			
			if(returnnfs > 0){
				ymPrompt.alert("挂载NFS文件系统，NFS参数不能为空！");
				returnnfs = 0;
				return;
			}
			
			$("#submits").submit();
		}
		
		//添加VG
		function addMsg(ele){
			var v1 = [];
			$(ele).find("ul").find("li").each(function(index3,ele3){
				if($(ele3).find("div").text() != ""){
					var str = $(ele3).find("div").text();
					var strnum =str.lastIndexOf("(");
					v1.push(str.substring(0,strnum));
				}
			});
			//alert(v1);
			if(v1.length == 0){
				flagN = false;
			}
			return v1;
		}
	</script>
	<script type="text/javascript">
		var vgcount = 0;
		function checkRadio(flag){
			if(flag == 1 && $("#spanaddvg").find(":input").length == 0 && $("#spancutvg").find(":input").length == 0){
				$("#divnfs1").attr("style","display: display;");
				$("#spanaddvg").append("<input type='button' onclick='addNFS();' class='vgcss' style='width:15%;' value='+ 增加NFS' />&nbsp;&nbsp;&nbsp;&nbsp;");
				$("#div1").after("<input id='input1'/>");
				$("#input1").focus();
				$("#input1").remove();
			}
			if(flag == 2){
				$("#divnfs1").find(":input").each(function(index, e){
					$(this).val("");
				});
				$("#divnfs1").attr("style","display: none;");
				$(".divnfs").each(function (){
					$(this).find(":input").each(function(index, e){
						$(this).val("");
					});
					$(this).attr("style","display: none;");
				});
				$("#spanaddvg").empty();
				$("#spancutvg").empty();
				vgcount = 0;
			}
		}
		
		function addNFS(){
			var hvg = [];//隐藏的NFS
 			$(".divnfs").each(function (){
				if($(this).attr("style") == "display: none;"){
					hvg.push(this);
				}
			});
			//alert(hvg.length);
			$(hvg.shift()).attr("style","display: display;");
			if(vgcount == 0){
				$("#spancutvg").empty();
				$("#spancutvg").append("&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='cutNFS();' class='vgcss' style='width:15%;' value='-  减少NFS' />");			
			}
			vgcount++;
			if(vgcount == 14){
				$("#spanaddvg").empty();
				//$("#spanaddvg").html("* 添加VG已达到最大数目");
				$("#spanaddvg").append("<input type='button' class='vgcsst' style='width:15%;ba' value='添加NFS已达到最大数目' />&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			$("#div1").after("<input id='input1'/>");
			$("#input1").focus();
			$("#input1").remove(); 
			//alert(vgcount);
		}
		
		function cutNFS(){
			var hvg = [];
			$(".divnfs").each(function (){
				if($(this).attr("style") == ""){
					hvg.push(this);
				}
			});
			var tempdiv = hvg.pop();
			$(tempdiv).find(":input").each(function(index, e){
				$(this).val("");
			});
			$(tempdiv).attr("style", "display: none;");
			
			if(vgcount == 14){
				$("#spanaddvg").empty();
				$("#spanaddvg").append("<input type='button' onclick='addNFS();' class='vgcss' style='width:15%;' value='+ 增加NFS' />&nbsp;&nbsp;&nbsp;&nbsp;");			
			}
			vgcount--;
			if(vgcount == 0){
				$("#spancutvg").empty();
				//$("#spancutvg").html("* 减少NFS已达到最小数目");
				$("#spancutvg").append("&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' class='vgcsst' style='width:15%;ba' value='减少NFS已达到最小数目' />");
			}
			//alert(vgcount);
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
	                      <div class="widget-content">配置当前虚拟机的VG信息.</div>
	                  </div>
	              </div>
	          </div>
	    </div>
	  </div>
		<div class="container-fluid">
        <div class="row-fluid">
          <div class="span12">
            <div class="columnauto">
              <!-- <h5>配置当前虚拟机的HA信息</h5> -->
              <div class="mainmodule">
                <h5 class="stairtit">拓扑结构</h5>
                
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
	                      	 <%--  <c:forEach items="${hostNames}" varStatus="nu">
	                      	    <c:if test="${nu.count == num.count}">
                          	  		${hostNames[nu.count-1] }
	                      	    </c:if>
                          	  </c:forEach> --%>
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
	                    </p>
	                    <p>
	                        <b>系统配置:</b><span class="column_txt">${ser.HConf }</span>
	                      <b>挂卷:</b><span class="column_txtl">volume_HB , volume_Data</span>
	                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
	                    </p>
	                  </div>
	                </div>
                </c:forEach>
                
                <div class="mainmodule"  id="div1">
                  <form action="toNextPage?status=installPageNew" method="post" id="submits" >
                  <h5>资源组名称
                  	<div class="inputb4 ml20"><input type="text" Id="ha_RGNmae" name="ha_RGNmae" value="RG1"/></div>
                    <div class="inputb2">
                    	<b class="input140 mr20" style="width:220px;">Application Server(Controller)</b>
                      <input type="text"Id="ha_ASName" name="ha_ASName" value="AS1"/>
                    </div>
                  </h5>
                 <input id="ptype" type="hidden" class="w45" name="ptype" value="${ptype }" />
                  <div class="form-horizontal processInfo">
                  <p class="twotit">资源组共享VG信息 &nbsp;&nbsp;&nbsp;<span style="color:#727272">*注：每一特定hdisk能且只能存在于某一VG中。</span></p>
                  	<input type="hidden" id="serId" name="serId" value="${serId }">
                  	<input type="hidden" id="ha_primaryNode" name="ha_primaryNode" value="${ha_primaryNode}">
                  	<!-- HOST Begin -->
                  	<input type="hidden" id="haname" name="haname" value="${haname}">
                  	<input type="hidden" id="hostName" name="hostName" value="${hostNames}">
                  	<input type="hidden" id="hostIp" name="hostIp" value="${hostIps }">
                  	<input type="hidden" id="serName" name="serName" value="${serNames }">
                  	<input type="hidden" id="serIp" name="serIp" value="${serIps }">
					<input type="hidden" id="perName" name="perName" value="${perNames }">
                  	<input type="hidden" id="perIp" name="perIp" value="${perIps }">
                  	<!-- HOST End -->
                  	
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
                  	
                  	<!-- makeVg Begin -->
                  	<input type="hidden" id="ppSize" name="ppSize" value="64">
                  	<input type="hidden" id="autoOn" name="autoOn" value="n">
                  	<input type="hidden" id="factor" name="factor" value="1">
                  	<input type="hidden" id="vgType" name="vgType" value="S">
                  	<input type="hidden" id="concurrent" name="concurrent" value="y">
                  	<!-- makeVg End -->
                  	<%
                    String allHdisk = String.valueOf(request.getAttribute("allHdisk"));
                    
                    ObjectMapper om = new ObjectMapper();
                    ArrayNode list = (ArrayNode) om.readTree(allHdisk);
                   	System.out.println("=====123====" + list);
                    Boolean falg = true;
                    if(list.size()==0){
                		falg = false;
                	}
                    int le = list.size();
                    if(le >1){
                    	le = 1;
                    }
                    for(int i=0;i<1;i++){
                    	ArrayNode items = (ArrayNode) om.readTree(list.get(i).toString());
                    	if(items.size()==0){
                    		falg = false;
                    	}
                    	System.out.println("jsp::falg:"+falg+items);
                    	List<String> hdisknameArray = new ArrayList<String>();
                        for(int j=0;j<items.size();j++){
	            			ObjectNode node = (ObjectNode) items.get(j);
	            			System.out.println("jsp::node:"+node);
	            			String hdiskname = node.get("hdiskname").asText();
	            			//String hdiskid = node.get("hdiskid").asText();
                    		hdisknameArray.add(hdiskname);
                        }
            		%>
                      <%-- <label class="control-label c-lmini"><p class="twotit">节点<%=i+1 %></p>VG信息<%=j+1 %></label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                        	<input class="hdiskname" type="text" name="hdisknames" value="<%=hdiskname %>" readonly="readonly"/>
                        </div>
                        <div class="inputb4">
                        	<input class="hdiskid" type="text" name="hdiskids" value="<%=hdiskid %>" readonly="readonly"/>
                        </div>
                        <div class="inputb4">
                        	<select class="vgtype w80" name="vgtypes">
                        		<option value="datavg1">datavg1</option>
                        		<option value="datavg2">datavg2</option>
                        		<option value="caavg-private">caavg-private</option>
                        	</select>
                        </div> --%>
                        <input type="hidden" id="ha_db2homepv" name="ha_db2homepv"/>
                        <input type="hidden" id="ha_db2logpv" name="ha_db2logpv"/>
                        <input type="hidden" id="ha_db2archlogpv" name="ha_db2archlogpv"/>
                        <input type="hidden" id="ha_db2backuppv" name="ha_db2backuppv"/>
                        
                        <input type="hidden" id="ha_dataspace1pv" name="ha_dataspace1pv"/>
                        <input type="hidden" id="ha_dataspace2pv" name="ha_dataspace2pv"/>
                        <input type="hidden" id="ha_dataspace3pv" name="ha_dataspace3pv"/>
                        <input type="hidden" id="ha_dataspace4pv" name="ha_dataspace4pv"/>
                        <input type="hidden" id="ha_caappv" name="ha_caappv"/>
                        <input type="hidden" id="alldisks" value=""/>
		                <input type="hidden" id="alldisksb" value=""/>
		                <input type="hidden" id="alldisksc" value=""/>
		                <input type="hidden" id="alldisksd" value=""/>
		                
		                <input type="hidden" name="ha_vgcaap" value="vgcaap" />
						<% 
						String allhdisksStr = "";
                        for(int h =0; h<hdisknameArray.size(); h++){ 
                        	if(h == 0){
                        		allhdisksStr = hdisknameArray.get(h);
                        	}else{
                        		allhdisksStr = allhdisksStr + "," + hdisknameArray.get(h);
                        	}
                        }
                        %>
						<input type="hidden" id="pvs9" value="<%=allhdisksStr%>"/>
                        <div class="control-group groupborder">
	                      <label class="control-label c-lmini"></label>
	                      <div class="controls controls-mini">
	                        <div class="inputb4"><input type="text" name="ha_vgdb2home" value="vgdb2home" readonly="readonly" style="margin-top:-20px;"/></div>
	                        <div class="inputb2">
	                          <select id="se0" multiple="" class="w85" name="ha_db2homepv_1">
	                          <% 
	                          int h =0; 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 0){
	                          		%>
	                          		<option selected="selected" value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}else{
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}
	                          	%>
	                          <%} %>
	                          </select>
	                        </div>
	                        <div class="inputb4">
	                          <select class="w80" name="ha_db2homemode">
	                              <option value="scalable">scalable VG</option>
	                              <option value="big">big VG</option>
	                          </select>
	                        </div>
	                      </div>
	                    </div>
	                    
	                    <div class="control-group groupborder">
	                      <label class="control-label c-lmini"></label>
	                      <div class="controls controls-mini">
	                        <div class="inputb4"><input type="text" name="ha_vgdb2log" value="vgdb2log" readonly="readonly" style="margin-top:-20px;"/></div>
	                        <div class="inputb2">
	                          <select id="se0" multiple="" class="w85" name="ha_db2logpv_2">
	                          <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 1){
	                          		%>
	                          		<option selected="selected" value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}else{
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}
	                          	%>
	                          <%} %>
	                          </select>
	                        </div>
	                        <div class="inputb4">
	                          <select class="w80" name="ha_db2logmode">
	                              <option value="scalable">scalable VG</option>
	                              <option value="big">big VG</option>
	                          </select>
	                        </div>
	                      </div>
	                    </div>
	                    
	                    <div class="control-group groupborder">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input  type="text" name="ha_vgdb2archlog" value="vgdb2archlog" readonly="readonly" style="margin-top:-20px;"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_db2archlogpv_3">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 2){
	                          		%>
	                          		<option selected="selected" value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}else{
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}
	                          	%>
	                          <%} %>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80" name="ha_db2archlogmode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="control-group groupborder">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input type="text" name="ha_vgdataspace" value="vgdataspace" readonly="readonly" style="margin-top:-20px;"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_dataspace1pv_5">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 3){
	                          		%>
	                          		<option selected="selected" value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}else{
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}
	                          	%>
	                          <%} %>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80" name="ha_dataspacemode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="control-group groupborder">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input  type="text" name="ha_vgcaap11" value="vgcaap(心跳盘)" readonly="readonly" style="margin-top:-20px;"/>
                        </div>
                        <div class="inputb2">
                          <select id="se0" multiple="true" class="w85" name="ha_caappv_9">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 4){
	                          		%>
	                          		<option selected="selected" value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}else{
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
	                          		<%
	                          	}
	                          	%>
	                          <%} %>
                          </select>
                        </div>
                        <div class="inputb4">
                          <select class="w80" name="ha_caapmode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                        
                    
                    <%}%>
                    
                    <%-- <p class="twotit">资源组主节点</p>
                    <div class="control-group groupborder mb0">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4">
                          <select class="w85" name="ha_primaryNode">
                          	  <c:forEach items="${hostNames}" varStatus="num">
                          	  	<option value="${hostNames[num.count-1] }">${hostNames[num.count-1] }</option>
                          	  </c:forEach>
                          	  <option value="${ha_hostname1}">${ha_hostname1}</option>
                          	  <option value="${ha_hostname2}">${ha_hostname2}</option>
                          </select>
                        </div>
                      </div>
                    </div> --%>
                    	<input type="hidden" id="falg" value="<%=falg %>" />
                    	
                    <div>
                      <p class="twotit">HA切换策略 &nbsp;&nbsp;&nbsp;<span style="color:#727272"></span></p>
                    </div>	
                    
                    <div class="control-group groupborder">
                      <label class="control-label c-lmini">启动HA策略</label>
                      <div class="controls controls-mini">
                      	<div class="inputb4">
                          <select class="w80" name="ha_startpolicy">
                              <option value="OUDP">online using node distribution policy</option>
	                          <option value="OHN">online on home node only</option>
	                          <option value="OFAN">online on first available node</option>
	                          <option value="OAAN">online on all available nodes</option>
                          </select>
                        </div>
                        <div class="inputb3">
                          <span class="inputbtxt mr20">HA切换策略</span>
                          <select class="w70" name="ha_fopolicy" >
                              <option value="FNPN">fallover to next priority node in the list</option>
                              <option value="FUDNP">fallover using dynamic node priority</option>
                              <option value="BO">bring offline (on error node only)</option>
                          </select>
                        </div>
                        <div class="inputb3">
                          <span class="inputbtxt mr20">HA回切策略</span>
                          <select class="w70" name="ha_fbpolicy">
                              <option value="NFB">never fallback</option>
                              <option value="FBHPN">fallback to higher priority node in the list</option>
                          </select>
                        </div>
                      </div>
                    </div>	
                    
                    <div>
                      <p class="twotit">NFS &nbsp;&nbsp;&nbsp;<span style="color:#727272"></span></p>
                    </div>	
                    <div class="control-group">
	                    <label class="control-label">是否挂载NFS文件系统</label>
	                    <div class="controls">
	                      <div class="inblock mr20"><label><input type="radio" name="ha_nfsON" value="yes" onClick="checkRadio(1)" />是</label></div>
	                      <div class="inblock mr20"><label><input type="radio" name="ha_nfsON" value="no" onClick="checkRadio(2)" checked/>否</label></div>
	                    </div>
	                </div>
                    
                    <div class="control-group groupborder" id = "divnfs1" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址1</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP1"   value=""  style="width:60%;" /></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录1</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint1"  value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点1</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint1"  value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址2</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP2" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录2</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint2" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点2</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint2" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址3</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP3" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录3</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint3" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点3</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint3" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址4</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP4" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录4</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint4" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点4</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint4" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址5</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP5" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录5</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint5" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点5</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint5" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址6</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP6" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录6</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint6" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点6</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint6" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址7</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP7" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录7</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint7" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点7</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint7" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址8</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP8" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录8</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint8" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点8</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint8" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址9</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP9" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录9</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint9" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点9</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint9" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址10</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP10" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录10</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint10" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点10</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint10" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址11</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP11" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录11</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint11" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点11</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint11" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址12</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP12" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录12</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint12" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点12</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint12" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址13</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP13" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录13</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint13" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点13</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint13" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址14</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP14" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录14</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint14" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点14</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint14" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    <div class="control-group groupborder divnfs" style="display:none;">
                      <div class="controls controls-mini" style="margin-left:0.7%;">
                        <span class="input140 mr20" style="width:142px;">服务端 IP地址15</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsIP15" value=""  style="width:60%;"/></div>
                        <span class="input140 mr20" style="width:128px;">服务端共享目录15</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsSPoint15" value="" style="width:50%;"/><span class="ml5" style="color: #C0C0C0">(如 /home/nfs)</span></div>
                        <span class="input140 mr20" style="width:128px;">客户端挂载点15</span>
                        <div class="inputb4" style="width:20%;"><input  type="text" name="ha_nfsCPoint15" value="" style="width:55%;"/><span class="ml5" style="color: #C0C0C0">(如 /nfs)</span></div>
                      </div>
                    </div>
                    
                    
                    <div style="text-align:center; position:relative; margin-top:20px;">
	                   <span id="spanaddvg" class="pull-login">
	                   </span>
	                   <span id="spancutvg" class="pull-login">
	                   	
	                   </span>
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
