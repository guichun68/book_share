package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
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
    private TextView tvSignature;
    private UserVO mUser;
    private Boolean isInFriendsBlackList;
    private String from;//标记从哪个页面来,取值从BundleFlag类中FROM_xxx
    private String[] popUpTitle = {"举报", "拉黑"};


    private ArrayList<BookDetail2> mBooks = new ArrayList<>();
    private Button btnAttention;
    private final int
            /*添加关注 成功否 回调标识*/
            SUCCESS = 0x919B,FAILURE= 0x4721,
            /*检查是否已经关注 成功否 回调标识*/
            SUCCESS_CHECK = 9,FAILURE_CHECK = 8,
            /*取消关注 成功否 回调标识*/
            SUCCESS_CANCEL_ATENTION = 10,FAILURE_CANCEL_ATENTION = 11,
            /*举报成功否 回调标识*/
            SUCCESS_REPORT = 12,FAILURE_REPORT = 13,
            /*添加黑名单 成功否 回调标识*/
            SUCCESS_ADD_BLACK = 14,FAILURE_ADD_BLACK = 15;

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
                    case 4:
                        CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"关注失败，对方已限制您添加关注",null).show();
                        btnAttention.setText("+关注");
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
            case SUCCESS_REPORT:
                ResponseJson r = new ResponseJson((String)msg.obj);
                if(ResponseJson.NO_DATA == r.errorCode){
                    UIUtil.showToastSafe("提交失败,请稍后再试");
                    return;
                }
                if(reportDialog != null && reportDialog.isShowing()){
                    reportDialog.dismiss();
                }
                CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"您的举报信息已提交，谢谢反馈",null).show();
                break;
            case FAILURE_REPORT:
                UIUtil.showToastSafe("提交失败,请稍后再试");
                break;
            case SUCCESS_ADD_BLACK:

                ResponseJson j = new ResponseJson((String) msg.obj);
                if(ResponseJson.NO_DATA == j.errorCode){
                    CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"添加失败，请稍后再试",null).show();
                    return;
                }
                if(j.errorCode==2)
                    CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"添加到黑名单成功",null).show();
                else
                    CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"添加失败，请稍后再试",null).show();
                break;
            case FAILURE_ADD_BLACK:
                CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"添加失败，请稍后再试",null).show();
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
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        findViewById(R.id.btn_send_msg).setOnClickListener(this);
        btnAttention = (Button) findViewById(R.id.btn_attention);
        btnAttention.setOnClickListener(this);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("更多");
    }

    @Override
    protected void initData() {
        getIntentData();
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
        if(mUser == null){
            UIUtil.showToastSafe("未能获取书友信息");
            return;
        }
        isInFriendsBlackList = intent.getBooleanExtra(BundleFlag.IS_IN_FRIENDS_BLACLIST,false);
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
                if(isInFriendsBlackList){
                    CustomProgressDialog.getPromptDialog(FriendHomePageAct.this,"对不起，对方已拒绝您的聊天请求",null).show();
                    return;
                }
                if(!StringUtil.isEmpty(from) && from.equals(BundleFlag.FROM_CHAT_ACT)){
                    //从聊天页面跳转过来，直接finish掉当前页面即可
                    finish();
                }else{
                    Intent in = new Intent(FriendHomePageAct.this,ChatActivity.class);
                    in.putExtra(EaseConstant.EXTRA_USER_ID,String.valueOf(mUser.getUserid()));
                    in.putExtra(BundleFlag.LOGIN_NAME, mUser.getLoginName());
                    in.putExtra(BundleFlag.AVATOR,mUser.getHeadIcon());
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
            case R.id.tv_add_mylib://更多
                //构建一个popupwindow的布局
                View popupView = FriendHomePageAct.this.getLayoutInflater().inflate(R.layout.popupwindow, null);

                ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
                lsvMore.setAdapter(new ArrayAdapter<>(FriendHomePageAct.this, R.layout.item_pop, popUpTitle));

                // 创建PopupWindow对象，指定宽度和高度
//                final PopupWindow window = new PopupWindow(popupView, 300, 600);

                final PopupWindow window=new PopupWindow(popupView, 400, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                window.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                window.setBackgroundDrawable(new ColorDrawable(0));
                int popHeight=window.getContentView().getMeasuredHeight();
//                window.showAsDropDown(view, 0, -popHeight);



                // 设置动画
                window.setAnimationStyle(R.style.popup_window_anim);
                // 设置背景颜色
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                // 置可以获取焦点
                window.setFocusable(true);
                // 设置可以触摸弹出框以外的区域
                window.setOutsideTouchable(true);
                // 更新popupwindow的状态
                window.update();
                // 以下拉的方式显示，并且可以设置显示的位置
                window.showAsDropDown(view, 0, 0);
                lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0){
                            window.dismiss();
                            showReportDialog();
                        }else{
                            window.dismiss();
                            CustomProgressDialog.getPromptDialog2Btn2(FriendHomePageAct.this, "确定要将\"" + mUser.getLoginName() + "\"加入黑名单么","确定","取消", new CustomProgressDialog.MyDialogClickListener() {
                                @Override
                                public void onClick(final Dialog dialog, View v) {
                                    showDefProgress();
                                    getUserPresenter().addBlackList(mUser.getUid(), new CallBack() {
                                        @Override
                                        public void onSuccess(Object obj, int... code) {
                                            dialog.dismiss();
                                            Message msg = handler.obtainMessage(SUCCESS_ADD_BLACK);
                                            msg.obj = obj;
                                            handler.sendMessage(msg);
                                        }

                                        @Override
                                        public void onFailure(Object obj, int... code) {
                                            dialog.dismiss();
                                            handler.sendEmptyMessage(FAILURE_ADD_BLACK);
                                        }
                                    });
                                }
                            },null).show();
                        }
                    }
                });
                break;
        }
    }
    private AlertDialog reportDialog;
    private RadioGroup rg1,rg2,rg3;
    //举报
    private void showReportDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_report, null);

        TextView tvUser = (TextView) view.findViewById(R.id.tvUser);
        rg1 = (RadioGroup) view.findViewById(R.id.rg1);
        rg2 = (RadioGroup) view.findViewById(R.id.rg2);
        rg3 = (RadioGroup) view.findViewById(R.id.rg3);
        rg1.setOnCheckedChangeListener(new MyRadioGroupOnCheckedChangedListener());
        rg2.setOnCheckedChangeListener(new MyRadioGroupOnCheckedChangedListener());
        rg3.setOnCheckedChangeListener(new MyRadioGroupOnCheckedChangedListener());

        final EditText etDesc = (EditText) view.findViewById(R.id.etDesc);
        Button btnOK = (Button) view.findViewById(R.id.dialog_btn);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn2);

        reportDialog = adb.create();
        reportDialog.setView(view, 0, 0, 0, 0);

        tvUser.setText(mUser.getLoginName());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = ((RadioButton)view.findViewById(checkedRadioBtnId)).getText().toString();
                String desc = etDesc.getText().toString();
                showDefProgress();
                getUserPresenter().report(mUser.getUid(),type,desc,new CallBack(){
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        Message msg = handler.obtainMessage(SUCCESS_REPORT);
                        msg.obj = obj;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        handler.sendEmptyMessage(FAILURE_REPORT);
                    }
                });

            }
        });
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.show();
    }

    private Boolean changeedGroup = false;
    private int checkedRadioBtnId;

    class MyRadioGroupOnCheckedChangedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!changeedGroup) {
                changeedGroup = true;
                if (group == rg1) {
                    rg2.clearCheck();
                    rg3.clearCheck();
                } else if (group == rg2) {
                    rg1.clearCheck();
                    rg3.clearCheck();
                } else if (group == rg3) {
                    rg1.clearCheck();
                    rg2.clearCheck();
                }
                checkedRadioBtnId = checkedId;
                changeedGroup = false;
            }
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
        Glide.with(mContext).load(mUser.getHeadIcon()).placeholder(R.mipmap.person).dontAnimate().into(ivHeadIcon);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
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
