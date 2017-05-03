package zyzx.linke.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.ImageUtils;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;
import zyzx.linke.views.UserInfoImagePOP;

/**
 * Created by austin on 2017/4/30.
 * Desc: 个人中心（详情）页面
 */

public class PersonalCenterAct extends BaseActivity {

    private CircleImageView mCiv;
    private UserInfoImagePOP uimp;
    private CapturePhoto capture;
    private TextView tvSignature;
    private UserVO mUser;
    private boolean isUserInfoUpdated;//用户是否修改了 头像信息

    @Override
    protected int getLayoutId() {
        return R.layout.act_person_info;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mUser = GlobalParams.getLastLoginUser();
        mTitleText.setText("个人资料");
        mCiv = (CircleImageView)findViewById(R.id.civ);
        mCiv.setOnClickListener(this);

        tvSignature = (TextView) findViewById(R.id.tv_signature);
        tvSignature.setOnClickListener(this);
        findViewById(R.id.iv_edit).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_user_login_name)).setText(mUser.getLogin_name());
        ((TextView)findViewById(R.id.tv_birthday)).setText(StringUtil.isEmpty(mUser.getBirthday())?"未填写":mUser.getBirthday());
        ((TextView)findViewById(R.id.tv_gender)).setText(StringUtil.isEmpty(mUser.getGenderName())?"未填写":mUser.getGenderName());
        ((TextView)findViewById(R.id.tv_location)).setText(StringUtil.isEmpty(mUser.getCityName())?"未填写":mUser.getCityName());
        ((TextView)findViewById(R.id.tv_school)).setText(StringUtil.isEmpty(mUser.getSchool())?"未填写":mUser.getSchool());
        ((TextView)findViewById(R.id.tv_department)).setText(StringUtil.isEmpty(mUser.getDepartment())?"未填写":mUser.getDepartment());
        ((TextView)findViewById(R.id.tv_diploma)).setText(StringUtil.isEmpty(mUser.getDiplomaName())?"未填写":mUser.getDiplomaName());
        ((TextView)findViewById(R.id.tv_soliloquy)).setText(StringUtil.isEmpty(mUser.getSoliloquy())?"未填写":mUser.getSoliloquy());
    }

    @Override
    protected void initData() {
        capture = new CapturePhoto(this);
        refreshSignature();

        if(!StringUtil.isEmpty(mUser.getHead_icon())){
            Glide.with(mContext).load(mUser.getHead_icon()).into(mCiv);
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
                getUserPresenter().mofiySignature(mUser.getUserid(),sig,new CallBack(){
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
                                    mUser.setSignature(sig);
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
        if(!StringUtil.isEmpty(mUser.getSignature())){
            tvSignature.setText(mUser.getSignature());
        }else{
            tvSignature.setText("写点什么吧！");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.civ:
                uimp = new UserInfoImagePOP(this, new itemsOnClick(Const.AVATAR_SELECTION));
                uimp.showAtLocation(findViewById(R.id.ll_root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_signature:
                //弹出修改签名的dialog
                showModifySignatureDialog();
                break;
            case R.id.iv_edit:
                gotoActivity(EditUserInfoAct.class);
                break;
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
                    capture.dispatchTakePictureIntent(CapturePhoto.PICK_ALBUM_IMAGE, requestCode);
                    uimp.dismiss();
                    break;
            }
        }

    }
    protected Bitmap cropBitmap;//裁剪后的图片
    private String mHeadeIconImagePath;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if ((resultCode != RESULT_OK)){
            UIUtil.showToastSafe("已取消选择");
            return;
        }
        if(requestCode==Const.AVATAR_SELECTION){//是头像选择
            //再判断是相册还是拍照
            if(capture.getActionCode() == CapturePhoto.PICK_ALBUM_IMAGE){//相册
                Uri targetUri = intent.getData();
                if (targetUri != null) {//裁剪方法
                    ImageUtils.doCropPhoto(this,new File(FileUtil.uriToFilePath(targetUri,mContext)));
                }
            }else if(capture.getActionCode()==CapturePhoto.SHOT_IMAGE){//拍照
                ImageUtils.doCropPhoto(this,new File(capture.getmCurrentPhotoPath()));
            }
        }else if(requestCode == ImageUtils.PHOTO_PICKED_WITH_DATA){//裁剪图片完成
            if (intent != null) {
                cropBitmap = ImageUtils.getCroppedImage();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                cropBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes=baos.toByteArray();
                Glide.with(mContext).load(bytes).into(mCiv);
                mHeadeIconImagePath = saveBitmap(cropBitmap);
                uploadHeadIcon();
            }else{
                UIUtil.showToastSafe("未选择图片");
            }
        }
    }
    /**
     * 将Bitmap转成file的Uri
     * @param bitmap 待转bitmap
     * @return 对应Uri
     */
    private String saveBitmap(Bitmap bitmap) {
        String cacheDir = FileUtil.getCacheDir(mContext);
        File file = new File(cacheDir + "avatar");
        boolean isCreatedOK;
        if (!file.exists())
        {
            isCreatedOK = file.mkdirs();
            if(!isCreatedOK){
                showSnack(null,"文件写入失败,请检查是否可读写SD卡!");
                return null;
            }
        }

        File imgFile = new File(file.getAbsolutePath() + "/head.jpeg");
        if (imgFile.exists())
            imgFile.delete();
        try {
            FileOutputStream outputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
            return imgFile.getAbsolutePath();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void uploadHeadIcon() {
        showDefProgress();
        getUserPresenter().uploadHeadIcon(mUser.getUserid(), mHeadeIconImagePath, new CallBack() {
            @Override
            public void onSuccess(Object obj) {
                dismissProgress();
                String json = (String) obj;
                JSONObject jsonObject = JSON.parseObject(json);
                Integer code = jsonObject.getInteger("code");
                String newHeadIconUrl = jsonObject.getString("icon_url");
                switch (code){
                    case 200:
                        isUserInfoUpdated = true;
                        UIUtil.showToastSafe("修改成功");
                        UserDao.getInstance(mContext).updateUser(mUser);
                        GlobalParams.setCurrUserHeadAvatar(newHeadIconUrl);
                        break;
                    default:
                        UIUtil.showToastSafe("修改头像失败");
                }
            }

            @Override
            public void onFailure(Object obj) {
                dismissProgress();
                UIUtil.showToastSafe("修改失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!StringUtil.isEmpty(mUser.getHead_icon())){
                            Glide.with(mContext).load(mUser.getHead_icon()).into(mCiv);
                        }else{
                            Glide.with(mContext).load(R.mipmap.person).asBitmap().into(mCiv) ;
                        }
                    }
                });

            }
        });
    }


    public void showSnack(String btnText,String msg) {
        final Snackbar snackbar = Snackbar.make(mCiv, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(btnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if(uimp!=null&& uimp.isShowing()){
           uimp.dismiss();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(BundleFlag.SHOULD_REFRESH,isUserInfoUpdated);
        setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
        finish();//此处一定要调用finish()方法
    }
}