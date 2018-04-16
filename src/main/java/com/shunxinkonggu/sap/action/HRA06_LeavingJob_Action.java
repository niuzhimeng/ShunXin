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
 * Ա����ְ
 * 
 * @author ����
 * 
 */
public class HRA06_LeavingJob_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA06_LeavingJob_Action start --- ");
		
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();     
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("HRA06_LeavingJob_Action Ա����ְ requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String I_PERNR = "";   // ��Ա����
			String I_BEGDA = "";   // �˶���ְ����
			String I_PERSG = "";   // Ա����
			String I_PERSK = "";   // Ա������
			String I_ZHTZDLX = ""; // ��ͬ�ж�����
			String I_ZHTJCYY = ""; // ��ͬ���ԭ��
			String I_MASSG = "";   // ��ְԭ��
			
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
				
				this.writeLog("HRA06_LeavingJob_Action ���ͽ�� --- " + table.getNumRows());
				
				boolean flag = false;
				String resultArry = "";
				String messageArry = "";
				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));
					
					this.writeLog("HRA06_LeavingJob_Action result --- " + result + " message --- " + message);
					
					if ("E".equals(result) || "A".equals(result)) {// �������E��A ��ʧ��
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}
	
				this.writeLog("HRA06_LeavingJob_Action result --- " + resultArry);
				this.writeLog("HRA06_LeavingJob_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap���ش�����Ϣ����---" + messageArry);
					return isSuccess;
				}
	
				this.writeLog("HRA06_LeavingJob_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA06_LeavingJob_Action Exception:" + e);
				request.getRequestManager().setMessageid("10000");
				request.getRequestManager().setMessagecontent("���ش���"+e);
				return isSuccess;
			} finally {
				try {
					// client.disconnect();
					connect.disConnection();
					client = null;
				} catch (Exception e) {
					e.printStackTrace();
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("���ش�����SAP�����쳣���������ύ���̣�"+e);
					return isSuccess;
				}
			}
		}
		return isSuccess;
	}
	
}
