package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import zyzx.linke.ContactListFragment;
import zyzx.linke.HomeFragment;
import zyzx.linke.MessageFragment;
import zyzx.linke.PersonalFragment;
import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;


public class HomeAct extends BaseActivity {

    private static final String TAG = HomeAct.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {HomeFragment.class,MessageFragment.class, ContactListFragment.class,PersonalFragment.class};
    private int mTitleArray[] = {R.string.tab_homepage, R.string.tab_mesg, R.string.tab_contact_list,R.string.tab_personal};
    private int mImageViewArray[] = {R.mipmap.home, R.mipmap.em_conversation_selected,R.mipmap.em_contact_list_selected,R.mipmap.personal};
    //    private String mTextviewArray[] = {"contact", "conversation", "setting"};
    private String mTextviewArray[] = {"suba", "search", "quality","me"};
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
}