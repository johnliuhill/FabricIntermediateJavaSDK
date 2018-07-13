/***********************************************************************
 *
 * @program: FabricIntermediateJavaSDK
 * @description: SDK_Demo
 *
 * @author: John Liu
 * @create: 2018/06/26 15:39
 *
 ***********************************************************************/

package cn.flt.dev.Demo;

import java.util.*;
import cn.flt.dev.FabricIntermediateJavaSDK.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

//////////////////////////////////////////////////////////////////////////////////////////////////////////

public class SDK_Demo {

    public void DemoStartup() throws Exception{

        System.out.println("=========== SDK demo startup ===========\r\n");

        ClientDemo();
    }

    private void ClientDemo() throws Exception{

        try{

            OrgManager omDev = new OrgManager();

            // org
            omDev.setOrgName("Dev");
            omDev.setOrgMSPID("DevMSP");
            omDev.setOrgDomainName("dev.flt.cn");

            // user
            omDev.setUser("User1",
                    "/root/GOPATH/fltFirstChain/User1@dev.flt.cn"
            );

            // orderer
            omDev.setOrdererDomainName("flt.cn");
            omDev.addOrderer("orderer.flt.cn",
                    "grpc://orderer.flt.cn:7050",
                    "/root/GOPATH/fltFirstChain/orderer.flt.cn");

            // peer
            omDev.addPeer("peer0.dev.flt.cn",
                    "peer0.dev.flt.cn",
                    "grpc://peer0.dev.flt.cn:7051",
                    "grpc://peer0.dev.flt.cn:7053",
                    "/root/GOPATH/fltFirstChain/peer0.dev.flt.cn",
                    true);

            // channel
            omDev.setChannel("fltchannel");

            // chaincode
            omDev.setChainCode("demo",
                    "/root/GOPATH",
                    "github.com/cctest/demo",
                    "0.0.2",
                    90000,
                    120);

            // 设置orderer、peer
            omDev.setOrdererAndPeerLocation();


            ////////////////////////////////////////////////////////

            OrgClient oc = omDev.initOrgClient();

            //Map<String,String> invokeResult = oc.invoke("write", new String[]{"key4","456933333944444"});

            Map<String,String> queryResult = oc.query("query", new String[]{"key4"});


            System.out.println("\r\n=========== Client demo end ===========\r\n");
        }
        catch(Exception e){
            System.out.println(e.toString());
        }


    }

}