<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@include file="loginCheck.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="header.jsp" flush="true" />
<title>自动化部署平台</title>
<script>
	$(document).ready(function() {

		(function($) {

			$('#filter').keyup(function() {

				var rex = new RegExp($(this).val(), 'i');
				$('.searchable tr').hide();
				$('.searchable tr').filter(function() {
					return rex.test($(this).text());
				}).show();

			})

		}(jQuery));

	});
</script>
<script language="javascript" type="text/javascript">
var infoId = [];
	//操作
	function isSelect(s) {

		if ($(s).attr("checked")) {
			if ($(s).parent().parent().parent().parent().parent().next().next()
					.next().next().next().find('b').text() == 'Error') {
				ymPrompt.alert('当前目标系统存在问题，无法选择');
				//  $(s).removeAttr("checked","checked");
				$(s).prop("checked", false);
				//	$(s).attr("checked",false)
				return;
			}
			infoId.push(s.value);
		} else {
			var index = 0;
			for (var i = 0; i < infoId.length; i++) {
				if (s.value == infoId[i]) {
					index = i;
				}
			}
			infoId.splice(index, 1);
		}
		console.log(infoId);	
	}
		
	
	function checkDB2Select() {
		var infoId = [];
		$("input[name='servers']").each(function() {
			if ($(this).attr("checked")) {
				infoId.push($(this).val());
			}
		});
		if (infoId.length == 0) {
			ymPrompt.alert("请选择一条实例!");
			return;
		} else if (infoId.length > 5) {
			ymPrompt.alert("一次最多只能选择5条实例!");
			return;
		} else {
			location.href = "getInstanceDetial?serId=" + infoId + "&ptype=wascluster";
		}
	}
	//提交IP检索的事件
	$(document).ready(function() {
		$("#postsubmit").click(function() 
		{
			var value = $("#search").val();
			$.ajax({
					  url : '/automation/searchServers',
					  data : { data : value },
					  type : 'post',
					  dataType : 'json',
					  success : function(jsonstr) 
					  {																														
						  var ht = '';
						  //var jsonObj = jQuery.parseJSON(str);
						  //var sLen = jsonstr.length;
						  for (var i = 0; i < jsonstr.length; i++) 
						  {
							  ht = ht + '<tr>';
							  for ( var key in jsonstr[i]) 
							  {
								  if (key == '_id') 
								  {
									  ht = ht + '<td style="text-align: center;"><input type="checkbox" name="servers" value="'
											  + jsonstr[i][key]
											  + '" onclick="isSelect(this);" /></td>';
								  } 
								  else 
								  {
									  ht = ht + '<td style=\"text-align: center;\">'
											  + jsonstr[i][key]
											  + '</td>';
								  }
						   }
						   ht = ht + '</tr>';
						}
						$('table tbody').html(ht);
						$('table tbody').attr('class','table table-striped table-bordered table-hover');
						$("#showcomputer").text("共有"+jsonstr.length+"台主机");
					 }
				   });
				 });
			   });
