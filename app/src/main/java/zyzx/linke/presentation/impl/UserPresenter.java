package zyzx.linke.presentation.impl;

import android.util.ArrayMap;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.FeedBack;
import zyzx.linke.model.bean.ResponseJson;
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
    private ArrayMap<String,Object> mParam;
    private ArrayMap<String,Object> getParam(){
        if(mParam==null){
            mParam = new ArrayMap<>();
        }
        mParam.clear();
        return mParam;
    }

    @Override
    public void loginByLoginName(String loginName, String password, final CallBack viewCallBack) {
        getDataWithPost(new CallBack() {
                            @Override
                            public void onSuccess(Object obj, int... code) {
                                String response = (String)obj;
                                ResponseJson rj = new ResponseJson(response);
                                if(ResponseJson.NO_DATA == rj.errorCode){
                                    Log.e("failure","登录失败");
                                    return;
                                }
                                switch (rj.errorCode){
                                    case 1:
                                        //登录成功
                                        UserVO u = new UserVO();
                                        Iterator<Object> it = rj.data.iterator();
                                        while(it.hasNext()){
                                            JSONObject jo = (JSONObject) it.next();
                                            u.setErrorCode(rj.errorCode);
                                            u.setCreditScore(jo.getInteger("credit_socre"));
                                            u.setLoginName(jo.getString("loginName"));
                                            String tempUrl = jo.getString("photo");
                                            String headUrl = null;
                                            if(!StringUtil.isEmpty(tempUrl))
                                                headUrl= GlobalParams.BASE_URL+GlobalParams.AvatarDirName+tempUrl;
                                            u.setHeadIcon(headUrl);
                                            u.setSignature(jo.getString("sig"));
                                            u.setPassword(jo.getString("psw"));
                                            u.setRealName(jo.getString("realName"));
                                            u.setUrl(jo.getString("url"));
                                            u.setUserid(jo.getInteger("userid"));
                                            u.setUid(jo.getString("uid"));
                                        }
                                        GlobalParams.saveUser(u);
                                        EMClient.getInstance().getOptions().setAutoLogin(true);
                                        if(viewCallBack!=null){
                                            viewCallBack.onSuccess(true);
                                        }
                                        break;

                                    case 0:
                                        Log.e("failure","登录失败");
                                        if(viewCallBack!=null) {
                                            viewCallBack.onFailure(rj.errorMsg);
                                        }
                                        break;
                                    default:
                                        if(viewCallBack!=null){
                                            viewCallBack.onFailure("登录错误" );
                                        }
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Object obj, int... code) {
                                if(viewCallBack!=null){
                                    viewCallBack.onFailure("连接服务器失败,请检查网络连接");
                                }
                            }
                        },GlobalParams.urlLogin,"请求出错，请稍后重试！",
                new String[]{"loginName","password"},
                loginName,password);
    }

    @Override
    public void sendLoginSMSVerifyCode(String phone,final CallBack viewCallBack) {
        getDataWithPost(new CallBack() {
                            @Override
                            public void onSuccess(Object obj, int... code) {
                                String response = (String)obj;
                                JSONObject jsonObject = JSON.parseObject(response);
                                int code2 = jsonObject.getInteger("code");
                                if(code2 == 200){
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
                            public void onFailure(Object obj, int... code) {
                                UIUtil.showTestLog("zyzx","beijing");
                            }
                        },GlobalParams.urlSmsLogin,"发送失败",
                new String[]{"phone"},
                phone);
    }

    @Override
    public void regist(String userName, String psw, String phone, final CallBack viewCallBack) {
        getDataWithPost(new CallBack() {
                            @Override
                            public void onSuccess(Object obj, int... code) {
                                String response = (String)obj;
                                ResponseJson rj = new ResponseJson(response);
                                if(rj.errorCode == 0 ){
                                    if(viewCallBack!=null){
                                        viewCallBack.onSuccess(rj.errorCode);
                                    }
                                }else{
                                    if(viewCallBack !=null){
                                        viewCallBack.onFailure(rj.errorMsg,rj.errorCode);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Object obj, int... code) {
                                if(viewCallBack!=null){
                                    viewCallBack.onFailure(UIUtil.getString(R.string.network_error));
                                }
                            }
                        },GlobalParams.urlRegist,"操作失败",
                new String[]{"loginName","password","phone"},
                userName,psw,phone);
    }

    @Override
    public void getUserInfoByUserId(String userid, final CallBack viewCallBack) {
        String url = GlobalParams.urlGetUserInfoByUserId.replace("#",userid);
        getModel().get(url, null, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                if(obj != null){
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(obj);
                    }
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                UIUtil.showTestLog("zyzx",obj.toString());
                if(viewCallBack!=null) {
                    viewCallBack.onFailure("未能获取用户信息");
                }
            }
        });
    }

    @Override
    public void getUserInfoByUid(String uid, final CallBack viewCallBack) {
        String url = GlobalParams.urlGetUserInfoByUid.replace("#",uid);
        getModel().get(url, null, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                if(obj != null){
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(obj);
                    }
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                if(viewCallBack!=null) {
                    viewCallBack.onFailure("未能获取用户信息");
                }
            }
        });
    }

    @Override
    public void uploadHeadIcon(Integer userId,String imagePath, CallBack viewCallBack) {
        ArrayMap<String, Object> params = getParam();
        params.put("img", new File(imagePath));
        params.put("user_id", userId);
        try {
            getModel().sendMultipart(GlobalParams.urlUploadHeadIcon, params, viewCallBack);
        } catch (Exception e) {
            e.printStackTrace();
            if (viewCallBack != null) {
                viewCallBack.onFailure("上传失败");
            }
        }
    }

    @Override
    public void mofiySignature(Integer userid, String sig, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSetUserSig,"设置失败!",
                new String[]{"user_id","sig"},
                userid,sig);
    }

    @Override
    public void searchFriend(String keyWord, int pageNum,CallBack viewCallBack) {
        getDataWithPost(viewCallBack,GlobalParams.urlSearchFriend,"查找失败!",
                new String[]{"key_word","page_num"},
                keyWord,pageNum);
    }

    @Override
    public void getAllMyContacts(final EMValueCallBack<List<EaseUser>> callBack) {
        ArrayMap<String,Object> param = getParam();
        param.put("user_id", EMClient.getInstance().getCurrentUser());
        try {
            getModel().post(GlobalParams.urlGetFriends, param, new CallBack() {
                @Override
                public void onSuccess(Object obj, int... code) {
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
                        u2.setAvatar(friends.get(i).getHeadIcon());
                        u2.setNickname(friends.get(i).getLoginName());
                        easeUsers.add(u2);
                    }
                    EaseUIHelper.getInstance().saveContactList(easeUsers);
                    if(callBack!=null){
                        callBack.onSuccess(easeUsers);
                    }
                }

                @Override
                public void onFailure(Object obj, int... code) {

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
        getDataWithPost(callBack,GlobalParams.urlDelFriend,"删除失败!",
                new String[]{"owner_id","friend_id"},
                GlobalParams.getLastLoginUser().getUserid(),friendUserId);
    }

    @Override
    public void addBlackList(String userId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlAddBlackList,"添加失败!",
                new String[]{"from_user_id","to_user_id"},
                GlobalParams.getLastLoginUser().getUserid(),userId);
    }

    @Override
    public void checkIfInBlackList(Integer userId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlCheckIfIMInBlackList,"检查失败!",
                new String[]{"user_id","friend_id"},
                GlobalParams.getLastLoginUser().getUserid(),userId);
    }

    @Override
    public void addFriend(Integer userid, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlAddFriend,"添加失败!",
                new String[]{"user_id","friend_id"},
                GlobalParams.getLastLoginUser().getUserid(),userid);
    }

    @Override
    public void getUserInfoInConversation(String userId, CallBack callBack) {
       String url = GlobalParams.urlGetUserInfoByUserId.replace("#",userId);
       getModel().get(url,null,callBack);
    }

    @Override
    public void sendForgetPswSMSVerifyCode(String phone, CallBack viewCallBack) {
        getDataWithPost(viewCallBack,GlobalParams.urlForgetPSWSms,"验证码发送失败!",
                new String[]{"phone"},
                phone);
    }

    @Override
    public void verifySMSCode(String verifyCode, int userId,int type, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlVerifySMSCode,"请求失败，请稍后重试!",
                new String[]{"verify_code","verify_type","user_id"},
                verifyCode,type,userId);
    }

    @Override
    public void resetPsw(String userId,String newPsw, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlResetPsw,"请求失败，请稍后重试!",
                new String[]{"new_psw","user_id"},
                newPsw,userId);
    }

    @Override
    public void modifyPsw(String uid, String oldPsw, String newPsw, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlModifyPsw,UIUtil.getString(R.string.err_request),
                new String[]{"uid","old_psw","new_psw"},
                uid,oldPsw,newPsw);
    }

    @Override
    public void feedBack(FeedBack mFeedBack, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlFeedBack,"提交失败，请重试！",
                new String[]{"content"},
                JSON.toJSONString(mFeedBack));
    }

    @Override
    public void uploadExcelFile(String userId, String filePath, CallBack viewCallBack) {
        ArrayMap<String,Object> params = getParam();
        params.put("file",new File(filePath));
        params.put("user_id",userId);
        try {
            getModel().sendMultipart(GlobalParams.urlUploadExcel, params, viewCallBack);
        }catch (Exception e){
            if(viewCallBack!=null){
                viewCallBack.onFailure("导入失败！");
                UIUtil.showTestLog("zyzx",e.getMessage());
            }
        }
    }

    @Override
    public void getSubArea(Integer pid, Integer holdFlag, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSubArea,"访问出错，请稍后再试.",
                new String[]{"pid","hold"},
                pid,holdFlag);
    }

    @Override
    public void saveUserInfo(UserVO user, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSaveUserInfo,"访问出错，请稍后再试.",
                new String[]{"user"},
                JSON.toJSONString(user));
    }

    @Override
    public void loginByThirdPlatform(String paramJSON,CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlthirdLogin,"访问出错，请稍后再试.",
                new String[]{"param"},
                paramJSON);
    }

    @Override
    public void shareBook(String shareJson, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlShareBook,"访问出错，请稍后再试.",
                new String[]{"param"},
                shareJson);
    }

    @Override
    public void getSharerArea(Integer shareAreaId, final CallBack viewCallBack) {
        String url = GlobalParams.urlGetAreaById.replace("#",shareAreaId+"");
        getModel().get(url, null, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                if(obj != null){
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(obj);
                    }
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                UIUtil.showTestLog("zyzx",obj.toString());
                if(viewCallBack!=null) {
                    viewCallBack.onFailure("未能获取地理信息");
                }
            }
        });
    }

    @Override
    public void getSharerArea2BookStatus(Integer shareAreaId, String userBookId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetArea2BookStatusId,"未能获取书籍状态",
                new String[]{"areaId","userBookId"},shareAreaId,userBookId);
    }

    @Override
    public void getAllShareBooks(String pro, String city, String county, int pageNo, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSharedBooks,"请求出错.",
                new String[]{"pro","city","county","pageNo"},
                pro,city,county,pageNo);
    }

    @Override
    public void sendBegBookMsg(Integer shareType,UserVO user, Integer relUserId, BookDetail2 book, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSendBegMsg,"请求出错.",
                new String[]{"userId","relUserId","bookId","bookTitle","shareType","uid","headIcon","nickName"},
                user.getUserid(),relUserId,book.getId(),book.getTitle(),shareType,user.getUid(),user.getHeadIcon(),user.getLoginName());
    }

    @Override
    public void getAllBorrowBegs(Integer userId,int pageNo,CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetBookBorrowBegs,"获取出错",
                new String[]{"userId","pageNo"},
                userId,pageNo);
    }

    @Override
    public void setBorrowFlowstatus(String userBookId,String currentUser, String chatUserId, String bookId, int status, CallBack callBack) {
        if(status==Const.BORROW_BORROWER_REPLY_AGREE){//借阅者-约会已同意
            getDataWithPost(callBack,GlobalParams.urlSetFollowStauts,"更改状态出错",
                    new String[]{"userBookId","userId","relUid","bid","status"},
                    userBookId,currentUser,chatUserId,bookId,status);
        }else{
            getDataWithPost(callBack,GlobalParams.urlSetFollowStauts,"更改状态出错",
                    new String[]{"userBookId","relUid","userId","bid","status"},
                    userBookId,currentUser,chatUserId,bookId,status);
        }
    }

    @Override
    public void swapBook(String userBookId,String bookId, String bookTitle, String bookAuthor, String msg, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSwapBook,"访问出错",
                new String[]{"userBookId","bookId","bookTitle","bookAuthor","msg"},
                userBookId,bookId,StringUtil.isEmpty(bookTitle)?"":bookTitle,StringUtil.isEmpty(bookAuthor)?"":bookAuthor,StringUtil.isEmpty(msg)?"":msg);
    }

    @Override
    public void cancelSwapBook(String userBookId,String swapId,CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlCancelSwapBook,"访问出错",new String[]{"userBookId","swapId"},userBookId,swapId);
    }

    @Override
    public void getSkillClassify(CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSkillType,"未能获取技能类型",new String[]{});
    }

    @Override
    public void publishMySkillSwap(String title, String ownSkillType, String ownSkillName, String swapSkillType, String swapSkillName, String detail, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlPulishSkill,"未能成功发布",
                new String[]{"title","ownSkillType","ownSkillName","swapSkillType","swapSkillName","detail"},
                title,ownSkillType,ownSkillName,swapSkillType,swapSkillName,detail);
    }

    @Override
    public void getSwapSkillDeatil(String swapSkillId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSwapSkillDetal,"未能获取技能交换信息",
                new String[]{"swapSkillId"},swapSkillId);
    }

    @Override
    public void deleteSwapSkill(String swapSkillId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlDelSkillSwap,"删除失败",
                new String[]{"swapSkillId"},swapSkillId);
    }

    @Override
    public void getBookInfo(String bookId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetBookInfo,"未能获取图书信息",
                new String[]{"bid"},bookId);
    }

    @Override
    public void addAttention(String uid1, String uid2, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlAddAttention,"添加出错，请稍后再试",
                new String[]{"a_uid","b_uid"},uid1,uid2);
    }

    @Override
    public void checkIfAttentioned(String uid, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlCheckIfAtentioned,"未能检测到关注信息",
                new String[]{"relUid"},uid);
    }

    @Override
    public void cancelAttention(String uid, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlCancelAttention,"取消关注操作失败",
                new String[]{"relUid"},uid);
    }

    @Override
    public void report(String relUid, String type, String desc, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlReport,"提交失败，请稍后再试",
                new String[]{"rRelUid","type","desc"},relUid,type,desc);
    }

    private void getDataWithPost(CallBack callBack, String url, String failureDesc, String[] argNames, Object ...values){
        if(argNames.length != values.length){
            throw new RuntimeException("参数个数不匹配--自定义异常");
        }
        if(argNames.length!=0){
            ArrayMap<String,Object> param = new ArrayMap<>();
            for(int i=0;i<argNames.length;i++){
                if(values[i] instanceof String){
                    param.put(argNames[i],values[i]);
                }else if(values[i] instanceof Integer){
                    param.put(argNames[i],String.valueOf(values[i]));
                }
            }
            post(callBack,url,param,failureDesc);
        }else{
            post(callBack,url,null,failureDesc);
        }
    }

    private void post(CallBack callBack,String url,ArrayMap<String,Object> params,String failureDesc){
        try {
            getModel().post(url, params, callBack);
        }catch(Exception e){
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure(failureDesc);
            }
        }
    }
}
