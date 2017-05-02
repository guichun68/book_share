package zyzx.linke;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;

import zxing.CaptureActivity;
import zyzx.linke.activity.AboutUsAct;
import zyzx.linke.activity.BorrowedInBookAct;
import zyzx.linke.activity.FeedBackAct;
import zyzx.linke.activity.HomeAct;
import zyzx.linke.activity.ImportResultAct;
import zyzx.linke.activity.LoginAct;
import zyzx.linke.activity.ManualInputAct;
import zyzx.linke.activity.MyBooksAct;
import zyzx.linke.activity.PersonalCenterAct;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.base.UpdateService;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.DownloadUtil;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.PreferenceManager;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.ToastUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * 主页界面
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    private final int EXCEL_FILE_SELECT_CODE = 11;
    private TextView tvUserName;//用户昵称（login_name）
    private Dialog dialog;
    private TextView tvCreditScore;
    private int REQUEST_CODE_PERSONAL_ACT=0x17F;

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_me, container, false);
    }

    @Override
    public void initView() {
        mBackBtn.setVisibility(View.INVISIBLE);
        tvUserName = (TextView) mRootView.findViewById(R.id.tv_user_login_name);
        tvCreditScore = (TextView) mRootView.findViewById(R.id.tv_credit_score);
        mRootView.findViewById(R.id.rl_top).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_all_checkin).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_borrow_in).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_scan_input).setOnClickListener(this);//扫描
        mRootView.findViewById(R.id.rl_manual_input).setOnClickListener(this);//手动录入
        mRootView.findViewById(R.id.rl_log_out).setOnClickListener(this);//注销登录
        mRootView.findViewById(R.id.rl_check_update).setOnClickListener(this);//注销登录
        mRootView.findViewById(R.id.rl_about).setOnClickListener(this);//注销登录
        mRootView.findViewById(R.id.rl_modify_psw).setOnClickListener(this);//修改密码
        mRootView.findViewById(R.id.rl_feedback).setOnClickListener(this);//修改密码
        mRootView.findViewById(R.id.rl_export).setOnClickListener(this);//导出
        mRootView.findViewById(R.id.rl_import).setOnClickListener(this);//导入
        intiData();
    }

    private void intiData() {

        refreshUserInfo();
        mTitleText.setText("个人中心");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_scan_input:
                gotoActivity(CaptureActivity.class);
                break;
            case R.id.rl_manual_input:
                gotoActivity(ManualInputAct.class);
                break;

            case R.id.rl_all_checkin://我的所有书
                gotoActivity(MyBooksAct.class);
                break;
            case R.id.rl_borrow_in://已借入的书
                gotoActivity(BorrowedInBookAct.class);
                break;
            case R.id.rl_log_out:
                ((HomeAct)getActivity()).logoutEaseMob();
                PreferenceManager.getInstance().setAutoLoginFlag(false);
                getActivity().finish();
                gotoActivity(LoginAct.class);
                break;
            case R.id.rl_check_update:
                //showProgress("检查中…");
                ((HomeAct)getActivity()).mBinder.callCheckUpdate(new UpdateService.CheckUpdateCallBack() {
                    @Override
                    public void shouldUpdate(boolean shoudUpdate) {
                        dismissProgress();
                        if(!shoudUpdate){
                            showSnack(null,"已经是最新版本");
                        }else{
                            //do nothing, if app should update,the UpdateActivity will auto evoked.
                            Log.i("zyzx","should update");
                        }
                    }
                });
                break;
            case R.id.rl_about:
                gotoActivity(AboutUsAct.class);
                break;
            case R.id.rl_modify_psw://修改密码
                gotoActivity(ModifyPswAct.class);
                break;
            case R.id.rl_feedback:
                gotoActivity(FeedBackAct.class);
                break;
            case R.id.rl_import:
                showFileChooser();
                break;
            case R.id.rl_export://导出excel
                dialog = CustomProgressDialog.getPromptDialog2Btn(mContext, UIUtil.getString(R.string.export_tip), UIUtil.getString(R.string.confirm), UIUtil.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgress("请稍后…");
                        downloadFile();
                    }
                },null);
                dialog.show();
                break;
            case R.id.rl_top:
                Intent in = new Intent(getActivity(),PersonalCenterAct.class);
                startActivityForResult(in,REQUEST_CODE_PERSONAL_ACT);
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "请选择Excel文件"), EXCEL_FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 下载导出的excle清单（由服务器生成excel，实际客户端为下载操作）
     */
    protected void downloadFile() {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_id",PreferenceManager.getInstance().getLastLoginUserId());
        DownloadUtil.get().download(GlobalParams.urlExportExcle, GlobalParams.BaseDir,param, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        String appPath = FileUtil.getExternalStoragePath();
                        if(appPath==null){
                            ToastUtil.show(mContext,"未检测到内存卡，导出失败！");
                            return;
                        }
                        String filePath = appPath+"getSummary.action";
                        File downloadFile = new File(filePath);
                        File newFile = new File(appPath+"我的书单.xls");
                        if(downloadFile.exists()){
                            if(downloadFile.renameTo(newFile)){
                                CustomProgressDialog.getPromptDialog(mContext,"已成功导出到:\n"+newFile.getAbsolutePath(),null).show();
                            }
                        }else{
                            if(newFile.exists()){
                                // 导出（下载）成功
                                CustomProgressDialog.getPromptDialog(mContext,"已成功导出到:\n"+newFile.getAbsolutePath(),null).show();
                            }else{
                                // 导出（下载）失败
                                UIUtil.showToastSafe(R.string.err_request);
                            }
                        }
                    }
                });
            }
            @Override
            public void onDownloading(long total,int progress) {
//                UIUtil.showTestLog("exportting……");
            }
            @Override
            public void onDownloadFailed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        String message = "导出错误";
                        UIUtil.showToastSafe(message);
                    }
                });

            }
        });
    }


    public void showSnack(String btnText,String msg) {
        final Snackbar snackbar = Snackbar.make(mRootView.findViewById(R.id.civ), msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(btnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if ((resultCode != RESULT_OK)){
            UIUtil.showToastSafe("已取消选择");
            return;
        }
        if(requestCode==EXCEL_FILE_SELECT_CODE){
            // Get the Uri of the selected file
            Uri uri = intent.getData();
            String path = FileUtil.getUriPath(mContext, uri);
            File file = null;
            if(path!=null) {
                file = new File(path);
            }
            if(file == null){
                UIUtil.showToastSafe("头像文件解析错误");
                return;
            }
            if(!(file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls"))){
                UIUtil.showToastSafe("解析错误，文件格式有误");
            }else{
                if(file.exists()){
                    double length = file.length();
                    if(length/1024/1024>2){//文件大于2M，过大
                        UIUtil.showToastSafe("文件过大，仅支持2M内文件导入");
                    }else{
                        uploadExcelFile(path);
                    }
                }
            }
        }else if(requestCode==REQUEST_CODE_PERSONAL_ACT){
            if(intent.getBooleanExtra(BundleFlag.SHOULD_REFRESH,false)){
                refreshUserInfo();
            }
        }
    }

    public void refreshUserInfo(){
        tvUserName.setText(GlobalParams.getLastLoginUser().getLogin_name());
        tvCreditScore.setText(String.valueOf(GlobalParams.getLastLoginUser().getCreditScore()));
        if(!StringUtil.isEmpty(GlobalParams.getLastLoginUser().getHead_icon())){
            Glide.with(mContext).load(GlobalParams.getLastLoginUser().getHead_icon()).into((CircleImageView)mRootView.findViewById(R.id.civ));
        }
    }

    private void uploadExcelFile(String filePath){
        showDefProgress();
        getUserPresenter().uploadExcelFile(GlobalParams.getLastLoginUser().getUserid(), filePath, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        final String json = (String) obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe("导入发生错误，请稍后再试！");
                            return;
                        }
                        JSONObject jsonObj = JSON.parseObject(json);
                        int code = jsonObj.getInteger("code");
                        switch (code){
                            case 400:
                                CustomProgressDialog.getPromptDialog(mContext,"文件为空,请重新选择文件",null).show();
                                break;
                            case 500:
                                CustomProgressDialog.getPromptDialog(mContext,"文件格式错误,目前仅支持Excel文件导入,请检查后重试!",null).show();
                                break;
                            case 600:
                                CustomProgressDialog.getPromptDialog(mContext,"导入错误，检测到不合模板规范的excel文档，请确保至少有“ISBN”和“书名”两列！",null).show();
                                break;
                            case 700:
                                CustomProgressDialog.getPromptDialog(mContext,"导入文档有误，请确保文档未损坏",null).show();
                                break;
                            case 200:
                                succDialog = CustomProgressDialog.getPromptDialog2Btn(mContext, "导入完毕,点击确定查看导入结果！", "确定", "取消",new DialogOnClickListener(json) ,null);
                                succDialog.show();
                                break;
                        }
                    }
                });

            }

            @Override
            public void onFailure(Object obj) {
                UIUtil.showToastSafe("导入失败");
            }
        });
    }
    private Dialog succDialog;//导入成功dialog
    private class DialogOnClickListener implements View.OnClickListener{
        String json;
        DialogOnClickListener(String json){
            this.json = json;
        }
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("json",json);
            gotoActivity(ImportResultAct.class,bundle);
            if(succDialog!=null)
                succDialog.dismiss();
        }
    }
}
