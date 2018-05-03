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
 * 员工调动
 *
 * @author 刘晔
 *
 */
public class HRA05_Reassignment_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA05_Reassignment_Action start --- ");

		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();
		String fromTable = request.getRequestManager().getBillTableName();

		this.writeLog("HRA05_Reassignment_Action 员工调动 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);

		if(operatetype.equals("submit")){

			String I_PERNR = "";   // 人员编码
			String I_BEGDA = "";   // 调动生效日期
			String I_BEGDA1 = "";  // 工资核算日期
			String I_OBJID1 = "";  // 调出部门ID
			String I_STEXT1 = "";  // 调出部门文本
			String I_OBJID2 = "";  // 调出单位ID
			String I_STEXT2 = "";  // 调出单位文本
			String I_OBJID3 = "";  // 调入部门ID
			String I_STEXT3 = "";  // 调入部门文本
			String I_OBJID4 = "";  // 调入单位ID
			String I_STEXT4 = "";  // 调入单位文本
			String I_PLANS1 = "";  // 原岗位ID
			String I_STEXT5 = "";  // 原岗位文本
			String I_PLANS2 = "";  // 现岗位ID
			String I_STEXT6 = "";  // 现岗位文本
			String I_PERSG = "";   // 员工组
			String I_PERSK = "";   // 员工子组：01，02
			String I_MASSG = "";   // 调动原因（ 01，02，03, 04）

			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {

				this.writeLog("HRA05_Reassignment_Action fromTable --- " + fromTable);

				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_HR_HRA05_UPDATE");
				function = new JCO.Function(ft);

				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {

					I_PERNR = Util.null2String(rs.getString("ddrybh"));
					I_BEGDA = Util.null2String(rs.getString("ddrq"));
					I_BEGDA1 = Util.null2String(rs.getString("gzhsrq"));
					I_OBJID1 = Util.null2String(rs.getString("dcbmid"));
					I_STEXT1 = Util.null2String(rs.getString("dcbmwb"));
					I_OBJID2 = Util.null2String(rs.getString("dcdwid"));
					I_STEXT2 = Util.null2String(rs.getString("dcdwwb"));
					I_OBJID3 = Util.null2String(rs.getString("drbmid"));
					I_STEXT3 = Util.null2String(rs.getString("drbmwb"));
					I_OBJID4 = Util.null2String(rs.getString("drdwid"));
					I_STEXT4 = Util.null2String(rs.getString("drdwwb"));
					I_PLANS1 = Util.null2String(rs.getString("ygwid"));
					I_STEXT5 = Util.null2String(rs.getString("ygwwb"));
					I_PLANS2 = Util.null2String(rs.getString("xgwid"));
					I_STEXT6 = Util.null2String(rs.getString("xgwwb"));
					I_PERSG = Util.null2String(rs.getString("ygzbm"));
					I_PERSK = Util.null2String(rs.getString("ygzzbm"));
					I_MASSG = Util.null2String(rs.getString("ddyybm"));

					I_BEGDA = I_BEGDA.replace("-", "");
					I_BEGDA1 = I_BEGDA1.replace("-", "");

					this.writeLog("HRA05_Reassignment_Action I_PERNR --- " + I_PERNR);
					this.writeLog("HRA05_Reassignment_Action I_BEGDA --- " + I_BEGDA);
					this.writeLog("HRA05_Reassignment_Action I_BEGDA1 --- " + I_BEGDA1);
					this.writeLog("HRA05_Reassignment_Action I_OBJID1 --- " + I_OBJID1);
					this.writeLog("HRA05_Reassignment_Action I_STEXT1 --- " + I_STEXT1);
					this.writeLog("HRA05_Reassignment_Action I_OBJID2 --- " + I_OBJID2);
					this.writeLog("HRA05_Reassignment_Action I_STEXT2 --- " + I_STEXT2);
					this.writeLog("HRA05_Reassignment_Action I_OBJID3 --- " + I_OBJID3);
					this.writeLog("HRA05_Reassignment_Action I_STEXT3 --- " + I_STEXT3);
					this.writeLog("HRA05_Reassignment_Action I_OBJID4 --- " + I_OBJID4);
					this.writeLog("HRA05_Reassignment_Action I_STEXT4 --- " + I_STEXT4);
					this.writeLog("HRA05_Reassignment_Action I_PLANS1 --- " + I_PLANS1);
					this.writeLog("HRA05_Reassignment_Action I_STEXT5 --- " + I_STEXT5);
					this.writeLog("HRA05_Reassignment_Action I_PLANS2 --- " + I_PLANS2);
					this.writeLog("HRA05_Reassignment_Action I_STEXT6 --- " + I_STEXT6);
					this.writeLog("HRA05_Reassignment_Action I_PERSG --- " + I_PERSG);
					this.writeLog("HRA05_Reassignment_Action I_PERSK --- " + I_PERSK);

					this.writeLog("HRA05_Reassignment_Action I_MASSG --- " + I_MASSG);

				}

				function.getImportParameterList().setValue(I_PERNR, "I_PERNR");
				function.getImportParameterList().setValue(I_BEGDA, "I_BEGDA");
				function.getImportParameterList().setValue(I_OBJID1, "I_OBJID1");
				function.getImportParameterList().setValue(I_STEXT1, "I_STEXT1");
				function.getImportParameterList().setValue(I_OBJID2, "I_OBJID2");
				function.getImportParameterList().setValue(I_STEXT2, "I_STEXT2");
				function.getImportParameterList().setValue(I_OBJID3, "I_OBJID3");
				function.getImportParameterList().setValue(I_STEXT3, "I_STEXT3");
				function.getImportParameterList().setValue(I_OBJID4, "I_OBJID4");
				function.getImportParameterList().setValue(I_STEXT4, "I_STEXT4");
				function.getImportParameterList().setValue(I_PLANS1, "I_PLANS1");
				function.getImportParameterList().setValue(I_STEXT5, "I_STEXT5");
				function.getImportParameterList().setValue(I_PLANS2, "I_PLANS2");
				function.getImportParameterList().setValue(I_STEXT6, "I_STEXT6");
				function.getImportParameterList().setValue(I_PERSG, "I_PERSG");
				function.getImportParameterList().setValue(I_PERSK, "I_PERSK");
				function.getImportParameterList().setValue(I_BEGDA1, "I_BEGDA1");
				function.getImportParameterList().setValue(I_MASSG, "I_MASSG");

				client.execute(function);
				JCO.Table table = function.getTableParameterList().getTable("RETURN");

				this.writeLog("HRA05_Reassignment_Action 推送结果 --- " + table.getNumRows());

				boolean flag = false;
				String resultArry = "";
				String messageArry = "";
				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));

					this.writeLog("HRA05_Reassignment_Action result --- " + result + " message --- " + message);

					if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}

				this.writeLog("HRA05_Reassignment_Action result --- " + resultArry);
				this.writeLog("HRA05_Reassignment_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + messageArry);
					return isSuccess;
				}

				this.writeLog("HRA05_Reassignment_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA05_Reassignment_Action Exception:" + e);
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
