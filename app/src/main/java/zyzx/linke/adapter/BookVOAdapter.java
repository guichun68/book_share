package zyzx.linke.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import zyzx.linke.R;
import zyzx.linke.activity.ShareBookDetailAct;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.AppUtil;

/**
 * Created by austin on 2017/3/4.
 * Desc: 分享中心 页 书籍adapter
 */

public class BookVOAdapter extends MyCommonAdapter<MyBookDetailVO>{


    public BookVOAdapter(Context context, List<MyBookDetailVO> datas, int itemLayoutResId, int footerLayoutId, int footerProgressResId, int footerTextTipResId) {
        super(context, datas, itemLayoutResId, footerLayoutId,footerProgressResId,footerTextTipResId);
    }

    @Override
    public void convert(MyViewHolder holder, final MyBookDetailVO o, final int position) {
        if(holder.getHolderType()==MyViewHolder.HOLDER_TYPE_NORMAL){
            holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShareBookDetailAct.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("book",o);
                    bundle.putString(BundleFlag.FLAG_USER_BOOK_ID,o.getUserBookId());
                    bundle.putInt("from", Const.FROM_HOME_FRAG);
                    intent.putExtra(BundleFlag.SHOWADDRESS,false);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            BookDetail2 book = o.getBook();
            String url = AppUtil.getMostDistinctPicUrl(book);
            Glide.with(mContext).load(url).into((ImageView)holder.getView(R.id.iv));
            holder.setText(R.id.tv_book_name,book.getTitle());
            StringBuilder sb = new StringBuilder();
            if(book.getAuthor()!=null){
                for(String author:book.getAuthor()){
                    sb.append(author).append(";");
                }
                if(sb.length()>0) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            holder.setText(R.id.tv_author,sb.toString());
            holder.setText(R.id.tv_intro,book.getSummary());
        }else{

        }
    }

}
