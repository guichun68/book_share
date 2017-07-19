package zyzx.linke.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.model.bean.BorrowFlowVO;
import zyzx.linke.utils.StringUtil;

/**
 * Created by austin on 2017/7/19.
 */

public class MyBookBorrowBegAdp extends BaseAdapter{

    private ArrayList<BorrowFlowVO> data;

    public MyBookBorrowBegAdp(ArrayList<BorrowFlowVO> data){
        this.data =data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BorrowFlowVO getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView == null){
            convertView = View.inflate(parent.getContext(),R.layout.item_begs,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvRelUserName.setText(getItem(position).getRelUserLoginName());
        vh.tvBookName.setText("《"+getItem(position).getBookName()+"》");
        vh.tvStatus.setText(getItem(position).getBorrowFlow().getStatus());
        String msg = getItem(position).getBorrowFlow().getMsg();
        if(!StringUtil.isEmpty(msg)){
            vh.llMsg.setVisibility(View.VISIBLE);
            vh.tvMsg.setText(msg);
        }else{
            vh.tvMsg.setText(null);
            vh.llMsg.setVisibility(View.GONE);
        }
        return convertView;
    }
    private class ViewHolder{
        private final TextView tvRelUserName;
        private final TextView tvBookName;
        private final TextView tvStatus;
        private final TextView tvMsg;
        private final LinearLayout llMsg;
        ViewHolder(View root){
            llMsg = (LinearLayout) root.findViewById(R.id.ll_msg);
            tvRelUserName = (TextView) root.findViewById(R.id.tv_rel_user_login_name);
            tvBookName = (TextView) root.findViewById(R.id.tv_book_name);
            tvStatus = (TextView) root.findViewById(R.id.tv_status);
            tvMsg = (TextView) root.findViewById(R.id.tv_msg);
        }
    }
}
