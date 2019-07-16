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
 * 员工加班工资
 *
 * @author 刘晔
 *
 */
public class HRA11_OvertimeWages_Action extends BaseBean implements Action {

    public String execute(RequestInfo request) {

        this.writeLog("HRA11_OvertimeWages_Action start --- ");

        String isSuccess = BaseAction.SUCCESS;
        String requestid = request.getRequestid();
        String operatetype = request.getRequestManager().getSrc();
        String fromTable = request.getRequestManager().getBillTableName();

        this.writeLog("HRA11_OvertimeWages_Action 员工加班工资 requestid --- " + requestid + "  operatetype --- " + operatetype + "   fromTable --- " + fromTable);

        if (operatetype.equals("submit")) {

            String pernr = "";  // 人员编码
            String jbgzyf = ""; // 加班工资月份
            //String wtxts = "";  // 未调休天数
            String jbgz = "";   // 加班工资


            RecordSet rs = null;
            SapConnectPool connect = null;
            JCO.Client client = null;
            JCO.Function function = null;
            JCO.Repository repository = null;
            IFunctionTemplate ft = null;
            try {

                this.writeLog("HRA11_OvertimeWages_Action fromTable --- " + fromTable);

                connect = new SapConnectPool();
                client = connect.getConnection();
                repository = new JCO.Repository("sap", client);
                ft = repository.getFunctionTemplate("ZRFC_HR_HRA11_UPDATE");
                function = new JCO.Function(ft);

                rs = new RecordSet();
                rs.execute("select * from " + fromTable + "_dt1 where mainid in (select id from " + fromTable + " where requestid =" + requestid + ")");

                int itempbc = 0;
                JCO.Table jiabandetail = function.getTableParameterList().getTable("IT_HRA11");

                while (rs.next()) {

                    pernr = Util.null2String(rs.getString("gh"));
                    if (pernr.contains("_")) {
                        this.writeLog("副账号切割before --- " + pernr);
                        pernr = pernr.split("_")[1];
                        this.writeLog("副账号切割after --- " + pernr);
                    }
                    int yf = (rs.getInt("ssyf") * 1 + 1);
                    if (yf < 10) {
                        jbgzyf = Util.null2String((rs.getInt("ssnf") * 1 + 2017) + "0" + yf);
                    } else {
                        jbgzyf = Util.null2String((rs.getInt("ssnf") * 1 + 2017) + "" + yf);
                    }
                    //wtxts = Util.null2String(rs.getString("wtxts"));
                    jbgz = Util.null2String(rs.getString("hsje"));

                    this.writeLog("HRA11_OvertimeWages_Action pernr --- " + pernr);
                    this.writeLog("HRA11_OvertimeWages_Action jbgzyf --- " + jbgzyf);
                    //this.writeLog("HRA11_OvertimeWages_Action wtxts --- " + wtxts);
                    this.writeLog("HRA11_OvertimeWages_Action jbgz --- " + jbgz);

                    jiabandetail.appendRow();
                    jiabandetail.setRow(itempbc);
                    jiabandetail.setValue(pernr, "PERNR");
                    jiabandetail.setValue(jbgzyf, "SMON");
                    //jiabandetail.setValue(wtxts, "");
                    jiabandetail.setValue(jbgz, "BETRG");
                    itempbc++;
                }

                client.execute(function);
                JCO.Table table = function.getTableParameterList().getTable("RETURN");

                this.writeLog("HRA11_OvertimeWages_Action 推送结果 --- " + table.getNumRows());

                boolean flag = false;
                String resultArry = "";
                String messageArry = "";
                for (int i = 0; i < table.getNumRows(); i++) {
                    table.setRow(i);
                    String result = Util.null2String(table.getString("TYPE"));
                    String message = Util.null2String(table.getString("MESSAGE"));

                    this.writeLog("HRA11_OvertimeWages_Action result --- " + result + " message --- " + message);

                    if ("E".equals(result) || "A".equals(result)) {// 如果包含E或A 则失败
                        flag = true;
                        messageArry += ";  " + message;
                        resultArry += ";  " + result;
                    }
                }

                this.writeLog("HRA11_OvertimeWages_Action result --- " + resultArry);
                this.writeLog("HRA11_OvertimeWages_Action message --- " + messageArry);
                if (flag) {
                    request.getRequestManager().setMessageid("10000");
                    request.getRequestManager().setMessagecontent("sap返回错误：消息内容---" + messageArry);
                    return isSuccess;
                }

                this.writeLog("HRA11_OvertimeWages_Action end --- ");
            } catch (Exception e) {
                this.writeLog("HRA11_OvertimeWages_Action Exception:" + e);
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
