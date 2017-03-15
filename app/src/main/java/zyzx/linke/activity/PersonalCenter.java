package zyzx.linke.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zxing.CaptureActivity;
import zyzx.linke.R;
import zyzx.linke.adapter.GalleryAdapter;
import zyzx.linke.constant.Const;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;
import zyzx.linke.views.UserInfoImagePOP;

/**
 * Created by austin on 2017/2/22.
 * Desc: 个人中心
 */

public class PersonalCenter extends BaseActivity {
    private RecyclerView mRecyclerView;//显示我的所有书籍
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;
    private RelativeLayout mRlScanTypeIn,mRlManualTypeIn;//ISBN扫描、手动录入
    private CircleImageView mCiv;
    private static UserInfoImagePOP uimp;
    private CapturePhoto capture;
    private Dialog mProgress;
    private TextView tvUserName;//用户昵称（login_name）
    private TextView tvSignature;

    @Override
    protected int getLayoutId() {
        return R.layout.act_personal_center;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //得到控件
        mRlScanTypeIn = (RelativeLayout) findViewById(R.id.rl_scan_input);
        mRlManualTypeIn = (RelativeLayout) findViewById(R.id.rl_manual_input);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_galary);
        mCiv = (CircleImageView) findViewById(R.id.civ);
        tvUserName = (TextView) findViewById(R.id.tv_user_login_name);
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        mCiv.setOnClickListener(this);
        mRlManualTypeIn.setOnClickListener(this);
        mRlScanTypeIn.setOnClickListener(this);
        tvSignature.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvUserName.setText(GlobalParams.gUser.getLogin_name());
        refreshSignature();
        capture = new CapturePhoto(this);
        if(!StringUtil.isEmpty(GlobalParams.gUser.getHead_icon())){
            Glide.with(mContext).load(GlobalParams.gUser.getHead_icon()).into(mCiv);
        }
        mTitleText.setText("个人中心");
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new GalleryAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.rl_scan_input:
                GlobalParams.gIsPersonCenterScan = true;
                gotoActivity(CaptureActivity.class,false);
                break;
            case R.id.rl_manual_input:
                gotoActivity(ManualInputAct.class,false);
                break;
            case R.id.civ:
                uimp = new UserInfoImagePOP(this, new itemsOnClick(Const.CAMERA_REQUEST_CODE));
                uimp.showAtLocation(findViewById(R.id.ll_root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_signature:
                //弹出修改签名的dialog
                showModifySignatureDialog();
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
                GlobalParams.getUserPresenter().mofiySignature(GlobalParams.gUser.getUserid(),sig,new CallBack(){

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
    class itemsOnClick implements View.OnClickListener {

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
                    PackageManager pm = getPackageManager();
                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
                            pm.checkPermission("android.permission.CAMERA", getPackageName()));
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
    private File file;// 图片上传 所保存图片file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (capture.getActionCode() == CapturePhoto.PICK_IMAGE) {
                Uri targetUri = data.getData();
                if (targetUri != null) {
                    String img_path = FileUtil.uriToFilePath(targetUri,this);
                    if (requestCode == Const.CAMERA_REQUEST_CODE) {
                        mHeadeIconImagePath = img_path;
                        img_path = null;
                        file = new File(mHeadeIconImagePath);
                        Glide.with(this).load(targetUri).into(mCiv);
//                        Bitmap bbb = PicConvertUtil.convertToBitmap(mHeadeIconImagePath, 90, 90);
//                        acivCover.setImageBitmap(bbb);
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
        showProgress();
        GlobalParams.getUserPresenter().uploadHeadIcon(GlobalParams.gUser.getUserid(), mHeadeIconImagePath, new CallBack() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!StringUtil.isEmpty(GlobalParams.gUser.getHead_icon())){
                            Glide.with(mContext).load(GlobalParams.gUser.getHead_icon()).into(mCiv);
                        }else{
                            Glide.with(mContext).load(R.mipmap.person).asBitmap().into(mCiv) ;
                        }
                    }
                });

            }
        });
    }

    private void showProgress(){
        if(mProgress==null){
            mProgress = CustomProgressDialog.getNewProgressBar(mContext);
        }
        mProgress.show();
    }
    private void dismissProgress(){
        CustomProgressDialog.dismissDialog(mProgress);
    }
}
