package zyzx.linke.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.alibaba.fastjson.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.SkillTypeAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.EnumConst;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by Austin on 2017-08-24.
 * Desc: 技能录入页
 */

public class SkillInputAct extends BaseActivity {

    private Spinner spSkillClassify,spSwapClassify;
    private AppCompatEditText etSkillName,etSwapSkillName,etSwapDetail,etTitle;
    private Button btnSubmit;
    private SkillTypeAdapter mAdapter;
    private ArrayList<EnumConst> mSkillClassifies;
    private final int SUCESS_GET_CLASSIFY = 0xC919,FAILURE_GET_CLASSIFY = 0xB2;
    private final int SUCESS_PUBLISH = 0xC717,FAILURE_PUBLISH = 0xB3;

    @Override
    protected int getLayoutId() {
        return R.layout.act_skill_input;
    }

    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg){
        dismissProgress();
        if(msg == null){
            return;
        }
        switch (msg.what){
            case FAILURE_GET_CLASSIFY:
                CustomProgressDialog.getPromptDialog(this, "未能获取技能分类信息,请重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
                break;
            case SUCESS_GET_CLASSIFY:
                DefindResponseJson drj = new DefindResponseJson((String) msg.obj);
                if(DefindResponseJson.NO_DATA == drj.errorCode){
                    CustomProgressDialog.getPromptDialog(this, "未能获取技能分类信息,请重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                    return;
                }

                mSkillClassifies.clear();
                mSkillClassifies.addAll(AppUtil.getSwapSkillTyps((JSONArray) drj.data.getItems()));
                mAdapter.notifyDataSetChanged();
                break;
            case SUCESS_PUBLISH:
                //发布成功
                ResponseJson rj = new ResponseJson((String) msg.obj);
                if(ResponseJson.NO_DATA == rj.errorCode){
                    UIUtil.showToastSafe("未能发布成功，请稍后再试");
                    return;
                }
                switch (rj.errorCode){
                    case 2:
                        UIUtil.showToastSafe("发布成功");
                        setResult(777);
                        this.finish();
                        break;
                    case 3:
                        UIUtil.showToastSafe("发布失败");
                        break;
                }
                break;
            case FAILURE_PUBLISH:
                UIUtil.showToastSafe("未能发布成功,请稍后再试");
                //发布失败
                break;
            default:
                UIUtil.showToastSafe("请求错误,请返回重试");
                break;
        }

    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("发布技能交换");
        spSkillClassify = (Spinner) findViewById(R.id.sp_skill_classify);
        spSwapClassify = (Spinner) findViewById(R.id.sp_swap_skill_type);
        etSkillName = (AppCompatEditText) findViewById(R.id.acet_skill_name);
        etSwapSkillName = (AppCompatEditText) findViewById(R.id.acet_swap_name);
        etSwapDetail = (AppCompatEditText) findViewById(R.id.acet_detail);
        etTitle = (AppCompatEditText) findViewById(R.id.acet_title);
        etSwapDetail = (AppCompatEditText) findViewById(R.id.acet_detail);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        mSkillClassifies = new ArrayList<>();
        mAdapter = new SkillTypeAdapter(mSkillClassifies);
        spSkillClassify.setAdapter(mAdapter);
        spSwapClassify.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        showDefProgress();
        getUserPresenter().getSkillClassify(new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage();
                msg.obj = obj;
                msg.what = SUCESS_GET_CLASSIFY;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                handler.sendEmptyMessage(FAILURE_GET_CLASSIFY);
            }
        });
    }

    private static class MyHandler extends Handler {
        WeakReference<SkillInputAct> mActivity;
        MyHandler(SkillInputAct act){
            this.mActivity = new WeakReference<SkillInputAct>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SkillInputAct act = mActivity==null?null:mActivity.get();
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_submit:
                if(!checkInput()){
                    return;
                }
                showDefProgress();
                getUserPresenter().publishMySkillSwap(
                        etTitle.getText().toString(),((EnumConst)spSkillClassify.getSelectedItem()).getId(),etSkillName.getText().toString(),
                        ((EnumConst)spSwapClassify.getSelectedItem()).getId(),etSwapSkillName.getText().toString(),etSwapDetail.getText().toString(),
                new CallBack(){
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        Message msg = handler.obtainMessage();
                        msg.obj = obj;
                        msg.what = SUCESS_PUBLISH;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        handler.sendEmptyMessage(FAILURE_PUBLISH);
                    }
                });
                break;
        }
    }

    public boolean checkInput(){
        if(StringUtil.isEmpty(etSkillName.getText().toString())){
            UIUtil.showToastSafe("请输入您拥有的技能");
            return false;
        }
        if(StringUtil.isEmpty(etSwapSkillName.getText().toString())){
            UIUtil.showToastSafe("请输入您想要交换的技能");
            return false;
        }
        if(StringUtil.isEmpty(etTitle.getText().toString())){
            UIUtil.showToastSafe("请输入标题");
            return false;
        }
        if((spSwapClassify.getSelectedItem()==null || StringUtil.isEmpty(((EnumConst)spSwapClassify.getSelectedItem()).getName()))){
            UIUtil.showToastSafe("未选择技能类型，请选择");
            return false;
        }
        return true;
    }
}
