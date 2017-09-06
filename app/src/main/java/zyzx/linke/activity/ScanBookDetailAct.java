package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.Tags;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/21.
 * Desc: 扫码识别图书详情页(区别于CommonBookDetail)
 */

public class ScanBookDetailAct extends BaseActivity {
    private static final int BOOKWHAT = 200, BOOKNOTGET = 400;
    private BookHandler handler = new BookHandler(this);

    private ImageView ivBookImage;
    private TextView tvTitle, tvAuthor, tvPublisher, tvPublishDate, tvTags, tvSummary, tvCatalog, tvAdd2MyLib;
    private BookDetail2 mBook;
//    private boolean isFromIndexAct;//是否是从首页进入的图书详情页（如果是，则不显示添加按钮）

    @Override
    protected int getLayoutId() {
        return R.layout.act_book_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        mTitleText.setText("详情");
        tvAdd2MyLib.setText("加入我的书架");
        tvAdd2MyLib.setOnClickListener(this);
        showDefProgress();
    }

    private String bookId;//添加书库成功后返回的bookId

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_add_mylib:
                if (mBook == null) {
                    Toast.makeText(mContext, "没有要添加的书籍", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDefProgress();
                mBook.setFromDouban(true);
                StringBuilder authorsSB = new StringBuilder(),translatorSB = new StringBuilder();
                for(String a:mBook.getAuthor()){
                    authorsSB.append(a);
                }
                for(String b:mBook.getTranslator()){
                    translatorSB.append(b);
                }

                getBookPresenter().addBook2MyLib(mBook,GlobalParams.getLastLoginUser().getUserid(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        dismissProgress();
                        String responseJson = (String) obj;
                        ResponseJson rj = new ResponseJson(responseJson);
                        if(rj.errorCode!=null && rj.errorCode != ResponseJson.NO_DATA) {
                            switch (rj.errorCode) {
                                case 1:
                                    bookId = (String) ((Map) rj.data.get(0)).get("bookId");
                                    mBook.setId(bookId);
                                    UIUtil.showToastSafe("添加成功");
                                    break;
                                default:
                                    UIUtil.showToastSafe(rj.errorMsg);
                            }
                        }else{
                            UIUtil.showToastSafe("未能成功添加");
                        }
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        UIUtil.showToastSafe("添加失败");
                        dismissProgress();
                    }
                });
                break;
        }
    }

    View.OnClickListener myOk;
    View.OnClickListener myCancel;
    Dialog askDialog = null;

    @Override
    protected void initData() {
        Intent in = getIntent();
        mBook = (BookDetail2)in.getParcelableExtra("book");
        if(mBook!=null){
            Message msg = handler.obtainMessage();
            msg.obj = mBook;
            msg.what = BOOKWHAT;
            handler.sendMessage(msg);
            return;
        }
        String isbn = in.getStringExtra("isbn");
        UIUtil.showTestLog("isbn", isbn);
        getBookPresenter().getBookDetailByISBN(isbn, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                if (obj == null) {
                    Toast.makeText(mContext, "未能获取书籍信息", Toast.LENGTH_SHORT).show();
                    handler.sendMessage(Message.obtain(handler, BOOKNOTGET));
                    return;
                }
                BookDetail2 book = (BookDetail2) obj;
                Message msg = handler.obtainMessage();
                msg.obj = book;
                msg.what = BOOKWHAT;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showTestLog("zyzx failure", (String) obj);
                handler.sendMessage(Message.obtain(handler, BOOKNOTGET));
            }
        });
    }
    private Dialog prompt;
    private void myHandleMessage(Message msg){

        dismissProgress();
        switch (msg.what) {
            case BOOKNOTGET:
                if (prompt == null) {
                    prompt = CustomProgressDialog.getPromptDialog(ScanBookDetailAct.this, "未能获取书籍信息", new PromptDialogClickListener());
                }
                prompt.show();
                break;

            case BOOKWHAT://成功获取图书信息
                mBook = (BookDetail2) msg.obj;
                //tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags,tvSummary,tvCatalog;
                String imageUrl = AppUtil.getMostDistinctPicUrl(mBook);
                if(imageUrl!=null){
                    Glide.with(mContext).load(imageUrl).into(ivBookImage);
                }
                tvTitle.setText(mBook.getTitle());
                StringBuilder sb = new StringBuilder();
                for (String author : mBook.getAuthor()) {
                    sb.append(author).append(";");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                tvAuthor.setText(sb);
                tvPublisher.setText(mBook.getPublisher());
                if(mBook.getPubdateDateType()!=null){
                    tvPublishDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(mBook.getPubdateDateType()));
                }else{
                    tvPublishDate.setVisibility(View.GONE);
                }
                if (mBook.getTags() != null) {
                    for (Tags tag : mBook.getTags()) {
                        tvTags.append(tag.getName() + ";");
                    }
                }
                if (StringUtil.isEmpty(mBook.getSummary())) {
                    tvSummary.setText("无");
                } else {
                    tvSummary.setText(mBook.getSummary());
                }
                if (StringUtil.isEmpty(mBook.getCatalog())) {
                    tvCatalog.setText("无");
                } else {
                    tvCatalog.setText(mBook.getCatalog());
                }
                break;

        }
    }

    class PromptDialogClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CustomProgressDialog.dismissDialog(prompt);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class BookHandler extends Handler {

        WeakReference<ScanBookDetailAct> mActivity;

        BookHandler(ScanBookDetailAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            ScanBookDetailAct act = mActivity == null?null:mActivity.get();
            if(act == null || act.isFinishing()){
                return;
            }
            act.myHandleMessage(msg);
        }
    }


}
