package zyzx.linke.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import zyzx.linke.R;


/**
 * 详情页点击打电话弹出的popupwindow
 * 
 * @author ligen
 * 
 */
public class CallPhonePopupWindow extends PopupWindow implements
		OnClickListener  {
	private Button mPhoneButton, mCancleButton;
	private Context mContext;
	private String[] mPhoneNums;

	public CallPhonePopupWindow(Context context, OnClickListener clickHandler,
								String[] phoneNums) {
		super(context);
		mContext = context;
		mPhoneNums = phoneNums;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout interalView = (LinearLayout) inflater.inflate(
				R.layout.popup_call, null);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		setContentView(interalView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		this.setBackgroundDrawable(null);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		mPhoneButton = (Button) interalView.findViewById(R.id.phone_button);
		mPhoneButton.setOnClickListener(this);
		LayoutParams exampleTitleLp = mPhoneButton.getLayoutParams();
		mPhoneButton.setText(mPhoneNums[0]);
		if (mPhoneNums.length > 1) {
			for (int i = 1; i < mPhoneNums.length; i++) {
				if (mPhoneNums[i] == null || mPhoneNums[i].equals("")) {
					continue;
				}
				final Button phone = new Button(mContext);
				phone.setBackgroundResource(R.drawable.button_phone_white);
				phone.setTextColor(mContext.getResources().getColor(
						R.color.blue_cloud_scheme));
				phone.setLayoutParams(exampleTitleLp);
				phone.setText(mPhoneNums[i]);
				phone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString()));
						mContext.startActivity(intent);
					}
				});
				interalView.addView(phone);
			}
		}
		mCancleButton = new Button(mContext);
		mCancleButton.setBackgroundResource(R.drawable.button_phone_gray);
		mCancleButton.setClickable(true);
		mCancleButton.setOnClickListener(this);
		mCancleButton.setLayoutParams(exampleTitleLp);
		mCancleButton.setText("取消");
		mCancleButton.setTextColor(mContext.getResources().getColor(
				R.color.white));
		interalView.addView(mCancleButton);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(mPhoneButton)) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ mPhoneNums[0]));
			mContext.startActivity(intent);
		} else if (v.equals(mCancleButton)) {
			this.dismiss();
		}
	}
}
