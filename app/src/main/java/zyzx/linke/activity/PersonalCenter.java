package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zxing.CaptureActivity;
import zyzx.linke.R;
import zyzx.linke.adapter.GalleryAdapter;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.views.CircleImageView;

/**
 * Created by austin on 2017/2/22.
 * Desc: 个人中心
 */

public class PersonalCenter extends BaseActivity {
    private RecyclerView mRecyclerView;//显示我的所有书籍
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;
    private Button btnScan,btnManual;//ISBN扫描、手动录入
    private CircleImageView mCiv;

    @Override
    protected int getLayoutId() {
        return R.layout.act_personal_center;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //得到控件
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnManual = (Button) findViewById(R.id.btn_manual);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_galary);
        mCiv = (CircleImageView) findViewById(R.id.civ);
        mCiv.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnManual.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        initDatas();
        mTitleText.setText("个人中心");
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new GalleryAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
    }
    public void initDatas(){
        mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.a,
                R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e,
                R.drawable.f));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_scan:
                GlobalParams.gIsPersonCenterScan = true;
                gotoActivity(CaptureActivity.class,false);
                break;
            case R.id.btn_manual:
                gotoActivity(ManualInputAct.class,false);
                break;
            case R.id.civ:

                break;
        }
    }
}
