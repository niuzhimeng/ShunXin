package com.shunxinkonggu.sap.util;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import weaver.conn.RecordSet;
import weaver.general.Util;

public class Utils {
	
	public static Log log = LogFactory.getLog(Utils.class.getName());
	
	/***
	 * 通过编码转ID
	 * @param tablename 表名
	 * @param code	编码
	 * @return
	 */
	public static String getIdByCode(String tablename, String code) {
		RecordSet rs = null;
		String returnValue = "";
		if (null == tablename || "".equals(tablename)) {
			return returnValue;
		}
		if (null == code || "".equals(code)) {
			return returnValue;
		}
		
		try {
			
			String codeColumnName = "";
			
			if (tablename.equalsIgnoreCase("hrmsubcompany")) {
				codeColumnName = "subcompanycode"; 
			} else if (tablename.equalsIgnoreCase("hrmdepartment")) {
				codeColumnName = "departmentcode"; 
			} else if (tablename.equalsIgnoreCase("hrmjobtitles")) {
				codeColumnName = "jobtitlecode";
			} else if (tablename.equalsIgnoreCase("hrmresource")) {
				codeColumnName = "workcode";
			}
			
			if("".equals(codeColumnName)){
				return returnValue;
			}
			
			rs = new RecordSet();
			
			rs.executeSql("select id from " + tablename + " where " + codeColumnName + " = '" + code + "'");
			if (rs.next()) {
				returnValue = Util.null2String(rs.getString("id"));
			}
			
		} catch (Exception e) {
			log.info("error in getIdByCode");
			log.info("tablename=" + tablename + ",code=" + code);
		}
		return returnValue;
	}
	
	/***
	 * 通过ID得到SAP编码
	 * @param tablename 表名
	 * @param code	编码
	 * @return
	 */
	public static String getCodeById(String tablename, String id) {
		RecordSet rs = null;
		String returnValue = "";
		if (null == tablename || "".equals(tablename)) {
			return returnValue;
		}
		if (null == id || "".equals(id)) {
			return returnValue;
		}
		try {
			
			String codeColumnName = "";
			
			if (tablename.equalsIgnoreCase("hrmsubcompany")) {
				codeColumnName = "subcompanycode"; 
			} else if (tablename.equalsIgnoreCase("hrmdepartment")) {
				codeColumnName = "departmentcode"; 
			} else if (tablename.equalsIgnoreCase("hrmjobtitles")) {
				codeColumnName = "jobtitlecode";
			} else if (tablename.equalsIgnoreCase("hrmresource")) {
				codeColumnName = "workcode";
			}
			
			if("".equals(codeColumnName)){
				return returnValue;
			}
			rs = new RecordSet();
			rs.executeSql("select " + codeColumnName + " from " + tablename + " where id = " + id);
			if (rs.next()) {
				returnValue = Util.null2String(rs.getString(codeColumnName));
			}
		} catch (Exception e) {
			log.info("error in getIdByCode");
			log.info("tablename=" + tablename + ",code=" + id);
		}
		return returnValue;
	}
	
	/***
	 * 得到人员最大ID
	 * 
	 * @return
	 */
	public static int getHrmMaxid() {
		int maxID = 1;
		RecordSet rs = new RecordSet();
		try {
			rs.executeProc("HrmResourceMaxId_Get","");
			if(rs.next()){
				maxID = rs.getInt(1);
			}
		} catch (Exception e) {
			log.error("得到最大的人员ID错误(插入)！" + e);
		}
		return maxID;
	}
	
	public static String replaceStr(String str) {
		ArrayList list = Util.TokenizerString(str, ",");
		String temp = "";
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (temp.equals(""))
					temp = (String) list.get(i);
				else
					temp += "," + (String) list.get(i);
			}
		}
		return temp;
	}
	
	public static String getWorkFlowBillTable(String workflowid){
		String returnValue = "";
		try{
			RecordSet rs = new RecordSet();
			if(null == workflowid || "".equals(workflowid)){
				return returnValue;
			}
			rs.executeSql("select tablename from workflow_bill where id = (select formid from workflow_base where id= "+ workflowid +" and isbill=1)");
			if(rs.next()){
				returnValue = Util.null2String(rs.getString("tablename")); 
			}
		}catch(Exception e){
			returnValue = "";
		}
		return returnValue;
	}
}
