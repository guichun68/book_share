package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.model.Area;

/**
 * Created by austin on 2017/5/3.
 * Desc :省份adapter
 */

public class AreaAdapter extends MyBaseAdapter<Area> {

    private Context ctx;
    public AreaAdapter(ArrayList<Area> items) {
        super(items);
    }
    public AreaAdapter(ArrayList<Area> items,Context context){
        super(context,items);
    }

    @Override
    public View mGetView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView =View.inflate(context!=null?context:parent.getContext(),R.layout.item_location,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvName.setText(getItem(position).getName());
        return convertView;
    }

    public static class ViewHolder{
        private final TextView tvName;
        public ViewHolder(View root){
            this.tvName = (TextView) root.findViewById(R.id.tv_item);
        }
    }
}
