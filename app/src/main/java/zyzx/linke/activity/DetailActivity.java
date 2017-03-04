package zyzx.linke.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudImage;
import com.amap.api.services.cloud.CloudItem;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.utils.AMApCloudImageCache;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CallPhonePopupWindow;

/***
 * 详情页展现
 * 
 * @author ligen
 * 
 */
public class DetailActivity extends CheckPermissionsActivity {
	private static final int REQUEST_CODE = 0x77AD;
	private CloudItem mCloudItem;
	private TextView mCloudName;
	private TextView mCloudLocation;
	private TextView mTitletv;
	private NetworkImageView mDetailImagePre;
	private TextView mImageSize;
	private LinearLayout mLLYDetailProps;
	private ImageView mCallImageLine;
	private RelativeLayout mPhoneRelativeLayout;
	private String mPhoneNum;
	private TextView mPhoneNumTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		getIntentData();

		setUpInteractiveControls();

	}

	private void setUpInteractiveControls() {

		configureTitle();

		mCloudName = (TextView) findViewById(R.id.detail_poi_name);
		mCloudLocation = (TextView) findViewById(R.id.detail_locaiotn_des);
		mDetailImagePre = (NetworkImageView) findViewById(R.id.cloud_detail_image);
		mImageSize = (TextView) findViewById(R.id.detail_image_size);
		mLLYDetailProps = (LinearLayout) findViewById(R.id.detail_identify_des);
		mCallImageLine = (ImageView) findViewById(R.id.call_divider_line);
		mPhoneRelativeLayout = (RelativeLayout) findViewById(R.id.detail_phone);
		mPhoneNumTv = (TextView) findViewById(R.id.detail_phone_number);

		setUIbasedonData();

	}

	/**
	 * 根据传过来的数据 进行详情页的显示
	 */
	private void setUIbasedonData() {
		if (mCloudItem != null) {
			mCloudName.setText(mCloudItem.getTitle());
			mCloudLocation.setText(mCloudItem.getSnippet());
			List<CloudImage> imageList = mCloudItem.getCloudImage();
			RequestQueue mQueue = Volley.newRequestQueue(this
					.getApplicationContext());
			ImageLoader imageLoader = new ImageLoader(mQueue,
					new AMApCloudImageCache());
			if (mCloudItem != null && mCloudItem.getCloudImage() != null
					&& mCloudItem.getCloudImage().size() > 0) {
				String preImageUrl = imageList.get(0).getPreurl();
				mDetailImagePre.setImageUrl(preImageUrl, imageLoader);
				mImageSize.setVisibility(View.VISIBLE);
				mImageSize.setText(String.valueOf(mCloudItem.getCloudImage()
						.size()));
			} else {
				mDetailImagePre.setBackgroundResource(R.mipmap.no_pictures);
			}
			showPropsDetail();
		}
	}

	private void configureTitle() {
		mTitletv = (TextView) findViewById(R.id.title_des_text);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mCloudItem = intent.getParcelableExtra(BundleFlag.CLOUD_ITEM);
	}

	public void onBackClick(View view) {
		this.finish();
	}

	/**
	 * 点击图片 进入到gallery页面 传输这个云图数据对象
	 * 
	 * @param view
	 */
	public void OnImageLayoutClick(View view) {
		if (mCloudItem.getCloudImage() == null
				|| mCloudItem.getCloudImage().size() == 0) {
			return;
		}
		Intent intent = new Intent(this.getApplicationContext(),
				PreviewPhotoActivity.class);
		intent.putExtra(BundleFlag.CLOUD_ITEM, mCloudItem);
		startActivity(intent);
	}

	/**
	 * 显示自定义字段信息
	 */
	private void showPropsDetail() {
		// 自定义字段
		HashMap<String, String> customFilds = mCloudItem.getCustomfield();
		if (mCloudItem == null || customFilds == null) {
			return;
		}
		mLLYDetailProps.removeAllViews();
		Iterator iter = customFilds.entrySet().iterator();
		// 通过迭代器模式遍历所有的键值对
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			// 如果在自定义字段的hashmap中发现telephone
			// 则显示在布局中显示telephone
			if (key.equals("telephone")) {
				if (val != null && !val.equals("")) {
					showPhone(val);
				}
				continue;
			}
			mLLYDetailProps.addView(createPropsLayout(key,
					customFilds.get(key), true));
		}
	}

	/**
	 * 在布局中 显示电话
	 * 
	 * @param phoneNum
	 */
	private void showPhone(String phoneNum) {
		mCallImageLine.setVisibility(View.VISIBLE);
		mPhoneRelativeLayout.setVisibility(View.VISIBLE);
		mPhoneNumTv.setText(phoneNum);
		mPhoneNum = phoneNum;
	}

	/**
	 * 动态生成 各个自定义字段对应的view
	 * 
	 * @param key
	 *            自定义hashmap中的key值
	 * @param value
	 *            自定义hashmap中的value值
	 * @param isShowLine
	 */
	private LinearLayout createPropsLayout(String key, String value,
										   boolean isShowLine) {
		LinearLayout lly = new LinearLayout(this);
		LinearLayout.LayoutParams llyParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		llyParams.topMargin = 24;
		lly.setLayoutParams(llyParams);
		lly.setOrientation(LinearLayout.VERTICAL);
		lly.setBackgroundResource(R.color.white);

		TextView tvTitle = new TextView(this);
		TextView tvData = new TextView(this);
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		titleParams.leftMargin = 22;
		titleParams.topMargin = 16;
		titleParams.bottomMargin = 16;
		titleParams.gravity = Gravity.CENTER_VERTICAL;
		tvTitle.setLayoutParams(titleParams);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimension(R.dimen.identify_text_size));

		LinearLayout.LayoutParams dataParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dataParams.leftMargin = 29;
		dataParams.bottomMargin = 16;
		dataParams.topMargin = 16;
		dataParams.gravity = Gravity.CENTER_VERTICAL;
		tvData.setLayoutParams(dataParams);

		tvData.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimension(R.dimen.identify_text_size));
		tvTitle.setTextColor(getResources().getColor(
				R.color.identify_title_text));
		tvData.setTextColor(getResources().getColor(
				R.color.identify_content_text));

		tvTitle.setText(key);
		tvData.setText(value);

		if (isShowLine) {
			View line = getLine();
			lly.addView(line);
		}
		lly.addView(tvTitle);
		if (isShowLine) {
			View line = getLine();
			lly.addView(line);
		}
		lly.addView(tvData);
		if (isShowLine) {
			View line = getLine();
			lly.addView(line);
		}
		return lly;
	}

	/**
	 * 动态画一条线
	 * 
	 * @return
	 */
	private View getLine() {
		View tvLine = new View(this);
		LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tvLine.setBackgroundColor(getResources().getColor(
				R.color.bg_divider_line));
		tvLine.setMinimumHeight(1);
		tvLine.setLayoutParams(lineParams);
		return tvLine;
	}

	/**
	 * 点击“去这里” 跳转到导航页面 传输这个云图数据对象
	 * 
	 * @param view
	 */
	public void onLocationClick(View view) {
		Intent intent = new Intent(this, RouteMapActivity.class);
		intent.putExtra(BundleFlag.CLOUD_ITEM, mCloudItem);
		this.startActivity(intent);
	}

	/**
	 * 点击“打电话”
	 * 
	 * @param view
	 */
	public void onCallClick(View view) {
		showCallPopupWindow();
	}

	/**
	 * 弹出打电话的popupwindow
	 */
	private void showCallPopupWindow() {
		//检查权限
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
				!= PackageManager.PERMISSION_GRANTED) {
			//进入到这里代表没有权限.

			if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
				//已经禁止提示了
				UIUtil.showToastSafe("您已禁止该权限，需要重新开启。");
			}else{
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
			}

		} else {
			String[] phoneNums = mPhoneNum.split(",");
			final CallPhonePopupWindow mPopupWindow = new CallPhonePopupWindow(
					this, null, phoneNums);
			backgroundAlpha(0.7f);
			mPopupWindow.showAtLocation(this
					// 设置layout在PopupWindow中显示的位置
					.findViewById(R.id.detail_phone_number), Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
			mPopupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					backgroundAlpha(1f);
				}
			});
			View view = getLayoutInflater().inflate(R.layout.activity_detail, null);
			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					}
					return false;
				}
			});
		}
	}

	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}
}
