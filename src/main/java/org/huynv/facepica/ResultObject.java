package org.huynv.facepica;

import java.util.ArrayList;

public class ResultObject {
	private String status;
	private ArrayList<Object> object;
	private String info;
	
	public ResultObject(String stt, String obj, String inf) {
		this.status = stt;
		this.object = new ArrayList<Object>();
		this.object.add(obj);
		this.info = inf;
	}
	
	public ResultObject(String stt) {
		this.status = stt;
		this.object = new ArrayList<Object>();
		this.info = "unknown";
	}
	
	public ResultObject() {
		
	}
	
	public void setStatus(String stt) {
		this.status = stt;
	}
	
	public void setInfo(String inf) {
		this.info = inf;
	}
	
	public void setObject(Object obj) {
		if (this.object.size() > 0) {
			this.object.add(obj);
		} else {
			this.object = new ArrayList<Object>();
			this.object.add(obj);
		}
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public String getInfo() {
		return this.info;
	}
	
	public Object getObject() {
		ArrayList objects = new ArrayList<String>();

		for (int i=0;i<this.object.size();i++) {
			objects.add(this.object.get(i));
		}
		return objects;
	}
}
