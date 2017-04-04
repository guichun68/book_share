package zyzx.linke.activity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.FeedBack;
import zyzx.linke.model.bean.TelephonyManagerInfo;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.NetworkUtil;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/4/1.
 * Desc: 意见反馈
 */

public class FeedBackAct extends BaseActivity{

    private Button btnSubmit;
    private EditText mEtContactWay,mEtTitle,mEtContent;
    private FeedBack mFeedBack;

    @Override
    protected int getLayoutId() {
        return R.layout.act_feed_back;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mEtContactWay = (EditText) findViewById(R.id.et_contact);
        SpannableString ss = new SpannableString("QQ、Email、电话、MSN等");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mEtContactWay.setHint(new SpannedString(ss));

        mTitleText.setText("意见反馈");
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void initData() {}

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_submit:
                if(!verifiInput()){
                    return;
                }
                if(!NetworkUtil.checkNetwork(this)){
                    UIUtil.showToastSafe("请检查网络连接后再试");
                    return;
                }
                showProgress("提交中…");
                TelephonyManagerInfo telephonyInfo = AppUtil.getTelephonyInfo(mContext);
                mFeedBack = new FeedBack();
                mFeedBack
                        .setPhoneNetworkStandard(telephonyInfo.NetworkType + "")
                        .setPhoneOperatorName(NetworkUtil.getProvidersName(mContext))
                        .setPhoneOsVersion(AppUtil.getOsDisplay())
                        .setPhoneModel(AppUtil.getPhoneType())
                        .setIsWifiConnected(""+NetworkUtil.isWIFICon(mContext))
                        .setIsMobileConnected(NetworkUtil.isNetworkAvailable(mContext)+"")
                        .setPhoneNetWorkStatus(NetworkUtil.GetNetworkType(mContext))
                        .setPhoneNum(NetworkUtil.getPhone(mContext))
                        .setAppVersion(AppUtil.getAppVersionName(mContext))
                        .setUserLoginName(PreferenceManager.getInstance().getCurrentUserNick())

                        .setTitle(mEtTitle.getText().toString())
                        .setContactWay(mEtContactWay.getText().toString())
                        .setContent(mEtContent.getText().toString());

                getUserPresenter().feedBack(mFeedBack,new CallBack(){

                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        String json = (String) obj;
                        if(TextUtils.isEmpty(json)){
                            UIUtil.showToastSafe("未能成功提交，请稍后重试!");
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        int code = jsonObj.getInteger("code");
                        if(code ==200){
                            UIUtil.showToastSafe("提交成功，谢谢反馈！");
                            finish();
                        }else{
                            UIUtil.showToastSafe("未能成功提交，请稍后重试!");
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                        UIUtil.showToastSafe("未能成功提交，请稍后重试!");
                    }
                });

                break;
        }
    }

    private boolean verifiInput() {
        if(TextUtils.isEmpty(mEtTitle.getText().toString().trim())){
            UIUtil.showToastSafe("请输入标题");
            return false;
        }
        if(TextUtils.isEmpty(mEtContent.getText().toString().trim())){
            UIUtil.showToastSafe("反馈内容为空");
            return false;
        }
        if(mEtTitle.getText().toString().length()>80){
            UIUtil.showToastSafe("标题过长");
            return false;
        }
        if(mEtContactWay.getText().toString().length()>80){
            UIUtil.showToastSafe("联系方式过长");
            return false;
        }
        if(mEtContent.getText().toString().length()>1000){
            UIUtil.showToastSafe("内容过多，请删减");
            return false;
        }

        return true;
    }
}
