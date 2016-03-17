package com.ibm.automation.core.bean;

import java.io.Serializable;

public class AddHostBean implements Serializable,Comparable<AddHostBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6626380570577382415L;
	private String _id="";
	private String Name ="";//名称
	private String IP="";//ip


	private String UserID="";//userId
	private String Password="";//password
	private String JTime="";
	private String Status="";
	private String HConf="";
	private String OS="";//os 
	private String type="";//账号的增、删、查、改 create modify remove search	
	private String HVisor;
  //  private Boolean isdisabled = true;//true:checkbox显示可进行下一步操作
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getJTime() {
		return JTime;
	}

	public void setJTime(String jTime) {
		JTime = jTime;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getHConf() {
		return HConf;
	}

	public void setHConf(String hConf) {
		HConf = hConf;
	}

	public String getOS() {
		return OS;
	}

	public void setOS(String oS) {
		OS = oS;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHVisor() {
		return HVisor;
	}

	public void setHVisor(String hVisor) {
		HVisor = hVisor;
	}

	@Override
	public int compareTo(AddHostBean s) {
		// TODO Auto-generated method stub
		return s.get_id().compareTo(this.get_id())==0?this.getName().compareTo(s.getName()):s.get_id().compareTo(this.get_id());
		
	}
	
}
