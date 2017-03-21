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


}
