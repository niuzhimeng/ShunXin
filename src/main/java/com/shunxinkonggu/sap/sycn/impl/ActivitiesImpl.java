package com.shunxinkonggu.sap.sycn.impl;

import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class ActivitiesImpl extends BaseBean{
	
	private ConnStatement statement;
	
	private String id;
	private String jobactivityname;
	private String jobactivitymark;
	private String jobgroupid;

	public String insertActivities() {
		String returnValue = "";
		statement = new ConnStatement();
		try {
			String sql = "insert into HrmJobActivities (jobactivityname,jobactivitymark,jobgroupid) values (?,?,?)";
			statement.setStatementSql(sql);
			statement.setString(1, this.getJobactivityname());
			statement.setString(2, this.getJobactivitymark());
			statement.setString(3, this.getJobgroupid());
			statement.executeUpdate();
			
		} catch (Exception e) {
			this.writeLog("insert Activities Exception :" + e);
		} finally {
			try {
				statement.close();
				returnValue = this.getActivitiesMaxId();
			} catch (Exception e) {
				this.writeLog("insert Activities close connect Exception :" + e);
			}
		}
		return returnValue;
	}
	
	public void updateActivities() {
		statement = new ConnStatement();
		try {
			String sql = "update HrmJobActivities set jobactivityname=?,jobactivitymark=?,jobgroupid=? where id = ?";
			statement.setStatementSql(sql);
			statement.setString(1, this.getJobactivityname());
			statement.setString(2, this.getJobactivitymark());
			statement.setString(3, this.getJobgroupid());
			statement.setString(4, this.getId());
			statement.executeUpdate();
			
		} catch (Exception e) {
			this.writeLog("updateActivities Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("updateActivities close connect Exception :" + e);
			}
		}
	}
	
	public String getActivitiesIdByCode(String acticode){
		String actid = "";
		if(null == acticode || "".equals(acticode)){
			return actid;
		}
		try {
			RecordSet rs = new RecordSet();
			rs.executeSql("select id from HrmJobActivities where jobactivitymark = '" + acticode + "'");
			if (rs.next()) {
				actid = Util.null2String(rs.getString("id"));
			}

		} catch (Exception e) {
			this.writeLog("getActivitiesIdByCode Exception :" + e);
		}
		return actid;
	}
	
	public String getActivitiesMaxId(){
		String returnValue = "";
		try{
			RecordSet rs = new RecordSet();
			rs.executeSql("select max(id) as maxid from HrmJobActivities");
			if (rs.next()) {
				returnValue = Util.null2String(rs.getString("maxid"));
			}
		}catch(Exception e){
			this.writeLog("getActivitiesMaxId Exception :" + e);
		}
		return returnValue;
	}
	
	public void truncateActivities(){
		try{
			RecordSet rs = new RecordSet();
			rs.executeSql("truncate table hrmjobactivities");
		}catch(Exception e){
			this.writeLog("truncateActivities Exception :" + e);
		}
	}
	
	public boolean deleteActivities() {
		boolean is_success = false;
		RecordSet rs = null;
		try {
			rs = new RecordSet();
			if (rs.executeSql("delete from HrmJobActivities where id = " + this.getId())) {
				is_success = true;
			}
		} catch (Exception e) {
			this.writeLog("deleteActivities Exception :" + e);
		}
		return is_success;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobactivityname() {
		return jobactivityname;
	}

	public void setJobactivityname(String jobactivityname) {
		this.jobactivityname = jobactivityname;
	}

	public String getJobactivitymark() {
		return jobactivitymark;
	}

	public void setJobactivitymark(String jobactivitymark) {
		this.jobactivitymark = jobactivitymark;
	}

	public String getJobgroupid() {
		return jobgroupid;
	}

	public void setJobgroupid(String jobgroupid) {
		this.jobgroupid = jobgroupid;
	}
	
	
}
