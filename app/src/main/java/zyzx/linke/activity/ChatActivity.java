package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;

/**
 * Created by austin on 2017/3/27.
 */

public class ChatActivity  extends BaseActivity{
    EaseChatFragment mChatFrag;
    String chatUserId, loginName;
    @Override
    protected int getLayoutId() {
        return R.layout.act_chat;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
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
        getSupportFragmentManager().beginTransaction().add(R.id.content,mChatFrag,"chat").commit();
    }

    @Override
    protected void initData() {

    }
}
