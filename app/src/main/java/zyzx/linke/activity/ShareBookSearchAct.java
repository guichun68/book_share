package zyzx.linke.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;

/**
 * Created by Austin on 2017-08-27.
 * Desc: 分享中心之--搜索
 */

public class ShareBookSearchAct extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.act_search_share_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//用toolbar替换原来的ActionBar
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.ab_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.finish();//真正实现回退功能的代码
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

}
