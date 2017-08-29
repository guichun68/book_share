package zyzx.linke.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import zyzx.linke.R;
import zyzx.linke.base.BasePager;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/8/12.
 * Desc: 我的个人信息View （PersonalCenterAct中viewPager的一个页面）
 */

public class MyInfoPage extends BasePager {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private UserVO mUser;
    private TextView tvGender;
    private TextView tvSignature;

    public MyInfoPage(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public void setData(UserVO userVO){
        this.mUser = userVO;
    }

    @Override
    public void initView() {
        tvSignature = (TextView) getRootView().findViewById(R.id.tv_signature);
        tvSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出修改签名的dialog
                showModifySignatureDialog();
            }
        });
        tvGender = (TextView) getRootView().findViewById(R.id.tv_gender);
    }

    public void refreshUI(){
        ((TextView)getRootView().findViewById(R.id.tv_birthday)).setText(mUser.getBirthday()==null?"未填写":sdf.format(mUser.getBirthday()));
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
        ((TextView)getRootView().findViewById(R.id.tv_location)).setText(StringUtil.isEmpty(sb.toString())?"未填写":sb.toString());
        ((TextView)getRootView().findViewById(R.id.tv_school)).setText(StringUtil.isEmpty(mUser.getSchool())?"未填写":mUser.getSchool());
        ((TextView)getRootView().findViewById(R.id.tv_department)).setText(StringUtil.isEmpty(mUser.getDepartment())?"未填写":mUser.getDepartment());
        ((TextView)getRootView().findViewById(R.id.tv_diploma)).setText(StringUtil.isEmpty(mUser.getDiplomaName())?"未填写":mUser.getDiplomaName());
        ((TextView)getRootView().findViewById(R.id.tv_soliloquy)).setText(StringUtil.isEmpty(mUser.getSoliloquy())?"未填写":mUser.getSoliloquy());
        if(mUser.getGender()!=null) {
            switch (mUser.getGender()) {
                case 0:
                    tvGender.setText("未填写");
                    break;
                case 1:
                    tvGender.setText("男");
                    break;
                case 2:
                    tvGender.setText("女");
                    break;
                case 3:
                    tvGender.setText("保密");
                    break;
            }
        }else{
            tvGender.setText("未填写");
        }
        if(!StringUtil.isEmpty(mUser.getSignature())){
            tvSignature.setText(mUser.getSignature());
        }else{
            tvSignature.setText("写点什么吧！");
        }
    }
    private void showModifySignatureDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        final View dialogView = View.inflate(context,R.layout.dialog_modify_signature,null);
        adb.setView(dialogView);
        final AlertDialog dialog = adb.create();//.show();
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sig = ((TextView)dialogView.findViewById(R.id.acet_signature)).getText().toString();
                if(StringUtil.isEmpty(sig)){
                    UIUtil.showToastSafe("请输入签名");
                    ((TextView) dialogView.findViewById(R.id.acet_signature)).setError("请输入签名");
                    return;
                }
                getUserPresenter().mofiySignature(mUser.getUserid(),sig,new CallBack(){
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        String json = (String) obj;
                        ResponseJson rj = new ResponseJson(json);
                        if(rj.errorCode==null || ResponseJson.NO_DATA == rj.errorCode ) {
                            UIUtil.showToastSafe("设置出错");
                            return;
                        }
                        switch (rj.errorCode){
                            case Const.SUCC_ERR_CODE:
                                UIUtil.showToastSafe("设置成功");
                                dialog.dismiss();
                                GlobalParams.getLastLoginUser().setSignature(sig);
                                mUser.setSignature(sig);
                                ((PersonalCenterAct)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvSignature.setText(sig);
                                    }
                                });
                                break;
                            default:
                                UIUtil.showToastSafe("发生错误-未知错误");
                                break;
                        }
                    }
                    @Override
                    public void onFailure(Object obj, int... code) {
                        UIUtil.showToastSafe("设置出错-未知错误");
                    }
                });
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                refreshUI();
            }
        });
    }

}
