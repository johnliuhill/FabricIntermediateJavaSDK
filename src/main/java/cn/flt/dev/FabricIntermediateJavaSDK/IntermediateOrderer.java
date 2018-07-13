/***********************************************************************
 *
 * @program: hft01
 * @description: 中间层表示-orderer
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/


package cn.flt.dev.FabricIntermediateJavaSDK;

class IntermediateOrderer {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 变量

    /** orderer 域名 */
    private String ordererName;

    /** orderer 访问地址 */
    private String ordererLocation;

    /** orderer 证书文件 */
    private String ordererCryptoConfigPath;


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 接口
    IntermediateOrderer(String ordererName, String ordererLocation, String ordererCryptoConfigPath) {
        super();
        this.ordererName = ordererName;
        this.ordererLocation = ordererLocation;
        this.ordererCryptoConfigPath = ordererCryptoConfigPath;
    }

    String getOrdererName() {
        return ordererName;
    }

    void setOrdererLocation(String ordererLocation) {
        this.ordererLocation = ordererLocation;
    }

    String getOrdererLocation() {
        return ordererLocation;
    }

    String getOrdererCryptoConfigPath(){ return ordererCryptoConfigPath; }

}
