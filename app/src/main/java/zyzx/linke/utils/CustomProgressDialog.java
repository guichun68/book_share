package zyzx.linke.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import zyzx.linke.R;


public class CustomProgressDialog {

	/**
	 * 自定义进度Dialog 旋转效果
	 * 
	 * @param context
	 * @param msg
	 *            自定义信息 长度太长的话显示的不好看，尽量短
	 * @return
	 */
	public static Dialog getProgressDialog(Context context, String msg) {
		Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.update_loading_progressbar_anim);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);  
		
		
		Dialog progressDialog = new Dialog(context, R.style.progress_dialog);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog, null);
		ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
		pb.setAnimation(operatingAnim);
		TextView msgTxt = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
		msgTxt.setText(msg);

		progressDialog.setContentView(view);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return progressDialog;
	}
	public static Dialog progressDialog;
	/**自定义不确定进度条 dialog 帧动画旋转效果
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog getProgressDialog2(Context context, String msg) {
		progressDialog = new Dialog(context, R.style.progress_dialog);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_frame_anim, null);
		TextView msgTxt = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
		msgTxt.setText(msg);

		progressDialog.setContentView(view);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return progressDialog;
	}
	
	public static Dialog getProgressDialogFramAni(Context context, String msg) {
		Dialog progressDialog = new Dialog(context, R.style.progress_dialog);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog, null);
		TextView msgTxt = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
		msgTxt.setText(msg);

		progressDialog.setContentView(view);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return progressDialog;
	}

	public static Dialog getToastDialog(Context context, String msg) {
		Dialog dialog = new Dialog(context, R.style.progress_dialog);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_toast, null);
		TextView msgTxt = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
		msgTxt.setText(msg);

		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return dialog;
	}

	public static Dialog getNewProgressBar(Context context){
		Dialog dialog = new Dialog(context, R.style.progress_dialog);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_progress_bar, null);

		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return dialog;
	}

	public static void dismissDialog(Dialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public static Dialog getPromptDialog(Context context, String msg, View.OnClickListener listener) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_text_btn, null);
		TextView dialog_txt = (TextView) view.findViewById(R.id.dialog_txt);
		Button dialog_btn = (Button) view.findViewById(R.id.dialog_btn);

		AlertDialog dialog2 = adb.create();
		dialog2.setView(view, 0, 0, 0, 0);
		dialog_txt.setText(msg);
		dialog_btn.setOnClickListener(listener);
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return dialog2;
	}

	/**
	 * 上方有loading转圈动画图标，下方提示文字的dialog
	 * @param context
	 * @param msg
     * @return
     */
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;

	}
}
