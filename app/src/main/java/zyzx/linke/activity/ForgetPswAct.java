package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.CheckPhone;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.TimeUtils;
import zyzx.linke.utils.UIUtil;

/**
 * Created by Austin on 2017/3/30.
 * Desc:  忘记密码页
 */

public class ForgetPswAct extends BaseActivity{
    private Button btnNextPage,btnSendVerifyCode;
    private AppCompatEditText etPhone,etVerifycode;
    private String mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.act_forget_psw;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mTitleText.setText(R.string.forget_psw_title);
        btnNextPage = (Button) findViewById(R.id.btn_next_page);
        etPhone = (AppCompatEditText) findViewById(R.id.aet_phone);
        btnSendVerifyCode = (Button) findViewById(R.id.btn_sendverifycode);
        etVerifycode = (AppCompatEditText) findViewById(R.id.aet_verifycode);


        SpannableString ss = new SpannableString(getString(R.string.input_phone_forget_psw));//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16,true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhone.setHint(new SpannedString(ss));

        SpannableString ss2 = new SpannableString(getString(R.string.input_verifycode));//定义hint的值
        AbsoluteSizeSpan ass2 = new AbsoluteSizeSpan(16,true);//设置字体大小 true表示单位是sp
        ss2.setSpan(ass2, 0, ss2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etVerifycode.setHint(new SpannedString(ss2));

        btnNextPage .setOnClickListener(this);
        btnSendVerifyCode.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_next_page:
                if(StringUtil.isEmpty(etVerifycode.getText().toString())){
                    UIUtil.showToastSafe("请输入验证码");
                    etVerifycode.setError("请输入验证码");
                    return;
                }
                if(StringUtil.isEmpty(mUserId)){
                    UIUtil.showToastSafe("请先验证手机号");
                    return;
                }
                showProgress("请稍候…");
                getUserPresenter().verifyForgotPSWSMSCode(mUserId,etVerifycode.getText().toString(),new CallBack(){

                    @Override
                    public void onSuccess(Object obj, int... code) {
                        dismissProgress();
                        ResponseJson rj = new ResponseJson((String) obj);
                        if(ResponseJson.NO_DATA == rj.errorCode){
                            UIUtil.showToastSafe("访问错误，请重试");
                            return;
                        }
                        switch (rj.errorCode){
                            case 2:
                                Bundle bundle = new Bundle();
                                bundle.putString(BundleFlag.UID,String.valueOf(mUserId));
                                gotoActivity(ResetPswAct.class,true,bundle);
                                break;
                            case 3:
                                UIUtil.showToastSafe("验证码错误或已过期");
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
                        UIUtil.showToastSafe(R.string.err_request);
                    }
                });

                break;
            case R.id.btn_sendverifycode:
                if(!checkInput()){
                    return;
                }
                showProgress("请稍候…");
                final TimeUtils tu = new TimeUtils(btnSendVerifyCode,"发送验证码");
                getUserPresenter().sendForgetPswSMSVerifyCode(etPhone.getText().toString().trim(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        dismissProgress();
                        ResponseJson rj = new ResponseJson((String) obj);
                        if(ResponseJson.NO_DATA == rj.errorCode){
                            UIUtil.showToastSafe("发送失败");
                            return;
                        }
                        switch (rj.errorCode){
                            case 2:
                                tu.runTimer();
                                mUserId = (String)((Map)rj.data.get(0)).get("uid");
                                UIUtil.showToastSafe("短信已发送，请查收");
                                break;
                            case 3:
                                CustomProgressDialog.getPromptDialog(ForgetPswAct.this,"该手机号对应用户不存在",null).show();
                                break;
                            case 4:
                                UIUtil.showToastSafe("服务器错误，请联系管理员");
                                break;
                            case 7:
                                CustomProgressDialog.getPromptDialog(ForgetPswAct.this,"该手机号短信验证过于频繁，请稍后再试",null).show();
                                break;
                            case 5:
                            case 11:
                                UIUtil.showToastSafe("发送失败，请稍后再试");
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
                        UIUtil.showToastSafe("发送失败.");
                        UIUtil.showTestLog("shareBook","sms verifyCode send failure.");
                    }
                });
                break;
        }
    }

    private boolean checkInput() {
        String phone = etPhone.getText().toString();
        if(StringUtil.isEmpty(phone)){
            UIUtil.showToastSafe("手机号不能为空");
            return false;
        }
        if(!CheckPhone.isChinaMobilePhoneNum(phone)){
            //不是中国移动，验证是否是联通
            if(!CheckPhone.isChinaUnicomPhoneNum(phone)){
                //不是中国联通，验证是否是电信
                if(!CheckPhone.isChinaTelecomPhoneNum(phone)){
                    UIUtil.showToastSafe(R.string.err_phone);
                    return false;
                }
            }
        }
        return true;
    }
}
