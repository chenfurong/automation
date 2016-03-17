<%@ page language="java" %>

<%
	String name = (String) session.getAttribute("userName");
	if (name == null || name.equals("")) {
		response.sendRedirect("login.jsp");
		//request.getRequestDispatcher("login.jsp").forward(request,response);
		return;
	}
%>
