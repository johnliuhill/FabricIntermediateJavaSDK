/***********************************************************************
 *
 * @program: hft01
 * @description: 中间层表示-org
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/


package cn.flt.dev.FabricIntermediateJavaSDK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


class IntermediateOrg {


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 变量
    
    private Logger log = LogManager.getLogger(IntermediateOrg.class);

    /** 执行SDK的Fabric用户名 */
    private String username;

    /** 用户密钥证书文件目录 */
    private String userCryptoConfigPath;

    /** orderer 排序服务器所在根域名 */
    private String ordererDomainName;

    /** orderer 排序服务器集合 */
    private List<IntermediateOrderer> orderers = new LinkedList<>();

    /** 当前指定的组织名称，如：Org1 */
    private String orgName;

    /** 当前指定的组织名称，如：Org1MSP */
    private String orgMSPID;

    /** 当前指定的组织所在根域名，如：org1.example.com */
    private String orgDomainName;

    /** orderer 排序服务器集合 */
    private List<IntermediatePeer> peers = new LinkedList<>();

    /** 是否开启TLS访问 */
    private boolean openTLS;

    /** 频道对象 */
    private IntermediateChannel channel;

    /** 智能合约对象 */
    private IntermediateChaincode chaincode;

    /** channel-artifacts所在路径 */
    private String channelArtifactsPath;

    private HFClient client;

    private Map<String, User> userMap = new HashMap<>();


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 接口
    
    void init(IntermediateStore intermediateStore) throws Exception {
        setPeerUser(intermediateStore);
    }

    
    /////////////////////////////
    // user
    String getUsername(){return username;}
    void setUsername(String username) {
        this.username = username;
    }
    
    String getUserCryptoConfigPath(){ return userCryptoConfigPath; }
    void setUserCryptoConfigPath(String userCryptoConfigPath){ this.userCryptoConfigPath = userCryptoConfigPath; }

    User getUser() {
        return userMap.get(username);
    }
    
    /////////////////////////////
    // orderer
    String getOrdererDomainName() {
        return ordererDomainName;
    }

    void setOrdererDomainName(String ordererDomainName) {
        this.ordererDomainName = ordererDomainName;
    }
    
    void addOrderer(String ordererName, String ordererLocation, String ordererCertificateFile) {
        orderers.add(new IntermediateOrderer(ordererName, ordererLocation, ordererCertificateFile));
    }

    List<IntermediateOrderer> getOrderers() {
        return orderers;
    }

    /////////////////////////////
    // org
    void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    
    void setOrgMSPID(String orgMSPID) {
        this.orgMSPID = orgMSPID;
    }

    String getOrgDomainName() {
        return orgDomainName;
    }
    void setOrgDomainName(String orgDomainName) {
        this.orgDomainName = orgDomainName;
    }

    /////////////////////////////
    // peer
    void addPeer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation, String peerCertificateFile,boolean isEventListener) {
        peers.add(new IntermediatePeer(peerName, peerEventHubName, peerLocation, peerEventHubLocation, peerCertificateFile, isEventListener));
    }

    List<IntermediatePeer> getPeers() {
        return peers;
    }

    /////////////////////////////
    // channel
    void setChannel(IntermediateChannel channel) {
        this.channel = channel;
    }

    IntermediateChannel getChannel() {
        return channel;
    }

    /////////////////////////////
    // chaincode
    void setChainCode(IntermediateChaincode chaincode) {
        this.chaincode = chaincode;
    }

    IntermediateChaincode getChainCode() {
        return chaincode;
    }

    /////////////////////////////
    // TLS
    void openTLS(boolean openTLS) {
        this.openTLS = openTLS;
    }
    
    boolean openTLS() {
        return openTLS;
    }


    /////////////////////////////
    // client
    void setClient(HFClient client) throws CryptoException, InvalidArgumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.client = client;
        log.debug("Create instance of HFClient");
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        log.debug("Set Crypto Suite of HFClient");
    }

    HFClient getClient() {
        return client;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**************************************************************
     * setPeerUser
     * 设置peer用户各类密钥文件
     *
     * @param intermediateStore
     * @return void
     *
     * @author : John Liu
     */
    private void setPeerUser(IntermediateStore intermediateStore) throws IOException {

        File skFile = findSKFile(Paths.get(userCryptoConfigPath,"/msp/keystore").toFile());
        File certificateFile = Paths.get( userCryptoConfigPath, String.format("/msp/signcerts/%s@%s-cert.pem", username, orgDomainName) ).toFile();
        log.debug("skFile = " + skFile.getAbsolutePath());
        log.debug("certificateFile = " + certificateFile.getAbsolutePath());
        // 一个特殊的用户，可以创建通道，连接对等点，并安装链码
        addUser(intermediateStore.getMember(username, orgName, orgMSPID, skFile, certificateFile));
    }

    private void addUser(IntermediateUser user) {
        userMap.put(user.getName(), user);
    }

    /**************************************************************
     * findSKFile
     * 从指定路径中获取后缀为 _sk 的文件，且该路径下有且仅有该文件
     *
     * @param directory     指定路径
     * @return java.io.File
     *
     * @author : John Liu
     */
    private File findSKFile(File directory) {
        
        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
        if (null == matches) {
            throw new RuntimeException(String.format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(String.format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }
}