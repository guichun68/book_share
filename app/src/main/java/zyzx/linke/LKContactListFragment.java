package zyzx.linke;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import zyzx.linke.db.UserDao;
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
                        contacts.clear();
                        for (int i = 0; i < friends.size(); i++) {
                            EaseUser u2 = new EaseUser(String.valueOf(friends.get(i).getUserid()));
                            u2.setAvatar(friends.get(i).getHead_icon());
                            u2.setNickname(friends.get(i).getLogin_name());
                            u2.setLoginName(friends.get(i).getLogin_name());
                            contacts.put("friend"+i, u2);
                            UserDao.getInstance(mContext).add(friends.get(i));
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
            public void onFailure(Object obj) {
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
}
