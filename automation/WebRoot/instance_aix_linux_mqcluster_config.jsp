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
.col-md-1, .col-md-2, .col-md-3, .col-md-4, .col-md-5, .col-md-6,
	.col-md-7, .col-md-8, .col-md-9, .col-md-10, .col-md-11, .col-md-12 {
	float: left;}

.col-sm-1, .col-sm-2, .col-sm-3, .col-sm-4, .col-sm-5, .col-sm-6,
	.col-sm-7, .col-sm-8, .col-sm-9, .col-sm-10, .col-sm-11, .col-sm-12 {
	float: left;}

.col-md-3 {
	width: 25%;
	align: right;
}

.col-sm-12 { width: 100% }

.col-sm-11 { width: 91.66666667% }

.col-sm-10 { width: 83.33333333% }

.col-sm-9 { width: 75% }

.col-sm-8 { width: 45% }

.col-sm-7 { width: 58.33333333% }

.col-sm-6 { width: 50% }

.col-sm-5 { width: 41.66666667% }

.col-sm-4 { width: 33.33333333% }

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
			<a href="getAllServers" title="IBM MQCluster" class="tip-bottom"><i class="icon-home"></i>IBM MQCluster</a> 
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
							<div class="widget-content">配置当前虚拟机的MQCluster参数信息</div>
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
						
						<form action="toMqClusterNextPage?status=confirm" method="post" id="submits" name="submits" class="form-horizontal">
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
												<span class="input140 mr20">MQ安装补丁</span> 
												<select style="width: 47.5%" id="mq_fp" class="w48" name="mq_fp"
													onchange="getVer(this)">
													<option value="-1">请选择...</option>
												</select> <input type="hidden" id="mqfix" name="mqfix" value="-">
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">MQ安装路径</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="mq_inst_path"
													name="mq_inst_path" value="/opt/mqm" />
											</div>										
											<div class="inputb2l">
												<span class="input140 mr20">MQ管理用户</span> 
												<input type="text" class="w45" id="mq_user" name="mq_user" value="mqm" />
											</div>
										</div>
									</div>
								</div>
							</div>

							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down icon-chevron-right"></i>队列管理器属性
								</h5>
								<div class="form-horizontal" style="display: block;" >
									<div class="control-group">
										<label class="control-label">队列管理器创建方式</label>
										<div class="controls">
											<div class="inputb2l">
												<div class="inblock mr20">
													<label><input type="radio" name="qmgr_method" value="默认命令方式" onClick="sortRadio1()" checked />默认命令方式</label>
												</div>
												<div class="inblock mr20">
													<label><input type="radio" name="qmgr_method" value="不创建" onClick="sortRadio2()" />不创建</label>
												</div>
											</div>
										</div>
									</div>
									
								<c:forEach items="${servers }" var="ser">							
									<div class="control-group add_info" style="padding-bottom: 9px;padding-top:9px;">									  
										<div class="col-md-3" style="margin-left:135px;">
											<div class="form-group">
												<label class="col-sm-2" style="padding-top: 2.5px;">主机名</label>
												<div class="col-sm-8" style="margin-left:12px;">
													<input type="text" name="all_hostnames" value="${ser.name }" class="form-control" onchange="change_hostname(this)"/>
												</div>
											</div>
										</div>										
										<div class="col-md-3" style="margin-left:-80px;">
											<div class="form-group">
												<label class="col-sm-2" style="padding-top: 2.5px;">IP地址</label>
												<div class="col-sm-8" style="padding-left:-10px;">
													<input readonly="readonly" name="all_ips" type="text" value="${ser.IP }" class="form-control all_ips"/>
												</div>
											</div>
										</div>																		  
										<div class="col-md-3" style="margin-left:-90px;">
											<div class="form-group">
												<label class="col-sm-3" style="padding-top: 2.5px;">QMGR名称</label>
												<div class="col-sm-8">
													<input type="text" class="all_qmgr_names" value="QMGR01" name="all_qmgr_names" />
												</div>
											</div>
										</div>
										<div class="col-md-3" style="margin-left:-70px;">
											<div class="form-group">
												<label class="col-sm-3" style="padding-top: 2.5px;">完全存储库</label>
												<div style="float:left;">
													<select style="width: 128px;" name="all_complete_saves" class="all_complete_saves">
														<option value="yes" selected="selected">Yes</option>
														<option value="no">No</option>
													</select>
												</div>
											</div>
										</div>	
										<div style="margin-left:-100px;float:left;">
										   <button type="button" class="btn add_btn" onclick="add_line(this)">添加</button>
									    </div>								 										
									</div> 
								</c:forEach>								
																	
									<div class="control-group" id="hide0">
										<label class="control-label">监听端口</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="mq_mon_port" value="1414" name="mq_mon_port" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">主日志文件数</span> 
												<input type="text" class="w45" id="mq_qmgr_plog" name="mq_qmgr_plog" value="10" />
											</div>
										</div>
									</div>
									
									<div class="control-group" id="hide1">
										<label class="control-label">QMGR数据路径</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="mq_data_path" name="mq_data_path" value="/var/mqm/qmgrs" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">次日志文件数</span> 
												<input type="text" class="w45" id="mq_qmgr_slog" name="mq_qmgr_slog" value="10" />
											</div>
										</div>
									</div>
										
									<div class="control-group" id="hide2">
										<label class="control-label">QMGR日志路径</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="mq_log_path" name="mq_log_path" value="/var/mqm/logs" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">日志文件页数(4KB)</span> 
												<input type="text" class="w45" id="mq_log_psize" name="mq_log_psize" value="4096" />
											</div>
										</div>
									</div>
										
									<div class="control-group" id="hide3">
										<label class="control-label">通道连接是否保持</label>
										<div class="controls">
											<div class="inputb2l">
												<select style="width: 47.5%" id="mq_chl_kalive" class="w48" name="mq_chl_kalive">
													<option value="yes" selected="selected">Yes</option>
													<option value="no">No</option>
												</select>
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">最大通道数</span> 
												<input type="text" class="w45" id="mq_chl_max" name="mq_chl_max" value="1000" />
											</div>
										</div>
									</div>
									
								</div>
							</div>
							
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
												<span class="input140 mr20">semmns</span> 
												<input type="text" class="w45" id="lin_semmns" name="lin_semmns" value="256000" />
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
												<span class="input140 mr20">semmni</span> 
												<input type="text" class="w45" id="lin_semmni" name="lin_semmni" value="1024" />
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
												<span class="input140 mr20">shmmni</span> 
												<input type="text" class="w45" id="lin_shmni" name="lin_shmni" value="4096" />
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
												<span class="input140 mr20">file-max</span> 
												<input type="text" class="w45" id="lin_filemax"
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
												<span class="input140 mr20">nproc</span> 
												<input type="text" class="w45" id="lin_nproc" name="lin_nproc" value="4096" />
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
    function change_hostname(obj){          //*********************change_hostname()改变主机名，实现联动变化 
    	var num = $(obj).val();  //获取改变后的主机名  
        var num_next = $(obj).parent().parent().parent()
                             .next("div").children("div").children("div")
                             .children("input[name='all_ips']").val();  //获取改变了主机名的那行IP值
        
        var index = $(obj).parent().parent().parent().parent().index();//获取当前行的索引 
        
        //定位到当前行的添加按钮  
        var curr_btn = $(obj).parent().parent().parent().next("div").next("div").next("div").next("div").children(".add_btn");

        //定位到下一个添加按钮 
        var next_btn = $(obj).parent().parent().parent().parent().nextAll().children("div").children(".add_btn:first");
        
        if($(next_btn).length > 0){ //如果存在 
        	var next_index = $(next_btn).parent().parent().index();//获取下一个添加按钮所在行的索引 
            for(var i=index;i<next_index-1;i++)
            {
            	$(".add_info").eq(i).children("div:eq(0)").children("div").children("div").children("input[name='all_hostnames']").val(num);
            }
        }
        else  //如果不存在 
        {
        	var total_line = $(".add_info").length;
        	for(var i=index;i<total_line;i++)
            {
            	$(".add_info").eq(i).children("div:eq(0)").children("div").children("div").children("input[name='all_hostnames']").val(num);
            }
        }
    }
	</script>	
	
	<script type="text/javascript">    //*********************add_line()函数：按序添加                   delete_line()函数：删除当前行 	    
	    function add_line(obj){
		    var a = $(obj).parent().parent().prop("outerHTML");//点击按钮获得当前行
		    var b = $(a).append("<div style='float:left;margin-left:-100px;'><button class='btn del_btn' onclick='delete_line(this)'>删除</botton></div>").prop("outerHTML");//添加删除按钮 
		    var c = $(b).children("div:eq(4)").children("button").removeClass("add_btn")
		    		    .parent().parent().prop("outerHTML");//移除add_btn
		    var d = $(c).children("div").children("div").children("div").children("input[name='all_hostnames']").attr("readonly","readonly")
		                .parent().parent().parent().parent().prop("outerHTML"); 
		    var next_add_btn = $(obj).parent().parent().nextAll().children("div").children(".add_btn:first");//查找下一个添加按钮 
		    if($(next_add_btn))//如果下一个添加按钮存在，就在那行之前插入 
		    {
		    	$($(next_add_btn).parent().parent()).before(d);
		    }
		    if(obj == $(".add_btn:last").get(0))
		    {
		    	$(".add_info:last").after(d);
		    } 
	    }
	    
	    function delete_line(obj){
	    	 $(obj).parent().parent().remove();   
	    }
	</script>
	
	<script type="text/javascript">    //*********************CheckInput()函数：判断MQ安装版本不能为空，判断QMGR名称不能相同   
	function CheckInput(){             
		if($("#mq_version").prev().find('a').find('span').text()=='请选择...')
		{
			ymPrompt.alert("请选择MQ安装版本!");
			return;
		}

		var cre_method = $("input:radio:checked").val(); //获取队列管理器创建方式 
		if(cre_method == "默认命令方式")  //如果是"默认命令方式",则需要判断qmgr的名称不能相同，否则不用判断 
		{
	        var numArr = []; // 定义一个空数组
	        var txt = $(".all_qmgr_names"); //获取所有的qmgr名称文本框 
	        for (var i = 0; i < txt.length; i++) {
	            numArr.push(txt.eq(i).val()); // 将文本框的值添加到数组中
	        }
			for(var j=0;j<numArr.length;j++)   //逐层进行判断 
			{
				for(var k=j+1;k<numArr.length;k++)   
				{
					if(numArr[j] == numArr[k])
					{
						ymPrompt.alert("QMGR名称不能相同");
						return ;
					}
				}
			}
		}
 		
		$("#submits").submit();
	}
	</script>
	
	<script type="text/javascript">    //*********************队列管理器创建方式	    
	    var total = $(".add_info").length;//总共多少条记录 
	            
		function sortRadio2() {   //不创建 
			$("#hide0").hide();
			$("#hide1").hide();
			$("#hide2").hide();
			$("#hide3").hide();	
			
	        $(".all_qmgr_names").each(function(){  //把所有的qmgr名称设置为"null" 
	        	$(".all_qmgr_names").val("null");
	        	$(".all_qmgr_names").attr("readonly","readonly");
	        })
	        
	        $(".del_btn").parent().parent().remove();  //把添加的信息移除掉 
	        
	        $(".add_btn").css("display","none");  //把所有添加按钮移除掉 	        
	        	        
 	        for(var i=0;i<total;i++)
	        {
	        	$(".add_info").eq(i).children("div:eq(3)").children("div").children("div").children("div")
                              .addClass("select2-container-disabled");  //将下拉框禁用                             
	        }             
		}
		
		function sortRadio1() {   //默认方式 
			$("#hide0").show();
			$("#hide1").show();
			$("#hide2").show();
			$("#hide3").show();				
			$("#mon_port").val("1414");
			$("#mq_qmgr_plog").val("10");			
 			$("#mq_data_path").val("/var/mqm/qmgrs");
 			$("#mq_qmgr_slog").val("10");			
			$("#mq_log_path").val("/var/mqm/logs");
			$("#mq_log_psize").val("4096");
			$("#mq_chl_kalive").removeAttr("readonly");
			$("#mq_chl_kalive").attr("disabled", false);
			$("#mq_chl_kalive").prop('disabled', false);
			$("#mq_chl_max").val("1000");	
			
			$(".all_qmgr_names").each(function(){
	        	$(".all_qmgr_names").removeAttr("readonly");//解禁：qmgr名称
	        })
	        
	        $(".add_btn").css("display","block");  //恢复添加按钮 
	        
 	        for(var i=0;i<total;i++)
	        {
	        	$(".add_info").eq(i).children("div:eq(3)").children("div").children("div").children("div")
                              .removeClass("select2-container-disabled");  //恢复下拉框 
	        } 
		}
	</script>   
	
	<script type="text/javascript">
		//自动加载
		function showfix(obj) 
		{
			var ver = obj.value;//获取版本
			$.ajax({
						url : "/automation/getmqfixver",
						data : { version : ver },
						type : 'post',
						dataType : 'json',
						success : function(result) 
						{							
							$("#mq_fp").empty();
							var str="<option value='-1'>"+ "请选择..." + "</option>";;
							for (var i = 0; i < result.length; i++) 
							{
								if (result[i].key == '-10')// -10表示只显示请选择...
								{
									$("#mqfix").val('请选择...');
									break;
								} 
								if(result[i].key=='-12')//表示是版本包含补丁
								{
									$("#mqfix").val('-12');
									str += "<option value='" + result[i].value + "'>" + result[i].key + "</option>";
								}
								else 
								{									
									str += "<option value='" + result[i].value + "'>" + result[i].key + "</option>";									
								}								
							}
							$("#mq_fp").append(str);							
						}
					});
		}
		
		function getVer(obj) 
		{
			if($("#mq_fp").find("option:selected").text()=='请选择...')
			{
				$("#mqfix").val('-');
			}
			else
				$("#mqfix").val($("#mq_fp").find("option:selected").text());
		}
		
		window.onload = function() 
		{
			$.ajax({
				url : '/automation/getmqversion',
				data : {
					product : "mq",
					platform : 'linux'
				},
				type : 'post',
				dataType : 'json',
				success : function(result) 
				{
					$("#mqfix").val('-');
					var str;
					for (var i = 0; i < result.length; i++) 
					{
						if (result[i].key == '.')// .号表示没有安装文件
						{
							break;
						}
						else 
						{
							str += "<option value='" + result[i].key + "'>" + result[i].value + "</option>";
						}
					}
					$("#mq_version").append(str);
				}
			})
		}	
	</script>
</body>
</html>
