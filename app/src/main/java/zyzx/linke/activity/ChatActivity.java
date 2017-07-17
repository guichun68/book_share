package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.EasyUtils;

import java.util.HashMap;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.runtimepermissions.PermissionsManager;
import zyzx.linke.utils.PreferenceManager;

/**
 * Created by austin on 2017/3/27.
 * Desc: 聊天界面
 */

public class ChatActivity extends BaseActivity{
    EaseChatFragment mChatFrag;
    public static ChatActivity activityInstance;
    String chatUserId, loginName;
    protected static final int REQUEST_CODE_MAP = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.act_chat;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        activityInstance = this;
        mChatFrag = new EaseChatFragment();

        Intent intent = getIntent();
        chatUserId = intent.getStringExtra(BundleFlag.UID);
        loginName = intent.getStringExtra(BundleFlag.LOGIN_NAME);
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, chatUserId);
        args.putString(BundleFlag.LOGIN_NAME, loginName);
        mChatFrag.setArguments(args);
        registListener();
        getSupportFragmentManager().beginTransaction().add(R.id.content,mChatFrag,"chat").commit();
    }



    private void registListener() {
        mChatFrag.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {
                //设置消息扩展属性
                // 通过扩展属性，将userAvatar和userName发送出去。
                String userAvatar = PreferenceManager.getInstance().getCurrentUserAvatar();
                if (!TextUtils.isEmpty(userAvatar)) {
                    message.setAttribute("user_avatar", userAvatar);
                }
                String userNickName = PreferenceManager.getInstance().getCurrentUserNick();
                if (!TextUtils.isEmpty(userNickName)) {
                    message.setAttribute("user_nick", userNickName);
                }
            }

            @Override
            public void onEnterToChatDetails() {

            }

            @Override
            public void onAvatarClick(String username) {
//                CloudItem item=new CloudItem("无", Const.TianAnMenPoint,"无","");//只是为了携带用户id到详情页
                //进入好友详情页
                HashMap<String,String> uidMap = new HashMap<>();
                uidMap.put("uid",username);
//                item.setCustomfield(uidMap);

                Intent in = new Intent(mContext,FriendHomePageAct.class);
                in.putExtra(BundleFlag.SHOWADDRESS,false);
                in.putExtra(BundleFlag.HEADCLICKABLE,false);
                mContext.startActivity(in);
            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                if(message.getType()==EMMessage.Type.LOCATION){
                    EMLocationMessageBody locBody=(EMLocationMessageBody) message.getBody();
                    Bundle bundle = new Bundle();
                    bundle.putString("address",locBody.getAddress());
                    bundle.putDouble("latitude",locBody.getLatitude());
                    bundle.putDouble("longitude",locBody.getLongitude());
                    gotoActivity(EaseGaodeMapAct.class,false,bundle);
                    return true;
                }
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });


        mChatFrag.setLocationClickListener(new EaseChatFragment.LocationClickListener() {
            @Override
            public void onLocationClicked() {
                startActivityForResult(new Intent(mContext, EaseGaodeMapAct.class), REQUEST_CODE_MAP);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (chatUserId.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        mChatFrag.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, HomeAct.class);
            startActivity(intent);
        }
    }

    public String getToChaUserLoginName(){
        return loginName;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mChatFrag.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
