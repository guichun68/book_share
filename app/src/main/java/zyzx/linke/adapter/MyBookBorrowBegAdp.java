package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.model.bean.BorrowFlowVO;
import zyzx.linke.utils.StringUtil;

/**
 * Created by austin on 2017/7/19.
 * Desc: 我的借入 adapter
 */

public class MyBookBorrowBegAdp extends MyCommonAdapter<BorrowFlowVO>{


    public MyBookBorrowBegAdp(Context context, List<BorrowFlowVO> datas, int itemLayoutResId, int footerLayoutId, int footerProgressResId, int footerTextTipResId) {
        super(context, datas, itemLayoutResId, footerLayoutId, footerProgressResId, footerTextTipResId);
    }

    @Override
    public void convert(MyViewHolder holder, BorrowFlowVO borrowFlowVO, int position) {
        holder.setText(R.id.tv_rel_user_login_name,borrowFlowVO.getRelUserLoginName());
        holder.setText(R.id.tv_book_name,"《"+borrowFlowVO.getBookName()+"》");
        holder.setText(R.id.tv_status,borrowFlowVO.getBorrowFlow().getStatus());
        String msg = borrowFlowVO.getBorrowFlow().getMsg();
        if(!StringUtil.isEmpty(msg)){
            holder.getView(R.id.ll_msg).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_msg,msg);
        }else{
            holder.getView(R.id.ll_msg).setVisibility(View.GONE);
            holder.setText(R.id.tv_msg,null);
        }
    }
}
