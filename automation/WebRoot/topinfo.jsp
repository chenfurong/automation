<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="loginCheck.jsp" %>
<style type="text/css">
.menuitem .viewcolumn {display: none;}
.menuitem:hover .viewcolumn {display: block;}

.submenu {display: none;}
.accordion .link {
	cursor: pointer;
	font-weight: 500;
	font-size:15px;
	height:30px;
	color:black;
}
.link:hover{background-color:rgb(100,185,225);}
.viewcolumn{min-width:225px; height:auto; }
.one{float:left;margin-top:5px;}
.accordion li i.fa-chevron-down {
	right: 12px;
	left: auto;
	font-size: 12px;
}
.accordion li.open i.fa-chevron-down {
	-webkit-transform: rotate(180deg);
	-ms-transform: rotate(180deg);
	-o-transform: rotate(180deg);
	transform: rotate(180deg);
}
.submenu{font-size:14px;}
</style>
<link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
<script type="text/javascript">
$(function() {
	var Accordion = function(el, multiple) {
		this.el = el || {};
		this.multiple = multiple || false;
		var links = this.el.find('.link');
		//links.on('mouseover', {el: this.el, multiple: this.multiple}, this.dropdown);//鼠标滑过展开子菜单
		links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);//鼠标点击展开子菜单
	}

	Accordion.prototype.dropdown = function(e) {
		var $el = e.data.el;
			$this = $(this),
			$next = $this.next();
		$next.slideToggle();
		$this.parent().toggleClass('open').css("background","white");

		if (!e.data.multiple) {
			$el.find('.submenu').not($next).slideUp().parent().removeClass('open');
		};
	}	
	var accordion = new Accordion($('#accordion'), false);
});
</script>

<a class="treelogo">系统自动化部署平台</a>
<div id="user-nav" class="navbar navbar_inverse">
	<ol class="viewbox">
		<li><a href="getAllServers">首页</a></li>
		<li class="menuitem"><a>自动化部署&nbsp;<span class="caret" style="margin-top:23px;"></span></a>
			<div class="viewcolumn">
				<ol class="viewlist accordion" id="accordion">
					<li>
					   <div class="link" style="margin-top:5px;font-size:14px;">
					      &nbsp;&nbsp;&nbsp;<img src="img/icons/tree/virtual.png" />&nbsp;IBM DB2
					      <div style="float:right;padding-right:5px;margin-top:-4px;"><i class="fa fa-chevron-down"></i></div>
					   </div>
					   <ul class="submenu">
					        <div style="margin-left:-9px;margin-top:-6px;">
							  <li><a href="getIBMAllInstance?ptype=db2&platform=all">DB2 单机</a></li>
							  <li><a href="getIBMAllInstance?ptype=db2ha&platform=aix">DB2 HA</a></li>
					        </div>
					   </ul>
					</li>
					<li>   
					   <div class="link" style="font-size:14px;">
					      &nbsp;&nbsp;&nbsp;<img src="img/icons/tree/virtual.png" />&nbsp;IBM WAS
					      <div style="float:right;padding-right:5px;margin-top:-4px;"><i class="fa fa-chevron-down"></i></div>
					   </div>
					   <ul class="submenu">
					        <div style="margin-left:-9px;margin-top:-6px;">
							  <li><a href="getIBMAllInstance?ptype=was&platform=all">WAS 单机</a></li>
							  <li><a href="getIBMAllInstance?ptype=wascluster&platform=all">WAS 集群</a></li>
					        </div>
					   </ul>
					</li>
					<li>
					   <div class="link" style="font-size:14px;">
					      &nbsp;&nbsp;&nbsp;<img src="img/icons/tree/virtual.png" />&nbsp;IBM MQ
					      <div style="float:right;padding-right:5px;margin-top:-4px;"><i class="fa fa-chevron-down"></i></div>
					   </div>
					   <ul class="submenu">
					        <div style="margin-left:-9px;margin-top:-6px;">
							  <li><a href="getIBMAllInstance?ptype=mq&platform=all">MQ 单机</a></li>
							  <li><a href="getIBMAllInstance?ptype=mqcluster&platform=linux">MQ 集群</a></li>	
							 <!--  						 
							  <li><a href="getIBMAllInstance?ptype=mqha&platform=aix">MQHA</a></li> -->
					        </div>
					   </ul>
					</li>	
					<li>
					   <div class="link" style="font-size:14px;">
					      &nbsp;&nbsp;&nbsp;<img src="img/icons/tree/virtual.png" />&nbsp;IBM ITM
					      <div style="float:right;padding-right:5px;margin-top:-4px;"><i class="fa fa-chevron-down"></i></div>
					   </div>
					   <ul class="submenu">
					        <div style="margin-left:-9px;margin-top:-6px;">
							  <li><a href="getIBMAllInstance?ptype=osagent&platform=linux">OS Agent</a></li>	
					        </div>
					   </ul>
					</li>														 
				</ol>
			</div></li>
		<li><a href="getLogInfo">历史执行记录</a></li>
	</ol>
	<ul class="nav btn-group">
		<li class="btn btn-help"><a title="用户名"><i
				class="icon icon-user"></i> <span class="text">用户名：<b>${userName }</b></span></a></li>
		<li class="btn btn-help"><a title="退出" href="loginOut"><i
				class="icon icon-share-alt"></i> <span class="text">退出</span></a></li>
	</ul>
</div>
<div class="clearfix"></div>
