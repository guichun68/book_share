package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.FeedBack;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.StringUtil;
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
//        Log.e("zzyy",login_name+"-->");
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
                       UserVO u = jsonObject.getObject("user",UserVO.class);
                       GlobalParams.saveUser(u);
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
                       viewCallBack.onFailure("连接服务器失败,请检查网络连接");
                   }
               }
           });
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null){
                viewCallBack.onFailure("请求出错，请稍后重试！");
            }
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
                        UserVO userVO = jsonObject.getObject("user",UserVO.class);
                        PreferenceManager.getInstance().saveLastLoginUser(userVO);
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
                        GlobalParams.saveUser(jsonObject.getObject("user",UserVO.class));
                    }else{
                        GlobalParams.saveUser(null);
                    }
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(code);
                    }
                }

                @Override
                public void onFailure(Object obj) {
                    if(viewCallBack!=null){
                        viewCallBack.onFailure("网络错误，请稍后重试！");
                    }
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
    public void getAllMyFriends(final EMValueCallBack<List<EaseUser>> callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id", EMClient.getInstance().getCurrentUser());
        try {
            getModel().post(GlobalParams.urlGetFriends, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String json = (String) obj;
                    if (StringUtil.isEmpty(json)) {
                        UIUtil.showToastSafe("未能获取好友信息");
                        if(callBack!=null){
                            callBack.onError(500,"未能获取好友信息");
                        }
                        return;
                    }
                    List<UserVO> friends = JSON.parseArray(json, UserVO.class);
                    List<EaseUser> easeUsers = new ArrayList<>();
                    for (int i = 0; i < friends.size(); i++) {
                        EaseUser u2 = new EaseUser(String.valueOf(friends.get(i).getUserid()));
                        u2.setAvatar(friends.get(i).getHead_icon());
                        u2.setNickname(friends.get(i).getLogin_name());
                        easeUsers.add(u2);
                    }
                    EaseUIHelper.getInstance().saveContactList(easeUsers);
                    if(callBack!=null){
                        callBack.onSuccess(easeUsers);
                    }
                }

                @Override
                public void onFailure(Object obj) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onError(500,"未能获取好友信息");
            }
        }
    }

    @Override
    public void delFriend(Integer friendUserId,CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("owner_id",String.valueOf(GlobalParams.getLastLoginUser().getUserid()));
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
        param.put("from_user_id",String.valueOf(GlobalParams.getLastLoginUser().getUserid()));
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
        param.put("user_id",String.valueOf(GlobalParams.getLastLoginUser().getUserid()));
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
        param.put("user_id",String.valueOf(GlobalParams.getLastLoginUser().getUserid()));
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
        param.put("user_id",String.valueOf(GlobalParams.getLastLoginUser().getUserid()));
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
        HashMap<String, Object> param = getParam();
        param.put("verify_code", verifyCode);
        param.put("verify_type", String.valueOf(type));
        param.put("user_id", String.valueOf(userId));
        try {
            getModel().post(GlobalParams.urlVerifySMSCode, param, callBack);
        } catch (IOException e) {
            if (callBack != null) {
                callBack.onFailure("请求失败，请稍后重试!");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void resetPsw(String userId,String newPsw, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("new_psw",newPsw);
        param.put("user_id",String.valueOf(userId));
        try {
            getModel().post(GlobalParams.urlResetPsw,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("请求失败，请稍后重试!");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void modifyPsw(Integer userid, String oldPsw, String newPsw, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("user_id",String.valueOf(userid));
        param.put("old_psw",oldPsw);
        param.put("new_psw",newPsw);
        try {
            getModel().post(GlobalParams.urlModifyPsw,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure(UIUtil.getString(R.string.err_request));
            }
            e.printStackTrace();
        }
    }

    @Override
    public void feedBack(FeedBack mFeedBack, CallBack callBack) {
        HashMap<String,Object> param = getParam();
        param.put("content",JSON.toJSONString(mFeedBack));
        try {
            getModel().post(GlobalParams.urlFeedBack,param,callBack);
        } catch (IOException e) {
            if(callBack!=null){
                callBack.onFailure("提交失败，请重试！");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void uploadExcelFile(Integer userId, String filePath, CallBack viewCallBack) {
        HashMap<String,Object> params = getParam();
        params.put("file",new File(filePath));
        params.put("user_id",userId);
        try {
            getModel().post2(GlobalParams.urlUploadExcel, params, viewCallBack);
        }catch (Exception e){
            if(viewCallBack!=null){
                viewCallBack.onFailure("导入失败！");
                UIUtil.showTestLog("zyzx",e.getMessage());
            }
        }
    }
}
