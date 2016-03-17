package com.ibm.automation.core.util;

import org.apache.commons.lang.StringUtils;

/**
 * @Title：PageState 
 * @Description:分页状态类    
 * @Auth: LiangRui
 * @CreateTime:2015年4月1日 上午10:49:11     
 * @version V1.0
 */
public enum PageState {
	
	/**
	 * 设置每页显示多少条
	 * */
	SETPAGE,
	/**
	 * 首页
	 * */
	FIRST, 
	/**
	 * 向前一页
	 * */
	PREVIOUS, 
	/**
	 * 向后一页
	 * */
	NEXT, 
	/**
	 * 末页
	 * */
	LAST, 
	/**
	 * 排序
	 * */
	SORT,
	/**
	 * 到第多少页
	 * */
	GOPAGE;

	
	/**
	 * @param value 索引名称
	 * @return 返回索引下标
	 */
	public static int getOrdinal(String value) {
		int index = -1;
		if (StringUtils.isEmpty(value)) {
			return index;
		}
		String newValue = StringUtils.trim(value).toUpperCase();
		try {
			index = valueOf(newValue).ordinal();
		} catch (IllegalArgumentException e) {}
		return index;
	}
}