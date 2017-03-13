package zyzx.linke.views;


import android.widget.PopupWindow;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import zyzx.linke.R;

/**
 * 个人资料或图书封面 点击头像等弹出POP
 * 
 * @author cy
 * 
 */
public class UserInfoImagePOP extends PopupWindow {
	private TextView crameTv, photoTv, goneTv,lookTv,title;
	private View mMenuView;
/**
 * 
 * @param context
 * @param itemsOnClick
 * @param 
 */
	public UserInfoImagePOP(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.pop_userinfo_image_pic, null);
		crameTv = (TextView) mMenuView.findViewById(R.id.tv_camera);
		photoTv = (TextView) mMenuView.findViewById(R.id.tv_photo);
		goneTv = (TextView) mMenuView.findViewById(R.id.gone);
//		lookTv = (TextView) mMenuView.findViewById(R.id.tv_look_image);
		title = (TextView) mMenuView.findViewById(R.id.title);
		
//		View look_lin=mMenuView.findViewById(R.id.look_lin);
		// 取消按钮
		goneTv.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		
		// 设置按钮监听
		photoTv.setOnClickListener(itemsOnClick);
		crameTv.setOnClickListener(itemsOnClick);
//		lookTv.setOnClickListener(itemsOnClick);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(false);
		// //设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom2);
		// //实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.ll_popup).getTop();
				Log.i("POP", height+"");
				int y = (int) event.getY();
				Log.i("POP", y+"");
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}
