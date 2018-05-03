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
 * 员工销假
 *
 * @author 刘晔
 *
 */
public class HRA08_ReturnFromLeave_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA08_ReturnFromLeave_Action start --- ");

		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();
		String fromTable = request.getRequestManager().getBillTableName();

		this.writeLog("HRA08_ReturnFromLeave_Action 员工销假 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);

		if(operatetype.equals("submit")){

			String pernr = ""; // 人员编码
			String qqdlx = ""; // 销假类型编码
			String qqdbd = ""; // 开始日期
			String qqded = ""; // 结束日期
			String qqdbt = ""; // 开始时间
			String qqdet = ""; // 结束时间

			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {

				this.writeLog("HRA08_ReturnFromLeave_Action fromTable --- " + fromTable);

				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_HR_HRA08_UPDATE");
				function = new JCO.Function(ft);

				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {

					pernr = Util.null2String(rs.getString("yggh"));
					qqdlx = changeQjlx(Util.null2String(rs.getString("xjlxbm")));
					qqdbd = Util.null2String(rs.getString("qjrq"));
					qqded = Util.null2String(rs.getString("xjrq"));
					qqdbt = Util.null2String(rs.getString("jtsj1"));
					qqdet = Util.null2String(rs.getString("jtsj2"));

					qqdbd = qqdbd.replace("-", "");
					qqded = qqded.replace("-", "");

					this.writeLog("HRA08_ReturnFromLeave_Action pernr --- " + pernr);
					this.writeLog("HRA08_ReturnFromLeave_Action qqdlx --- " + qqdlx);
					this.writeLog("HRA08_ReturnFromLeave_Action qqdbd --- " + qqdbd);
					this.writeLog("HRA08_ReturnFromLeave_Action qqded --- " + qqded);
					this.writeLog("HRA08_ReturnFromLeave_Action qqdbt --- " + qqdbt);
					this.writeLog("HRA08_ReturnFromLeave_Action qqdet --- " + qqdet);

				}

				function.getImportParameterList().setValue(pernr, "I_PERNR");
				function.getImportParameterList().setValue(qqdlx, "I_AWART");
				function.getImportParameterList().setValue(qqdbd, "I_BEGDA");
				function.getImportParameterList().setValue(qqded, "I_ENDDA");
				function.getImportParameterList().setValue(qqdbt, "I_FLAG1");
				function.getImportParameterList().setValue(qqdet, "I_FLAG2");

				client.execute(function);
				JCO.Table table = function.getTableParameterList().getTable("RETURN");

				this.writeLog("HRA08_ReturnFromLeave_Action 推送结果 --- " + table.getNumRows());

				boolean flag = false;
				String resultArry = "";
				String messageArry = "";
				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));

					this.writeLog("HRA08_ReturnFromLeave_Action result --- " + result + " message --- " + message);

					if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}

				this.writeLog("HRA08_ReturnFromLeave_Action result --- " + resultArry);
				this.writeLog("HRA08_ReturnFromLeave_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + messageArry);
					return isSuccess;
				}

				this.writeLog("HRA08_ReturnFromLeave_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA08_ReturnFromLeave_Action Exception:" + e);
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

	private String changeQjlx(String yuanID) {

//		String resultID = "";
//		if ("0".equals(yuanID)) {
//			resultID = "A001";// 年假
//		} else if ("1".equals(yuanID)) {
//			resultID = "A002";// 探亲假
//		}
		return yuanID;

	}

}
