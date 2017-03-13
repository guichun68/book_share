package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;
import java.util.HashMap;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户逻辑实现类
 */

public class UserPresenter implements IUserPresenter {


    @Override
    public void loginByLoginName(String login_name, String password, final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("loginName",login_name);
        param.put("password",password);

        try {
           GlobalParams.getgModel().post(GlobalParams.urlLogin, param, new CallBack() {
               @Override
               public void onSuccess(Object obj) {
                   String response = (String)obj;
                   if(response.contains("<html>")){
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
    public String loginBySMS() {
        return null;
    }

    @Override
    public String regist() {
        return null;
    }

    @Override
    public String forgetPsw() {
        return null;
    }

    @Override
    public void sendLoginSMSVerifyCode(String phone,final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("phone",phone);
        try {
            GlobalParams.getgModel().post(GlobalParams.urlSmsLogin, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String response = (String)obj;
                    if(response.contains("<html>")){
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
            GlobalParams.getgModel().post(GlobalParams.urlRegist, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String response = (String)obj;
                    if(response.contains("<html>")){
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
            GlobalParams.getgModel().post(GlobalParams.urlGetUserInfo, param, new CallBack() {
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
}
