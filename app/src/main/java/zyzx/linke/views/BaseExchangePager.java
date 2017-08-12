package zyzx.linke.views;

import android.content.Context;
import android.view.View;

/**
 * Created by austin on 2017/8/12.
 * Desc: 交换市场选项卡页 抽取类
 */

public abstract class BaseExchangePager {
    protected Context context;
    protected View rootView;

    public BaseExchangePager(Context context,int layoutResId){
        this.context = context.getApplicationContext();
        rootView = View.inflate(this.context,layoutResId,null);
    }

    public View getRootView(){
        return rootView;
    }
}
