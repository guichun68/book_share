package zyzx.linke.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;

/**
 * Created by austin on 2017/4/8.
 * Desc: 导入书籍结果ListView Adapter
 */

public class ImportResultAdapter extends BaseAdapter {
    private ArrayList<String> data;

    public ImportResultAdapter(ArrayList<String> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh ;
        if(convertView==null){
            convertView = View.inflate(parent.getContext(),R.layout.item_import_result,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvItem.setText(getItem(position));
        if(getItem(position).contains("成功")){
            vh.tvItem.setTextColor(Color.parseColor("#009200"));
        }else{
            vh.tvItem.setTextColor(Color.RED);
        }
        return convertView;
    }

    private class ViewHolder {
        private final TextView tvItem;

        ViewHolder(View root) {
            this.tvItem = (TextView) root.findViewById(R.id.tv);
        }
    }
}
