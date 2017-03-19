package zyzx.linke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

import zyzx.linke.R;
import zyzx.linke.adapter.CityListAdapter;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.BundleResult;
import zyzx.linke.model.bean.City;
import zyzx.linke.utils.UIUtil;

/**
 * 城市列表选择页面
 * 
 * @author lingxiang.wang
 *
 */
public class CityListActivity extends Activity implements OnClickListener {
	private ArrayList<String> mLetters;
	private ArrayList<City> mCityList;
	private LinearLayout mLlayoutLetter;
	private TextView mTvShow;
	private TextView mTvResult;
	private LinearLayout mResultLayout;
	private EditText mEditSearch;
	private HashMap<String, Integer> mCityMap;
	private ListView mLvCity;
	private City mModelResult;
	private Handler mHandler = new Handler();
	private TextView mTitletv;
	private boolean mIsLoaded;
	private RelativeLayout mRlayoutCancelInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_citylist);

		// 之所以这里在一个新的thread里面进行生成页面，是因为希望做到
		// 下面这种用户体验：
		// 列表可以慢点显示出来，但是一定要先能够进入该页面
		new Thread() {

			public void run() {
				setUpInteractiveControls();

				getDataFromIntent();

				// 返回主线程来进行listview显示操作
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						CityListAdapter city_adapter = new CityListAdapter(
								CityListActivity.this, mCityList);
						mLvCity.setAdapter(city_adapter);
						mIsLoaded = true;
					}
				});
			}

		}.start();
	}

	/**
	 * 因为复用了titleBar的layout 所以用程序的方式进行具体页面的赋值
	 */
	private void configureTitle() {
		mTitletv = (TextView) findViewById(R.id.title_des_text);
		mTitletv.setText(getResources().getString(R.string.city_choose_title));
	}

	/**
	 * 确定了用户选择的城市，并返回上一页
	 * 
	 * @param model
	 */
	private void setCityResult(City model) {
		Intent intent = new Intent();
		intent.putExtra(BundleFlag.CITY_MODEL, model);
		CityListActivity.this.setResult(BundleResult.SUCCESS, intent);
		CityListActivity.this.finish();
	}

	/**
	 * 新开一个线程，进行城市的搜索，如果能搜索成功 则显示这个城市
	 * 
	 * @param str
	 */
	private void searchCity(final String str) {
		new Thread() {
			public void run() {
				for (final City model : mCityList) {
					if (model.name.toString().contains(str)) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								showResult(model);
							}
						});
						return;
					}
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						showResult(null);

					}
				});
			}
		}.start();
	}

	/**
	 * 这是一个之前unvisible的layout 如果搜索成功的话，会visible这个layout 并把搜索的结果呈现出来
	 * 
	 * @param model
	 */
	private void showResult(City model) {
		mResultLayout.setVisibility(View.VISIBLE);
		mModelResult = model;
		if (mModelResult == null) {
			mTvResult.setText(this.getString(R.string.city_search_none));
			mTvResult.setGravity(Gravity.CENTER);
			mTvResult.setPadding(0, 0, 0, 0);
		} else {
			mTvResult.setText(mModelResult.name);
			mTvResult.setGravity(Gravity.CENTER_VERTICAL);
			mTvResult.setPadding(UIUtil.dip2px(50), 0, 0, 0);
		}
	}

	public void getIndexView(final int height, final int offset) {

		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, height);

		// 该for循环用于显示右侧的触摸索引栏
		// 用于通过index来引导listview
		for (int i = 0; i < mLetters.size(); i++) {
			final TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(mLetters.get(i));
			tv.setTextColor(this.getResources().getColor(R.color.change_city));
			tv.getPaint().setFakeBoldText(true);
			tv.setTextSize(12);
			tv.setPadding(10, 0, 10, 0);
			mLlayoutLetter.addView(tv);

			// 设置右侧触摸索引栏的touch事件
			mLlayoutLetter.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event)

				{
					// 这个touch事件用途有2：
					// 1、确定所触摸到的index
					// 2、listview置位到该index对应的第一个item位置，
					// 并将index通过mTvShow显示在屏幕中央
					float y = event.getY();
					int index = (int) (y / height);
					if (index > -1 && index < mLetters.size()) {
						String key = mLetters.get(index);
						if (mCityMap.containsKey(key)) {
							int pos = mCityMap.get(key) + offset;
							mLvCity.setSelectionFromTop(pos, 0);

							mTvShow.setVisibility(View.VISIBLE);
							mTvShow.setText(mLetters.get(index));
						}
					}
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						mTvShow.setVisibility(View.GONE);
						break;
					}
					return true;
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.clear_input_layout:
			mEditSearch.setText("");
			break;

		case R.id.tv_result:
			if (mModelResult != null) {
				setCityResult(mModelResult);
			}
			break;
		default:
			break;
		}

	}

	public void onBackClick(View view) {
		finish();
	}

	/**
	 * 专门配置edittext 因为这段代码比较长 所以专门摘出来，以增强可读性 主要目的是 （1）用户点击回车键可以确定输入，并返回
	 * （2）会根据是否存在输入来决定是否显示取消按钮
	 */
	private void configureEditText() {
		mEditSearch = (EditText) findViewById(R.id.edittext_cloud_data);
		mEditSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					String key = mEditSearch.getText().toString();
					if (!TextUtils.isEmpty(key)) {
						searchCity(key);
						return true;
					}
				}
				return false;
			}
		});

		mEditSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() != 0) {
					mRlayoutCancelInput.setVisibility(View.VISIBLE);
					searchCity(s.toString());
				} else {
					mRlayoutCancelInput.setVisibility(View.GONE);
					mResultLayout.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	/**
	 * 获取传输过来的数据 因为我能确保cast的正确性 因此我将attribute置为unchecked
	 */
	@SuppressWarnings("unchecked")
	private void getDataFromIntent() {
		mLetters = getIntent().getStringArrayListExtra(BundleFlag.CITY_LETTERS);
		mCityList = (ArrayList<City>) getIntent().getSerializableExtra(
				BundleFlag.CITY_LIST);
		mCityMap = (HashMap<String, Integer>) getIntent().getSerializableExtra(
				BundleFlag.CITY_MAP);
	}

	/**
	 * 配置ui控件
	 */
	private void setUpInteractiveControls() {
		configureTitle();

		View.inflate(CityListActivity.this, R.layout.citylist_head, null);
		mLlayoutLetter = (LinearLayout) findViewById(R.id.llayout_letter);
		mTvShow = (TextView) findViewById(R.id.tv_show);
		mTvResult = (TextView) findViewById(R.id.tv_result);

		mRlayoutCancelInput = (RelativeLayout) findViewById(R.id.clear_input_layout);
		mRlayoutCancelInput.setVisibility(View.GONE);

		configureEditText();

		mResultLayout = (LinearLayout) findViewById(R.id.llayout_result);
		mResultLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		mLvCity = (ListView) findViewById(R.id.lv_city);
		mLvCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				position = position - mLvCity.getHeaderViewsCount();
				City model = mCityList.get(position);
				setCityResult(model);
			}

		});

		final RelativeLayout rlayout_citylist = (RelativeLayout) findViewById(R.id.rlayout_citylist);
		ViewTreeObserver observer = rlayout_citylist.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (mIsLoaded) {
					mIsLoaded = false;
					int itemHeight = (int) (rlayout_citylist.getHeight() / mLetters
							.size());
					getIndexView(itemHeight, 0);
				}

			}
		});
	}

}
