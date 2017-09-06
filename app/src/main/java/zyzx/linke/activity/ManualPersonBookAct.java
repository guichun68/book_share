package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.adapter.BookClassifyAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.EnumConst;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.UserInfoImagePOP;

/**
 * Created by austin on 2017/7/11.
 * Desc: 个人手写资料录入
 */

public class ManualPersonBookAct extends BaseActivity{

    private AppCompatSpinner spBookClassify;
    private BookClassifyAdapter mAdapter;
    private ArrayList<EnumConst> mClassifys = new ArrayList<>();
    private TextView tvSave;
    private UserInfoImagePOP uimp;
    private CapturePhoto capture;
    private AppCompatImageView acivCover;
    private AppCompatEditText mMaterialTitle,mIntro;
    private BookDetail2 mBook;
    private Dialog dialog;

    private static class MyHandler extends Handler {
        private final WeakReference<ManualPersonBookAct> mActivity;

        private MyHandler(ManualPersonBookAct activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().myHandleMessage(msg);
        }
    }

    private final Handler mHandler = new MyHandler(this);

    private void myHandleMessage(Message msg) {
        dismissProgress();
        switch (msg.what) {
            case 1://成功
                String responseJson = (String) msg.obj;
                ResponseJson rj = new ResponseJson(responseJson);
                if (rj.errorCode != null && rj.errorCode != ResponseJson.NO_DATA) {
                    switch (rj.errorCode) {
                        case 1:
                            bookId = (String) ((Map) rj.data.get(0)).get("bookId");
                            mBook.setId(bookId);
                            UIUtil.showToastSafe("添加成功");
                            dialog = CustomProgressDialog.getPromptDialog2Btn(ManualPersonBookAct.this, "添加成功，继续添加？", "完成", "继续", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setResult(777);
                                    ManualPersonBookAct.this.finish();
                                }
                            }, null);
                            dialog.show();
                            break;
                        default:
                            UIUtil.showToastSafe(rj.errorMsg);
                            break;
                    }
                }
                break;
            case 2://失败
                UIUtil.showToastSafe("上传失败");
                break;
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.act_manual_input2;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        spBookClassify = (AppCompatSpinner) findViewById(R.id.sp_book_classify);
        acivCover = (AppCompatImageView) findViewById(R.id.aciv_cover);
        mMaterialTitle = (AppCompatEditText) findViewById(R.id.acet_title);
        mIntro = (AppCompatEditText) findViewById(R.id.acet_intro);
        acivCover.setOnClickListener(this);
        mAdapter = new BookClassifyAdapter(mClassifys);
        spBookClassify.setAdapter(mAdapter);
        mTitleText.setText("手写资料录入");
        tvSave = (TextView) findViewById(R.id.tv_add_mylib);
        tvSave.setText("保存");
        tvSave.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        //  If null, all callbacks and messages will be removed.
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void initData() {
        capture = new CapturePhoto(this);
        showDefProgress();
        getBookPresenter().getBookClassify(new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                String json = (String) obj;
                DefindResponseJson drj = new DefindResponseJson(json);
                if(drj.errorCode == DefindResponseJson.NO_DATA){
                    UIUtil.showToastSafe("未能获取分类信息，请返回重试");
                    return;
                }
                mClassifys.clear();
                switch (drj.errorCode){
                    case 1:
                        List items = drj.getData().getItems();
                        for(int i=0;i<items.size();i++){
                            EnumConst ec = new EnumConst();
                            ec.setId((String)((Map)items.get(i)).get("ID"));
                            ec.setName((String)((Map)items.get(i)).get("NAME"));
                            ec.setCode((String)((Map)items.get(i)).get("CODE"));
                            ec.setNameSpace((String)((Map)items.get(i)).get("NAMESPACE"));
                            ec.setCreateDate(new Date((Long)((Map)items.get(i)).get("CREATE_DATE")));
                            mClassifys.add(ec);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取分类信息,请返回重试");
                        break;
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                if(obj instanceof String){
                    UIUtil.showToastSafe((String) obj);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_add_mylib:
                //保存
                if(!checkInput()){
                    UIUtil.showToastSafe("*为必填项");
                    return;
                }
                showDefProgress();
                saveBook();
                break;
            case R.id.aciv_cover:
                uimp = new UserInfoImagePOP(this, new itemsOnClick(Const.CAMERA_REQUEST_CODE));
                uimp.showAtLocation(findViewById(R.id.ll_munual), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                break;
        }
    }

    private boolean checkInput() {
        String title = mMaterialTitle.getText().toString();
        if(StringUtil.isEmpty(mMaterialTitle.getText().toString())){
            return false;
        }
        if((null == spBookClassify.getSelectedItem()) || (!(spBookClassify.getSelectedItem() instanceof EnumConst)) || StringUtil.isEmpty(((EnumConst)spBookClassify.getSelectedItem()).getName())){
            return false;
        }
        return true;
    }


    private String bookId;//添加书库成功后返回的bookId
    public void saveBook() {
        mBook = new BookDetail2();
        ArrayMap<String,Object> params = new ArrayMap<>();
        mBook.setTitle(mMaterialTitle.getText().toString().trim());
        mBook.setBookClassify(((EnumConst)spBookClassify.getSelectedItem()).getId());
        if(!StringUtil.isEmpty(mIntro.getText().toString())){
            mBook.setSummary(StringUtil.filter(mIntro.getText().toString()));
        }
        if(!StringUtil.isEmpty(mCoverImagePath)){//加入图片本地手机路径参数
            params.put("img",new File(mCoverImagePath));
        }
        params.put("uid", GlobalParams.getLastLoginUser().getUid());
        params.put("book", JSON.toJSONString(mBook));
        mBook.setFromDouban(false);
        showDefProgress();
        getBookPresenter().uploadBook(params, new CallBack() {
            @Override
            public void onSuccess(final Object obj, int... code) {
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj = obj;
                mHandler.sendMessage(msg);
            }
            @Override
            public void onFailure(Object obj, int... code) {
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
            }
        });
    }

    private String mCoverImagePath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (capture.getActionCode() == CapturePhoto.PICK_ALBUM_IMAGE) {
                Uri targetUri = data.getData();
                if (targetUri != null) {
                    if (requestCode == Const.CAMERA_REQUEST_CODE) {
                        mCoverImagePath = FileUtil.uriToFilePath(targetUri,this);
                        Glide.with(this).load(targetUri).into(acivCover);
                    }
                }
            } else {
                if (requestCode == Const.CAMERA_REQUEST_CODE) {
                    mCoverImagePath = capture.getmCurrentPhotoPath();
                    Glide.with(this).load(mCoverImagePath).into(acivCover);
                }
            }
        }
    }

    /**
     * 弹出窗口监听类
     *
     * @author Austin
     *
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
                    capture.dispatchTakePictureIntent(CapturePhoto.PICK_ALBUM_IMAGE, requestCode);
                    uimp.dismiss();
                    break;
            }
        }

    }

}
