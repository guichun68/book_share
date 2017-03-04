package zyzx.linke.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import zyzx.linke.R;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail;
import zyzx.linke.model.bean.Tags;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/21.
 * Desc: 图书详情页面
 */

public class BookDetailAct extends BaseActivity {
    private static final int BOOKWHAT = 200,BOOKNOTGET=400;
    private Dialog progressDialog;
    private BookHandler handler = new BookHandler();

    private ImageView ivBookImage;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags, tvSummary,tvCatalog,tvAdd2MyLib;
    private BookDetail mBook;

    @Override
    protected int getLayoutId() {
        return R.layout.act_book_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ivBookImage = (ImageView) findViewById(R.id.iv_book_image);
        tvTitle = (TextView) findViewById(R.id.tv_book_title);
        tvAuthor = (TextView) findViewById(R.id.tv_book_author);
        tvPublisher = (TextView) findViewById(R.id.tv_book_publisher);
        tvPublishDate = (TextView) findViewById(R.id.tv_book_publish_date);
        tvTags = (TextView) findViewById(R.id.tv_book_tags);
        tvSummary = (TextView) findViewById(R.id.tv_summary);
        tvCatalog = (TextView) findViewById(R.id.tv_catalog);
        tvAdd2MyLib = (TextView) findViewById(R.id.tv_add_mylib);
        tvAdd2MyLib.setClickable(true);
        mTitleText.setText("图书详情");
        if(!GlobalParams.gIsPersonCenterScan){
            tvAdd2MyLib.setVisibility(View.INVISIBLE);
        }
        tvAdd2MyLib.setText("添加");
        tvAdd2MyLib.setOnClickListener(this);
        progressDialog = CustomProgressDialog.getNewProgressBar(mContext);
        progressDialog.show();
    }

    String bookId;//添加地图成功后返回的bookId

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_add_mylib:
                if(mBook == null){
                    Toast.makeText(mContext, "没有要添加的书籍", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(progressDialog == null){
                    progressDialog = CustomProgressDialog.getNewProgressBar(mContext);
                }
                progressDialog.show();
                mBook.setFromDouban(true);
                GlobalParams.getBookPresenter().addBook2MyLib(mBook,GlobalParams.gUser.getUserid(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        CustomProgressDialog.dismissDialog(progressDialog);
                        String responseJson = (String)obj;
                        JSONObject jsonObject = JSON.parseObject(responseJson);
                        int code = jsonObject.getInteger("code");
                        if(code == 200){
                            bookId = jsonObject.getString("bookId");
                            mBook.setB_id(bookId);
                            UIUtil.showToastSafe("添加成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAskIfShareOnMapDialog();
                                }
                            });
                        }else if(code == 500){
                            UIUtil.showToastSafe("未能成功添加书籍信息");
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        CustomProgressDialog.dismissDialog(progressDialog);
                    }
                });
                break;
        }
    }

    View.OnClickListener myOk;
    View.OnClickListener myCancel;
    Dialog askDialog = null;
    private void showAskIfShareOnMapDialog() {
       myOk =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(askDialog!=null)
                    askDialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putParcelable("book",mBook);
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
        askDialog =  CustomProgressDialog.getPromptDialog2Btn(this, "添加成功,是否在地图分享此书?", "分享", "不需要", myOk,myCancel);


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

    @Override
    protected void initData() {
        Intent in = getIntent();
        String isbn = in.getStringExtra("isbn");
        UIUtil.showTestLog("isbn",isbn);
        GlobalParams.getBookPresenter().getBookDetailByISBN(isbn, new CallBack() {
            @Override
            public void onSuccess(Object obj) {
                CustomProgressDialog.dismissDialog(progressDialog);
                if(obj == null){
                    Toast.makeText(mContext, "未能获取书籍信息", Toast.LENGTH_SHORT).show();
                    handler.sendMessage(Message.obtain(handler,BOOKNOTGET));
                    return;
                }
                BookDetail book = (BookDetail) obj;
                Message msg = handler.obtainMessage();
                msg.obj = book;
                msg.what = BOOKWHAT;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj) {
                CustomProgressDialog.dismissDialog(progressDialog);
                UIUtil.showTestLog("zyzx failure", (String) obj);
                handler.sendMessage(Message.obtain(handler,BOOKNOTGET));
            }
        });



    }

    class BookHandler extends Handler{
        private Dialog promt ;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BOOKNOTGET:
                    if(promt == null){
                        promt = CustomProgressDialog.getPromptDialog(mContext,"未能获取书籍信息",new PromptDialogClickListener());
                    }
                    promt.show();
                    break;

                case BOOKWHAT://成功获取图书信息
                    mBook = (BookDetail) msg.obj;
                    //tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags,tvSummary,tvCatalog;
                    Glide.with(mContext).load(mBook.getImage()).into(ivBookImage);
                    tvTitle.setText(mBook.getTitle());
                    StringBuilder sb = new StringBuilder();
                    for (String author: mBook.getAuthor()) {
                        sb.append(author).append(";");
                    }
                    if(sb.length()>0)
                    {
                        sb.deleteCharAt(sb.length()-1);
                    }
                    tvAuthor.setText(sb);
                    tvPublisher.setText(mBook.getPublisher());
                    tvPublishDate.setText(mBook.getPubdate());
                    for (Tags tag:
                            mBook.getTags()) {
                        tvTags.append(tag.getName()+";");
                    }
                    if(StringUtil.isEmpty(mBook.getSummary())){
                        tvSummary.setText("无");
                    }else{
                        tvSummary.setText(mBook.getSummary());
                    }
                    if(StringUtil.isEmpty(mBook.getCatalog())){
                        tvCatalog.setText("无");
                    }else{
                        tvCatalog.setText(mBook.getCatalog());
                    }
                    break;

            }

        }
        class PromptDialogClickListener implements View.OnClickListener{

            @Override
            public void onClick(View v) {
                CustomProgressDialog.dismissDialog(promt);
                finish();
            }
        }
    }

}
