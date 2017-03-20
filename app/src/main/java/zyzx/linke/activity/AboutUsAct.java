package zyzx.linke.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.base.GlobalParams;

/**
 * Created by austin on 2017/2/19.
 * Desc: 关于临客
 */
public class AboutUsAct extends BaseActivity {
    private WebView mWebView;
    private FrameLayout loading_view;

    @Override
    protected int getLayoutId() {
        return R.layout.act_about_us;
    }

    public void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.tv_version_name)).setText(AppUtil.getAppVersionName(this));
        mRightBtn.setVisibility(View.VISIBLE);

        mTitleText.setText("关于临客");
        mRightBtn.setVisibility(View.INVISIBLE);
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });

        mWebView = (WebView) findViewById(R.id.wb_grammar_detail);
        loading_view = (FrameLayout) findViewById(R.id.loading_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loading_view.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
/*		mWebView.getSettings().setJavaScriptEnabled(true);//支持js
		mWebView.getSettings().setPluginsEnabled(true);//设置webview支持插件*/
        WebSettings settings = mWebView.getSettings();
        // 设置网页的排列算法（Algorithm：算法） 为“单列结构”
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebView.loadUrl(GlobalParams.urlAboutus);

    }

    @Override
    protected void initData() {

    }
}
