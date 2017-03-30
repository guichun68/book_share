package zyzx.linke.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/30.
 * Desc: 重置密码
 */

public class ResetPswAct extends BaseActivity{
    private AppCompatEditText etPsw,etRePsw;
    private Button btnReset;

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
                //TODO Reset psw

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
