<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML>
<html>
<head>
<base href="<%=basePath%>">
<title>自动化部署平台</title>
<meta name=”renderer” content=”webkit|ie-comp|ie-stand”>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="自动化部署平台">
<link rel="stylesheet" href="<%=path%>/css/bootstrap.min.css" />
<link rel="stylesheet" href="<%=path%>/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="<%=path%>/css/unicorn.login.css" />
<script src="<%=path%>/js/jquery.min.js" type="text/javascript"></script>

<script type="text/javascript">
	$(document).ready(function() {
		var errorMessageFlag = $("#errorMessageFlag").val();
		if (errorMessageFlag == "fail") {
			$("#errorMessage").css("display", "block");
			$("#pwd").val('');
		}
		if (errorMessageFlag == "networkerror") {
			$("#networkerrorMessage").css("display", "block");
			$("#pwd").val('');
		}
		$(document).keypress(function(event) {
			var keyCode = event.keyCode;
			if (keyCode == 13) {
				checkFormat();
			} else {
				var ecode = event.which;
				if (ecode == 13) {
					checkFormat();
				}
			}
		});
	});

	//校验用户名和密码格式
	
	function checkFormat() {
		if ($("#userName").val() == '') {
			$("#errorMessage").html("请输入用户名！");
			$("#errorMessage").css("display", "block");
			return;
		} else if ($("#pwd").val() == '') {
			$("#errorMessage").html("请输入密码！");
			$("#errorMessage").css("display", "block");
			return;
		} else {
			$("#errorMessage").css("display", "none");
			$("#pwd").val(window.btoa($("#pwd").val()));
			$("#loginform").submit();
			/* function(){
				$("#pwd").val(window.btoa($("#pwd").val()));
				return true;
				 
			});*/
		}
	}
</script>
<style type="text/css">
#header h1 {
	padding-left: 50px;
	background: url("img/logo.png") no-repeat;
}

</style>

</head>

<body>
<!-- 	<div id="header">
		<h1>
			<a class="treelogo">系统自动化部署平台</a>
		</h1>
	</div> -->
	<div id="loginbox">
		<form id="loginform" method="post" class="form-vertical"
			action="AllLoginAction" />
		<div class="logobox">
			<p>
				<img src="<%=path%>/img/logo.png">
			</p>
			<h4>用户登录</h4>
		</div>
		<div class="control-group">
			<input id="errorMessageFlag" type="hidden"
				value="${errorMessageFlag}" />
			<div id="errorMessage" class="prompt" style="display: none;">
				<i></i>登录名或登录密码不正确！
			</div>
			<div id="networkerrorMessage" class="prompt" style="display: none;">
				<i></i>网络或服务器存在问题！
			</div>
			<div class="controls">
				<div class="input-prepend">
					<div class="similarinput">
						<i class="icon-user"></i><input id="userName" name="userName"
							type="text" placeholder="用户名" class="loginTxt nobottom" />
					</div>
					<div class="similarinput">
						<i class="icon-lock"></i><input id="pwd" name="password"
							type="password" placeholder="密码" class="loginTxt notopradius" />
					</div>
				</div>
			</div>
		</div>
		<div class="form-actions">
			<span class="pull-login"><input type="button"
				onclick="checkFormat();" class="btn btn-login" value="登录" /></span>
		</div>
		</form>

	</div>



	
</body>


</html>