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
      <a href="#" class="current">HA配置</a>
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
               <b>serviceIP1：</b><span>${ha_svcip }</span> 
                </p>
                <div class="column">
                  <div class="span12">
                     <p>
                      <b>主机名:</b>
                      	<span class="column_txt">
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
                      <b>资源组：</b><span class="mr10">${ha_RGNmae }</span>
                    </p>
                    <p>
                      <b>系统配置:</b><span class="column_txt">${ser.HConf }</span>
                      <b>挂卷:</b><span class="column_txtl">volume_HB , volume_Data</span>
                      <b>状态:</b><span class="column_txt"><img src="img/icons/common/icon_success.png"><em>${ser.status }</em></span>
                      <b>VG信息：</b><span class="mr10">${ha_ASName }</span>
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
audb.sh  ha_setup.ksh  hostname.helper.sh  hosts.helper.sh  importvg.ksh  install.db2.ksh  mkvg.ksh  prepare.db2.ksh  prepare.db2.lst  update.db2.ksh  mount.nfs.ksh
            
*注：主备机脚本路径相同
                      </textarea>
							</div>
							<h5>手工执行脚本方法如下：</h5>
							<div class="form-horizontal processInfo">
								<textarea readonly="readonly" rows="7"
									style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;">
1.使用root用户分别登录两台主机
2.分别进入两台目标主机的目录/script中
3.首先在主机端执行./prepare.db2.ksh ${ha_primaryNode }
4.等待主机执行完成后，在备机端执行./prepare.db2.ksh ${ha_subNode }
5.等待主备机全部执行完prepare.db2.ksh脚本后，在主机端执行./install.db2.ksh ${ha_clusterName }
6.若挂载了NFS文件系统，在主备机两端分别执行mount.nfs.ksh挂载NFS文件系统
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
<!--content end-->
 <!--  <script src="js/jquery.min.js"></script>
  <script src="js/bootstrap.min.js"></script>
  <script src="js/jquery-ui.custom.js"></script>
  <script src="js/jquery.uniform.js"></script>
  <script src="js/jquery.validate.js"></script>
  <script src="js/select2.min.js"></script>
  <script src="js/unicorn.form_validation.js"></script> 
  <script src="js/jquery.dataTables.min.js"></script>
  <script src="js/lodash.min.js"></script>
	<script src="js/unicorn.data.js"></script>
  <script src="js/unicorn.js"></script>
  <script src="js/unicorn.tables.js"></script>
  <script src="js/jquery.spinner.min.js"></script>
  <script src="js/custom.js"></script> -->
</body>

</html>