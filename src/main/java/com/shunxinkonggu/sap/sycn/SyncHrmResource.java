package com.shunxinkonggu.sap.sycn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.TimeUtil;
import weaver.general.Util;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.job.JobActivitiesComInfo;
import weaver.hrm.job.JobTitlesComInfo;
import weaver.hrm.resource.ResourceComInfo;

import com.shunxinkonggu.sap.sycn.impl.SubCompanyImpl;
import com.shunxinkonggu.sap.sycn.impl.DepartmentImpl;
import com.shunxinkonggu.sap.sycn.impl.JobGroupsImpl;
import com.shunxinkonggu.sap.sycn.impl.ActivitiesImpl;
import com.shunxinkonggu.sap.sycn.impl.JobtitleImpl;
import com.shunxinkonggu.sap.sycn.impl.HrmResourceImpl;

import com.shunxinkonggu.sap.util.SapConnect;
import com.shunxinkonggu.sap.util.Utils;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;

public class SyncHrmResource extends BaseBean {

    private String logcontent = "</br>";//保存成功消息
    private String logcontentFailure = null;//保存失败消息

    public String synSubcompany() {

        this.writeLog("synSubcompany start --- " + TimeUtil.getCurrentTimeString());
        logcontentFailure = "提示消息：synCompany start --- " + "</br>";

        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        try {
            int subcompanycout = 0;
            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_001");
            function = new JCO.Function(ft);
            client.execute(function);

            SubCompanyImpl si = null;
            DepartmentImpl di = null;
            SubCompanyComInfo scc = new SubCompanyComInfo();
            DepartmentComInfo dc = new DepartmentComInfo();

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZHRP_COM");
            this.writeLog("synSubcompany table.getNumRows --- " + table.getNumRows());
            logcontentFailure += "synSubcompany 获取SAP接口数据条数为 --- " + table.getNumRows();
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);
                //String objectType = Util.null2String(table.getString("hrp9107")); 		 // 对象类型 hrp9107 标记01公司 02部门
                String orgcode = Util.null2String(table.getString("OBJID"));             // 组织编码
                String orgdesc = Util.null2String(table.getString("STEXT"));             // 组织简称
                String orgname = Util.null2String(table.getString("STEXT"));             // 组织名称
                String orgsuperiorcode = Util.null2String(table.getString("OBJID_COM")); // 上级组织编码
                int showorder = Util.getIntValue(table.getString("NUM"), 0);             // 优先级
                String canceled = Util.null2String(table.getString("FLAG"));             // 是否删除标示 1封存，null或者0代表有效
                String startTime = Util.null2String(table.getString("BEGDA"));        //有效期开始
                String endTime = Util.null2String(table.getString("ENDDA"));          //有效期结束

                si = new SubCompanyImpl();
                di = new DepartmentImpl();

                subcompanycout++;
                this.writeLog("synSubcompany orgcode-----" + orgcode);
                this.writeLog("synSubcompany orgdesc-----" + orgdesc);
                this.writeLog("synSubcompany orgname-----" + orgname);
                this.writeLog("synSubcompany orgsuperiorcode-----" + orgsuperiorcode);
                this.writeLog("synSubcompany showorder-----" + showorder);
                this.writeLog("synSubcompany NUM-----" + table.getString("NUM"));
                this.writeLog("synSubcompany canceled-----" + canceled);
                this.writeLog("synSubcompany startTime-----" + startTime);
                this.writeLog("synSubcompany endTime-----" + endTime);

                //判断是否在有效期内
                Boolean useless = isUseless(startTime, endTime);
                if (useless) {
                    continue;
                }

                int subid = Util.getIntValue(Utils.getIdByCode("hrmsubcompany", orgcode), 0);

                int orgsuperiorid = Util.getIntValue(Utils.getIdByCode("hrmsubcompany", orgsuperiorcode), 0);

                si.setId(subid + "");
                si.setSubcompanycode(orgcode);
                si.setSubcompanyname(orgname);
                si.setSubcompanydesc(orgdesc);
                si.setSupsubcomid(orgsuperiorid + "");
                si.setShoworder(showorder + "");
                si.setCanceled(canceled);

                boolean isSuccess = false;

                if (subid <= 0) {//分部不存在
                    isSuccess = si.insertSubCompany();
                    if (isSuccess) {
                        int id = Util.getIntValue(Utils.getIdByCode("hrmsubcompany", orgcode), 0);
                        if (id > 0) {
                            si.insertMenuconfig(id + "");

                            di.setDepartmentcode(orgcode);
                            di.setDepartmentname(orgname);
                            di.setDepartmentmark(orgdesc);
                            di.setSubcompanyid1(id + "");
                            di.setSupdepid(0 + "");
                            di.setShoworder("0");
                            di.setCanceled(canceled);

                            di.insertDepartment();
                        }
                    }
                } else {//分部存在
                    isSuccess = si.updateSubCompany();

                    int depid = Util.getIntValue(Utils.getIdByCode("hrmdepartment", orgcode), 0);
                    if (depid > 0) {
                        di.setId(depid + "");
                        di.setDepartmentcode(orgcode);
                        di.setDepartmentname(orgname);
                        di.setDepartmentmark(orgdesc);
                        di.setSubcompanyid1(subid + "");
                        di.setSupdepid(0 + "");
                        di.setShoworder("0");
                        di.setCanceled(canceled);
                        boolean updateDepartmentbl = di.updateDepartment();
                    }
                }

                this.writeLog("synSubcompany i --- " + i);
            }

