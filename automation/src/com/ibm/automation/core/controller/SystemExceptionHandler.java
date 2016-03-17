package com.ibm.automation.core.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ibm.automation.core.exception.handler.PopUpMessageAndReturnException;
import com.ibm.automation.core.exception.handler.PopUpMessageException;
import com.ibm.automation.core.exception.handler.ReturnToLastPageException;
import com.ibm.automation.core.exception.handler.ReturnToMainPageException;

/**
 * 全局异常处理器，跳转到错误页面。所有未处理的异常，都会被处理。
 *
 * @author leiwen@FansUnion.cn
 */
@Component
public class SystemExceptionHandler implements HandlerExceptionResolver {

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		System.out.println(ex.getClass().getName());

		
		String exHeader = "<html><head><link type=\"text/css\" rel=\"stylesheet\" href=\"" 
				+ request.getContextPath() + "/css/exception.css\"></head><body>";
		String exFooter = "</body></html>";
		
		//关闭打开的弹出款的时候，异常处理用本逻辑
		if (ex instanceof PopUpMessageException) {
			request.setAttribute("message", ex.getMessage());
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			try {
				String alertString = "<script>alert('"
						+ replaceBlank(ex.getMessage().toString().replace("{", "").replace("}", "").replace("\'", ""))
						+ "')</script>";

				response.getWriter().print(exHeader);
				response.getWriter().print(alertString);
				response.getWriter().print(
						"<script>parent.closePopup();</script>");
				response.getWriter().print(exFooter);
				response.getWriter().flush();
				response.getWriter().close();
				return null;
			} catch (IOException e) {
				request.setAttribute("message", ex.getMessage());
				return new ModelAndView("exception");
			}
		}
		
		if (ex instanceof PopUpMessageAndReturnException) {
			request.setAttribute("message", ex.getMessage());
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			try {
				String alertString = "<script>alert('"
						+ replaceBlank(ex.getMessage().toString().replace("{", "").replace("}", "").replace("\'", ""))
						+ "')</script>";

				response.getWriter().print(exHeader);
				response.getWriter().print(alertString);
				response.getWriter().print(
						"<script>parent.closePopup();</script>");
				response.getWriter().print(
						"<script>Javascript:window.history.go(-1);</script>");
				response.getWriter().print(exFooter);
				response.getWriter().flush();
				response.getWriter().close();
				return null;
			} catch (IOException e) {
				request.setAttribute("message", ex.getMessage());
				return new ModelAndView("exception");
			}
		}
		//遇到异常后返回主页的逻辑处理
		if (ex instanceof ReturnToMainPageException) {
			request.setAttribute("message", ex.getMessage());
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			try {
				String alertString = "<script>alert('"
						+ replaceBlank(ex.getMessage().toString().replace("{", "").replace("}", "").replace("\'", ""))
						+ "')</script>";

				response.getWriter().print(exHeader);
				response.getWriter().print(alertString);
				response.getWriter().print(
						"<script>parent.closePopup();</script>");
				response.getWriter().print("<script>window.location.href='toIndexPage';</script>");
				response.getWriter().print(exFooter);
				response.getWriter().flush();
				response.getWriter().close();
				return null;
			} catch (IOException e) {
				request.setAttribute("message", ex.getMessage());
				return new ModelAndView("exception");
			}
		}
		//遇到异常后返回上一页的逻辑处理
		if (ex instanceof ReturnToLastPageException) {
			request.setAttribute("message", ex.getMessage());
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			try {
				String alertString = "<script>alert('"
						+ replaceBlank(ex.getMessage().toString().replace("{", "").replace("}", "").replace("\'", ""))
						+ "')</script>";

				response.getWriter().print(exHeader);
				response.getWriter().print(alertString);
				response.getWriter().print(
						"<script>Javascript:window.history.go(-1);</script>");
				response.getWriter().print(exFooter);
				response.getWriter().flush();
				response.getWriter().close();
				return null;
			} catch (IOException e) {
				request.setAttribute("message", ex.getMessage());
				return new ModelAndView("exception/exception");
			}
		}
		
		String msg = "业务处理错误:"+ex.getMessage();
		request.setAttribute("message", msg);
		return new ModelAndView("exception/exception");
	}

	public String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}
