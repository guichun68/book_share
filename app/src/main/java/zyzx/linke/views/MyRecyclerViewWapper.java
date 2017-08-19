package zyzx.linke.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;

import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/8/19.
 */

public class MyRecyclerViewWapper extends RecyclerView{


    public MyRecyclerViewWapper(Context context) {
        super(context);
    }

    public MyRecyclerViewWapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerViewWapper(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    private float x1,y1,x2,y2;
    private int slidStatus = 0;
    public static final int SLIDE_UP = 1,SLDE_DOWN = 2,SLIDE_IDLE = 0;

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
                if(listener != null){
                    listener.onScrollStateChanged(recyclerView,newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(listener != null){
                    listener.onScrolled(recyclerView,dx,dy);
                }
            }
        });
    }

    public abstract static class MyOnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState){}
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){}
    }

}
