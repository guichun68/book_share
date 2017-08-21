package zyzx.linke.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;

import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/8/19.
 */

public class MyRecyclerViewWapper extends RecyclerView{

    private LinearLayoutManager linearLayoutManager;
    private float x1,y1,x2,y2;
    private int slidStatus = 0;
    private final int SLOP = 5;//垂直方向最小滑动响应距离
    private int lastVisibleItemPosition;
    public static final int SLIDE_UP = 1,SLDE_DOWN = 2,SLIDE_IDLE = 0;

    public MyRecyclerViewWapper(Context context) {
        super(context);
        linearLayoutManager =new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        this.setLayoutManager(linearLayoutManager);
    }

    public MyRecyclerViewWapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        linearLayoutManager =new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        this.setLayoutManager(linearLayoutManager);
    }

    public MyRecyclerViewWapper(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        linearLayoutManager =new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        this.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(y1 - y2 > 50) {
                slidStatus = SLIDE_UP;
                UIUtil.showTestLog("slide","向上滑");
//                Toast.makeText(MainActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
            } else if(y2 - y1 > 50) {
                slidStatus = SLDE_DOWN;
                UIUtil.showTestLog("slide","向下滑");
//                Toast.makeText(MainActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
            } else if(x1 - x2 > 50) {
                UIUtil.showTestLog("slide","向<--滑");
//                Toast.makeText(MainActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
            } else if(x2 - x1 > 50) {
                UIUtil.showTestLog("slide","向→滑");
//                Toast.makeText(MainActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
            }
        }

        return super.dispatchTouchEvent(event);
    }

    public int getSlidStatus(){
        return slidStatus;
    }

    public void setSlidStatus(int status){
        slidStatus = status;
    }

    private List<OnScrollListener> mScrollListeners;

    public void AddMyOnScrollListener(final MyOnScrollListener listener) {

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 ==recyclerView.getAdapter().getItemCount()) {
                    if(((MyRecyclerViewWapper)recyclerView).getSlidStatus()==MyRecyclerViewWapper.SLIDE_UP
                            && ((MyCommonAdapter)recyclerView.getAdapter()).load_more_status!=MyCommonAdapter.Status.STATUS_NO_MORE_DATE){
                        if(listener != null){
                            listener.onScrollStateChanged((MyRecyclerViewWapper) recyclerView,newState,true);
                        }
                    }else{
                        setSlidStatus(MyRecyclerViewWapper.SLIDE_IDLE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItemPosition =linearLayoutManager.findLastCompletelyVisibleItemPosition();
                /*if(dy<0){//向下滑动
                    if(Math.abs(dy)>=SLOP){
                        isRefreshing = true;
                    }
                }else{
                    if(dy!=0){
                        //向上滑动
                        isRefreshing = false;
                    }
                }
                */
                super.onScrolled(recyclerView, dx, dy);
                if(listener != null){
                    listener.onScrolled(recyclerView,dx,dy);
                }
            }
        });
    }

    public abstract static class MyOnScrollListener {
        public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore){}
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){}
    }

}
