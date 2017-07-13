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

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
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
    private TextView tvType;
    private BookDetail2 mBook;
    private Integer friendUserId;//好友id

    @Override
    protected int getLayoutId() {
        return R.layout.act_comon_book_detail;
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
        tvType = (TextView) findViewById(R.id.tv_type);
        tvAdd2MyLib.setClickable(true);
        mTitleText.setText("图书详情");
        tvAdd2MyLib.setVisibility(View.INVISIBLE);
        tvAdd2MyLib.setText("添加");
        tvAdd2MyLib.setOnClickListener(this);

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
           /* case R.id.tvSharer:
                //进入好友详情页
                Intent in = new Intent(this, FriendHomePageAct.class);
                HashMap<String,String> uidMap = new HashMap<>();
                uidMap.put("uid",friendUserId.toString());
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                break;*/
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
        friendUserId = in.getIntExtra(BundleFlag.UID,0);
        if(!StringUtil.isEmpty(mBook.getBookClassify())){
            switch (mBook.getBookClassify()){
                case Const.CLASSIFY_ZHONGKAO:
                    tvType.setText("中考-手写资料");
                    break;
                case Const.CLASSIFY_GAOKAO:
                    tvType.setText("高考-手写资料");
                    break;
                case Const.CLASSIFY_KAOYAN:
                    tvType.setText("考研-手写资料");
                    break;
                case Const.CLASSIFY_ZIXUE:
                    tvType.setText("自学考试-手写资料");
                    break;
                case Const.CLASSIFY_SIJI:
                    tvType.setText("四级-手写资料");
                    break;
                case Const.CLASSIFY_LIUJI:
                    tvType.setText("六级-手写资料");
                    break;
                case Const.CLASSIFY_GONGWUYUAN:
                    tvType.setText("公务员-手写资料");
                    break;
                case Const.CLASSIFY_SIKAO:
                    tvType.setText("司考-手写资料");
                    break;
                case Const.CLASSIFY_YIXUE:
                    tvType.setText("医学-手写资料");
                    break;
                case Const.CLASSIFY_TUOFU:
                    tvType.setText("托福-手写资料");
                    break;
                case Const.CLASSIFY_YASI:
                    tvType.setText("雅思-手写资料");
                    break;
                case Const.CLASSIFY_GRE:
                    tvType.setText("GRE-手写资料");
                    break;
                case Const.CLASSIFY_JLPT:
                    tvType.setText("JLPT-手写资料");
                    break;
                case Const.CLASSIFY_XIAOYUZHONG:
                    tvType.setText("小语种-手写资料");
                    break;
                case Const.CLASSIFY_BIJI:
                    tvType.setText("课堂笔记-手写资料");
                    break;
                case Const.CLASSIFY_DAAN:
                    tvType.setText("答案-手写资料");
                    break;
                case Const.CLASSIFY_QITA:
                    tvType.setText("其他-手写资料");
                    break;
            }
        }else{
            tvType.setText("普通书籍");
        }

        if(friendUserId==0){
            friendUserId=null;
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
