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
import zyzx.linke.activity.BookDetailAct;
import zyzx.linke.activity.CommonBookDetailAct;
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

public class BookVOAdapter extends BaseAdapter{

    private ArrayList<MyBookDetailVO> books;
    private Context context;

    public BookVOAdapter(Context ctx, ArrayList<MyBookDetailVO> books){
        this.context = ctx;
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
        BookViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item_book,null);
            holder = new BookViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (BookViewHolder) convertView.getTag();
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShareBookDetailAct.class);
                Bundle bundle = new Bundle();
//                bundle.putParcelable("book",getItem(position));
                bundle.putParcelable("book",getItem(position));
                bundle.putInt("from", Const.FROM_HOME_FRAG);
                intent.putExtra(BundleFlag.SHOWADDRESS,false);
//                intent.putExtra("book",bundle);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        BookDetail2 book = getItem(position).getBook();
        String url = AppUtil.getMostDistinctPicUrl(book);
        Glide.with(context).load(url).into(holder.ivCover);
        holder.tvBookName.setText(book.getTitle());
        StringBuilder sb = new StringBuilder();
        if(book.getAuthor()!=null){
            for(String author:book.getAuthor()){
                sb.append(author).append(";");
            }
            if(sb.length()>0) {
                sb.deleteCharAt(sb.length()-1);
            }
        }
        holder.tvAuthor.setText(sb.toString());
        holder.tvIntro.setText(book.getSummary());
        return convertView;
    }

    private class BookViewHolder{
        private final View root;
        private final ImageView ivCover;
        private final TextView tvBookName;
        private final TextView tvAuthor;
        private final TextView tvIntro;
        BookViewHolder(View root){
            this.root = root;
            this.ivCover = (ImageView) root.findViewById(R.id.iv);
            this.tvBookName = (TextView) root.findViewById(R.id.tv_book_name);
            this.tvAuthor = (TextView) root.findViewById(R.id.tv_author);
            this.tvIntro = (TextView) root.findViewById(R.id.tv_intro);
        }
    }
}
