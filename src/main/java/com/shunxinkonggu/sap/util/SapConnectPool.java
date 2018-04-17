package com.shunxinkonggu.sap.util;

import java.util.Properties;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

import com.sap.mw.jco.JCO;

public class SapConnectPool extends BaseBean {

	private static String client = "300";           // 客户端 测试300 正式800
	private static String user = "RFCUSER";          // 用户名
	private static String passwd = "sap123456";        // 密码19920706
	private static String lang = "ZH";              // 语言
	private static String ashost = "172.16.0.104";  // 服务器的主机名
	private static String sysnr = "00";             // 系统
	private static String sapRouter = "";        // SapRouter /H/saproute.shunxinholdings.com/H/

	JCO.Client connection = null;
	Properties prop = new Properties();

	private static int maxconnection = 5;
	final static String poolname = "ThePool";

	//获取配置文件
	private void getProp() {
		RecordSet rs = new RecordSet();
		rs.executeSql("SELECT * FROM SAP_DATASOURCE WHERE POOLNAME='SAP'");//SAPERP
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

	/**
	 * 初始化链接
	 */
	private void init() {
		//getProp();
		try {
			JCO.Pool pool = JCO.getClientPoolManager().getPool(poolname);
			if (pool == null) {
				prop.put("jco.client.ashost", ashost);
				prop.put("jco.client.client", client);
				prop.put("jco.client.sysnr", sysnr);
				prop.put("jco.client.user", user);
				prop.put("jco.client.passwd", passwd);
				prop.put("jco.client.sapRouter", sapRouter);
				prop.put("jco.client.lang", lang);
				JCO.addClientPool(poolname, maxconnection, prop);
			}
		} catch (Exception e) {
			this.writeLog("建立sap连接池异常：" + e);
		}
	}

	public JCO.Client getConnection() {
		JCO.Pool pool = JCO.getClientPoolManager().getPool(poolname);
		if (pool == null) {
			init();
		}
		connection = JCO.getClient(poolname);
		return connection;
	}

	public void disConnection() {
		JCO.releaseClient(connection);
		connection = null;
	}

	public static void removeConnectionPool() {
		JCO.removeClientPool(poolname);
	}

}
