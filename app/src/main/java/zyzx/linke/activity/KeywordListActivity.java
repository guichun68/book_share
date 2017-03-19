package zyzx.linke.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.KeywordSearchListAdapter;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.BundleResult;

public class KeywordListActivity extends Activity implements OnClickListener,
		TextWatcher {

	private EditText mEditTextCloudData;
	private ListView mSearchResultListview;
	private ArrayList<String> mListString;
	private RelativeLayout mRlayoutCancelInput;
	protected static Dialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// R 需要引用包import com.amapv2.apis.R;
		setContentView(R.layout.activity_cloud_data_search);
		setUpInteractiveControls();

	}

	private void setUpInteractiveControls() {
		mRlayoutCancelInput = (RelativeLayout) findViewById(R.id.clear_input_layout);
		mRlayoutCancelInput.setVisibility(View.GONE);

		mSearchResultListview = (ListView) findViewById(R.id.search_result_listview);
		mSearchResultListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				returnIndexWithResult(mListString.get(position));
			}
		});

		mEditTextCloudData = (EditText) findViewById(R.id.edittext_cloud_data);
		mEditTextCloudData.addTextChangedListener(this);
		mEditTextCloudData.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					returnIndexWithResult(mEditTextCloudData.getText()
							.toString());
				}
				return false;
			}
		});
	}

	/**
	 * 当用户点击了其中一个结果的时候，记录这个结果 并且返回到上一页，也就是主页 这样就知道了用户选择的关键字到底是什么
	 * @param result
	 */
	protected void returnIndexWithResult(String result) {
		Intent intent = new Intent();
		intent.putExtra(BundleFlag.POI_ITEM, result);
		KeywordListActivity.this.setResult(BundleResult.SUCCESS, intent);
		KeywordListActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.clear_input_layout:
			mEditTextCloudData.setText("");
			break;

		case R.id.cancel_layout:
			returnIndexWithResult("");
			break;

		default:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {
		// TODO Auto-generated method stub

	}

	/**
	 * 实现搜索结果根据用户的输入变化而变化
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		// 如果用户存在输入的字符，则显示清除按钮，方便用户清空输入
		if (s.toString().length() != 0) {
			mRlayoutCancelInput.setVisibility(View.VISIBLE);
		} else {
			mRlayoutCancelInput.setVisibility(View.GONE);
		}

		String newText = s.toString().trim();
		Inputtips inputTips = new Inputtips(this, new InputtipsListener() {

			@Override
			public void onGetInputtips(List<Tip> tipList, int rCode) {
				if (rCode == 1000) {// 正确返回
					if (tipList == null) {

						Toast.makeText(
								KeywordListActivity.this,
								getResources().getString(
										R.string.there_is_no_data),
								Toast.LENGTH_SHORT).show();
						return;
					}
					mListString = new ArrayList<String>();
					for (int i = 0; i < tipList.size(); i++) {
						mListString.add(tipList.get(i).getName());
					}
					KeywordSearchListAdapter keywordApater = new KeywordSearchListAdapter(
							KeywordListActivity.this, mListString);

					mSearchResultListview.setAdapter(keywordApater);
					keywordApater.notifyDataSetChanged();
				} else {
					Toast.makeText(KeywordListActivity.this,
							getResources().getString(R.string.network_error),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		try {
			inputTips.requestInputtips(newText, mEditTextCloudData.getText()
					.toString());// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

		} catch (AMapException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	public void onBackClick(View view) {
		finish();
	}
}
