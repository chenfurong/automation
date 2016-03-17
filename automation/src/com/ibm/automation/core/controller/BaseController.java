package com.ibm.automation.core.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ibm.automation.core.util.Page;
import com.ibm.automation.core.util.PageState;
import com.ibm.automation.core.util.PageUtil;

/**
 * 类名称：BaseController 类描述： 分页控制类 创建人：梁瑞 创建时间：2014-3-31 上午11:44:54 修改人： 修改时间：
 * 修改备注：
 * 
 * @version 1.0
 *
 */
public class BaseController {
	private static final Logger logger = Logger.getLogger(BaseController.class);

	/**
	 * oracel的三层分页语句 子类在展现数据前,进行分页计算!
	 * 
	 * @param querySql
	 *            查询的SQL语句,未进行分页
	 * @param totalCount
	 *            根据查询SQL获取的总条数
	 * @param columnNameDescOrAsc
	 *            列名+排序方式 : ID DESC or ASC
	 */
	protected Page executePage(HttpServletRequest request, Long totalCount) {
		if (null == totalCount) {
			totalCount = 0L;
		}
		/** 页面状态,这个状态是分页自带的,与业务无关 */
		String pageAction = request.getParameter("pageAction");
		String value = request.getParameter("pageKey");

		/** 获取下标判断分页状态 */
		int index = PageState.getOrdinal(pageAction);

		Page page = null;
		/**
		 * index < 1 只有二种状态 1 当首次调用时,分页状态类中没有值为 NULL 返回 -1 2 当页面设置每页显示多少条:
		 * index=0,当每页显示多少条时,分页类要重新计算
		 * */
		Page sessionPage = getPage(request);

		if (index < 1) {
			page = PageUtil.inintPage(totalCount, index, value, sessionPage);
		} else {
			page = PageUtil.execPage(index, value, sessionPage);
		}
		setSession(request, page);
		return page;
	}

	private Page getPage(HttpServletRequest request) {
		Page page = (Page) request.getSession().getAttribute(
				PageUtil.SESSION_PAGE_KEY);
		if (page == null) {
			page = new Page();
		}
		return page;
	}

	private void setSession(HttpServletRequest request, Page page) {
		request.getSession().setAttribute(PageUtil.SESSION_PAGE_KEY, page);
	}

	public String setException(Exception e, HttpServletRequest request) {
		request.setAttribute("message", e.getMessage());
		logger.info("异常信息：" + e.getMessage());
		return "exception/exception";
	}

	public String setWindowException(Exception e, HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("<script>alert('" + e.getMessage().toString() + "')</script>");
		response.getWriter().flush();
		response.getWriter().close();
		return null;
	}

	public String setExceptionTologin(Exception e, HttpServletRequest request) {
		logger.info("异常信息：" + e.getMessage());
		return "redirect:/gologin";
	}

	public String setExceptionToMessageBox(Exception e, HttpServletRequest request,HttpServletResponse response,String url,Map<String, String> paramMap) throws IOException {
		logger.info("异常信息：" + e.getMessage());
		
		request.setAttribute("message", e.getMessage());
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		if (paramMap.isEmpty()) {
			response.getWriter().print("<script>window.location.href=\""+url+"?message="+e.getMessage()+"\";</script>");
		
		}
		else {
			String paramString = "";
			Set<Map.Entry<String, String>> mapKeySet = paramMap
					.entrySet();
			for (Iterator<Map.Entry<String, String>> it = mapKeySet
					.iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it
						.next();
				paramString=paramString+entry.getKey()+"="+entry.getValue();
			}

			response.getWriter().print("<script>javascript:parent.closePopup();</script>");
			//System.out.println("<script>window.location.href=\""+url+"?"+paramString+"&message="+e.getMessage()+"\";</script>");
			response.getWriter().print("<script>window.location.href=\""+url+"?"+paramString+"&message="+"test"+"\";</script>");
				
			
			
		}
		
		response.getWriter().flush();
		response.getWriter().close();
		return null;
	}
	
}
