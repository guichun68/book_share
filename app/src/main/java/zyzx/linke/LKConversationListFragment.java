package zyzx.linke;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import zyzx.linke.activity.ChatActivity;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;


/**
 * 消息界面
 */
public class LKConversationListFragment extends BaseFragment {

    private EaseConversationListFragment mConversationListFrag;
    private User mUser;
    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_msg,container,false);
    }

    @Override
    public void initView() {

        mConversationListFrag = new EaseConversationListFragment();
        mConversationListFrag.hideTitleBar();
        mConversationListFrag.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                showProgress("请稍后……");
                getUserPresenter().getUserInfo(conversation.conversationId(),new CallBack(){
                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        String userJson = (String) obj;
                        mUser = JSON.parseObject(userJson,User.class);
                        if(mUser==null){
                           UIUtil.showToastSafe("未查询到用户信息,请稍后重试。");
                            return;
                        }
                        Intent in = new Intent(getActivity(),ChatActivity.class);
                        in.putExtra(BundleFlag.LOGIN_NAME,mUser.getLogin_name());
                        in.putExtra(BundleFlag.UID,String.valueOf(mUser.getUserid()));
                        startActivity(in);
//                        startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                        UIUtil.showToastSafe("未能获取用户信息");

                    }
                });

            }
        });
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content,mConversationListFrag).commit();
    }
}
