/***********************************************************************
 *
 * @program: hft01
 * @description: 中间层表示-chaincode
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/


package cn.flt.dev.FabricIntermediateJavaSDK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;


class IntermediateChaincode {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 变量

    private Logger log = LogManager.getLogger(IntermediateChaincode.class);

    /** chaincode名称 */
    private String chaincodeName; // mycc

    /** $GOPATH环境变量路径 一般情况是～/GOPATH */
    private String chaincodeSource; //

    /** chaincode安装路径 从chaincodeSource/src开始计算
     ** 也就是$GOPATH环境变量定义的Go语言的源码默认安装位置*/
    private String chaincodePath;   // github.com/hyperledger/fabric/xxx/chaincode/go/example/test

    /** chaincode版本号 */
    private String chaincodeVersion; // 1.0

    /** 指定ID的chaincode */
    private ChaincodeID chaincodeID;

    /** 单个提案请求的超时时间以毫秒为单位 */
    private int proposalWaitTime = 200000;

    /** 事务等待时间以秒为单位 */
    private int transactionWaitTime = 120;

    /** 部署等待时间以秒为单位 */
//    private int deployWatiTime = 120000;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 接口函数

    void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    void setChaincodeSource(String chaincodeSource) {
        this.chaincodeSource = chaincodeSource;
    }

    void setChaincodePath(String chaincodePath) {
        this.chaincodePath = chaincodePath;
    }

    void setChaincodeVersion(String chaincodeVersion) {
        this.chaincodeVersion = chaincodeVersion;
    }

    void setProposalWaitTime(int proposalWaitTime) {
        this.proposalWaitTime = proposalWaitTime;
    }

    void setTransactionWaitTime(int invokeWaitTime) {
        this.transactionWaitTime = invokeWaitTime;
    }

    void setChaincodeID() {
        if (null != chaincodeName && null != chaincodePath && null != chaincodeVersion) {
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // chaincode管理

    /**************************************************************
     * install 安装chaincode
     *
     * @param org
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    Map<String, String> install(IntermediateOrg org) throws ProposalException, InvalidArgumentException {


        /// Send transaction proposal to all peers
        InstallProposalRequest installProposalRequest = org.getClient().newInstallProposalRequest();
        installProposalRequest.setChaincodeName(chaincodeName);
        installProposalRequest.setChaincodeVersion(chaincodeVersion);
        installProposalRequest.setChaincodeSourceLocation(new File(chaincodeSource));
        installProposalRequest.setChaincodePath(chaincodePath);
        installProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        installProposalRequest.setProposalWaitTime(proposalWaitTime);

        long currentStart = System.currentTimeMillis();
        Collection<ProposalResponse> installProposalResponses = org.getClient().sendInstallProposal(installProposalRequest, org.getChannel().getChannel().getPeers());
        log.info("chaincode install transaction proposal time = " + (System.currentTimeMillis() - currentStart));
        return toPeerResponse(installProposalResponses, false);
    }

    /**************************************************************
     * instantiate 实例化chaincode
     *
     * @param org       org对象，一般是生成chaincode的org
     * @param args      实例化参数
     *
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    Map<String, String> instantiate(IntermediateOrg org, String[] args) throws ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {



        /// Send transaction proposal to all peers
        InstantiateProposalRequest instantiateProposalRequest = org.getClient().newInstantiationProposalRequest();
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setProposalWaitTime(proposalWaitTime);
        instantiateProposalRequest.setArgs(args);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("/code/src/policy/chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm2);

        long currentStart = System.currentTimeMillis();
        Collection<ProposalResponse> instantiateProposalResponses = org.getChannel().getChannel().sendInstantiationProposal(instantiateProposalRequest, org.getChannel().getChannel().getPeers());
        log.info("chaincode instantiate transaction proposal time = " + (System.currentTimeMillis() - currentStart));
        return toOrdererResponse(instantiateProposalResponses, org);
    }

    /**************************************************************
     * upgrade
     *
     * @param org       org对象，一般是生成chaincode的org
     * @param args      参数
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    Map<String, String> upgrade(IntermediateOrg org, String[] args) throws ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {


        /// Send transaction proposal to all peers
        UpgradeProposalRequest upgradeProposalRequest = org.getClient().newUpgradeProposalRequest();
        upgradeProposalRequest.setChaincodeID(chaincodeID);
        upgradeProposalRequest.setProposalWaitTime(proposalWaitTime);
        upgradeProposalRequest.setArgs(args);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("/code/src/policy/chaincodeendorsementpolicy.yaml"));
        upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "UpgradeProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        upgradeProposalRequest.setTransientMap(tm2);

        long currentStart = System.currentTimeMillis();
        Collection<ProposalResponse> upgradeProposalResponses = org.getChannel().getChannel().sendUpgradeProposal(upgradeProposalRequest, org.getChannel().getChannel().getPeers());
        log.info("chaincode instantiate transaction proposal time = " + (System.currentTimeMillis() - currentStart));
        return toOrdererResponse(upgradeProposalResponses, org);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // chaincode 使用接口

    /**************************************************************
     * invoke
     *
     * @param org       在那个org组织执行chaincode
     * @param fcn       方法名 如：set
     * @param args      方法参数
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    Map<String, String> invoke(IntermediateOrg org, String fcn, String[] args) throws InvalidArgumentException, ProposalException, IOException, InterruptedException, ExecutionException, TimeoutException {



        /// Send transaction proposal to all peers
        TransactionProposalRequest transactionProposalRequest = org.getClient().newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn(fcn);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(proposalWaitTime);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        transactionProposalRequest.setTransientMap(tm2);

        long currentStart = System.currentTimeMillis();
        Collection<ProposalResponse> transactionProposalResponses = org.getChannel().getChannel().sendTransactionProposal(transactionProposalRequest, org.getChannel().getChannel().getPeers());
        log.info("chaincode invoke transaction proposal time = " + (System.currentTimeMillis() - currentStart));
        return toOrdererResponse(transactionProposalResponses, org);
    }

    /**************************************************************
     * query
     *
     * @param org       在那个org组织执行chaincode
     * @param fcn       方法名 如：query
     * @param args      方法参数
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    Map<String, String> query(IntermediateOrg org, String fcn, String[] args) throws InvalidArgumentException, ProposalException {


        QueryByChaincodeRequest queryByChaincodeRequest = org.getClient().newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(fcn);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);
        queryByChaincodeRequest.setProposalWaitTime(proposalWaitTime);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm2);

        long currentStart = System.currentTimeMillis();
        Collection<ProposalResponse> queryProposalResponses = org.getChannel().getChannel().queryByChaincode(queryByChaincodeRequest, org.getChannel().getChannel().getPeers());
        log.info("chaincode query transaction proposal time = " + (System.currentTimeMillis() - currentStart));
        return toPeerResponse(queryProposalResponses, true);
    }

    /**************************************************************
     * toOrdererResponse
     * 获取实例化chaincode、升级chaincode以及invoke的返回结果集合
     * 
     * @param proposalResponses 请求返回集合
     * @param org
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    private Map<String, String> toOrdererResponse(Collection<ProposalResponse> proposalResponses, IntermediateOrg org) throws InvalidArgumentException, UnsupportedEncodingException, InterruptedException, ExecutionException, TimeoutException {

        Map<String, String> resultMap = new HashMap<>();
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        for (ProposalResponse response : proposalResponses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(proposalResponses);
        if (proposalConsistencySets.size() != 1) {
            log.error("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size());
        }
        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            log.error("Not enough endorsers for inspect:" + failed.size() + " endorser error: " + firstTransactionProposalResponse.getMessage() + ". Was verified: "
                    + firstTransactionProposalResponse.isVerified());
            resultMap.put("code", "error");
            resultMap.put("data", firstTransactionProposalResponse.getMessage());
            return resultMap;
        } else {
            log.info("Successfully received transaction proposal responses.");
            ProposalResponse resp = proposalResponses.iterator().next();
            log.debug("TransactionID: " + resp.getTransactionID());
            byte[] x = resp.getChaincodeActionResponsePayload();
            String resultAsString = null;
            if (x != null) {
                resultAsString = new String(x, "UTF-8");
            }
            log.info("resultAsString = " + resultAsString);
            org.getChannel().getChannel().sendTransaction(successful).get(transactionWaitTime, TimeUnit.SECONDS);
            resultMap.put("code", "success");
            resultMap.put("data", resultAsString);
            resultMap.put("txid", resp.getTransactionID());
            return resultMap;
        }
//        channel.sendTransaction(successful).thenApply(transactionEvent -> {
//            if (transactionEvent.isValid()) {
//                log.info("Successfully send transaction proposal to orderer. Transaction ID: " + transactionEvent.getTransactionID());
//            } else {
//                log.info("Failed to send transaction proposal to orderer");
//            }
//            // chain.shutdown(true);
//            return transactionEvent.getTransactionID();
//        }).get(chaincode.getInvokeWatiTime(), TimeUnit.SECONDS);
    }

    /**************************************************************
     * toPeerResponse
     * 获取安装chaincode以及query的返回结果集合
     *
     * @param proposalResponses 请求返回集合
     * @param checkVerified     是否验证提案
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    private Map<String, String> toPeerResponse(Collection<ProposalResponse> proposalResponses, boolean checkVerified) {


        Map<String, String> resultMap = new HashMap<>();
        for (ProposalResponse proposalResponse : proposalResponses) {
            if ((checkVerified && !proposalResponse.isVerified()) || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                String data = String.format("Failed install/query proposal from peer %s status: %s. Messages: %s. Was verified : %s",
                        proposalResponse.getPeer().getName(), proposalResponse.getStatus(), proposalResponse.getMessage(), proposalResponse.isVerified());
                log.debug(data);
                resultMap.put("code", "error");
                resultMap.put("data", data);
            } else {
                String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                log.debug("Install/Query payload from peer: " + proposalResponse.getPeer().getName());
                log.debug("TransactionID: " + proposalResponse.getTransactionID());
                log.debug("" + payload);
                resultMap.put("code", "success");
                resultMap.put("data", payload);
                resultMap.put("txid", proposalResponse.getTransactionID());
            }
        }
        return resultMap;
    }



}