</script>
<script>
//编辑框的关闭按钮事件
function closeModal()
{
	window.location.reload();
}
//编辑框的校验代码
function CheckInput()
{

	if ($("#IP").val().trim() == "") {
		ymPrompt.alert("请输入IP!");
		return;
	}
	if ($("#UserID").val().trim() == "") {
		ymPrompt.alert("请输入用户名!");
		return;
	}
	if ($("#Password").val().trim() == "") {
		ymPrompt.alert("请输入密码!");
		return;
	}

	if ($("#msgflag").val() == 1) {
		ymPrompt.alert("IP已经存在，请更换IP!");
		return;
	}
	
	$.ajax({
		url : '/automation/postEditForm',
		data : {
			_id : $("#_id").val(),
			UserID:$("#UserID").val(),
			Password:$("#Password").val(),
			IP:$("#IP").val()
			
		},
		type : 'post',
		dataType : 'json',
		success : function(result){
			if(result.msg=='success')
			{
				ymPrompt.alert('修改成功！');
			}
			if(result.msg=='failure')
				
			{
				ymPrompt.alert('修改失败！');
			}
			
		}
		
		
	})
	
}
	//弹出编辑框
	function editHost(obj) {
		var _id = $(obj).parent().parent().prev().prev().prev().prev().prev()
				.prev().find('div').find('span').find('div').find('span').find(
						'input').val(); //_id

		$.ajax({
			url : "/automation/getModifyHost",
			data : {
				_id : _id
			},
			type : 'post',
			dataType : 'json',
			success : function(result) {
				//alert(result);
				$("#_id").val(result._id);
				$("#UserID").val(result.UserID);
				$("#IP").val(result.IP);
				$("#Password").val(result.Password);
				$("#Status").val(result.Status);
				$("#Name").val(result.Name);
				$("#HConf").val(result.HConf);
				$("#OS").val(result.OS);
			}

		})
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
			<a href="getAllServers" class="current"><i class="icon-home"></i>实例一览</a>
			<a href="#" title="IBM WAS" class="tip-bottom">IBM WAS</a>
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
							<div class="widget-content">所有WAS Cluster实例信息.</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">
						<div class="widget-box nostyle">
							<%-- <div class="col-sm-6 form-inline">
								<input class="form-control" id="search" type="text"
									placeholder="请输入主机名或IP地址" style="width: 160px" /> 
								<img src="<%=path%>/img/icon-search.png" id="postsubmit" name="postsubmit"
									onmousedown="old=this.src; this.src='<%=path%>/img/icon-search-sm.png';"
									onmouseup="this.src=old;" />
								<h5 id="showcomputer" name="showcomputer" style="weight: 200px; float: right">共有 ${total }台主机</h5>
							</div> --%>
							<div class="col-sm-6 form-inline">
								
								<input id="filter" type="text" class="form-control" placeholder="请输入过滤项...">
							
								<h5 id="showcomputer" name="showcomputer"
									style="weight: 200px; float: right">共有 ${total }台主机</h5>
							</div>
							<div style="margin-bottom: 5px"></div>
							<table
								class="table table-bordered data-table  with-check table-hover no-search no-select">
								<thead>
									<tr>
										<th style="text-align: center;">选择</th>
										<th style="text-align: center;">主机名</th>
										<th style="text-align: center;">IP地址</th>
										<th style="text-align: center;">主机配置</th>
										<th style="text-align: center;">操作系统</th>
										<!-- <th style="text-align: center;">HyperVisor</th> -->
										<th style="text-align: center;">健康状态</th>
										<th style="text-align: center;">编辑</th>
									</tr>
								</thead>
								<tbody class="searchable">
									<c:forEach items="${servers }" var="ser">
										<tr>
											<td style="text-align: center;"><input type="checkbox"
												name="servers" value="${ser._id }" onclick="isSelect(this);" />
											</td>
											<td style="text-align: center;">${ser.name }</td>
											<td style="text-align: center;">${ser.IP }</td>
											<td style="text-align: center;">${ser.HConf}</td>
											<td style="text-align: center;">${ser.OS}</td>
											<!-- <td style="text-align: center;">${ser.HVisor}</td> -->
											<td style="text-align: center;" name="state"><c:if
													test="${ser.status eq 'Active' }">
													&nbsp;&nbsp;<img src="img/icon_success.png"></img>
													<b>${ser.status}</b>
												</c:if> <c:if test="${ser.status eq 'Error' }">
													<img src="img/icon_error.png"></img>
													<b>${ser.status}</b>
												</c:if></td>
											<td style="text-align: center;">
											 <div style="height:20px;">
											  <img src="<%=path%>/img/edit.png" id="postsubmit"
									               name="postsubmit" data-toggle="modal" data-target="#edit"
									               onmousedown="old=this.src; this.src='<%=path%>/img/edit18.png';"
									               onmouseup="this.src=old;" onclick="editHost(this);" />
									          </div>
											</td>
										</tr>
										<div class="modal fade" id="edit" tabindex="-1" role="dialog"
											aria-labelledby="myModalLabel" aria-hidden="true">
											<div class="modal-dialog">
												<div class="modal-content">
													<div class="modal-header">
														<button type="button" class="close" data-dismiss="modal"
															aria-hidden="true">&times;</button>
														<h5 class="modal-title" id="myModalLabel">
															<img src="<%=path%>/img/edit16.png">&nbsp;&nbsp;编辑信息
														</h5>
													</div>

													<form action="postEditForm" method="post" id="submits">
														<input type="hidden" id="_id" name="_id" ></input>
														<div class="modal-body">
															<div class="control-group">
																<div class="controls">
																	<span class="input140 mr20">用户名：</span> <input
																		class="form-control" type="text" id="UserID"
																		name="UserID"> <span
																		style="font-size: 13px; color: red;">*&nbsp;需管理员用户</span>
																</div>
															</div>

															<div class="control-group">
																<div class="controls">
																	<span class="input140 mr20">密码：</span> <input
																		class="form-control" type="password" id="Password"
																		name="Password">
																</div>
															</div>

															<div class="control-group">
																<div class="controls">
																	<span class="input140 mr20">主机名：</span> <input
																		class="form-control" type="text" readonly="readonly"
																		id="Name" name="Name" value="">
																</div>
															</div>

															<div class="control-group">
																<div class="controls">
																	<span class="input140 mr20">IP：</span> <input
																		class="form-control" type="text" id="IP" name="IP"
																		value="">
																	<div
																		style="margin-top: 3px; float: right; margin-right: 20px;">
																		<span hidden="" id="ipshow" name="ipshow"> <img
																			alt="" src="img/icon_error.png">&nbsp; <span
																			style="font-size: 13px">此IP地址已注册</span>
																		</span>
																	</div>
																	<div style="float: right; margin-top: 4px;">
																		<span hidden="" id="ipshow1" name="ipshow1"> <img
																			align="top" alt="" src="img/icon_success.png">&nbsp;
																			<span style="font-size: 13px">此IP地址未注册</span>
																		</span>
																	</div>
																	<input type="hidden" id="msgflag" name="msgflag"
																		value="">
																</div>
															</div>

															<div class="control-group">
																<div class="controls">
																	<span class="input140 mr20">主机配置：</span> <input
																		class="form-control" type="text" readonly="readonly"
																		value="" id="HConf" name="HConf">
																</div>
															</div>

															<div class="control-group">
																<div class="controls">
																	<span class="input140 mr20">操作系统：</span> <input
																		class="form-control" type="text" readonly="readonly"
																		value="" id="OS" name="OS">
																</div>
															</div>
														</div>

													</form>
												</div>

												<div class="modal-footer">
													<button type="button" class="btn btn-default"
														data-dismiss="modal" onclick="closeModal();">关闭</button>
													<button type="button" class="btn"
														style="background-color: rgb(68, 143, 200);"
														onclick="CheckInput();">
														<font color="white">保存</font>
													</button>

												</div>
											</div>
										</div>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					
					
				
				  <div style="margin-bottom: 55px"></div>
				  					
					<div class="columnfoot">
						<a class="btn btn-info fr btn-next" onclick="checkDB2Select();">
							<span>下一页</span> <i class="icon-btn-next"></i>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
