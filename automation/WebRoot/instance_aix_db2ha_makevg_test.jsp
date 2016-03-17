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
		
	});	
	
	//vg数目判断
	var vgnum = 0;
 	function getVGnum(){
		if($("#pvs9").val() == '' || $("#pvs9").val().split(',').length < 6){
//			alert("VG不足！");
 			ymPrompt.win({message:'&nbsp;虚机当前挂载的Hdisk数量小于6，请至少挂载6块！',title:'创建提示！',handler:function(tp){
				//javascript:history.go(-1);
				window.location.href="getAllInstance";
				
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
			//alert(v1 + "---" + flagN + "---" + $(ele).parent().parent().parent().attr("style"));
			if(v1.length == 0){
				if($(ele).parent().parent().parent().attr("style") == ""){
					flagN = false;
				}
			}
			return v1;
		}
	</script>
	<script language="javascript" type="text/javascript">
	var vgcount = 0;
	function addvg(){
		var hvg = [];//隐藏的vg
		$(".control-group.groupborder").each(function (){
			if($(this).attr("style") == "display: none;"){
				hvg.push(this);
			}
		});
		//alert(hvg.length);
		$(hvg.shift()).attr("style","display: display;");
		if(vgcount == 0){
			$("#spancutvg").empty();
			$("#spancutvg").append("&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='cutvg();' class='vgcss' style='width:15%;' value='-  减少VG' />");			
		}
		vgcount++;
		if(vgcount == 3){
			$("#spanaddvg").empty();
			//$("#spanaddvg").html("* 添加VG已达到最大数目");
			$("#spanaddvg").append("<input type='button' class='vgcsst' style='width:15%;ba' value='添加VG已达到最大数目' />&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		$("#div1").after("<input id='input1'/>");
		$("#input1").focus();
		$("#input1").remove();
	}
	
	function cutvg(){
		var hvg = [];
		$(".control-group.groupborder").each(function (){
			if($(this).attr("style") == ""){
				hvg.push(this);
			}
		});
		var tempdiv = hvg.pop();
		$(tempdiv).find(".select2-container.select2-container-multi.w85").find("ul").find("li").each(function(index, e){
			//alert($(this).find("div").length);
			if($(this).find("div").length != 0){
				$(this).remove();
			}
		});
		
		$(tempdiv).attr("style","display: none;");
		
		if(vgcount == 3){
			$("#spanaddvg").empty();
			$("#spanaddvg").append("<input type='button' onclick='addvg();' class='vgcss' style='width:15%;' value='+ 增加VG' />&nbsp;&nbsp;&nbsp;&nbsp;");			
		}
		vgcount--;
		if(vgcount == 0){
			$("#spancutvg").empty();
			//$("#spancutvg").html("* 减少VG已达到最小数目");
			$("#spancutvg").append("&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' class='vgcsst' style='width:15%;ba' value='减少VG已达到最小数目' />");
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
	    <a href="getAllInstance" title="IBM DB2HA" class="tip-bottom"><i class="icon-home"></i>IBM DB2HA</a>
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
	                	<c:forEach items="${ser.address}" var="addr">
	                   	  <c:if test="${ha_ip1==addr.addr}">
	                   	  	<c:if test="${ha_hostname1==ha_primaryNode}">
				                <em class="majornode">主</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;
	               	        </c:if>
	               	        <c:if test="${ha_hostname1!=ha_primaryNode}">
				                <em class="vicenode">备</em>节点<c:out  value="${num.count }"/>&nbsp;&nbsp;&nbsp;
	               	        </c:if>
	               	      </c:if>
	               	      <c:if test="${ha_ip2==addr.addr}">
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
                          	  <c:forEach items="${ser.address}" var="addr">
	                          	  <c:if test="${ha_ip1==addr.addr}">
	                         	  	${ha_hostname1 }
	                      	      </c:if>
	                      	      <c:if test="${ha_ip2==addr.addr}">
	                         	  	${ha_hostname2 }
	                      	      </c:if>
                          	  </c:forEach>
	                      </span>
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
                
                <div class="mainmodule" id="div1">
                  <form action="toNextPage?status=installPageNew" method="post" id="submits" >
                  <h5>资源组名称
                  	<div class="inputb4 ml20"><input type="text" Id="ha_RGNmae" name="ha_RGNmae" value="RG1"/></div>
                    <div class="inputb2">
                    	<b class="input140 mr20">AS名称</b>
                      <input type="text"Id="ha_ASName" name="ha_ASName" value="AS1"/>
                    </div>
                  </h5>
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
	                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdb2home" value="vgdb2home"/></div>
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
	                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdb2log" value="vgdb2log"/></div>
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
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdb2archlog" value="vgdb2archlog"/></div>
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
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdb2backup" value="vgdb2backup"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_db2backuppv_4">
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
                          <select class="w80" name="ha_db2backupmode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    
                    <div class="control-group groupborder" >
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgcaap" value="vgcaap"/></div>
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
                    
                    <div class="control-group groupborder">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdataspace1" value="vgdataspace1"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_dataspace1pv_5">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 5){
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
                          <select class="w80" name="ha_dataspace1mode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="control-group groupborder" style="display:none">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdataspace2" value="vgdataspace2"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_dataspace2pv_6">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 6){
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
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
                          <select class="w80" name="ha_dataspace2mode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="control-group groupborder" style="display:none">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdataspace3" value="vgdataspace3"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_dataspace3pv_7">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          	<%
	                          	if(h == 7){
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
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
                          <select class="w80" name="ha_dataspace3mode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="control-group groupborder" style="display:none">
                      <label class="control-label c-lmini"></label>
                      <div class="controls controls-mini">
                        <div class="inputb4"><input style="margin-top:-20px;" type="text" name="ha_vgdataspace4" value="vgdataspace4"/></div>
                        <div class="inputb2">
                          <select id="se0" multiple="" class="w85" name="ha_dataspace4pv_8">
                              <% 
	                          for(h=0; h<hdisknameArray.size(); h++){ 
	                          %>
	                          <%
	                          	if(h == 8){
	                          		%>
	                          		<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h) %></option>
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
                          <select class="w80" name="ha_dataspace4mode">
                              <option value="scalable">scalable VG</option>
	                          <option value="big">big VG</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    
                        
                    
                    <%}%>
                    <div style="text-align:center; position:relative; margin-top:20px;">
	                   <span id="spanaddvg" class="pull-login">
	                   	<input type="button" onclick="addvg();" class="vgcss" style="width:15%;" value="+ 增加VG" />&nbsp;&nbsp;&nbsp;&nbsp;
	                   </span>
	                   <span id="spancutvg" class="pull-login">
	                   	
	                   </span>
	                </div>
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
