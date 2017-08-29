package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.BookAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.ResponseJson;
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
    private String from;//标记从哪个页面来,取值从BundleFlag类中FROM_xxx

    private BookAdapter mAdapter;
    private int pageNum = 0;
    private String mAddress;//中文地址描述
    private ArrayList<BookDetail2> mBooks = new ArrayList<>();
    private Button btnSendMsg;
    private Button btnAttention;
    private final int
            /*添加关注 成功否 回调标识*/
            SUCCESS = 0x919B,FAILURE= 0x4721,
            /*检查是否已经关注 成功否 回调标识*/
            SUCCESS_CHECK = 9,FAILURE_CHECK = 8,
            /*取消关注 成功否 回调标识*/
            SUCCESS_CANCEL_ATENTION = 10,FAILURE_CANCEL_ATENTION = 11;

    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg) {
        dismissProgress();
        switch (msg.what) {
            case SUCCESS:
                ResponseJson rj = new ResponseJson((String)msg.obj);
                if(ResponseJson.NO_DATA == rj.errorCode){
                    UIUtil.showToastSafe("未能关注成功,请稍后再试");
                    return;
                }
                switch (rj.errorCode){
                    case 2:
                        UIUtil.showToastSafe("已关注");
                        btnAttention.setText("已关注");
                        break;
                    case 3:
                        UIUtil.showToastSafe("未能关注成功,请稍后再试");
                        break;
                }
                break;
            case FAILURE:
                UIUtil.showToastSafe("未能关注成功,请稍后再试");
                break;
            case SUCCESS_CHECK:
                ResponseJson rj2 = new ResponseJson((String) msg.obj);
                if(ResponseJson.NO_DATA == rj2.errorCode){
                    UIUtil.showToastSafe("未能获取关注信息");
                    return;
                }
                switch (rj2.errorCode){
                    case 2:
                        btnAttention.setText("已关注");
                        break;
                    case 3:
                        btnAttention.setText("+关注");
                        break;
                }
                break;
            case FAILURE_CHECK:
                UIUtil.showToastSafe("未能获取关注信息");
                break;
            case SUCCESS_CANCEL_ATENTION:
                UIUtil.showToastSafe("已取消关注");
                btnAttention.setText("+关注");
                break;
            case FAILURE_CANCEL_ATENTION:
                UIUtil.showToastSafe("未能取消关注");
                break;
        }
    }

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
        btnAttention = (Button) findViewById(R.id.btn_attention);
        btnAttention.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getIntentData();
        if(headerClickable){
            ivHeadIcon.setOnClickListener(this);
        }
        showDefProgress();
        getUserPresenter().checkIfAttentioned(mUser.getUid(),new CallBack(){
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage(SUCCESS_CHECK);
                msg.obj = obj;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                handler.sendEmptyMessage(FAILURE_CHECK);
            }
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            UIUtil.showToastSafe("未能获取好友信息");
            finish();
            return;
        }
        mUser = (UserVO) intent.getSerializableExtra(BundleFlag.FLAG_USER);
        from = intent.getStringExtra(BundleFlag.FROM_CHAT_ACT);
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
                if(!StringUtil.isEmpty(from) && from.equals(BundleFlag.FROM_CHAT_ACT)){
                    //从聊天页面跳转过来，直接finish掉当前页面即可
                    finish();
                }else{
                    Intent in = new Intent(FriendHomePageAct.this,ChatActivity.class);
                    in.putExtra(BundleFlag.UID,String.valueOf(mUser.getUserid()));
                    in.putExtra(BundleFlag.LOGIN_NAME, mUser.getLoginName());
                    startActivity(in);
                }
                break;
            case R.id.btn_attention:
                //判断当前关注状态，如果是已关注，则取消关注，反之则添加关注
                if(btnAttention.getText().toString().equals("+关注")){
                    //添加关注
                    getUserPresenter().addAttention(GlobalParams.getLastLoginUser().getUid(),mUser.getUid(),new CallBack(){
                        @Override
                        public void onSuccess(Object obj, int... code) {
                            Message msg = handler.obtainMessage();
                            msg.obj = obj;
                            msg.what = SUCCESS;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
                            handler.sendEmptyMessage(FAILURE);
                        }
                    });
                }else{
                    //取消关注
                    getUserPresenter().cancelAttention(mUser.getUid(),new CallBack(){

                        @Override
                        public void onSuccess(Object obj, int... code) {
                            Message msg = handler.obtainMessage(SUCCESS_CANCEL_ATENTION);
                            msg.obj = obj;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
                            handler.sendEmptyMessage(FAILURE_CANCEL_ATENTION);
                        }
                    });
                }

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

    private static class MyHandler extends Handler {
        WeakReference<FriendHomePageAct> mActivity;
        MyHandler(FriendHomePageAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            FriendHomePageAct act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
