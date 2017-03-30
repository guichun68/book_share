package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/30.
 * Desc: 重置密码
 */

public class ResetPswAct extends BaseActivity{
    private AppCompatEditText etPsw,etRePsw;
    private Button btnReset;
    private String mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.act_reset_psw;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("重置密码");
        etPsw = (AppCompatEditText) findViewById(R.id.aet_psw);
        etRePsw = (AppCompatEditText) findViewById(R.id.aet_re_psw);
        btnReset = (Button) findViewById(R.id.btn_rest);
        btnReset.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mUserId = getIntent().getStringExtra(BundleFlag.UID);
        if(StringUtil.isEmpty(mUserId)){
            UIUtil.showToastSafe("未能获取用户信息,请返回重新验证");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_rest:
                if(!checkInput()){
                    return;
                }

                String newPsw = etPsw.getText().toString().trim();
                showProgress("请稍后…");
                getUserPresenter().resetPsw(mUserId,newPsw,new CallBack(){

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
                        if(code == 200){
                            UIUtil.showToastSafe("重置成功,请重新登录");
                            finish();
                        }else{
                            UIUtil.showToastSafe("重置密码错误，code="+code);
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                        UIUtil.showToastSafe(R.string.err_request);
                    }
                });
                break;
        }
    }

    private boolean checkInput() {
        String psw = etPsw.getText().toString();
        String rePsw = etRePsw.getText().toString();
        if(StringUtil.isEmpty(psw)){
            etPsw.setError(getString(R.string.input_psw));
            UIUtil.showToastSafe(R.string.input_psw);
            return false;
        }
        if(StringUtil.isEmpty(rePsw)){
            etRePsw.setError(getString(R.string.re_password));
            UIUtil.showToastSafe(R.string.re_password);
            return false;
        }
        if(!psw.equals(rePsw)){
            UIUtil.showToastSafe(R.string.twopsw_not_equal);
            return false;
        }
        return true;
    }
}
