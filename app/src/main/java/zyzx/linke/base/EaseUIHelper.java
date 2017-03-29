package zyzx.linke.base;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.UserProfileManager;
import zyzx.linke.activity.ChatActivity;
import zyzx.linke.db.UserDao;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;


/**
 * Created by austin on 2017/3/27.
 */

public class EaseUIHelper {
    private static EaseUIHelper instance = null;
    private Context appContext;
    private UserProfileManager userProManager;
    private EaseUI easeUI;
    private IUserPresenter mUserPresenter;
    private Map<String, EaseUser> contactList = new HashMap<>();


    public synchronized static EaseUIHelper getInstance() {
        if (instance == null) {
            instance = new EaseUIHelper();
        }
        return instance;
    }

    /**
     * get instance of EaseNotifier
     * @return
     */
    public EaseNotifier getNotifier(){
        return easeUI.getNotifier();
    }

    /**
     * init helper
     * @param context application context
     */
    public void init(Context context) {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(false);
        if (EaseUI.getInstance().init(context, options)) {
            this.appContext = context;
            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
        }
    }

    public boolean isVoiceCalling;
    public boolean isVideoCalling;
    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
//set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if(message.getType() == EMMessage.Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if(user != null){
                    if(EaseAtMessageHelper.get().isAtMeMsg(message)){
                        return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                }else{
                    if(EaseAtMessageHelper.get().isAtMeMsg(message)){
                        return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                // you can set what activity you want display when user click the notification
                Intent intent = new Intent(appContext, ChatActivity.class);
                // open calling activity if there is call
                if(isVideoCalling){
//                    intent = new Intent(appContext, VideoCallActivity.class);
                }else if(isVoiceCalling){
//                    intent = new Intent(appContext, VoiceCallActivity.class);
                }else{
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // single chat message
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", EaseConstant.CHATTYPE_SINGLE);
                    } else { // group chat message
                        // message.getTo() is the group id
                        intent.putExtra("userId", message.getTo());
                        if(chatType == EMMessage.ChatType.GroupChat){
                            intent.putExtra("chatType", EaseConstant.CHATTYPE_GROUP);
                        }else{
                            intent.putExtra("chatType", EaseConstant.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        });
    }


    private EaseUser getUserInfo(String username){
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = new EaseUser(username);;
        if(username.equals(EMClient.getInstance().getCurrentUser())){
            return getUserProfileManager().getCurrentUserInfo();
        }
        User u = UserDao.getInstance(appContext).queryUserByUid(Integer.valueOf(username));
        user.setLoginName(u.getLogin_name());
        user.setNickname(u.getLogin_name());
        user.setAvatar(u.getHead_icon());

//        user = getContactList().get(username);
        /*TODO if(user == null && getRobotList() != null){
            user = getRobotList().get(username);
        }*/

        // if user is not in your contacts, set inital letter for him/her
        if(user == null){
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }

    /**
     * update contact list
     *
     * @param aContactList
     */
    public void setContactList(Map<String, EaseUser> aContactList) {
        if(aContactList == null){
            if (contactList != null) {
                contactList.clear();
            }
            return;
        }

        contactList = aContactList;
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if ( contactList == null) {
            getUserPresenter().getAllMyFriends(GlobalParams.gUser.getUserid(), new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String json = (String) obj;
                    if (StringUtil.isEmpty(json)) {
                        UIUtil.showTestLog("zyzx","未能获取好友信息");
                        return;
                    }
                    List<User> friends = JSON.parseArray(json, User.class);

                    for (int i = 0; i < friends.size(); i++) {
                        EaseUser u2 = new EaseUser(String.valueOf(friends.get(i).getUserid()));
                        u2.setAvatar(friends.get(i).getHead_icon());
                        u2.setNickname(friends.get(i).getLogin_name());
                        u2.setLoginName(friends.get(i).getLogin_name());
                        contactList.put("easeuitest" + i, u2);
                    }





                }

                @Override
                public void onFailure(Object obj) {

                }
            });
        }

        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }

        return contactList;
    }
    protected IUserPresenter getUserPresenter(){
        if(mUserPresenter==null){
            mUserPresenter=(GlobalParams.getUserPresenter());
        }
        return mUserPresenter;
    }

    /**
     * 注销环信登录
     */
    public void logoutEaseMob() {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                UIUtil.print("已注销EASEMob");
//                gotoActivity(LoginAct.class,true);
            }
            @Override
            public void onError(int i, String s) {
//                UIUtil.showToastSafe("注销失败，请稍后重试！");
                UIUtil.showTestLog("zyzx","环信注销登录失败："+i+s);
            }
            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

}
