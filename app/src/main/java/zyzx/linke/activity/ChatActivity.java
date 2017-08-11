package zyzx.linke.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
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
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/27.
 * Desc: 聊天界面
 */

public class ChatActivity extends BaseActivity {
    EaseChatFragment mChatFrag;
    public static ChatActivity activityInstance;
    String chatUserId, loginName;
    protected static final int REQUEST_CODE_MAP = 1;
    private TextView tvMettingDate;//约见日期
    private TextView tvMettingTime;//会面时间
    private TextView tvExpireDateTip;//借书截止时间
    private Date mExpireDate;
    private EditText etMettingAddress;
    private double latitude, longitude;//见面地点
    private final int CODE_SEL_ADDRESS_ON_MAP = 777;

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
        getSupportFragmentManager().beginTransaction().add(R.id.content, mChatFrag, "chat").commit();
    }

    private android.support.v7.app.AlertDialog begDialg;

    private void registListener() {
        mChatFrag.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {
                //设置消息扩展属性
                // 通过扩展属性，将userAvatar和userName发送出去。
                String userAvatar = PreferenceManager.getInstance().getCurrentUserAvatar();
                if (!TextUtils.isEmpty(userAvatar)) {
                    message.setAttribute(Const.EXTRA_AVATAR, StringUtil.getExtraName(userAvatar));
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
                HashMap<String, String> uidMap = new HashMap<>();
                uidMap.put("uid", username);
//                item.setCustomfield(uidMap);
                Intent in = new Intent(mContext, FriendHomePageAct.class);
                in.putExtra(BundleFlag.SHOWADDRESS, false);
                in.putExtra(BundleFlag.HEADCLICKABLE, false);
                mContext.startActivity(in);
            }

            @Override
            public void onAvatarLongClick(String username) {
            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                if (message.getType() == EMMessage.Type.LOCATION) {
                    EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
                    Bundle bundle = new Bundle();
                    bundle.putString("address", locBody.getAddress());
                    bundle.putDouble("latitude", locBody.getLatitude());
                    bundle.putDouble("longitude", locBody.getLongitude());
                    gotoActivity(EaseGaodeMapAct.class, false, bundle);
                    return true;
                }
                if (message.getType() == EMMessage.Type.TXT) {
                    try {
                        int shareType = message.getIntAttribute(Const.EXTRA_SHARE_TYPE);
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
                gotoActivityForResult(SelAddresOnMapAct.class, CODE_SEL_ADDRESS_ON_MAP, null);
            }
        });
        tvMettingTime = (TextView) view.findViewById(R.id.tv_metting_time);
        tvMettingDate = (TextView) view.findViewById(R.id.tv_metting_date);
        tvExpireDateTip = (TextView) view.findViewById(R.id.tv_expire_date);
        final LinearLayout llReturn = (LinearLayout) view.findViewById(R.id.ll_return);
        final EditText etRefuseReson;
        etRefuseReson = (EditText) view.findViewById(R.id.et_refuse_reason);
        etMettingAddress = (EditText) view.findViewById(R.id.et_meet_place);
        Button okBtn = (Button) view.findViewById(R.id.dialog_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_btn2);
        final LinearLayout llMain = (LinearLayout) view.findViewById(R.id.ll_main);
        final LinearLayout llRefuse = (LinearLayout) view.findViewById(R.id.ll_refuse);
        llMain.setVisibility(View.VISIBLE);
        llRefuse.setVisibility(View.GONE);

        tvMettingDate.setClickable(true);
        tvMettingTime.setClickable(true);
        tvExpireDateTip.setClickable(true);

        rbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llMain.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                llRefuse.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                switch (shareType) {
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
                showTimeDialog(tvMettingTime);
            }
        });
        tvExpireDateTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出时间选择器
                showDateDialog(tvExpireDateTip);
            }
        });
        begDialg = adb.create();
        begDialg.setView(view, 0, 0, 0, 0);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbRefuse.isChecked()) {
                    //这里是扩展自文本消息，如果这个自定义的消息需要用到语音或者图片等，可以扩展自语音、图片消息，亦或是位置消息。
                    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);

                    String refuseReson = etRefuseReson.getText().toString();
                    StringBuilder content = new StringBuilder();
                    if(StringUtil.isEmpty(refuseReson)){
                        content.append("不好意思，本书暂不方便"+ AppUtil.getShareDes(shareType)+",请见谅！");
                    }else{
                        content.append(refuseReson);
                    }
                    content.append("\n(请求已拒绝！系统代发消息)");
                    EMTextMessageBody txtBody = new EMTextMessageBody(content.toString());
                    message.addBody(txtBody);

                    message.setFrom(EMClient.getInstance().getCurrentUser());
                    message.setTo(chatUserId);
                    showDefProgress();
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            dismissProgress();
                            UIUtil.showToastSafe("已回绝！");
                            mChatFrag.onResume();
                            begDialg.dismiss();
                        }

                        @Override
                        public void onError(int i, String s) {
                            dismissProgress();
                            UIUtil.showToastSafe("未能成功发送");
                        }

                        @Override
                        public void onProgress(int i, String s) {}
                    });
                    //发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);
                }else{
                    if(llReturn.getVisibility()==View.VISIBLE){
                        if(tvExpireDateTip.getText().toString().equals("请选择")){
                            UIUtil.showToastSafe("请输入还书日期");
                            return;
                        }
                    }
                    if(tvMettingDate.getText().toString().equals("请选择") || tvMettingTime.getText().toString().equals("请选择")){
                        UIUtil.showToastSafe("请输入约见时间");
                        return;
                    }
                    if(StringUtil.isEmpty(etMettingAddress.getText().toString())){
                        UIUtil.showToastSafe("请输入约见地点");
                        etMettingAddress.setError("请输入约见地点");
                        return;
                    }
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(chatUserId);
                    //这里是扩展自文本消息，如果这个自定义的消息需要用到语音或者图片等，可以扩展自语音、图片消息，亦或是位置消息。
                    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
                    StringBuilder content = new StringBuilder("好的，约定事宜：\n最迟还书日期：");
                    content.append(tvExpireDateTip.getText().toString()).append("\n");
                    content.append("约见时间：").append(tvMettingDate.getText().toString()).append("日").append(tvMettingTime.getText().toString()).append("\n");
                    content.append("约见地点:").append(etMettingAddress.getText().toString()).append("。\n");
                    content.append("(系统代发消息,点击可快速回复)");

                    EMMessageBody txtBody = new EMTextMessageBody(content.toString());
                    message.addBody(txtBody);

                    message.setFrom(EMClient.getInstance().getCurrentUser());
                    message.setTo(chatUserId);
                    conversation.appendMessage(message);
                    showDefProgress();
                    message.setMessageStatusCallback(new MyEMCallBack(true,"回复成功"));
                    //发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);
                    if(longitude != 0 && latitude !=0){
                        EMMessage locMsg = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
                        locMsg.setChatType(EMMessage.ChatType.Chat);
                        EMLocationMessageBody locBody = new EMLocationMessageBody(etMettingAddress.getText().toString(), latitude, longitude);
                        locMsg.addBody(locBody);
                        locMsg.setTo(chatUserId);
                        conversation.appendMessage(locMsg);
                        locMsg.setMessageStatusCallback(new MyEMCallBack(true,null));
                        EMClient.getInstance().chatManager().sendMessage(locMsg);
                    }

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                begDialg.dismiss();
            }
        });

        begDialg.setCanceledOnTouchOutside(false);
        begDialg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        begDialg.show();
    }
    class MyEMCallBack implements EMCallBack{
        boolean refreshList;
        String succMsg;
        public MyEMCallBack(boolean refreshList,String succMsg){
            this.refreshList = refreshList;
            this.succMsg = succMsg;
        }
        @Override
        public void onSuccess() {
            dismissProgress();
            if(begDialg!=null){
                begDialg.dismiss();
            }
            if(refreshList){
                mChatFrag.onResume();
            }
            if(!TextUtils.isEmpty(succMsg)){
                UIUtil.showToastSafe(succMsg);
            }
        }

        @Override
        public void onError(int i, String s) {
            dismissProgress();
            UIUtil.showToastSafe("发送失败");
        }

        @Override
        public void onProgress(int i, String s) {}
    }
    private Calendar mettingDate = Calendar.getInstance();
    private Calendar mettingTime = Calendar.getInstance();

    private void showDateDialog(final TextView tipView) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mettingDate.set(Calendar.YEAR, year);
                mettingDate.set(Calendar.MONTH, monthOfYear);
                mettingDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tipView.setText(new StringBuffer(DateFormat.format("yyyy-MM-dd", mettingDate)).toString());
            }
        }, mettingDate.get(Calendar.YEAR), mettingDate.get(Calendar.MONTH), mettingDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog(final TextView tipView) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mettingTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mettingTime.set(Calendar.MINUTE, minute);
                tipView.setText(new StringBuilder(DateFormat.format("HH:mm", mettingTime)));
            }
        }, mettingTime.get(Calendar.HOUR_OF_DAY), mettingTime.get(Calendar.MINUTE), true).show();
    }

    class OnBegDialogClickListener implements View.OnClickListener {
        int shareType;

        OnBegDialogClickListener(int shareType) {
            this.shareType = shareType;
        }

        @Override
        public void onClick(View v) {
            switch (shareType) {
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

    public String getToChaUserLoginName() {
        return loginName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mChatFrag.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 888 && requestCode == CODE_SEL_ADDRESS_ON_MAP && data != null) {
            String address = data.getStringExtra("address");
            latitude = data.getDoubleExtra("lat", 0);
            longitude = data.getDoubleExtra("long", 0);
            if (begDialg != null && begDialg.isShowing()) {
                if (etMettingAddress != null) {
                    etMettingAddress.setText(address);
                }
            }
        }
    }
}
