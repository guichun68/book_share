package zyzx.linke;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zyzx.linke.activity.ChatActivity;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * 联系人界面
 */
public class LKContactListFragment extends BaseFragment {
    private EaseContactListFragment mContactListFrag;
    Map<String, EaseUser> contacts = new HashMap<>();

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_contact_list, container, false);
    }

    @Override
    public void initView() {
        mTitleText.setText("联系人");
        showProgress("请稍后……");
        refreshContacts();
    }

    //获取并刷新联系人列表
    private void refreshContacts() {
        getUserPresenter().getAllMyFriends(new EMValueCallBack<List<EaseUser>>() {
            @Override
            public void onSuccess(final List<EaseUser> easeUsers) {
                dismissProgress();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contacts.clear();
                        for (int i=0;i<easeUsers.size();i++) {
                            contacts.put("friend"+i,easeUsers.get(i));
                        }
                        mContactListFrag = new EaseContactListFragment();
                        //需要设置联系人列表才能启动fragment
                        mContactListFrag.setContactsMap(contacts);
                        //设置item点击事件
                        mContactListFrag.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

                            @Override
                            public void onListItemClicked(final EaseUser user) {
                                getUserPresenter().getUserInfoInConversation(user.getUsername(),new CallBack(){
                                    @Override
                                    public void onSuccess(Object obj) {
                                        dismissProgress();
                                        String userJson = (String) obj;
                                        User userTemp = JSON.parseObject(userJson,User.class);
                                        if(userTemp==null){
                                            UIUtil.showToastSafe("未查询到用户信息,请稍后重试。");
                                            return;
                                        }
                                        if(userTemp.getBak4().equals("500")){
                                            UIUtil.showToastSafe(R.string.error_chat);
                                            return;
                                        }
                                        if(userTemp.getBak4().equals("400")){
                                            UIUtil.showToastSafe("未查询到用户信息,请稍后重试");
                                            return;
                                        }
                                        if(!userTemp.getBak4().equals("200")){
                                            UIUtil.showToastSafe("请求出错，请稍后重试");
                                            return;
                                        }
                                        Intent in = new Intent(getActivity(),ChatActivity.class);
                                        in.putExtra(BundleFlag.LOGIN_NAME,user.getNickname());//设置用户昵称
                                        in.putExtra(BundleFlag.UID,String.valueOf(user.getUsername()));//同userId
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
                            public void onListItemLongClicked(EaseUser user) {
                                showDelOrBlacklistDialog(user);
                            }
                        });

                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content, mContactListFrag, "contact").commit();

                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                dismissProgress();
            }
        });
    }

    /**
     * 显示是否删除和加入黑名单的dialog
     * @param user
     */
    private void showDelOrBlacklistDialog(final EaseUser user) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        View view = View.inflate(mContext,R.layout.dialog_del_blacklist, null);
        TextView tvDel = (TextView) view.findViewById(R.id.tv_del);
        TextView tvAddBlack = (TextView) view.findViewById(R.id.tv_addblack);
        final AlertDialog dialog = adb.create();
        dialog.setView(view,0,0,0,0);

        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("正在删除……");
                getUserPresenter().delFriend(Integer.valueOf(user.getUsername()), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        if(dialog!=null) {
                            dialog.dismiss();
                        }
                        String json = (String) obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe("未能成功删除,请稍后重试!");
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        Integer code = jsonObj.getInteger("code");
                        if(code!=null && code.intValue()==200 ){
                            UIUtil.showToastSafe("已删除"+user.getNickname());
                            //deleteConversation删除和指定用户的对话,参数2：是否删除消息
                            EMClient.getInstance().chatManager()
                                    .deleteConversation(user.getUsername(), true);
                            refreshContacts();
//                            mContactListFrag.setContactsMap(contactListMap);
                        }else {
                            UIUtil.showToastSafe("未能删除成功");
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        UIUtil.showToastSafe("未能成功删除,请稍后重试!");
                        dismissProgress();
                        if(dialog!=null) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        tvAddBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("添加中……");
                getUserPresenter().addBlackList(user.getUsername(),new CallBack(){

                    @Override
                    public void onSuccess(Object obj) {
                        dismissProgress();
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        String json = (String) obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe("添加黑名单失败，请稍后重试！");
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        Integer code = jsonObj.getInteger("code");
                        if(code!=null && code.intValue()==200){
                            UIUtil.showToastSafe("已添加到黑名单");
                            EMClient.getInstance().chatManager()
                                    .deleteConversation(user.getUsername(), true);
                            refreshContacts();
                        }else{
                            UIUtil.showToastSafe("添加失败.");
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        dismissProgress();
                    }
                });

            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GlobalParams.shouldRefreshContactList){
            refreshContacts();
            GlobalParams.shouldRefreshContactList = false;
        }
    }
}
