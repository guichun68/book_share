package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;
import java.util.HashMap;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.IModel;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.BeanFactoryUtil;
import zyzx.linke.utils.GlobalParams;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户逻辑实现类
 */

public class UserPresenter implements IUserPresenter {

    private IModel model;

    private IModel getModel(){
        if(model !=null){
            return model;
        }
        return model = BeanFactoryUtil.getImpl(IModel.class);
    }

    @Override
    public void loginByLoginName(String login_name, String password, final CallBack viewCallBack) {
        HashMap<String,String> param = new HashMap<>();
        param.put("loginName",login_name);
        param.put("password",password);

        try {
           getModel().post(GlobalParams.urlLogin, param, new CallBack() {
               @Override
               public void onSuccess(Object obj) {
                   String response = (String)obj;
                   if(response.contains("<html>")){
                       if(viewCallBack!=null){
                           viewCallBack.onFailure("服务器错误");
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
                   }else{
                       if(viewCallBack!=null){
                           viewCallBack.onFailure("用户名或密码错误");
                       }
                   }
               }

               @Override
               public void onFailure(Object obj) {
                   if(viewCallBack!=null){
                       viewCallBack.onFailure(false);
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
        HashMap<String,String> param = new HashMap<>();
        param.put("phone",phone);
        try {
            getModel().post(GlobalParams.urlSmsLogin, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String response = (String)obj;
                    if(response.contains("<html>")){
                        if(viewCallBack!=null){
                            viewCallBack.onFailure("服务器错误");
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

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
