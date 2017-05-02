package zyzx.linke;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import zyzx.linke.activity.ChatActivity;
import zyzx.linke.activity.HomeAct;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.UIUtil;


/**
 * 消息界面
 */
public class LKConversationListFragment extends BaseFragment {

    private EaseConversationListFragment mConversationListFrag;
    private UserVO mUserVO;
    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_msg,container,false);
    }

    @Override
    public void initView() {
        mTitleText.setText("会话");
        mBackBtn.setVisibility(View.GONE);
        mConversationListFrag = new EaseConversationListFragment();
        mConversationListFrag.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                showProgress("请稍后……");
                getUserPresenter().getUserInfoInConversation(conversation.conversationId(),new CallBack(){
                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        String userJson = (String) obj;
                        mUserVO = JSON.parseObject(userJson,UserVO.class);
                        if(mUserVO ==null){
                           UIUtil.showToastSafe("未查询到用户信息,请稍后重试。");
                            return;
                        }
                        if(mUserVO.getBak4().equals("500")){
                            UIUtil.showToastSafe(R.string.error_chat);
                            return;
                        }
                        if(mUserVO.getBak4().equals("400")){
                            UIUtil.showToastSafe("未查询到用户信息,请稍后重试");
                            return;
                        }
                        if(!mUserVO.getBak4().equals("200")){
                            UIUtil.showToastSafe("请求出错，请稍后重试");
                            return;
                        }
                        Intent in = new Intent(getActivity(),ChatActivity.class);
                        in.putExtra(BundleFlag.LOGIN_NAME, mUserVO.getLogin_name());
                        in.putExtra(BundleFlag.UID,String.valueOf(mUserVO.getUserid()));
                        startActivity(in);
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                        UIUtil.showToastSafe("未能获取用户信息");

                    }
                });

            }

            @Override
            public void onListItemLongClickedListener(EMConversation conversation) {
                showDelOrBlacklistDialog(conversation);
            }
        });

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content,mConversationListFrag).commit();
    }

    /**
     * 显示是否删除会话的dialog(如删除，将一同删除会话消息)
     * @param conversation 针对的哪条会话
     */
    private void showDelOrBlacklistDialog(final EMConversation conversation) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        View view = View.inflate(mContext,R.layout.dialog_del_blacklist, null);
        TextView tvDel = (TextView) view.findViewById(R.id.tv_del);
        tvDel.setText("删除会话");
        view.findViewById(R.id.tv_addblack).setVisibility(View.GONE);
        final AlertDialog dialog = adb.create();
        dialog.setView(view,0,0,0,0);

        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteConversation删除和指定用户的对话,参数2：是否一并删除消息
                EMClient.getInstance().chatManager()
                        .deleteConversation(conversation.conversationId(), true);
                mConversationListFrag.refresh();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ((HomeAct)getActivity()).removeUnreadMsg();
            }
        });
    }

    public void refresh(){
        mConversationListFrag.refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        mConversationListFrag.refresh();
        ((HomeAct)getActivity()).removeUnreadMsg();
    }
}
