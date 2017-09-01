package zyzx.linke.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.BeanFactoryUtil;
import zyzx.linke.base.EaseUIHelper;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.db.UserDao;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;


/**
 * Created by austin on 2017/2/17.
 * Desc: 登录页面
 */
public class LoginAct extends BaseActivity {
    private AppCompatEditText aetLoginName, aetPsw;
    private CheckBox cbAutoLogin;
    private final int WHAT_THRID_PLAT_FORM = 0x7BF;
    private final int WHAT_ERROR = 0x7CF;


    private static class MyHandler extends Handler{
        WeakReference<LoginAct> mActivity;
        MyHandler(LoginAct activity){
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().myHandleMessage(msg);
        }
    }

    public void myHandleMessage(Message msg){
        switch (msg.what){
            case WHAT_ERROR:
                UIUtil.showToastSafe("登录出错");
                break;
            case WHAT_THRID_PLAT_FORM:
                ArrayMap<String,Object> res = (ArrayMap<String, Object>) msg.obj;
                if(res==null){
                    UIUtil.showToastSafe("授权出错");
                    return;
                }

                getUserPresenter().loginByThirdPlatform(JSON.toJSONString(res), new CallBack() {
                    @Override
                    public void onSuccess(Object obj, int... code) {

                    }

                    @Override
                    public void onFailure(Object obj, int... code) {

                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private Handler mHandler = new MyHandler(this);

    @Override
    protected int getLayoutId() {
        return R.layout.act_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        ShareSDK.initSDK(mContext);

        mTitleText.setText("用户登录");
        mBackBtn.setVisibility(View.INVISIBLE);

        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_sms_login).setOnClickListener(this);
        findViewById(R.id.tv_forget_psw).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_regist).setOnClickListener(this);
        findViewById(R.id.iv_sina).setOnClickListener(this);
        findViewById(R.id.iv_qq).setOnClickListener(this);
        findViewById(R.id.iv_wechat).setOnClickListener(this);

        mTitleText.setClickable(true);
        mTitleText.setOnClickListener(this);

        aetLoginName = (AppCompatEditText) findViewById(R.id.aet_login_name);
        aetPsw = (AppCompatEditText) findViewById(R.id.aet_psw);

        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);//设置字体大小 true表示单位是sp
        SpannableString ss = new SpannableString("请输入用户名");//定义hint的值
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        aetLoginName.setHint(new SpannedString(ss));

        SpannableString ss2 = new SpannableString("请输入密码");//定义hint的值
        ss2.setSpan(ass, 0, ss2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        aetPsw.setHint(new SpannedString(ss2));
    }

    @Override
    protected void initData() {
        aetLoginName.setText(PreferenceManager.getInstance().getCurrentUsername());
        aetPsw.setText(PreferenceManager.getInstance().getCurrentUserPsw());
        if (PreferenceManager.getInstance().getAutoLoginFlag()) {
            (findViewById(R.id.btn_login)).performClick();
        }
    }


    @Override
    public void onClick(final View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_login:
                if(!EaseCommonUtils.isNetWorkConnected(this)){
                    Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkInput()) {
                    return;
                }
                showProgress("正在登录…",true);
                getUserPresenter().loginByLoginName(aetLoginName.getText().toString(), aetPsw.getText().toString(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        loginEaseMob();
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
//                        UIUtil.showToastSafe("用户名或密码错误.");
                        UIUtil.showToastSafe((String) obj);
                    }
                });

                break;
            case R.id.tv_sms_login:
                Intent intent = new Intent(LoginAct.this, SMSLoginAct.class);
                startActivityForResult(intent, 200);
                //gotoActivity(SMSLoginAct.class,false);
                break;
            case R.id.tv_forget_psw:
                gotoActivity(ForgetPswAct.class, false);
                break;
            case R.id.tv_regist://用户注册
                Intent intent2 = new Intent(LoginAct.this, RegisterAct.class);
                startActivityForResult(intent2, 300);
//                gotoActivity(RegisteAct.class,false);
                break;
            case R.id.title_text:
                threeClick();
                break;
            case R.id.tv_about_us:
                gotoActivity(AboutUsAct.class, false);
                break;
            case R.id.iv_qq://qq账号登录
//                UIUtil.showToastSafe("QQ登录实现中，敬请期待…");
//                loginByThirdPlatform(QQ.NAME);
                break;
            case R.id.iv_wechat://微信登录
//                UIUtil.showToastSafe("微信登录实现中，敬请期待…");
//                loginByThirdPlatform(Wechat.NAME);
                break;
            case R.id.iv_sina://新浪微博账号登录
//                loginByThirdPlatform(SinaWeibo.NAME);
                break;
        }
    }

 /*   public void loginByThirdPlatform(String platformName) {
        showProgress("请稍后……");
        Platform platform = ShareSDK.getPlatform(platformName);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        platform.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = mHandler.obtainMessage(WHAT_ERROR);
                mHandler.sendMessage(msg);
                arg2.printStackTrace();

            }

            @Override
            public void onComplete(Platform platform, int action, final HashMap<String, Object> res) {
                UIUtil.showTestLog(Const.TAG,"授权完毕");
//                Platform plat = ShareSDK.getPlatform(QQ.NAME);
                //输出所有授权信息
                UIUtil.showTestLog(Const.TAG+"_userId:",platform.getDb().getUserId());
                Message msg = mHandler.obtainMessage(WHAT_THRID_PLAT_FORM);
                msg.obj = res;
                mHandler.sendMessage(msg);
                platform.getDb().exportData();
                for (Map.Entry<String, Object> entry : res.entrySet()) {
                    UIUtil.showTestLog(Const.TAG,entry.getKey()+"-"+entry.getValue());
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                UIUtil.showToastSafe("授权取消");
            }
        });
        //authorize与showUser单独调用一个即可
//        weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
        platform.SSOSetting(false);//设置false表示使用SSO授权方式,简单来说就是有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。

        platform.showUser(null);//授权并获取用户信息
        //移除授权
        //weibo.removeAccount(true);
    }
*/
    /**
     * 登录环信
     */
    private void loginEaseMob() {
        EMClient.getInstance().login(String.valueOf(PreferenceManager.getInstance().getLastLoginUserId()), PreferenceManager.getInstance().getLastLoginUserPSWHASH(), new EMCallBack() {
            @Override
            public void onSuccess() {
                dismissProgress();
                loginSucc();
            }

            @Override
            public void onError(int i, String s) {
                dismissProgress();
                UIUtil.showToastSafe(s);
                if(s.equals("User is already login") && i==200){
                    loginSucc();
                }
                UIUtil.showTestLog("zyzx", "登录onError日志:" + i + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void loginSucc(){
        PreferenceManager.getInstance().setCurrentUserPSW(aetPsw.getText().toString());
        PreferenceManager.getInstance().setCurrentUserName(aetLoginName.getText().toString());
        //记录用户名和uid
//                EaseUIHelper.getInstance().getUserProfileManager().setCurrentUserNick(u.getLogin_name());
        EaseUIHelper.getInstance().getUserProfileManager().setCurrentUserAvatar(GlobalParams.getLastLoginUser().getHeadIcon());

        if (cbAutoLogin.isChecked()) {
            PreferenceManager.getInstance().setAutoLoginFlag(true);
        }
        //保证进入主页面后本地会话和群组都 load 完毕。
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();
        //一并将登录成功的user信息缓存到sqlite
        //先查询sqlite，如果本地没有记录，再添加，如果有记录，则直接更新
        UserVO u = UserDao.getInstance(mContext).queryUserByUid(GlobalParams.getLastLoginUser().getUserid());
        if (u != null) {
            UserDao.getInstance(mContext).updateUser(GlobalParams.getLastLoginUser());
        } else {
            UserDao.getInstance(mContext).add(GlobalParams.getLastLoginUser());
        }
//                gotoActivity(IndexActivity2.class,true);
//        gotoActivity(HomeAct.class, true);
        Intent in = new Intent(this,HomeAct.class);
        startActivity(in);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == 200) {//手机号登录页登录成功返回
            gotoActivity(HomeAct.class, true);
        }
        if (requestCode == 300 && resultCode == 300) {//注册页注册成功返回
//            gotoActivity(HomeAct.class,true);
        }
    }

    private boolean checkInput() {
//        Snackbar snackbar;
        if (StringUtil.isEmpty(aetLoginName.getText().toString())) {
            /*snackbar = Snackbar.make(aetLoginName, "用户名不能为空", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.info(snackbar).show();*/
            UIUtil.showToastSafe("用户名不能为空");
            return false;
        }
        if (StringUtil.isEmpty(aetPsw.getText().toString())) {
            /*snackbar = Snackbar.make(aetLoginName, "密码不能为空", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.info(snackbar).show();*/
            UIUtil.showToastSafe("密码不能为空");
            return false;
        }
        return true;
    }

    long[] mHits = new long[3];

    private void threeClick() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            ipConfigDialog();
        }
    }

    private void ipConfigDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_ipconfig, null);
        ((TextView) view.findViewById(R.id.tv_curr_server)).setText("current server:" + GlobalParams.BASE_URL);

        final EditText etIpInput = (EditText) view.findViewById(R.id.et_input);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etIpInput.getText().toString())) {
                    GlobalParams.BASE_URL = "http://" + etIpInput.getText().toString();
                    UIUtil.showToastSafe("已设置为:" + GlobalParams.BASE_URL);
                    GlobalParams.refreshIP();
                }
            }
        });

        Button btn25, btn_8080, btnFj, btnChan, btnAddSakura;
        btn25 = (Button) view.findViewById(R.id.btn_25);
        btn_8080 = (Button) view.findViewById(R.id.btn_8080);
        btnChan = (Button) view.findViewById(R.id.btn_chan);
        btnFj = (Button) view.findViewById(R.id.btn_hbx_server);
        btnAddSakura = (Button) view.findViewById(R.id.btn_addSakura);
        btnChan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("chanURL");
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:" + GlobalParams.BASE_URL);
            }
        });
        btn25.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_AndroidStudio");
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:" + GlobalParams.BASE_URL);
            }
        });
        btnAddSakura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = GlobalParams.BASE_URL + "/lk";
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:" + GlobalParams.BASE_URL);
            }
        });
        btn_8080.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_genymotion");
                GlobalParams.BASE_URL = GlobalParams.BASE_URL + ":8080";
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:" + GlobalParams.BASE_URL);
            }
        });
        btnFj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("FuJia");
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:" + GlobalParams.BASE_URL);
            }
        });
        final AlertDialog dialog = adb.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 1500) {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.press_more_then_exit),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
//                finish();
                AppManager.getAppManager().finishAllActivity();
//                System.exit(0);
                /*Platform platf = ShareSDK.getPlatform(Wechat.NAME);
                if (platf.isAuthValid()) {
                    platf.removeAccount(true);
                }*/
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean needCheckPermission = true;
    @Override
    protected void onResume() {
        if(needCheckPermission){
            checkPermissions(needPermissions);
        }
        super.onResume();
    }

    private static final int PERMISSON_REQUESTCODE = 0;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,//--2
            Manifest.permission.ACCESS_FINE_LOCATION,//--2
       /*     Manifest.permission.WRITE_EXTERNAL_STORAGE,//--3
            Manifest.permission.READ_EXTERNAL_STORAGE,//--3
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA//--1*/
    };

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSON_REQUESTCODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {
                    needCheckPermission = false;
                    CustomProgressDialog.getPromptDialog(LoginAct.this,"您拒绝了定位权限，APP将不能使用定位功能。",null).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
