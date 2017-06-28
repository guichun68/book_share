package zyzx.linke.base;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.UpdateBeanVO;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

public class UpdateService extends Service implements CallBack {

	private PackageManager pm;
	private int currVersionCode;
	private CheckUpdateCallBack callBack;
	private MyBinder localBinder = new MyBinder();


	@Override
	public void onCreate() {
		
		pm = getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getPackageName(), 0);
			currVersionCode = pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}

	@Override
	public void onSuccess(Object obj, int... code) {
		String response = (String) obj;
		GlobalParams.isCheckedUpdate = true;
		if(StringUtil.isEmpty(response)){
			UIUtil.print("未能获取最新版本");
			UIUtil.showToastSafe("检查更新失败");
			return;
		}
		UpdateBeanVO update = JSON.parseObject(response, UpdateBeanVO.class);
		if(update==null){
			UIUtil.print("未能获取最新版本2");
			UIUtil.showToastSafe("检查更新失败");
			return;
		}
		// 415不需要更新 ; 400有更新
		if ((update.getCode())==200) {
//			String url =  update.getUrl();// 下载地址
//			String descrip = (String)(jsonObj.get("des"));
//			String isForceUpdate = (String) jsonObj.get("forceUpdate");
//				Boolean isForceUpdate = jsonObj.getBoolean("forceUpdate");
			if(update.getForceUpdate() != null && update.getForceUpdate().equals("YES")){//需要强制更新，不更新则退出
				Intent inten = new Intent(UpdateService.this,ForceUpdateActivity.class);
				inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				inten.putExtra("desc", update.getDes());
				inten.putExtra("url", update.getUrl());
				startActivity(inten);
			}else{
				// 启动一个透明背景的Activty，弹出下载新版本提示dialog
				Intent intent = new Intent(UpdateService.this,UpdateActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("desc", update.getDes());
				intent.putExtra("url", update.getUrl());
				startActivity(intent);
			}

//				String description = new String( (String)(jsonObj.get("description")),"UTF-8");// 版本描述
//				showConfirmUpdateDialog(url, descrip);

		} else {
			if (update.getCode()==500) {
				if(callBack!=null){
					callBack.shouldUpdate(false);
				}
//					OtherUtils.showShortToastInAnyThread(act, "已是最新版本.");
			} else {
				UIUtil.print( "未知的检查更新参数，返回代码:" + update.getCode());
			}
		}

	}

	@Override
	public void onFailure(Object obj, int... code) {
		GlobalParams.isCheckedUpdate = true;
	}

	public class MyBinder extends Binder {
		public void callCheckUpdate(CheckUpdateCallBack callBack){
			checkUpdate(callBack);
		}
	}
	void checkUpdate(CheckUpdateCallBack callBack){
		this.callBack = callBack;
		GlobalParams.getBookPresenter().checkUpdate(currVersionCode,UpdateService.this,false);
	}
	

	public interface CheckUpdateCallBack{
		void shouldUpdate(boolean shoudUpdate);
	}
	
}
