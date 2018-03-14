package zyzx.linke;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.Map;

import zyzx.linke.activity.ChatActivity;
import zyzx.linke.activity.HomeAct;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.global.MyEaseConstant;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;


/**
 * 会话界面
 */
public class LKConversationListFragment extends BaseFragment {

    private EaseConversationListFragment mConversationListFrag;
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
            public void onListItemClicked(final EMConversation conversation) {
                showProgress("请稍候……");
                getUserPresenter().getUserInfoByUserId2(conversation.conversationId(),new CallBack(){
                    @Override
                    public void onSuccess(Object obj, int... code) {
                        dismissProgress();
                        ResponseJson rj = new ResponseJson((String) obj);
                        if(ResponseJson.NO_DATA == rj.errorCode || rj.errorCode!=2){
                            UIUtil.showToastSafe("用户信息获取失败！");
                            return;
                        }
                        JSONArray ja =  rj.data;
                        JSONObject jo = (JSONObject) ja.get(0);
                        boolean isInRelsBlackList = ((JSONObject)ja.get(1)).getBoolean("isInRelsBlackList");
                        if(isInRelsBlackList){
                            UIUtil.showToastSafe("对不起，对方已拒绝您的聊天请求");
                            return;
                        }
                        String loginName = jo.getString("login_name");
                        Intent in = new Intent(getActivity(),ChatActivity.class);
                        in.putExtra(BundleFlag.LOGIN_NAME,loginName);
                        in.putExtra(BundleFlag.UID,conversation.conversationId());
                        startActivity(in);
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
                        UIUtil.showToastSafe("未能获取用户信息");
                    }
                });

            }

            @Override
            public void onListItemLongClicked(EMConversation conversation) {
                if(!conversation.conversationId().equals(MyEaseConstant.ADMIN_USERID)){
                    showDelOrBlacklistDialog(conversation);
                }
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
