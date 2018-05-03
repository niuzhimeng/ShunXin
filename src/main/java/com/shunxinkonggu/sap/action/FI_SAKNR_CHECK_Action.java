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
 * 会计科目(集团层)  CHECK 科目编码
 *
 * @author 刘晔
 *
 */
public class FI_SAKNR_CHECK_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("FI_SAKNR_CHECK_Action start --- ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("FI_SAKNR_CHECK_Action 会计科目(集团层)  CHECK 科目编码 requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {

            String SAKNR = "";            // 会计科目编码
            String KTOKS = "";          // 科目组

            RecordSet rs = new RecordSet();
            SapConnectPool connect = null;
            JCO.Client client = null;

            try {

                this.writeLog("FI_SAKNR_CHECK_Action fromTable --- " + fromTable);

                JCO.Function function = null;
                JCO.Repository repository = null;
                connect = new SapConnectPool();
                client = connect.getConnection();
                repository = new JCO.Repository("sap", client);
                IFunctionTemplate ft  = repository.getFunctionTemplate("ZRFC_FI_SAKNR_CHECK_B");
                function = new JCO.Function(ft);

                writeLog("执行sql=============================");
                rs.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);
                JCO.Table table = function.getImportParameterList().getTable("IT_SAKNR");
                int i = 0;
                writeLog("开始执行while=======================");
                while (rs.next()) {
                    SAKNR = Util.null2String(rs.getString("kjkmbm"));
                    KTOKS = Util.null2String(rs.getString("kmz"));

                    table.appendRow();
                    table.setRow(i);
                    table.setValue(SAKNR, "SAKNR");
                    i++;

                    this.writeLog("FI_SAKNR_CHECK_Action SAKNR --- " + SAKNR);
                    this.writeLog("FI_SAKNR_CHECK_Action KTOKS --- " + KTOKS);
                }
                writeLog("调用sqp中----");
                client.execute(function);
                writeLog("调用结束----");

                //处理返回数据
                JCO.Table resultTable = function.getExportParameterList().getTable("ET_SAKNR");
                int length = resultTable.getNumRows();
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    resultTable.setRow(j);
                    // 如果包含E或A 则失败
                    if ("E".equals(resultTable.getString("MSGTY")) || "A".equals(resultTable.getString("MSGTY"))) {
                        builder.append(resultTable.getString("SAKNR")).append("： ").append(resultTable.getString("MSGTX")).append("</br>");
                    }
                }

                //说明有表中有N行错误
                if (builder.length() > 0) {
                    request.getRequestManager().setMessageid("10000");
                    request.getRequestManager().setMessagecontent("sap返回消息：--- " + builder.toString());
                }
                this.writeLog("FI_SAKNR_CHECK_Action end --- ");
                return isSuccess;
            } catch (Exception e) {
                this.writeLog("FI_SAKNR_CHECK_Action Exception:" + e);
                request.getRequestManager().setMessageid("10000");
                request.getRequestManager().setMessagecontent("返回错误：" + e);
                return isSuccess;
            } finally {
                try {
                    // client.disconnect();
                    connect.disConnection();
                    client = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    request.getRequestManager().setMessageid("10000");
                    request.getRequestManager().setMessagecontent("返回错误：与SAP连接异常，请重新提交流程！" + e);
                    return isSuccess;
                }
            }
        }
        return isSuccess;
    }

}
