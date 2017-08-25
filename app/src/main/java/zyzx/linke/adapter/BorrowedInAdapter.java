package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import zyzx.linke.R;
import zyzx.linke.model.bean.BorrowedInVO;
import zyzx.linke.utils.StringUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: BorrowedBookAct 页面中我借入的书籍Adapter
 */

public class BorrowedInAdapter extends MyCommonAdapter<BorrowedInVO> {


    public BorrowedInAdapter(Context context, List<BorrowedInVO> datas, int itemLayoutResId, int footerLayoutId, int footerProgressResId, int footerTextTipResId) {
        super(context, datas, itemLayoutResId, footerLayoutId, footerProgressResId, footerTextTipResId);
    }

    @Override
    public void convert(MyViewHolder holder, final BorrowedInVO bookDetailVO, final int position) {
        String imageUrl = bookDetailVO.getBookImage();
        if(imageUrl!=null){
            Glide.with(mContext).load(imageUrl).into((ImageView)holder.getView(R.id.iv));
        }else{
            Glide.with(mContext).load(R.mipmap.defaultcover).asBitmap().into((ImageView)holder.getView(R.id.iv)) ;
        }
        holder.setText(R.id.tv_book_name,"书名："+bookDetailVO.getBookTitle());

        holder.setText(R.id.tv_author,"作者："+ (StringUtil.isEmpty(bookDetailVO.getBookAuthor())?"未知":bookDetailVO.getBookAuthor()));
        holder.setText(R.id.tv_owner,"所有者："+bookDetailVO.getOwnerName());

        holder.setOnLongClickListener(R.id.ll_root, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return clickListener!=null && clickListener.onLongItemClickListener(v,bookDetailVO,position);
            }
        });
        holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener!=null){
                    clickListener.onItemClickListener(v,bookDetailVO,position);
                }
            }
        });
    }
    private OnClickListener clickListener;

    public void setOnClickListener(OnClickListener listener){
        this.clickListener = listener;
    }

    public interface OnClickListener {
        boolean onLongItemClickListener(View view,BorrowedInVO bookDetailVO,int position);
        void onItemClickListener(View view,BorrowedInVO bookDetailVO,int position);
    }
}
