package zyzx.linke.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.model.bean.EnumConst;

/**
 * Created by austin on 2017/7/9.
 * Desc: 图书分类 sp的列表
 */

public class BookClassifyAdapter extends MyBaseAdapter<EnumConst> {

    public BookClassifyAdapter(ArrayList<EnumConst> items) {
        super(items);
    }

    @Override
    public View mGetView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView =View.inflate(parent.getContext(), R.layout.item_location,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvName.setText(getItem(position).getName());
        return convertView;
    }

    private class ViewHolder{
        private final TextView tvName;
        ViewHolder(View root){
            this.tvName = (TextView) root.findViewById(R.id.tv_item);
        }
    }
}
