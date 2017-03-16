package zyzx.linke.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.activity.CommonBookDetailAct;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.StringUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: MyBooksAct 页面中我的所有的书籍列表的Adapter
 */

public class AllMyBookAdapter extends BaseAdapter {

    ArrayList<MyBookDetailVO> books;
    private Context context;

    public AllMyBookAdapter(Context context,ArrayList<MyBookDetailVO> books){
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
      MyBookViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item_my_books,null);
            holder = new MyBookViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (MyBookViewHolder) convertView.getTag();
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommonBookDetailAct.class);
                Bundle bundle = new Bundle();
//                bundle.putParcelable("book",getItem(position));
                bundle.putSerializable("book",getItem(position).getBook());
                intent.putExtra(BundleFlag.SHOWADDRESS,false);
//                intent.putExtra("book",bundle);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        if(!StringUtil.isEmpty(getItem(position).getBook().getImage_medium())){
            Glide.with(context).load(getItem(position).getBook().getImage_medium()).into(holder.ivCover);
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
        holder.refreshBookState(getItem(position).getStatus());
        return convertView;
    }


    class MyBookViewHolder {
        private final View root;
        //三种不同状态：在库、已分享、已借出
        private final ImageView ivInStock,ivSharing,ivBorrowed;
        private final ImageView ivCover;//图书封面
        private final TextView tvBookName;
        private final TextView tvAuthor;
        private final TextView tvIntro;

        MyBookViewHolder(View root) {
            this.root = root;
            this.ivCover = (ImageView) root.findViewById(R.id.iv);
            this.ivInStock = (ImageView) root.findViewById(R.id.iv_corner_in_stock);
            this.ivSharing = (ImageView) root.findViewById(R.id.iv_corner_sharing);
            this.ivBorrowed = (ImageView) root.findViewById(R.id.iv_corner_borrowed);
            this.tvBookName = (TextView) root.findViewById(R.id.tv_book_name);
            this.tvAuthor = (TextView) root.findViewById(R.id.tv_author);
            this.tvIntro = (TextView) root.findViewById(R.id.tv_intro);
        }

        /**
         * 更新书籍状态
         * @param state 1:在库， 2:分享中， 3:已借出
         */
        public void refreshBookState(int state){
            switch (state){
                case 1:
                    this.ivInStock.setVisibility(View.VISIBLE);
                    this.ivSharing.setVisibility(View.INVISIBLE);
                    this.ivBorrowed.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    this.ivInStock.setVisibility(View.INVISIBLE);
                    this.ivSharing.setVisibility(View.VISIBLE);
                    this.ivBorrowed.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    this.ivInStock.setVisibility(View.INVISIBLE);
                    this.ivSharing.setVisibility(View.INVISIBLE);
                    this.ivBorrowed.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

}
