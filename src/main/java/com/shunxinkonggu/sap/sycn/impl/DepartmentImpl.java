package com.shunxinkonggu.sap.sycn.impl;

import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class DepartmentImpl extends BaseBean{
	private ConnStatement statement;

	private String id;
	private String departmentcode;
	private String departmentname;
	private String departmentmark;
	private String subcompanyid1;
	private String supdepid;
	private String showorder;
	private String canceled;

	public boolean insertDepartment() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "insert into HrmDepartment (departmentcode, departmentname, departmentmark, subcompanyid1, supdepid, showorder, canceled) values (?,?,?,?,?,?,?)";
			statement.setStatementSql(sql);
			statement.setString(1, this.getDepartmentcode());
			statement.setString(2, this.getDepartmentname());
			statement.setString(3, this.getDepartmentmark());
			statement.setString(4, this.getSubcompanyid1());
			statement.setString(5, this.getSupdepid());
			statement.setString(6, this.getShoworder());
			statement.setString(7, this.getCanceled());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("insert department Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("insert department close connect Exception :" + e);
			}
		}
		return is_success;
	}

	public boolean updateDepartment() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "update HrmDepartment set departmentcode=?,departmentname=?,departmentmark=?,subcompanyid1=?,supdepid=?,showorder=?,canceled=? where id = ?";
			statement.setStatementSql(sql);
			statement.setString(1, this.getDepartmentcode());
			statement.setString(2, this.getDepartmentname());
			statement.setString(3, this.getDepartmentmark());
			statement.setString(4, this.getSubcompanyid1());
			statement.setString(5, this.getSupdepid());
			statement.setString(6, this.getShoworder());
			statement.setString(7, this.getCanceled());
			statement.setString(8, this.getId());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("update department Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("update department close connect Exception :" + e);
			}
		}
		return is_success;
	}
	
	public int getDeptSupid(int depid){
		int returnValue = 0;
		
		RecordSet rs = null;
		try{
			
			rs = new RecordSet();
			
			int supdepid = 0;
			
			int id = 0;
			rs.executeSql("select id,supdepid from HrmDepartment where id = " + depid);
			if(rs.next()){
				id = Util.getIntValue(rs.getString("id"));
				supdepid = Util.getIntValue(rs.getString("supdepid"));
			}
			if(supdepid > 0){
				this.getDeptSupid(supdepid);
			}else{
				returnValue = this.getSubcompanyid(id);
			}
		}catch(Exception e){
			this.writeLog("getDeptSupid Exception :" + e);
		}
		return returnValue;
	}
	
	public int getSubcompanyid(int depid){
		int returnValue = 0;
		
		RecordSet rs = null;
		try{
			rs = new RecordSet();
			rs.executeSql("select subcompanyid1 from HrmDepartment where id = " + depid);
			if(rs.next()){
				returnValue = Util.getIntValue(rs.getString("subcompanyid1"));
			}
		}catch(Exception e){
			this.writeLog("getSubcompanyid Exception :" + e);
		}
		return returnValue;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepartmentname() {
		return departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getDepartmentmark() {
		return departmentmark;
	}

	public void setDepartmentmark(String departmentmark) {
		this.departmentmark = departmentmark;
	}

	public String getSupdepid() {
		return supdepid;
	}

	public void setSupdepid(String supdepid) {
		this.supdepid = supdepid;
	}

	public String getSubcompanyid1() {
		return subcompanyid1;
	}

	public void setSubcompanyid1(String subcompanyid1) {
		this.subcompanyid1 = subcompanyid1;
	}

	public String getShoworder() {
		return showorder;
	}

	public void setShoworder(String showorder) {
		this.showorder = showorder;
	}

	public String getDepartmentcode() {
		return departmentcode;
	}

	public void setDepartmentcode(String departmentcode) {
		this.departmentcode = departmentcode;
	}
	
	public String getCanceled() {
		return canceled;
	}

	public void setCanceled(String canceled) {
		this.canceled = canceled;
	}
}
