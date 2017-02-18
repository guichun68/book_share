package zyzx.linke.activity;

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
                gotoActivity(LoginAct.class,true);
            }
        };
        t.schedule(task,3000);
    }

    @Override
    protected void initData() {

    }
}
