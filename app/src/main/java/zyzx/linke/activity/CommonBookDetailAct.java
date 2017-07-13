package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.Tags;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/21.
 * Desc: 通用图书详情页面（非扫码识别图书详情）
 */

public class CommonBookDetailAct extends BaseActivity {

    private ImageView ivBookImage;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags, tvSummary,tvCatalog,tvAdd2MyLib;
    private BookDetail2 mBook;
    private TextView tvSharer;//分享者
    private Integer friendUserId;//好友id
    private Double longi,lati;//书籍位置
    private boolean showExtraInfo;//是否显示分享者和地址信息（如果从好友页面过来则不显示这些信息，从首页过来则显示）

    @Override
    protected int getLayoutId() {
        return R.layout.act_comon_book_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ivBookImage = (ImageView) findViewById(R.id.iv_book_image);
        tvTitle = (TextView) findViewById(R.id.tv_book_title);
        tvAuthor = (TextView) findViewById(R.id.tv_book_author);
        tvSharer = (TextView) findViewById(R.id.tvSharer);
        tvPublisher = (TextView) findViewById(R.id.tv_book_publisher);
        tvPublishDate = (TextView) findViewById(R.id.tv_book_publish_date);
        tvTags = (TextView) findViewById(R.id.tv_book_tags);
        tvSummary = (TextView) findViewById(R.id.tv_summary);
        tvCatalog = (TextView) findViewById(R.id.tv_catalog);
        tvAdd2MyLib = (TextView) findViewById(R.id.tv_add_mylib);
        tvAdd2MyLib.setClickable(true);
        mTitleText.setText("图书详情");
        tvAdd2MyLib.setVisibility(View.INVISIBLE);
        tvAdd2MyLib.setText("添加");
        tvAdd2MyLib.setOnClickListener(this);
        tvSharer.setOnClickListener(this);
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

                showDefProgress();
                mBook.setFromDouban(true);
                getBookPresenter().addBook2MyLib(mBook,GlobalParams.getLastLoginUser().getUserid(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        dismissProgress();
                        String responseJson = (String)obj;
                        JSONObject jsonObject = JSON.parseObject(responseJson);
                        int code2 = jsonObject.getInteger("code");
                        if(code2 == 200){
                            bookId = jsonObject.getString("bookId");
                            mBook.setId(bookId);
                            UIUtil.showToastSafe("添加成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAskIfShareOnMapDialog();
                                }
                            });
                        }else if(code2 == 500){
                            UIUtil.showToastSafe("未能成功添加书籍信息");
                        }
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
                    }
                });
                break;
            case R.id.tvSharer:
                //进入好友详情页
                Intent in = new Intent(this, FriendHomePageAct.class);
                HashMap<String,String> uidMap = new HashMap<>();
                uidMap.put("uid",friendUserId.toString());
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
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
//                bundle.putParcelable("book",mBook);
                bundle.putSerializable(BundleFlag.BOOK,mBook);

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

        askDialog.show();
    }

    @Override
    protected void initData() {
        Intent in = getIntent();
        mBook = (BookDetail2)in.getSerializableExtra("book");
        showExtraInfo = in.getBooleanExtra(BundleFlag.SHOWADDRESS,true);
        friendUserId = in.getIntExtra(BundleFlag.UID,0);

        tvSharer.setText(in.getStringExtra(BundleFlag.SHARER));
        longi = in.getDoubleExtra(BundleFlag.LONGITUDE,0);
        lati = in.getDoubleExtra(BundleFlag.LATITUDE,0);


        if(friendUserId==0){
            friendUserId=null;
        }
        if(longi.intValue()==0){
            longi = null;
        }
        if(lati.intValue() ==0){
            lati=null;
        }

        tvAdd2MyLib.setVisibility(View.INVISIBLE);
        refreshBookInfo();
    }

    private void refreshBookInfo() {
        String imageUrl = AppUtil.getMostDistinctPicUrl(mBook);
        if(imageUrl != null){
            Glide.with(mContext).load(imageUrl).into(ivBookImage);
        }
        tvTitle.setText(mBook.getTitle());
        //作者------------------------------
        if(mBook.getAuthor()!=null && !mBook.getAuthor().isEmpty()){
            StringBuilder sb = new StringBuilder();
            for (String author : mBook.getAuthor()) {
                sb.append(author).append(";");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            if(StringUtil.isEmpty(sb.toString())){
                tvAuthor.setVisibility(View.GONE);
            }else{
                tvAuthor.setText(sb);
            }
        }else{
            tvAuthor.setVisibility(View.GONE);
        }
        //出版社-----------------------------
        if(StringUtil.isEmpty(mBook.getPublisher())){
            tvPublisher.setVisibility(View.GONE);
        }else{
            tvPublisher.setText(mBook.getPublisher());
        }
        //设置出版日期------start---------

        if(mBook.getPubdateDateType()!=null){
            tvPublishDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(mBook.getPubdateDateType()));
        }else{
            tvPublishDate.setVisibility(View.GONE);
        }
        //------标签---------------------
        if (mBook.getTags() != null && !mBook.getTags().isEmpty()) {
            for (Tags tag : mBook.getTags()) {
                tvTags.append(tag.getName() + ";");
            }
        }else{
            tvTags.setVisibility(View.GONE);
        }
        //-----简介-----------------------
        if (StringUtil.isEmpty(mBook.getSummary())) {
            tvSummary.setText("暂无简介");
        } else {
            tvSummary.setText(mBook.getSummary());
        }
        if (StringUtil.isEmpty(mBook.getCatalog())) {
            tvCatalog.setText("暂无目录");
        } else {
            tvCatalog.setText(mBook.getCatalog());
        }
    }

}
