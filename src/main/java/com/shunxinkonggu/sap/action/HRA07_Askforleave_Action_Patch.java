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
 * 员工请假
 *
 * @author 刘晔
 *
 */
public class HRA07_Askforleave_Action_Patch extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("HRA07_Askforleave_Action start --- ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        //获取操作类型   save(保存) reject(退回) submit(提交) delete(删除)
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("HRA07_Askforleave_Action 员工请假批量 requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {
            String pernr = ""; // 人员编码
            String qqdlx = ""; // 请假类型编码
            String startDate = ""; // 开始日期
            String endDate = ""; // 结束日期
            String startTime = ""; // 开始时间
            String endTime = ""; // 结束时间

            SapConnectPool connect = null;
            JCO.Client client;
            JCO.Function function;
            JCO.Repository repository;
            IFunctionTemplate ft;
            try {
                this.writeLog("HRA07_Askforleave_Action fromTable --- " + fromTable);
                connect = new SapConnectPool();
                client = connect.getConnection();
                repository = new JCO.Repository("sap", client);
                ft = repository.getFunctionTemplate("ZRFC_HR_HRA07_UPDATE");
                function = new JCO.Function(ft);

                this.writeLog("返回的函数ft： " + ft);
                this.writeLog("返回的函数function： " + function);

                RecordSet rs = new RecordSet();
                rs.execute("select * from " + fromTable + " where requestid = " + requestid);
                if (rs.next()) {
                    pernr = Util.null2String(rs.getString("yggh"));
                    qqdlx = Util.null2String(rs.getString("qjlxbm"));
                    this.writeLog("HRA07_Askforleave_Action pernr --- " + pernr);
                    this.writeLog("HRA07_Askforleave_Action qqdlx --- " + qqdlx);

                }
                RecordSet detailSet = new RecordSet();
                detailSet.execute("SELECT d.* FROM " + fromTable + " m LEFT JOIN " + fromTable + "_DT1" + " d ON m.id = d.MAINID WHERE m.REQUESTID = " + requestid);
                int i = 0;
                JCO.Table table = function.getTableParameterList().getTable("IN_TABLE0912");
                while (detailSet.next()) {
                    this.writeLog("循环开始=============");
                    startDate = Util.null2String(detailSet.getString("ksrq").replace("-", "")); // 开始日期
                    endDate = Util.null2String(detailSet.getString("jsrq").replace("-", "")); // 结束日期
                    startTime = Util.null2String(detailSet.getString("kssj")); // 开始时间
                    endTime = Util.null2String(detailSet.getString("jssj")); // 结束时间

                    this.writeLog("HRA07_Askforleave_Action startDate --- " + startDate);
                    this.writeLog("HRA07_Askforleave_Action endDate --- " + endDate);
                    this.writeLog("HRA07_Askforleave_Action startTime --- " + startTime);
                    this.writeLog("HRA07_Askforleave_Action endTime --- " + endTime);

                    //赋值 1 -> 2
                    table.appendRow();
                    table.setRow(i);
                    table.setValue(pernr, "PERNR");//人员编码
                    table.setValue(qqdlx, "AWART");//请假类型编码
                    table.setValue(startDate, "BEGDA");
                    table.setValue(endDate, "ENDDA");
                    table.setValue(startTime, "FLAG1");
                    table.setValue(endTime, "FLAG2");
                    i++;
                }

                client.execute(function);
                //处理返回数据
                JCO.Table resultTable = function.getTableParameterList().getTable("RETURN");

                this.writeLog("HRA07_Askforleave_Action 推送结果 --- " + resultTable.getNumRows());

                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < resultTable.getNumRows(); j++) {
                    resultTable.setRow(i);
                    String result = Util.null2String(resultTable.getString("TYPE"));
                    String message = Util.null2String(resultTable.getString("MESSAGE"));
                    this.writeLog("返回第 " + j + " 行, result = " + request);
                    this.writeLog("返回第 " + j + " 行, message = " + message);

                    this.writeLog("HRA07_Askforleave_Action result --- " + result + " message --- " + message);
                    if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
                        builder.append(resultTable.getString("TYPE")).append("： ").append(resultTable.getString("MESSAGE")).append("</br>");
                    }
                }
                //说明有表中有N行错误
                if (builder.length() > 0) {
                    request.getRequestManager().setMessageid("10000");
                    request.getRequestManager().setMessagecontent("sap返回消息：--- " + builder.toString());
                }
                this.writeLog("HRA07_Askforleave_Action end --- ");
            } catch (Exception e) {
                this.writeLog("HRA07_Askforleave_Action Exception:" + e);
                request.getRequestManager().setMessageid("10000");
                request.getRequestManager().setMessagecontent("返回错误：" + e);
                return isSuccess;
            } finally {
                try {
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
