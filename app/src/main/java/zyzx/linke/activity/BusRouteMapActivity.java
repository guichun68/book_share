package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.MapView;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

import zyzx.linke.R;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.overlay.SchemeBusOverlay;

/***
 * 公交路径规划详情地图展现
 * @author ligen
 *
 */
public class BusRouteMapActivity extends Activity implements
		OnMapLoadedListener {
	private MapView mMapView;
	private TextView mTitleDesTv;
	private AMap mAMap;
	private BusPath mBusPath;
	private BusRouteResult mBusRouteResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busroute_map);

		getIntentData();

		mMapView = (MapView) findViewById(R.id.bus_route_map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		if (mAMap == null) {
			mAMap = mMapView.getMap();
		}
		// 初始化AMap对象 并给该对象注册loaded监听
		mAMap.setOnMapLoadedListener(this);

		configureTitle();
	}

	private void configureTitle() {
		mTitleDesTv = (TextView) findViewById(R.id.title_des_text);
		mTitleDesTv.setText("公交路线");
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mBusPath = intent.getParcelableExtra(BundleFlag.BUS_PATH);
		mBusRouteResult = intent.getParcelableExtra(BundleFlag.BUS_RESULT);
	}

	public void onBackClick(View view) {
		this.finish();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	/**
	 * map加载完成
	 */
	@Override
	public void onMapLoaded() {
		// 根据intent中传过来的
		// BusPath对象与BusRouteResult对象
		// 生成对应的路径规划overlay
		SchemeBusOverlay routeOverlay = new SchemeBusOverlay(this, mAMap,
				mBusPath, mBusRouteResult.getStartPos(),
				mBusRouteResult.getTargetPos());
		// 将overlay加载到地图上面，并调整缩放，以适配该overlay
		routeOverlay.addToMap();
		routeOverlay.zoomToSpan();
	}
}
