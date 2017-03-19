package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudItem;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.utils.AMApCloudImageCache;

/***
 * 云图图片浏览功能
 * 
 * @author ligen
 * 
 */
public class PreviewPhotoActivity extends Activity {
	private ArrayList<ImageView> listViews = null;
	private ViewPager phtoPager;
	private SchemePageAdapter adapter;
	private int pageIndex = 0;
	private int pageCount = 0;
	private TextView mTitletv;
	private ProgressBar pbLoding;
	private CloudItem mCloudItem;
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private AMApCloudImageCache mAMApCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview_photo);
		mQueue = Volley.newRequestQueue(this.getApplicationContext());
		mAMApCache = new AMApCloudImageCache();
		mImageLoader = new ImageLoader(mQueue, mAMApCache);
		mTitletv = (TextView) findViewById(R.id.title_des_text);
		pbLoding = (ProgressBar) findViewById(R.id.pb_loading);
		phtoPager = (ViewPager) findViewById(R.id.viewpager_photo);
		phtoPager.setOnPageChangeListener(pageChangeListener);
		getIntentData();
		pageCount = mCloudItem.getCloudImage().size();
		for (int i = 0; i < pageCount; i++) {
			initListViews();
		}

		adapter = new SchemePageAdapter(listViews);// 构造adapter
		phtoPager.setAdapter(adapter);// 设置适配器
		phtoPager.setCurrentItem(pageIndex);
		if (pageCount > 0) {
			mTitletv.setText((pageIndex + 1) + "/" + pageCount);
		}
		downLoadImage(0);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mCloudItem = intent.getParcelableExtra(BundleFlag.CLOUD_ITEM);
	}

	private void initListViews() {
		if (listViews == null) {
			listViews = new ArrayList<ImageView>();
		}
		ImageView img = new ImageView(this);
		img.setBackgroundColor(getResources().getColor(R.color.pre_image_bg));
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		img.setScaleType(ScaleType.FIT_CENTER);
		img.setAdjustViewBounds(true);
		listViews.add(img);// 添加view
	}

	public void onBackClick(View view) {
		this.finish();
	}

	private void downLoadImage(final int index) {
		String imageUrl = mCloudItem.getCloudImage().get(index).getUrl();
		Bitmap cachedBitmap = mAMApCache.getBitmap(imageUrl);
		if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
			listViews.get(index).setImageBitmap(cachedBitmap);
		} else {
			pbLoding.setVisibility(View.VISIBLE);
			mImageLoader.get(imageUrl, new ImageListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					pbLoding.setVisibility(View.GONE);
				}

				@Override
				public void onResponse(ImageContainer response,
						boolean isImmediate) {
					// TODO Auto-generated method stub
					if (response.getBitmap() == null
							|| response.getBitmap().isRecycled()) {
						return;
					} else {
						listViews.get(index).setImageBitmap(
								response.getBitmap());
						pbLoding.setVisibility(View.GONE);
						adapter.notifyDataSetChanged();
					}

				}
			}, Const.IAMGE_MAX_WIDTH, Const.IMAGE_MAX_HEIGHT);
		}
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		// 页面选择响应函数
		public void onPageSelected(int arg0) {
			downLoadImage(arg0);
			pageIndex = arg0;
			mTitletv.setText((pageIndex + 1) + "/" + pageCount);
		}

		// 滑动中。。。
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		// 滑动状态改变
		public void onPageScrollStateChanged(int arg0) {

		}

	};

	private class SchemePageAdapter extends PagerAdapter {

		private ArrayList<ImageView> listViews;// content

		private int size;// 页数

		public SchemePageAdapter(ArrayList<ImageView> listViews) {// 构造函数
			// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}
}