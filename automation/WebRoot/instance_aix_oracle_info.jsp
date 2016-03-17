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
<title>自动化部署平台——oracle</title>
	<script>
		function sortRadio(val) 
		{
			if (val == '1') 
			{
				$("#oracle_home").val("/oracle/app/oracle/10.2.0/dbhome_1");
			} 
			else  if(val=='2')
			{
				$("#oracle_home").val("/oracle/app/oracle/11.2.0/dbhome_1");
		    }	
		}
		
	   function modify()
		{
		    var processes=  $("#oracle_processes").val().trim();
		    var sessions=processes*1.1+5;
			$("#oracle_sessions").val(sessions);
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
		<a href="#" title="既有虚机+oracle" class="tip-bottom"><i class="icon-home"></i>既有虚机+oracle</a> 
		<a class="current">实例配置详细</a>
	  </div>
	  
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> 
							<span class="icon"> <i class="icon-arrow-right"></i></span>	
							<h5>说明：</h5>
							</a>
							
						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">配置当前虚拟机的oracle参数信息</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="mainmodule">
			   <h5 class="swapcon"><i class="icon-chevron-down"></i>基本信息</h5>	
			     <div class="form-horizontal">	
					<div class="control-group">
						<label class="control-label">Oracle安装版本</label>
						<div class="controls">
							<div class="inblock mr20">
								<label><input type="radio" id="oracle_version" name="oracle_version" value="v10.2" onClick="sortRadio(1)" checked />v10.2</label>
							</div>
							<div class="inblock mr20">
								<label><input type="radio" id="oracle_version" name="oracle_version" value="v11.2" onClick="sortRadio(2)" />v11.2</label>
							</div>
						</div>
					</div>
					
	   				<div class="control-group">
						<label class="control-label">ORACLE_HOME</label>
						<div class="controls">
								<input type="text" id="oracle_home" name="oracle_home" style="width:78%"
									value="/oracle/app/oracle/10.2.0/dbhome_1" />
						</div>
					</div>
					
	   				<div class="control-group">
						<label class="control-label">ORACLE_BASE</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_base" name="oracle_base"
									value="/oracle/app/oracle"  readonly="readonly" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">ORACLE_HOSTNAME</span> 
								<input type="text" id="oracle_service_hostname" name="oracle_service_hostname"
									value="" readonly="readonly" class="w45" />
							</div>							
						</div>
					</div>					
												     	   
	   				<div class="control-group">
						<label class="control-label">USERNAME</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_oracleuser" name="oracle_oracleuser"
									value="oracle" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">GROUPNAME</span> 
								<input type="text" id="oracle_oracledba" name="oracle_oracledba"
									value="dba" class="w45" />
							</div>
						</div>
					</div>	
					
	   				<div class="control-group">
						<label class="control-label">ORACLE_SID</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_sid" name="oracle_sid"
									value="orcl"  class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">AIXTHREAD_SCOPE</span> 
								<input type="text" id="oracle_aixthread_scope" name="oracle_aixthread_scope"
									value="S" readonly="readonly" class="w45" />
							</div>
						</div>
					</div>	
					
	   				<div class="control-group">
						<label class="control-label">TEMP</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_temp_path" name="oracle_temp_path"
									value="/oracle/tmp"  readonly="readonly" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">TMP</span> 
								<input type="text" id="oracle_tmp_path" name="oracle_tmp_path"
									value="/oracle/tmp" readonly="readonly" class="w45" />
							</div>
						</div>
					</div>
					
	   				<div class="control-group">
						<label class="control-label">TMPDIR</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_tmpdir_path" name="oracle_tmpdir_path"
									value="/oracle/tmp"  readonly="readonly" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">ORA_NLS10</span> 
								<input type="text" id="oracle_ora_nls" name="oracle_ora_nls"
									value="$ORACLE_HOME/nls/data" readonly="readonly" class="w45" />
							</div>
						</div>
					</div>	
					
	   				<div class="control-group">
						<label class="control-label">LD_LIBRARY_PATH</label>
						<div class="controls">
								<input type="text" id="oracle_ld_lib_path" name="oracle_ld_lib_path" style="width:78%"
									value="$ORACLE_HOME/lib:$ORACLE_HOME/lib32"  readonly="readonly" />
						</div>
					</div>	
					
	   				<div class="control-group">
						<label class="control-label">LIBPATH</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_libpath" name="oracle_libpath"
									value="$LD_LIBRARY_PATH"  readonly="readonly" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">TNS_ADMIN</span> 
								<input type="text" id="oracle_tns_admin" name="oracle_tns_admin"
									value="$ORACLE_HOME/network/admin" readonly="readonly" class="w45" />
							</div>							
						</div>
					</div>	
					
	   				<div class="control-group">
						<label class="control-label">PATH</label>
						<div class="controls">
								<input type="text" id="oracle_path" name="oracle_path" style="width:78%"
									value="$ORACLE_HOME/bin:$ORACLE_HOME/OPatch:$PATH"  readonly="readonly" />
						</div>
					</div>																
				</div>							   
		    </div>
		    
		    <div class="mainmodule">
		       <h5 class="swapcon">
				  <i class="icon-chevron-down icon-chevron-right"></i>数据库参数
			   </h5>
			   <div class="form-horizontal" style="display: none;">
			      	<div class="control-group">
						<label class="control-label">DB_NAME</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_name" name="oracle_name"
									value="orcl" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">DB_UNIQUE_NAME</span> 
								<input type="text" id="oracle_unique_name" name="oracle_unique_name"
									value="orcl" class="w45" />
							</div>							
						</div>
					</div>
					
			      	<div class="control-group">
						<label class="control-label">DB_DOMAIN</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_domain_name" name="oracle_domain_name"
									value="" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">INSTANCE_NAME</span> 
								<input type="text" id="oracle_instance" name="oracle_instance"
									value="orcl" class="w45" />
							</div>							
						</div>
					</div>
					
					<div class="control-group">
						<label class="control-label">CHARACTER SET</label>
						<div class="controls">
						  <div class="inputb2l">
							<select id="styleSelect" class="w48" onChange="changeDivShow('styleSelect')" name="oracle_charset">
								<option value="AL32UTF8" >AL32UTF8</option>
								<option value="AR8ISO8859P6">AR8ISO8859P6</option>
								<option value="AR8MSWIN1256">AR8MSWIN1256</option>
								<option value="BLT8ISO8859P13">BLT8ISO8859P13</option>
								<option value="BLT8MSWIN1257">BLT8MSWIN1257</option>
								<option value="CL8ISO8859P5">CL8ISO8859P5</option>
								<option value="CL8MSWIN1251">CL8MSWIN1251</option>
								<option value="EE8ISO8859P2">EE8ISO8859P2</option>
								<option value="EE8MSWIN1250">EE8MSWIN1250</option>
								<option value="EL8ISO8859P7">EL8ISO8859P7</option>
								<option value="ELMSWIN1253">ELMSWIN1253</option>
								<option value="IW8ISO8859P8">IW8ISO8859P8</option>
								<option value="IW8MSWIN1255">IW8MSWIN1255</option>
								<option value="JA16EUC">JA16EUC</option>
								<option value="JA16EUCTILDE">JA16EUCTILDE</option>
								<option value="JA16SJIS">JA16SJIS</option>
								<option value="JA16JISTILDE">JA16JISTILDE</option>
								<option value="KO16MSWIN949">KO16MSWIN949</option>
								<option value="NE8ISO8859P10">NE8ISO8859P10</option>
								<option value="NEE8ISO8859P4">NEE8ISO8859P4</option>
								<option value="TH8THSASCII">TH8THSASCII</option>
								<option value="TR8MSWIN1254">TR8MSWIN1254</option>
								<option value="VN8MSWIN1258">VN8MSWIN1258</option>
								<option value="WE8ISO8859P15">WE8ISO8859P15</option>
								<option value="WE8ISO8859P9">WE8ISO8859P9</option>
								<option value="ZHS16GBK" selected="selected">ZHS16GBK</option>
								<option value="ZHS16HKSCS">ZHS16HKSCS</option>
								<option value="ZHS16MSWIN950">ZHS16MSWIN950</option>
								<option value="ZHT32EUC">ZHT32EUC</option>
							</select>
						   </div>
							<div class="inputb2l">
								<span class="input140 mr20">NATIONAL CHARACTER SET</span> 
								<select class="w48" name="oracle_ncharset">
									<option value="UTF8" selected="selected">UTF8</option>
									<option value="AL16UTF16">AL16UTF16</option>
								</select>
							</div>
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">Default Language</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_nls_language" name="oracle_nls_language"
									value="AMERICAN"  readonly="readonly" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">Default Territory</span> 
								<input type="text" id="oracle_nls_territory" name="oracle_nls_territory"
									value="AMERICA" readonly="readonly" class="w45" />
							</div>							
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">数据库表存放路径</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oradata_dbpath" name="oradata_dbpath"
									value="/oracle/oradata"  readonly="readonly" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">db_block_size</span> 
								<select class="w48" name="oracle_db_block_size">									
									<option value="2048">2048</option>
									<option value="4096">4096</option>
									<option value="8192" selected="selected">8192</option>
									<option value="16384">16384</option>
									<option value="32768">32768</option>
								</select>
							</div>							
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">open_cursors</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_open_cursors" name="oracle_open_cursors"
									value="300" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">db_domain</span> 
								<input type="text" id="oracle_db_domain" name="oracle_db_domain"
									value="" class="w45" />
							</div>							
						</div>
					</div>
					
			      	<div class="control-group">
						<label class="control-label">db_name</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_db_name" name="oracle_db_name"
									value="orcl" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">db_block_size</span> 
								<select class="w48" name="oracle_db_block_size">									
									<option value="2048">2048</option>
									<option value="4096">4096</option>
									<option value="8192" selected="selected">8192</option>
									<option value="16384">16384</option>
									<option value="32768">32768</option>
								</select>
							</div>							
						</div>
					</div>	
					
					<div class="control-group">
						<label class="control-label">control_files</label>
						<div class="controls">											
							<input type="text" id="oracle_control_files" name="oracle_control_files" style="width:78%"
								value="/oracle/oradata/orcl/control01.ctl,/oracle/oradata/orcl/control02.ctl"  />
						</div>
					</div>					
					
			      	<div class="control-group">
						<label class="control-label">compatible</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_compatible" name="oracle_compatible"
									value="11.2.0.4.0" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">diagnostic_dest</span> 
								<input type="text" id="oracle_diag_dest" name="oracle_diag_dest"
									value="/oracle/app/oracle" readonly="readonly" class="w45" />
							</div>							
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">memory_target</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_memory_target" name="oracle_memory_target"
									value="2147483648" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">memory_max_target</span> 
								<input type="text" id="oracle_memory_max_target" name="oracle_memory_max_target"
									value="2147483648" class="w45" />
							</div>							
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">processes</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_processes" name="oracle_processes"
									value="500" class="w45" onblur="modify()" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">sessions</span> 
								<input type="text" id="oracle_sessions" name="oracle_sessions"
									value="555" class="w45" />
							</div>							
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">audit_file_dest</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_audit_file_dest" name="oracle_audit_file_dest"
									value="/oracle/app/oracle/admin/orcl/adump" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">audit_trail</span> 
								<select class="w48" name="oracle_audit_trail">									
									<option value="DB" selected="selected">DB</option>
									<option value="OS">OS</option>
									<option value="TRUE">TRUE</option>
								</select>
							</div>						
						</div>
					</div>	
					
					<div class="control-group">
						<label class="control-label">remote_login_passwordfile</label>
						<div class="controls">
						  <div class="inputb2l">
							<select id="styleSelect" class="w48" name="oracle_remote_login_passwordfile">
								<option value="EXCLUSIVE" selected="selected">EXCLUSIVE</option>
								<option value="NONE">NONE</option>
								<option value="SHARED">SHARED</option>
							</select>
						   </div>
							<div class="inputb2l">
								<span class="input140 mr20">undo_tablespace</span> 
								<input type="text" id="oracle_undotbs" name="oracle_undotbs"
									value="UNDOTBS1" readonly="readonly" class="w45" />
							</div>
						</div>
					</div>	
					
			      	<div class="control-group">
						<label class="control-label">log_archive_format</label>
						<div class="controls">
							<div class="inputb2l">
								<input type="text" id="oracle_log_archive_format" name="oracle_log_archive_format"
									value="orcl%t_%s_%r.arc" class="w45" />
							</div>
							<div class="inputb2l">
								<span class="input140 mr20">log_archive_dest_1</span> 
								<input type="text" id="oracle_log_archive_dest_1" name="oracle_log_archive_dest_1"
									value="LOCATION=/oracle/archlog" class="w45" />
							</div>							
						</div>
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


</body>
</html>