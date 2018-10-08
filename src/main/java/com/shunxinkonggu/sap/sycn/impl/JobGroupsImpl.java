package com.shunxinkonggu.sap.sycn.impl;

import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class JobGroupsImpl extends BaseBean{
	
	private ConnStatement statement;
	
	private String id;
	private String JOBGROUPNAME;
	private String JOBGROUPREMARK;

	public String insertJobGroups() {
		String returnValue = "";
		statement = new ConnStatement();
		try {
			String sql = "insert into HrmJobGroups (JOBGROUPNAME,JOBGROUPREMARK) values (?,?)";
			statement.setStatementSql(sql);
			statement.setString(1, this.getJOBGROUPNAME());
			statement.setString(2, this.getJOBGROUPREMARK());
			statement.executeUpdate();
			
		} catch (Exception e) {
			this.writeLog("insert JobGroups Exception :" + e);
		} finally {
			try {
				statement.close();
				returnValue = this.getJobGroupsMaxId();
			} catch (Exception e) {
				this.writeLog("insert JobGroups close connect Exception :" + e);
			}
		}
		return returnValue;
	}
	
	public void updateJobGroups() {
		statement = new ConnStatement();
		try {
			String sql = "update HrmJobGroups set JOBGROUPNAME=?,JOBGROUPREMARK=? where id = ?";
			statement.setStatementSql(sql);
			statement.setString(1, this.getJOBGROUPNAME());
			statement.setString(2, this.getJOBGROUPREMARK());
			statement.setString(3, this.getId());
			statement.executeUpdate();
			
		} catch (Exception e) {
			this.writeLog("updateJobGroups Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("updateJobGroups close connect Exception :" + e);
			}
		}
	}
	
	public String getJobGroupsIdByCode(String jobgroupscode){
		String actid = "";
		if(null == jobgroupscode || "".equals(jobgroupscode)){
			return actid;
		}
		try {
			RecordSet rs = new RecordSet();
			rs.executeSql("select id from HrmJobGroups where JOBGROUPREMARK = '" + jobgroupscode + "'");
			if (rs.next()) {
				actid = Util.null2String(rs.getString("id"));
			}
		} catch (Exception e) {
			this.writeLog("getJobGroupsIdByCode Exception :" + e);
		}
		return actid;
	}
	
	public String getJobGroupsMaxId(){
		String returnValue = "";
		try{
			RecordSet rs = new RecordSet();
			rs.executeSql("select max(id) as maxid from HrmJobGroups");
			if (rs.next()) {
				returnValue = Util.null2String(rs.getString("maxid"));
			}
		}catch(Exception e){
			this.writeLog("getJobGroupsMaxId Exception :" + e);
		}
		return returnValue;
	}
	
	public void truncateActivities(){
		try{
			RecordSet rs = new RecordSet();
			rs.executeSql("truncate table HrmJobGroups");
		}catch(Exception e){
			this.writeLog("truncate HrmJobGroups Exception :" + e);
		}
	}
	
	public boolean deleteJobGroups() {
		
		boolean is_success = false;
		RecordSet rs = null;
		try {
			rs = new RecordSet();
			if (rs.executeSql("delete from HrmJobGroups where id = " + this.getId())) {
				is_success = true;
			}
		} catch (Exception e) {
			this.writeLog("deleteJobGroups Exception :" + e);
		}
		return is_success;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJOBGROUPNAME() {
		return JOBGROUPNAME;
	}

	public void setJOBGROUPNAME(String jOBGROUPNAME) {
		JOBGROUPNAME = jOBGROUPNAME;
	}

	public String getJOBGROUPREMARK() {
		return JOBGROUPREMARK;
	}

	public void setJOBGROUPREMARK(String jOBGROUPREMARK) {
		JOBGROUPREMARK = jOBGROUPREMARK;
	}
	
}
