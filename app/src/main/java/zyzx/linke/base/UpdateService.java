package zyzx.linke.base;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.qiangxi.checkupdatelibrary.bean.CheckUpdateInfo;
import com.qiangxi.checkupdatelibrary.service.BaseService;

import java.io.File;
import java.util.Map;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UpdateBeanVO;
import zyzx.linke.utils.UIUtil;


public class UpdateService extends BaseService implements CallBack {

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

	private CheckUpdateInfo mCheckUpdateInfo;

	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}

	@Override
	public void downloading(int currentProgress, int totalProgress) {

	}

	@Override
	public void downloadSuccess(File file) {

	}

	@Override
	public void downloadFailure(String failureMessage) {

	}

	@Override
	public void onSuccess(Object obj, int... code) {
		String response = (String) obj;
		GlobalParams.isCheckedUpdate = true;
		ResponseJson rj = new ResponseJson(response);
		if(rj.errorCode == ResponseJson.NO_DATA){
			UIUtil.showTestLog("未能获取最新版本");
			UIUtil.showToastSafe("检查更新失败");
			return;
		}
		switch (rj.errorCode){
			case 0://出错
				UIUtil.showToastSafe("检查更新失败");
				break;
			case 1://已是最新
				if(callBack!=null){
					callBack.shouldUpdate(false);
				}
				break;
			case 2://需要强制更新
				String resultJson = (String) ((Map<String,Object>)rj.data.get(0)).get("resultJson");
				UpdateBeanVO update = JSON.parseObject(resultJson,UpdateBeanVO.class);

				Intent inten = new Intent(UpdateService.this,ForceUpdateActivity.class);
				inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				inten.putExtra("desc", update.getDes());
				inten.putExtra("url", update.getUrl());
				inten.putExtra("fileName",update.getApkFileName());
				startActivity(inten);
				break;
			case 3://不强制更新
				String resultJson2 = (String) ((Map<String,Object>)rj.data.get(0)).get("resultJson");
				UpdateBeanVO update2 = JSON.parseObject(resultJson2,UpdateBeanVO.class);
				// 启动一个透明背景的Activty，弹出下载新版本提示dialog
				Intent intent = new Intent(UpdateService.this,UpdateActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("desc", update2.getDes());
				intent.putExtra("url", update2.getUrl());
				intent.putExtra("fileName",update2.getApkFileName());
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onFailure(Object obj, int... code) {
		GlobalParams.isCheckedUpdate = true;
		if(obj instanceof String){
			UIUtil.showToastSafe((String) obj);
		}
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
