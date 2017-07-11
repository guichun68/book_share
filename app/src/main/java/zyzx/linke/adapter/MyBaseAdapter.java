package zyzx.linke.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by austin on 2017/7/9.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private ArrayList<T> items;

    MyBaseAdapter(ArrayList<T> items){
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mGetView(position,convertView,parent);
    }

    abstract View mGetView(int position,View convertView,ViewGroup parent);

}
