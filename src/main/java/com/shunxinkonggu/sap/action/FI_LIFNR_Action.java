package com.shunxinkonggu.sap.action;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.shunxinkonggu.sap.util.SapConnectPool;
import com.shunxinkonggu.sap.util.SapConnectPoolBatch;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.interfaces.workflow.action.BaseAction;
import weaver.soa.workflow.request.RequestInfo;

/***
 * 供应商主数据
 *
 * @author 刘晔
 *
 */
public class FI_LIFNR_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("FI_LIFNR_Action start ================================= ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("FI_LIFNR_Action 供应商主数据 requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {

            String LIFNR = "";        // 供应商编码
            String LAND1 = "";        // 国家代码
            String TITLE_MEDI = "";   // 称谓
            String KTOKK = "";          // 账户组
            String NAME1 = "";          // 供应商全称
            String SORT1 = "";          // 供应商简称

            String STRAS = "";        // 地址街道
            String TELF1 = "";        // 电话
            String TELFX = "";        // 传真
            String STCEG = "";        // 增值税登记号


            RecordSet rs = null;
            SapConnectPoolBatch connect = null;
            JCO.Client client = null;
            JCO.Function function = null;
            JCO.Repository repository = null;
            IFunctionTemplate ft = null;
            try {

                this.writeLog("FI_LIFNR_Action fromTable --- " + fromTable);

                connect = new SapConnectPoolBatch();
                client = connect.getConnection();
                repository = new JCO.Repository("sap", client);
                ft = repository.getFunctionTemplate("ZRFC_FI_LIFNR_CREATE_B");
                function = new JCO.Function(ft);

                rs = new RecordSet();
                rs.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);

                JCO.Table table = function.getImportParameterList().getTable("IT_LFA1");
                int i = 0;
                while (rs.next()) {
                    LIFNR = Util.null2String(rs.getString("gysbm"));
                    LAND1 = Util.null2String(rs.getString("gjdm"));
                    TITLE_MEDI = Util.null2String(rs.getString("applletion"));
                    KTOKK = Util.null2String(rs.getString("accountgroup"));
                    NAME1 = Util.null2String(rs.getString("gysqc"));
                    SORT1 = Util.null2String(rs.getString("gysjc"));

                    table.appendRow();
                    table.setRow(i);
                    table.setValue(LIFNR, "LIFNR");
                    table.setValue(LAND1, "LAND1");
                    table.setValue(TITLE_MEDI, "TITLE_MEDI");
                    table.setValue(KTOKK, "KTOKK");
                    table.setValue(NAME1, "NAME1");
                    table.setValue(SORT1, "SORT1");

                    table.setValue(STRAS, "STRAS");
                    table.setValue(TELF1, "TELF1");
                    table.setValue(TELFX, "TELFX");
                    table.setValue(STCEG, "STCEG");
                    i++;

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
                writeLog("调用SAP中。。。。。。");
                client.execute(function);
                writeLog("调用结束。。。。。。。");

                //处理返回数据
                JCO.Table resultTable = function.getExportParameterList().getTable("ET_LFA1");
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
                this.writeLog("FI_LIFNR_Action end ================================= ");
                return isSuccess;
            } catch (Exception e) {
                this.writeLog("FI_LIFNR_Action Exception:" + e);
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
