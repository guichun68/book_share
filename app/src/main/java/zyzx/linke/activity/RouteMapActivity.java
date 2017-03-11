package zyzx.linke.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.BusResultListAdapter;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.Const;
import zyzx.linke.overlay.SchemeDriveOverlay;
import zyzx.linke.overlay.SchemeWalkOverlay;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.SchemeUtil;
import zyzx.linke.utils.ToastUtil;
import zyzx.linke.utils.UIUtil;

/**
 * 点击详情页地址或去这里，展示当前位置到该云图poi的路径规划功能
 * 
 * @author ligen
 * 
 */
public class RouteMapActivity extends Activity implements
		OnRouteSearchListener, AMapLocationListener {
	private MapView mMapView;
	private AMap mAMap;
	private TextView mTitletv;
	private ImageView mBus, mDrive, mWalk;
	private RelativeLayout mBottomLayout;
	private LinearLayout mBusResultLayout;
	private Dialog mProgressDialog;// 搜索时进度条
	private RouteSearch mRouteSearch;
	private LatLonPoint mStartPoint;
	private LatLonPoint mEndPoint;
	private Context mContext;
	private DriveRouteResult mDriveRouteResult;
	private BusRouteResult mBusRouteResult;
	private WalkRouteResult mWalkRouteResult;
	private CloudItem mCloudItem;
	private AMapLocationClient mAMapLocationClient = null;
	private AMapLocationClientOption mAMapLocationOptions = null;
	private TextView mRotueTimeDes, mRouteDetailDes;
	private TextView mBusDefault, mBusLeaseWalk, mBusLeaseChange, mBusNoSubway;
	private PullToRefreshListView mBusResultList;
	private BusResultListAdapter mBusResultListAdapter;
	private ImageView mDefaultDividerLine, mLeaseWalkDividerLine,
			mLeasechangeLine;
	private int mBusStrage = RouteSearch.BusDefault;
	private Resources mResources;
	private String mCurrentCityName = "";
	private final int ROUTE_TYPE_BUS = 1;
	private final int ROUTE_TYPE_DRIVE = 2;
	private final int ROUTE_TYPE_WALK = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routemap);
		getIntentData();
		mContext = this.getApplicationContext();
		mResources = getResources();
		mMapView = (MapView) findViewById(R.id.route_map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		mTitletv = (TextView) findViewById(R.id.title_des_text);
		mTitletv.setText("路线规划");
		mBus = (ImageView) findViewById(R.id.route_bus);
		mDrive = (ImageView) findViewById(R.id.route_drive);
		mWalk = (ImageView) findViewById(R.id.route_walk);
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		mRotueTimeDes = (TextView) findViewById(R.id.poi_name);
		mRouteDetailDes = (TextView) findViewById(R.id.poi_address);
		mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
		mBusDefault = (TextView) findViewById(R.id.bus_default);
		mBusLeaseWalk = (TextView) findViewById(R.id.bus_lease_walk);
		mBusLeaseChange = (TextView) findViewById(R.id.bus_lease_change);
		mBusNoSubway = (TextView) findViewById(R.id.bus_no_subway);

		mDefaultDividerLine = (ImageView) findViewById(R.id.bus_default_line);
		mLeaseWalkDividerLine = (ImageView) findViewById(R.id.bus_leasewalk_line);
		mLeasechangeLine = (ImageView) findViewById(R.id.bus_leasechange_line);

		mBusResultListAdapter = new BusResultListAdapter(mContext,
				mCloudItem.getTitle());
		mBusResultListAdapter.setData(new ArrayList<BusPath>());
		mBusResultList = (PullToRefreshListView) findViewById(R.id.bus_result_list);
		mBusResultList.setMode(Mode.DISABLED);
		ListView actualListView = mBusResultList.getRefreshableView();
		actualListView.setAdapter(mBusResultListAdapter);
		init();
		if (mCloudItem != null) {
			double lat = mCloudItem.getLatLonPoint().getLatitude();
			double lon = mCloudItem.getLatLonPoint().getLongitude();
			mEndPoint = new LatLonPoint(lat, lon);
		}
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
		}
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);
		mAMapLocationOptions = new AMapLocationClientOption();
		mAMapLocationOptions.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
		mAMapLocationClient.setLocationOption(mAMapLocationOptions);
		mAMapLocationClient.setLocationListener(this);
		mAMapLocationClient.startLocation();
		double lat = mCloudItem.getLatLonPoint().getLatitude();
		double lon = mCloudItem.getLatLonPoint().getLongitude();
		BitmapDescriptor markerBitmap = BitmapDescriptorFactory
				.fromBitmap(BitmapFactory.decodeResource(getResources(),
						R.mipmap.poi_marker));
		MarkerOptions options = new MarkerOptions();
		options.position(new LatLng(lat, lon));
		options.title(mCloudItem.getTitle());
		options.snippet(mCloudItem.getSnippet());
		options.icon(markerBitmap);
		mAMap.addMarker(options);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mCloudItem = intent.getParcelableExtra(BundleFlag.CLOUD_ITEM);
	}

	public void onDriveClick(View view) {
		drive();
	}

	private void drive() {
		mDrive.setImageResource(R.mipmap.drive_select);
		mBus.setImageResource(R.mipmap.bus);
		mWalk.setImageResource(R.mipmap.walk);
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
		mBusResultLayout.setVisibility(View.GONE);
		mMapView.setVisibility(View.VISIBLE);
	}

	public void onBusClick(View view) {
		bus();
	}

	private void bus() {
		mDrive.setImageResource(R.mipmap.drive);
		mBus.setImageResource(R.mipmap.bus_select);
		mWalk.setImageResource(R.mipmap.walk);
		mBusResultLayout.setVisibility(View.VISIBLE);
		mMapView.setVisibility(View.GONE);
		searchRouteResult(ROUTE_TYPE_BUS, mBusStrage);
	}

	public void onWalkClick(View view) {
		walk();
	}

	private void walk() {
		mDrive.setImageResource(R.mipmap.drive);
		mBus.setImageResource(R.mipmap.bus);
		mWalk.setImageResource(R.mipmap.walk_select);
		searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
		mBusResultLayout.setVisibility(View.GONE);
		mMapView.setVisibility(View.VISIBLE);
	}

	public void onBusDefaultClick(View view) {
		mDefaultDividerLine.setVisibility(View.INVISIBLE);
		mLeaseWalkDividerLine.setVisibility(View.VISIBLE);
		mLeasechangeLine.setVisibility(View.VISIBLE);
		busDefault();
	}

	private void busDefault() {
		mBusStrage = RouteSearch.BusDefault;
		mBusDefault
				.setBackgroundResource(R.drawable.bus_default_stragegy_pressed);
		mBusLeaseWalk.setBackgroundResource(R.color.act_background);
		mBusLeaseChange.setBackgroundResource(R.color.act_background);
		mBusNoSubway
				.setBackgroundResource(R.drawable.bus_no_subway_stragegy_normal);
		mBusDefault.setTextColor(mResources.getColor(R.color.white));
		mBusLeaseWalk.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));
		mBusLeaseChange.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));
		mBusNoSubway.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));

		searchRouteResult(1, RouteSearch.BusDefault);
	}

	public void onBusLeaseWalkClick(View view) {
		mDefaultDividerLine.setVisibility(View.INVISIBLE);
		mLeaseWalkDividerLine.setVisibility(View.INVISIBLE);
		mLeasechangeLine.setVisibility(View.VISIBLE);
		busLeaseWalk();
	}

	private void busLeaseWalk() {
		Resources resources = getResources();
		mBusStrage = RouteSearch.BusLeaseWalk;
		mBusDefault
				.setBackgroundResource(R.drawable.bus_default_stragegy_normal);
		mBusLeaseWalk.setBackgroundResource(R.color.blue_cloud_scheme);
		mBusLeaseChange.setBackgroundResource(R.color.act_background);
		mBusNoSubway
				.setBackgroundResource(R.drawable.bus_no_subway_stragegy_normal);
		mBusDefault.setTextColor(resources.getColor(R.color.blue_cloud_scheme));
		mBusLeaseWalk.setTextColor(resources.getColor(R.color.white));
		mBusLeaseChange.setTextColor(resources
				.getColor(R.color.blue_cloud_scheme));
		mBusNoSubway
				.setTextColor(resources.getColor(R.color.blue_cloud_scheme));
		searchRouteResult(1, RouteSearch.BusLeaseWalk);
	}

	public void onBusLeaseChangeClick(View view) {
		mDefaultDividerLine.setVisibility(View.VISIBLE);
		mLeaseWalkDividerLine.setVisibility(View.INVISIBLE);
		mLeasechangeLine.setVisibility(View.INVISIBLE);
		busLeaseChange();
	}

	private void busLeaseChange() {
		mBusStrage = RouteSearch.BusLeaseChange;
		mBusDefault
				.setBackgroundResource(R.drawable.bus_default_stragegy_normal);
		mBusLeaseWalk.setBackgroundResource(R.color.act_background);
		mBusLeaseChange.setBackgroundResource(R.color.blue_cloud_scheme);
		mBusNoSubway
				.setBackgroundResource(R.drawable.bus_no_subway_stragegy_normal);
		mBusDefault
				.setTextColor(mResources.getColor(R.color.blue_cloud_scheme));
		mBusLeaseWalk.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));
		mBusLeaseChange.setTextColor(mResources.getColor(R.color.white));
		mBusNoSubway.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));
		searchRouteResult(1, RouteSearch.BusLeaseChange);
	}

	public void onBusNoSubwayClick(View view) {
		mDefaultDividerLine.setVisibility(View.VISIBLE);
		mLeaseWalkDividerLine.setVisibility(View.VISIBLE);
		mLeasechangeLine.setVisibility(View.INVISIBLE);
		busNoSubway();
	}

	private void busNoSubway() {
		mBusStrage = RouteSearch.BusNoSubway;
		mBusDefault
				.setBackgroundResource(R.drawable.bus_default_stragegy_normal);
		mBusLeaseWalk.setBackgroundResource(R.color.act_background);
		mBusLeaseChange.setBackgroundResource(R.color.act_background);
		mBusNoSubway
				.setBackgroundResource(R.drawable.bus_no_subway_stragegy_pressed);
		mBusDefault
				.setTextColor(mResources.getColor(R.color.blue_cloud_scheme));
		mBusLeaseWalk.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));
		mBusLeaseChange.setTextColor(mResources
				.getColor(R.color.blue_cloud_scheme));
		mBusNoSubway.setTextColor(mResources.getColor(R.color.white));
		searchRouteResult(1, RouteSearch.BusNoSubway);
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (mProgressDialog == null)
			mProgressDialog = CustomProgressDialog.getToastDialog(this,"加载数据中...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {
		if (mStartPoint == null) {
			UIUtil.showToastSafe(mContext,"定位中，稍后再试...");
			return;
		}
		if (mEndPoint == null) {
			UIUtil.showToastSafe(mContext,"终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
					mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, mode);
			mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	/**
	 * 公交路线查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {
		mAMap.clear();// 清理地图上的所有覆盖物
		mBottomLayout.setVisibility(View.GONE);
		dissmissProgressDialog();
		if (errorCode == Const.SUCCESSCODE) {
			if (result != null && result.getPaths() != null) {
				int size = result.getPaths().size();
				mBusRouteResult = result;
				mAMap.clear();// 清理地图上的所有覆盖物
				mBusResultListAdapter.setData(mBusRouteResult.getPaths());
				mBusResultListAdapter.setBusResult(mBusRouteResult);
				mBusResultListAdapter.notifyDataSetChanged();
				if (result.getPaths().size() > 0
						&& result.getPaths().get(0).getDistance() < 1000) {
					UIUtil.showToastSafe(mContext,R.string.route_suggestion_walk);
				} else if (result.getPaths().size() == 0) {
					UIUtil.showToastSafe(mContext,"没有搜索到相关路线");
				}
				if (result != null && result.getPaths() == null) {
					UIUtil.showToastSafe(mContext,R.string.route_suggestion_walk);
				}

			} else {
				UIUtil.showToastSafe(mContext,R.string.error_route_result_bus);
			}

		} else if (errorCode == Const.ERROR_CODE_SOCKE_TIME_OUT) {
			UIUtil.showToastSafe(mContext,R.string.error_socket_timeout);
		} else if (errorCode == Const.ERROR_CODE_UNKNOW_HOST) {
			UIUtil.showToastSafe(mContext,R.string.error_network);
		} else if (errorCode == Const.ERROR_CODE_SCODE) {
			UIUtil.showToastSafe(mContext,R.string.error_route_result_bus);
		} else if (errorCode == Const.ERROR_CODE_TABLEID) {
			UIUtil.showToastSafe(mContext,R.string.error_table_id);
		} else {
			UIUtil.showToastSafe(mContext,UIUtil.getString(R.string.error_other)+errorCode);
		}
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		mAMap.clear();// 清理地图上的所有覆盖物
		mBottomLayout.setVisibility(View.GONE);
		dissmissProgressDialog();
		if (errorCode == Const.SUCCESSCODE) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					mAMap.clear();// 清理地图上的所有覆盖物
					SchemeDriveOverlay drivingRouteOverlay = new SchemeDriveOverlay(
							this, mAMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos());
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					mBottomLayout.setVisibility(View.VISIBLE);
					int dis = (int) drivePath.getDistance();
					int dur = (int) drivePath.getDuration();
					String des = SchemeUtil.getBusRouteTitle(dur, dis);
					mRotueTimeDes.setText(des);
					int taxiCost = (int) mDriveRouteResult.getTaxiCost();
					SpannableStringBuilder spanabledes = SchemeUtil
							.getRouteDes(this.getApplication(), taxiCost);
					mRouteDetailDes.setText(spanabledes);
					mBottomLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									DriveRouteDetailActivity.class);
							intent.putExtra(BundleFlag.DRIVE_PATH, drivePath);
							intent.putExtra(BundleFlag.DRIVE_RESULT,
									mDriveRouteResult);
							intent.putExtra(BundleFlag.DRIVE_TARGET_NAME,
									mCloudItem.getTitle());
							startActivity(intent);
						}
					});
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.route_suggestion_walk);
				}

			} else {
				ToastUtil.show(mContext, R.string.error_route_result_drive);
			}
		} else if (errorCode == Const.ERROR_CODE_SOCKE_TIME_OUT) {
			ToastUtil.show(this.getApplicationContext(),
					R.string.error_socket_timeout);
		} else if (errorCode == Const.ERROR_CODE_UNKNOW_HOST) {
			ToastUtil
					.show(this.getApplicationContext(), R.string.error_network);
		} else if (errorCode == Const.ERROR_CODE_FAILURE_AUTH) {
			ToastUtil.show(this.getApplicationContext(), R.string.error_key);
		} else if (errorCode == 33) {
			ToastUtil.show(this.getApplicationContext(),
					R.string.error_route_result_drive);
		} else if (errorCode == Const.ERROR_CODE_TABLEID) {
			ToastUtil.show(this.getApplicationContext(),
					R.string.error_table_id);
		} else {
			ToastUtil.show(this.getApplicationContext(),
					getString(R.string.error_other) + errorCode);
		}
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
		mAMap.clear();// 清理地图上的所有覆盖物
		mBottomLayout.setVisibility(View.GONE);
		dissmissProgressDialog();
		if (errorCode == Const.SUCCESSCODE) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				mWalkRouteResult = result;
				final WalkPath walkPath = mWalkRouteResult.getPaths().get(0);

				SchemeWalkOverlay walkRouteOverlay = new SchemeWalkOverlay(
						this, mAMap, walkPath, mWalkRouteResult.getStartPos(),
						mWalkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
				// mBottomLayout = (RelativeLayout)
				// findViewById(R.id.bottom_layout);
				mBottomLayout.setVisibility(View.VISIBLE);
				String walkTime = SchemeUtil.getFriendlyTime((int) walkPath
						.getDuration());
				String walkDistance = SchemeUtil
						.getFriendlyLength((int) walkPath.getDistance());
				mRotueTimeDes.setText(walkTime + "(" + walkDistance + ")");
				mRouteDetailDes.setText("");
				mBottomLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext,
								WalkRouteDetailActivity.class);
						intent.putExtra(BundleFlag.WALK_PATH, walkPath);
						intent.putExtra(BundleFlag.WALK_RESULT,
								mWalkRouteResult);
						intent.putExtra(BundleFlag.WALK_TARGET_NAME,
								mCloudItem.getTitle());
						startActivity(intent);
					}
				});
			} else {
				ToastUtil.show(mContext, R.string.error_route_result_walk);
			}
		} else if (errorCode == Const.ERROR_CODE_SOCKE_TIME_OUT) {
			ToastUtil.show(this.getApplicationContext(),
					R.string.error_socket_timeout);
		} else if (errorCode == Const.ERROR_CODE_UNKNOW_HOST) {
			ToastUtil
					.show(this.getApplicationContext(), R.string.error_network);
		} else if (errorCode == Const.ERROR_CODE_FAILURE_AUTH) {
			ToastUtil.show(this.getApplicationContext(), R.string.error_key);
		} else if (errorCode == Const.ERROR_CODE_SCODE) {
			ToastUtil.show(this.getApplicationContext(),
					R.string.error_route_result_walk);
		} else if (errorCode == Const.ERROR_CODE_TABLEID) {
			ToastUtil.show(this.getApplicationContext(),
					R.string.error_table_id);
		} else {
			ToastUtil.show(this.getApplicationContext(),
					getString(R.string.error_other) + errorCode);
		}
	}

	@Override
	public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mAMapLocationClient != null) {
			mAMapLocationClient.unRegisterLocationListener(this);
			mAMapLocationClient.onDestroy();
		}
		mAMapLocationClient = null;
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
		stopLocation();// 停止定位
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


	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location == null) {
			return;
		}

		if (location.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
			stopLocation();
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			mCurrentCityName = location.getCity();
			mStartPoint = new LatLonPoint(geoLat, geoLng);
			LatLonPoint destination = new LatLonPoint(mCloudItem
					.getLatLonPoint().getLatitude(), mCloudItem
					.getLatLonPoint().getLongitude());
			int distance = SchemeUtil.calculateLineDistance(mStartPoint,
					destination);
			if (distance <= Const.WALK_DISTANCE) {
				walk();
			} else {
				bus();
			}

		}
	}
}
