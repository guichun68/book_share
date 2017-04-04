package zyzx.linke.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.activity.ChatActivity;
import zyzx.linke.activity.HomeAct;
import zyzx.linke.db.DemoDBManager;
import zyzx.linke.db.DemoModel;
import zyzx.linke.db.HXUserDao;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.PreferenceManager;
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
    private String username;
    private Map<String, EaseUser> contactList = new HashMap<>();
    private boolean isSyncingContactsWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;
    /**
     * sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;


    public synchronized static EaseUIHelper getInstance() {
        if (instance == null) {
            instance = new EaseUIHelper();
        }
        return instance;
    }
    private DemoModel demoModel = null;


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
        demoModel = new DemoModel(context);
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
            //initialize preference manager
            PreferenceManager.init(context);
            //to set user's profile and avatar
            setEaseUIProviders();
            setGlobalListeners();
        }
    }

    EMConnectionListener connectionListener;
    /**
     * set global listener
     */
    protected void setGlobalListeners(){
        syncContactsListeners = new ArrayList<>();
        isContactsSyncedWithServer = demoModel.isContactSynced();
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    notifyHomeError(EaseConstant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    notifyHomeError(EaseConstant.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    notifyHomeError(EaseConstant.ACCOUNT_FORBIDDEN);
                }
            }

            @Override
            public void onConnected() {
                if (isContactsSyncedWithServer) {
                    EMLog.d(TAG, "group and contact already synced with servre");
                }else{
                    asyncFetchContactsFromServer(null);
                }
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                UIUtil.showTestLog(TAG,"EaseUI is onConnected!");
            }
        };

        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register message event listener
        registerMessageListener();
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn(){
        return EMClient.getInstance().isLoggedInBefore();
    }

    public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback){
        if(isSyncingContactsWithServer){
            return;
        }


        isSyncingContactsWithServer = true;

        new Thread(){
            @Override
            public void run(){
                List<String> usernames = null;
                try {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    // in case that logout already before server returns, we should return immediately
                    if(!isLoggedIn()){
                        isContactsSyncedWithServer = false;
                        isSyncingContactsWithServer = false;
                        notifyContactsSyncListener(false);
                        return;
                    }

                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                    for (String username : usernames) {
                        EaseUser user = new EaseUser(username);
                        EaseCommonUtils.setUserInitialLetter(user);
                        userlist.put(username, user);
                    }
                    // save the contact list to cache
                    getContactList().clear();
                    getContactList().putAll(userlist);
                    // save the contact list to database
                    HXUserDao dao = new HXUserDao(appContext);
                    List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                    dao.saveContactList(users);

                    demoModel.setContactSynced(true);
                    EMLog.d(TAG, "set contact syn status to true");

                    isContactsSyncedWithServer = true;
                    isSyncingContactsWithServer = false;

                    //notify sync success
                    notifyContactsSyncListener(true);

                    getUserProfileManager().asyncFetchContactInfosFromServer(usernames,new EMValueCallBack<List<EaseUser>>() {

                        @Override
                        public void onSuccess(List<EaseUser> uList) {
                            updateContactList(uList);
                            getUserProfileManager().notifyContactInfosSyncListener(true);
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                        }
                    });
                    if(callback != null){
                        callback.onSuccess(usernames);
                    }
                } catch (HyphenateException e) {
                    demoModel.setContactSynced(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;
                    notifyContactsSyncListener(false);
                    e.printStackTrace();
                    if(callback != null){
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    /**
     * update user list to cache and database
     *
     * @param contactInfoList
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
        for (EaseUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        demoModel.saveContactList(mList);
    }


    synchronized void reset(){
        isSyncingContactsWithServer = false;

        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);

        isContactsSyncedWithServer = false;


        setContactList(null);
        getUserProfileManager().reset();
        DemoDBManager.getInstance().closeDB();
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    //************接收并处理扩展消息***********************
                    String userNickName = message.getStringAttribute("user_nick", "");
                    String userAvatar = message.getStringAttribute("user_avatar", "");
                    String hxIdFrom = message.getFrom();//发送人

                    EaseUser easeUser = new EaseUser(hxIdFrom);
                    easeUser.setAvatar(userAvatar);
                    easeUser.setNickname(userNickName);
                    // 存入内存
                    contactList.put(hxIdFrom, easeUser);
                    // 存入db
                    HXUserDao dao = new HXUserDao(appContext);
                    dao.saveContact(easeUser);
                    demoModel.setContactSynced(true);
                    // 通知listeners联系人同步完毕
                    notifyContactsSyncListener(true);
                    // 应用在后台，不需要刷新UI,在通知栏提示新消息
                    if(!easeUI.hasForegroundActivies()){
                        getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.d(TAG, "change:");
                EMLog.d(TAG, "change:" + change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * 保存联系人到数据库 austin
     * @param users
     */
    public void saveContactList(List<EaseUser> users){
        HXUserDao dao = new HXUserDao(appContext);
        dao.saveContactList(users);
    }

    public void notifyContactsSyncListener(boolean success){
        for (DataSyncListener listener : syncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }
    public void addSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactsListeners.contains(listener)) {
            syncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactsListeners.contains(listener)) {
            syncContactsListeners.remove(listener);
        }
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName(){
        if(username == null){
            username = demoModel.getCurrentUsernName();
        }
        return username;
    }
    /**
     * 通知首页登录信息出错了
     */
    private void notifyHomeError(String exception) {
        Intent intent = new Intent(exception);
        intent.addCategory(appContext.getPackageName());
        appContext.sendBroadcast(intent);
    }

    private String TAG = "zyzx";
    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception){
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, HomeAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * logout
     * @param unbindDeviceToken
     *            whether you need unbind your device token
     * @param callback
     *            callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
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


    private EaseUser getUserInfo(String hxUserId){
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser easeUser;
        if(hxUserId.equals(EMClient.getInstance().getCurrentUser())){
            return getUserProfileManager().getCurrentUserInfo();
        }
        if (contactList != null && contactList.containsKey(hxUserId)) {
        } else { // 如果内存中没有，则将本地数据库中的取出到内存中。
            getContactList();
        }
      /*  User u = UserDao.getInstance(appContext).queryUserByUid(Integer.valueOf(hxUserId));
        user.setNickname(u.getLogin_name());
        user.setAvatar(u.getHead_icon());*/
        easeUser = contactList.get(hxUserId);
        if(easeUser == null){
            easeUser = new EaseUser(hxUserId);
        } else {
            if(TextUtils.isEmpty(easeUser.getNick())){ // 如果名字为空，则显示环信号码
                easeUser.setNickname(easeUser.getUsername());
            }
        }
//        user = getContactList().get(username);
        /*TODO if(user == null && getRobotList() != null){
            user = getRobotList().get(username);
        }*/

        // if user is not in your contacts, set inital letter for him/her
        /*if(user == null){
            user = new EaseUser(hxUserId);
            EaseCommonUtils.setUserInitialLetter(user);
        }else{

        }*/
        return easeUser;
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

    /**从数据库中取出好友列表到内存(变量)
     * get contact list
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        // return a empty non-null object to avoid app crash
        if(contactList == null){
            contactList = new Hashtable<String, EaseUser>();
            if(isLoggedIn()){
                contactList = demoModel.getContactList();
            }
        }else{
            contactList.clear();
            contactList = demoModel.getContactList();
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
    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }
    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

}
