package com.shunxinkonggu.sap.sycn.impl;

import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

public class JobtitleImpl extends BaseBean{
	
	private ConnStatement statement;
	
	private String id;
	private String jobtitlecode;
	private String jobtitlename;
	private String jobtitlemark;
	private String jobactivityid;
	private String jobdepartmentid;
	
	public boolean insertJobtitle() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "insert into HrmJobTitles (jobtitlecode,jobtitlename,jobtitlemark,jobactivityid,jobdepartmentid) values (?,?,?,?,?)";
			statement.setStatementSql(sql);
			statement.setString(1, this.getJobtitlecode());
			statement.setString(2, this.getJobtitlename());
			statement.setString(3, this.getJobtitlemark());
			statement.setString(4, this.getJobactivityid());
			statement.setString(5, this.getJobdepartmentid());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("insert Jobtitle Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("insertJobtitle Exception :" + e);
			}
		}
		return is_success;
	}
	
	public boolean updateJobtitle() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "update HrmJobTitles set jobtitlecode=?,jobtitlename=?,jobtitlemark=?,jobactivityid=?,jobdepartmentid=? where id = ?";
			statement.setStatementSql(sql);
			statement.setString(1, this.getJobtitlecode());
			statement.setString(2, this.getJobtitlename());
			statement.setString(3, this.getJobtitlemark());
			statement.setString(4, this.getJobactivityid());
			statement.setString(5, this.getJobdepartmentid());
			statement.setString(6, this.getId());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("update Jobtitle Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("updateJobtitle Exception :" + e);
			}
		}
		return is_success;
	}
	
	public boolean deleteJobtitle() {
		boolean is_success = false;
		RecordSet rs = null;
		try {
			rs = new RecordSet();
			if (rs.executeSql("delete from HrmJobTitles where id = " + this.getId())) {
				is_success = true;
			}
		} catch (Exception e) {
			this.writeLog("deleteJobtitle Exception :" + e);
		}
		return is_success;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobtitlename() {
		return jobtitlename;
	}

	public void setJobtitlename(String jobtitlename) {
		this.jobtitlename = jobtitlename;
	}

	public String getJobtitlemark() {
		return jobtitlemark;
	}

	public void setJobtitlemark(String jobtitlemark) {
		this.jobtitlemark = jobtitlemark;
	}

	public String getJobactivityid() {
		return jobactivityid;
	}

	public void setJobactivityid(String jobactivityid) {
		this.jobactivityid = jobactivityid;
	}

	public String getJobdepartmentid() {
		return jobdepartmentid;
	}

	public void setJobdepartmentid(String jobdepartmentid) {
		this.jobdepartmentid = jobdepartmentid;
	}

	public String getJobtitlecode() {
		return jobtitlecode;
	}

	public void setJobtitlecode(String jobtitlecode) {
		this.jobtitlecode = jobtitlecode;
	}
	
}
