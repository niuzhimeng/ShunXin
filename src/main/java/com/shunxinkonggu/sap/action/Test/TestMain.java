package com.shunxinkonggu.sap.action.Test;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.shunxinkonggu.sap.util.SapConnectPoolBatch;

public class TestMain {

    public void test() {
        SapConnectPoolBatch connect = new SapConnectPoolBatch();
        JCO.Client client = connect.getConnection();
        JCO.Repository repository = new JCO.Repository("sap", client);
        IFunctionTemplate ft = repository.getFunctionTemplate("ZRFC_FI_LIFNR_CHECK_B");
        JCO.Function function = new JCO.Function(ft);
        System.out.println(function);
    }
}
