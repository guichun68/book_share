package zyzx.linke.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.WalkStep;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.utils.SchemeUtil;


/**
 * 步行路线详情页adapter
 * 
 * @author ligen
 * 
 */
public class WalkSegmentListAdapter extends BaseAdapter {
	private Context mContext;
	private List<WalkStep> mItemList = new ArrayList<WalkStep>();
	private String mTargetName;

	public WalkSegmentListAdapter(Context applicationContext,
								  List<WalkStep> steps, String mWalkTargetName) {
		// TODO Auto-generated constructor stub
		mContext = applicationContext;
		mItemList.add(new WalkStep());
		for (WalkStep walkStep : steps) {
			mItemList.add(walkStep);
		}
		mItemList.add(new WalkStep());
		mTargetName = mWalkTargetName;
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
			holder.driveLineName = (TextView) convertView
					.findViewById(R.id.bus_line_name);
			holder.driveDirIcon = (ImageView) convertView
					.findViewById(R.id.bus_dir_icon);
			holder.driveDirUp = (ImageView) convertView
					.findViewById(R.id.bus_dir_icon_up);
			holder.driveDirDown = (ImageView) convertView
					.findViewById(R.id.bus_dir_icon_down);
			holder.splitLine = (ImageView) convertView
					.findViewById(R.id.bus_seg_split_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final WalkStep item = mItemList.get(position);
		if (position == 0) {
			holder.driveDirIcon.setImageResource(R.mipmap.dir_start);
			holder.driveLineName.setText("我的位置");
			holder.driveDirUp.setVisibility(View.INVISIBLE);
			holder.driveDirDown.setVisibility(View.VISIBLE);
			holder.splitLine.setVisibility(View.INVISIBLE);
			return convertView;
		}
		if (position == mItemList.size() - 1) {
			holder.driveDirIcon.setImageResource(R.mipmap.dir_end);
			mItemList.get(mItemList.size() - 2);
			holder.driveLineName.setText("到达终点" + mTargetName);
			holder.driveDirUp.setVisibility(View.VISIBLE);
			holder.driveDirDown.setVisibility(View.INVISIBLE);
			return convertView;
		}
		holder.splitLine.setVisibility(View.VISIBLE);
		holder.driveDirUp.setVisibility(View.VISIBLE);
		holder.driveDirDown.setVisibility(View.VISIBLE);
		String actionName = item.getAction();
		int resID = SchemeUtil.getWalkActionID(actionName);
		holder.driveDirIcon.setImageResource(resID);
		holder.driveLineName.setText(item.getInstruction());
		return convertView;
	}

	private class ViewHolder {
		TextView driveLineName;
		ImageView driveDirIcon;

		ImageView driveDirUp;
		ImageView driveDirDown;
		ImageView splitLine;
	}

}
