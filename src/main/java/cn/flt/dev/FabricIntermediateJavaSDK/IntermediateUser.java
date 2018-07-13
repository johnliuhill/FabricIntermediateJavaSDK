/***********************************************************************
 *
 * @program: hft01
 * @description: 中间层表示-user
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/

package cn.flt.dev.FabricIntermediateJavaSDK;

import io.netty.util.internal.StringUtil;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.*;
import java.util.Set;

class IntermediateUser implements User, Serializable {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 变量
    
    private static final long serialVersionUID = 5695080465408336815L;

    /** 名称 */
    private String name;
    
    /** 规则 */
    private Set<String> roles;
    
    /** 账户 */
    private String account;
    
    /** 从属联盟 */
    private String affiliation;
    
    /** 组织 */
    private String organization;
    
    /** 注册操作的密密钥 */
    private String enrollmentSecret;
    
    /** 成员id */
    private String mspId;
    
    /** 注册登记操作 */
    private Enrollment enrollment = null;

    /** 存储配置对象 */
    private transient IntermediateStore intermediateStore;
    
    private String keyForFabricStoreName;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 接口

    /**************************************************************
     * IntermediateUser
     * Fabric网络用户对象
     *
     * @param name    用户名称（User1）
     * @param orgName 组织名称（Org1）
     * @param store   联盟存储配置对象
     * @return
     *
     * @author : John Liu
     */
    IntermediateUser(String name, String orgName, IntermediateStore store) {
        
        this.name = name;
        this.organization = orgName;
        this.intermediateStore = store;
        this.keyForFabricStoreName = getKeyForFabricStoreName(this.name, orgName);

        String memberStr = intermediateStore.getValue(keyForFabricStoreName);
        if (null != memberStr) {
            saveState();
        } else {
            restoreState();
        }
    }

    //////////////////////////////////////
    // account
    
    /**************************************************************
     * setAccount
     * 设置账户信息并将用户状态更新至存储配置对象
     *
     * @param account   账户
     * @return void
     *
     * @author : John Liu
     */
    void setAccount(String account) {
        
        this.account = account;
        saveState();
    }

    @Override
    public String getAccount() {
        return this.account;
    }


    //////////////////////////////////////
    // affiliation
    
    /**************************************************************
     * setAffiliation
     * 设置从属联盟信息并将用户状态更新至存储配置对象
     *
     * @param affiliation 从属联盟
     *                    
     * @author : John Liu
     */
    void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
        saveState();
    }
    
    @Override
    public String getAffiliation() {
        return this.affiliation;
    }


    //////////////////////////////////////
    // enrollment

    /**************************************************************
     * setEnrollment
     * 设置注册登记操作信息并将用户状态更新至存储配置对象
     *
     * @param enrollment    注册登记操作
     * @return void
     *
     * @author : John Liu
     */
    void setEnrollment(Enrollment enrollment) {
        
        this.enrollment = enrollment;
        saveState();
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }
    
    String getEnrollmentSecret() {
        return enrollmentSecret;
    }

    void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }

    //////////////////////////////////////
    // mspID

    /**************************************************************
     * setMspId
     * 设置成员id信息并将用户状态更新至存储配置对象
     *
     * @param mspID 成员id
     * @return void
     *
     * @author : John Liu
     */
    void setMspId(String mspID) {
        
        this.mspId = mspID;
        saveState();
    }

    @Override
    public String getMspId() {
        return this.mspId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    //////////////////////////////////////
    // roles

    /**************************************************************
     * setRoles
     * 设置角色规则信息并将用户状态更新至存储配置对象
     *
     * @param roles 规则
     * @return void
     *
     * @author : John Liu
     */
    void setRoles(Set<String> roles) {

        this.roles = roles;
        saveState();
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    //////////////////////////////////////

    boolean isRegistered() {
        return !StringUtil.isNullOrEmpty(enrollmentSecret);
    }

    boolean isEnrolled() {
        return this.enrollment != null;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**************************************************************
     * saveState
     * 将用户状态保存至存储配置对象
     *
     * @param
     * @return void
     *
     * @author : John Liu
     */
    void saveState() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            intermediateStore.setValue(keyForFabricStoreName, Hex.toHexString(bos.toByteArray()));
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**************************************************************
     * restoreState
     * 从键值存储中恢复该用户的状态(如果找到的话)。如果找不到，什么也不要做
     *
     * @param
     * @return void
     *
     * @author : John Liu
     */
    private void restoreState() {

        String memberStr = intermediateStore.getValue(keyForFabricStoreName);
        if (null != memberStr) {
            // 用户在键值存储中被找到，因此恢复状态
            byte[] serialized = Hex.decode(memberStr);
            ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
            try {
                ObjectInputStream ois = new ObjectInputStream(bis);
                IntermediateUser state = (IntermediateUser) ois.readObject();
                if (state != null) {
                    this.name = state.name;
                    this.roles = state.roles;
                    this.account = state.account;
                    this.affiliation = state.affiliation;
                    this.organization = state.organization;
                    this.enrollmentSecret = state.enrollmentSecret;
                    this.enrollment = state.enrollment;
                    this.mspId = state.mspId;
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("Could not restore state of member %s", this.name), e);
            }
        }
    }

    /**************************************************************
     * getKeyForFabricStoreName
     * 得到联盟存储配置对象key
     *
     * @param name 用户名称（User1）
     * @param org  组织名称（Org1）
     * @return 类似user.Org1User1.Org1
     *
     * @author : John Liu
     */
    static String getKeyForFabricStoreName(String name, String org) {

        System.out.println("toKeyValStoreName = " + "user." + name + org);
        return "user." + name + org;
    }

}
