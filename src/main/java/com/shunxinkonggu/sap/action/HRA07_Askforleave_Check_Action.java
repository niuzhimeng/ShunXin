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
 * 员工请假
 * 
 * @author 刘晔
 * 
 */
public class HRA07_Askforleave_Check_Action extends BaseBean implements Action {

	public String execute(RequestInfo request) {

		this.writeLog("HRA07_Askforleave_Check_Action start --- ");
		
		String startendnodeids = "1015,1022";
		String isSuccess = BaseAction.SUCCESS;
		String requestid = request.getRequestid();
		String workflowid = request.getWorkflowid();
		//获取操作类型   save(保存) reject(退回) submit(提交) delete(删除) 
		String operatetype = request.getRequestManager().getSrc();     
		String fromTable = request.getRequestManager().getBillTableName();
		
		this.writeLog("HRA07_Askforleave_Check_Action 员工请假检查 requestid --- "+requestid+"  operatetype --- "+operatetype+"   fromTable --- "+fromTable);
		
		if(operatetype.equals("submit")){
			
			String pernr = ""; // 人员编码
			RecordSet rs = null;
			
			try {
				
				this.writeLog("HRA07_Askforleave_Check_Action fromTable --- " + fromTable);
				
				rs = new RecordSet();
				rs.execute("select * from " + fromTable + " where requestid = " + requestid);
				if (rs.next()) {
					
					pernr = Util.null2String(rs.getString("yggh"));
					this.writeLog("HRA07_Askforleave_Check_Action pernr --- " + pernr);
					
					//判断是否有正在审批中的年假流程 
					if(getWorkflowDataIsExit(workflowid,startendnodeids,"yggh",pernr,"qjlxbm","'2000','2001','2002'")){
						request.getRequestManager().setMessageid("10000");
						request.getRequestManager().setMessagecontent("返回错误：您有未审批结束的年假申请单，不能发起新的年假申请！");
						return isSuccess;
					}
					
				}
				this.writeLog("HRA07_Askforleave_Check_Action end --- ");

			} catch (Exception e) {
				this.writeLog("HRA07_Askforleave_Check_Action Exception:" + e);
				request.getRequestManager().setMessageid("10000");
				request.getRequestManager().setMessagecontent("返回错误："+e);
				return isSuccess;
			}
		}
		return isSuccess;
	}

	/***
	 * 获取流程是否已经存在，除去节点以外的数据
	 * @param tablename 表名
	 * @param code	编码
	 * @return
	 */
	//根据表名和 value值  和 key值（不同value对应不同key值），返回查出的  id（该表id）tablename
	public static boolean getWorkflowDataIsExit( String workflowid, String nodeids, String fieldname, String fieldvalue, String fieldname1, String fieldvalue1) {
		
		RecordSet rs = null;
		boolean returnValue = false;
		String tablename = "";
		
		try {
			rs = new RecordSet();
			rs.executeSql("select tablename from workflow_bill wbill,workflow_base wbase where wbill.id = wbase.formid and wbase.id = "+workflowid);
			if (rs.next()) {
				tablename = Util.null2String(rs.getString("tablename"));
			}
			if(tablename.length() > 0){
				rs.executeSql("select id from "+tablename+" where requestid not in (select requestid from workflow_requestbase where workflowid = "+workflowid+" and currentnodeid in ("+nodeids+")) and "+fieldname+" = '"+fieldvalue+"' and "+fieldname1+" in ("+fieldvalue1+") ");  
				if (rs.next()) {
					//tablename = Util.null2String(rs.getString("tablename"));
					returnValue = true;
				}
				else{
					returnValue = false;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
}
