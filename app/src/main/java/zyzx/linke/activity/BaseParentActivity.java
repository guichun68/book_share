package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import zyzx.linke.utils.NetworkUtil;
import zyzx.linke.utils.UIUtil;


public abstract class BaseParentActivity extends CheckPermissionsActivity{
    private static BaseParentActivity mForegroundActivity;
    protected LayoutInflater mInflater;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
            initTitleBar();
        }
        AppManager.getAppManager().addActivity(this);

        mInflater = getLayoutInflater();
        mContext = this;
        initView(savedInstanceState);
        initData();
        checkNetwork();
    }

    private void checkNetwork() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if(!NetworkUtil.checkState_21orNew(mContext)){
                UIUtil.showToastSafe("网络异常");
            }
        }else{
            if(!NetworkUtil.checkNetwork(mContext)){
                UIUtil.showToastSafe("网络异常");
            }
        }
    }

    protected abstract void initView(Bundle saveInstanceState);
    protected abstract void initData();


    /**
     * 打开一个Activity 默认 不关闭当前activity
     * @param clz
     */
    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false, null);
    }

    public void gotoActivity(Class<?> clz,boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    public  void gotoActivity(Class<?> clz,boolean isCloseCurrentActivity,Bundle ex) {
        Intent intent=new Intent(this, clz);
        if(ex!=null)
            intent.putExtras(ex);
        startActivity(intent);
        if (isCloseCurrentActivity) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected void initTitleBar() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mForegroundActivity = this;
    }

    public static BaseParentActivity getForegroundActivity() {
        return mForegroundActivity;
    }
}
