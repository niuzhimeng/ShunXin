package com.shunxinkonggu.sap.sycn.impl;

import java.util.Calendar;

import com.shunxinkonggu.sap.util.Utils;

import weaver.conn.ConnStatement;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

public class HrmResourceImpl extends BaseBean{
	
	private ConnStatement statement;
	
	private String id;
	private String lastname;
	private String loginid;
	private String workcode;
	private String status;
	private String sex;
	private String email;
	private String mobile;
	//private String managerid;
	private String accounttype;
	private String belongto;
	private String departmentid;
	private String subcompanyid1;
	private String jobtitle;
	//private String managerstr;
	//private String password;
	//private String seclevel;
	private String startdate;
	private String birthday;
	
	public boolean insertHrmResource() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "insert into HrmResource (id,workcode,lastname,subcompanyid1,departmentid,jobtitle,sex,status," +
					"mobile,email,loginid,accounttype,belongto,systemlanguage,startdate,birthday,password) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			statement.setStatementSql(sql);
			statement.setString(1, this.getId());
			statement.setString(2, this.getWorkcode());
			statement.setString(3, this.getLastname());
			statement.setString(4, this.getSubcompanyid1());
			statement.setString(5, this.getDepartmentid());
			statement.setString(6, this.getJobtitle());
			statement.setString(7, this.getSex());
			statement.setString(8, this.getStatus());
			statement.setString(9, this.getMobile());
			statement.setString(10, this.getEmail());
			statement.setString(11, this.getLoginid());
			statement.setString(12, this.getAccounttype());
			statement.setString(13, this.getBelongto());
			statement.setString(14, "7");
			statement.setString(15, this.getStartdate());
			statement.setString(16, this.getBirthday());
			statement.setString(17, "C4CA4238A0B923820DCC509A6F75849B");
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("insert HrmResource Exception :" + e);
		} finally {
			try {
				statement.close();
				this.UpdateRights(this.getId());
			} catch (Exception e) {
				this.writeLog("insert HrmResource close connect Exception :" + e);
			}
		}
		return is_success;
	}
	
	
	public boolean updateHrmResource() {
		boolean is_success = false;
		statement = new ConnStatement();
		try {
			String sql = "update HrmResource set workcode=?,lastname=?,subcompanyid1=?,departmentid=?,jobtitle=?,sex=?,status=?," +
					"mobile=?,email=?,loginid=?,accounttype=?,belongto=?,startdate=?,birthday=? where id = ?";
			statement.setStatementSql(sql);
			statement.setString(1, this.getWorkcode());
			statement.setString(2, this.getLastname());
			statement.setString(3, this.getSubcompanyid1());
			statement.setString(4, this.getDepartmentid());
			statement.setString(5, this.getJobtitle());
			statement.setString(6, this.getSex());
			statement.setString(7, this.getStatus());
			statement.setString(8, this.getMobile());
			statement.setString(9, this.getEmail());
			statement.setString(10, this.getLoginid());
			statement.setString(11, this.getAccounttype());
			statement.setString(12, this.getBelongto());
			statement.setString(13, this.getStartdate());
			statement.setString(14, this.getBirthday());
			statement.setString(15, this.getId());
			statement.executeUpdate();
			is_success = true;
		} catch (Exception e) {
			this.writeLog("update HrmResource Exception :" + e);
		} finally {
			try {
				statement.close();
				this.UpdateRights(this.getId());
			} catch (Exception e) {
				this.writeLog("update HrmResource close connect Exception :" + e);
			}
		}
		return is_success;
	}
	
	public boolean deleteHrmResource5(String hrmid,String status){
		boolean is_success = false;
		if(null == hrmid || "".equals(hrmid)){
			return is_success;
		}
		
		if(null == status || "".equals(status)){
			return is_success;
		}
		
		if("0".equals(status) || "1".equals(status) || "2".equals(status) || "3".equals(status)){
			return is_success;
		}
		
		RecordSet rs = null;
		try {
			rs = new RecordSet();
			rs.executeSql("delete from hrmrolemembers where resourceid= " + hrmid);
            rs.executeSql("delete from PluginLicenseUser where plugintype='mobile' and sharetype='0' and sharevalue='" + hrmid + "'");
            rs.executeSql("update HrmResource set status = "+status+", loginid='',password='' ,account='' where id = " + hrmid);
            rs.executeSql("delete hrmgroupmembers where userid= " + hrmid);
            rs.executeSql("select max(id) from HrmStatusHistory");
            rs.next();
            rs.executeSql("update HrmStatusHistory set isdispose = 1 where id= " + rs.getInt(1));
            is_success = true;
		} catch (Exception e) {
			this.writeLog("update HrmResource canceled Exception :" + e);
		}
		return is_success;
	}
	
	private void UpdateRights(String maxid){
		try{
			char separator = Util.getSeparator();
			Calendar todaycal = Calendar.getInstance();
	        String today = Util.add0(todaycal.get(Calendar.YEAR), 4) + "-" + Util.add0(todaycal.get(Calendar.MONTH) + 1, 2) + "-" +Util.add0(todaycal.get(Calendar.DAY_OF_MONTH), 2);
	        String userpara = "" + 1 + separator + today;
			RecordSet rs = new RecordSet();
			rs.executeProc("HrmResource_CreateInfo", "" + maxid + separator + userpara + separator + userpara);
			rs.executeSql("select hrmid from HrmInfoStatus where hrmid="+maxid);
			if(!rs.next()){
                String sql_1 = "insert into HrmInfoStatus (itemid,hrmid,status) values(1," + maxid + ",1)";
                rs.executeSql(sql_1);
                String sql_2 = "insert into HrmInfoStatus (itemid,hrmid) values(2," + maxid + ")";
                rs.executeSql(sql_2);
                String sql_3 = "insert into HrmInfoStatus (itemid,hrmid) values(3," + maxid + ")";
                rs.executeSql(sql_3);
                String sql_10 = "insert into HrmInfoStatus (itemid,hrmid) values(10," + maxid + ")";
                rs.executeSql(sql_10);
            }
		}catch(Exception e){
			this.writeLog("update rights Exception :" + e);
		}
	}
	
	public String getManagerIdAndStr(String managerid) {
		RecordSet rs = new RecordSet();
		String returnStr = "";
		if (null == managerid || "".equals(managerid)) {
			return returnStr;
		}
		try {
			rs.executeSql("select managerstr from HrmResource where id = " + managerid);
			String managerstr = "";
			String managerId = managerid;
			if (rs.next()) {
				managerstr = Util.null2String(rs.getString("managerstr"));
				if (managerstr.equals("")) {
					managerstr = "" + managerId;
				} else {
					managerstr = managerstr + "," + managerId;
				}
			}
			returnStr = Utils.replaceStr(managerstr) + ",";
		} catch (Exception e) {
			this.writeLog("get hrmresource all manager Exception :" + e);
		}
		return returnStr;
	}
	
	public String getSubidByDepid(String depid) {
		String returnValue = "";
		if(null == depid || "".equals(depid)){
			return returnValue;
		}
		try {
			RecordSet rs = new RecordSet();
			rs.executeSql("select subcompanyid1 from HrmDepartment where id =" + depid);
			if (rs.next()) {
				returnValue = Util.null2String(rs.getString("subcompanyid1"));
			}
		} catch (Exception e) {
			this.writeLog("getSubidByDepid Exception" + e);
		}
		return returnValue;
	}
	
	public String getStatus(String stat1){
		String returnValue = "";
		try{
			if("A".equals(stat1)){
				returnValue = "1";
			}else if("B".equals(stat1)){
				returnValue = "0";
			}else if("C".equals(stat1)){
				returnValue = "3";
			}else if("D".equals(stat1)){
				returnValue = "2";
			}else if("E".equals(stat1)){
				returnValue = "6";
			}else if("F".equals(stat1)){
				returnValue = "7";
			}else if("G".equals(stat1)){
				returnValue = "7";
			}else if("H".equals(stat1)){
				returnValue = "7";
			}else if("I".equals(stat1)){
				returnValue = "7";
			}else if("J".equals(stat1)){
				returnValue = "5";
			}else if("K".equals(stat1)){
				returnValue = "1";
			}else if("L".equals(stat1)){
				returnValue = "7";
			}else if("M".equals(stat1)){
				returnValue = "1";
			}
		}catch(Exception e){
			this.writeLog("getStatus Exception" + e);
		}
		return returnValue;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWorkcode() {
		return workcode;
	}

	public void setWorkcode(String workcode) {
		this.workcode = workcode;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}

	public String getSubcompanyid1() {
		return subcompanyid1;
	}

	public void setSubcompanyid1(String subcompanyid1) {
		this.subcompanyid1 = subcompanyid1;
	}
	
	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginid() {
		return loginid;
	}


	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getAccounttype() {
		return accounttype;
	}


	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}


	public String getBelongto() {
		return belongto;
	}


	public void setBelongto(String belongto) {
		this.belongto = belongto;
	}


	public String getStartdate() {
		return startdate;
	}


	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}


	public String getBirthday() {
		return birthday;
	}


	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	

}
