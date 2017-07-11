package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.adapter.BookClassifyAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.EnumConst;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/7/11.
 * Desc: 个人手写资料录入
 */

public class ManualPersonBookAct extends BaseActivity{

    private AppCompatSpinner spBookClassify;
    private BookClassifyAdapter mAdapter;
    private ArrayList<EnumConst> mClassifys = new ArrayList<>();
    private TextView tvSave;

    @Override
    protected int getLayoutId() {
        return R.layout.act_manual_input2;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        spBookClassify = (AppCompatSpinner) findViewById(R.id.sp_book_classify);
        mAdapter = new BookClassifyAdapter(mClassifys);
        spBookClassify.setAdapter(mAdapter);
        mTitleText.setText("手写资料录入");
        tvSave = (TextView) findViewById(R.id.tv_add_mylib);
        tvSave.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        showDefProgress();
        getBookPresenter().getBookClassify(new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                String json = (String) obj;
                if(StringUtil.isEmpty(json)){
                    UIUtil.showToastSafe("未能获取分类信息，请返回重试");
                    return;
                }
                DefindResponseJson drj = new DefindResponseJson(json);
                mClassifys.clear();
                switch (drj.errorCode){
                    case 1:
                        List items = drj.getData().getItems();
                        for(int i=0;i<items.size();i++){
                            EnumConst ec = new EnumConst();
                            ec.setId((String)((Map)items.get(i)).get("ID"));
                            ec.setName((String)((Map)items.get(i)).get("NAME"));
                            ec.setCode((String)((Map)items.get(i)).get("CODE"));
                            ec.setNameSpace((String)((Map)items.get(i)).get("NAMESPACE"));
                            ec.setCreateDate(new Date((Long)((Map)items.get(i)).get("CREATE_DATE")));
                            mClassifys.add(ec);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取分类信息,请返回重试");
                        break;
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                if(obj instanceof String){
                    UIUtil.showToastSafe((String) obj);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_add_mylib:

                break;
        }
    }
}
