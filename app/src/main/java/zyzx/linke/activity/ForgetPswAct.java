package zyzx.linke.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyzx.linke.PersonalFragment;
import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CheckPhone;
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
    private Integer mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.act_forget_psw;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText(R.string.forget_psw_title);
        btnNextPage = (Button) findViewById(R.id.btn_next_page);
        etPhone = (AppCompatEditText) findViewById(R.id.aet_phone);
        btnSendVerifyCode = (Button) findViewById(R.id.btn_sendverifycode);
        etVerifycode = (AppCompatEditText) findViewById(R.id.aet_verifycode);
        btnNextPage .setOnClickListener(this);
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
                if(mUserId==null){
                    UIUtil.showToastSafe("请先验证手机号");
                    return;
                }
                showProgress("请稍后…");
                getUserPresenter().verifySMSCode(etVerifycode.getText().toString(),mUserId.intValue(),2,new CallBack(){

                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        String json = (String) obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe(R.string.err_request);
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        int code = jsonObj.getInteger("code");
                        if(code ==200){
                            gotoActivity(ResetPswAct.class,true);
                        }else if(code ==500){
                            UIUtil.showToastSafe("验证码已过期");
                        }else{
                            UIUtil.showToastSafe(R.string.err_request);
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                        UIUtil.showToastSafe(R.string.err_request);
                    }
                });

                break;
            case R.id.btn_sendverifycode:
                if(!checkInput()){
                    return;
                }
                showProgress("请稍后…");
                final TimeUtils tu = new TimeUtils(btnSendVerifyCode,"发送验证码");
                getUserPresenter().sendForgetPswSMSVerifyCode(etPhone.getText().toString().trim(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        String json = (String) obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe("未能成功发送短信,请稍后再试");
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        int code = jsonObj.getInteger("code");

                        switch (code){
                            case 200://发送成功
                                tu.runTimer();
                                mUserId = jsonObj.getInteger("uid");
                                break;
                            case 404://手机号对应用户不存在
                                UIUtil.showToastSafe("手机号对应用户不存在");
                                break;
                            default://发送失败
                                Snackbar.make(btnSendVerifyCode,"未能成功发送短信,请稍后再试",Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                        UIUtil.showToastSafe("未能成功发送.");
                        UIUtil.showTestLog("zyzx","sms verifyCode send failure.");
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
