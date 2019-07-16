package com.shunxinkonggu.sap.action;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.shunxinkonggu.sap.util.SapConnectPoolBatch;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/***
 * 客户主数据  CHECK
 *
 * @author 刘晔
 *
 */
public class FI_KUNNR_CHECK_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("FI_KUNNR_CHECK_Action start --- ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("FI_KUNNR_CHECK_Action 客户主数据 CHECK requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {
            writeLog("进入if后====");
            String khmc;            //客户名称
            String khzhz;              //客户帐户组
            RecordSet rs = null;
            SapConnectPoolBatch connect = null;
            JCO.Client client = null;
            JCO.Function function = null;
            JCO.Repository repository = null;
            IFunctionTemplate ft = null;
            try {

                this.writeLog("FI_KUNNR_CHECK_Action fromTable --- " + fromTable);
                connect = new SapConnectPoolBatch();
                client = connect.getConnection();
                repository = new JCO.Repository("sap", client);
                ft = repository.getFunctionTemplate("ZRFC_FI_KUNNR_CHECK_B");
                function = new JCO.Function(ft);

                rs = new RecordSet();
                rs.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);
                JCO.Table table = function.getImportParameterList().getTable("IT_KUNNR");

                int i = 0;
                writeLog("准备执行循环====");
                while (rs.next()) {
                    khmc = Util.null2String(rs.getString("khqc"));
                    khzhz = Util.null2String(rs.getString("accountgroup"));

                    table.appendRow();
                    table.setRow(i);
                    table.setValue(khzhz, "KTOKD");
                    table.setValue(khmc, "NAME1");
                    this.writeLog("FI_KUNNR_CHECK_Action khmc --- " + khmc);
                    this.writeLog("FI_KUNNR_CHECK_Action khzhz --- " + khzhz);
                    i++;
                }
                writeLog("执行调用中======================");
                client.execute(function);
                writeLog("调用完毕======================");

                //处理返回数据
                JCO.Table resultTable = function.getExportParameterList().getTable("ET_KUNNR");

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

                this.writeLog("FI_KUNNR_CHECK_Action end --- ");
                return isSuccess;
            } catch (Exception e) {
                this.writeLog("FI_KUNNR_CHECK_Action Exception:" + e);
                request.getRequestManager().setMessageid("10000");
                request.getRequestManager().setMessagecontent("返回错误：" + e);
                return isSuccess;
            } finally {
                try {
                    // client.disconnect();
                    if (connect != null) {
                        connect.disConnection();
                    }
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
