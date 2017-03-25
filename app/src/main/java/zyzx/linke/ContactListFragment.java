package zyzx.linke;


import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zyzx.linke.base.BaseFragment;

/**
 * 联系人界面
 */
public class ContactListFragment extends BaseFragment {
    private View view;

    @Nullable
    @Override
    public View getView(LayoutInflater inflater,ViewGroup container) {
        return inflater.inflate(R.layout.frag_contact_list, container, false);
    }

    @Override
    public void initView() {
    }

}
