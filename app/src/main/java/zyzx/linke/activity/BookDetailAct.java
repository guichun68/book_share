package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.Tags;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/21.
 * Desc: 通用图书详情页面（非扫码识别图书详情）,接收MyBookDetailVo
 */

public class BookDetailAct extends BaseActivity {

    private ImageView ivBookImage;
    private LinearLayout llShare;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags, tvSummary,tvCatalog,tvAdd2MyLib;
    private TextView tvType,tvShareType;
    private BookDetail2 mBook;
    private MyBookDetailVO mBookVo;
    private Integer friendUserId;//好友id
    private TextView tvBookStatus,tvArea,tvMsg;
//    private Button btnSharer;
    private Button btnBegBorrow;//求借

    @Override
    protected int getLayoutId() {
        return R.layout.act_comon_book_detail;
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
        btnBegBorrow = (Button) findViewById(R.id.btn_beg_borrow);
        tvCatalog = (TextView) findViewById(R.id.tv_catalog);
        tvAdd2MyLib = (TextView) findViewById(R.id.tv_add_mylib);
        llShare = (LinearLayout) findViewById(R.id.ll_share);
//        btnSharer = (Button) findViewById(R.id.btn_sharer);
        findViewById(R.id.ll_sharer).setVisibility(View.GONE);
        tvBookStatus = (TextView) findViewById(R.id.tv_book_status);
        tvArea = (TextView) findViewById(R.id.tv_area);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvShareType = (TextView) findViewById(R.id.tv_share_type);
        llShare.setVisibility(View.VISIBLE);
        tvAdd2MyLib.setClickable(true);
        mTitleText.setText("图书详情");
        tvAdd2MyLib.setVisibility(View.INVISIBLE);
        tvAdd2MyLib.setText("添加");
        tvAdd2MyLib.setOnClickListener(this);
        btnBegBorrow.setOnClickListener(this);
//        btnSharer.setOnClickListener(this);
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
            case R.id.btn_beg_borrow://求借

                break;
            case R.id.tv_book_tags:
                getUserInfo(mBookVo.getRelatedUserId());
                break;
        }
    }

    private UserVO mFriend =new UserVO();
    private void getUserInfo(String userId) {
        showProgress("请稍候…", false);
        getUserPresenter().getUserInfoByUserId2(userId, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgresSingle();
                ResponseJson rj = new ResponseJson((String) obj);
                if(ResponseJson.NO_DATA == rj.errorCode || rj.errorCode!=2){
                    UIUtil.showToastSafe("用户信息获取失败！");
                    return;
                }
                JSONArray ja = rj.data;
                JSONObject jo = (JSONObject) ja.get(0);
                boolean isInRelsBlackList = ((JSONObject)ja.get(1)).getBoolean("isInRelsBlackList");
                mFriend.setUserid(jo.getInteger("userid"));
                mFriend.setUid(jo.getString("id"));
                mFriend.setLoginName(jo.getString("login_name"));
                mFriend.setMobilePhone(jo.getString("mobile_phone"));
                mFriend.setAddress(jo.getString("address"));
                mFriend.setPassword(jo.getString("password"));
                mFriend.setProvinceName(jo.getString("pro"));
                mFriend.setCityName(jo.getString("city"));
                mFriend.setCountyName(jo.getString("county"));
                String genderStr = jo.getString("gender");
                Integer gender = Integer.parseInt(genderStr==null?"0":genderStr);
                mFriend.setGender(gender);
                mFriend.setHobby(jo.getString("hobby"));
                mFriend.setEmail(jo.getString("email"));
                mFriend.setRealName(jo.getString("real_name"));
                mFriend.setCityId(jo.getInteger("city_id"));
                mFriend.setLastLoginTime(jo.getString("last_login_time"));

                mFriend.setSignature(jo.getString("signature"));
                String headTemp = jo.getString("head_icon");
                mFriend.setHeadIcon(StringUtil.isEmpty(headTemp)?null:GlobalParams.BASE_URL+GlobalParams.AvatarDirName+headTemp);
                mFriend.setBak4(jo.getString("bak4"));
                mFriend.setBirthday(jo.getDate("birthday"));
                mFriend.setSchool(jo.getString("school"));
                mFriend.setDepartment(jo.getString("department"));
                mFriend.setDiplomaId(jo.getInteger("diploma_id"));
                mFriend.setSoliloquy(jo.getString("soliloquy"));
                mFriend.setCreditScore(jo.getInteger("credit_score"));
                mFriend.setFromSystem(jo.getInteger("from_system"));
                Bundle ex = new Bundle();
                ex.putSerializable(BundleFlag.FLAG_USER,mFriend);
                gotoActivity(FriendHomePageAct.class,false,ex);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgresSingle();
                UIUtil.showToastSafe("用户信息获取失败！");
            }
        });
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
                bundle.putParcelable(BundleFlag.BOOK,mBook);

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

    private String pro,city,county;
    private void refreshShareView(){
        tvArea.setText("所在地区： "+(StringUtil.isEmpty(pro)?"--":pro)+" "+(StringUtil.isEmpty(city)?"--":city)+" "+(StringUtil.isEmpty(county)?"--":county));
        switch (mBookVo.getBookStatusId()){
            case Const.BOOK_STATUS_BORROWED://借入
                llShare.setVisibility(View.GONE);
                break;
            case Const.BOOK_STATUS_EXCHANGING://交换中
                llShare.setVisibility(View.GONE);
                break;
            case Const.BOOK_STATUS_LOANED://借出
                llShare.setVisibility(View.GONE);
                break;
            case Const.BOOK_STATUS_ONSHELF://在架
                llShare.setVisibility(View.GONE);
                break;
            case Const.BOOK_STATUS_SHARED://分享中
                llShare.setVisibility(View.VISIBLE);
                switch (mBookVo.getShareType()){
                    case 1:
                        tvShareType.setText("分享类型：赠送");
                        break;
                    case 2:
                        tvShareType.setText("分享类型：仅供借阅");
                        break;
                    case 3:
                        tvShareType.setText("分享类型：可借阅，可赠送");
                        break;
                    default:
                        tvShareType.setText("分享类型：未知");
                }
                tvMsg.setText("分享者留言："+mBookVo.getShareMsg());
                tvBookStatus.setText("书籍状态：分享中");
                break;
        }
    }

    @Override
    protected void initData() {
        Intent in = getIntent();
        mBookVo = in.getParcelableExtra("book");
        int from = in.getIntExtra("from",0);
        if(from == Const.FROM_HOME_FRAG){
            btnBegBorrow.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_sharer).setVisibility(View.VISIBLE);
        }
        if(mBookVo.getBookStatusId().equals(Const.BOOK_STATUS_LOANED)){
            tvTags.setText("借阅人："+mBookVo.getRelLoginName());
            tvTags.setClickable(true);
            tvTags.setOnClickListener(this);
            tvTags.setBackground(getResources().getDrawable(R.drawable.btn_sel));
//            tvTags.setBackground(getDrawable(R.drawable.btn_sel));
        }
        mBook = mBookVo.getBook();
        friendUserId = in.getIntExtra(BundleFlag.UID,0);
        refreshShareView();
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

        if(mBookVo.getShareAreaId()!=-1) {
            showDefProgress();
            getUserPresenter().getSharerArea(mBookVo.getShareAreaId(),new CallBack(){

                @Override
                public void onSuccess(Object obj, int... code) {
                    dismissProgress();
                    DefindResponseJson drj = JSON.parseObject((String)obj, DefindResponseJson.class);
                    if(drj.getErrorCode()==0){
                        //获取失败
                        UIUtil.showToastSafe("地理位置获取失败！");
                        return;
                    }
                    if(drj.getErrorCode()==1){
                        pro = (String)((Map)drj.getData().getItems().get(0)).get("pro");
                        city = (String)((Map)drj.getData().getItems().get(0)).get("city");
                        county = (String)((Map)drj.getData().getItems().get(0)).get("dis");
                        if(StringUtil.isEmpty(pro)){
                            pro = city;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshShareView();
                            }
                        });
                    }else{
                        UIUtil.showToastSafe("地理位置获取失败！");
                        return;
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
            String date = new SimpleDateFormat("yyyy-MM-dd").format(mBook.getPubdateDateType());
            if(!date.equals("1970-01-01")){
                tvPublishDate.setText(date);
            }else{
                tvPublishDate.setText("出版日期:");
            }
        }else{
            tvPublishDate.setVisibility(View.GONE);
        }
        //------标签---------------------
        if(!mBookVo.getBookStatusId().equals(Const.BOOK_STATUS_LOANED)){
            if (mBook.getTags() != null && !mBook.getTags().isEmpty()) {
                for (Tags tag : mBook.getTags()) {
                    tvTags.append(tag.getName() + ";");
                }
            }else{
                tvTags.setVisibility(View.GONE);
            }
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
