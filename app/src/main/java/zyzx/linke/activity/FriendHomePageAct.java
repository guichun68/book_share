package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.BookAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;

/**
 * Created by austin on 2017/3/4.
 * Desc: 好友主页（显示好友信息并展示其在地图中分享的书籍,即其在zyzx_user_books表中book_status=2的所有书籍）
 */

public class FriendHomePageAct extends BaseActivity {

    private CircleImageView ivHeadIcon;
    private TextView tvGender;
    private TextView tvLoginname;
    private TextView tvLocation;
    private TextView tvSignature;
    private boolean headerClickable;
    private UserVO mUser;


    private BookAdapter mAdapter;
    private int pageNum = 0;
    private String mAddress;//中文地址描述
    private ArrayList<BookDetail2> mBooks = new ArrayList<>();
    private Button btnSendMsg;

    @Override
    protected int getLayoutId() {
        return R.layout.act_friend_page;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("书友信息");
        tvGender = (TextView) findViewById(R.id.tv_gender);
        ivHeadIcon = (CircleImageView) findViewById(R.id.iv_icon);
        tvLoginname = (TextView) findViewById(R.id.tv_loginname);
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        btnSendMsg = (Button) findViewById(R.id.btn_send_msg);
        btnSendMsg.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getIntentData();
        if(headerClickable){
            ivHeadIcon.setOnClickListener(this);
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            UIUtil.showToastSafe("未能获取好友信息");
            finish();
            return;
        }
        mUser = (UserVO) intent.getSerializableExtra(BundleFlag.FLAG_USER);
        refreshUI();
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.iv_icon:
                break;
            case R.id.btn_send_msg:
                if((""+mUser.getUserid()).equals(EMClient.getInstance().getCurrentUser())){
                    UIUtil.showToastSafe("无需同自己聊天");
                    return;
                }
                Intent in = new Intent(FriendHomePageAct.this,ChatActivity.class);
                in.putExtra(BundleFlag.UID,String.valueOf(mUser.getUserid()));
                in.putExtra(BundleFlag.LOGIN_NAME, mUser.getLoginName());
                startActivity(in);
                break;
        }
    }

    //刷新界面（不刷新头像）
    private void refreshUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ((TextView) findViewById(R.id.tv_loginname)).setText(mUser.getLoginName());
        ((TextView)findViewById(R.id.tv_birthday)).setText(mUser.getBirthday()==null?"未填写":sdf.format(mUser.getBirthday()));
        if(mUser.getGender()!=null)
            switch (mUser.getGender()){
                case 0:tvGender.setText("未填写");
                    break;
                case 1:tvGender.setText("男");
                    break;
                case 2:
                    tvGender.setText("女");
                    break;
                case 3:
                    tvGender.setText("保密");
                    break;
            }
        else{
            tvGender.setText("未填写");
        }
        StringBuilder sb = new StringBuilder();
        if(!StringUtil.isEmpty(mUser.getProvinceName())){
            sb.append(" ").append(mUser.getProvinceName());
        }
        if(!StringUtil.isEmpty(mUser.getCityName())){
            sb.append(" ").append(mUser.getCityName());
        }
        if(!StringUtil.isEmpty(mUser.getCountyName())){
            sb.append(" ").append(mUser.getCountyName());
        }
        ((TextView)findViewById(R.id.tv_location)).setText(StringUtil.isEmpty(sb.toString())?"未填写":sb.toString());
        ((TextView)findViewById(R.id.tv_school)).setText(StringUtil.isEmpty(mUser.getSchool())?"未填写":mUser.getSchool());
        ((TextView)findViewById(R.id.tv_department)).setText(StringUtil.isEmpty(mUser.getDepartment())?"未填写":mUser.getDepartment());
        mUser.setDiplomaName(AppUtil.getDiplomaName(mUser.getDiplomaId()));
        ((TextView)findViewById(R.id.tv_diploma)).setText(StringUtil.isEmpty(mUser.getDiplomaName())?"未填写":mUser.getDiplomaName());
        ((TextView)findViewById(R.id.tv_soliloquy)).setText(StringUtil.isEmpty(mUser.getSoliloquy())?"未填写":mUser.getSoliloquy());
        if(!StringUtil.isEmpty(mUser.getSignature())){
            tvSignature.setText(mUser.getSignature());
        }else{
            tvSignature.setText("未填写！");
        }
        Glide.with(mContext).load(mUser.getHeadIcon()).into(ivHeadIcon);
    }
}
