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
</head>
<style type="text/css">
.input140 {
	font-size: 13px;
}

.tooltip-inner{color:black; background-color:#FFB073;}
.tooltip.right .tooltip-arrow{border-right-color:#FFB073;}
</style>
<script type="text/javascript">
	function CheckInput() {
		/* if ($("#was_user").val().trim() == "") {
			ymPrompt.alert("请输入安装用户!");
			return;
		} */
	//	return;
		//alert($("#mq_version").prev().find('a').find('span').text())
		if($("#mq_version").prev().find('a').find('span').text()=='请选择...')
		{
			ymPrompt.alert("请选择MQ安装版本!");
			return;
		}		
		$("#submits").submit();
	}
	
	  $(function(){
		    $('[data-toggle="tooltip"]').tooltip();
		    $('#mq_hostname').tooltip({
		      title:"提示：请输入主机名", 
		      placement:'right'
		    });
		  });	
</script>
<body>
	<!--header start-->
	<div class="header">
		<jsp:include page="topinfo.jsp" flush="true" />
	</div>
	<!--header end-->

	<!--content start-->
	<div class="content">
		<div class="breadcrumb">
			<a href="getAllServers" title="IBM MQ" class="tip-bottom"><i
				class="icon-home"></i>IBM MQ</a> <a class="current">实例配置详细</a>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> <span
								class="icon"><i class="icon-arrow-right"></i></span>
								<h5>说明：</h5>
							</a>
						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">配置当前虚拟机的MQ参数信息</div>
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
							<h5 class="stairtit swapcon">拓扑结构</h5>
							<p class="twotit" style="padding-left: 0px;">
								<em class="majornode">单</em>节点1
							</p>
							<c:forEach items="${servers }" var="ser" varStatus="num"
								begin="0" end="0">
								<div class="column">
									<div class="span12">
										<p>
											<b>主机名:</b><span class="column_txt"> ${ser.name } </span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>IP地址:</b><span class="column_txt">${ser.IP}</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
										</p>
										<p>
											<b>系统配置:</b><span class="column_txt">${ser.HConf }</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<%-- <b>Hypervisor:</b><span class="column_txt">${ser.HVisor }</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; --%>
											<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>
						</div>
						<form action="toMqNextPage?status=confirm" method="post"
							id="submits" name="submits" class="form-horizontal">
							<input type="hidden" id="serId" name="serId" value="${serId}">
							<input type="hidden" id="ptype" name="ptype" value="${ptype}">
							<input type="hidden" id="hostId" name="hostId" value="${hostId}">
							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down"></i>基本属性
								</h5>
								<div class="form-horizontal">
									<div class="control-group">
										<label class="control-label">MQ安装版本</label>
										<div class="controls">
											<div class="inputb2l">
												<select style="width: 47.5%" id="mq_version" class="w48"
													name="mq_version" onchange="showfix(this);">
													<option value="-1" selected="selected">请选择...</option>
												</select>
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">MQ安装补丁</span> <select
													style="width: 47.5%" id="mq_fp" class="w48" name="mq_fp"
													onchange="getVer(this)">
													<option value="-1">请选择...</option>
												</select> <input type="hidden" id="mqfix" name="mqfix" value="-">
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">MQ安装路径</label>
										<div class="controls">
											<c:if test="${os eq 'linux' }">
												<div class="inputb2l">
													<input type="text" class="w45" id="mq_inst_path"
														name="mq_inst_path" value="/opt/mqm" />
												</div>
											</c:if>
											<c:if test="${os eq 'aix' }">
												<div class="inputb2l">
													<input type="text" class="w45" id="mq_inst_path"
														name="mq_inst_path" value="/usr/mqm" />
												</div>
											</c:if>
											<div class="inputb2l">
												<span class="input140 mr20">MQ管理用户</span> <input type="text"
													class="w45" id="mq_user" name="mq_user" value="mqm" />
											</div>
										</div>
									</div>
								</div>
							</div>

							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down icon-chevron-right"></i>队列管理器属性
								</h5>
								<div class="form-horizontal" style="display: none;">
									<div class="control-group">
										<label class="control-label">队列管理器创建方式</label>
										<div class="controls">
											<div class="inputb2l">
												<div class="inblock mr20">
													<label><input type="radio" name="qmgr_method"
														value="yes" onClick="sortRadio1()" checked />默认命令方式</label>
												</div>
												<div class="inblock mr20">
													<label><input type="radio" name="qmgr_method"
														value="no" onClick="sortRadio2()" />不创建</label>
												</div>
											</div>

										</div>
									</div>
									<div class="control-group">
										<label class="control-label">主机名</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="mq_hostname"
													name="mq_hostname" value="${mq_hostname }" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">IP地址</span> <input type="text"
													class="w45" id="mq_ip" name="mq_ip" readonly="readonly"
													value="${mq_ip }" />
											</div>
										</div>
									</div>
									<fieldset id="myfieldset">
										<div class="control-group">
											<label class="control-label">QMGR名称</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="mq_qmgr_name"
														value="QMGR01" name="mq_qmgr_name"
														value="${mq_qmgr_name }" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">主日志文件数</span> <input
														type="text" class="w45" id="mq_qmgr_plog"
														name="mq_qmgr_plog" value="10" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">QMGR数据路径</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="mq_data_path"
														name="mq_data_path" value="/var/mqm/qmgrs" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">次日志文件数</span> <input
														type="text" class="w45" id="mq_qmgr_slog"
														name="mq_qmgr_slog" value="10" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">QMGR日志路径</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="mq_log_path"
														name="mq_log_path" value="/var/mqm/logs" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">日志文件页数(4KB)</span> <input
														type="text" class="w45" id="mq_log_psize"
														name="mq_log_psize" value="4096" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">通道连接是否保持</label>
											<div class="controls">
												<div class="inputb2l">
													<select style="width: 47.5%" id="mq_chl_kalive" class="w48"
														name="mq_chl_kalive">
														<option value="yes" selected="selected">Yes</option>
														<option value="no">No</option>
													</select>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">最大通道数</span> <input type="text"
														class="w45" id="mq_chl_max" name="mq_chl_max" value="1000" />
												</div>
											</div>

										</div>
									</fieldset>
								</div>
							</div>
							<input type="hidden" name="os" id="os" value="${os }">
							<c:if test="${os eq 'linux' }">
								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>系统属性
									</h5>
									<div class="form-horizontal" style="display: none;">
										<div class="control-group">
											<label class="control-label">semmsl</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="lin_semmsl"
														name="lin_semmsl" value="500" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">semmns</span> <input
														type="text" class="w45" id="lin_semmns" name="lin_semmns"
														value="256000" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">semopm</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="lin_semopm"
														name="lin_semopm" value="250" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">semmni</span> <input
														type="text" class="w45" id="lin_semmni" name="lin_semmni"
														value="1024" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">shmmax</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="lin_shmax"
														name="lin_shmax" value="268435456" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">shmmni</span> <input
														type="text" class="w45" id="lin_shmni" name="lin_shmni"
														value="4096" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">shmall</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="lin_shmall"
														name="lin_shmall" value="2097152" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">file-max</span> <input
														type="text" class="w45" id="lin_filemax"
														name="lin_filemax" value="524288" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">nofile</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="lin_nofile"
														name="lin_nofile" value="10240" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">nproc</span> <input type="text"
														class="w45" id="lin_nproc" name="lin_nproc" value="4096" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">tcptime</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="lin_tcptime"
														name="lin_tcptime" value="300" />
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:if>

							<!-- for aix 平台参数 -->
							<c:if test="${os eq 'aix' }">
								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>系统属性
									</h5>
									<div class="form-horizontal" style="display: none;">
										<div class="control-group">
											<label class="control-label">semmni</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="aix_semmni"
														name="aix_semmni" value="Auto Tuned" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">semmns</span> <input
														type="text" class="w45" id="aix_semmns" name="aix_semmns"
														value="Auto Tuned" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">shmmni</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="aix_shmmni"
														name="aix_shmmni" value="Auto Tuned" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">maxuproc</span> <input
														type="text" class="w45" id="aix_maxuproc"
														name="aix_maxuproc" value="4096" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">nofiles</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="aix_nofiles"
														name="aix_nofiles" value="10240" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">data</span> <input type="text"
														class="w45" id="aix_data" name="aix_data" value="-1" />
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">stack</label>
											<div class="controls">
												<div class="inputb2l">
													<input type="text" class="w45" id="aix_stack"
														name="aix_stack" value="-1" />
												</div>

											</div>
										</div>

									</div>
								</div>
							</c:if>


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
			<i class="icon-btn-up"></i> <span>上一页</span>
		</a> <a class="btn btn-info fr btn-down" onclick="CheckInput();"> <span>下一页</span>
			<i class="icon-btn-next"></i>
		</a>
	</div>
	<!--footer end-->
	<script type="text/javascript">
		function sortRadio2() {

			$("#mq_data_path").attr("readonly", "disabled");
			$("#mq_log_path").attr("readonly", "disabled");
			$("#mq_qmgr_plog").attr("readonly", "disabled");
			$("#mq_qmgr_slog").attr("readonly", "disabled");
			$("#mq_log_psize").attr("readonly", "disabled");
			$("#mq_chl_max").attr("readonly", "disabled");
			$("#mq_qmgr_name").attr("readonly", "disabled");

			$("#mq_chl_kalive").attr("readonly", "disabled");
			$("#mq_chl_kalive").attr("disabled", "disabled");
			$("#mq_chl_kalive").prop('disabled', 'disabled');
			$("#myfieldset").hide();
			//	$("#myfieldset").prop('disabled','disabled');

			$("#mq_data_path").val("");
			$("#mq_log_path").val("");
			$("#mq_qmgr_plog").val("");
			$("#mq_qmgr_slog").val("");
			$("#mq_log_psize").val("");
			$("#mq_chl_max").val("");
			$("#mq_qmgr_name").val("");
			$("#mq_chl_kalive").val("");

		}
		function sortRadio1() {

			$("#mq_data_path").val("/var/mqm/qmgrs");
			$("#mq_log_path").val("/var/mqm/logs");
			$("#mq_qmgr_plog").val("10");
			$("#mq_qmgr_slog").val("10");
			$("#mq_log_psize").val("4096");
			$("#mq_chl_max").val("1000");
			$("#mq_qmgr_name").val("QMGR01");
			$("#mq_chl_kalive").val("yes");

			$("#mq_data_path").attr("readonly", false);
			$("#mq_log_path").attr("readonly", false);

			$("#mq_qmgr_plog").attr("readonly", false);
			$("#mq_qmgr_slog").attr("readonly", false);
			$("#mq_log_psize").attr("readonly", false);
			$("#mq_chl_max").attr("readonly", false);
			$("#mq_qmgr_name").attr("readonly", false);
			$("#myfieldset").show();
			$("#mq_chl_kalive").removeAttr("readonly");
			$("#mq_chl_kalive").attr("disabled", false);
			$("#mq_chl_kalive").prop('disabled', false);
		
		}
	</script>

	<script type="text/javascript">
		//自动加载
		function showfix(obj) {

			var ver = obj.value;//获取版本
			$
					.ajax({
						url : "/automation/getmqfixver",
						data : {
							version : ver
						},
						type : 'post',
						dataType : 'json',
						success : function(result) {
							
							$("#mq_fp").empty();
							var str="<option value='-1'>"+ "请选择..." + "</option>";;
							for (var i = 0; i < result.length; i++) {
								if (result[i].key == '-10')// -10表示只显示请选择...
								{
									$("#mqfix").val('请选择...');
									break;
								} if(result[i].key=='-12')//表示是版本包含补丁
								{
									$("#mqfix").val('-12');
									str += "<option value='" + result[i].value + "'>"
									+ result[i].key + "</option>";
								}else {
									
									str += "<option value='" + result[i].value + "'>"
											+ result[i].key + "</option>";
									
								}
								
							}
							$("#mq_fp").append(str);
							
						}
					});
		}
		function getVer(obj) {
			if($("#mq_fp").find("option:selected").text()=='请选择...')
			{
				$("#mqfix").val('-');
			}else
			$("#mqfix").val($("#mq_fp").find("option:selected").text());
		}
		
		window.onload = function() {
			$.ajax({
				url : '/automation/getmqversion',
				data : {
					product : "mq",
					platform : 'linux'
				},
				type : 'post',
				dataType : 'json',
				success : function(result) {
					$("#mqfix").val('-');
					var str;
					for (var i = 0; i < result.length; i++) {
						if (result[i].key == '.')// .号表示没有安装文件
						{
							break;
						} else {
							str += "<option value='" + result[i].key + "'>"
									+ result[i].value + "</option>";
						}
					}
					$("#mq_version").append(str);
				}
			})
		}

	
	</script>
</body>
</html>
