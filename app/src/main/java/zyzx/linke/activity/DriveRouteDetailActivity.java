package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import zyzx.linke.R;
import zyzx.linke.adapter.DriveSegmentListAdapter;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.utils.SchemeUtil;


/**
 * 点击路线规划页面中的 车辆tab下面的 详情 进入的页面 用来通过文字列表显示车辆导航的路线规划
 * 
 * @author lingxiang.wang
 * 
 */
public class DriveRouteDetailActivity extends Activity {
	private DrivePath mDrivePath;
	private DriveRouteResult mDriveRouteResult;
	private TextView mTitleDesTv;
	private TextView mTitleDriveRoute, mDesDriveRoute;
	private PullToRefreshListView mDriveSegmentList;
	private DriveSegmentListAdapter mDriveSegmentListAdapter;
	private String mDriveTargetName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busroute_detail);

		getIntentData();

		setUpInteractiveControls();
	}

	private void setUpInteractiveControls() {
		mTitleDesTv = (TextView) findViewById(R.id.title_des_text);
		mTitleDesTv.setText(Const.Drive_ROUTE_DETAIL);
		mTitleDriveRoute = (TextView) findViewById(R.id.title_bus_route);
		mDesDriveRoute = (TextView) findViewById(R.id.des_bus_route);
		String dur = SchemeUtil.getFriendlyTime((int) mDrivePath.getDuration());
		String dis = SchemeUtil.getFriendlyLength((int) mDrivePath
				.getDistance());
		mTitleDriveRoute.setText(dur + "(" + dis + ")");
		int taxiCost = (int) mDriveRouteResult.getTaxiCost();
		SpannableStringBuilder spanabledes = SchemeUtil.getRouteDes(
				this.getApplication(), taxiCost);
		mDesDriveRoute.setText(spanabledes);
		configureListView();
	}

	private void configureListView() {
		mDriveSegmentList = (PullToRefreshListView) findViewById(R.id.bus_segment_list);
		mDriveSegmentList.setMode(Mode.DISABLED);
		ListView actualListView = mDriveSegmentList.getRefreshableView();
		mDriveSegmentListAdapter = new DriveSegmentListAdapter(
				this.getApplicationContext(), mDrivePath.getSteps(),
				mDriveTargetName);
		actualListView.setAdapter(mDriveSegmentListAdapter);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mDrivePath = intent.getParcelableExtra(BundleFlag.DRIVE_PATH);
		mDriveRouteResult = intent.getParcelableExtra(BundleFlag.DRIVE_RESULT);
		mDriveTargetName = intent.getStringExtra(BundleFlag.DRIVE_TARGET_NAME);
	}

	public void onBackClick(View view) {
		this.finish();
	}
}
