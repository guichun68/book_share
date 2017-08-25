package zyzx.linke.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.model.bean.EnumConst;

/**
 * Created by Austin on 2017-08-24.
 */

public class SkillTypeAdapter extends MyBaseAdapter<EnumConst> {

    public SkillTypeAdapter(ArrayList<EnumConst> items) {
        super(items);
    }

    @Override
    public View mGetView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView =View.inflate(parent.getContext(), R.layout.item_dropdown_line,null);
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
            this.tvName = (TextView) root.findViewById(android.R.id.text1);
        }
    }
}