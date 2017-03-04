package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import zyzx.linke.R;
import zyzx.linke.adapter.BusSegmentListAdapter;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.Const;
import zyzx.linke.utils.SchemeUtil;

/**
 * 公交路线详情页面 通过列表文字显示路径规划路线
 * 
 * @author lingxiang.wang
 * 
 */
public class BusRouteDetailActivity extends Activity {
	private BusPath mBusPath;
	private BusRouteResult mBusRouteResult;
	private TextView mTitleDesTv;
	private TextView mTitleBusRoute, mDesBusRoute;
	private PullToRefreshListView mBusSegmentList;
	private BusSegmentListAdapter mBusSegmentListAdapter;
	private String mBusTargetName;
	private ImageView mTitleMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busroute_detail);
		getIntentData();
		mTitleBusRoute = (TextView) findViewById(R.id.title_bus_route);
		mTitleMap = (ImageView) findViewById(R.id.title_right_img);
		mTitleMap.setVisibility(View.VISIBLE);
		mDesBusRoute = (TextView) findViewById(R.id.des_bus_route);
		getIntentData();
		setUpInteractiveControls();
	}

	private void setUpInteractiveControls() {
		configureTitle();
		mTitleMap = (ImageView) findViewById(R.id.title_right_img);
		mTitleMap.setVisibility(View.VISIBLE);
		mTitleBusRoute = (TextView) findViewById(R.id.title_bus_route);
		mDesBusRoute = (TextView) findViewById(R.id.des_bus_route);
		configureListView();
	}

	private void configureListView() {
		mBusSegmentList = (PullToRefreshListView) findViewById(R.id.bus_segment_list);
		mBusSegmentList.setMode(Mode.DISABLED);

		ListView actualListView = mBusSegmentList.getRefreshableView();
		mBusSegmentListAdapter = new BusSegmentListAdapter(
				this.getApplicationContext(), mBusPath.getSteps(),
				mBusTargetName);
		actualListView.setAdapter(mBusSegmentListAdapter);
		setupBusRouteDes();
	}

	private void setupBusRouteDes() {
		int dur = (int) mBusPath.getDuration();
		int dis = (int) mBusPath.getDistance();
		String busRotueTitle = SchemeUtil.getBusRouteTitle(dur, dis);
		mTitleBusRoute.setText(busRotueTitle);
		int taxiCost = (int) mBusRouteResult.getTaxiCost();
		SpannableStringBuilder busRouteDes = SchemeUtil.getRouteDes(
				this.getApplicationContext(), taxiCost);
		mDesBusRoute.setText(busRouteDes);
	}

	private void configureTitle() {
		mTitleDesTv = (TextView) findViewById(R.id.title_des_text);
		mTitleDesTv.setText(Const.BUS_ROUTE_DETAIL);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mBusPath = intent.getParcelableExtra(BundleFlag.BUS_PATH);
		mBusRouteResult = intent.getParcelableExtra(BundleFlag.BUS_RESULT);
		mBusTargetName = intent.getStringExtra(BundleFlag.BUS_TARGET_NAME);
	}

	public void onBackClick(View view) {
		this.finish();
	}

	/**
	 * 右上角的地图按钮点击事件 用于进入一个地图页面，并在这个页面显示从起点到终点的公交路线规划图
	 * 
	 * @param view
	 */
	public void onTitleRightClick(View view) {
		Intent intent = new Intent(this, BusRouteMapActivity.class);
		intent.putExtra(BundleFlag.BUS_PATH, mBusPath);
		intent.putExtra(BundleFlag.BUS_RESULT, mBusRouteResult);
		startActivity(intent);
	}
}
