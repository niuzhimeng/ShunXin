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
 * 客户主数据
 *
 * @author 刘晔
 *
 */
public class FI_KUNNR_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("FI_KUNNR_Action start --- ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("FI_KUNNR_Action 客户主数据 requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {
            String KUNNR = "";            // 客户编码
            String LAND1 = "";          // 国家代码
            String TITLE_MEDI = "";      // 称谓
            String KTOKD = "";              // 账户组
            String NAME1 = "";              // 客户全称
            String SORT1 = "";              // 客户简称

            String STRAS = "";              // 地址街道

            RecordSet rs = null;
            SapConnectPool connect = null;
            JCO.Client client = null;
            JCO.Function function = null;
            JCO.Repository repository = null;
            IFunctionTemplate ft = null;
            try {

                this.writeLog("FI_KUNNR_Action fromTable ================================= " + fromTable);

                connect = new SapConnectPool();
                client = connect.getConnection();
                //JCO.Repository类的构造函数有两个参数，第一个是可以任意指定的名字，第二个是当前使用的连接
                repository = new JCO.Repository("sap", client);
                ft = repository.getFunctionTemplate("ZRFC_FI_KUNNR_CREATE_B");
                function = new JCO.Function(ft);

                rs = new RecordSet();
                rs.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);
                int i = 0;
                while (rs.next()) {
                    KUNNR = Util.null2String(rs.getString("khbm"));
                    LAND1 = Util.null2String(rs.getString("gj"));
                    TITLE_MEDI = Util.null2String(rs.getString("applletion"));
                    KTOKD = Util.null2String(rs.getString("accountgroup"));
                    NAME1 = Util.null2String(rs.getString("khqc"));
                    SORT1 = Util.null2String(rs.getString("khjc"));
                    STRAS = Util.null2String("");

                    //返回该函数的Import参数列表
                    JCO.Table table = function.getImportParameterList().getTable("IT_KNA1");
                    //赋值 1 -> 2
                    table.appendRow();
                    table.setRow(i);
                    table.setValue(KUNNR, "KUNNR");
                    table.setValue(LAND1, "LAND1");
                    table.setValue(TITLE_MEDI, "TITLE_MEDI");
                    table.setValue(KTOKD, "KTOKD");
                    table.setValue(NAME1, "NAME1");
                    table.setValue(SORT1, "SORT1");
                    table.setValue(STRAS, "STRAS");

                    this.writeLog("FI_KUNNR_Action KUNNR --- " + KUNNR);
                    this.writeLog("FI_KUNNR_Action LAND1 --- " + LAND1);
                    this.writeLog("FI_KUNNR_Action TITLE_MEDI --- " + TITLE_MEDI);
                    this.writeLog("FI_KUNNR_Action KTOKD --- " + KTOKD);
                    this.writeLog("FI_KUNNR_Action NAME1 --- " + NAME1);
                    this.writeLog("FI_KUNNR_Action SORT1 --- " + SORT1);
                    this.writeLog("FI_KUNNR_Action STRAS --- " + STRAS);
                    i++;
                }
                //执行 远程调用
                client.execute(function);

                //处理返回数据
                JCO.Table resultTable = function.getExportParameterList().getTable("ET_KNA1");
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

                this.writeLog("FI_KUNNR_Action end =================================");
                return isSuccess;
            } catch (Exception e) {
                this.writeLog("FI_KUNNR_Action Exception:" + e);
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
