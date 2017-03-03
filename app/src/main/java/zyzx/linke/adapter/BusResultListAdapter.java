package zyzx.linke.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

import java.util.List;

import zyzx.linke.R;
import zyzx.linke.activity.BusRouteDetailActivity;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.utils.SchemeUtil;

/**
 * BusPath列表展示adapter
 * 
 * @author ligen
 * 
 */
public class BusResultListAdapter extends BaseAdapter {
	private Context mContext;
	private List<BusPath> mItemList;
	private BusRouteResult mBusRouteResult;
	private String mTargetName;

	public BusResultListAdapter(Context context, String targetName) {
		mContext = context;
		mTargetName = targetName;
	}

	public void setData(List<BusPath> list) {
		this.mItemList = list;
	}

	public void setBusResult(BusRouteResult busRouteResult) {
		mBusRouteResult = busRouteResult;
	}

	public BusRouteResult getBusResult() {
		return mBusRouteResult;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(mContext, R.layout.item_bus_result, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.bus_path_title);
			holder.des = (TextView) convertView.findViewById(R.id.bus_path_des);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final BusPath item = mItemList.get(position);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext.getApplicationContext(),
						BusRouteDetailActivity.class);
				intent.putExtra(BundleFlag.BUS_PATH, item);
				intent.putExtra(BundleFlag.BUS_RESULT, mBusRouteResult);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(BundleFlag.BUS_TARGET_NAME, mTargetName);
				mContext.startActivity(intent);
			}
		});
		holder.title.setText(SchemeUtil.getBusPathTitle(item));
		holder.des.setText(SchemeUtil.getBusPathDes(item));
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		TextView des;
	}
}
