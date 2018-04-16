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
 * �ͻ�������
 * 
 * @author ����
 * 
 */
public class FI_KUNNR_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("FI_KUNNR_Action start --- ");
		
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();     
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("FI_KUNNR_Action �ͻ������� requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String KUNNR = "";            // �ͻ�����
			String LAND1 = "";   		  // ���Ҵ���
			String TITLE_MEDI = "";   	  // ��ν
			String KTOKD = ""; 			  // �˻���
			String NAME1 = ""; 			  // �ͻ�ȫ��
			String SORT1 = ""; 			  // �ͻ����
			
			String STRAS = ""; 			  // ��ַ�ֵ�
			
			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {
				
				this.writeLog("FI_KUNNR_Action fromTable --- " + fromTable);
	
				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_FI_KUNNR_CREATE");
				function = new JCO.Function(ft);
				
				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {
					
					KUNNR = Util.null2String(rs.getString("khbm"));
					LAND1 = Util.null2String(rs.getString("gj"));
					TITLE_MEDI = Util.null2String(rs.getString("applletion"));
					KTOKD = Util.null2String(rs.getString("accountgroup"));
					NAME1 = Util.null2String(rs.getString("khqc"));
					SORT1 = Util.null2String(rs.getString("khjc"));
					STRAS = Util.null2String("");
	
					this.writeLog("FI_KUNNR_Action KUNNR --- " + KUNNR);
					this.writeLog("FI_KUNNR_Action LAND1 --- " + LAND1);
					this.writeLog("FI_KUNNR_Action TITLE_MEDI --- " + TITLE_MEDI);
					this.writeLog("FI_KUNNR_Action KTOKD --- " + KTOKD);
					this.writeLog("FI_KUNNR_Action NAME1 --- " + NAME1);
					this.writeLog("FI_KUNNR_Action SORT1 --- " + SORT1);
					this.writeLog("FI_KUNNR_Action STRAS --- " + STRAS);
					
				}
				
				JCO.Structure row = function.getImportParameterList().getStructure("I_KNA1");
				row.setValue(KUNNR,"KUNNR");
				row.setValue(LAND1,"LAND1");
				row.setValue(TITLE_MEDI,"TITLE_MEDI");
				row.setValue(KTOKD,"KTOKD");
				row.setValue(NAME1,"NAME1");
				row.setValue(SORT1,"SORT1");
				row.setValue(STRAS,"STRAS");
				
				client.execute(function);
				
				String result = Util.null2String(function.getExportParameterList().getValue("E_MSGTY"));
				String message = Util.null2String(function.getExportParameterList().getValue("E_MSGTX"));
	
				if ("E".equals(result) || "A".equals(result)) {// �������E��A ��ʧ��
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap���ش�����Ϣ����---" + message);
					return isSuccess;
				}
	
				this.writeLog("FI_KUNNR_Action end --- ");
			} catch (Exception e) {
				this.writeLog("FI_KUNNR_Action Exception:" + e);
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
