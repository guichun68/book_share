package zyzx.linke.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.ImportResultAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.utils.StringUtil;

/**
 * Created by austin on 2017/4/8.
 * Desc: 导入结果页
 */

public class ImportResultAct extends BaseActivity {

    private ListView mListView;
    private ImportResultAdapter mAdapter;
    private ArrayList<String> results = new ArrayList<>();
    private TextView tvTip;

    @Override
    protected int getLayoutId() {
        return R.layout.act_import_result;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("导入结果");
        mListView = (ListView) findViewById(R.id.lv);
        tvTip = (TextView) findViewById(R.id.tv_tip);

        String json = getIntent().getExtras().getString("json");
        if(StringUtil.isEmpty(json)){
            return;
        }

        JSONObject parseObject = JSON.parseObject(json);
        JSONArray succList = parseObject.getJSONArray("succ_list");
        JSONArray errList = parseObject.getJSONArray("err_list");
        tvTip.setText("执行结果:导入成功 "+succList.size()+" 条记录，导入失败 "+errList.size()+" 条记录");
        results.clear();
        for(int i=0;i<succList.size();i++){
            results.add(succList.get(i).toString());
        }
        for(int j=0;j<errList.size();j++){
            results.add(errList.get(j).toString());
        }
        mAdapter = new ImportResultAdapter(results);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {}
}
