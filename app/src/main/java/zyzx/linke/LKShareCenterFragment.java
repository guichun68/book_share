package zyzx.linke;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;

import java.util.HashMap;
import java.util.Map;

import zyzx.linke.base.BaseFragment;

/**
 * 分享中心页面
 */
public class LKShareCenterFragment extends BaseFragment {

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_share_center, container, false);
    }

    @Override
    public void initView() {
        mTitleText.setText("分享中心");
        mBackBtn.setVisibility(View.INVISIBLE);
    }

}
