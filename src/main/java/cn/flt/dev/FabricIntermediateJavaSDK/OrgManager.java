
/***********************************************************************
 *
 * @program: hft01
 * @description: Org管理器
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/

package cn.flt.dev.FabricIntermediateJavaSDK;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.helper.Utils;

import javax.annotation.Nonnull;
import java.io.File;


public class OrgManager {

    private IntermediateOrg OrgObject;

    public OrgManager() {
        OrgObject = new IntermediateOrg();
        OrgObject.openTLS(true);
    }

    /////////////////////////////////////////////////////////////////////////////
    // 机构设置

    /**************************************************************
     * setOrgName 设置机构名字
     *
     * @param orgName   如：org0，org1
     * @return OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setOrgName(String orgName){

        OrgObject.setOrgName(orgName);
        return this;
    }
    /**************************************************************
     * setOrgMSPID
     * 设置MSPID 如：DevMSP
     *
     * @param orgMSPID
     * @return OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setOrgMSPID(String orgMSPID){

        OrgObject.setOrgMSPID(orgMSPID);
        return this;
    }

    /**************************************************************
     * setOrgDomainName
     * 设置域名 如：dev.flt.cn
     *
     * @param orgDomainName
     * @return OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setOrgDomainName(String orgDomainName){



        OrgObject.setOrgDomainName(orgDomainName);
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////
    // 用户设置

    /**************************************************************
     * setUser
     * 设置默认用户（一个特殊用户，即可对Channel及ChainCode进行操作的用户，一般为Admin；
     *
     * @param username              用户名，一般为机构的管理员Admin
     * @param userCryptoConfigPath  用户密钥证书文件目录，装有用户msp、tls两个文件夹的目录
     * @return OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setUser(@Nonnull String username, @Nonnull String userCryptoConfigPath) {

        OrgObject.setUsername(username);
        OrgObject.setUserCryptoConfigPath(userCryptoConfigPath);
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////
    // orderer设置

    /**************************************************************
     * setOrdererDomainName
     *
     * @param ordererDomainName     如：example.com，flt.cn
     * @return OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setOrdererDomainName(String ordererDomainName) {
        OrgObject.setOrdererDomainName(ordererDomainName);
        return this;
    }

    /**************************************************************
     * addOrderer
     * 增加orderer排序服务器
     *
     * @param ordererName               orderer hostname
     * @param ordererLocation           orderer 连接地址 如：grpc://orderer.flt.cn:7050
     * @param ordererCryptoConfigPath   orderer 证书文件目录，装有msp、tls两个文件夹的目录
     * @return cn.flt.dev.FabricIntermediateJavaSDK.OrgManager
     *
     * @author : John Liu
     */
    public OrgManager addOrderer(String ordererName,
                                 String ordererLocation,
                                 String ordererCryptoConfigPath) {

        OrgObject.addOrderer(ordererName, ordererLocation, ordererCryptoConfigPath);
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////
    // peer设置

    /**************************************************************
     * addPeer
     *
     * @param peerName                  peer domainname 如：peer0.dev.flt.cn
     * @param peerEventHubName          peer 事件处理hubname 如：peer0.dev.flt.cn
     * @param peerLocation              peer 连接地址 如：grpc://peer0.dev.flt.cn:7051
     * @param peerEventHubLocation      peer 事件处理地址 如：grpc://peer0.dev.flt.cn:7053
     * @param peerCryptoConfigPath      peer 证书文件目录，装有msp、tls两个文件夹的目录
     * @param isEventListener           事件监听设置
     * @return OrgManager
     *
     * @author : John Liu
     */
    public OrgManager addPeer(String peerName,
                              String peerEventHubName,
                              String peerLocation,
                              String peerEventHubLocation,
                              String peerCryptoConfigPath,
                              boolean isEventListener) {


        OrgObject.addPeer(peerName, peerEventHubName, peerLocation, peerEventHubLocation, peerCryptoConfigPath, isEventListener);
        return this;

    }

    /////////////////////////////////////////////////////////////////////////////
    // Channel设置

    /**************************************************************
     * setChannel
     * 设置频道
     *
     * @param channelName   如：mychannel，fltchannel
     * @return cn.flt.dev.FabricIntermediateJavaSDK.OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setChannel(String channelName) {


        IntermediateChannel channel = new IntermediateChannel();
        channel.setChannelName(channelName);
        OrgObject.setChannel(channel);
        return this;

    }

    /////////////////////////////////////////////////////////////////////////////
    // Chaincode设置

    /**************************************************************
     * setChainCode
     *
     * @param chaincodeName    chaincode名称，如：demo
     * @param ChaincodeSource  环境变量$GOPATH的路径
     * @param chaincodePath    chaincode路径，$GOPATH/src后的相对路径，链码必须放到$GOPATH/src中
     * @param chaincodeVersion chaincode版本，如：1.0
     * @param proposalWaitTime 单个提案请求的超时时间以毫秒为单位，如：100000
     * @param invokeWaitTime   事务等待时间以秒为单位,如：120
     *
     * @return cn.flt.dev.FabricIntermediateJavaSDK.OrgManager
     *
     * @author : John Liu
     */
    public OrgManager setChainCode(String chaincodeName,
                                   String ChaincodeSource,
                                   String chaincodePath,
                                   String chaincodeVersion,
                                   int proposalWaitTime,
                                   int invokeWaitTime) {

        IntermediateChaincode chaincode = new IntermediateChaincode();
        chaincode.setChaincodeName(chaincodeName);
        chaincode.setChaincodeSource(ChaincodeSource);
        chaincode.setChaincodePath(chaincodePath);
        chaincode.setChaincodeVersion(chaincodeVersion);
        chaincode.setProposalWaitTime(proposalWaitTime);
        chaincode.setTransactionWaitTime(invokeWaitTime);
        chaincode.setChaincodeID();
        OrgObject.setChainCode(chaincode);
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////
    // Location设置

    /**************************************************************
     * setOrdererAndPeerLocation
     * 设置orderer和peer节点
     *
     * @param
     * @return void
     *
     * @author : John Liu
     */
    public void setOrdererAndPeerLocation() {


        if (OrgObject.getPeers().size() == 0) {
            throw new RuntimeException("peers is null or peers size is 0");
        }
        if (OrgObject.getOrderers().size() == 0) {
            throw new RuntimeException("orderers is null or orderers size is 0");
        }
        if (OrgObject.getChainCode() == null) {
            throw new RuntimeException("chaincode must be instantiated");
        }

        // 根据TLS开启状态循环确认Peer节点各服务的请求grpc协议
        for (int i = 0; i < OrgObject.getPeers().size(); i++) {
            OrgObject.getPeers().get(i).setPeerLocation(grpcTLSify(OrgObject.openTLS(), OrgObject.getPeers().get(i).getPeerLocation()));
            OrgObject.getPeers().get(i).setPeerEventHubLocation(grpcTLSify(OrgObject.openTLS(), OrgObject.getPeers().get(i).getPeerEventHubLocation()));
        }
        // 根据TLS开启状态循环确认Orderer节点各服务的请求grpc协议
        for (int i = 0; i < OrgObject.getOrderers().size(); i++) {
            OrgObject.getOrderers().get(i).setOrdererLocation(grpcTLSify(OrgObject.openTLS(), OrgObject.getOrderers().get(i).getOrdererLocation()));
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    // 客户端初始化

    /**************************************************************
     * initOrgClient
     * org客户端初始化
     *
     * @param
     * @return cn.flt.dev.FabricIntermediateJavaSDK.OrgClient
     *
     * @author : John Liu
     */
    public OrgClient initOrgClient() throws Exception {

        IntermediateOrg org = OrgObject;
        File storeFile = new File(String.format("%s/HFCStore_%s@%s.properties", System.getProperty("java.io.tmpdir"), OrgObject.getUsername(), OrgObject.getOrgDomainName()));
        IntermediateStore intermediateStore = new IntermediateStore(storeFile);
        org.init(intermediateStore);
        org.setClient(HFClient.createNewInstance());
        org.getChannel().init(org);

        return new OrgClient(org);
    }


    /////////////////////////////////////////////////////////////////////////////
    // 其他私有函数

    /**************************************************************
     * grpcTLSify
     *
     * @param openTLS
     * @param location
     * @return java.lang.String
     *
     * @author : John Liu
     */
    private String grpcTLSify(boolean openTLS, String location) {

        location = location.trim();
        Exception e = Utils.checkGrpcUrl(location);
        if (e != null) {
            throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
        }
        return openTLS ? location.replaceFirst("^grpc://", "grpcs://") : location;

    }

    /**************************************************************
     * httpTLSify
     *
     * @param openCATLS
     * @param location
     * @return java.lang.String
     *
     * @author : John Liu
     */
    private String httpTLSify(boolean openCATLS, String location) {

        location = location.trim();
        return openCATLS ? location.replaceFirst("^http://", "https://") : location;
    }

}
