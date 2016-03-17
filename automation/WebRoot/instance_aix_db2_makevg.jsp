<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@include file="loginCheck.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.fasterxml.jackson.databind.*"%>
<%@ page import="com.fasterxml.jackson.databind.node.*"%>
<!DOCTYPE HTML>
<html>
<head>
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="header.jsp" flush="true" />
<title>自动化部署平台</title>
<style type="text/css">
.vgcss {
	width: 100%;
	height: 40px;
	background: #2592e0;
	color: #fff;
	font-size: 16px;
	font-weight: bold;
	border: 1px solid #2592e0;
	-moz-border-radius: 4px; /* Gecko browsers */
	-webkit-border-radius: 4px; /* Webkit browsers */
	border-radius: 4px; /* W3C syntax */
}

.vgcsst {
	width: 100%;
	height: 40px;
	background: #b0b0b0;
	color: #fff;
	font-size: 16px;
	font-weight: bold;
	border: 1px solid #fff;
	-moz-border-radius: 4px; /* Gecko browsers */
	-webkit-border-radius: 4px; /* Webkit browsers */
	border-radius: 4px; /* W3C syntax */
}

#zhujiming {
	position: relative;
	top: -3px;
}
</style>
<script type="text/javascript">
	//监听创建div
	var a = [ 0, 0, 0, 0 ];
	$(document).ready(
			function() {
				$(".select2-container.select2-container-multi.w85").each(
						function(index, ele) {
							a[index] = $(ele).find("ul").find("li").length;
						});

				$("#alldisks").val(a);

				$("#divnfs1").find(":input").each(function(index, e) {
					$(this).val("");
				});
				$(".divnfs").each(function() {
					$(this).find(":input").each(function(index, e) {
						$(this).val("");
					});
				});

			});

	//vg数目判断
	var vgnum = 0;
	function getVGnum() {
		if ($("#pvs9").val() == '' || $("#pvs9").val().split(',').length < 4) {
			ymPrompt.win({
				message : '&nbsp;虚机当前挂载的Hdisk数量小于4，请至少挂载4块！',
				title : '创建提示！',
				handler : function(tp) {
					window.location.href = "getAllServers";
				},
				btn : [ [ '返回主页', 'yes' ] ],
				closeBtn : false,
				icoCls : 'ymPrompt_alert'
			});
		}
	}

	var flag = true;

	var b;
	var t = 0;
	function test222() {
		if (vgnum == 1) {
			getVGnum();
		}
		vgnum = vgnum + 1;
		b = [ 0, 0, 0, 0 ];
		$(".select2-container.select2-container-multi.w85").each(
				function(index, ele) {
					b[index] = $(ele).find("ul").find("li").length;
				});

		$("#alldisksb").val(b);

		flag = true;
		for (var i = 0; i < b.length; i++) {
			if (a[i] != b[i]) {
				flag = false;
			}
		}
		if (t == 0) {
			$(".select2-container.select2-container-multi.w85").each(
					function(index, ele) {
						$(ele).next().empty();
					});//进入页面先删除所有的option
			var tt = [];//存放初始化页面时已经选择的选项
			$(".select2-container.select2-container-multi.w85").each(
					function(index, ele) {
						$(ele).find("ul").find("li").each(
								function(index3, ele3) {
									if ($(ele3).find("div").text() != "") {
										tt.push($(ele3).find("div").text());
									}
								});//
					});
			var ttt = $("#pvs9").val().split(",");//存放所有选项
			var tttt = [];//存放所有选项是否被选择标记
			for (var i = 0; i < ttt.length; i++) {
				if (ttt[i] == tt[i]) {
					tttt.push(true);
				} else {
					tttt.push(false);
				}
			}
			var ttemp = [];//存放未选择选项
			for (var i = 0; i < ttt.length; i++) {
				if (tttt[i] == false) {
					ttemp.push(ttt[i]);
				}
			}

			$(".select2-container.select2-container-multi.w85").each(
					function(index, ele) {
						for (var j = 0; j < ttemp.length; j++) {
							var tab = "<option>" + ttemp[j] + "</option>";
							$(ele).next().append(tab);
						}
					});
		}
		if (!flag) {
			$(".select2-container.select2-container-multi.w85").each(
					function(index, ele) {
						$(ele).next().empty();
					});
			$("#alldisks").val($("#alldisksb").val());
			a = b;
			//alert("删完了");
			var c = [];
			$(".select2-container.select2-container-multi.w85").each(
					function(index, ele) {
						$(ele).find("ul").find("li").each(
								function(index3, ele3) {
									if ($(ele3).find("div").text() != "") {
										c.push($(ele3).find("div").text());
									}
								});//
						//alert("添加了");
					});

			if (t == 0) {
				$("#alldisksc").val(c);
				t = 1;
			} else {
				$("#alldisksd").val(c);
				$("#alldisksc").val(c);
			}
			if ($("#alldisksc").val() != $("#pvs9").val()
					|| $("#pvs9").val() != $("#alldisksd").val()) {
				var la = $("#pvs9").val().split(",");
				var ld = $("#alldisksc").val().split(",");
				var dd = [];
				for (var m = 0; m < la.length; m++) {
					for (var n = 0; n < ld.length; n++) {
						if (la[m] == ld[n]) {
							dd.push(m);
						}
					}
				}
				for (var k = 0; k < dd.length; k++) {
					if (k == 0) {
						la.splice(dd[k], 1);
					} else {
						la.splice(dd[k] - k, 1);
					}
				}
				//alert("la=" + la);
				$(".select2-container.select2-container-multi.w85").each(
						function(index, ele) {
							for (var j = 0; j < la.length; j++) {
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
<script type="text/javascript">
	//是否为空标记
	var flagN = true;
	function CheckInput() {

		$(".select2-container.select2-container-multi.w85").each(
				function(index, ele) {
					if ($(ele).next().attr("name") == "db2homepv_1") {

						$("#db2homepv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "db2logpv_2") {
						$("#db2logpv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "db2archlogpv_3") {
						$("#db2archlogpv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "db2backuppv_4") {
						$("#db2backuppv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "dataspace1pv_5") {
						$("#dataspacepv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "dataspace2pv_6") {
						$("#dataspace2pv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "dataspace3pv_7") {
						$("#dataspace3pv").val(addMsg(ele));
					}
					if ($(ele).next().attr("name") == "dataspace4pv_8") {
						$("#dataspace4pv").val(addMsg(ele));
					}
				});
		if (!flagN) {
			ymPrompt.alert("每个VG请至少选择一个PV!");
			flagN = true;
			return;
		}

		var returnnfs = 0;
		if ($("#divnfs1").attr("style") == "") {
			$("#divnfs1").find(":input").each(function(index, e) {
				if ($(this).val() == '') {
					returnnfs++;
				}
			});
		}

		$(".divnfs").each(function() {
			if ($(this).attr("style") == "") {
				$(this).find(":input").each(function(index, e) {
					if ($(this).val() == '') {
						returnnfs++;
					}
				});
			}
		});

		if (returnnfs > 0) {
			ymPrompt.alert("挂载NFS文件系统，NFS参数不能为空！");
			returnnfs = 0;
			return;
		}

		if ($("#hostname").val().length == 0) {
			ymPrompt.alert("主机名不能为空!");
			return;
		}
		$("#submits").submit();
	}

	//添加VG
	function addMsg(ele) {
		var v1 = [];
		$(ele).find("ul").find("li").each(function(index3, ele3) {
			if ($(ele3).find("div").text() != "") {
				var str = $(ele3).find("div").text();
				var strnum = str.lastIndexOf("(");
				v1.push(str.substring(0, strnum));
			}
		});
		//	alert(v1);
		if (v1.length == 0) {
			flagN = false;
		}
		return v1;
	}
</script>
<script type="text/javascript">
	var vgcount = 0;
	function checkRadio(flag) {
		if (flag == 1 && $("#spanaddvg").find(":input").length == 0
				&& $("#spancutvg").find(":input").length == 0) {
			$("#divnfs1").attr("style", "display: display;");
			$("#spanaddvg")
					.append(
							"<input type='button' onclick='addNFS();' class='vgcss' style='width:15%;' value='+ 增加NFS' />&nbsp;&nbsp;&nbsp;&nbsp;");
			$("#div1").after("<input id='input1'/>");
			$("#input1").focus();
			$("#input1").remove();
		}
		if (flag == 2) {
			$("#divnfs1").find(":input").each(function(index, e) {
				$(this).val("");
			});
			$("#divnfs1").attr("style", "display: none;");
			$(".divnfs").each(function() {
				$(this).find(":input").each(function(index, e) {
					$(this).val("");
				});
				$(this).attr("style", "display: none;");
			});
			$("#spanaddvg").empty();
			$("#spancutvg").empty();
			vgcount = 0;
		}
	}

	function addNFS() {
		var hvg = [];//隐藏的NFS
		$(".divnfs").each(function() {
			if ($(this).attr("style") == "display: none;") {
				hvg.push(this);
			}
		});
		//alert(hvg.length);
		$(hvg.shift()).attr("style", "display: display;");
		if (vgcount == 0) {
			$("#spancutvg").empty();
			$("#spancutvg")
					.append(
							"&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='cutNFS();' class='vgcss' style='width:15%;' value='-  减少NFS' />");
		}
		vgcount++;
		if (vgcount == 14) {
			$("#spanaddvg").empty();
			//$("#spanaddvg").html("* 添加VG已达到最大数目");
			$("#spanaddvg")
					.append(
							"<input type='button' class='vgcsst' style='width:15%;ba' value='添加NFS已达到最大数目' />&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		$("#div1").after("<input id='input1'/>");
		$("#input1").focus();
		$("#input1").remove();
		//alert(vgcount);
	}

	function cutNFS() {
		var hvg = [];
		$(".divnfs").each(function() {
			if ($(this).attr("style") == "") {
				hvg.push(this);
			}
		});
		var tempdiv = hvg.pop();
		$(tempdiv).find(":input").each(function(index, e) {
			$(this).val("");
		});
		$(tempdiv).attr("style", "display: none;");

		if (vgcount == 14) {
			$("#spanaddvg").empty();
			$("#spanaddvg")
					.append(
							"<input type='button' onclick='addNFS();' class='vgcss' style='width:15%;' value='+ 增加NFS' />&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		vgcount--;
		if (vgcount == 0) {
			$("#spancutvg").empty();
			//$("#spancutvg").html("* 减少NFS已达到最小数目");
			$("#spancutvg")
					.append(
							"&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' class='vgcsst' style='width:15%;ba' value='减少NFS已达到最小数目' />");
		}
		//alert(vgcount);
	}
</script>
</head>

<body>
	<!--header start-->
	<div class="header">
		<jsp:include page="topinfo.jsp" flush="true" />
	</div>
	<!--header end-->


	<!--content start-->
	<div class="content">
		<div class="breadcrumb">
			<a href="getAllServers" title="IBM DB2" class="tip-bottom"><i
				class="icon-home"></i>IBM DB2</a> <a class="current">实例配置详细</a>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> <span
								class="icon"> <i class="icon-arrow-right"></i>
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
						<div class="mainmodule">
							<h5 class="stairtit">拓扑结构</h5>
							<p class="twotit" style="padding-left: 0px;">
								<em class="majornode">单</em>节点1
							</p>

							<c:forEach items="${servers }" var="ser" varStatus="num"
								begin="0" end="0">
								<div class="column">
									<div class="span12">
										<p>
											<b>主机名:</b> <span class="column_txt"> ${ser.name } </span> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>IP地址:</b><span class="column_txtl">${ser.IP}</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
										</p>
										<p>
											<b>系统配置:</b><span class="column_txt">${ser.HConf }</span> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<!-- <b>挂卷:</b><span class="column_txt">volume_HB , volume_Data</span> --> 
											<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>

							<div class="mainmodule" id="div1">
								<form action="toDb2NextPage?status=installPageNew" method="post"
									id="submits">
									<div class="form-horizontal processInfo">
										<p class="twotit">
											资源组共享VG信息 &nbsp;&nbsp;&nbsp;<span style="color: #727272">*注：每一特定hdisk能且只能存在于某一VG中。</span>
										</p>


										<!-- IP Begin -->
										<input type="hidden" id="serId" name="serId" value="${serId }">
										<input type="hidden" id="ip" name="ip" value="${ip}">
										<input type="hidden" id="bootip" name="bootip" value="${ip}">
										<input type="hidden" id="hostId" name="hostId"
											value="${hostId}">
										<!-- IP End -->
										<input type="hidden" id="ptype" name="ptype" value="${ptype}">
										<!-- makeVg Begin -->
										<input type="hidden" id="ppSize" name="ppSize" value="64">
										<input type="hidden" id="autoOn" name="autoOn" value="n">
										<input type="hidden" id="factor" name="factor" value="1">
										<input type="hidden" id="vgType" name="vgType" value="S">
										<input type="hidden" id="concurrent" name="concurrent"
											value="y">
										<!-- makeVg End -->
										<%
											String allHdisk = String.valueOf(request.getAttribute("allHdisk"));

											ObjectMapper om = new ObjectMapper();
											ArrayNode list = (ArrayNode) om.readTree(allHdisk);
											System.out.println("=====123====" + list);
											Boolean falg = true;
											if (list.size() == 0) {
												falg = false;
											}
											int le = list.size();
											if (le > 1) {
												le = 1;
											}
											for (int i = 0; i < 1; i++) {
												ArrayNode items = (ArrayNode) om.readTree(list.get(i).toString());
												if (items.size() == 0) {
													falg = false;
												}
												System.out.println("jsp::falg:" + falg + items);
												List<String> hdisknameArray = new ArrayList<String>();
												for (int j = 0; j < items.size(); j++) {
													ObjectNode node = (ObjectNode) items.get(j);
													System.out.println("jsp::node:" + node);
													String hdiskname = node.get("hdiskname").asText();
													//String hdiskid = node.get("hdiskid").asText();
													hdisknameArray.add(hdiskname);
												}
										%>

										<input type="hidden" id="db2homepv" name="db2homepv" /> <input
											type="hidden" id="db2logpv" name="db2logpv" /> <input
											type="hidden" id="db2archlogpv" name="db2archlogpv" /> <input
											type="hidden" id="dataspacepv" name="dataspacepv" /> <input
											type="hidden" id="alldisks" value="" /> <input type="hidden"
											id="alldisksb" value="" /> <input type="hidden"
											id="alldisksc" value="" /> <input type="hidden"
											id="alldisksd" value="" />
										<%
											String allhdisksStr = "";
												for (int h = 0; h < hdisknameArray.size(); h++) {
													if (h == 0) {
														allhdisksStr = hdisknameArray.get(h);
													} else {
														allhdisksStr = allhdisksStr + "," + hdisknameArray.get(h);
													}
												}
										%>
										<input type="hidden" id="pvs9" value="<%=allhdisksStr%>" />
										<div class="control-group groupborder">
											<label class="control-label c-lmini"></label>
											<div class="controls controls-mini">
												<div class="inputb4">
													<input type="text" name="vgdb2home" value="vgdb2home"
														readonly="readonly" style="margin-top: 0px;" />
												</div>
												<div class="inputb2">
													<select id="se0" multiple="" class="w85" name="db2homepv_1">
														<%
															int h = 0;
																for (h = 0; h < hdisknameArray.size(); h++) {
														%>
														<%
															if (h == 0) {
														%>
														<option selected="selected"
															value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															} else {
														%>
														<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															}
														%>
														<%
															}
														%>
													</select>
												</div>
												<div class="inputb4">
													<select class="w80" name="db2homemode">
														<option value="scalable">scalable VG</option>
														<option value="big">big VG</option>
													</select>
												</div>
											</div>
										</div>

										<div class="control-group groupborder">
											<label class="control-label c-lmini"></label>
											<div class="controls controls-mini">
												<div class="inputb4">
													<input type="text" name="vgdb2log" value="vgdb2log"
														readonly="readonly" style="margin-top: 0px;" />
												</div>
												<div class="inputb2">
													<select id="se0" multiple="" class="w85" name="db2logpv_2">
														<%
															for (h = 0; h < hdisknameArray.size(); h++) {
														%>
														<%
															if (h == 1) {
														%>
														<option selected="selected"
															value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															} else {
														%>
														<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															}
														%>
														<%
															}
														%>
													</select>
												</div>
												<div class="inputb4">
													<select class="w80" name="db2logmode">
														<option value="scalable">scalable VG</option>
														<option value="big">big VG</option>
													</select>
												</div>
											</div>
										</div>

										<div class="control-group groupborder">
											<label class="control-label c-lmini"></label>
											<div class="controls controls-mini">
												<div class="inputb4">
													<input type="text" name="vgdb2archlog" value="vgdb2archlog"
														readonly="readonly" style="margin-top: 0px;" />
												</div>
												<div class="inputb2">
													<select id="se0" multiple="" class="w85"
														name="db2archlogpv_3">
														<%
															for (h = 0; h < hdisknameArray.size(); h++) {
														%>
														<%
															if (h == 2) {
														%>
														<option selected="selected"
															value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															} else {
														%>
														<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															}
														%>
														<%
															}
														%>
													</select>
												</div>
												<div class="inputb4">
													<select class="w80" name="db2archlogmode">
														<option value="scalable">scalable VG</option>
														<option value="big">big VG</option>
													</select>
												</div>
											</div>
										</div>

										<div class="control-group groupborder">
											<label class="control-label c-lmini"></label>
											<div class="controls controls-mini">
												<div class="inputb4">
													<input type="text" name="vgdataspace" value="vgdataspace"
														readonly="readonly" style="margin-top: 0px;" />
												</div>
												<div class="inputb2">
													<select id="se0" multiple="" class="w85"
														name="dataspace1pv_5">
														<%
															for (h = 0; h < hdisknameArray.size(); h++) {
														%>
														<%
															if (h == 3) {
														%>
														<option selected="selected"
															value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															} else {
														%>
														<option value="<%=hdisknameArray.get(h)%>"><%=hdisknameArray.get(h)%></option>
														<%
															}
														%>
														<%
															}
														%>
													</select>
												</div>
												<div class="inputb4">
													<select class="w80" name="dataspacemode">
														<option value="scalable">scalable VG</option>
														<option value="big">big VG</option>
													</select>
												</div>
											</div>
										</div>

										<%
											}
										%>



										<input type="hidden" id="falg" value="<%=falg%>" />


										<div>
											<p class="twotit">
												NFS &nbsp;&nbsp;&nbsp;<span style="color: #727272"></span>
											</p>
										</div>
										<div class="control-group">
											<label class="control-label">是否挂载NFS文件系统</label>
											<div class="controls">
												<div class="inblock mr20">
													<label><input type="radio" name="nfsON" value="yes"
														onClick="checkRadio(1)" />是</label>
												</div>
												<div class="inblock mr20">
													<label><input type="radio" name="nfsON" value="no"
														onClick="checkRadio(2)" checked />否</label>
												</div>
											</div>
										</div>

										<div class="control-group groupborder" id="divnfs1"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址1</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP1" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录1</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint1" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点1</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint1" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址2</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP2" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录2</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint2" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点2</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint2" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址3</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP3" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录3</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint3" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点3</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint3" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址4</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP4" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录4</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint4" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点4</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint4" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址5</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP5" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录5</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint5" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点5</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint5" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址6</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP6" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录6</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint6" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点6</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint6" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址7</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP7" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录7</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint7" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点7</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint7" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址8</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP8" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录8</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint8" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点8</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint8" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址9</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP9" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录9</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint9" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点9</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint9" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址10</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP10" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录10</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint10" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点10</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint10" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址11</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP11" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录11</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint11" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点11</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint11" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址12</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP12" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录12</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint12" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点12</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint12" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址13</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP13" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录13</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint13" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点13</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint13" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址14</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP14" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录14</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint14" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点14</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint14" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>
										<div class="control-group groupborder divnfs"
											style="display: none;">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">服务端
													IP地址15</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsIP15" value=""
														style="width: 60%;" />
												</div>
												<span class="input140 mr20" style="width: 128px;">服务端共享目录15</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsSPoint15" value=""
														style="width: 50%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /home/nfs)</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">客户端挂载点15</span>
												<div class="inputb4" style="width: 20%;">
													<input type="text" name="nfsCPoint15" value=""
														style="width: 55%;" /><span class="ml5"
														style="color: #C0C0C0">(如 /nfs)</span>
												</div>
											</div>
										</div>


										<div style="text-align: center; position: relative; margin-top: 20px;">
											<span id="spanaddvg" class="pull-login"></span> 
											<span id="spancutvg" class="pull-login"></span>
										</div>

									</div>
									<!-- 新增加的主机名和提示 -->
									<div class="control-group">
										<p class="twotit">
											<b id="zhujiming">主机名：</b> 
											<input class="form-control" type="text" id="hostname" 
											       name="hostname" value="${hostname }"> 
											<span style="color: red;">&nbsp;&nbsp;*提示：若编辑可修改主机名</span>
										</p>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<div class="columnfoot" style="position: fixed;">
		<a class="btn btn-info btn-up" onclick="javascript:history.back(-1);">
			<i class="icon-btn-up"></i> <span>上一页</span>
		</a> <a class="btn btn-info fr btn-next" onclick="CheckInput();"> <span>下一页</span>
			<i class="icon-btn-next"></i>
		</a>
	</div>
	<!--content end-->

</body>
</html>
