package zyzx.linke.activity;

import android.content.Intent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import zyzx.linke.R;

/**
 * Created by austin on 2017/1/23.
 * Desc: Splash page
 */

public class SplashAct extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.act_splash;
    }

    @Override
    protected void initView() {
        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                SplashAct.this.finish();
//                gotoActivity(LoginAct.class,true);
                /*Intent intent = new Intent(SplashAct.this, LoginAct.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);*/
                startActivity(new Intent(SplashAct.this, LoginAct.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        };
        t.schedule(task,3000);
    }

    @Override
    protected void initData() {

    }
}
