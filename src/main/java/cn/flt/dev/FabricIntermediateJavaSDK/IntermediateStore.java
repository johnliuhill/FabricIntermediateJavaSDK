/***********************************************************************
 *
 * @program: hft01
 * @description: 中间层表示-store
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/


package cn.flt.dev.FabricIntermediateJavaSDK;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.*;
import java.security.PrivateKey;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class IntermediateStore {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 变量
    
    private String file;
    /**
     * 用户信息集合
     */
    private final Map<String, IntermediateUser> members = new HashMap<>();

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 接口
    
    /**************************************************************
     * IntermediateStore
     *
     * @param file  用户信息存储文件
     * @return
     *
     * @author : John Liu
     */
    IntermediateStore(File file) {

        this.file = file.getAbsolutePath();
    }

    /**************************************************************
     * setValue
     * 设置键值对
     *
     * @param name      key name
     * @param value     value
     * @return void
     *
     * @author : John Liu
     */
    void setValue(String name, String value) {
        
        Properties properties = loadProperties();
        try (OutputStream output = new FileOutputStream(file)) {
            properties.setProperty(name, value);
            properties.store(output, "");
        } catch (IOException e) {
            System.out.println(String.format("Could not save the key-value store, reason:%s", e.getMessage()));
        }
    }

    /**************************************************************
     * getValue
     *
     * @param name      key name
     * @return java.lang.String
     *
     * @author : John Liu
     */
    String getValue(String name) {
        
        Properties properties = loadProperties();
        return properties.getProperty(name);
    }

    /**************************************************************
     * getMember
     * 用给定的名称获取用户
     *
     * @param name    用户名称（User1）
     * @param orgName 组织名称（Org1）
     * @return 用户
     *
     * @author : John Liu
     */
    IntermediateUser getMember(String name, String orgName) {
        // 尝试从缓存中获取User状态
        IntermediateUser user = members.get(IntermediateUser.getKeyForFabricStoreName(name, orgName));
        if (null != user) {
            return user;
        }
        // 创建User，并尝试从键值存储中恢复它的状态(如果找到的话)
        user = new IntermediateUser(name, orgName, this);
        members.put(IntermediateUser.getKeyForFabricStoreName(name, orgName), user);
        return user;
    }

    /**************************************************************
     * getMember
     * 用给定的名称获取用户
     *
     * @param name            用户名称（User1）
     * @param org             组织名称（Org1）
     * @param mspId           组织 MSPID
     * @param privateKeyFile  用户私钥
     * @param certificateFile 用户证书
     * @return user 用户
     *
     * @author : John Liu
     */
    IntermediateUser getMember(String name, String org, String mspId, File privateKeyFile, File certificateFile) throws IOException {

        // 尝试从缓存中获取User状态
        IntermediateUser user = members.get(IntermediateUser.getKeyForFabricStoreName(name, org));
        if (null != user) {
            System.out.println("Try get user status from fabric store, User = " + user);
            return user;
        }

        // 创建User，并尝试从键值存储中恢复它的状态(如果找到的话)
        user = new IntermediateUser(name, org, this);
        user.setMspId(mspId);
        String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
        PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
        user.setEnrollment(new StoreEnrollment(privateKey, certificate));
        user.saveState();
        members.put(IntermediateUser.getKeyForFabricStoreName(name, org), user);
        return user;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Could not find the file \"%s\"", file));
        } catch (IOException e) {
            System.out.println(String.format("Could not load key-value store from file \"%s\", reason:%s", file, e.getMessage()));
        }
        return properties;
    }



    /**************************************************************
     * getPrivateKeyFromBytes
     * 通过字节数组信息获取私钥
     *
     * @param data  字节数组
     * @return java.security.PrivateKey
     *
     * @author : John Liu
     */
    private PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {

        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        return privateKey;
    }


    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**************************************************************
     * 自定义注册登记操作类
     *
     * @author : John Liu
     */
    static final class StoreEnrollment implements Enrollment, Serializable {


        private static final long serialVersionUID = 6965341351799577442L;

        /** 私钥 */
        private final PrivateKey privateKey;

        /** 授权证书 */
        private final String certificate;

        StoreEnrollment(PrivateKey privateKey, String certificate) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        @Override
        public PrivateKey getKey() {
            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }
    }

}
