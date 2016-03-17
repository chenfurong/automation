package com.ibm.automation.core.service;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.core.bean.AddHostBean;

public interface AddHostService {
	public int create(AddHostBean ah );//增加主机
	public int createOne(ObjectNode on);//增加数组单个节点
	public int modifyOne(ObjectNode on);//增加修改单个节点
	public int HostCheck(String ip , String type );//判断IP是否已经添加
	public String searchServers(String iporname);//根据IP或者name检索所有的serves
}
