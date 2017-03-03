package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.cloud.CloudItem;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.model.bean.MarkerStatus;

/**
 * 通过地图marker展示列表当前在屏幕内的可见item
 * 
 * @author ligen
 * 
 */
public class MapActivity extends Activity implements OnMarkerClickListener,
		OnInfoWindowClickListener, OnMarkerDragListener, OnMapLoadedListener,
		OnClickListener, InfoWindowAdapter {

	private MapView mMapView;
	private AMap mAMap;
	ArrayList<CloudItem> mCloudItems;
	private UiSettings mUiSettings;
	private LinearLayout mBtnDetail;
	private TextView mTextViewName;
	private TextView mTextViewAddress;
	private CloudItem mCurrentItem = null;
	private MarkerStatus mLastMarkerStatus;
	private Button mRoadCondition;
	private boolean mIsShowRoadCondition = false;
	private RelativeLayout mAddLayout;
	private RelativeLayout mMinusLayout;
	private TextView mTitleDesTv;
	private ImageView mTitleMap;
	private RelativeLayout mBottomRlayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_display);
		// R 需要引用包import com.amapv2.apis.R;

		// 获取列表页中的云图数据
		mCloudItems = getIntent().getParcelableArrayListExtra(
				BundleFlag.CLOUD_ITEM_LIST);

		setUpInteractiveControls();

		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 必须要写

		init();
	}

	/**
	 * 设置所有可点击的控件
	 */
	private void setUpInteractiveControls() {

		mBottomRlayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		if (mCloudItems.size() == 0) {
			mBottomRlayout.setVisibility(View.GONE);
		}

		mTitleDesTv = (TextView) findViewById(R.id.title_des_text);
		mTitleDesTv.setText(getResources().getString(R.string.map_title));

		mTitleMap = (ImageView) findViewById(R.id.title_right_img);
		mTitleMap.setVisibility(View.VISIBLE);
		mTitleMap.setImageResource(R.drawable.goto_list_btn_status);

		mBtnDetail = (LinearLayout) findViewById(R.id.poi_detail);
		mTextViewName = (TextView) findViewById(R.id.poi_name);
		mTextViewAddress = (TextView) findViewById(R.id.poi_address);
		mRoadCondition = (Button) findViewById(R.id.road_condition);
		mAddLayout = (RelativeLayout) findViewById(R.id.add_layout);
		mMinusLayout = (RelativeLayout) findViewById(R.id.minus_layout);

		mBtnDetail.setOnClickListener(this);
		mRoadCondition.setOnClickListener(this);
		mAddLayout.setOnClickListener(this);
		mMinusLayout.setOnClickListener(this);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			setUpMap();
		}
	}

	public void onBackClick(View view) {
		this.finish();
	}

	public void onTitleRightClick(View view) {
		this.finish();
	}

	private void setUpMap() {

		setMapUi();

		setMapListener();

		addMarkersToMap();
	}

	/**
	 * 为地图增加一些事件监听
	 */
	private void setMapListener() {
		mAMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		mAMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		mAMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		mAMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
	}

	/**
	 * 地图用3D地图显示，可缩放和拖动；
	 */
	private void setMapUi() {
		mUiSettings = mAMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(true);
		mUiSettings.setScaleControlsEnabled(true);

		mUiSettings.setRotateGesturesEnabled(false);
		mUiSettings.setTiltGesturesEnabled(false);
	}

	/**
	 * 往地图上添加marker，为列表页获得的数据
	 */
	private void addMarkersToMap() {

		int size = mCloudItems.size();
		for (int i = 0; i < size; i++) {

			// 根据该poi的经纬度进行marker点的添加
			MarkerOptions markerOption = new MarkerOptions();
			markerOption.position(new LatLng(mCloudItems.get(i)
					.getLatLonPoint().getLatitude(), mCloudItems.get(i)
					.getLatLonPoint().getLongitude()));
			Marker marker = mAMap.addMarker(markerOption);

			// 每个marker点上带有一个状态类，来说明这个marker是否是被选中的状态
			// 会根据是否被选中来决定一些事件处理
			MarkerStatus markerStatus = new MarkerStatus(i);
			markerStatus.setCloudItem(mCloudItems.get(i));
			markerStatus.setMarker(marker);
			if (i == 0) {
				markerChosen(markerStatus);
				mLastMarkerStatus = markerStatus;
			}
			setMarkerBasedonStatus(markerStatus);
			marker.setObject(markerStatus);
		}

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		case R.id.poi_detail:
			gotoDetailActivity();
			break;

		case R.id.road_condition:
			ToggleRoadConditionStatus();
			break;

		case R.id.add_layout:
			ZoomIn();
			break;

		case R.id.minus_layout:
			ZoomOut();
			break;

		default:
			break;
		}
	}

	private void ZoomOut() {
		mAMap.animateCamera(CameraUpdateFactory.zoomOut(), 300, null);

	}

	private void ZoomIn() {
		mAMap.animateCamera(CameraUpdateFactory.zoomIn(), 300, null);
	}

	/**
	 * 改变当前的实时交通是否显示 本应用中未将其visible 你可以选择这种实现来进行是否进行实时交通的呈现
	 */
	private void ToggleRoadConditionStatus() {
		mIsShowRoadCondition = !mIsShowRoadCondition;
		if (mIsShowRoadCondition) {
			mRoadCondition.setBackgroundResource(R.mipmap.road_condition_on);
		} else {
			mRoadCondition.setBackgroundResource(R.mipmap.road_condition_off);
		}
		mAMap.setTrafficEnabled(mIsShowRoadCondition);
	}

	/**
	 * 进入当前选中poi的详情页
	 */
	private void gotoDetailActivity() {

		if (mCurrentItem != null) {
			Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra(BundleFlag.CLOUD_ITEM, mCurrentItem);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}

	/**
	 * 在map加载完成之后，移动到一个视图 该视图可以保证在屏幕内看到所有的marker
	 */
	@Override
	public void onMapLoaded() {

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		if (mCloudItems.size() == 0) {
			return;
		}
		if (mCloudItems.size() == 1) {

			mAMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
					mCloudItems.get(0).getLatLonPoint().getLatitude(),
					mCloudItems.get(0).getLatLonPoint().getLongitude())));
			return;
		}

		// 当数据大于等于2的时候，才谈得上是一个bound
		for (CloudItem item : mCloudItems) {
			builder.include(new LatLng(item.getLatLonPoint().getLatitude(),
					item.getLatLonPoint().getLongitude()));
		}

		LatLngBounds bounds;
		bounds = builder.build();

		// 设置所有maker显示在当前可视区域地图中
		mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));

	}

	@Override
	public void onMarkerDrag(Marker arg0) {

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {

	}

	@Override
	public void onMarkerDragStart(Marker arg0) {

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {

	}

	/**
	 * marker被点击后的事件处理 改变他的当前状态 同时改变最后一个被点击的marker引用 以做到之前选中的现在是未选中的状态
	 * 现在选中的呈现选中状态
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {

		if (mLastMarkerStatus != null) {
			mLastMarkerStatus.pressStatusToggle();
			setMarkerBasedonStatus(mLastMarkerStatus);
		}

		MarkerStatus newMarkerStatus = (MarkerStatus) marker.getObject();
		markerChosen(newMarkerStatus);
		mLastMarkerStatus = newMarkerStatus;
		return false;
	}

	/**
	 * 根据该marker的最新状态决定应该显示什么样的marker
	 * 
	 * @param status
	 */
	private void setMarkerBasedonStatus(MarkerStatus status) {
		if (status.getIsPressed()) {
			status.getMarker().setIcon(
					BitmapDescriptorFactory.fromBitmap(BitmapFactory
							.decodeResource(getResources(),
									status.getmResPressed())));
		} else {
			status.getMarker().setIcon(
					BitmapDescriptorFactory.fromBitmap(BitmapFactory
							.decodeResource(getResources(),
									status.getmResUnPressed())));
		}
	}

	/**
	 * marker被选中之后，需要更改marker的样式，以及在底部bar显示信息
	 * 
	 * @param markerStatus
	 */
	private void markerChosen(MarkerStatus markerStatus) {
		markerStatus.pressStatusToggle();
		mCurrentItem = (CloudItem) markerStatus.getCloudItem();
		mTextViewName.setText(mCurrentItem.getTitle());
		mTextViewAddress.setText(mCurrentItem.getSnippet());
		setMarkerBasedonStatus(markerStatus);
	}

}
