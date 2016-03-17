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
.col-sm-1, .col-sm-2, .col-sm-3, .col-sm-4, .col-sm-5, .col-sm-6,
	.col-sm-7, .col-sm-8, .col-sm-9, .col-sm-10, .col-sm-11, .col-sm-12 { float: left;}

.col-md-4 {
	width: 33.3%;
	align: right;
	float: left;
}
.col-sm-4 { width: 33.33333333% }
.col-sm-8 { width: 62.5% }



.col-sm-12 { width: 100% }

.col-sm-11 { width: 91.66666667% }

.col-sm-10 { width: 83.33333333% }

.col-sm-9 { width: 75% }

.col-sm-7 { width: 58.33333333% }

.col-sm-6 { width: 50% }

.col-sm-5 { width: 41.66666667% }

.col-sm-3 { width: 23% }

.col-sm-2 { width: 16.66666667% }

.col-sm-1 { width: 8.33333333% }

.form-group {
	margin-right: -15px;
	margin-left: -15px
}

.container {
	padding-right: 15px;
	padding-left: 15px;
	margin-right: auto;
	margin-left: auto
}

@media ( min-width :768px) {
	.container {
		width: 750px
	}
}

@media ( min-width :992px) {
	.container {
		width: 970px
	}
}

@media ( min-width :1200px) {
	.container {
		width: 1170px
	}
}
label{font-size:13px;}
.input140{font-size: 13px;}
.one{display:none;}

