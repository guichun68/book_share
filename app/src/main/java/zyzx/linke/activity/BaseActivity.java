package zyzx.linke.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import zyzx.linke.R;


public abstract class BaseActivity extends BaseParentActivity implements View.OnClickListener{

    protected TextView mTitleText;
    protected ImageView mBackBtn;
    protected ImageView mRightBtn;

    @Override
    protected void initTitleBar() {
//        Log.e("initTitleBar:",this.toString());
        mTitleText = (TextView) findViewById(R.id.title_text);
        mBackBtn = (ImageView) findViewById(R.id.back_img);
        mRightBtn = (ImageView) findViewById(R.id.right_img);
        try{
            mBackBtn.setOnClickListener(this);
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new RuntimeException("check if you forget to include title layout in your layout xml file.");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
