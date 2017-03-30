package zyzx.linke.presentation;

import zyzx.linke.base.IPresenter;
import zyzx.linke.model.CallBack;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户相关逻辑
 */

public abstract class IUserPresenter extends IPresenter{

    public abstract void loginByLoginName(String login_name, String password, CallBack viewCallBack);

    /**
     * 发送登录用的短信验证码
     */
    public abstract void sendLoginSMSVerifyCode(String phone,CallBack viewCallBack);

    /**
     * 找回密码页面发送短信验证码
     * @param phone
     * @param viewCallBack
     */
    public abstract void sendForgetPswSMSVerifyCode(String phone,CallBack viewCallBack);

    /**
     * 注册
     * @param userName
     * @param psw
     * @param phone
     */
    public abstract void regist(String userName, String psw, String phone,CallBack viewCallBack);

    /**
     * 获取用户信息
     * @param uid
     * @param callBack
     */
    public abstract void getUserInfo(String uid, CallBack callBack);
    /**
     * 上传头像
     */
    public abstract void uploadHeadIcon(Integer userId,String imagePath,CallBack viewCallBack);

    /**
     * 修改用户签名
     * @param userid
     * @param sig
     * @param callBack
     */
    public abstract void mofiySignature(Integer userid, String sig, CallBack callBack);


    public abstract void searchFriend(String keyWord, int pageNum,CallBack viewCallBack);

    /**
     * 得到所有好友
     * @param userId 要得到谁的好友列表
     * @param callBack
     */
    public abstract void getAllMyFriends(int userId, CallBack callBack);

    /**
     * 删除指定好友（联系人）
     * @param friendUserId 好友userId
     */
    public abstract void delFriend(Integer friendUserId,CallBack viewCallBack);

    /**
     * 添加指定用户到黑名单
     * @param userId 要把谁添加到我的黑名单
     * @param callBack
     */
    public abstract void addBlackList(String userId, CallBack callBack);

    /**
     * 检查自己是否已被对方加入黑名单了
     * @param userid 对方的userId
     * @param callBack
     */
    public abstract void checkIfInBlackList(Integer userid, CallBack callBack);

    /**
     * 发起添加userid为好友的请求
     * @param userid
     * @param callBack
     */
    public abstract void addFriend(Integer userid, CallBack callBack);

    /**
     * 得到用户信息（在本类getUserInfo方法基础上外加自己是否已被对方(userId)添加到黑名单等）
     * @param userId
     * @param callBack
     */
    public abstract void getUserInfoInConversation(String userId, CallBack callBack);

    /**
     * 验证短信验证码是否正确
     * @param verifyCode 短信验证码
     * @param type 验证码类型：1注册； 2忘记密码 ； 3短信登录
     * @param callBack
     */
    public abstract void verifySMSCode(String verifyCode, int userId,int type, CallBack callBack);
}
