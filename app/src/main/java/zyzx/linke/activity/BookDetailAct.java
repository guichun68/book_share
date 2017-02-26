package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.impl.BookPresenter;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.GlobalParams;
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
    protected void initView() {
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
                            UIUtil.showToastSafe("添加成功");
                            finish();
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

                case BOOKWHAT:
                    mBook = (BookDetail) msg.obj;
                    //tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags,tvSummary,tvCatalog;
                    Glide.with(mContext).load(mBook.getImage()).into(ivBookImage);
                    tvTitle.setText(mBook.getTitle());
                    for (String author: mBook.getAuthor()) {
                        tvAuthor.append(author+";");
                    }
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
