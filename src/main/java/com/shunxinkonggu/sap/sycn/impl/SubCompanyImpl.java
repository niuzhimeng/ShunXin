package com.shunxinkonggu.sap.sycn.impl;


import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

public class SubCompanyImpl extends BaseBean{
	private ConnStatement statement;
	private String id;
	private String subcompanycode;
	private String subcompanyname;
	private String subcompanydesc;
	private String supsubcomid;
	private String showorder;
	private String canceled;

	public boolean insertSubCompany() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "insert into HrmSubCompany (subcompanycode,subcompanyname,subcompanydesc,supsubcomid,showorder,companyid,canceled) values (?,?,?,?,?,?,?)";
			statement.setStatementSql(sql);
			statement.setString(1, this.getSubcompanycode());
			statement.setString(2, this.getSubcompanyname());
			statement.setString(3, this.getSubcompanydesc());
			statement.setString(4, this.getSupsubcomid());
			statement.setString(5, this.getShoworder());
			statement.setString(6, "1");
			statement.setString(7, this.getCanceled());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("insert SubCompany Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("insert SubCompany close connect Exception :" + e);
			}
		}
		return is_success;
	}

	public boolean updateSubCompany() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "update HrmSubCompany set subcompanycode=?,subcompanyname=?,subcompanydesc=?,supsubcomid=?,showorder=?,canceled=? where id = ?";
			statement.setStatementSql(sql);
			statement.setString(1, this.getSubcompanycode());
			statement.setString(2, this.getSubcompanyname());
			statement.setString(3, this.getSubcompanydesc());
			statement.setString(4, this.getSupsubcomid());
			statement.setString(5, this.getShoworder());
			statement.setString(6, this.getCanceled());
			statement.setString(7, this.getId());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("update SubCompany Exception :" + e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				this.writeLog("update SubCompany close connect Exception :" + e);
			}
		}
		return is_success;
	}

	public void insertMenuconfig(String id) {
		RecordSet rs = null;
		try {
			rs = new RecordSet();
			rs.executeSql("insert into leftmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  select  distinct  userid,infoid,visible,viewindex," + id + ",2,locked,lockedbyid,usecustomname,customname,customname_e from leftmenuconfig  where resourcetype=1  and resourceid=1");
			rs.executeSql("insert into mainmenuconfig (userid,infoid,visible,viewindex,resourceid,resourcetype,locked,lockedbyid,usecustomname,customname,customname_e)  select  distinct  userid,infoid,visible,viewindex," + id + ",2,locked,lockedbyid,usecustomname,customname,customname_e from mainmenuconfig where resourcetype=1  and resourceid=1");
		} catch (Exception e) {
			this.writeLog("insert Menuconfig Exception :" + e);
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubcompanycode() {
		return subcompanycode;
	}

	public void setSubcompanycode(String subcompanycode) {
		this.subcompanycode = subcompanycode;
	}

	public String getSubcompanyname() {
		return subcompanyname;
	}

	public void setSubcompanyname(String subcompanyname) {
		this.subcompanyname = subcompanyname;
	}

	public String getSubcompanydesc() {
		return subcompanydesc;
	}

	public void setSubcompanydesc(String subcompanydesc) {
		this.subcompanydesc = subcompanydesc;
	}

	public String getSupsubcomid() {
		return supsubcomid;
	}

	public void setSupsubcomid(String supsubcomid) {
		this.supsubcomid = supsubcomid;
	}

	public String getShoworder() {
		return showorder;
	}

	public void setShoworder(String showorder) {
		this.showorder = showorder;
	}
	
	public String getCanceled() {
		return canceled;
	}

	public void setCanceled(String canceled) {
		this.canceled = canceled;
	}
}
