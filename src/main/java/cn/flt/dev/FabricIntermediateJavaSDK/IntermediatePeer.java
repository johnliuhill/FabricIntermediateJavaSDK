/***********************************************************************
 *
 * @program: hft01
 * @description: 中间层表示-peer
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/

package cn.flt.dev.FabricIntermediateJavaSDK;


class IntermediatePeer {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 变量

    /** peer域名 */
    private String peerName;

    /** peer事件域名 */
    private String peerEventHubName;

    /** peer访问地址 */
    private String peerLocation;

    /** peer事件监听访问地址 */
    private String peerEventHubLocation;

    /** peer 证书文件 */
    private String peerCryptoConfigPath;

    /** 当前peer是否增加Event事件处理 */
    private boolean addEventHub;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 接口

    /**************************************************************
     * IntermediatePeer
     * 初始化中继Peer对象
     *
     * @param peerName             当前指定的peer域名
     * @param peerEventHubName     当前指定的peer事件域名
     * @param peerLocation         当前指定的peer访问地址
     * @param peerEventHubLocation 当前指定的peer事件监听访问地址
     * @param peerCryptoConfigPath 当前指定的peer证书文件目录
     * @param isEventListener      当前peer是否增加Event事件处理
     * @author : John Liu
     */
    IntermediatePeer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation, String peerCryptoConfigPath, boolean isEventListener) {

        this.peerName = peerName;
        this.peerEventHubName = peerEventHubName;
        this.peerLocation = peerLocation;
        this.peerEventHubLocation = peerEventHubLocation;
        this.peerCryptoConfigPath = peerCryptoConfigPath;
        this.addEventHub = isEventListener;
    }

    String getPeerName() {
        return peerName;
    }
    String getPeerEventHubName() {
        return peerEventHubName;
    }

    String getPeerLocation() {
        return peerLocation;
    }
    void setPeerLocation(String peerLocation) {
        this.peerLocation = peerLocation;
    }

    String getPeerEventHubLocation() {
        return peerEventHubLocation;
    }
    void setPeerEventHubLocation(String peerEventHubLocation) { this.peerEventHubLocation = peerEventHubLocation; }

    String getPeerCryptoConfigPath(){ return peerCryptoConfigPath; }

    boolean isAddEventHub() {
        return addEventHub;
    }

}
