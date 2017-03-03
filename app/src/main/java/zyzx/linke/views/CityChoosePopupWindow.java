package zyzx.linke.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import zyzx.linke.R;
import zyzx.linke.activity.IndexActivity2;


public class CityChoosePopupWindow extends PopupWindow {

	private GridView mDistrictGridView;
	private LinearLayout mChangeCitylayout;
	private TextView mCurrentCityTextView;

	public CityChoosePopupWindow(Context context, OnClickListener clickHandler) {

		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View interalView = inflater.inflate(R.layout.popup_city_choose, null);

		setContentView(interalView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);

		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable(context.getResources(),
				(Bitmap) null));

		mChangeCitylayout = (LinearLayout) interalView
				.findViewById(R.id.change_city_linearlayout);

		setCurrentCityTextView((TextView) interalView
				.findViewById(R.id.current_city));

		mDistrictGridView = (GridView) interalView
				.findViewById(R.id.district_gridview);

		mChangeCitylayout.setOnClickListener(clickHandler);

		getCurrentCityTextView().setText(
				((IndexActivity2) context).getCurrentCity());

	}

	public GridView getDistrictGridView() {
		return mDistrictGridView;
	}

	public void setmDistrictGridView(GridView mDistrictGridView) {
		this.mDistrictGridView = mDistrictGridView;
	}

	public TextView getCurrentCityTextView() {
		return mCurrentCityTextView;
	}

	private void setCurrentCityTextView(TextView mCurrentCityTextView) {
		this.mCurrentCityTextView = mCurrentCityTextView;
	}

}