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
 * Ա������
 * 
 * @author ����
 * 
 */
public class HRA08_ReturnFromLeave_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA08_ReturnFromLeave_Action start --- ");
		
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("HRA08_ReturnFromLeave_Action Ա������ requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String pernr = ""; // ��Ա����
			String qqdlx = ""; // �������ͱ���
			String qqdbd = ""; // ��ʼ����
			String qqded = ""; // ��������
			String qqdbt = ""; // ��ʼʱ��
			String qqdet = ""; // ����ʱ��
			
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
				
				this.writeLog("HRA08_ReturnFromLeave_Action ���ͽ�� --- " + table.getNumRows());
				
				boolean flag = false;
				String resultArry = "";
				String messageArry = "";
				for (int i = 0; i < table.getNumRows(); i++) {
					table.setRow(i);
					String result = Util.null2String(table.getString("TYPE"));
					String message = Util.null2String(table.getString("MESSAGE"));
					
					this.writeLog("HRA08_ReturnFromLeave_Action result --- " + result + " message --- " + message);
					
					if ("E".equals(result) || "A".equals(result)) {// �������E��A ��ʧ��
						flag = true;
						messageArry += ";  " + message;
						resultArry += ";  " + result;
					}
				}
	
				this.writeLog("HRA08_ReturnFromLeave_Action result --- " + resultArry);
				this.writeLog("HRA08_ReturnFromLeave_Action message --- " + messageArry);
				if (flag) {
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap���ش�����Ϣ����---" + messageArry);
					return isSuccess;
				}
	
				this.writeLog("HRA08_ReturnFromLeave_Action end --- ");
			} catch (Exception e) {
				this.writeLog("HRA08_ReturnFromLeave_Action Exception:" + e);
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

	private String changeQjlx(String yuanID) {

//		String resultID = "";
//		if ("0".equals(yuanID)) {
//			resultID = "A001";// ���
//		} else if ("1".equals(yuanID)) {
//			resultID = "A002";// ̽�׼�
//		}
		return yuanID;

	}
	
}