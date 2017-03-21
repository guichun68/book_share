package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户逻辑实现类
 */

public class UserPresenter extends IUserPresenter {


    @Override
    public void loginByLoginName(String login_name, String password, final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
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
                       GlobalParams.gUser = jsonObject.getObject("user", User.class);
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
        HashMap<String,Object> param = new HashMap<>();
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
        HashMap<String,Object> param = new HashMap<>();
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
            HashMap<String,Object> param = new HashMap<>();
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
        HashMap<String,Object> params = new HashMap<>();
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
        HashMap<String,Object> param = new HashMap<>();
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
}
