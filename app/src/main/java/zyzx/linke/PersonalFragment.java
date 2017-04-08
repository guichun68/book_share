package zyzx.linke;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import zxing.CaptureActivity;
import zyzx.linke.activity.AboutUsAct;
import zyzx.linke.activity.BorrowedInBookAct;
import zyzx.linke.activity.FeedBackAct;
import zyzx.linke.activity.HomeAct;
import zyzx.linke.activity.ImportResultAct;
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
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.DownloadUtil;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.ImageUtils;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.ToastUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;
import zyzx.linke.views.UserInfoImagePOP;

import static android.app.Activity.RESULT_OK;

/**
 * 主页界面
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {
    private final int EXCEL_FILE_SELECT_CODE = 11;
    private CircleImageView mCiv;
    private UserInfoImagePOP uimp;
    private CapturePhoto capture;
    private TextView tvUserName;//用户昵称（login_name）
    private TextView tvSignature;
    private Dialog dialog;


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
        mRootView.findViewById(R.id.rl_feedback).setOnClickListener(this);//修改密码
        mRootView.findViewById(R.id.rl_export).setOnClickListener(this);//导出
        mRootView.findViewById(R.id.rl_import).setOnClickListener(this);//导入

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
            case R.id.rl_feedback:
                gotoActivity(FeedBackAct.class);
                break;
            case R.id.rl_import:
                showFileChooser();
                break;
            case R.id.rl_export://导出excel
                dialog = CustomProgressDialog.getPromptDialog2Btn(mContext, UIUtil.getString(R.string.export_tip), UIUtil.getString(R.string.confirm), UIUtil.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgress("请稍后…");
                        downloadFile();
                    }
                },null);
                dialog.show();
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "请选择Excel文件"), EXCEL_FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 下载导出的excle清单（由服务器生成excel，实际客户端为下载操作）
     */
    protected void downloadFile() {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_id",PreferenceManager.getInstance().getLastLoginUserId());
        DownloadUtil.get().download(GlobalParams.urlExportExcle, GlobalParams.BaseDir,param, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        String appPath = FileUtil.getExternalStoragePath();
                        if(appPath==null){
                            ToastUtil.show(mContext,"未检测到内存卡，导出失败！");
                            return;
                        }
                        String filePath = appPath+"getSummary.action";
                        File downloadFile = new File(filePath);
                        File newFile = new File(appPath+"我的书单.xls");
                        if(downloadFile.exists()){
                            if(downloadFile.renameTo(newFile)){
                                CustomProgressDialog.getPromptDialog(mContext,"已成功导出到:\n"+newFile.getAbsolutePath(),null).show();
                            }
                        }else{
                            if(newFile.exists()){
                                // 导出（下载）成功
                                CustomProgressDialog.getPromptDialog(mContext,"已成功导出到:\n"+newFile.getAbsolutePath(),null).show();
                            }else{
                                // 导出（下载）失败
                                UIUtil.showToastSafe(R.string.err_request);
                            }
                        }
                    }
                });
            }
            @Override
            public void onDownloading(long total,int progress) {
//                UIUtil.showTestLog("exportting……");
            }
            @Override
            public void onDownloadFailed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        String message = "导出错误";
                        UIUtil.showToastSafe(message);
                    }
                });

            }
        });
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
                UIUtil.showToastSafe("未选择图片");
            }
        }else if(requestCode==EXCEL_FILE_SELECT_CODE){
            // Get the Uri of the selected file
            Uri uri = intent.getData();
            String path = FileUtil.getUriPath(mContext, uri);
            File file = new File(path);
            if(!(file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls"))){
                UIUtil.showToastSafe("解析错误，文件格式有误");
            }else{
                if(file.exists()){
                    double length = file.length();
                    if(length/1024/1024>2){//文件大于2M，过大
                        UIUtil.showToastSafe("文件过大，仅支持2M内文件导入");
                    }else{
                        uploadExcelFile(path);
                    }
                }

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

    private void uploadExcelFile(String filePath){
        showDefProgress();
        getUserPresenter().uploadExcelFile(GlobalParams.gUser.getUserid(), filePath, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        final String json = (String) obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe("导入发生错误，请稍后再试！");
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        int code = jsonObj.getInteger("code");
                        switch (code){
                            case 400:
                                CustomProgressDialog.getPromptDialog(mContext,"文件为空,请重新选择文件",null).show();
                                break;
                            case 500:
                                CustomProgressDialog.getPromptDialog(mContext,"文件格式错误,目前仅支持Excel文件导入,请检查后重试!",null).show();
                                break;
                            case 600:
                                CustomProgressDialog.getPromptDialog(mContext,"导入错误，检测到不合模板规范的excel文档，请确保至少有“ISBN”和“书名”两列！",null).show();
                                break;
                            case 700:
                                CustomProgressDialog.getPromptDialog(mContext,"导入文档有误，请确保文档未损坏",null).show();
                                break;
                            case 200:
                                succDialog = CustomProgressDialog.getPromptDialog2Btn(mContext, "导入完毕,点击确定查看导入结果！", "确定", "取消",new DialogOnClickListener(json) ,null);
                                succDialog.show();
                                break;
                        }
                    }
                });

            }

            @Override
            public void onFailure(Object obj) {
                UIUtil.showToastSafe("导入失败");
            }
        });
    }
    private Dialog succDialog;//导入成功dialog
    private class DialogOnClickListener implements View.OnClickListener{
        String json;
        DialogOnClickListener(String json){
            this.json = json;
        }
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("json",json);
            gotoActivity(ImportResultAct.class,bundle);
            if(succDialog!=null)
                succDialog.dismiss();
        }
    }

    public UserInfoImagePOP getUimp() {
        return uimp;
    }
}
