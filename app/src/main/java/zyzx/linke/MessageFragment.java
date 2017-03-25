package zyzx.linke;


import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zyzx.linke.base.BaseFragment;

/**
 * 消息界面
 */
public class MessageFragment extends BaseFragment {
    private View view;


    @Nullable
    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_msg,container,false);
    }

    @Override
    public void initView() {

    }
}
