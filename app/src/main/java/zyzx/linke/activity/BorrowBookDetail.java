package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BorrowedInVO;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserInfoResult;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by Austin on 2017-08-28.
 * Desc: 我借入的单本书籍详情
 */

public class BorrowBookDetail extends BaseActivity {

    private ImageView ivBookImage;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPublishDate, tvSummary,tvCatalog;
    private TextView tvType,tvShareType;
//    private MyBookDetailVO mBookVo;
    private String userBookId;
    private TextView tvBookStatus,tvMsg;
    private TextView tvSharer;
    private BorrowedInVO mBorrowInVO;
    private UserVO mFriend = new UserVO();


    private final int SUCCESS =0x054D ,FAILURE = 0x47B1B;


    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg) {
        dismissProgress();
        switch (msg.what) {
            case SUCCESS:
                DefindResponseJson drj = new DefindResponseJson((String)msg.obj);
                if(DefindResponseJson.NO_DATA == drj.errorCode){
                    UIUtil.showToastSafe("未能获取书籍详情");
                    return;
                }
                Map data = (Map)drj.getData().getItems().get(0);
                tvType.setText(AppUtil.getStringClassify((String)data.get("book_classify")));
                String pb = (String)data.get("publisher");
                tvPublisher.setText(StringUtil.isEmpty(pb)?"出版社：暂无":pb);

                Date pbDate = new Date((long)data.get("pubdate"));
                String d =new SimpleDateFormat("yyyy-MM-dd").format(pbDate);
                if(StringUtil.isEmpty(d)){
                    tvPublishDate.setText("出版日期:暂无");
                }else{
                    tvPublishDate.setText(d);
                }
                String sum = (String)data.get("summary");
                tvSummary.setText(StringUtil.isEmpty(sum)?"暂无":sum);
                String cat = (String) data.get("catalog");
                tvCatalog.setText(StringUtil.isEmpty(cat)?"暂无":cat);
                break;
            case FAILURE:
                UIUtil.showToastSafe("未能获取书籍详情");
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_borrow_book_detail;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ivBookImage = (ImageView) findViewById(R.id.iv_book_image);
        ivBookImage.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_book_title);
        tvAuthor = (TextView) findViewById(R.id.tv_book_author);
        tvPublisher = (TextView) findViewById(R.id.tv_book_publisher);
        tvPublishDate = (TextView) findViewById(R.id.tv_book_publish_date);
        tvSummary = (TextView) findViewById(R.id.tv_summary);
        tvCatalog = (TextView) findViewById(R.id.tv_catalog);
        findViewById(R.id.tv_add_mylib).setVisibility(View.GONE);
        tvSharer = (TextView) findViewById(R.id.tv_sharer);
        tvBookStatus = (TextView) findViewById(R.id.tv_book_status);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvShareType = (TextView) findViewById(R.id.tv_share_type);
        mTitleText.setText("图书详情");
        tvSharer.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        mBorrowInVO = getIntent().getParcelableExtra(BundleFlag.BOOK_BORROW);
        if(mBorrowInVO==null){
            UIUtil.showToastSafe("未能解析图书信息");
            return;
        }
        refreshUI();
        showDefProgress();
        getUserPresenter().getBookInfo(mBorrowInVO.getBookId(),new CallBack(){
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage(SUCCESS);
                msg.obj = obj;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                handler.sendEmptyMessage(FAILURE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_sharer://好友名字
                getUserInfo(mBorrowInVO.getUid());
//                Bundle ex = new Bundle();
//                ex.putSerializable("user",mFriend);
//                gotoActivity(FriendHomePageAct.class,false,ex);
                break;
            case R.id.iv_book_image:
                break;
        }
    }

    private void refreshUI(){
        Glide.with(this).load(mBorrowInVO.getBookImage()).placeholder(R.mipmap.defaultcover).into(ivBookImage);
        tvTitle.setText(mBorrowInVO.getBookTitle());
        tvAuthor.setText(mBorrowInVO.getBookAuthor());
        tvSharer.setText("所有者:"+mBorrowInVO.getOwnerName());
    }


    private static class MyHandler extends Handler {
        WeakReference<BorrowBookDetail> mActivity;
        MyHandler(BorrowBookDetail act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            BorrowBookDetail act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
    }

    private void getUserInfo(String friendUid) {
        showProgress("请稍后…", false);
        getUserPresenter().getUserInfoByUserId2(friendUid, new CallBack() {
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
}
