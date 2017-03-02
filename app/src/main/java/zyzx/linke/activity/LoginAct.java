package zyzx.linke.activity;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import zyzx.linke.R;
import zyzx.linke.model.CallBack;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.BeanFactoryUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.GlobalParams;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;


/**
 * Created by austin on 2017/2/17.
 * Desc: 登录页面
 */
public class LoginAct extends BaseActivity{
    private AppCompatEditText aetLoginName,aetPsw;
    private Button btnLogin;
    private TextView tvSmsLogin,tvForgetPsw,tvRegist,tvAboutus;
    private Dialog progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.act_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        progressBar = CustomProgressDialog.getNewProgressBar(this);
        mTitleText.setText("用户登录");
        mBackBtn.setVisibility(View.INVISIBLE);

        btnLogin = (Button) findViewById(R.id.btn_login);
        tvSmsLogin = (TextView) findViewById(R.id.tv_sms_login);
        tvForgetPsw = (TextView) findViewById(R.id.tv_forget_psw);
        tvAboutus = (TextView) findViewById(R.id.tv_about_us);
        tvRegist = (TextView) findViewById(R.id.tv_regist);

        mTitleText.setClickable(true);
        tvAboutus.setClickable(true);
        mTitleText.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvSmsLogin.setOnClickListener(this);
        tvForgetPsw.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        tvAboutus.setOnClickListener(this);

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
    }

    @Override
    public void onClick(final View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_login:
                if(!checkInput()){
                    return;
                }
                progressBar.show();
                GlobalParams.getUserPresenter().loginByLoginName(aetLoginName.getText().toString(), aetPsw.getText().toString(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        CustomProgressDialog.dismissDialog(progressBar);
                        gotoActivity(HomeAct.class,true);
                    }

                    @Override
                    public void onFailure(Object obj) {
                        CustomProgressDialog.dismissDialog(progressBar);
//                        UIUtil.showToastSafe("用户名或密码错误.");
                        Snackbar.make(view, "访问超时，请重试", Snackbar.LENGTH_SHORT).show();
//                        Snackbar.make(view, (String)obj, Snackbar.LENGTH_SHORT).show();
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
                Intent intent2 = new Intent(LoginAct.this,RegisteAct.class);
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

        Button btn2_2,btn_3_3,btnFj,btnChan,btnAddSakura;
        btn2_2 = (Button) view.findViewById(R.id.btn_2_2);
        btn_3_3 = (Button) view.findViewById(R.id.btn_3_3);
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
        btn2_2.setOnClickListener(new View.OnClickListener() {

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
                GlobalParams.BASE_URL = GlobalParams.BASE_URL+"/SakuraGoServer";
                GlobalParams.refreshIP();
                UIUtil.showToastSafe("已设置为:"+GlobalParams.BASE_URL);
            }
        });
        btn_3_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalParams.BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_genymotion");
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

}
