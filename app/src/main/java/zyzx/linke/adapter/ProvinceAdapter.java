package zyzx.linke.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.model.Area;

/**
 * Created by austin on 2017/5/3.
 * Desc :省份adapter
 */

public class ProvinceAdapter extends BaseAdapter {

    private ArrayList<Area> provinces;

    public ProvinceAdapter(ArrayList<Area> provinces){
        this.provinces = provinces;
    }

    @Override
    public int getCount() {
        return provinces.size();
    }

    @Override
    public Area getItem(int position) {
        return provinces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView =View.inflate(parent.getContext(),R.layout.item_location,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tvName.setText(getItem(position).getName());
        return convertView;
    }

    private class ViewHolder{
        private final View root;
        private final TextView tvName;
        ViewHolder(View root){
            this.root = root;
            this.tvName = (TextView) root.findViewById(R.id.tv_item);
        }
    }
}
