package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.text.SimpleDateFormat;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.global.MyEaseConstant;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.Tags;
import zyzx.linke.model.bean.UserInfoResult;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/21.
 * Desc: 查看分享中心图书的详情页面,接收MyBookDetailVo
 */

public class ShareBookDetailAct extends BaseActivity {

    private ImageView ivBookImage;
    private LinearLayout llShare;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPublishDate,tvTags, tvSummary,tvCatalog;
    private TextView tvType,tvShareType;
    private BookDetail2 mBook;
    private MyBookDetailVO mBookVo;
    private TextView tvBookStatus,tvArea,tvMsg;
    private Button btnSharer;
    private UserVO mFriend = new UserVO();
    private Button btnBegBorrow;//求借
    private Dialog begDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.act_comon_book_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ivBookImage = (ImageView) findViewById(R.id.iv_book_image);
        ivBookImage.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_book_title);
        tvAuthor = (TextView) findViewById(R.id.tv_book_author);
        tvPublisher = (TextView) findViewById(R.id.tv_book_publisher);
        tvPublishDate = (TextView) findViewById(R.id.tv_book_publish_date);
        tvTags = (TextView) findViewById(R.id.tv_book_tags);
        tvSummary = (TextView) findViewById(R.id.tv_summary);
        btnBegBorrow = (Button) findViewById(R.id.btn_beg_borrow);
        tvCatalog = (TextView) findViewById(R.id.tv_catalog);
        findViewById(R.id.tv_add_mylib).setVisibility(View.GONE);
        llShare = (LinearLayout) findViewById(R.id.ll_share);
        btnSharer = (Button) findViewById(R.id.btn_sharer);
        tvBookStatus = (TextView) findViewById(R.id.tv_book_status);
        tvArea = (TextView) findViewById(R.id.tv_area);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvShareType = (TextView) findViewById(R.id.tv_share_type);
        llShare.setVisibility(View.VISIBLE);
        mTitleText.setText("图书详情");
        btnSharer.setOnClickListener(this);
        btnBegBorrow.setOnClickListener(this);
    }

    String bookId;//添加地图成功后返回的bookId

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_beg_borrow://求借
                showBegDialog();
                break;
            case R.id.btn_sharer://好友名字
                Bundle ex = new Bundle();
                ex.putSerializable("user",mFriend);
                gotoActivity(FriendHomePageAct.class,false,ex);
                break;
            case R.id.iv_book_image:
                break;
        }
    }

    private void showBegDialog() {
        begDialog =
        CustomProgressDialog.getPromptDialog2Btn(this,"确定向对方发送消息"+btnBegBorrow.getText().toString()+"么？","确定","取消",new DialogListener(),null);
        begDialog.show();
    }

    private class DialogListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(StringUtil.isEmpty(mFriend.getUid()) || null==mFriend.getUserid()){
                UIUtil.showToastSafe("未能获取书友信息，请返回重试！");
                begDialog.dismiss();
                return;
            }
            if(GlobalParams.getLastLoginUser().getUid().equals(mFriend.getUid())){
                UIUtil.showToastSafe("无须借阅自己的书籍");
                begDialog.dismiss();
                return;
            }
            showDefProgress();
            sendBegMsg();
        }
    }

    public void sendBegMsg(){
        //这里是扩展自文本消息，如果这个自定义的消息需要用到语音或者图片等，可以扩展自语音、图片消息，亦或是位置消息。
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        String content = "【系统代发消息，点此可快速回复】亲爱的书友您好，我对您分享的书籍《"+mBook.getTitle()+"》非常感兴趣，"+btnBegBorrow.getText().toString()+"。";
        EMTextMessageBody txtBody = new EMTextMessageBody(content);
        message.addBody(txtBody);
        // 增加自己特定的属性，目前SDK支持int、boolean、String这三种属性，可以设置多个扩展属性
        message.setAttribute(MyEaseConstant.EXTRA_UID, GlobalParams.getLastLoginUser().getUid());
        message.setAttribute(MyEaseConstant.EXTRA_USERID,  GlobalParams.getLastLoginUser().getUserid());
        message.setAttribute(MyEaseConstant.EXTRA_BOOKID,mBook.getId());
        message.setAttribute(MyEaseConstant.EXTRA_BOOKTITLE,mBook.getTitle());
        message.setAttribute(MyEaseConstant.EXTRA_SHARE_TYPE,mBookVo.getShareType()+"");

        message.setAttribute(MyEaseConstant.EXTRA_FROM_AVATAR,GlobalParams.getLastLoginUser().getHeadIcon());
        message.setAttribute(MyEaseConstant.EXTRA_TO_AVATAR,mFriend.getHeadIcon());
        message.setAttribute(MyEaseConstant.EXTRA_FROM_NICKNAME,GlobalParams.getLastLoginUser().getLoginName());
        message.setAttribute(MyEaseConstant.EXTRA_TO_NICKNAME,mFriend.getLoginName());

        message.setFrom(GlobalParams.getLastLoginUser().getUserid()+"");
        message.setTo(mFriend.getUserid()+"");
        showDefProgress();
        CustomProgressDialog.dismissDialog(begDialog);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
//                dismissProgress();
                uploadBegRecord();
                UIUtil.showToastSafe("发送成功");
            }

            @Override
            public void onError(int i, String s) {
                dismissProgress();
                UIUtil.showToastSafe("发送失败");
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    //上传请求记录到服务器，保存到server DB
    private void uploadBegRecord(){
        getUserPresenter().sendBegBookMsg(mBookVo.getShareType(),mFriend,GlobalParams.getLastLoginUser().getUserid(),mBook,new CallBack(){

            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                begDialog.dismiss();
                String json = (String) obj;
                DefindResponseJson drj = new DefindResponseJson(json);
                if(DefindResponseJson.NO_DATA == drj.errorCode){
                    UIUtil.showToastSafe("发送失败");
                    return;
                }
                switch (drj.errorCode){
                    case 0:
                        UIUtil.showToastSafe("发送失败！");
                        break;
                    case 1://已经发送过请求了
                        break;
                    case 2:
                    case 3:
                    case 4:
                        UIUtil.showToastSafe(drj.errorMsg);
                        break;
                    default:
                        UIUtil.showToastSafe("发送失败，错误-2");
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showToastSafe("发送失败，请稍后再试");
                begDialog.dismiss();
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
        tvArea.setText("分享地： "+(StringUtil.isEmpty(pro)?"--":pro)+" "+(StringUtil.isEmpty(city)?"--":city)+" "+(StringUtil.isEmpty(county)?"--":county));
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
                        btnBegBorrow.setText("求赠送");
                        break;
                    case 2:
                        tvShareType.setText("分享类型：仅供借阅");
                        btnBegBorrow.setText("求借阅");
                        break;
                    case 3:
                        tvShareType.setText("分享类型：可借阅，可赠送");
                        btnBegBorrow.setText("求借求赠送");
                        break;
                    default:
                        tvShareType.setText("分享类型：未知");
                        btnBegBorrow.setText("求借阅");
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
        mBook = mBookVo.getBook();
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
        showProgress("请稍后…",false);
        getUserPresenter().getUserInfoByUid(mBookVo.getUid(), new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgresSingle();
                UserInfoResult ui = JSON.parseObject((String)obj, UserInfoResult.class);
                if( ui.getErrorCode() == null || ui.getErrorCode().equals("0")){
                    //获取失败
                    UIUtil.showToastSafe("用户信息获取失败！");
                    return;
                }
                if(ui.getErrorCode().equals("1") && !ui.getData().getItems().isEmpty()){
                    UserInfoResult.DataEntity.ItemsEntity ie = ui.getData().getItems().get(0);
                    mFriend.setUserid(ie.getUserid());
                    mFriend.setUid(ie.getId());
                    mFriend.setLoginName(ie.getLogin_name());
                    mFriend.setMobilePhone(ie.getMobile_phone());
                    mFriend.setAddress(ie.getAddress());
                    mFriend.setPassword(ie.getPassword());
                    mFriend.setProvinceName(ie.getPro());
                    mFriend.setCityName(ie.getCity());
                    mFriend.setCountyName(ie.getCounty());
                    String genderStr = ie.getGender();
                    Integer gender = Integer.parseInt(genderStr==null?"0":genderStr);
                    mFriend.setGender(gender);
                    mFriend.setHobby(ie.getHobby());
                    mFriend.setEmail(ie.getEmail());
                    mFriend.setRealName(ie.getReal_name());
                    mFriend.setCityId(ie.getCity_id());
                    mFriend.setLastLoginTime(ie.getLast_login_time());

                    mFriend.setSignature(ie.getSignature());
                    String headTemp = ie.getHead_icon();
                    mFriend.setHeadIcon(StringUtil.isEmpty(headTemp)?null:GlobalParams.BASE_URL+GlobalParams.AvatarDirName+headTemp);
                    mFriend.setBak4(ie.getBak4());
                    mFriend.setBirthday(ie.getBirthday());
                    mFriend.setSchool(ie.getSchool());
                    mFriend.setDepartment(ie.getDepartment());
                    mFriend.setDiplomaId(ie.getDiploma_id());
                    mFriend.setSoliloquy(ie.getSoliloquy());
                    mFriend.setCreditScore(ie.getCredit_score());
                    mFriend.setFromSystem(ie.getFrom_system());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnSharer.setText(mFriend.getLoginName());
                        }
                    });
                }else{
                    UIUtil.showToastSafe("未能获取用户信息");
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgresSingle();
                UIUtil.showToastSafe("用户信息获取失败！");
            }
        });
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
