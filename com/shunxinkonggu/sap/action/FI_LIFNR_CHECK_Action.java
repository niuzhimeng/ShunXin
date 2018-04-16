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
 * ��Ӧ��������  CHECK
 * 
 * @author ����
 * 
 */
public class FI_LIFNR_CHECK_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("FI_LIFNR_CHECK_Action start --- ");
		
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();     
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("FI_LIFNR_CHECK_Action ��Ӧ�������� CHECK requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String SAKNR = "";            // ��Ӧ������
			String KTOKK = ""; 		      // �˻���
			
			SapConnectPool connect = null;
			JCO.Client client = null;

			try {
				
				this.writeLog("FI_LIFNR_CHECK_Action fromTable --- " + fromTable);
				
				RecordSet rs = rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {
					SAKNR = Util.null2String(rs.getString("gysqc"));
					KTOKK = Util.null2String(rs.getString("accountgroup"));
					this.writeLog("FI_LIFNR_CHECK_Action SAKNR --- " + SAKNR);
					this.writeLog("FI_LIFNR_CHECK_Action KTOKK --- " + KTOKK);
				}
				
				if(KTOKK.equals("V001") || KTOKK.equals("V002")){
					
					JCO.Function function = null;
					JCO.Repository repository = null;
					IFunctionTemplate ft = null;
					connect = new SapConnectPool();
					client = connect.getConnection();
					repository = new JCO.Repository("sap", client);
					ft = repository.getFunctionTemplate("ZRFC_FI_LIFNR_CHECK");
					function = new JCO.Function(ft);
					
					JCO.Structure row = function.getImportParameterList().getStructure("I_LFA1");
					row.setValue(KTOKK,"KTOKK");
					row.setValue(SAKNR,"NAME1");
					//function.getImportParameterList().setValue(SAKNR, "I_NAME1");   
					
					client.execute(function);
					
					String result = Util.null2String(function.getExportParameterList().getValue("E_MSGTY"));
					String message = Util.null2String(function.getExportParameterList().getValue("E_MSGTX"));
		
					if ("E".equals(result) || "A".equals(result)) {// �������E��A ��ʧ��
						request.getRequestManager().setMessageid("10000");
						request.getRequestManager().setMessagecontent("sap���ش���--- " + message);
						return isSuccess;
					}
				}
	
				this.writeLog("FI_LIFNR_CHECK_Action end --- ");
			} catch (Exception e) {
				this.writeLog("FI_LIFNR_CHECK_Action Exception:" + e);
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
