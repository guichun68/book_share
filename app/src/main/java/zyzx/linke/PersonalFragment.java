package zyzx.linke;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import zxing.CaptureActivity;
import zyzx.linke.activity.AboutUsAct;
import zyzx.linke.activity.BorrowedInBookAct;
import zyzx.linke.activity.HomeAct;
import zyzx.linke.activity.LoginAct;
import zyzx.linke.activity.ManualInputAct;
import zyzx.linke.activity.MyBooksAct;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.base.UpdateService;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.ImageUtils;
import zyzx.linke.utils.PreferenceManager;
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


    // 裁剪后图片的宽(X)和高(Y),600 X 600的正方形。
    private static int output_X = 600;
    private static final int CODE_RESULT_REQUEST = 0xa2;//最终裁剪后的结果
    private static int output_Y = 600;

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
        mRootView.findViewById(R.id.rl_log_out).setOnClickListener(this);//注销登录
        mRootView.findViewById(R.id.rl_check_update).setOnClickListener(this);//注销登录
        mRootView.findViewById(R.id.rl_about).setOnClickListener(this);//注销登录
        mRootView.findViewById(R.id.rl_modify_psw).setOnClickListener(this);//修改密码

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
                uimp = new UserInfoImagePOP(this.getActivity(), new PersonalFragment.itemsOnClick(Const.AVATAR_SELECTION));
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
            case R.id.rl_log_out:
                ((HomeAct)getActivity()).logoutEaseMob();
                PreferenceManager.getInstance().setAutoLoginFlag(false);
                getActivity().finish();
                gotoActivity(LoginAct.class);
                break;
            case R.id.rl_check_update:
                //showProgress("检查中…");
                ((HomeAct)getActivity()).mBinder.callCheckUpdate(new UpdateService.CheckUpdateCallBack() {
                    @Override
                    public void shouldUpdate(boolean shoudUpdate) {
                        dismissProgress();
                        if(!shoudUpdate){
                            showSnack(null,"已经是最新版本");
                        }else{
                            //do nothing, if app should update,the UpdateActivity will auto evoked.
                            Log.i("zyzx","should update");
                        }
                    }
                });
                break;
            case R.id.rl_about:
                gotoActivity(AboutUsAct.class);
                break;
            case R.id.rl_modify_psw://修改密码
                gotoActivity(ModifyPswAct.class);
                break;
        }
    }

    public void showSnack(String btnText,String msg) {
        final Snackbar snackbar = Snackbar.make(tvSignature, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(btnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
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
                UIUtil.showToastSafe("未能选择图片");
            }

        }
    }

    /**
     * 将Bitmap转成file的Uri
     *
     * @param bitmap
     * @return
     */
    private String saveBitmap(Bitmap bitmap) {
        String cacheDir = FileUtil.getCacheDir(mContext);
        File file = new File(cacheDir + "avatar");
        if (!file.exists())
            file.mkdirs();
        File imgFile = new File(file.getAbsolutePath() + "/head.jpeg");
        if (imgFile.exists())
            imgFile.delete();
        try {
            FileOutputStream outputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
//            Uri uri = Uri.fromFile(imgFile);
            return imgFile.getAbsolutePath();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 裁剪图片
     * @param uri
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //把裁剪的数据填入里面
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
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
                        UserDao.getInstance(mContext).updateUser(GlobalParams.gUser);
                        GlobalParams.gUser.setHead_icon(newHeadIconUrl);
                        PreferenceManager.getInstance().setCurrentUserAvatar(newHeadIconUrl);
                        EaseUIHelper.getInstance().getUserProfileManager().getCurrentUser().setAvatar(newHeadIconUrl);
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
