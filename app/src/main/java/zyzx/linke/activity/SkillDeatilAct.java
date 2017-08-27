package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.model.bean.UserInfoResult;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by Austin on 2017-08-25.
 * Desc: 技能交换之 发布的技能详情页
 */

public class SkillDeatilAct extends BaseActivity {

    private TextView tvPublisher,tvTitle,tvOwnSkillType,tvOwnSkillName,tvSwapSkillType,tvSkillTip,tvSwapSkillName,tvDesc;
    private String publisherUserId,publisherLoginName,skillDesc;
    private SwapSkillVo swapSkillVo;
    private UserVO mFriend = new UserVO();

    private final int SUCCESS = 0xc929,FAILURE = 0x052D,GET_USER_SUCC=0x789B;

    private MyHandler mHandler = new MyHandler(this);

    private void myHandleMessage(Message msg){
        dismissProgress();
        if(msg.obj==null)return;
        switch (msg.what){
            case SUCCESS:
                DefindResponseJson drj = new DefindResponseJson((String) msg.obj);
                if(DefindResponseJson.NO_DATA == drj.errorCode){
                    UIUtil.showToastSafe("未能获取技能详情,请重试");
                    return;
                }
                switch (drj.errorCode){
                    case 2:
                        List items = drj.data.getItems();
                        for (int i = 0; i < items.size(); i++) {
                            SwapSkillVo sbVO = new SwapSkillVo();
                            JSONObject jo = (JSONObject) items.get(i);
                            publisherUserId =jo.getString("userid");
                            publisherLoginName =jo.getString("login_name");
                            skillDesc = jo.getString("s_desc");
                        }
                        tvPublisher.setText(publisherLoginName);
                        tvDesc.setText(skillDesc);
                        break;
                    case 3:
                        UIUtil.showToastSafe("未能获取技能详情");
                        break;
                }
                break;
            case FAILURE:
                if(StringUtil.isEmpty((String)msg.obj)){
                    UIUtil.showToastSafe("未能获取技能详情");
                }else{
                    UIUtil.showToastSafe((Integer) msg.obj);
                }
                break;
            case GET_USER_SUCC:
                dismissProgress();
                UserInfoResult ui = JSON.parseObject((String)msg.obj, UserInfoResult.class);
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
                    mFriend.setHeadIcon(StringUtil.isEmpty(headTemp)?null: GlobalParams.BASE_URL+GlobalParams.AvatarDirName+headTemp);
                    mFriend.setBak4(ie.getBak4());
                    mFriend.setBirthday(ie.getBirthday());
                    mFriend.setSchool(ie.getSchool());
                    mFriend.setDepartment(ie.getDepartment());
                    mFriend.setDiplomaId(ie.getDiploma_id());
                    mFriend.setSoliloquy(ie.getSoliloquy());
                    mFriend.setCreditScore(ie.getCredit_score());
                    mFriend.setFromSystem(ie.getFrom_system());
                    Intent in = new Intent(mContext,FriendHomePageAct.class);
                    in.putExtra(BundleFlag.SHOWADDRESS,false);
                    in.putExtra(BundleFlag.FLAG_USER,mFriend);
                    this.startActivity(in);
                }else{
                    UIUtil.showToastSafe("未能获取用户信息");
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_skill_detail;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("技能交换详情");
        tvPublisher = (TextView) findViewById(R.id.tv_publisher);
        tvPublisher.setClickable(true);
        tvPublisher.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvOwnSkillType = (TextView) findViewById(R.id.tv_skill_type);
        tvOwnSkillName = (TextView) findViewById(R.id.tv_own_skill);
        tvSwapSkillType = (TextView) findViewById(R.id.tv_swap_skill_type);
        tvSwapSkillName = (TextView) findViewById(R.id.tv_swap_skill_name);
        tvSkillTip = (TextView) findViewById(R.id.tv_skill_tip);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    @Override
    protected void initData() {
       swapSkillVo =  getIntent().getParcelableExtra(BundleFlag.FLAG_SKILL_SWAP);
        if(swapSkillVo == null){
            CustomProgressDialog.getPromptDialog(this, "未能解析数据", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
            return;
        }
        tvTitle.setText(swapSkillVo.getSkillTitle());
        tvOwnSkillType.setText(swapSkillVo.getSkillType());
        tvOwnSkillName.setText(swapSkillVo.getSkillHaveName());
        tvSwapSkillType.setText(swapSkillVo.getSwapSkillType());

        if(StringUtil.isEmpty(swapSkillVo.getSkillWantName())){
            tvSwapSkillName.setVisibility(View.GONE);
            tvSkillTip.setVisibility(View.GONE);
        }else{
            tvSwapSkillName.setVisibility(View.VISIBLE);
            tvSkillTip.setVisibility(View.VISIBLE);
            tvSwapSkillName.setText(swapSkillVo.getSkillWantName());
        }

        showDefProgress();
        getUserPresenter().getSwapSkillDeatil(swapSkillVo.getSwapSkillId(),new CallBack(){
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = mHandler.obtainMessage();
                msg .obj = obj;
                msg .what = SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                Message msg = mHandler.obtainMessage(FAILURE);
                msg.obj = obj;
                mHandler.sendMessage(msg);
            }
        });
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_publisher:
                getUserInfo();
                break;
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<SkillDeatilAct> mActivity;

        MyHandler(SkillDeatilAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SkillDeatilAct act = mActivity==null?null:mActivity.get();
            if(act==null || act.isFinishing()){
                return;
            }
            act.myHandleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void getUserInfo(){
        showDefProgress();
        getUserPresenter().getUserInfoByUid(swapSkillVo.getUid(), new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = mHandler.obtainMessage(GET_USER_SUCC);
                msg.obj = obj;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgresSingle();
                UIUtil.showToastSafe("用户信息获取失败！");
            }
        });
    }
}
