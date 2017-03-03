package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import zyzx.linke.R;

public class KeywordSearchListAdapter extends BaseAdapter {
	private Context ctx;
	private ArrayList<String> list;

	public KeywordSearchListAdapter(Context context, ArrayList<String> list) {
		this.ctx = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(ctx, R.layout.item_keyword_search, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.searched_result_textview);
			holder.tv_line = (TextView) convertView.findViewById(R.id.tv_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String item = list.get(position);
		holder.name.setText(item);

		return convertView;
	}

	private class ViewHolder {
		TextView name;
		TextView tv_line;
	}
}
