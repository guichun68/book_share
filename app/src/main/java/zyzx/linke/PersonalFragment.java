package zyzx.linke;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import zxing.CaptureActivity;
import zyzx.linke.activity.BorrowedInBookAct;
import zyzx.linke.activity.ManualInputAct;
import zyzx.linke.activity.MyBooksAct;
import zyzx.linke.activity.PersonalCenter;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;
import zyzx.linke.views.UserInfoImagePOP;

import static android.app.Activity.RESULT_OK;

/**
 * 主页界面
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView mCiv;
    private UserInfoImagePOP uimp;
    private CapturePhoto capture;
    private TextView tvUserName;//用户昵称（login_name）
    private TextView tvSignature;

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.act_personal_center, container, false);
    }

    @Override
    public void initView() {
        mBackBtn.setVisibility(View.INVISIBLE);
        mCiv = (CircleImageView)mRootView. findViewById(R.id.civ);
        tvUserName = (TextView) mRootView.findViewById(R.id.tv_user_login_name);
        tvSignature = (TextView) mRootView.findViewById(R.id.tv_signature);
        mRootView.findViewById(R.id.rl_all_checkin).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_borrow_in).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_scan_input).setOnClickListener(this);//扫描
        mRootView.findViewById(R.id.rl_manual_input).setOnClickListener(this);//手动录入
        mCiv.setOnClickListener(this);
        tvSignature.setOnClickListener(this);
        intiData();
    }

    private void intiData() {
        tvUserName.setText(GlobalParams.gUser.getLogin_name());
        refreshSignature();
        capture = new CapturePhoto(this);
        if(!StringUtil.isEmpty(GlobalParams.gUser.getHead_icon())){
            Glide.with(mContext).load(GlobalParams.gUser.getHead_icon()).into(mCiv);
        }
        mTitleText.setText("个人中心");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_scan_input:
                gotoActivity(CaptureActivity.class);
                break;
            case R.id.rl_manual_input:
                gotoActivity(ManualInputAct.class);
                break;
            case R.id.civ:
                uimp = new UserInfoImagePOP(this.getActivity(), new PersonalFragment.itemsOnClick(Const.CAMERA_REQUEST_CODE));
                uimp.showAtLocation(mRootView.findViewById(R.id.ll_root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_signature:
                //弹出修改签名的dialog
                showModifySignatureDialog();
                break;
            case R.id.rl_all_checkin://我的所有书
                gotoActivity(MyBooksAct.class);
                break;
            case R.id.rl_borrow_in://已借入的书
                gotoActivity(BorrowedInBookAct.class);
                break;
        }
    }



    private void showModifySignatureDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        final View dialogView = View.inflate(mContext,R.layout.dialog_modify_signature,null);
        adb.setView(dialogView);
        final Dialog dialog = adb.create();//.show();
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sig = ((TextView)dialogView.findViewById(R.id.acet_signature)).getText().toString();
                if(StringUtil.isEmpty(sig)){
                    UIUtil.showToastSafe("请输入签名");
                    ((TextView) dialogView.findViewById(R.id.acet_signature)).setError("请输入签名");
                    return;
                }
                getUserPresenter().mofiySignature(GlobalParams.gUser.getUserid(),sig,new CallBack(){

                    @Override
                    public void onSuccess(Object obj) {
                        String json = (String) obj;
                        JSONObject jsonObject = JSON.parseObject(json);
                        Integer code = jsonObject.getInteger("code");
                        if(code ==null){
                            UIUtil.showToastSafe("发生错误-未知错误");
                        }else{
                            switch (code){
                                case 200:
                                    UIUtil.showToastSafe("设置成功");
                                    GlobalParams.gUser.setSignature(sig);
                                    dialog.dismiss();
                                    break;
                                case 500:
                                    UIUtil.showToastSafe("发生错误-未知错误");
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        UIUtil.showToastSafe("设置出错-未知错误");
                    }
                });
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                refreshSignature();
            }
        });

    }

    private void refreshSignature(){
        if(!StringUtil.isEmpty(GlobalParams.gUser.getSignature())){
            tvSignature.setText(GlobalParams.gUser.getSignature());
        }else{
            tvSignature.setText("写点什么吧！");
        }
    }

    /**
     * 弹出窗口监听类
     * @author Austin
     */
    private class itemsOnClick implements View.OnClickListener {

        private int requestCode;

        itemsOnClick(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_camera:
                    if (null == FileUtil.getImageFileLocation()) {
                        UIUtil.showToastSafe("未检测到内存卡,无法拍照");
                        return;
                    }
                    //检查是否有相机权限
                    PackageManager pm = mContext.getPackageManager();
                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
                            pm.checkPermission("android.permission.CAMERA", mContext.getPackageName()));
                    if (permission) {
//                        imageUri = Uri.parse(FileUtil.getImageFileLocation());
                        capture.dispatchTakePictureIntent(CapturePhoto.SHOT_IMAGE, requestCode);
                        uimp.dismiss();
                    }else {
                        UIUtil.showToastSafe("未能获取相机权限,请在手机设置中赋予权限");
                    }
                    break;
                case R.id.tv_photo:
                    capture.dispatchTakePictureIntent(CapturePhoto.PICK_IMAGE, requestCode);
                    uimp.dismiss();
                    break;
            }
        }

    }

    private String mHeadeIconImagePath;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (capture.getActionCode() == CapturePhoto.PICK_IMAGE) {
                Uri targetUri = data.getData();
                if (targetUri != null) {
                    if (requestCode == Const.CAMERA_REQUEST_CODE) {
                        mHeadeIconImagePath = FileUtil.uriToFilePath(targetUri,mContext);
                        Glide.with(this).load(targetUri).into(mCiv);
                    }
                }
            } else {
                if (requestCode == Const.CAMERA_REQUEST_CODE) {
                    mHeadeIconImagePath = capture.getmCurrentPhotoPath();
                    Glide.with(this).load(mHeadeIconImagePath).into(mCiv);
                    /*file = new File(mHeadeIconImagePath);
                    Bitmap bbb = PicConvertUtil.convertToBitmap(mHeadeIconImagePath, 90, 90);
                    acivCover.setImageBitmap(bbb);*/
                }
            }
        }
        if(!StringUtil.isEmpty(mHeadeIconImagePath)){
            uploadHeadIcon();
        }
    }

    private void uploadHeadIcon() {
        showDefProgress();
        getUserPresenter().uploadHeadIcon(GlobalParams.gUser.getUserid(), mHeadeIconImagePath, new CallBack() {
            @Override
            public void onSuccess(Object obj) {
                dismissProgress();
                String json = (String) obj;
                JSONObject jsonObject = JSON.parseObject(json);
                Integer code = jsonObject.getInteger("code");
                String newHeadIconUrl = jsonObject.getString("icon_url");
                switch (code){
                    case 200:
                        UIUtil.showToastSafe("修改成功");
                        GlobalParams.gUser.setHead_icon(newHeadIconUrl);
                        break;
                    default:
                        UIUtil.showToastSafe("修改头像失败");

                }
            }

            @Override
            public void onFailure(Object obj) {
                dismissProgress();
                UIUtil.showToastSafe("修改失败");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!StringUtil.isEmpty(GlobalParams.gUser.getHead_icon())){
                            Glide.with(getContext()).load(GlobalParams.gUser.getHead_icon()).into(mCiv);
                        }else{
                            Glide.with(getContext()).load(R.mipmap.person).asBitmap().into(mCiv) ;
                        }
                    }
                });

            }
        });
    }

}
