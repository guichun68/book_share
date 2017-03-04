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
import zyzx.linke.model.bean.BookDetail;

/**
 * Created by austin on 2017/3/4.
 * Desc: 好友主页 之书籍列表的adapter
 */

public class BookAdapter extends BaseAdapter{

    private ArrayList<BookDetail> books;
    private Context context;

    public BookAdapter(Context ctx, ArrayList<BookDetail> books){
        this.context = ctx;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public BookDetail getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item_book,null);
            holder = new BookViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (BookViewHolder) convertView.getTag();
        }
        Glide.with(context).load(getItem(position).getImage()).into(holder.ivCover);
        holder.tvBookName.setText(getItem(position).getTitle());
        StringBuilder sb = new StringBuilder();
        for(String author:getItem(position).getAuthor()){
            sb.append(author).append(";");
        }
        if(sb.length()>0)
        {
            sb.deleteCharAt(sb.length()-1);
        }
        holder.tvAuthor.setText(sb.toString());
        holder.tvIntro.setText(getItem(position).getSummary());

        return convertView;
    }

    class BookViewHolder{
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
