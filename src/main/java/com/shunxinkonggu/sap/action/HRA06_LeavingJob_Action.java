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
 * 员工离职
 *
 * @author 刘晔
 *
 */
public class HRA06_LeavingJob_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA06_LeavingJob_Action start --- ");

		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();
		String fromTable = request.getRequestManager().getBillTableName();

		this.writeLog("HRA06_LeavingJob_Action 员工离职 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);

		if(operatetype.equals("submit")){

			String I_PERNR = "";   // 人员编码
			String I_BEGDA = "";   // 核定离职日期
			String I_PERSG = "";   // 员工组
			String I_PERSK = "";   // 员工子组
			String I_ZHTZDLX = ""; // 合同中断类型
			String I_ZHTJCYY = ""; // 合同解除原因
			String I_MASSG = "";   // 离职原因

			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {

				this.writeLog("HRA06_LeavingJob_Action fromTable --- " + fromTable);

				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_HR_HRA06_UPDATE");
				function = new JCO.Function(ft);

				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {

					I_PERNR = Util.null2String(rs.getString("gh"));
					I_BEGDA = Util.null2String(rs.getString("hdlzrq"));
					I_PERSG = Util.null2String(rs.getString("ygzbm"));
					I_PERSK = Util.null2String(rs.getString("ygzzbm"));
					I_ZHTZDLX = Util.null2String(rs.getString("htzdlxbm"));
					I_ZHTJCYY = Util.null2String(rs.getString("htjcyybm"));
					I_MASSG = Util.null2String(rs.getString("lzyybm"));

					I_BEGDA = I_BEGDA.replace("-", "");

					this.writeLog("HRA06_LeavingJob_Action I_PERNR --- " + I_PERNR);
					this.writeLog("HRA06_LeavingJob_Action I_BEGDA --- " + I_BEGDA);
					this.writeLog("HRA06_LeavingJob_Action I_PERSG --- " + I_PERSG);
					this.writeLog("HRA06_LeavingJob_Action I_PERSK --- " + I_PERSK);
					this.writeLog("HRA06_LeavingJob_Action I_ZHTZDLX --- " + I_ZHTZDLX);
					this.writeLog("HRA06_LeavingJob_Action I_ZHTJCYY --- " + I_ZHTJCYY);
					this.writeLog("HRA06_LeavingJob_Action I_MASSG --- " + I_MASSG);

				}

				function.getImportParameterList().setValue(I_PERNR, "I_PERNR");
				function.getImportParameterList().setValue(I_BEGDA, "I_BEGDA");
				function.getImportParameterList().setValue(I_PERSG, "I_PERSG");
				function.getImportParameterList().setValue(I_PERSK, "I_PERSK");
				function.getImportParameterList().setValue(I_ZHTZDLX, "I_ZHTZDLX");
				function.getImportParameterList().setValue(I_ZHTJCYY, "I_ZHTJCYY");
				function.getImportParameterList().setValue(I_MASSG, "I_MASSG");

				client.execute(function);
				JCO.Table table = function.getTableParameterList().getTable("RETURN");

				this.writeLog("HRA06_LeavingJob_Action 推送结果 --- " + table.getNumRows());

				boolean flag = false;
				String resultArry = "";
				String messageArry = "";
				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));

					this.writeLog("HRA06_LeavingJob_Action result --- " + result + " message --- " + message);

					if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}

				this.writeLog("HRA06_LeavingJob_Action result --- " + resultArry);
				this.writeLog("HRA06_LeavingJob_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + messageArry);
					return isSuccess;
				}

				this.writeLog("HRA06_LeavingJob_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA06_LeavingJob_Action Exception:" + e);
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
