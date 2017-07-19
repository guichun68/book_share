package zyzx.linke.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.runtimepermissions.PermissionsManager;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/27.
 * Desc: 聊天界面
 */

public class ChatActivity extends BaseActivity{
    EaseChatFragment mChatFrag;
    public static ChatActivity activityInstance;
    String chatUserId, loginName;
    protected static final int REQUEST_CODE_MAP = 1;
    private TextView tvMettingDate;//约见日期
    private TextView tvMettingTime;//会面时间
    private TextView tvExpireDateTip;//借书截止时间
    private Date mExpireDate;

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
        /*mChatFrag.setAdminListener(new EaseChatFragment.OnAdminConversationDelListener() {
            @Override
            public void onAdminConversationDelListener(List<EMMessage> msgs) {
                CustomProgressDialog.getPromptDialog2Btn(mContext,"如果消息中含有借阅请求，删除将意味着您拒绝所有请求，确定删除？","确定","取消",new DelAdminMsgClickListener(msgs),null);
            }
        });*/
        registListener();
        getSupportFragmentManager().beginTransaction().add(R.id.content,mChatFrag,"chat").commit();
    }

    private Dialog begDialg;
    private void registListener() {
        mChatFrag.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {
                //设置消息扩展属性
                // 通过扩展属性，将userAvatar和userName发送出去。
                String userAvatar = PreferenceManager.getInstance().getCurrentUserAvatar();
                if (!TextUtils.isEmpty(userAvatar)) {
                    message.setAttribute(Const.EXTRA_AVATAR, userAvatar);
                }
                String userNickName = PreferenceManager.getInstance().getCurrentUserNick();
                if (!TextUtils.isEmpty(userNickName)) {
                    message.setAttribute(Const.EXTRA_NICKNAME, userNickName);
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
                if(message.getType() == EMMessage.Type.TXT){
                    try {
                        int shareType = message.getIntAttribute(Const.EXTRA_SHARE_TYPE);
//                        begDialg = CustomProgressDialog.getPromptDialog2Btn(mContext,"提示","确定","取消",new OnBegDialogClickListener(shareType),null);
                        showBegDialog(shareType);
                    } catch (HyphenateException e) {
                        UIUtil.showTestLog("normalMsg");
                    }
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

    private void showBegDialog(final int shareType) {
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_beg, null);
        RadioButton rbAgree = (RadioButton) view.findViewById(R.id.rb_agreement);
        final RadioButton rbRefuse = (RadioButton) view.findViewById(R.id.rb_refuse);
        view.findViewById(R.id.tv_sel_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvMettingTime = (TextView) view.findViewById(R.id.tv_metting_time);
        tvMettingDate = (TextView) view.findViewById(R.id.tv_metting_date);
        tvExpireDateTip = (TextView) view.findViewById(R.id.tv_expire_date);
        final LinearLayout llReturn = (LinearLayout) view.findViewById(R.id.ll_return);
        EditText etRefuseReson,etMettingAddress;
        etRefuseReson = (EditText) view.findViewById(R.id.et_refuse_reason);
        etMettingAddress = (EditText) view.findViewById(R.id.et_meet_place);
        Button okBtn = (Button) view.findViewById(R.id.dialog_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_btn2);
        final LinearLayout llMain = (LinearLayout) view.findViewById(R.id.ll_main);
        final LinearLayout llRefuse = (LinearLayout) view.findViewById(R.id.ll_refuse);
        llMain.setVisibility(View.GONE);
        llRefuse.setVisibility(View.VISIBLE);

        tvMettingDate.setClickable(true);
        tvMettingTime.setClickable(true);
        tvExpireDateTip.setClickable(true);

        rbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llMain.setVisibility(isChecked?View.VISIBLE:View.GONE);
                llRefuse.setVisibility(isChecked?View.GONE:View.VISIBLE);
                switch (shareType){
                    case 0:
                    case 1:
                    case 3:
                        llReturn.setVisibility(View.GONE);
                        break;
                    case 2:
                        llReturn.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        tvMettingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tvMettingDate);
            }
        });
        tvMettingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvExpireDateTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出时间选择器
                showDateDialog(tvExpireDateTip);
            }
        });
        final AlertDialog dialog2 = adb.create();
        dialog2.setView(view, 0, 0, 0, 0);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbRefuse.isChecked()){

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog2.show();
    }
    private Calendar mettingDate = Calendar.getInstance();
    private Calendar mettingTime = Calendar.getInstance();

    private void showDateDialog(final TextView tipView) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mettingDate.set(Calendar.YEAR,year);
                mettingDate.set(Calendar.MONTH,monthOfYear);
                mettingDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                tipView.setText(new StringBuffer(DateFormat.format("yyyy-MM-dd", mettingDate)).toString());
            }
        }, mettingDate.get(Calendar.YEAR), mettingDate.get(Calendar.MONTH), mettingDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog(final TextView tipView){
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mettingTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                mettingTime.set(Calendar.MINUTE,minute);
                tipView.setText(new StringBuilder(DateFormat.format("HH:mm", mettingTime)));
            }
        }, mettingTime.get(Calendar.HOUR_OF_DAY), mettingTime.get(Calendar.MINUTE),true).show();
    }

    class OnBegDialogClickListener implements View.OnClickListener{
        int shareType;
        OnBegDialogClickListener(int shareType){
            this.shareType = shareType;
        }

        @Override
        public void onClick(View v) {
            switch (shareType){
                case 1:

                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
        }
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
