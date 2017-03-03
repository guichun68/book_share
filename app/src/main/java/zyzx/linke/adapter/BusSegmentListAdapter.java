package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.route.BusStep;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.model.bean.SchemeBusStep;

/**
 * 公交线路详情页adapter
 * 
 * @author ligen
 * 
 */
public class BusSegmentListAdapter extends BaseAdapter {
	private Context mContext;
	private List<SchemeBusStep> mItemList = new ArrayList<SchemeBusStep>();
	private String mTargetName;

	public BusSegmentListAdapter(Context context, List<BusStep> itemList,
								 String targetName) {
		this.mContext = context;
		mTargetName = targetName;
		SchemeBusStep start = new SchemeBusStep(null);
		start.setStart(true);
		mItemList.add(start);
		for (BusStep busStep : itemList) {
			if (busStep.getWalk() != null) {
				SchemeBusStep walk = new SchemeBusStep(busStep);
				walk.setWalk(true);
				mItemList.add(walk);
			}
			if (busStep.getBusLine() != null) {
				SchemeBusStep bus = new SchemeBusStep(busStep);
				bus.setBus(true);
				mItemList.add(bus);
			}
		}
		SchemeBusStep end = new SchemeBusStep(null);
		end.setEnd(true);
		mItemList.add(end);
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
			convertView = View.inflate(mContext, R.layout.item_bus_segment,
					null);

			holder.parent = (RelativeLayout) convertView
					.findViewById(R.id.bus_item);
			holder.busLineName = (TextView) convertView
					.findViewById(R.id.bus_line_name);
			holder.busDirIcon = (ImageView) convertView
					.findViewById(R.id.bus_dir_icon);
			holder.busStationNum = (TextView) convertView
					.findViewById(R.id.bus_station_num);
			holder.busExpandImage = (ImageView) convertView
					.findViewById(R.id.bus_expand_image);
			holder.busDirUp = (ImageView) convertView
					.findViewById(R.id.bus_dir_icon_up);
			holder.busDirDown = (ImageView) convertView
					.findViewById(R.id.bus_dir_icon_down);
			holder.splitLine = (ImageView) convertView
					.findViewById(R.id.bus_seg_split_line);
			holder.expandContent = (LinearLayout) convertView
					.findViewById(R.id.expand_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final SchemeBusStep item = mItemList.get(position);
		// if (holder == null || mItemList == null) {
		// return convertView;
		// }
		if (item.isStart()) {
			holder.busDirIcon.setImageResource(R.mipmap.dir_start);
			holder.busLineName.setText("我的位置");
			holder.busDirUp.setVisibility(View.INVISIBLE);
			holder.busDirDown.setVisibility(View.VISIBLE);
			holder.splitLine.setVisibility(View.INVISIBLE);
			holder.busStationNum.setVisibility(View.INVISIBLE);
			holder.busExpandImage.setVisibility(View.INVISIBLE);
			return convertView;
		}
		if (item.isEnd()) {
			holder.busDirIcon.setImageResource(R.mipmap.dir_end);
			mItemList.get(mItemList.size() - 2);
			holder.busLineName.setText("到达终点" + mTargetName);
			holder.busDirUp.setVisibility(View.VISIBLE);
			holder.busDirDown.setVisibility(View.INVISIBLE);
			holder.busStationNum.setVisibility(View.INVISIBLE);
			holder.busExpandImage.setVisibility(View.INVISIBLE);
			return convertView;
		}
		if (item.isWalk() && item.getWalk() != null) {
			holder.busDirIcon.setImageResource(R.mipmap.dir13);
			holder.busDirUp.setVisibility(View.VISIBLE);
			holder.busDirDown.setVisibility(View.VISIBLE);
			holder.busLineName.setText("步行"
					+ (int) item.getWalk().getDistance() + "米");
			holder.busStationNum.setVisibility(View.INVISIBLE);
			holder.busExpandImage.setVisibility(View.INVISIBLE);
		} else if (item.isBus() && item.getBusLine() != null) {
			holder.busDirIcon.setImageResource(R.mipmap.dir14);
			holder.busDirUp.setVisibility(View.VISIBLE);
			holder.busDirDown.setVisibility(View.VISIBLE);
			holder.busLineName.setText(item.getBusLine().getBusLineName());
			holder.busStationNum.setVisibility(View.VISIBLE);
			holder.busStationNum
					.setText((item.getBusLine().getPassStationNum() + 1) + "站");
			holder.busExpandImage.setVisibility(View.VISIBLE);
			ArrowClick arrowClick = new ArrowClick(holder, item);
			holder.parent.setTag(position);
			holder.parent.setOnClickListener(arrowClick);
		}
		return convertView;
	}

	private class ViewHolder {
		public RelativeLayout parent;
		TextView busLineName;
		ImageView busDirIcon;
		TextView busStationNum;
		ImageView busExpandImage;

		ImageView busDirUp;
		ImageView busDirDown;
		ImageView splitLine;
		LinearLayout expandContent;
		boolean arrowExpend = false;
	}

	private class ArrowClick implements OnClickListener {
		private ViewHolder mHolder;
		private BusStep mItem;

		public ArrowClick(final ViewHolder holder, final BusStep item) {
			mHolder = holder;
			mItem = item;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = Integer.parseInt(String.valueOf(v.getTag()));
			mItem = mItemList.get(position);
			if (mHolder.arrowExpend == false) {
				mHolder.arrowExpend = true;
				mHolder.busExpandImage
						.setImageResource(R.mipmap.arrow_list_down);
				addBusStation(mItem.getBusLine().getDepartureBusStation());
				for (BusStationItem station : mItem.getBusLine()
						.getPassStations()) {
					addBusStation(station);
				}
				addBusStation(mItem.getBusLine().getArrivalBusStation());

			} else {
				mHolder.arrowExpend = false;
				mHolder.busExpandImage
						.setImageResource(R.mipmap.arrow_list_up);
				mHolder.expandContent.removeAllViews();
			}

		}

		private void addBusStation(BusStationItem station) {
			LinearLayout ll = (LinearLayout) View.inflate(mContext,
					R.layout.item_bus_segment_extends, null);
			TextView tv = (TextView) ll
					.findViewById(R.id.bus_line_station_name);
			tv.setText(station.getBusStationName());
			mHolder.expandContent.addView(ll);
		}
	}
}
