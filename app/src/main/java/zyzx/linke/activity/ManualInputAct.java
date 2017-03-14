package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import zyzx.linke.R;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.Const;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.utils.CapturePhoto;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.UserInfoImagePOP;

/**
 * Created by austin on 2017/3/10.
 * Desc: 手动录入书籍Act
 */

public class ManualInputAct extends BaseActivity {
    private Dialog progressDialog;
    private TextView tvSave;
    private AppCompatEditText acetBookName,acetISBN,acetAuthor,acetPublisher,acetIntro;
    private AppCompatImageView acivCover;
    private static UserInfoImagePOP uimp;
//    private Uri imageUri;
    private CapturePhoto capture;
    private BookDetail2 mBook;

    @Override
    protected int getLayoutId() {
        return R.layout.act_manual_input;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        progressDialog = CustomProgressDialog.getNewProgressBar(mContext);
        tvSave = (TextView) findViewById(R.id.tv_add_mylib);
        acetBookName = (AppCompatEditText) findViewById(R.id.acet_book_name);
        acetISBN = (AppCompatEditText) findViewById(R.id.acet_isbn);
        acetAuthor = (AppCompatEditText) findViewById(R.id.acet_author);
        acetPublisher = (AppCompatEditText) findViewById(R.id.acet_publisher);
        acetIntro = (AppCompatEditText) findViewById(R.id.acet_intro);
        acivCover = (AppCompatImageView) findViewById(R.id.aciv_cover);

        acivCover.setOnClickListener(this);
        mTitleText.setText("书籍录入");
        tvSave.setText("保存");
        tvSave.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        capture = new CapturePhoto(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.tv_add_mylib:
                //保存按钮
                if(!checkInput()){
                    return;
                }
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
        //检查书名
        if(StringUtil.isEmpty(acetBookName.getText().toString())){
            acetBookName.setError("请填写书名！");
            shake(acetBookName);
            return false;
        }
        if(StringUtil.isEmpty(acetISBN.getText().toString()) && StringUtil.isEmpty(acetAuthor.getText().toString())){
            UIUtil.showToastSafe("ISBN和作者至少填写一项");
            shake(acetAuthor);
            shake(acetISBN);
            return false;
        }
        if(!StringUtil.isEmpty(acetISBN.getText().toString())) {
            if (acetISBN.getText().toString().trim().length() != 10 &&
                    acetISBN.getText().toString().trim().length() != 13) {
                acetISBN.setError("ISBN长度有误");
                shake(acetISBN);
                return false;
            }
            /*if(!StringUtil.isISBN(acetISBN.getText().toString())){
                acetISBN.setError("非法的ISBN号,请检查");
                shake(acetISBN);
                return false;
            }*/
        }

        return true;
    }

    private void shake(View v){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        v.startAnimation(shake);
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
                    capture.dispatchTakePictureIntent(CapturePhoto.PICK_IMAGE, requestCode);
                    uimp.dismiss();
                    break;
            }
        }

    }

    private String mCoverImagePath;
    private File file;// 图片上传 所保存图片file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        isSetHeadIcon = true;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (capture.getActionCode() == CapturePhoto.PICK_IMAGE) {
                Uri targetUri = data.getData();
                if (targetUri != null) {
                    String img_path = FileUtil.uriToFilePath(targetUri,this);
                    if (requestCode == Const.CAMERA_REQUEST_CODE) {
                        mCoverImagePath = img_path;
                        img_path = null;
                        file = new File(mCoverImagePath);
                        Glide.with(this).load(targetUri).into(acivCover);
//                        Bitmap bbb = PicConvertUtil.convertToBitmap(mCoverImagePath, 90, 90);
//                        acivCover.setImageBitmap(bbb);
                    }
                }
            } else {
                if (requestCode == Const.CAMERA_REQUEST_CODE) {
                    mCoverImagePath = capture.getmCurrentPhotoPath();
                    Glide.with(this).load(mCoverImagePath).into(acivCover);
                    /*file = new File(mCoverImagePath);
                    Bitmap bbb = PicConvertUtil.convertToBitmap(mCoverImagePath, 90, 90);
                    acivCover.setImageBitmap(bbb);*/
                }
            }
        }
    }

    public void saveBook() {

        mBook = new BookDetail2();
        HashMap<String,Object> params = new HashMap<>();
        mBook.setTitle(acetBookName.getText().toString().trim());
        String isbn = acetISBN.getText().toString();
        if(!StringUtil.isEmpty(isbn)){
            if(isbn.length()==13){
                mBook.setIsbn13(isbn);
            }else if(isbn.length()==10){
                mBook.setIsbn10(isbn);
            }
        }
        if(!StringUtil.isEmpty(acetAuthor.getText().toString())){
            ArrayList<String> authos = new ArrayList<>();
            authos.add(acetAuthor.getText().toString());
            mBook.setAuthor(authos);
        }
        if(!StringUtil.isEmpty(acetPublisher.getText().toString())){
            mBook.setPublisher(acetPublisher.getText().toString());
        }
        if(!StringUtil.isEmpty(acetIntro.getText().toString())){
            mBook.setSummary(acetIntro.getText().toString());
        }
        if(!StringUtil.isEmpty(mCoverImagePath)){//加入图片本地手机路径参数
            params.put("book_cover",new File(mCoverImagePath));
        }
        params.put("user_id",GlobalParams.gUser.getUserid());
        params.put("book", JSON.toJSONString(mBook));
        mBook.setFromDouban(false);
        progressDialog.show();
        GlobalParams.getBookPresenter().uploadBook(params, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                CustomProgressDialog.dismissDialog(progressDialog);
                UIUtil.showToastSafe("保存成功");
                final String json = (String)obj;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = JSON.parseObject(json);
                        Integer code = jsonObject.getInteger("code");
                        String bookId = jsonObject.getString("book_id");
                        String bookImageUrl = jsonObject.getString("book_image");
                        if(code == null){
                            return;
                        }
                        switch (code){
                            case 200://book有记录，user_book有记录，直接返回，用户已经将该书加入进来了，无须重复操作
                                showDialog("您已经添加过该书了,无须重复添加！");
                                break;
                            case 300://book有记录，user_book无记录，自动关联该书籍成功
                                mBook.setB_id(bookId);
                                mBook.setImage(bookImageUrl);
                                mBook.setImage_medium(bookImageUrl);
                                showAskIfShareOnMapDialog("系统搜索到该书籍信息,已自动关联并添加！是否在地图中分享此书?");
                                break;
                            case 500://book有记录，user_book无记录，自动关联该书籍失败
                                showDialog("未能成功添加,code="+code);
                                break;
                            case 600://book无记录，user_book无记录，插入book数据失败
                                showDialog("未能成功添加书籍,code="+code);
                                break;
                            case 700://book无记录，user_book无记录，插入book数据成功，插入user_book成功
//                                showDialog("添加成功！");
                                mBook.setB_id(bookId);
                                mBook.setImage(bookImageUrl);
                                mBook.setImage_medium(bookImageUrl);
                                showAskIfShareOnMapDialog("添加成功,是否在地图分享此书?");
                                break;
                            case 800://book无记录，user_book无记录，插入book数据成功，插入user_book失败
                                showDialog("未能成功添加书籍,code="+code);
                                break;
                        }
                    }
                });

            }

            @Override
            public void onFailure(Object obj) {
                CustomProgressDialog.dismissDialog(progressDialog);
                UIUtil.showToastSafe("上传失败");
            }
        });
    }
    private Dialog promtDialog;
    public void showDialog(String msg){
        if(promtDialog!=null){
            CustomProgressDialog.dismissDialog(promtDialog);
        }
        promtDialog = CustomProgressDialog.getPromptDialog(mContext,msg,new PromptDialogClicklistener());
        promtDialog.show();
    }
    class PromptDialogClicklistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            CustomProgressDialog.dismissDialog(promtDialog);
        }
    }

    View.OnClickListener myOk;
    View.OnClickListener myCancel;
    Dialog askDialog = null;
    private void showAskIfShareOnMapDialog(String msg) {
        myOk =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(askDialog!=null)
                    askDialog.dismiss();
                Bundle bundle = new Bundle();
//                bundle.putParcelable("book",mBook);
                bundle.putSerializable(BundleFlag.BOOK,mBook);

                gotoActivity(BookShareOnMapAct.class,true,bundle);
            }
        };
        myCancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(askDialog!=null && askDialog.isShowing())
                    askDialog.dismiss();
                finish();
            }
        };

        askDialog =  CustomProgressDialog.getPromptDialog2Btn(this, msg, "分享", "不需要", myOk,myCancel);


        /*AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("添加成功,是否在地图分享此书?");
        dialog.setNegativeButton("分享", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putParcelable("book",mBook);
                gotoActivity(BookShareOnMapAct.class,true,bundle);
            }
        });
        dialog.setPositiveButton("不需要", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });*/
        askDialog.show();
    }

}