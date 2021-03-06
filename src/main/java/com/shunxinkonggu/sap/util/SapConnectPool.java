package com.shunxinkonggu.sap.util;

import java.util.Properties;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;

import com.sap.mw.jco.JCO;

public class SapConnectPool extends BaseBean {

	private static String client = "300";           // 客户端 测试300 正式800
	private static String user = "SAP-OA";          // 用户名
	private static String passwd = "123456";        // 密码19920706
	private static String lang = "ZH";              // 语言
	private static String ashost = "172.16.0.35";  // 服务器的主机名
	private static String sysnr = "00";             // 系统
	private static String sapRouter = "";        // SapRouter /H/saproute.shunxinholdings.com/H/

//  private static String client = "800";           // 客户端 测试300 正式800
//	private static String user = "RFCOA";          // 用户名
//	private static String passwd = "rfcoa2018";        // 密码19920706
//	private static String lang = "ZH";              // 语言
//	private static String ashost = "172.16.0.102";  // 服务器的主机名
//	private static String sysnr = "00";             // 系统
//	private static String sapRouter = "";        // SapRouter /H/saproute.shunxinholdings.com/H/

	private JCO.Client connection = null;
	private Properties prop = new Properties();

	private final static int MAX_CONNECTION = 5;
	private final static String POOL_NAME = "qjThePool";

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
	 * 创建连接池
	 */
	private void init() {
		//getProp();
		try {
			JCO.Pool pool = JCO.getClientPoolManager().getPool(POOL_NAME);
			if (pool == null) {
				prop.put("jco.client.ashost", ashost);
				prop.put("jco.client.client", client);
				prop.put("jco.client.sysnr", sysnr);
				prop.put("jco.client.user", user);
				prop.put("jco.client.passwd", passwd);
				prop.put("jco.client.sapRouter", sapRouter);
				prop.put("jco.client.lang", lang);
				JCO.addClientPool(POOL_NAME, MAX_CONNECTION, prop);
			}
		} catch (Exception e) {
			this.writeLog("建立sap连接池异常：" + e);
		}
	}

	public JCO.Client getConnection() {
		JCO.Pool pool = JCO.getClientPoolManager().getPool(POOL_NAME);
		if (pool == null) {
			init();
		}
		connection = JCO.getClient(POOL_NAME);
		return connection;
	}

	public void disConnection() {
		JCO.releaseClient(connection);
		connection = null;
	}

	public static void removeConnectionPool() {
		JCO.removeClientPool(POOL_NAME);
	}

}
