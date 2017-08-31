package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.SwapBookVO;
import zyzx.linke.model.bean.UserInfoResult;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/8/17.
 * Desc: 单本图书交换详情页
 */

public class BookSwapAct extends BaseActivity {

    private SwapBookVO mSwapBookVO;
    private MyHandler handler = new MyHandler(this);
    private final int SUCCFLAG = 0x738A,FAILUREFLAG = 0x891A,GET_USER_SUCC=0x789B,GET_USER_FAIL = 0x47B;
    private TextView tvBAuthorIntroTip;
    private TextView tvSwapper,tvBType,tvBTitle,tvBAuthor,tvBPublisher,tvBPublishDate,tvBAuthorIntro,tvBSummary;
    private TextView tvSwapTitle,tvSwapAuthor,tvSwapMsg;
    private ImageView ivBookImage;

    @Override
    protected int getLayoutId() {
        return R.layout.act_swap_book_detail;
    }

    private void myHandleMsg(Message msg){
        dismissProgress();
        Object obj = msg.obj;
        switch (msg.what){
            case SUCCFLAG:
                String resp = (String)obj;
                DefindResponseJson drj = new DefindResponseJson(resp);
                if(drj.errorCode == DefindResponseJson.NO_DATA){
                    UIUtil.showToastSafe("未能获取数据");
                    return;
                }
                switch (drj.errorCode){
                    case 2:
//                        AppUtil.getBookDetailVOs(drj.data);
                        List<JSONObject> items = drj.data.getItems();
                        for (JSONObject jo:items) {
                            swapper = jo.getString("login_name");
                            bPublisher = jo.getString("publisher");
                            bPublishDate = jo.getDate("pubdate");
                            bType = jo.getString("book_classify");
                            bAuthorIntro = jo.getString("author_intro");
                            bSummary = jo.getString("summary");
                        }
                        refreshUI();
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取图书信息");
                        return;
                }
                break;
            case FAILUREFLAG:
                UIUtil.showToastSafe("未能获取数据");
                dismissProgress();
                break;

            case GET_USER_SUCC:
                dismissProgress();
                ResponseJson rj = new ResponseJson((String) obj);
                if(ResponseJson.NO_DATA == rj.errorCode || rj.errorCode!=2){
                    UIUtil.showToastSafe("用户信息获取失败！");
                    return;
                }
                JSONArray ja = (JSONArray) rj.data;
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
                Intent in = new Intent(mContext,FriendHomePageAct.class);
                in.putExtra(BundleFlag.FLAG_USER,mFriend);
                in.putExtra(BundleFlag.IS_IN_FRIENDS_BLACLIST,isInRelsBlackList);
                this.startActivity(in);
                break;
        }
    }

    private String swapper,bType,bPublisher,bAuthorIntro,bSummary;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date bPublishDate;

