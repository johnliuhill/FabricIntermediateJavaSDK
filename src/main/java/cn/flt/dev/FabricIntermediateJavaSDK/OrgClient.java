/***********************************************************************
 *
 * @program: hft01
 * @description: 客户端服务
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/

package cn.flt.dev.FabricIntermediateJavaSDK;

import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class OrgClient {

    private IntermediateOrg org;

    OrgClient(IntermediateOrg org) {
        this.org = org;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Chaincode 命令集

    /**************************************************************
     * install
     *
     * @param
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> install() throws ProposalException, InvalidArgumentException {

        return org.getChainCode().install(org);
    }

    /**************************************************************
     * instantiate
     *
     * @param args
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> instantiate(String[] args) throws ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {

        return org.getChainCode().instantiate(org, args);
    }

    /**************************************************************
     * upgrade
     *
     * @param args
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> upgrade(String[] args) throws ProposalException, InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {

        return org.getChainCode().upgrade(org, args);
    }

    /**************************************************************
     * invoke
     *
     * @param functionName
     * @param args
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> invoke(String functionName, String[] args) throws InvalidArgumentException, ProposalException, IOException, InterruptedException, ExecutionException, TimeoutException {

        return org.getChainCode().invoke(org, functionName, args);
    }

    /**************************************************************
     * query
     *
     * @param fcn
     * @param args
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> query(String fcn, String[] args) throws InvalidArgumentException, ProposalException {

        return org.getChainCode().query(org, fcn, args);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Channel 命令集

    /**************************************************************
     * queryBlockByTransactionID
     *
     * @param txID
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> queryBlockByTransactionID(String txID) throws ProposalException, IOException, InvalidArgumentException {

        return org.getChannel().queryBlockByTransactionID(txID);
    }

    /**************************************************************
     * queryBlockByHash
     *
     * @param blockHash
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> queryBlockByHash(byte[] blockHash) throws ProposalException, IOException, InvalidArgumentException {

        return org.getChannel().queryBlockByHash(blockHash);
    }

    /**
     * 在指定频道内根据区块高度查询区块
     *
     * @param blockNumber 区块高度
     */
    public Map<String, String> queryBlockByNumber(long blockNumber) throws ProposalException, IOException, InvalidArgumentException {
        return org.getChannel().queryBlockByNumber(blockNumber);
    }

    /**************************************************************
     * joinPeer
     *
     * @param peerName             当前指定的组织节点域名
     * @param peerEventHubName     当前指定的组织节点事件域名
     * @param peerLocation         当前指定的组织节点访问地址
     * @param peerEventHubLocation 当前指定的组织节点事件监听访问地址
     * @param isEventListener      当前peer是否增加Event事件处理
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> joinPeer(String peerName,
                                        String peerEventHubName,
                                        String peerLocation,
                                        String peerEventHubLocation,
                                        String peerCertificateFile,
                                        boolean isEventListener) throws ProposalException, InvalidArgumentException {

        return org.getChannel().joinPeer(new IntermediatePeer(peerName, peerEventHubName, peerLocation, peerEventHubLocation, peerCertificateFile, isEventListener));
    }

    /**************************************************************
     * getBlockchainInfo
     * 查询当前频道的链信息，包括链长度、当前最新区块hash以及当前最新区块的上一区块hash
     *
     * @param
     * @return java.util.Map<java.lang.String,java.lang.String>
     *
     * @author : John Liu
     */
    public Map<String, String> getBlockchainInfo() throws ProposalException, InvalidArgumentException {

        return org.getChannel().getBlockchainInfo();
    }
    
}