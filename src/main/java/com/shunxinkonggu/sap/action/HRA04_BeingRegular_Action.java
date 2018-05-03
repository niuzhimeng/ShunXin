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
 * 员工转正
 *
 * @author 刘晔
 *
 */
public class HRA04_BeingRegular_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA04_BeingRegular_Action start --- ");

		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();
		String fromTable = request.getRequestManager().getBillTableName();

		this.writeLog("HRA04_BeingRegular_Action 员工转正 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);

		if(operatetype.equals("submit")){

			String pernr = "";  // 人员编码
			String zzyy = "";   // 转正原因
			String ygzz = "";   // 员工子组
			String sqzzrq = ""; // 申请转正日期

			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {

				this.writeLog("HRA04_BeingRegular_Action fromTable --- " + fromTable);

				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_HR_HRA04_UPDATE");
				function = new JCO.Function(ft);

				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {

					pernr = Util.null2String(rs.getString("sqrbm"));
					zzyy = Util.null2String(rs.getString("zzyybm"));
					ygzz = Util.null2String(rs.getString("ygzzbm"));
					sqzzrq = Util.null2String(rs.getString("zzrq"));

					sqzzrq = sqzzrq.replace("-", "");

					this.writeLog("HRA04_BeingRegular_Action pernr --- " + pernr);
					this.writeLog("HRA04_BeingRegular_Action zzyy --- " + zzyy);
					this.writeLog("HRA04_BeingRegular_Action ygzz --- " + ygzz);
					this.writeLog("HRA04_BeingRegular_Action sqzzrq --- " + sqzzrq);

				}

				function.getImportParameterList().setValue(pernr, "I_PERNR");
				function.getImportParameterList().setValue(zzyy, "I_MASSG");
				function.getImportParameterList().setValue(ygzz, "I_PERSK");
				function.getImportParameterList().setValue(sqzzrq, "I_BEGDA");

				client.execute(function);
				JCO.Table table = function.getTableParameterList().getTable("RETURN");

				boolean flag = false;
				String resultArry = "";
				String messageArry = "";

				this.writeLog("HRA04_BeingRegular_Action 推送结果 --- " + table.getNumRows());

				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));

					this.writeLog("HRA04_BeingRegular_Action result --- " + result + " message --- " + message);

					if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}

				this.writeLog("HRA04_BeingRegular_Action result --- " + resultArry);
				this.writeLog("HRA04_BeingRegular_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + messageArry);
					return isSuccess;
				}

				this.writeLog("HRA04_BeingRegular_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA04_BeingRegular_Action Exception:" + e);
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
