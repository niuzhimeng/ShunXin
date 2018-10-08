package com.shunxinkonggu.sap.sycn;

import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.job.JobActivitiesComInfo;
import weaver.hrm.job.JobTitlesComInfo;
import weaver.hrm.job.JobGroupsComInfo;
import weaver.hrm.resource.ResourceComInfo;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.general.BaseBean;

public class RemoveHrmresourceCache extends BaseCronJob {

	public void execute() {
		SubCompanyComInfo sc = null;
		DepartmentComInfo dc = null;
		JobGroupsComInfo jgc = null;
		JobActivitiesComInfo jac = null;
		JobTitlesComInfo jc = null;
		ResourceComInfo rc = null;
		try {

			sc = new SubCompanyComInfo();
			dc = new DepartmentComInfo();
			jgc = new JobGroupsComInfo();
			jac = new JobActivitiesComInfo();
			jc = new JobTitlesComInfo();
			rc = new ResourceComInfo();

			sc.removeCompanyCache();
			dc.removeCompanyCache();
			jgc.removeCompanyCache();
			jac.removeJobActivitiesCache();
			jc.removeJobTitlesCache();
			rc.removeResourceCache();

			BaseBean bb = new BaseBean();
			bb.writeLog("组织人员缓存更新成功！ RemoveHrmresourceCache ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
