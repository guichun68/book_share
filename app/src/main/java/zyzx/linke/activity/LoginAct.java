package zyzx.linke.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.BeanFactoryUtil;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.db.UserDao;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.SharedPreferencesUtils;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;


/**
 * Created by austin on 2017/2/17.
 * Desc: 登录页面
 */
public class LoginAct extends BaseActivity {
    private AppCompatEditText aetLoginName,aetPsw;
    private CheckBox cbAutoLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.act_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTitleText.setText("用户登录");
        mBackBtn.setVisibility(View.INVISIBLE);

        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_sms_login).setOnClickListener(this);
        findViewById(R.id.tv_forget_psw).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_regist).setOnClickListener(this);

        mTitleText.setClickable(true);
        mTitleText.setOnClickListener(this);

        aetLoginName = (AppCompatEditText) findViewById(R.id.aet_login_name);
        aetPsw = (AppCompatEditText) findViewById(R.id.aet_psw);

        SpannableString ss = new SpannableString("请输入用户名");//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16,true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        aetLoginName.setHint(new SpannedString(ss));

        SpannableString ss2 = new SpannableString("请输入密码");//定义hint的值
        AbsoluteSizeSpan ass2 = new AbsoluteSizeSpan(16,true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass2, 0, ss2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        aetPsw.setHint(new SpannedString(ss2));
    }

    @Override
    protected void initData() {
        aetLoginName.setText(SharedPreferencesUtils.getString(SharedPreferencesUtils.LAST_LOGIN_NAME,""));
        aetPsw.setText(SharedPreferencesUtils.getString(SharedPreferencesUtils.USER_PSW,""));
        if(SharedPreferencesUtils.getBoolean(SharedPreferencesUtils.AUTO_LOGIN)){
            ((Button)findViewById(R.id.btn_login)).performClick();
        }

    }


    @Override
    public void onClick(final View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_login:
                if(!checkInput()){
                    return;
                }
                showProgress("请稍后……");
                getUserPresenter().loginByLoginName(aetLoginName.getText().toString(), aetPsw.getText().toString(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        SharedPreferencesUtils.putString(SharedPreferencesUtils.USER_PSW,aetPsw.getText().toString());
                        loginEaseMob();
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
//                        UIUtil.showToastSafe("用户名或密码错误.");
                        UIUtil.showToastSafe((String)obj);
                    }
                });

                break;
            case R.id.tv_sms_login:
                Intent intent = new Intent(LoginAct.this,SMSLoginAct.class);
                startActivityForResult(intent,200);
                //gotoActivity(SMSLoginAct.class,false);
                break;
            case R.id.tv_forget_psw:
                break;
            case R.id.tv_regist://用户注册
                Intent intent2 = new Intent(LoginAct.this,RegisterAct.class);
                startActivityForResult(intent2,300);
//                gotoActivity(RegisteAct.class,false);
                break;
            case R.id.title_text:
                threeClick();
                break;
            case R.id.tv_about_us:
                gotoActivity(AboutUsAct.class,false);
                break;
        }
    }

    /**
     * 登录环信
     */
    private void loginEaseMob() {
        EMClient.getInstance().login(String.valueOf(SharedPreferencesUtils.getInt(SharedPreferencesUtils.USER_ID, 0)), SharedPreferencesUtils.getString(SharedPreferencesUtils.USER_PSW_Hash, ""), new EMCallBack() {
            @Override
            public void onSuccess() {
                dismissProgress();
                if(cbAutoLogin.isChecked()){
                    SharedPreferencesUtils.putBoolean(SharedPreferencesUtils.AUTO_LOGIN,true);
                }
                //保证进入主页面后本地会话和群组都 load 完毕。
                EMClient.getInstance().chatManager().loadAllConversations();
                EMClient.getInstance().groupManager().loadAllGroups();
                //一并将登录成功的user信息缓存到sqlite
                //先查询sqlite，如果本地没有记录，再添加，如果有记录，则直接更新
                User u = UserDao.getInstance(mContext).queryUserByUid(GlobalParams.gUser.getUserid());
                if(u!=null){
                    UserDao.getInstance(mContext).updateUser(GlobalParams.gUser);
                }else{
                    UserDao.getInstance(mContext).add(GlobalParams.gUser);
                }
//                gotoActivity(IndexActivity2.class,true);
                gotoActivity(HomeAct.class,true);
            }

            @Override
            public void onError(int i, String s) {
                dismissProgress();
                UIUtil.showToastSafe("登录失败，请稍后重试");
                UIUtil.showTestLog("zyzx","登录失败:"+i+s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200 && resultCode==200){//手机号登录页登录成功返回
            gotoActivity(HomeAct.class,true);
        }
        if(requestCode==300 && resultCode==300){//注册页注册成功返回
            gotoActivity(HomeAct.class,true);
        }
    }

    private boolean checkInput() {
        if(StringUtil.isEmpty(aetLoginName.getText().toString())){
            aetLoginName.setError("用户名不能为空");
            Snackbar.make(aetLoginName,"用户名不能为空",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(StringUtil.isEmpty(aetPsw.getText().toString())){
            Snackbar.make(aetLoginName,"密码不能为空",Snackbar.LENGTH_SHORT).show();
            aetPsw.setError("密码不能为空");
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
        ((TextView)view.findViewById(R.id.tv_curr_server)).setText("current server:"+ GlobalParams.BASE_URL);

        final EditText etIpInput = (EditText) view.findViewById(R.id.et_input);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etIpInput.getText().toString())){
                    GlobalParams.BASE_URL = "http://"+etIpInput.getText().toString();
                    UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
                    GlobalParams.refreshIP();
                }
            }
        });

        Button btn25,btn_8080,btnFj,btnChan,btnAddSakura;
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
                UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
            }
        });
        btn25.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_AndroidStudio");
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
            }
        });
        btnAddSakura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = GlobalParams.BASE_URL+"/lk";
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
            }
        });
        btn_8080.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_genymotion");
                GlobalParams.BASE_URL= GlobalParams.BASE_URL+":8080";
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
            }
        });
        btnFj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_fjjsp");
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
            }
        });
        final AlertDialog dialog = adb.create();
        dialog.setView(view, 0,0,0,0);
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
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
