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
 * ��ƿ�Ŀ(���Ų�)
 * 
 * @author ����
 * 
 */
public class FI_SAKNR_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("FI_SAKNR_Action start --- ");
		
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();     
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("FI_SAKNR_Action ��ƿ�Ŀ(���Ų�) requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String SAKNR = "";            // ��ƿ�Ŀ����
			String GLACCOUNT_TYPE = "";   // ��Ŀ����
			String KTOKS = "";   		  // ��Ŀ��
			String TXT50 = ""; 			  // ���˿�Ŀ���ı�
			String TXT20 = ""; 			  // ���˿�Ŀ���ı�
			String GVTYP = ""; 			  // ������Ŀ����
			String KTOPL = ""; 			  // ��Ŀ��
			
			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {
				
				this.writeLog("FI_SAKNR_Action fromTable --- " + fromTable);
	
				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_FI_SAKNR_CREATE");
				function = new JCO.Function(ft);
				
				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {
					
					SAKNR = Util.null2String(rs.getString("kjkmbm"));
					GLACCOUNT_TYPE = Util.null2String(rs.getString("Accounttype"));
					KTOKS = Util.null2String(rs.getString("kmz"));
					TXT50 = Util.null2String(rs.getString("zzkmcwb"));
					TXT20 = Util.null2String(rs.getString("zzkmdwb"));
					GVTYP = Util.null2String(rs.getString("sybkmlx"));
					KTOPL = Util.null2String(rs.getString("zmb"));
	
					this.writeLog("FI_SAKNR_Action SAKNR --- " + SAKNR);
					this.writeLog("FI_SAKNR_Action GLACCOUNT_TYPE --- " + GLACCOUNT_TYPE);
					this.writeLog("FI_SAKNR_Action KTOKS --- " + KTOKS);
					this.writeLog("FI_SAKNR_Action TXT50 --- " + TXT50);
					this.writeLog("FI_SAKNR_Action TXT20 --- " + TXT20);
					this.writeLog("FI_SAKNR_Action GVTYP --- " + GVTYP);
					this.writeLog("FI_SAKNR_Action KTOPL --- " + KTOPL);
					
				}
				
				JCO.Structure row = function.getImportParameterList().getStructure("I_SKAT");
				row.setValue(SAKNR,"SAKNR");
				row.setValue(GLACCOUNT_TYPE,"GLACCOUNT_TYPE");
				row.setValue(KTOKS,"KTOKS");
				row.setValue(TXT50,"TXT50");
				row.setValue(TXT20,"TXT20");
				row.setValue(GVTYP,"GVTYP");
				row.setValue(KTOPL,"KTOPL");
				
				client.execute(function);
				
				String result = Util.null2String(function.getExportParameterList().getValue("E_MSGTY"));
				String message = Util.null2String(function.getExportParameterList().getValue("E_MSGTX"));
	
				if ("E".equals(result) || "A".equals(result)) {// �������E��A ��ʧ��
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap���ش�����Ϣ����---" + message);
					return isSuccess;
				}
	
				this.writeLog("FI_SAKNR_Action end --- ");
			} catch (Exception e) {
				this.writeLog("FI_SAKNR_Action Exception:" + e);
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
