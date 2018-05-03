package com.shunxinkonggu.sap.action;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

import com.shunxinkonggu.sap.util.SapConnectPool;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;

/***
 * 员工加班
 *
 * @author 刘晔
 *
 */
public class HRA10_WorkingOvertime_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA10_WorkingOvertime_Action start --- ");

		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();
		String fromTable = request.getRequestManager().getBillTableName();

		this.writeLog("HRA10_WorkingOvertime_Action 员工加班 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);

		if(operatetype.equals("submit")){

			String pernr = ""; // 人员编码
			String qqdbd = ""; // 开始日期
			String qqdbt = ""; // 加班情况


			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {

				this.writeLog("HRA10_WorkingOvertime_Action fromTable --- " + fromTable);

				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_HR_HRA10_UPDATE");
				function = new JCO.Function(ft);

				rs = new RecordSet();
				rs.execute("select * from " + fromTable + "_dt1 where mainid in (select id from " + fromTable + " where requestid =" + requestid+")");

				int itempbc = 0;
				JCO.Table jiabandetail = function.getTableParameterList().getTable("IT_HRA10");

				while (rs.next()) {

					pernr = Util.null2String(rs.getString("gh"));
					qqdbd = Util.null2String(rs.getString("ksrq"));
					qqdbt = Util.null2String(rs.getString("sjd1"));

					qqdbd = qqdbd.replace("-", "");

					this.writeLog("HRA10_WorkingOvertime_Action pernr --- " + pernr);
					this.writeLog("HRA10_WorkingOvertime_Action qqdbd --- " + qqdbd);
					this.writeLog("HRA10_WorkingOvertime_Action qqdbt --- " + qqdbt);

					jiabandetail.appendRow();
					jiabandetail.setRow(itempbc);
					jiabandetail.setValue(pernr,"PERNR");
					jiabandetail.setValue(qqdbd, "BEGDA");
					jiabandetail.setValue(qqdbt, "FLAG");
					itempbc++;
				}

				client.execute(function);
				JCO.Table table = function.getTableParameterList().getTable("RETURN");

				this.writeLog("HRA10_WorkingOvertime_Action 推送结果 --- " + table.getNumRows());

				boolean flag = false;
				String resultArry = "";
				String messageArry = "";
				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));

					this.writeLog("HRA10_WorkingOvertime_Action result --- " + result + " message --- " + message);

					if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}

				this.writeLog("HRA10_WorkingOvertime_Action result --- " + resultArry);
				this.writeLog("HRA10_WorkingOvertime_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + messageArry);
					return isSuccess;
				}

				this.writeLog("HRA10_WorkingOvertime_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA10_WorkingOvertime_Action Exception:" + e);
				request.getRequestManager().setMessageid("10000");
				request.getRequestManager().setMessagecontent("返回错误："+e);
				return isSuccess;
			} finally {
				try {
					// client.disconnect();
					connect.disConnection();
					client = null;
				} catch (Exception e) {
					e.printStackTrace();
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("返回错误：与SAP连接异常，请重新提交流程！"+e);
					return isSuccess;
				}
			}
		}
		return isSuccess;
	}

}
