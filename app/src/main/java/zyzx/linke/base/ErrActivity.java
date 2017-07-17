package zyzx.linke.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.hyphenate.util.EMLog;

import zyzx.linke.R;
import zyzx.linke.activity.AppManager;
import zyzx.linke.activity.LoginAct;
import zyzx.linke.global.MyEaseConstant;
import zyzx.linke.utils.PreferenceManager;

/**
 * 错误提示页（比如用户下线通知、账号被限制等）
 */
public class ErrActivity extends BaseActivity {
	private final String TAG = ErrActivity.class.getSimpleName();
	private String mErrType;//错误类型
	@Override
	protected int getLayoutId() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		return 0;//no layout
	}

	@Override
	protected void initView(Bundle saveInstanceState) {
		mErrType = getIntent().getStringExtra("error_type");
		showExceptionDialog(mErrType);
	}
	@Override
	protected void initData() {}
	
	private android.app.AlertDialog.Builder exceptionBuilder;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;

	private int getExceptionMessageId(String exceptionType) {
		if(exceptionType.equals(MyEaseConstant.ACCOUNT_CONFLICT)) {
			return R.string.connect_conflict;
		} else if (exceptionType.equals(MyEaseConstant.ACCOUNT_REMOVED)) {
			return R.string.em_user_remove;
		} else if (exceptionType.equals(MyEaseConstant.ACCOUNT_FORBIDDEN)) {
			return R.string.user_forbidden;
		}
		return R.string.Network_error;
	}

	/**
	 * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
	 */
	private void showExceptionDialog(String exceptionType) {
		EaseUIHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!ErrActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (exceptionBuilder == null)
					exceptionBuilder = new android.app.AlertDialog.Builder(ErrActivity.this);
				exceptionBuilder.setTitle(st);
				exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));
				exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						exceptionBuilder = null;
						AppManager.getAppManager().finishAllActivity();
						Intent intent = new Intent(ErrActivity.this, LoginAct.class);
						PreferenceManager.getInstance().setAutoLoginFlag(false);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				exceptionBuilder.setCancelable(false);
				exceptionBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}
		}
	}



}
