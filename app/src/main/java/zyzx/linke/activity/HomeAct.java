package zyzx.linke.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.io.File;
import java.util.List;

import zyzx.linke.LKConversationListFragment;
import zyzx.linke.LKSwapCenterFrg;
import zyzx.linke.MeFragment;
import zyzx.linke.R;
import zyzx.linke.ShareCenterFragment;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.ErrActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.base.UpdateService;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.Const;
import zyzx.linke.global.MyEaseConstant;
import zyzx.linke.utils.UIUtil;


public class HomeAct extends BaseActivity {

    private static final String TAG = HomeAct.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private boolean isFirstStartApp = true;//flag,一开始启动App时不检查更新(在首页加载完图书信息后更新),只为了绑定binder，使其不为空
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {ShareCenterFragment.class,LKConversationListFragment.class, LKSwapCenterFrg.class,MeFragment.class};
    private int mTitleArray[] = {R.string.tab_homepage, R.string.tab_mesg, R.string.tab_exchange_market,R.string.tab_personal};
//    private int mImageViewArray[] = {R.mipmap.home, R.mipmap.conversation,R.mipmap.contact_list,R.mipmap.personal};
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
        if (savedInstanceState != null && savedInstanceState.getBoolean(MyEaseConstant.ACCOUNT_REMOVED, false)) {
            EaseUIHelper.getInstance().logout(false,null);
            finish();
            startActivity(new Intent(this, LoginAct.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginAct.class));
            return;
        }

        // unregister this event listener when this activity enters the
        // background
        EaseUIHelper.getInstance().pushActivity(this);

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
        filter.addAction(MyEaseConstant.ACCOUNT_CONFLICT);
        filter.addAction(MyEaseConstant.ACCOUNT_FORBIDDEN);
        filter.addAction(MyEaseConstant.ACCOUNT_REMOVED);
        filter.addAction(Const.ONCLICK);
        registerReceiver(mMessageReceiver, filter);
        checkUpdate();
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
                EaseUIHelper.getInstance().logout(false,null);
                AppManager.getAppManager().finishAllActivity();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            if(isFirstStartApp){
                mBinder.callCheckUpdate(null);
                isFirstStartApp = false;
            }
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
            if(action.equals(MyEaseConstant.ACCOUNT_CONFLICT)){
                gotoErrAct(MyEaseConstant.ACCOUNT_CONFLICT);
            }else if(action.equals(MyEaseConstant.ACCOUNT_FORBIDDEN)){
                gotoErrAct(MyEaseConstant.ACCOUNT_FORBIDDEN);
            }else if(action.equals(MyEaseConstant.ACCOUNT_REMOVED)){
                gotoErrAct(MyEaseConstant.ACCOUNT_REMOVED);
            }else if(action.equals(Const.ONCLICK)){
                Intent inten = new Intent();
                inten.setAction("android.intent.action.VIEW");
                inten.addCategory("android.intent.category.DEFAULT");
                File downloadFile = new File(Environment.getExternalStorageDirectory(), GlobalParams.BaseDir+"/"+GlobalParams.downloadFileName);
                inten.setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
                startActivityForResult(inten, 0);
            }
        }
    }
    private void gotoErrAct(String errType){
        Intent intent = new Intent(HomeAct.this,ErrActivity.class);
        intent.putExtra("error_type", errType);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EaseUIHelper.getInstance().popActivity(this);

        super.onStop();
    }

}