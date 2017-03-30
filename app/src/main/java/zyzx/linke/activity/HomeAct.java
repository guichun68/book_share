package zyzx.linke.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;

import java.util.List;

import zyzx.linke.HomeFragment;
import zyzx.linke.LKContactListFragment;
import zyzx.linke.LKConversationListFragment;
import zyzx.linke.PersonalFragment;
import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.ErrActivity;
import zyzx.linke.base.UpdateService;
import zyzx.linke.db.UserDao;
import zyzx.linke.utils.UIUtil;


public class HomeAct extends BaseActivity {

    private static final String TAG = HomeAct.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {HomeFragment.class,LKConversationListFragment.class, LKContactListFragment.class,PersonalFragment.class};
    private int mTitleArray[] = {R.string.tab_homepage, R.string.tab_mesg, R.string.tab_contact_list,R.string.tab_personal};
    private int mImageViewArray[] = {R.mipmap.home, R.mipmap.em_conversation_selected,R.mipmap.em_contact_list_selected,R.mipmap.personal};
    //    private String mTextviewArray[] = {"contact", "conversation", "setting"};
    private String mTextviewArray[] = {"homepage", "conversation", "contacts","me"};
    private ImageView msgUnread;
    public UpdateService.MyBinder mBinder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //make sure activity will not in background if user is logged into another device or removed
        if (savedInstanceState != null && savedInstanceState.getBoolean(EaseConstant.ACCOUNT_REMOVED, false)) {
            EaseUIHelper.getInstance().logout(false,null);
            finish();
            startActivity(new Intent(this, LoginAct.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginAct.class));
            return;
        }

        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);
        int fragmentCount = fragmentArray.length;
        for (int i = 0; i < fragmentCount; ++i) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);

        }
    }

    View tabView2;//底部第二个tab（消息）的布局
    TextView unreadLabel;
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.home_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
//        icon.setImageResource(mImageViewArray[index]);
        switch (index){
            case 0:
                icon.setBackgroundResource(R.drawable.tab_home);
                break;
            case 1:
                icon.setBackgroundResource(R.drawable.tab_conversation);
                break;
            case 2:
                icon.setBackgroundResource(R.drawable.tab_contact);
                break;
            case 3:
                icon.setBackgroundResource(R.drawable.tab_me);
                break;
        }
        TextView title = (TextView) view.findViewById(R.id.title);
//        unreadLabel = (TextView) view.findViewById(R.id.tabUnread);
        unreadLabel = (TextView) view.findViewById(R.id.unread_msg_number);
        unreadLabel.setVisibility(View.GONE);
        title.setText(mTitleArray[index]);
        if(index==1){
            tabView2 = view;
        }
        return view;
    }

    @Override
    protected void initData() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addCategory(getPackageName());
        filter.addAction(EaseConstant.ACCOUNT_CONFLICT);
        filter.addAction(EaseConstant.ACCOUNT_FORBIDDEN);
        filter.addAction(EaseConstant.ACCOUNT_REMOVED);
        registerReceiver(mMessageReceiver, filter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                UIUtil.showToastSafe(UIUtil.getString(R.string.press_more_then_exit));
                exitTime = System.currentTimeMillis();
            } else {
//                MobclickAgent.onKillProcess(mContext);
//                finish();
                logoutEaseMob();
                AppManager.getAppManager().finishAllActivity();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                EaseUIHelper.getInstance().getNotifier().onNewMsg(message);
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //red packet code : 处理红包回执透传消息
            /*for (EMMessage message : messages) {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                final String action = cmdMsgBody.action();//获取自定义action
                if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    RedPacketUtil.receiveRedPacketAckMessage(message);
                }
            }*/
            //end of red packet code
            refreshUIWithMessage();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {}
    };


    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
                LKConversationListFragment conversationListFragment = (LKConversationListFragment) HomeAct.this.getSupportFragmentManager().findFragmentByTag(mTextviewArray[1]);
                // refresh conversation list
                if (conversationListFragment != null) {
                    conversationListFragment.refresh();
                }
            }
        });
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMessageCount();
        for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
            if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal-chatroomUnreadMsgCount;
    }


    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
//            ((TextView)tabView2.findViewById(R.id.tabUnread)).setText(String.valueOf(count));
            tabView2.findViewById(R.id.tabUnread).setVisibility(View.VISIBLE);
        } else {
            tabView2.findViewById(R.id.tabUnread).setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }
    private ServiceConnection myServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.e(TAG,"Update service is Connected.");
            mBinder = (UpdateService.MyBinder) service;
            mBinder.callCheckUpdate(null);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG,"Update service is Disconnected.");
        }
    };
    /**
     * 启动服务检查更新
     */
    public void checkUpdate() {
        Intent in = new Intent(HomeAct.this, UpdateService.class);
        bindService(in, myServiceConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        UserDao.getInstance(this).closeDb();
        if (null != myServiceConn) {
            if(isServiceRunning()){
                unbindService(myServiceConn);
            }
            myServiceConn = null;
        }
        // 注销广播接收者
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //zyzx.linke.base.UpdateService
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("zyzx.linke.base.UpdateService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void removeUnreadMsg(){
        tabView2.findViewById(R.id.tabUnread).setVisibility(View.INVISIBLE);
    }

    private MessageReceiver mMessageReceiver;

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(EaseConstant.ACCOUNT_CONFLICT)){
                gotoErrAct(EaseConstant.ACCOUNT_CONFLICT);
            }else if(action.equals(EaseConstant.ACCOUNT_FORBIDDEN)){
                gotoErrAct(EaseConstant.ACCOUNT_FORBIDDEN);
            }else if(action.equals(EaseConstant.ACCOUNT_REMOVED)){
                gotoErrAct(EaseConstant.ACCOUNT_REMOVED);
            }
        }
    }
    private void gotoErrAct(String errType){
        Intent intent = new Intent(HomeAct.this,ErrActivity.class);
        intent.putExtra("error_type", errType);
        startActivity(intent);
    }

}