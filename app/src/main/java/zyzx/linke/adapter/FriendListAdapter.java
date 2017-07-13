package zyzx.linke.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.activity.FriendHomePageAct;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/24.
 * Desc: 好友查找页面好友list Adapter
 */

public class FriendListAdapter extends BaseAdapter {

    List<UserVO> mUserVOs;
    public FriendListAdapter(List<UserVO> userVOs){
        this.mUserVOs = userVOs;
    }

    @Override
    public int getCount() {
        return mUserVOs.size();
    }

    @Override
    public UserVO getItem(int position) {
        return mUserVOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FriendViewHolder vh;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(),R.layout.item_friend,null);
            vh = new FriendViewHolder(convertView,getItem(position), parent.getContext());
            convertView.setTag(vh);
        }else{
            vh = (FriendViewHolder) convertView.getTag();
        }
        //头像处理
        if(!StringUtil.isEmpty(getItem(position).getHeadIcon())){
            Glide.with(parent.getContext()).load(getItem(position).getHeadIcon()).into(vh.ivHeadIcon);
        }else{
            Glide.with(parent.getContext()).load(R.mipmap.ease_default_avatar).asBitmap().into(vh.ivHeadIcon);
        }
        vh.tvLoginName.setText(getItem(position).getLoginName());
        vh.tvId .setText(String.valueOf(getItem(position).getUserid()));
        return convertView;
    }

    private class FriendViewHolder{
        private final View root;
        private final ImageView ivHeadIcon;//头像
        private final TextView tvLoginName;//昵称
        private final TextView tvId;//userId
        private final Button btnAddFriend;//添加好友按钮
        private Context mContext;
        private UserVO userVO;
        FriendViewHolder(View root, UserVO u, Context context){
            this.userVO = u;
            this.mContext = context;
            this.root = root;
            ivHeadIcon = (ImageView) root.findViewById(R.id.avatar);
            tvLoginName = (TextView) root.findViewById(R.id.name);
            tvId= (TextView) root.findViewById(R.id.tv_id);
            btnAddFriend = (Button) root.findViewById(R.id.btn_add_friend);
            btnAddFriend.setOnClickListener(new ItemClickListener(true));

            ivHeadIcon.setOnClickListener(new ItemClickListener(false));
            tvLoginName.setOnClickListener(new ItemClickListener(false));
            tvId .setOnClickListener(new ItemClickListener(false));
        }

        private class ItemClickListener implements View.OnClickListener{
            private boolean isAddFriend;
            ItemClickListener(boolean isAddFriend){
                this.isAddFriend = isAddFriend;
            }

            @Override
            public void onClick(View v) {
                if(isAddFriend){
                    //添加好友
                    UIUtil.showToastSafe("添加好友:"+ userVO.getLoginName());
                }else{
//                    CloudItem item;//只是为了携带用户id到详情页
                    //进入好友详情页
//                    item = new CloudItem("无", Const.TianAnMenPoint,"无","");
                    HashMap<String,String> uidMap = new HashMap<>();
                    uidMap.put("uid",String.valueOf(userVO.getUserid()));
//                    item.setCustomfield(uidMap);

                    Intent in = new Intent(mContext,FriendHomePageAct.class);
                    in.putExtra(BundleFlag.SHOWADDRESS,false);
                    mContext.startActivity(in);
                }
            }
        }

    }

}
