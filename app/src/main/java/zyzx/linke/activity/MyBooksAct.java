package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.AllMyBookAdapter;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: 我的所有书籍列表页(除借入书籍外)
 */

public class MyBooksAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener, AbsListView.OnScrollListener {
    private PullToRefreshListView mPullRefreshListView;
    private AllMyBookAdapter myBookAdapter;
    private ArrayList<MyBookDetailVO> mBooks;
    private int pageNum;
    private PopupWindow pop;
    private int mWindowHeight, mWindownWidth;

    @Override
    protected int getLayoutId() {
        return R.layout.act_all_my_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWindownWidth = size.x;
        mWindowHeight = size.y;

        mTitleText.setText("我的书架");
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//上拉加载更多
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        actualListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                View popView = View.inflate(mContext, R.layout.pop_modify_book, null);
                setPopwinViewControls(popView, (MyBookDetailVO) parent.getItemAtPosition(position), position);
                //测量布局的大小
                popView.measure(0, 0);
                pop = new PopupWindow(popView, popView.getMeasuredWidth(), popView.getMeasuredHeight(), true);
                pop.setOutsideTouchable(true);
                pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pop.setContentView(popView);


                int[] location = new int[2];
                view.getLocationInWindow(location);
                pop.showAtLocation(parent, Gravity.TOP + Gravity.LEFT, mWindownWidth / 2 - pop.getWidth() / 2, location[1] + UIUtil.dip2px(10));

                AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                aa.setDuration(100);
                ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(100);

                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                popView.startAnimation(set);
                return true;
            }
        });

        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //进入图书详情页
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                    return;
                }
                MyBookDetailVO myBookDetailVO = (MyBookDetailVO) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, CommonBookDetailAct.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("book", myBookDetailVO.getBook());
                intent.putExtra(BundleFlag.SHOWADDRESS, false);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        mBooks = new ArrayList<>();
        myBookAdapter = new AllMyBookAdapter(mContext, mBooks);
        actualListView.setAdapter(myBookAdapter);
    }


    private int tempPosition;//临时记录点击条目跳转到分享页面时的position
    /**
     * 初始化并设置popupWin中的控件
     *
     * @param popView
     * @param bookDetailVO
     */
    private void setPopwinViewControls(final View popView, final MyBookDetailVO bookDetailVO, final int position) {

        TextView item1 = (TextView) popView.findViewById(R.id.tv_item1);
        TextView item2 = (TextView) popView.findViewById(R.id.tv_item2);
        switch (bookDetailVO.getStatus()) {
            case 1://在书架上
                item1.setText("从书架中删除");
                item2.setText("在地图中分享");
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //从书架删除
                        GlobalParams.getBookPresenter().deleteUserBook(GlobalParams.gUser.getUserid(), bookDetailVO.getBook().getB_id(), null, new CallBack() {

                            @Override
                            public void onSuccess(final Object obj) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pop != null && pop.isShowing()) {
                                            pop.dismiss();
                                        }
                                        String json = (String) obj;
                                        JSONObject jsonObj = JSON.parseObject(json);
                                        Integer code = jsonObj.getInteger("code");
                                        if (code != null && code == 200) {
                                            UIUtil.showToastSafe("删除成功");
                                            mBooks.remove(bookDetailVO);
                                            myBookAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onFailure(Object obj) {
                                UIUtil.showToastSafe("删除失败");
                            }
                        });
                    }
                });
                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(mContext,BookShareOnMapAct.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BundleFlag.BOOK,bookDetailVO.getBook());
                        in.putExtras(bundle);
//                        gotoActivity(BookShareOnMapAct.class,false,bundle);
                        tempPosition = position;
                        startActivityForResult(in,tempPosition);
                    }
                });
                break;
            case 2://地图分享中。。。
                item1.setText("取消分享");
                item2.setText("取消分享并从书架删除");
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消分享
                        GlobalParams.getBookPresenter().cancelShare(bookDetailVO.getUserBookId(), bookDetailVO.getMapId(),new CallBack() {
                            @Override
                            public void onSuccess(final Object obj) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pop.dismiss();
                                        final String json = (String) obj;
                                        if (StringUtil.isEmpty(json)) {
                                            UIUtil.showToastSafe("未能成功取消");
                                            return;
                                        }
                                        JSONObject jsonObj = JSON.parseObject(json);
                                        Integer code = jsonObj.getInteger("code");
                                        if (code != null && code == 200) {
                                            UIUtil.showToastSafe("已取消分享");
                                            mBooks.get(position - 1).setStatus(1);
                                            myBookAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Object obj) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pop.dismiss();
                                        UIUtil.showToastSafe("取消失败");
                                    }
                                });

                            }
                        });
                    }
                });
                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(1001==RESULT_OK){
                if(requestCode==tempPosition){
                    mBooks.get(tempPosition-1).setStatus(2);
                    mBooks.get(tempPosition-1).setMapId(Integer.valueOf(data.getStringExtra("map_id")));
                    if(pop!=null){
                        pop.dismiss();
                    }
                    myBookAdapter.notifyDataSetChanged();
                    UIUtil.showTestLog("zyzx","新插入的云图id："+data.getStringExtra("map_id"));
                }
            }
    }

    @Override
    protected void initData() {
        mBooks.clear();
        getBooks(GlobalParams.gUser.getUserid(), 0);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel(getResources().getString(R.string.pull_label));
        endLabels.setRefreshingLabel(getResources().getString(
                R.string.refresh_label));
        endLabels.setReleaseLabel(getResources().getString(
                R.string.release_label));
        endLabels.setLoadingDrawable(getResources().getDrawable(
                R.mipmap.publicloading));
        pageNum++;
        getBooks(GlobalParams.gUser.getUserid(), pageNum);
    }

    /**
     * 获取我的所有书籍（不包含借入的书籍）
     *
     * @param userid
     * @param pageNum
     */
    private void getBooks(Integer userid, int pageNum) {
        GlobalParams.getBookPresenter().getMyBooks(userid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        ArrayList<MyBookDetailVO> books = (ArrayList<MyBookDetailVO>) obj;
                        if (books == null || books.isEmpty()) {
                            UIUtil.showToastSafe("没有更多书籍了!");
                        } else {
                            mBooks.addAll(books);
                            myBookAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                mPullRefreshListView.onRefreshComplete();
                mPullRefreshListView.clearAnimation();
                UIUtil.showToastSafe("未能获取书籍信息!");
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }


}
