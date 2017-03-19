package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import zyzx.linke.R;
import zyzx.linke.adapter.WalkSegmentListAdapter;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.utils.SchemeUtil;

/**
 * 步行路径规划详情
 * 
 * @author ligen
 * 
 */
public class WalkRouteDetailActivity extends Activity {
	private WalkPath mWalkPath;
	private TextView mTitleDesTv;
	private TextView mTitleWalkRoute;
	private PullToRefreshListView mWalkSegmentList;
	private WalkSegmentListAdapter mWalkSegmentListAdapter;
	private String mWalkTargetName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busroute_detail);
		getIntentData();
		mTitleDesTv = (TextView) findViewById(R.id.title_des_text);
		mTitleDesTv.setText(Const.WALK_ROUTE_DETAIL);
		mTitleWalkRoute = (TextView) findViewById(R.id.title_bus_route);
		String dur = SchemeUtil.getFriendlyTime((int) mWalkPath.getDuration());
		String dis = SchemeUtil
				.getFriendlyLength((int) mWalkPath.getDistance());
		mTitleWalkRoute.setText(dur + "(" + dis + ")");
		mWalkSegmentList = (PullToRefreshListView) findViewById(R.id.bus_segment_list);
		mWalkSegmentList.setMode(Mode.DISABLED);
		ListView actualListView = mWalkSegmentList.getRefreshableView();
		mWalkSegmentListAdapter = new WalkSegmentListAdapter(
				this.getApplicationContext(), mWalkPath.getSteps(),
				mWalkTargetName);
		actualListView.setAdapter(mWalkSegmentListAdapter);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mWalkPath = intent.getParcelableExtra(BundleFlag.WALK_PATH);
		mWalkTargetName = intent.getStringExtra(BundleFlag.WALK_TARGET_NAME);
	}

	public void onBackClick(View view) {
		this.finish();
	}
}
