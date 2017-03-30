package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import zyzx.linke.db.UserDao;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.utils.SharedPreferencesUtils;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户逻辑实现类
 */

public class UserPresenter extends IUserPresenter {
    private HashMap<String,Object> mParam;
    private HashMap<String,Object> getParam(){
        if(mParam==null){
            mParam = new HashMap<>();
        }
        mParam.clear();
        return mParam;
    }

    @Override
    public void loginByLoginName(String login_name, String password, final CallBack viewCallBack) {
        HashMap<String,Object> param = getParam();
        param.put("loginName",login_name);
        param.put("password",password);
        try {
           getModel().post(GlobalParams.urlLogin, param, new CallBack() {
               @Override
               public void onSuccess(Object obj) {
                   String response = (String)obj;
                   if(response.contains("</html>")){
                       if(viewCallBack!=null){
                           viewCallBack.onFailure("服务器错误，请检查URL");
                       }
                       return;
                   }
                   JSONObject jsonObject = JSON.parseObject(response);
                   int code = jsonObject.getInteger("code");
                   if(code == 200){
                       //登录成功
                       User u = jsonObject.getObject("user",User.class);
                       GlobalParams.gUser = u;
                       //记录用户名和uid
                       SharedPreferencesUtils.putString(SharedPreferencesUtils.LAST_LOGIN_NAME, u.getLogin_name());
                       SharedPreferencesUtils.putInt(SharedPreferencesUtils.USER_ID,u.getUserid());
                       SharedPreferencesUtils.putString(SharedPreferencesUtils.USER_PSW_Hash,u.getPassword());


                       if(viewCallBack!=null){
                           viewCallBack.onSuccess(true);
                       }
                   }else if(code==404){
                       if(viewCallBack!=null) {
                           viewCallBack.onFailure("无此用户");
                       }
                   }else if(viewCallBack!=null){
                           viewCallBack.onFailure("用户名或密码错误");
                   }
               }

               @Override
               public void onFailure(Object obj) {
                   if(viewCallBack!=null){
                       viewCallBack.onFailure("连接服务器失败");
                   }
               }
           });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendLoginSMSVerifyCode(String phone,final CallBack viewCallBack) {
        HashMap<String,Object> param = getParam();
        param.put("phone",phone);
        try {
            getModel().post(GlobalParams.urlSmsLogin, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String response = (String)obj;
                    if(response.toLowerCase().contains("</html>")){
                        if(viewCallBack!=null){
                            viewCallBack.onFailure("服务器错误，请检查URL");
                        }
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(response);
                    int code = jsonObject.getInteger("code");
                    if(code == 200){
                        GlobalParams.gVerifyCode = jsonObject.getInteger("verifyCode");
                        GlobalParams.gUser = jsonObject.getObject("user",User.class);
                    }else{
                        GlobalParams.gVerifyCode = 0;
                    }
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(code);
                    }
                }

                @Override
                public void onFailure(Object obj) {
                    UIUtil.showTestLog("zyzx","beijing");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void regist(String userName, String psw, String phone, final CallBack viewCallBack) {
        HashMap<String,Object> param = getParam();
        param.put("login_name",userName);
        param.put("password",psw);
        param.put("phone",phone);
        try {
            getModel().post(GlobalParams.urlRegist, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String response = (String)obj;
                    if(response.toLowerCase().contains("</html>")){
                        if(viewCallBack!=null){
                            viewCallBack.onFailure("服务器错误，请检查URL");
                        }
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(response);
                    int code = jsonObject.getInteger("code");
                    if(code == 200){
                        GlobalParams.gUser = jsonObject.getObject("user",User.class);
                    }else{
                        GlobalParams.gUser = null;
                    }
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(code);
                    }
                }

                @Override
                public void onFailure(Object obj) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void getUserInfo(String uid, final CallBack viewCallBack) {
        HashMap<String,Object> param = getParam();
        param.put("uid",uid);
        try {
            getModel().post(GlobalParams.urlGetUserInfo, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    if(obj != null){
                        if(viewCallBack!=null){
                            viewCallBack.onSuccess(obj);
                        }
                    }
                }

                @Override
                public void onFailure(Object obj) {
                    UIUtil.showTestLog("zyzx","根据uid获取用户信息失败！");
                    UIUtil.showTestLog("zyzx",obj.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadHeadIcon(Integer userId,String imagePath, CallBack viewCallBack) {
        HashMap<String,Object> params = getParam();
        params.put("head_icon",new File(imagePath));
        params.put("user_id",userId);
        try {
            getModel().post2(GlobalParams.urlUploadHeadIcon,params,viewCallBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null) {
                viewCallBack.onFailure("上传失败");
            }
        }
    }

    @Override
    public void mofiySignature(Integer userid, String sig, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id",userid+"");
        param.put("sig",sig);//个性签名
        try {
            getModel().post(GlobalParams.urlSetUserSig,param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure("设置失败");
            }
        }
    }

    @Override
    public void searchFriend(String keyWord, int pageNum,CallBack viewCallBack) {
        HashMap<String,Object> param = getParam();
        param.put("key_word",keyWord);
        param.put("page_num",String.valueOf(pageNum));
        try {
            getModel().post(GlobalParams.urlSearchFriend,param,viewCallBack);

        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null){
                viewCallBack.onFailure("查找失败");
            }
        }
    }

    @Override
    public void getAllMyFriends(int anInt, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id",String.valueOf(anInt));
        try {
            getModel().post(GlobalParams.urlGetFriends,param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure("未能获取好友信息");
            }
        }
    }

    @Override
    public void delFriend(Integer friendUserId,CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("owner_id",String.valueOf(GlobalParams.gUser.getUserid()));
        param.put("friend_id",String.valueOf(friendUserId));
        try {
            getModel().post(GlobalParams.urlDelFriend,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("删除失败!");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void addBlackList(String userId, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("from_user_id",String.valueOf(GlobalParams.gUser.getUserid()));
        param.put("to_user_id",userId);
        try {
            getModel().post(GlobalParams.urlAddBlackList,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("添加失败");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void checkIfInBlackList(Integer userid, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id",String.valueOf(GlobalParams.gUser.getUserid()));
        param.put("friend_id",String.valueOf(userid));

        try {
            getModel().post(GlobalParams.urlCheckIfIMInBlackList,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("检查失败");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void addFriend(Integer userid, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id",String.valueOf(GlobalParams.gUser.getUserid()));
        param.put("friend_id",String.valueOf(userid));
        try {
            getModel().post(GlobalParams.urlAddFriend,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("添加失败");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void getUserInfoInConversation(String userId, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id",String.valueOf(GlobalParams.gUser.getUserid()));
        param.put("friend_id",userId);
        try {
            getModel().post(GlobalParams.urlGetUserInfoInConversation,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("未能获取用户信息");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void sendForgetPswSMSVerifyCode(String phone, CallBack viewCallBack) {
        HashMap<String,Object> param = getParam();
        param.put("phone",phone);
        try {
            getModel().post(GlobalParams.urlForgetPSWSms,param,viewCallBack);
        } catch (IOException e) {
            if(viewCallBack!=null){
                viewCallBack.onFailure("验证码发送失败");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void verifySMSCode(String verifyCode, int userId,int type, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("verify_code",verifyCode);
        param.put("verify_type",String.valueOf(type));
        param.put("user_id",String.valueOf(userId));
        try {
            getModel().post(GlobalParams.urlVerifySMSCode,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("请求失败，请稍后重试!");
            }
            e.printStackTrace();
        }
    }
}
