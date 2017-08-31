package zyzx.linke.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hyphenate.util.NetUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import zyzx.linke.R;
import zyzx.linke.adapter.BaseVPAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.BasePager;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.ImageUtils;
import zyzx.linke.utils.PreferenceManager;
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

    private UserVO mUser;
    private boolean isUserInfoUpdated;//用户是否修改了 头像信息
    private final int EDIT_USERINFO_CODE = 0x375B;

    private ViewPager mViewPager;
    private BaseVPAdapter mViewPagerAdapter;
    private ArrayList<BasePager> mPages = new ArrayList<>();
    private TabLayout mTabLayout;
    private String[] titles = {"我的资料","我的关注"};
    private BasePager myInfoPage,attentionPage;

    @Override
    protected int getLayoutId() {
        return R.layout.act_person_info;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("个人资料");
        mCiv = (CircleImageView)findViewById(R.id.civ);
        mCiv.setOnClickListener(this);


        findViewById(R.id.iv_edit).setOnClickListener(this);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mTabLayout.addTab(mTabLayout.newTab().setText(titles[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles[1]));
        mViewPager = (ViewPager)findViewById(R.id.vp_viewpager);
        mTabLayout.setupWithViewPager(mViewPager);
        myInfoPage = new MyInfoPage(this,R.layout.myinfo_page);
        attentionPage = new AttentionPage(this,R.layout.attention_page);
        mPages.add(myInfoPage);
        mPages.add(attentionPage);

        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new BaseVPAdapter(mPages,titles);
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    //刷新界面（不刷新头像）
    private void refreshUI() {
        mUser = GlobalParams.getLastLoginUser();
        ((TextView) findViewById(R.id.tv_user_login_name)).setText(mUser.getLoginName());
        ((MyInfoPage)myInfoPage).setData(mUser);
        ((MyInfoPage)myInfoPage).refreshUI();
    }

    @Override
    protected void onDestroy() {
        ((AttentionPage)attentionPage).getHandler().removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void initData() {
        mUser = GlobalParams.getLastLoginUser();
        if(mUser == null){
            CustomProgressDialog.getPromptDialog(this, "未能获取账号信息,请重新登录", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EaseUIHelper.getInstance().logout(false,null);
                    PreferenceManager.getInstance().setAutoLoginFlag(false);
                    AppManager.getAppManager().finishAllActivity();
                    gotoActivity(LoginAct.class);
                }
            }).show();
            return;
        };
        capture = new CapturePhoto(this);
        if(!StringUtil.isEmpty(mUser.getHeadIcon())){
            Glide.with(mContext).load(mUser.getHeadIcon()).placeholder(R.mipmap.person).dontAnimate().into(mCiv);
        }
        showDefProgress();
        getUserPresenter().getUserInfoByUid2(String.valueOf(mUser.getUid()), new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                ResponseJson rj = new ResponseJson((String) obj);
                if(ResponseJson.NO_DATA == rj.errorCode || rj.errorCode!=2){
                    UIUtil.showToastSafe("用户信息获取失败！");
                    return;
                }
                JSONArray ja = rj.data;
                JSONObject jo = (JSONObject) ja.get(0);
                boolean isInRelsBlackList = ((JSONObject)ja.get(1)).getBoolean("isInRelsBlackList");
                mUser.setUserid(jo.getInteger("userid"));
                mUser.setUid(jo.getString("id"));
                mUser.setLoginName(jo.getString("login_name"));
                mUser.setMobilePhone(jo.getString("mobile_phone"));
                mUser.setAddress(jo.getString("address"));
                mUser.setPassword(jo.getString("password"));
                mUser.setProvinceName(jo.getString("pro"));
                mUser.setCityName(jo.getString("city"));
                mUser.setCountyName(jo.getString("county"));
                String genderStr = jo.getString("gender");
                Integer gender = Integer.parseInt(genderStr==null?"0":genderStr);
                mUser.setGender(gender);
                mUser.setHobby(jo.getString("hobby"));
                mUser.setEmail(jo.getString("email"));
                mUser.setRealName(jo.getString("real_name"));
                mUser.setCityId(jo.getInteger("city_id"));
                mUser.setLastLoginTime(jo.getString("last_login_time"));

                mUser.setSignature(jo.getString("signature"));
                String headTemp = jo.getString("head_icon");
                mUser.setHeadIcon(StringUtil.isEmpty(headTemp)?null:GlobalParams.BASE_URL+GlobalParams.AvatarDirName+headTemp);
                mUser.setBak4(jo.getString("bak4"));
                mUser.setBirthday(jo.getDate("birthday"));
                mUser.setSchool(jo.getString("school"));
                mUser.setDepartment(jo.getString("department"));
                mUser.setDiplomaId(jo.getInteger("diploma_id"));
                mUser.setSoliloquy(jo.getString("soliloquy"));
                mUser.setCreditScore(jo.getInteger("credit_score"));
                mUser.setFromSystem(jo.getInteger("from_system"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshUI();
                        ((MyInfoPage)myInfoPage).setData(mUser);
                        ((MyInfoPage)myInfoPage).refreshUI();
                    }
                });
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showToastSafe("用户信息获取失败！");
                refreshUI();
            }
        });
    }




    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.civ:
                uimp = new UserInfoImagePOP(this, new itemsOnClick(Const.AVATAR_SELECTION));
                uimp.showAtLocation(findViewById(R.id.ll_root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.iv_edit:
                if(!NetUtils.hasNetwork(this)){
                    UIUtil.showToastSafe(R.string.network_error);
                    return;
                }
                Intent intent = new Intent(mContext,EditUserInfoAct.class);
                startActivityForResult(intent,EDIT_USERINFO_CODE);
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
        }else if(requestCode==EDIT_USERINFO_CODE){
            mUser = GlobalParams.getLastLoginUser();
            refreshUI();
        }else if(requestCode == BundleFlag.FLAG_FRIEND_HOME){
            ((AttentionPage)attentionPage).refresh();
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
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                ResponseJson rj = new ResponseJson((String) obj);
                if(rj.errorCode!=null)
                switch (rj.errorCode){
                    case Const.SUCC_ERR_CODE:
                        isUserInfoUpdated = true;
                        UIUtil.showToastSafe("修改成功");
                        Iterator<Object> it = rj.data.iterator();
                        while(it.hasNext()){
                            JSONObject jo = (JSONObject) it.next();
                            String headUrlTemp = jo.getString("headUrl");
//                            mUser.setHeadIcon(headUrl);
                            String headUrl =  GlobalParams.BASE_URL+GlobalParams.AvatarDirName+headUrlTemp;
                            mUser.setHeadIcon(headUrl);
                            GlobalParams.setCurrUserHeadAvatar(headUrl);
                            UserDao.getInstance(mContext).updateUser(mUser);
                        }
                        break;
                    default:
                        UIUtil.showToastSafe("修改头像失败");
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showToastSafe("修改失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!StringUtil.isEmpty(mUser.getHeadIcon())){
                            Glide.with(mContext).load(mUser.getHeadIcon()).into(mCiv);
                        }else{
                            Glide.with(mContext).load(R.mipmap.person).asBitmap().into(mCiv) ;
                        }
                    }
                });

            }
        });
    }


    public void showSnack(String btnText,String msg) {
        /*final Snackbar snackbar = Snackbar.make(mCiv, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(btnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();*/
        UIUtil.showToastSafe(msg);
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