.tooltip-inner{color:black; background-color:#FFB073;}
.tooltip.right .tooltip-arrow{border-right-color:#FFB073;}
</style>

<body>
	<!--header start-->
	<div class="header">
		<jsp:include page="topinfo.jsp" flush="true" />
	</div>
	<!--header end-->

	<!--content start-->
	<div class="content">
		<div class="breadcrumb">
			<a href="getAllServers" title="IBM OS Agent" class="tip-bottom"><i class="icon-home"></i>IBM OS Agent</a> 
			<a class="current">实例配置详细</a>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> 
							   <span class="icon"><i class="icon-arrow-right"></i></span>
							   <h5>说明：</h5>
							</a>
						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">配置当前虚拟机的OS Agent参数信息</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">

						<div class="mainmodule">     <!--**************** 拓扑结构 ****************-->
						   <h5 class="stairtit swapcon">拓扑结构</h5>
						   <c:forEach items="${servers }" var="ser" varStatus="num">
							 <p class="twotit" style="padding-left: 0px;">
								节点 <c:out value="${num.count }" />
							 </p>
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
										<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
									</p>
							    </div>
							 </div>
							</c:forEach>
						</div>
						
						<form action="toosagentNextPage?status=confirm" method="post" id="submits" name="submits" class="form-horizontal">
							<input type="hidden" id="serId" name="serId" value="${serId}">
							<input type="hidden" id="ptype" name="ptype" value="${ptype}">
							<input type="hidden" id="hostId" name="hostId" value="${hostId}">
							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down"></i>基本信息
								</h5>
								<div class="form-horizontal">
									<div class="control-group">
										<label class="control-label">产品类型</label>
										<div class="controls">
											<div class="inputb2l">
											    <select id="product_type" class="w48" name="product_type">
													<option value="os agent" selected="selected">OS Agent</option>
													<option value="db Agent">DB Agent</option>
												</select>
											</div>										
											<div class="inputb2l">
												<span class="input140 mr20">软件版本</span> 
												<select id="product_version" class="w48" name="product_version">
													<option value="6.3" selected="selected">6.3</option>
												</select>											
											</div>
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">产品代码</label>
										<div class="controls">
											<div class="inputb2l">
												<select id="product_code" class="w48" name="product_code">
													<option value="lz" selected="selected">LZ</option>
													<option value="ux">UX</option>
													<option value="nt">NT</option>
												</select>
											</div>										
											<div class="inputb2l">
												<span class="input140 mr20">补丁版本</span> 
												<input type="text" class="w45" id="product_patch" name="product_patch" value="6.3.0.5" />
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">部署路径</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="install_path"
													name="install_path" value="/opt/IBM/ITM" />
											</div>										
											<div class="inputb2l">
												<span class="input140 mr20">管理用户</span> 
												<input type="text" class="w45" id="manager_user" name="manager_user" value="root/tivoli" />
											</div>
										</div>
									</div>
								</div>

							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down icon-chevron-right"></i>配置信息
								</h5>
								<div class="form-horizontal" style="display: block;" >
									<div class="control-group">
										<label class="control-label">是否主备TEMS</label>
										<div class="controls">
											<div class="inputb2l">
												<div class="inblock mr20">
													<label><input type="radio" name="ha_tems" value="是" onClick="sortRadio1()" />是</label>
												</div>
												<div class="inblock mr20">
													<label><input type="radio" name="ha_tems" value="否" onClick="sortRadio2()" checked />否</label>
												</div>
											</div>
										</div>
									</div>
																									
								<div class="control-group" style="padding-bottom: 9px;padding-top:9px;">									  
									<div class="col-md-4" style="margin-left:110px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">主TEMS IP</label>
											<div class="col-sm-8" style="margin-left:-77px;">
												<input type="text" name="pri_tems" value="127.0.0.1" class="form-control" />
											</div>
										</div>
									</div>																																						  
								    <div class="col-md-4" style="margin-left:-80px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">网络协议</label>
											<div style="float:left;margin-left:-100px;">
												<select style="width:230px;" name="pri_protocol" class="pri_protocol">
													<option value="ip.pipe" selected="selected">IP.PIPE</option>
													<option value="tcp/ip">TCP/IP</option>
												</select>
											</div>
										</div>
									</div> 										
									<div class="col-md-4" style="margin-left:-148px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">连接端口</label>
											<div class="col-sm-8" style="margin-left:-100px;">
												<input name="pri_port" type="text" value="1918" class="form-control pri_port"/>
											</div>
										</div>
									</div> 								 										
								</div> 
								
								<div class="control-group min" style="padding-bottom: 9px;padding-top:9px;display:none;">									  
									<div class="col-md-4" style="margin-left:110px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">备TEMS IP</label>
											<div class="col-sm-8" style="margin-left:-77px;">
												<input type="text" name="sec_tems" value="127.0.0.1" class="form-control" />
											</div>
										</div>
									</div>																																						  
								    <div class="col-md-4" style="margin-left:-80px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">网络协议</label>
											<div style="float:left;margin-left:-100px;">
												<select style="width:230px;" name="sec_protocol" class="sec_protocol">
													<option value="ip.pipe" selected="selected">IP.PIPE</option>
													<option value="tcp/ip">TCP/IP</option>
												</select>
											</div>
										</div>
									</div> 										
									<div class="col-md-4" style="margin-left:-148px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">连接端口</label>
											<div class="col-sm-8" style="margin-left:-100px;">
												<input name="sec_port" type="text" value="1918" class="form-control sec_port"/>
											</div>
										</div>
									</div> 								 										
								</div>	
								
							<c:forEach items="${servers }" var="ser">
								<div class="control-group" style="padding-bottom: 9px;padding-top:9px;">									  
									<div class="col-md-4" style="margin-left:133px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">主机名</label>
											<div class="col-sm-8" style="margin-left:-100px;">
												<input type="text" readonly="readonly" 
												       name="all_hostnames" value="${ser.name }" class="form-control" />
											</div>
										</div>
									</div>																																						  
								    <div class="col-md-4" style="margin-left:-90px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">IP地址</label>
											<div style="float:left;margin-left:-113px;width:270px;">
												<input type="text" readonly="readonly"
												       name="all_ips" value="${ser.IP }" class="form-control"/>
											</div>
										</div>
									</div> 										
									<div class="col-md-4" style="margin-left:-165px;">
										<div class="form-group">
											<label class="col-sm-4" style="padding-top: 2.5px;">TEPS名称</label>
											<div class="col-sm-8" style="margin-left:-95px;">
												<input type="text" name="teps_name" id="teps_name" value="${ser.name }" class="form-control "/>
											</div>
										</div>
									</div> 								 										
								</div>	
							  </c:forEach>																													
									
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
			<i class="icon-btn-up"></i> <span>上一页</span>
		</a> 
		<a class="btn btn-info fr btn-down" onclick="CheckInput();"> <span>下一页</span>
			<i class="icon-btn-next"></i>
		</a>
	</div>
	<!--footer end-->		
	
    <script type="text/javascript">
    function sortRadio1(){   //按钮"是" 
    	$(".min").css("display","block");  //点击"是"的时候显示备TEMS IP信息 
    }
    
    function sortRadio2(){   //按钮"否"  
    	$(".min").css("display","none");   //点击"否"的时候隐藏备TEMS IP信息 
    }
    </script>
</body>
</html>
