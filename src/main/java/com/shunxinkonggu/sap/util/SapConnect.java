package com.shunxinkonggu.sap.util;

import java.util.Properties;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

import com.shunxinkonggu.sap.util.SapConnect;
import com.sap.mw.jco.JCO;

public class SapConnect extends BaseBean {
	
//	private static String client = "300";           // 客户端 测试300 正式800
//	private static String user = "RFCUSER";          // 用户名
//	private static String passwd = "sap123456";        // 密码19920706
//	private static String lang = "ZH";              // 语言
//	private static String ashost = "172.16.0.104";  // 服务器的主机名
//	private static String sysnr = "00";             // 系统
//	private static String sapRouter = "";        // SapRouter /H/saproute.shunxinholdings.com/H/

	private static String client = "800";           // 客户端 测试300 正式800
	private static String user = "SAP-OA";          // 用户名
	private static String passwd = "123456";        // 密码19920706
	private static String lang = "ZH";              // 语言
	private static String ashost = "172.16.0.36";  // 服务器的主机名
	private static String sysnr = "00";             // 系统
	private static String sapRouter = "";        // SapRouter /H/saproute.shunxinholdings.com/H/

	private static JCO.Client mConnection = null;
	Properties prop = new Properties();
	
	public static void main(String[] args) {
		String logcontent = "";
		SapConnect sc = null;
		JCO.Client client = null;
		JCO.Repository repository = null;
		JCO.Function function = null;
		sc = new SapConnect();
		client = sc.getConnection();
	}
	
	private void getProp() {
		RecordSet rs = new RecordSet();
		rs.executeSql("SELECT * FROM SAP_DATASOURCE WHERE POOLNAME='SAP'");
		if (rs.next()) {
			user = Util.null2String(rs.getString("USERNAME"));
			client = Util.null2String(rs.getString("CLIENT"));
			passwd = Util.null2String(rs.getString("PASSWORD"));
			lang = Util.null2String(rs.getString("LANGUAGE"));
			ashost = Util.null2String(rs.getString("HOSTNAME"));
			sysnr = Util.null2String(rs.getString("SYSTEMNUM"));
			sapRouter = Util.null2String(rs.getString("SAPROUTER"));
		}
	}
	
	private void init() {
		getProp();
		try {
			mConnection = JCO.createClient(client, user, passwd, lang, sapRouter+ashost,sysnr);
			mConnection.connect();
		} catch (Exception e) {

		}
	}

	public static JCO.Client Conn() {
		try {
			mConnection = JCO.createClient(client, user, passwd, lang, sapRouter+ashost,
					sysnr);
			mConnection.connect();
			return mConnection;
		} catch (Exception e) {
			
		}
		return null;
	}

	/**
	 * 
	 */
	public JCO.Client getConnection() {
		if (mConnection == null) {
			init();
		}
		return mConnection;
	}

	public void setConnection(JCO.Client conn) {
		mConnection = conn;
	}
	public void disConnection(){
		mConnection.disconnect();
		mConnection=null;
	}

}