            scc.removeCompanyCache();
            dc.removeCompanyCache();

            this.writeLog("synSubcompany end -- subcompanycout=" + subcompanycout + " -- " + TimeUtil.getCurrentTimeString());
        } catch (Exception e) {
            this.writeLog("synSubcompany Exception:" + e);
        } finally {
            // 关闭sap链接
            client.disconnect();
            sc.disConnection();
            client = null;
        }
        return logcontentFailure + logcontent + "提示消息： end --- " + "</br>";
    }


    public String synDepartment() {

        logcontentFailure = "提示消息：synDepartment start --- " + "</br>";
        this.writeLog("synDepartment start --- " + TimeUtil.getCurrentTimeString());

        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        try {
            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_002");
            function = new JCO.Function(ft);
            //function.getImportParameterList().setValue("O", "OTYPE");// 获取组织
            client.execute(function);

            DepartmentImpl di = null;

            DepartmentComInfo dc = null;
            dc = new DepartmentComInfo();

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZHRP_DEP");
            this.writeLog("synDepartment table.getNumRows --- " + table.getNumRows());
            logcontentFailure += "synDepartment 获取SAP接口数据条数为 --- " + table.getNumRows();
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);
                //String objectType = Util.null2String(table.getString("OTYPE")); 		   // 对象类型
                String orgcode = Util.null2String(table.getString("OBJID"));               // 组织编码
                String orgdesc = Util.null2String(table.getString("STEXT"));               // 组织简称
                String orgname = Util.null2String(table.getString("STEXT"));               // 组织名称
                String orgsuperiorcode = Util.null2String(table.getString("OBJID_COM"));   // 上级分部编码
                String orgsuperdepcode = Util.null2String(table.getString("OBJID_DEP"));   // 上级部门编码
                int showorder = Util.getIntValue(table.getString("NUM"), 0);               // 优先级
                String canceled = Util.null2String(table.getString("FLAG"));               //是否删除标示 	1封存，null或者0代表有效
                String startTime = Util.null2String(table.getString("BEGDA"));        //有效期开始
                String endTime = Util.null2String(table.getString("ENDDA"));          //有效期结束

                //this.writeLog("synDepartment objectType-----" + objectType);
                this.writeLog("synDepartment orgcode-----" + orgcode);
                this.writeLog("synDepartment orgdesc-----" + orgdesc);
                this.writeLog("synDepartment orgname-----" + orgname);
                this.writeLog("synDepartment orgsuperiorcode-----" + orgsuperiorcode);
                this.writeLog("synDepartment orgsuperdepcode-----" + orgsuperdepcode);
                this.writeLog("synDepartment showorder-----" + showorder);
                this.writeLog("synDepartment canceled-----" + canceled);
                this.writeLog("synDepartment startTime-----" + startTime);
                this.writeLog("synDepartment endTime-----" + endTime);

                //判断是否在有效期内
                Boolean useless = isUseless(startTime, endTime);
                if (useless) {
                    continue;
                }

                di = new DepartmentImpl();

                int depid = Util.getIntValue(Utils.getIdByCode("hrmdepartment", orgcode), 0);
                int supdepid = Util.getIntValue(Utils.getIdByCode("hrmdepartment", orgsuperdepcode), 0);
                int subcompanyid = Util.getIntValue(Utils.getIdByCode("hrmsubcompany", orgsuperiorcode), 0);

//				if(supdepid <= 0){
//					subcompanyid = Util.getIntValue(Utils.getIdByCode("hrmsubcompany", orgsuperiorcode),0);
//				}else{
//					subcompanyid = di.getSubcompanyid(supdepid);
//				}

                if (subcompanyid <= 0) {
                    this.writeLog("synDepartment 所属公司不存在，公司编码为 --- " + orgsuperiorcode);
                    //logcontentFailure += "提示消息： 所属公司不存在，公司编码为 - " + orgsuperiorcode + "，部门ID为 - " + orgcode + " 部门名称为 - " + orgdesc + "</br>";
                    continue;
                }

                di.setId(depid + "");
                di.setDepartmentcode(orgcode);
                di.setDepartmentname(orgname);
                di.setDepartmentmark(orgdesc);
                di.setSubcompanyid1(subcompanyid + "");
                di.setSupdepid(supdepid + "");
                di.setShoworder(showorder + "");
                di.setCanceled(canceled);

                if (depid <= 0) {
                    di.insertDepartment();
                } else {
                    di.updateDepartment();
                }

            }
            dc.removeCompanyCache();

        } catch (Exception e) {
            this.writeLog("synDepartment Exception:" + e);
        } finally {
            // 关闭sap链接
            client.disconnect();
            sc.disConnection();
            client = null;
        }

        this.writeLog("synDepartment end --- " + TimeUtil.getCurrentTimeString());
        return logcontentFailure + logcontent + "提示消息：synDepartment end --- " + TimeUtil.getCurrentTimeString() + "</br>";

    }

    public String synJobGroups() {

        this.writeLog("提示消息：synJobGroups start --- " + TimeUtil.getCurrentTimeString());
        logcontentFailure = "提示消息：synJobGroups start --- " + "</br>";

        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        try {

            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_003");
            function = new JCO.Function(ft);
            //function.getImportParameterList().setValue("O", "OTYPE");// 获取组织
            client.execute(function);

            JobGroupsImpl jg = new JobGroupsImpl();
            String jobgName = "";    //职务类别
            String jobgcode = "";    //职务类别编码

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZHRP_01");
            this.writeLog("synJobGroups table.getNumRows --- " + table.getNumRows());
            logcontentFailure += "synJobGroups 获取SAP接口数据条数为 --- " + table.getNumRows();
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);

                jobgcode = Util.null2String(table.getString("OBJID"));
                jobgName = Util.null2String(table.getString("STEXT"));

                int actid = 0;
                //职务id
                int jobgroupsId = Util.getIntValue(jg.getJobGroupsIdByCode(jobgcode), 0);
                if (jobgroupsId <= 0) {
                    jg.setJOBGROUPNAME(jobgName);
                    jg.setJOBGROUPREMARK(jobgcode);
                    actid = Util.getIntValue(jg.insertJobGroups(), 0);
                    this.writeLog("提示消息：职务类别创建成功，职务ID为-----" + jobgcode + " --  职务名称为--" + jobgName);
                } else {
                    jg.setId(jobgroupsId + "");
                    jg.setJOBGROUPNAME(jobgName);
                    jg.setJOBGROUPREMARK(jobgcode);
                    jg.updateJobGroups();
                    actid = jobgroupsId;
                    this.writeLog("提示消息：职务类别更新成功，职务ID为-----" + jobgcode + " --  职务名称为--" + jobgName);
                }

                if (actid <= 0) {
                    this.writeLog("提示消息：职务类别同步失败，职务ID为-----" + jobgcode + " --  职务名称为--" + jobgName);
                    logcontentFailure += "提示消息：职务同步失败，职务ID为-----" + jobgcode + " --  职务名称为--" + jobgName + "</br>";
                    continue;
                }
            }

        } catch (Exception e) {
            this.writeLog("提示消息： catch Exception:" + e);
        }

        this.writeLog("提示消息： end --- " + TimeUtil.getCurrentTimeString());
        return logcontentFailure + logcontent + "提示消息： end --- " + "</br>";

    }

    public String synActivities() {

        this.writeLog("提示消息：synActivities start --- " + TimeUtil.getCurrentTimeString());
        logcontentFailure = "提示消息：synActivities start --- " + "</br>";

        JobGroupsImpl jg = null;
        ActivitiesImpl ai = null;
        JobActivitiesComInfo jac = null;

        String actiName = "";        //职务名称
        String acticode = "";        //职务编码
        String jobgroupscode = "";    //职务类别编码
        String jobgroupscodetemp = "";

        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        try {

            jg = new JobGroupsImpl();
            ai = new ActivitiesImpl();
            jac = new JobActivitiesComInfo();

            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_004");
            function = new JCO.Function(ft);
            //function.getImportParameterList().setValue("O", "OTYPE");// 获取组织
            client.execute(function);

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZHRP_C");
            this.writeLog("synActivities table.getNumRows --- " + table.getNumRows());
            logcontentFailure += "synActivities 获取SAP接口数据条数为 --- " + table.getNumRows();
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);

                acticode = Util.null2String(table.getString("OBJID"));
                actiName = Util.null2String(table.getString("STEXT"));
                jobgroupscode = Util.null2String(table.getString("OBJID_01"));
                jobgroupscodetemp = jobgroupscode;

                //职务类别ID
                jobgroupscode = jg.getJobGroupsIdByCode(jobgroupscode);

                if (!(jobgroupscode.length() > 0)) {
                    jobgroupscode = "11";
                }

                if (jobgroupscode.length() > 0) {

                    int actid = 0;
                    //职务id
                    int activitiesId = Util.getIntValue(ai.getActivitiesIdByCode(acticode), 0);
                    if (activitiesId <= 0) {
                        ai.setJobactivityname(actiName);
                        ai.setJobactivitymark(acticode);
                        ai.setJobgroupid(jobgroupscode);
                        actid = Util.getIntValue(ai.insertActivities(), 0);
                        this.writeLog("提示消息：职务创建成功，职务ID为-----" + acticode + " --  职务名称为--" + actiName + " --  职务类别编码为--" + jobgroupscode);
                    } else {
                        ai.setId(activitiesId + "");
                        ai.setJobactivityname(actiName);
                        ai.setJobactivitymark(acticode);
                        ai.setJobgroupid(jobgroupscode);
                        ai.updateActivities();
                        actid = activitiesId;
                        this.writeLog("提示消息：职务更新成功，职务ID为-----" + acticode + " --  职务名称为--" + actiName + " --  职务类别编码为--" + jobgroupscode);
                    }

                    if (actid <= 0) {
                        this.writeLog("提示消息：职务同步失败，职务ID为-----" + acticode + " --  职务名称为--" + actiName);
                        logcontentFailure += "提示消息：职务同步失败，职务ID为-----" + acticode + " --  职务名称为--" + actiName + "</br>";
                        continue;
                    }
                } else {
                    this.writeLog("提示消息：职务同步失败，职务类别" + jobgroupscodetemp + "不存在，职务ID为-----" + acticode + " --  职务名称为--" + actiName);
                    logcontentFailure += "提示消息：职务同步失败，职务类别" + jobgroupscodetemp + "不存在，职务ID为-----" + acticode + " --  职务名称为--" + actiName + "</br>";
                    continue;
                }
            }

        } catch (Exception e) {
            this.writeLog("提示消息： catch Exception:" + e);
        }

        jac.removeJobActivitiesCache();
        this.writeLog("提示消息： end --- " + TimeUtil.getCurrentTimeString());
        return logcontentFailure + "提示消息： end --- " + "</br>";
    }

    public String synJobtitle() {

        this.writeLog("synJobtitle start --- " + TimeUtil.getCurrentTimeString());
        logcontentFailure = "提示消息：synJobtitle start --- " + "</br>";

        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        JobTitlesComInfo jc = null;
        ActivitiesImpl ai = new ActivitiesImpl();
        JobtitleImpl ji = null;

        try {
            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_005");
            function = new JCO.Function(ft);
            //function.getImportParameterList().setValue("S", "OTYPE");// 获取组织
            client.execute(function);

            jc = new JobTitlesComInfo();

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZHRP_S");
            this.writeLog("synJobtitle table.getNumRows --- " + table.getNumRows());
            logcontentFailure += "synJobtitle 获取SAP接口数据条数为 --- " + table.getNumRows();
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);
                String postcode = Util.null2String(table.getString("OBJID"));      //岗位编码
                String postdesc = Util.null2String(table.getString("STEXT"));      //岗位简称
                String postname = Util.null2String(table.getString("STEXT"));      //岗位名称
                String depcode = Util.null2String(table.getString("OBJID_DEP"));  //所属部门编码
                String acticode = Util.null2String(table.getString("OBJID_C"));   //所属职务编码
                //String canceled = Util.null2String(table.getString("FLAG_DEL"));  //是否删除标示  (需要修改字段)

                this.writeLog("synJobtitle postcode-----" + postcode);
                this.writeLog("synJobtitle postdesc-----" + postdesc);
                this.writeLog("synJobtitle postname-----" + postname);
                this.writeLog("synJobtitle depcode-----" + depcode);
                this.writeLog("synJobtitle acticode-----" + acticode);

                ji = new JobtitleImpl();

                int postid = Util.getIntValue(Utils.getIdByCode("hrmjobtitles", postcode), 0);

//				if("1".equals(canceled) && postid > 0){
//					ji.setId(postid+"");
//					ji.deleteJobtitle();
//					continue;
//				}

                int depid = 0;

                int tempid = Util.getIntValue(Utils.getIdByCode("hrmdepartment", depcode), 0);

//				if(tempid <= 0){
//
//					tempid = Util.getIntValue(Utils.getIdByCode("hrmsubcompany", depcode),0);
//
//					String tempcode = depcode + "_" + tempid;
//
//					depid = Util.getIntValue(Utils.getIdByCode("hrmdepartment", tempcode),0);
//
//				}else{
                depid = tempid;
//				}

                if (depid <= 0) {
                    this.writeLog("synJobtitle 所属部门不存在，岗位编码为-----" + postcode);
                    logcontentFailure += "synJobtitle 所属部门不存在，岗位编码为-----" + postcode + "   所属部门编码为-----" + depcode + "</br>";
                    continue;
                }

                int activitiesId = Util.getIntValue(ai.getActivitiesIdByCode(acticode), 0);
                if (activitiesId <= 0) {
                    //this.writeLog("synJobtitle 所属职务不存在，岗位编码为-----" + postcode + "   职务编码为-----" +acticode);
                    //logcontentFailure += "synJobtitle 所属职务不存在，岗位编码为-----" + postcode + "   职务编码为-----" +acticode + "</br>";
                    //continue;
                    activitiesId = 7541;
                }

                ji.setId(postid + "");
                ji.setJobtitlecode(postcode);
                ji.setJobtitlemark(postdesc);
                ji.setJobtitlename(postname);
                ji.setJobactivityid(activitiesId + "");
                ji.setJobdepartmentid(depid + "");

                if (postid <= 0) {
                    ji.insertJobtitle();
                } else {
                    ji.updateJobtitle();
                }
            }

            jc.removeJobTitlesCache();

            this.writeLog("synJobtitle end --- " + TimeUtil.getCurrentTimeString());
        } catch (Exception e) {
            this.writeLog("synJobtitle Exception:" + e);
        } finally {
            // 关闭sap链接
            client.disconnect();
            sc.disConnection();
            client = null;
        }
        return logcontentFailure + logcontent + "提示消息： end --- " + "</br>";
    }

    public String synHrmresource() {

        this.writeLog("synHrmresource start --- " + TimeUtil.getCurrentTimeString());
        logcontentFailure = "提示消息： start --- " + "</br>";

        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        RecordSet rs = new RecordSet();
        RecordSet rsjiangang = new RecordSet();
        ResourceComInfo rc = null;
        HrmResourceImpl ri = null;
        ArrayList<String> workcodeary = new ArrayList<String>();

        try {
            RecordSet seclevelSet = new RecordSet();
            seclevelSet.executeQuery("select id, seclevel from hrmresource");
            // id - 安全级别map
            Map<Integer, String> idSecMap = new HashMap<Integer, String>(seclevelSet.getCounts() + 10);
            while (seclevelSet.next()) {
                idSecMap.put(seclevelSet.getInt("id"), seclevelSet.getString("seclevel"));
            }
            rc = new ResourceComInfo();
            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_006");
            function = new JCO.Function(ft);
            function.getImportParameterList().setValue("10000000", "IN_OBJID");// 获取组织
            //function.getImportParameterList().setValue(TimeUtil.getCurrentDateString(), "DATUM");// 获取组织
            client.execute(function);

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZUSER_INFO");
            this.writeLog("synHrmresource table.getNumRows --- " + table.getNumRows());
            logcontentFailure += "synHrmresource 获取SAP接口数据条数为 --- " + table.getNumRows();
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);

                String lastname = Util.null2String(table.getString("NACHN"));     //姓名
                this.writeLog("synHrmresource lastname-----" + lastname);
                String workcode = Util.null2String(table.getString("PERNR"));     //人员编号

                if (!workcodeary.contains(workcode)) {
                    workcodeary.add(workcode);
                    String sqljiangang = "update hrmresource set status = '7' where instr(workcode, '" + workcode + "_') > 0";
                    rsjiangang.executeSql(sqljiangang);
                }

                this.writeLog("synHrmresource workcode-----" + workcode);
                String loginid = "sx" + workcode.substring(1, workcode.length());  //系统登陆帐号
                this.writeLog("synHrmresource loginid-----" + loginid);
                String stat1 = Util.null2String(table.getString("MASSN"));        //员工状态
                this.writeLog("synHrmresource stat1-----" + stat1);
                String status = "";
                if ("A".equals(stat1)) {
                    status = "1";
                } else if ("B".equals(stat1)) {
                    status = "0";
                } else if ("C".equals(stat1)) {
                    status = "1";
                } else if ("D".equals(stat1)) {
                    status = "2";
                } else if ("E".equals(stat1)) {
                    status = "6";
                } else if ("F".equals(stat1)) {
                    status = "7";
                } else if ("G".equals(stat1)) {
                    status = "7";
                } else if ("H".equals(stat1)) {
                    status = "7";
                } else if ("I".equals(stat1)) {
                    status = "7";
                } else if ("J".equals(stat1)) {
                    status = "5";
                } else if ("K".equals(stat1)) {
                    status = "1";
                } else if ("L".equals(stat1)) {
                    status = "7";
                } else if ("M".equals(stat1)) {
                    status = "1";
                }

                if (status.isEmpty()) {
                    status = stat1;
                }

                this.writeLog("synHrmresource stat1-----" + status);
                String sex = Util.null2String(table.getString("GESCH"));          //性别代码    0代表男，1代表女
                this.writeLog("synHrmresource gesch-----" + sex);
                String email = Util.null2String(table.getString("USRID_LONG"));   //邮箱
                this.writeLog("synHrmresource email-----" + email);
                String mobile = Util.null2String(table.getString("USRID"));       //手机
                this.writeLog("synHrmresource mobile-----" + mobile);
                String accounttype = Util.null2String(table.getString("TYPE"));   //账号类型 0代表主账号 1代表次账号
                this.writeLog("synHrmresource accounttype-----" + accounttype);
                String postcode = Util.null2String(table.getString("OBJID_S"));   //岗位
                this.writeLog("synHrmresource postcode-----" + postcode);
                String belongto = "";
                if (accounttype.equals("1")) {
                    belongto = Utils.getIdByCode("hrmresource", workcode);
                    loginid = loginid + "_" + postcode;
                    workcode = workcode + "_" + postcode;
                }
                this.writeLog("synHrmresource belongto-----" + belongto);
                String depcode = Util.null2String(table.getString("OBJID_DEP"));  //所属部门
                this.writeLog("synHrmresource depcode-----" + depcode);
                String subcode = Util.null2String(table.getString("OBJID_COM"));  //所属分部
                this.writeLog("synHrmresource comcode-----" + subcode);
                String birthday = Util.null2String(table.getString("GBDAT"));     //生日
                this.writeLog("synHrmresource birthday-----" + birthday);
                String startdate = Util.null2String(table.getString("BEGDA"));    //入职日期
                this.writeLog("synHrmresource startdate-----" + startdate);
                this.writeLog("         -----         ");

                ri = new HrmResourceImpl();

                int id = Util.getIntValue(Utils.getIdByCode("hrmresource", workcode), 0);

                if (id > 0 && Util.getIntValue(status) > 3) {
                    ri.deleteHrmResource5(id + "", status);
                    continue;
                }
                // 安全级别大于等于80 手机号置空
                if (Util.getIntValue(idSecMap.get(id)) >= 80) {
                    mobile = "";
                }

                String depid = "";
                String subid = "";

                if (Util.getIntValue(depcode, 0) > 0) {
                    depid = Util.null2String(Utils.getIdByCode("hrmdepartment", depcode));
                    subid = Util.null2String(ri.getSubidByDepid(depid));
                }

                if ("".equals(depid)) {//部门不存在

                    if (!"".equals(subid)) {//部门不存在 分部存在 把人员放在同名部门下面
                        depid = Util.null2String(Utils.getIdByCode("hrmdepartment", subcode));
                        subid = Util.null2String(ri.getSubidByDepid(depid));

                        if ("".equals(depid)) {//部门不存在
                            this.writeLog("synHrmresource 所属分部同名部门不存在，人员编码为----" + workcode + "  部门编码为---" + subcode);
                            logcontentFailure += "提示消息：  所属分部同名部门不存在，人员编码为----" + workcode + "  部门编码为---" + subcode + "</br>";
                            continue;
                        }
                    } else {//部门不存在 分部不存在 将该人员设置为无效
                        ri.deleteHrmResource5(id + "", status);
                        continue;
                    }
                }

                String postid = Util.null2String(Utils.getIdByCode("hrmjobtitles", postcode));

                if ("".equals(postid)) {
                    this.writeLog("synHrmresource 所属岗位不存在，人员编码为----" + workcode);
                    logcontentFailure += "提示消息： 所属岗位不存在，人员编号为----" + workcode + "  岗位编码为---" + postcode + "</br>";
                    continue;
                }

                //String password = Util.getEncrypt(workcode);
                //ri.setPassword(password);
                ri.setId(id + "");
                ri.setLastname(lastname);
                ri.setLoginid(loginid);
                ri.setWorkcode(workcode);
                ri.setStatus(status);
                ri.setSex(sex);
                ri.setEmail(email);
                ri.setMobile(mobile);
                ri.setAccounttype(accounttype);
                ri.setBelongto(belongto);
                ri.setDepartmentid(depid);
                ri.setSubcompanyid1(subid);
                ri.setJobtitle(postid);
                ri.setStartdate(startdate);
                ri.setBirthday(birthday);

                boolean isSuccess = false;
                if (id <= 0) {
                    id = Utils.getHrmMaxid();
                    ri.setId(id + "");
                    //ri.setAccounttype("0");
                    //ri.setBelongto("");
                    isSuccess = ri.insertHrmResource();
                    if (isSuccess) {
                        ri.deleteHrmResource5(id + "", status);
                    }
                } else {
                    //ri.setAccounttype("0");
                    //ri.setBelongto("");
                    isSuccess = ri.updateHrmResource();
                    if (isSuccess) {
                        ri.deleteHrmResource5(id + "", status);
                    }
                }

            }
            rc = new ResourceComInfo();
            rc.removeResourceCache();

            this.writeLog("synHrmresource end --- " + TimeUtil.getCurrentTimeString());

            //计算岗位下人数
            this.computePostPeopleCount();

        } catch (Exception e) {
            this.writeLog("synHrmresource Exception:" + e);
        } finally {
            // 关闭sap链接
            client.disconnect();
            sc.disConnection();
            client = null;
        }
        return logcontentFailure + logcontent + "提示消息： end --- " + "</br>";
    }

    private void computePostPeopleCount() {

        this.writeLog("computePostPeopleCount start --- " + TimeUtil.getCurrentTimeString());

        RecordSet rs = null;
        SapConnect sc = null;
        JCO.Client client = null;
        JCO.Repository repository = null;
        JCO.Function function = null;

        try {
            rs = new RecordSet();
            sc = new SapConnect();
            client = sc.getConnection();
            repository = new JCO.Repository("sap", client);
            IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_HR_OA_005");
            function = new JCO.Function(ft);
            //function.getImportParameterList().setValue("S", "OTYPE");// 获取组织
            client.execute(function);

            // 返回参数
            JCO.Table table = function.getTableParameterList().getTable("ZHRP_S");
            for (int i = 0; i < table.getNumRows(); i++) {
                table.setRow(i);

                String postcode = Util.null2String(table.getString("OBJID"));
                int postid = Util.getIntValue(Utils.getIdByCode("hrmjobtitles", postcode), 0);

                if (postid > 0) {
                    int peopleCount = 0;
                    rs.executeSql("select count(*) as peopleCount from hrmresource where status in (0,1,2,3) and jobtitle = " + postid);

                    if (rs.next()) {
                        peopleCount = Util.getIntValue(rs.getString("peopleCount"), 0);
                    }

                    rs.executeSql("update HrmJobTitles set peopleCount = " + peopleCount + " where id = " + postid);
                }
            }
            this.writeLog("computePostPeopleCount end --- " + TimeUtil.getCurrentTimeString());
        } catch (Exception e) {
            this.writeLog("computePostPeopleCount Exception:" + e);
        }
    }

    /**
     * 判断日期是否无效
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否无效
     */
    private Boolean isUseless(String startTime, String endTime) {
        boolean flag = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = format.parse(startTime);
            Date end = format.parse(endTime);
            Date nowTime = new Date();
            if (!(nowTime.after(start) && nowTime.before(end))) {
                //时间不符合
                flag = true;
            }
        } catch (Exception e) {
            this.writeLog("分部OR部门时间格式转换异常： " + e);
            this.writeLog("startTime: " + startTime + "; endTime: " + endTime);
        }
        return flag;
    }

}
