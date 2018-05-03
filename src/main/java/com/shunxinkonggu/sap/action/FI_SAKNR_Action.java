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
 * 会计科目(集团层)
 *
 * @author 刘晔
 *
 */
public class FI_SAKNR_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("FI_SAKNR_Action start ================================= ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("FI_SAKNR_Action 会计科目(集团层) requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {

            String SAKNR = "";            // 会计科目编码
            String GLACCOUNT_TYPE = "";   // 科目类型
            String KTOKS = "";          // 科目组
            String TXT50 = "";              // 总账科目长文本
            String TXT20 = "";              // 总账科目短文本
            String GVTYP = "";              // 损益表科目类型
            String KTOPL = "";              // 账目表

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
                ft = repository.getFunctionTemplate("ZRFC_FI_SAKNR_CREATE_B");
                function = new JCO.Function(ft);

                rs = new RecordSet();
                rs.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);
                JCO.Table table = function.getImportParameterList().getTable("IT_SKAT");
                int i = 0;
                while (rs.next()) {

                    SAKNR = Util.null2String(rs.getString("kjkmbm"));
                    GLACCOUNT_TYPE = Util.null2String(rs.getString("kmlx"));
                    KTOKS = Util.null2String(rs.getString("kmz"));
                    TXT50 = Util.null2String(rs.getString("zzkmcwb"));
                    TXT20 = Util.null2String(rs.getString("zzkmdwb"));
                    GVTYP = Util.null2String(rs.getString("sybkmlx"));
                    KTOPL = Util.null2String(rs.getString("zmz"));

                    table.appendRow();
                    table.setRow(i);
                    table.setValue(SAKNR, "SAKNR");
                    table.setValue(GLACCOUNT_TYPE, "GLACCOUNT_TYPE");
                    table.setValue(KTOKS, "KTOKS");
                    table.setValue(TXT50, "TXT50");
                    table.setValue(TXT20, "TXT20");
                    table.setValue(GVTYP, "GVTYP");
                    table.setValue(KTOPL, "KTOPL");
                    i++;

                    this.writeLog("FI_SAKNR_Action SAKNR --- " + SAKNR);
                    this.writeLog("FI_SAKNR_Action GLACCOUNT_TYPE --- " + GLACCOUNT_TYPE);
                    this.writeLog("FI_SAKNR_Action KTOKS --- " + KTOKS);
                    this.writeLog("FI_SAKNR_Action TXT50 --- " + TXT50);
                    this.writeLog("FI_SAKNR_Action TXT20 --- " + TXT20);
                    this.writeLog("FI_SAKNR_Action GVTYP --- " + GVTYP);
                    this.writeLog("FI_SAKNR_Action KTOPL --- " + KTOPL);
                }
                writeLog("调用sap中-----");
                client.execute(function);
                writeLog("调用结束-------");

                //处理返回数据
                JCO.Table resultTable = function.getExportParameterList().getTable("ET_SKAT");
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
                this.writeLog("FI_SAKNR_Action end ================================= ");
            } catch (Exception e) {
                this.writeLog("FI_SAKNR_Action Exception:" + e);
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
