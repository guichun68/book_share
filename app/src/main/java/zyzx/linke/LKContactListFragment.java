package zyzx.linke;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zyzx.linke.activity.ChatActivity;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.SharedPreferencesUtils;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * 联系人界面
 */
public class LKContactListFragment extends BaseFragment {
    private EaseContactListFragment mContactListFrag;

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_contact_list, container, false);
    }

    @Override
    public void initView() {
        mTitleText.setText("联系人");
        showProgress("请稍后……");
        getUserPresenter().getAllMyFriends(SharedPreferencesUtils.getInt(SharedPreferencesUtils.USER_ID, 0), new CallBack() {

            @Override
            public void onSuccess(final Object obj) {


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        String json = (String) obj;
                        if (StringUtil.isEmpty(json)) {
                            UIUtil.showToastSafe("未能获取好友信息");
                            return;
                        }
                        List<User> friends = JSON.parseArray(json, User.class);
                        Map<String, EaseUser> contacts = new HashMap<>();
                        for (int i = 0; i < friends.size(); i++) {
                            EaseUser u2 = new EaseUser(String.valueOf(friends.get(i).getUserid()));
                            u2.setAvatar(friends.get(i).getHead_icon());
                            u2.setNickname(friends.get(i).getLogin_name());
                            u2.setLoginName(friends.get(i).getLogin_name());
                            contacts.put("easeuitest" + i, u2);
                        }
                        mContactListFrag = new EaseContactListFragment();
                        //需要设置联系人列表才能启动fragment
                        mContactListFrag.setContactsMap(contacts);
                        //设置item点击事件
                        mContactListFrag.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

                            @Override
                            public void onListItemClicked(EaseUser user) {
                                Intent in = new Intent(getActivity(),ChatActivity.class);
                                in.putExtra(BundleFlag.LOGIN_NAME,user.getNickname());//设置用户昵称
                                in.putExtra(BundleFlag.UID,String.valueOf(user.getUsername()));//同userId
                                startActivity(in);
                            }
                        });

                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content, mContactListFrag, "contact").commit();

                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                dismissProgress();
            }
        });
    }
}
