package zyzx.linke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import zyzx.linke.R;


public class DistrictListAdapter extends BaseAdapter {

	private Context mContext;
	private final String[] mDistricts;
	private int mIndex;

	public DistrictListAdapter(Context context, String[] districts, int index) {
		this.mContext = context;
		this.mDistricts = districts;
		this.mIndex = index;
	}

	@Override
	public int getCount() {
		return mDistricts.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView;

		itemView = new View(mContext);

		itemView = inflater.inflate(R.layout.item_district, null);

		TextView contentTextView = (TextView) itemView
				.findViewById(R.id.district_name);
		contentTextView.setText(mDistricts[position]);

		if (position == mIndex) {

			contentTextView.setTextColor(mContext.getResources().getColor(
					R.color.blue_cloud_scheme));

		}

		return itemView;
	}
}
