package zyzx.linke;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/30.
 * Desc:修改密码页
 */

public class ModifyPswAct extends BaseActivity {
    private AppCompatEditText etOldPsw,etNewPsw;
    private Button btnOK;
    @Override
    protected int getLayoutId() {
        return R.layout.act_modify_psw;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("修改密码");
        etOldPsw = (AppCompatEditText) findViewById(R.id.aet_old_psw);
        etNewPsw = (AppCompatEditText) findViewById(R.id.aet_new_psw);
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(this);
    }

    @Override
    protected void initData() {}

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_ok:
                if(!checkInput()){return;}
                showProgress(UIUtil.getString(R.string.loading));
                getUserPresenter().modifyPsw(GlobalParams.getLastLoginUser().getUid(),etOldPsw.getText().toString().trim(),
                        etNewPsw.getText().toString().trim(),new CallBack(){

                            @Override
                            public void onSuccess(final Object obj, final int... code) {
                                dismissProgress();
                                final String json = (String) obj;
                                if(StringUtil.isEmpty(json)){
                                    UIUtil.showToastSafe("修改失败，请稍后再试！");
                                    return;
                                }
                                final ResponseJson rj = new ResponseJson((String)obj);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch(rj.errorCode){
                                            case 0://Server不可预知异常错误
                                                UIUtil.showToastSafe(R.string.err_request);
                                                break;
                                            case 2://旧密码错误
                                                UIUtil.showToastSafe(R.string.err_old_psw);
                                                etOldPsw.setError(UIUtil.getString(R.string.err_old_psw));
                                                break;
                                            case 1://修改成功
                                                UIUtil.showToastSafe(R.string.modify_succ);
                                                finish();
                                                break;
                                        }
                                    }
                                });


                            }

                            @Override
                            public void onFailure(Object obj, int... code) {
                                dismissProgress();
                                if(obj instanceof String){
                                    UIUtil.showToastSafe((String) obj);
                                }
                            }
                        });

                break;
        }
    }

    private boolean checkInput() {
        if(StringUtil.isEmpty(etOldPsw.getText().toString())){
            UIUtil.showToastSafe(R.string.input_old_psw);
            etOldPsw.setError(UIUtil.getString(R.string.input_old_psw));
            return false;
        }
        if(StringUtil.isEmpty(etNewPsw.getText().toString())){
            UIUtil.showToastSafe(R.string.input_new_psw);
            etNewPsw.setError(UIUtil.getString(R.string.input_new_psw));
            return false;
        }
        return true;
    }
}
