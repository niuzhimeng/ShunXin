package com.shunxinkonggu.sap.action;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.shunxinkonggu.sap.util.SapConnectPool;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/***
 * 供应商主数据  CHECK
 *
 * @author 刘晔
 *
 */
public class FI_LIFNR_CHECK_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("FI_LIFNR_CHECK_Action start ================================= ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("FI_LIFNR_CHECK_Action 供应商主数据 CHECK requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {

            String NAME1 = "";            // 供应商名称
            String KTOKK = "";              // 账户组

            RecordSet rs = null;
            SapConnectPool connect = null;
            JCO.Client client = null;
            JCO.Function function = null;
            JCO.Repository repository = null;
            IFunctionTemplate ft = null;

            try {

                this.writeLog("FI_LIFNR_CHECK_Action fromTable --- " + fromTable);
                connect = new SapConnectPool();
                client = connect.getConnection();
                repository = new JCO.Repository("sap", client);
                ft = repository.getFunctionTemplate("ZRFC_FI_LIFNR_CHECK_B");
                function = new JCO.Function(ft);
                rs = new RecordSet();

                rs.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);
                JCO.Table table = function.getImportParameterList().getTable("IT_LIFNR");
                int i = 0;
                while (rs.next()) {
                    if ("V001".equals(rs.getString("accountgroup")) || "V002".equals(rs.getString("accountgroup"))) {
                        NAME1 = Util.null2String(rs.getString("gysqc"));
                        KTOKK = Util.null2String(rs.getString("accountgroup"));

                        table.appendRow();
                        table.setRow(i);
                        table.setValue(NAME1, "NAME1");
                        table.setValue(KTOKK, "KTOKK");
                        this.writeLog("第 " + i + "行");
                        this.writeLog("FI_LIFNR_CHECK_Action NAME1 --- " + NAME1);
                        this.writeLog("FI_LIFNR_CHECK_Action KTOKK --- " + KTOKK);
                        i++;
                    }
                }
                writeLog("调用SAP中。。。。。。");
                client.execute(function);
                writeLog("调用结束。。。。。。。");

                //处理返回数据
                JCO.Table resultTable = function.getExportParameterList().getTable("ET_LIFNR");
                int length = resultTable.getNumRows();
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    resultTable.setRow(j);
                    // 如果包含E或A 则失败
                    if ("E".equals(resultTable.getString("MSGTY")) || "A".equals(resultTable.getString("MSGTY"))) {
                        builder.append(resultTable.getString("NAME1")).append("： ").append(resultTable.getString("MSGTX")).append("</br>");
                    }
                }

                //说明有表中有N行错误
                if (builder.length() > 0) {
                    request.getRequestManager().setMessageid("10000");
                    request.getRequestManager().setMessagecontent("sap返回消息：--- " + builder.toString());
                }
                this.writeLog("FI_LIFNR_CHECK_Action end --- ");
                return isSuccess;
            } catch (Exception e) {
                this.writeLog("FI_LIFNR_CHECK_Action Exception:" + e);
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
