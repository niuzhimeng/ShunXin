<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ include file="/systeminfo/init_wev8.jsp" %>
<%@ page import="weaver.general.Util,
                 weaver.file.Prop,
                 weaver.login.Account,
								 weaver.login.VerifyLogin,
								 weaver.hrm.common.*,
                 weaver.general.GCONST" %>
<%@ page import="java.util.*" %>
<jsp:useBean id="pack" class="weaver.general.ParameterPackage" scope="page"/>
<jsp:useBean id="rs" class="weaver.conn.RecordSet" scope="page"/>
<jsp:useBean id="CompanyComInfo" class = "weaver.hrm.company.CompanyComInfo" scope = "page"/>
<jsp:useBean id="ResourceComInfo" class="weaver.hrm.resource.ResourceComInfo" scope="page"/>

	<%
	String titlename = "组织人员同步"; 
	%>
	<%@ include file="/systeminfo/TopTitle_wev8.jsp" %>
	<%@ include file="/systeminfo/RightClickMenuConent_wev8.jsp" %>
	<%@ include file="/systeminfo/RightClickMenu_wev8.jsp" %>
<%
HashMap<String,String> kv = (HashMap<String,String>)pack.packageParams(request, HashMap.class);
String _fromURL = Util.null2String(kv.get("_fromURL"));//来源
_fromURL = "OnlineUser";
String mouldid = "resource";

%>
<HTML><HEAD>
<script src="/js/tabs/jquery.tabs.extend_wev8.js"></script>
<SCRIPT language="javascript" src="/js/weaver_wev8.js"></script>
<SCRIPT language="javascript" src="/js/messagejs/simplehrm_wev8.js"></script>
<link type="text/css" href="/js/tabs/css/e8tabs1_wev8.css" rel="stylesheet" />
<link rel="stylesheet" href="/css/ecology8/request/searchInput_wev8.css" type="text/css" />
<script type="text/javascript" src="/js/ecology8/request/searchInput_wev8.js"></script>

<link rel="stylesheet" href="/css/ecology8/request/seachBody_wev8.css" type="text/css" />
<link rel="stylesheet" href="/css/ecology8/request/hoverBtn_wev8.css" type="text/css" />
<script type="text/javascript" src="/js/ecology8/request/hoverBtn_wev8.js"></script>
<script type="text/javascript" src="/js/ecology8/request/titleCommon_wev8.js"></script>

<script type="text/javascript">

function showMyTree(){
	jQuery("#e8_tablogo").click();
} 

var myCurrentindex = 0;
function setMyCurrentLi(){
	jQuery("li").removeClass("current");
	jQuery(jQuery("li")[myCurrentindex]).addClass("current");
}

function refreshTab(){
	jQuery('.flowMenusTd',parent.document).toggle();
	jQuery('.leftTypeSearch',parent.document).toggle();
} 

function onBtnSearchClick(){
	parent.location.reload();
}

var dialog = null;
function closeDialog(){
	if(dialog)
		dialog.close();
}
function viewHrm(id){
	var url ="";
	if(id=="1"){
		url = "/hrm/HrmTab.jsp?_fromURL=HrmSysAdminBasic&id="+id;
	}else{
		url = "/hrm/HrmTab.jsp?_fromURL=HrmResourceBase&id="+id;
	}
	openFullWindowForXtable(url,800,600);
}
function viewDepartment(departmentid) {
	openFullWindowForXtable("/hrm/HrmTab.jsp?_fromURL=HrmDepartmentDsp&id="+departmentid)
}

function viewSubCompany(subcompanyid) {
	openFullWindowForXtable("/hrm/HrmTab.jsp?_fromURL=HrmSubCompanyDsp&id="+subcompanyid)
}
</script>
<%
	LinkedHashMap<String,String> tabInfo = new LinkedHashMap<String,String>();
	int title = 0;
	boolean hasTree = false;//是否有树形导航
	boolean showDiv = true;
	String url = "";
	boolean isShowRewardsRecord = false;
	ShowTab tab = new ShowTab(rs,user);
	//if(_fromURL.equals("OnlineUser")){
		//前端--在线人员
		//hasTree = true;
		String departmentid=Util.null2String(request.getParameter("departmentid"));
	    String subcompanyid1=Util.null2String(request.getParameter("subcompanyid1"));
		url = "/app/sycn/SynchronizedataResult.jsp?subcompanyid1="+subcompanyid1+"&departmentid="+departmentid+"&companyid=1";
		//System.out.println("url="+url);
	//}
%>
<script type="text/javascript">
jQuery(function(){
    jQuery('.e8_box').Tabs({
        getLine:1,
        iframe:"tabcontentframe",
 		mouldID:"<%= MouldIDConst.getID(mouldid)%>",
        staticOnLoad:true,
		objName:"<%=titlename %>"
    });
});

function jsOnNavLogClick(){
	tabcontentframe.setUserIcon();
}
function changeOrg(id){
	window.location.href = "/hrm/HrmTab.jsp?_fromURL=<%=_fromURL%>&sorgid="+id;
}
</script>
</head>
<BODY scroll="no">
	<div class="e8_box demo2">
				<%if(showDiv){ %>
				<div class="e8_boxhead">
					<div class="div_e8_xtree" id="div_e8_xtree"></div>
			        <div class="e8_tablogo" id="e8_tablogo"></div>
					<div class="e8_ultab">
					<div class="e8_navtab" id="e8_navtab">
						<span id="objName"></span>
					</div>
				<div>
				<%} %>
		    <ul class="tab_menu">
		     <%if(hasTree){%>
					<li class="e8_tree">
						<a onClick="javascript:refreshTab();"></a>
					</li>
					<%} %>			
						<%if(showDiv){
				%>
					<li class="defaultTab">
						<a href="#" target="tabcontentframe" onClick="javascript:void('0')"><%=TimeUtil.getCurrentTimeString() %></a>
					</li>
		        <%} %>
		    </ul>
				    <div id="rightBox" class="e8_rightBox"></div>
			<%if(showDiv){ %>
	    		</div>
				</div>
			</div>
	    <%} %>
	    <div class="tab_box">
	    	<div>
       		<%
       		if(url.endsWith(".jsp")){
       			url +="?fromHrmTab=1";
       		}else{
       			url +="&fromHrmTab=1";
       		}
       		%>
	        <iframe src="<%=url %>" onload="update();" id="tabcontentframe" name="tabcontentframe" class="flowFrame" frameborder="0" height="100%" width="100%"></iframe>
	      </div>
	    </div>
	</div>     
</body>
<%if(_fromURL.equals("OnlineUser")){%>
<script type="text/javascript">
window.notExecute = true;
</script>
<%}%>
</html>