    private void refreshUI() {
        tvSwapper.setText(swapper);

        if(StringUtil.isEmpty(bPublisher)){
            tvBPublisher.setVisibility(View.GONE);
        }else{
            tvBPublisher.setVisibility(View.VISIBLE);
            tvBPublisher.setText(bPublisher);
        }
        if(StringUtil.isEmpty(mSwapBookVO.getBookAuthor())){
            tvBAuthor.setVisibility(View.GONE);
        }else{
            tvBAuthor.setVisibility(View.VISIBLE);
            tvBAuthor.setText(mSwapBookVO.getBookAuthor());
        }
        if(StringUtil.isEmpty(bSummary)){
            tvBSummary.setVisibility(View.GONE);
        }else{
            tvBSummary.setVisibility(View.VISIBLE);
            tvBSummary.setText(bSummary);
        }
        if(null != bPublishDate)
        {
            tvBPublishDate.setText(sdf.format(bPublishDate));
        }
        if(StringUtil.isEmpty(bAuthorIntro)){
            tvBAuthorIntroTip.setVisibility(View.GONE);
            tvBAuthorIntro.setVisibility(View.GONE);
        }else{
            tvBAuthorIntroTip.setVisibility(View.VISIBLE);
            tvBAuthorIntro.setVisibility(View.VISIBLE);
        }

        if(!StringUtil.isEmpty(bType)){
            switch (bType){
                case Const.CLASSIFY_ZHONGKAO:
                    tvBType.setText("中考-手写资料");
                    break;
                case Const.CLASSIFY_GAOKAO:
                    tvBType.setText("高考-手写资料");
                    break;
                case Const.CLASSIFY_KAOYAN:
                    tvBType.setText("考研-手写资料");
                    break;
                case Const.CLASSIFY_ZIXUE:
                    tvBType.setText("自学考试-手写资料");
                    break;
                case Const.CLASSIFY_SIJI:
                    tvBType.setText("四级-手写资料");
                    break;
                case Const.CLASSIFY_LIUJI:
                    tvBType.setText("六级-手写资料");
                    break;
                case Const.CLASSIFY_GONGWUYUAN:
                    tvBType.setText("公务员-手写资料");
                    break;
                case Const.CLASSIFY_SIKAO:
                    tvBType.setText("司考-手写资料");
                    break;
                case Const.CLASSIFY_YIXUE:
                    tvBType.setText("医学-手写资料");
                    break;
                case Const.CLASSIFY_TUOFU:
                    tvBType.setText("托福-手写资料");
                    break;
                case Const.CLASSIFY_YASI:
                    tvBType.setText("雅思-手写资料");
                    break;
                case Const.CLASSIFY_GRE:
                    tvBType.setText("GRE-手写资料");
                    break;
                case Const.CLASSIFY_JLPT:
                    tvBType.setText("JLPT-手写资料");
                    break;
                case Const.CLASSIFY_XIAOYUZHONG:
                    tvBType.setText("小语种-手写资料");
                    break;
                case Const.CLASSIFY_BIJI:
                    tvBType.setText("课堂笔记-手写资料");
                    break;
                case Const.CLASSIFY_DAAN:
                    tvBType.setText("答案-手写资料");
                    break;
                case Const.CLASSIFY_QITA:
                    tvBType.setText("其他-手写资料");
                    break;
            }
        }else{
            tvBType.setText("普通书籍");
        }
        tvBTitle.setText(mSwapBookVO.getBookTitle());
        tvSwapTitle.setText("《"+mSwapBookVO.getSwapBookTitle()+"》");
        tvSwapAuthor.setText("作者："+mSwapBookVO.getSwapBookAuthor());

        if(StringUtil.isEmpty(mSwapBookVO.getSwapMsg())){
            tvSwapMsg.setText("无");
        }else{
            tvSwapMsg.setText(mSwapBookVO.getSwapMsg());
        }
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mSwapBookVO = getIntent().getParcelableExtra(BundleFlag.SWAP_BOOK_VO);
        if(mSwapBookVO == null){
            UIUtil.showToastSafe("解析图书信息出错");
            this.finish();
        }
        mTitleText.setText("交换详情");
        tvSwapper = (TextView) findViewById(R.id.tv_swapper);
        tvBAuthorIntroTip = (TextView) findViewById(R.id.tv_author_intro_tip);
        tvSwapper.setOnClickListener(this);
        ivBookImage = (ImageView) findViewById(R.id.iv_book_image);
        tvBType = (TextView) findViewById(R.id.tv_book_type);
        tvBTitle = (TextView) findViewById(R.id.tv_book_title);
        tvBAuthor = (TextView) findViewById(R.id.tv_book_author);
        tvBPublisher = (TextView) findViewById(R.id.tv_book_publisher);
        tvBPublishDate = (TextView) findViewById(R.id.tv_book_publish_date);
        tvBAuthorIntro = (TextView) findViewById(R.id.tv_author_intro);
        tvBSummary = (TextView) findViewById(R.id.tv_summary);
        tvSwapTitle = (TextView) findViewById(R.id.tv_swap_title);
        tvSwapAuthor = (TextView) findViewById(R.id.tv_swap_author);
        tvSwapMsg = (TextView) findViewById(R.id.tv_swap_msg);

        Glide.with(this).load(mSwapBookVO.getBookImageLarge()).placeholder(R.mipmap.defaultcover).into(ivBookImage);
        refreshUI();
    }


    @Override
    protected void initData() {
        showDefProgress();
        getBookPresenter().getSwapBookInfo(mSwapBookVO.getUserBookId(), new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage(SUCCFLAG);
                msg.obj = obj;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                handler.sendEmptyMessage(FAILUREFLAG);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_swapper:
                getUserInfo();
                break;
        }
    }

    private UserVO mFriend = new UserVO();
    public void getUserInfo(){
        if(swapper!=null && GlobalParams.getLastLoginUser().getLoginName().equals(swapper)){
            gotoActivity(PersonalCenterAct.class);
            return;
        }

        showDefProgress();
        getUserPresenter().getUserInfoByUid2(mSwapBookVO.getUserId(), new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage(GET_USER_SUCC);
                msg.obj = obj;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgresSingle();
                UIUtil.showToastSafe("用户信息获取失败！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class MyHandler extends Handler {
        WeakReference<BookSwapAct> mAct;
        MyHandler(BookSwapAct page){
            this.mAct = new WeakReference<>(page);
        }

        @Override
        public void handleMessage(Message msg) {
            BookSwapAct act = mAct==null?null:mAct.get();
            if(act == null){
                return;
            }
            act.myHandleMsg(msg);
        }
    }
}
