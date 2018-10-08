package com.shunxinkonggu.sap.sycn;

import weaver.interfaces.schedule.BaseCronJob;
import com.shunxinkonggu.sap.sycn.SyncHrmResource;

public class QuartzSyncHrmResource extends BaseCronJob {
	
	public void execute() {
		
		SyncHrmResource hrmResource = new SyncHrmResource();
		
		hrmResource.synSubcompany();
		
		hrmResource.synDepartment();
		
		hrmResource.synJobGroups();
		
		hrmResource.synActivities();
		
		hrmResource.synJobtitle();
		
		hrmResource.synHrmresource();
		
	}
}
