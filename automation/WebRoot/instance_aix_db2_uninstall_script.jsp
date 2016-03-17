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
      <a href="#" title="服务编排管理平台" class="tip-bottom"><i class="icon-home"></i>服务编排管理平台</a>
      <a href="#" class="current">DB2单节点配置</a>
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
                        <div class="widget-content">手工运行脚本创建环境信息</div>
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
     		<c:forEach items="${servers }" var="ser" varStatus="num"
								begin="0" end="0">
								<div class="column">
									<div class="span12">
										<p>
											<b>主机名:</b> <span class="column_txt"> ${ser.name } </span>
											 <b>IP地址:</b><span
												class="column_txtl">${ser.IP}</span>
											<!-- 把镜像注释 -->
											<!--
						     <b>镜像:</b><span><c:forEach items="${imageList }" var="img"><c:if test="${ser.image.id eq img.id }">${img.name }</c:if></c:forEach></span> 
						  -->
											<!-- 新加的 -->
											<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
											<!-- 新加的 -->
										</p>
										<p>
											<b>系统配置:</b><span class="column_txt">${ser.HConf }</span> <b>挂卷:</b><span class="column_txtl">volume_HB ,
												volume_Data</span>&nbsp; <b>状态:</b><span class="column_txt"><img
												src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>
              </div>

						<div class="mainmodule">
							<h5>脚本已生成并上传至服务器路径：</h5>
							<div class="form-horizontal processInfo">
								<textarea readonly="readonly" rows="6"
									style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;">
[username@hostname]# cd /script
[username@hostname script]# ls
audb.sh hostname.helper.sh  hosts.helper.sh  mkvg.ksh  mount.nfs.ksh  prepare.db2.ksh  prepare.db2.lst  update.db2.ksh  
            
                      </textarea>
							</div>
							<h5>手工执行脚本方法如下：</h5>
							<div class="form-horizontal processInfo">
								<textarea readonly="readonly" rows="7"
									style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;">
1.使用root用户登录主机
2.进入目标主机的目录/script中
3.首先在主机端执行hostname.helper.sh
4.其次在主机端执行./prepare.db2.ksh ${ip}
5.若挂载了NFS文件系统，在主备机两端分别执行mount.nfs.ksh挂载NFS文件系统
                      </textarea>
							</div>
						</div>
					</div>   
          </div>
        </div>
      </div>
  </div>
  <div class="columnfoot" style="position:fixed;">
    <a class="btn btn-info fr btn-next" href="getAllServers">
        <span>返回</span>
      <i class="icon-btn-next"></i>
    </a>
  </div>
</body>

</html>