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
 * 供应商主数据
 * 
 * @author 刘晔
 * 
 */
public class FI_LIFNR_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("FI_LIFNR_Action start --- ");
		
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String operatetype = request.getRequestManager().getSrc();     
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("FI_LIFNR_Action 供应商主数据 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String LIFNR = "";        // 供应商编码
			String LAND1 = "";        // 国家代码
			String TITLE_MEDI = "";   // 称谓
			String KTOKK = ""; 		  // 账户组
			String NAME1 = ""; 	      // 供应商全称
			String SORT1 = ""; 		  // 供应商简称
			
			String STRAS = "";        // 地址街道
			String TELF1 = "";        // 电话
			String TELFX = "";        // 传真
			String STCEG = "";        // 增值税登记号

			
			RecordSet rs = null;
			SapConnectPool connect = null;
			JCO.Client client = null;
			JCO.Function function = null;
			JCO.Repository repository = null;
			IFunctionTemplate ft = null;
			try {
				
				this.writeLog("FI_LIFNR_Action fromTable --- " + fromTable);
	
				connect = new SapConnectPool();
				client = connect.getConnection();
				repository = new JCO.Repository("sap", client);
				ft = repository.getFunctionTemplate("ZRFC_FI_LIFNR_CREATE");
				function = new JCO.Function(ft);
				
				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {
					
					LIFNR = Util.null2String(rs.getString("gysbm"));
					LAND1 = Util.null2String(rs.getString("gjdm"));
					TITLE_MEDI = Util.null2String(rs.getString("applletion"));
					KTOKK = Util.null2String(rs.getString("accountgroup"));
					NAME1 = Util.null2String(rs.getString("gysqc"));
					SORT1 = Util.null2String(rs.getString("gysjc"));
					
					STRAS = Util.null2String("");
					TELF1 = Util.null2String("");
					TELFX = Util.null2String("");
					STCEG = Util.null2String("");
	
					this.writeLog("FI_LIFNR_Action LIFNR --- " + LIFNR);
					this.writeLog("FI_LIFNR_Action LAND1 --- " + LAND1);
					this.writeLog("FI_LIFNR_Action TITLE_MEDI --- " + TITLE_MEDI);
					this.writeLog("FI_LIFNR_Action KTOKK --- " + KTOKK);
					this.writeLog("FI_LIFNR_Action NAME1 --- " + NAME1);
					this.writeLog("FI_LIFNR_Action SORT1 --- " + SORT1);
					
					this.writeLog("FI_LIFNR_Action STRAS --- " + STRAS);
					this.writeLog("FI_LIFNR_Action TELF1 --- " + TELF1);
					this.writeLog("FI_LIFNR_Action TELFX --- " + TELFX);
					this.writeLog("FI_LIFNR_Action STCEG --- " + STCEG);
					
					
				}
				
				JCO.Structure row = function.getImportParameterList().getStructure("I_LFA1");
				row.setValue(LIFNR,"LIFNR");
				row.setValue(LAND1,"LAND1");
				row.setValue(TITLE_MEDI,"TITLE_MEDI");
				row.setValue(KTOKK,"KTOKK");
				row.setValue(NAME1,"NAME1");
				row.setValue(SORT1,"SORT1");
				
				row.setValue(STRAS,"STRAS");
				row.setValue(TELF1,"TELF1");
				row.setValue(TELFX,"TELFX");
				row.setValue(STCEG,"STCEG");
				
				client.execute(function);
				
				String result = Util.null2String(function.getExportParameterList().getValue("E_MSGTY"));
				String message = Util.null2String(function.getExportParameterList().getValue("E_MSGTX"));
	
				if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
					request.getRequestManager().setMessageid("10000");
					request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + message);
					return isSuccess;
				}
	
				this.writeLog("FI_LIFNR_Action end --- ");
			} catch (Exception e) {
				this.writeLog("FI_LIFNR_Action Exception:" + e);
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
