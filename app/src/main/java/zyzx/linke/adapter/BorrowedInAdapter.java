package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import zyzx.linke.R;
import zyzx.linke.global.Const;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.AppUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: BorrowedBookAct 页面中我借入的书籍Adapter
 */

public class BorrowedInAdapter extends MyCommonAdapter<MyBookDetailVO> {


    public BorrowedInAdapter(Context context, List<MyBookDetailVO> datas, int itemLayoutResId, int footerLayoutId, int footerProgressResId, int footerTextTipResId) {
        super(context, datas, itemLayoutResId, footerLayoutId, footerProgressResId, footerTextTipResId);
    }

    @Override
    public void convert(MyViewHolder holder, final MyBookDetailVO bookDetailVO, final int position) {
        String imageUrl = AppUtil.getMostDistinctPicUrl(bookDetailVO.getBook());
        if(imageUrl!=null){
            Glide.with(mContext).load(imageUrl).into((ImageView)holder.getView(R.id.iv));
        }else{
            Glide.with(mContext).load(R.mipmap.defaultcover).asBitmap().into((ImageView)holder.getView(R.id.iv)) ;
        }
        holder.setText(R.id.tv_book_name,bookDetailVO.getBook().getTitle());
        StringBuilder sb = new StringBuilder();
        if(bookDetailVO.getBook().getAuthor()!=null){
            for(String author:bookDetailVO.getBook().getAuthor()){
                sb.append(author).append(";");
            }
            if(sb.length()>0) {
                sb.deleteCharAt(sb.length()-1);
            }
        }
        holder.setText(R.id.tv_author,sb.toString());
        holder.setText(R.id.tv_intro,bookDetailVO.getBook().getSummary());

        switch (bookDetailVO.getBookStatusId()){
            case Const.BOOK_STATUS_ONSHELF:
                holder.getView(R.id.iv_corner_in_stock).setVisibility(View.VISIBLE);
                holder.getView(R.id.iv_corner_sharing).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_borrowed).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_borrowed_in).setVisibility(View.INVISIBLE);
                break;
            case Const.BOOK_STATUS_SHARED:
                holder.getView(R.id.iv_corner_in_stock).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_sharing).setVisibility(View.VISIBLE);
                holder.getView(R.id.iv_corner_borrowed).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_borrowed_in).setVisibility(View.INVISIBLE);
                break;
            case Const.BOOK_STATUS_LOANED:
                holder.getView(R.id.iv_corner_in_stock).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_sharing).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_borrowed).setVisibility(View.VISIBLE);
                holder.getView(R.id.iv_corner_borrowed_in).setVisibility(View.INVISIBLE);
                break;
            case Const.BOOK_STATUS_BORROWED:
                holder.getView(R.id.iv_corner_borrowed_in).setVisibility(View.VISIBLE);
                holder.getView(R.id.iv_corner_in_stock).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_sharing).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_borrowed).setVisibility(View.INVISIBLE);
                break;
            default:
                holder.getView(R.id.iv_corner_borrowed).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_in_stock).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_sharing).setVisibility(View.INVISIBLE);
                holder.getView(R.id.iv_corner_borrowed_in).setVisibility(View.INVISIBLE);
        }
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
        boolean onLongItemClickListener(View view,MyBookDetailVO bookDetailVO,int position);
        void onItemClickListener(View view,MyBookDetailVO bookDetailVO,int position);
    }
}
