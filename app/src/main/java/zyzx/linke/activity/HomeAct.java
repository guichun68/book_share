package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.widget.EaseConversationList;

import zyzx.linke.ContactListFragment;
import zyzx.linke.HomeFragment;
import zyzx.linke.MessageFragment;
import zyzx.linke.PersonalFragment;
import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.utils.UIUtil;


public class HomeAct extends BaseActivity {

    private static final String TAG = HomeAct.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {HomeFragment.class,EaseConversationListFragment.class, EaseContactListFragment.class,PersonalFragment.class};
    private int mTitleArray[] = {R.string.tab_homepage, R.string.tab_mesg, R.string.tab_contact_list,R.string.tab_personal};
    private int mImageViewArray[] = {R.mipmap.home, R.mipmap.em_conversation_selected,R.mipmap.em_contact_list_selected,R.mipmap.personal};
    //    private String mTextviewArray[] = {"contact", "conversation", "setting"};
    private String mTextviewArray[] = {"homepage", "conversation", "contacts","me"};
    private ImageView msgUnread;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
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


    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.home_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mImageViewArray[index]);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mTitleArray[index]);
        if (index == 0){
            msgUnread = (ImageView) view.findViewById(R.id.tabUnread);
        }
        return view;
    }

    @Override
    protected void initData() {

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

}