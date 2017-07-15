package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.global.Const;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.AppUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: BorrowedBookAct 页面中我借入的书籍Adapter
 */

public class BorrowedInAdapter extends BaseAdapter {

    ArrayList<MyBookDetailVO> books;
    private Context context;

    public BorrowedInAdapter(Context context, ArrayList<MyBookDetailVO> books){
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public MyBookDetailVO getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      final MyBookViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item_my_books,null);
            holder = new MyBookViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (MyBookViewHolder) convertView.getTag();
        }
        String imageUrl = AppUtil.getMostDistinctPicUrl(getItem(position).getBook());
        if(imageUrl!=null){
            Glide.with(context).load(imageUrl).into(holder.ivCover);
        }else{
            Glide.with(context).load(R.mipmap.defaultcover).asBitmap().into(holder.ivCover) ;
        }
        holder.tvBookName.setText(getItem(position).getBook().getTitle());
        StringBuilder sb = new StringBuilder();
        if(getItem(position).getBook().getAuthor()!=null){
            for(String author:getItem(position).getBook().getAuthor()){
                sb.append(author).append(";");
            }
            if(sb.length()>0) {
                sb.deleteCharAt(sb.length()-1);
            }
        }

        holder.tvAuthor.setText(sb.toString());
        holder.tvIntro.setText(getItem(position).getBook().getSummary());
        holder.refreshBookState(getItem(position).getBookStatusId());
        return convertView;
    }


    class MyBookViewHolder {
        private final View root;
        //四种不同状态：在库、已分享、已借出、已借入
        private final ImageView ivInStock,ivSharing,ivBorrowedOut,ivBorrowedIn;
        private final ImageView ivCover;//图书封面
        private final TextView tvBookName;
        private final TextView tvAuthor;
        private final TextView tvIntro;

        MyBookViewHolder(View root) {
            this.root = root;
            this.ivCover = (ImageView) root.findViewById(R.id.iv);
            this.ivInStock = (ImageView) root.findViewById(R.id.iv_corner_in_stock);
            this.ivSharing = (ImageView) root.findViewById(R.id.iv_corner_sharing);
            this.ivBorrowedOut = (ImageView) root.findViewById(R.id.iv_corner_borrowed);
            this.ivBorrowedIn = (ImageView) root.findViewById(R.id.iv_corner_borrowed_in);
            this.tvBookName = (TextView) root.findViewById(R.id.tv_book_name);
            this.tvAuthor = (TextView) root.findViewById(R.id.tv_author);
            this.tvIntro = (TextView) root.findViewById(R.id.tv_intro);
        }

        /**
         * 更新书籍状态
         * @param state 1:在库， 2:分享中， 3:已借出 ,4:已借入
         */
        public void refreshBookState(String state){
            switch (state){
                case Const.BOOK_STATUS_ONSHELF:
                    this.ivInStock.setVisibility(View.VISIBLE);
                    this.ivSharing.setVisibility(View.INVISIBLE);
                    this.ivBorrowedOut.setVisibility(View.INVISIBLE);
                    this.ivBorrowedIn.setVisibility(View.INVISIBLE);
                    break;
                case Const.BOOK_STATUS_SHARED:
                    this.ivInStock.setVisibility(View.INVISIBLE);
                    this.ivSharing.setVisibility(View.VISIBLE);
                    this.ivBorrowedOut.setVisibility(View.INVISIBLE);
                    this.ivBorrowedIn.setVisibility(View.INVISIBLE);
                    break;
                case Const.BOOK_STATUS_LOANED:
                    this.ivInStock.setVisibility(View.INVISIBLE);
                    this.ivSharing.setVisibility(View.INVISIBLE);
                    this.ivBorrowedOut.setVisibility(View.VISIBLE);
                    this.ivBorrowedIn.setVisibility(View.INVISIBLE);
                    break;
                case Const.BOOK_STATUS_BORROWED:
                    this.ivBorrowedIn.setVisibility(View.VISIBLE);
                    this.ivInStock.setVisibility(View.INVISIBLE);
                    this.ivSharing.setVisibility(View.INVISIBLE);
                    this.ivBorrowedOut.setVisibility(View.INVISIBLE);
                    break;
                default:
                    this.ivBorrowedOut.setVisibility(View.INVISIBLE);
                    this.ivInStock.setVisibility(View.INVISIBLE);
                    this.ivSharing.setVisibility(View.INVISIBLE);
                    this.ivBorrowedIn.setVisibility(View.INVISIBLE);
            }
        }
    }

}
