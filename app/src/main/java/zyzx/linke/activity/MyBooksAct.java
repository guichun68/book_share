package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import zyzx.linke.global.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: 我的所有书籍列表页(除借入书籍外)
 */

public class MyBooksAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private LinearLayout llRoot;
    private PullToRefreshListView mPullRefreshListView;
    private AllMyBookAdapter myBookAdapter;
    private ArrayList<MyBookDetailVO> mBooks;
    private int mPageNum;
    private PopupWindow pop;
    private int mWindowWidth;
    private boolean isLoadingMore;//是否是加载更多的动作

    @Override
    protected int getLayoutId() {
        return R.layout.act_all_my_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWindowWidth = size.x;
//        mWindowHeight = size.y;

        llRoot = (LinearLayout) findViewById(R.id.ll_root);
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
                pop.showAtLocation(parent, Gravity.TOP + Gravity.START, mWindowWidth / 2 - pop.getWidth() / 2, location[1] + UIUtil.dip2px(10));

                AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                aa.setDuration(100);
                ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
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

    private Dialog mPromptDialog;
    private int tempPosition;//临时记录点击条目跳转到分享页面时的position
    /**
     * 初始化并设置popupWin中的控件
     * @param popView popWindow View
     * @param bookDetailVO book detail bean
     */
    private void setPopwinViewControls(final View popView, final MyBookDetailVO bookDetailVO, final int position) {

        final TextView item1 = (TextView) popView.findViewById(R.id.tv_item1);
        final TextView item2 = (TextView) popView.findViewById(R.id.tv_item2);
        switch (bookDetailVO.getStatus()) {
            case 1://在书架上
                item1.setText("从书架上移除");
                item2.setText("在地图中分享");
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(mContext,"确定要移除《"+bookDetailVO.getBook().getTitle()+"》这本书么?","确定","保留",
                                new PopItemClickListener(bookDetailVO,position,PopItemClickListener.REMOVE_FROM_BOOKRACK),null);
                        mPromptDialog.show();
                    }
                });
                item2.setOnClickListener(new PopItemClickListener(bookDetailVO,position,PopItemClickListener.SHARE_ON_MAP));
                break;
            case 2://地图分享中。。。
                item1.setText("取消分享");
                item2.setText("取消分享并从书架删除");
                item1.setOnClickListener(new PopItemClickListener(bookDetailVO,position,PopItemClickListener.CANCEL_SHARE));

                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(mContext,"确定取消分享并移除《"+bookDetailVO.getBook().getTitle()+"》这本书么?","确定","取消",
                                new PopItemClickListener(bookDetailVO,position,PopItemClickListener.CANCEL_AND_REMOVE),null);
                        mPromptDialog.show();

                    }
                });
                break;
        }
    }

    private class PopItemClickListener implements View.OnClickListener{
        private int operId;
        private MyBookDetailVO bookDetailVO;
        private static final int REMOVE_FROM_BOOKRACK = 0;//从书架移除
        private static final int SHARE_ON_MAP = 1;//在地图中分享
        private static final int CANCEL_SHARE = 2;//取消地图分享
        private static final int CANCEL_AND_REMOVE = 3;//取消地图中分享并从书架移除
        private int position;//在listView中的位置索引
        /**
         * @param bookDetailVO 操作的书籍
         * @param operId 操作id
         */
        PopItemClickListener(MyBookDetailVO bookDetailVO,int position,Integer operId){
            this.operId = operId;
            this.bookDetailVO = bookDetailVO;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            CustomProgressDialog.dismissDialog(mPromptDialog);
            switch (operId){
                case REMOVE_FROM_BOOKRACK://从书架删除
                    showDefProgress();
                    getBookPresenter().deleteUserBook(GlobalParams.gUser.getUserid(), bookDetailVO.getBook().getB_id(), null, new CallBack() {

                        @Override
                        public void onSuccess(final Object obj) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgress();
                                    if (pop != null && pop.isShowing()) {
                                        pop.dismiss();
                                    }
                                    String json = (String) obj;
                                    JSONObject jsonObj = JSON.parseObject(json);
                                    Integer code = jsonObj.getInteger("code");
                                    if (code != null && code == 200) {
                                        Snackbar.make(llRoot,"删除成功",Snackbar.LENGTH_SHORT).show();
                                        mBooks.remove(bookDetailVO);
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
                                    dismissProgress();
                                    Snackbar.make(llRoot,"删除失败",Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    break;
                case SHARE_ON_MAP://地图中分享
                    Intent in = new Intent(mContext,BookShareOnMapAct.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BundleFlag.BOOK,bookDetailVO.getBook());
                    in.putExtras(bundle);
                    tempPosition = position;
                    startActivityForResult(in,tempPosition);
                    break;
                case CANCEL_SHARE://取消分享
                    showDefProgress();
                    pop.dismiss();
                    getBookPresenter().cancelShare(bookDetailVO.getUserBookId(), bookDetailVO.getMapId(),new CallBack() {
                        @Override
                        public void onSuccess(final Object obj) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgress();
                                    final String json = (String) obj;
                                    if (StringUtil.isEmpty(json)) {
                                        Snackbar.make(llRoot,"未操作成功",Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                    JSONObject jsonObj = JSON.parseObject(json);
                                    Integer code = jsonObj.getInteger("code");
                                    if (code != null && code == 200) {
                                        Snackbar.make(llRoot,"已取消分享",Snackbar.LENGTH_SHORT).show();
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
                                    dismissProgress();
                                    Snackbar.make(llRoot,"操作失败", Snackbar.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    break;
                case CANCEL_AND_REMOVE://取消分享并从书架移除
                    showDefProgress();
                    getBookPresenter().cancelShareAndDelBook(bookDetailVO.getUserBookId(), bookDetailVO.getMapId(),new CallBack() {
                        @Override
                        public void onSuccess(final Object obj) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pop.dismiss();
                                    dismissProgress();
                                    final String json = (String) obj;
                                    if (StringUtil.isEmpty(json)) {
                                        Snackbar.make(llRoot,"操作失败",Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                    JSONObject jsonObj = JSON.parseObject(json);
                                    Integer code = jsonObj.getInteger("code");
                                    if (code != null && code == 200) {
                                        Snackbar.make(llRoot,"操作成功",Snackbar.LENGTH_SHORT).show();
                                        mBooks.remove(position-1);
                                        myBookAdapter.notifyDataSetChanged();
                                    }else{
                                        Snackbar.make(llRoot,"操作失败",Snackbar.LENGTH_SHORT).show();

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
                                    dismissProgress();
                                    Snackbar.make(llRoot,"操作失败",Snackbar.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(1001==resultCode){
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
        showDefProgress();
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
        mPageNum++;
        isLoadingMore = true;
        getBooks(GlobalParams.gUser.getUserid(), mPageNum);
    }

    /**
     * 获取我的所有书籍（不包含借入的书籍）
     *
     * @param userid userId
     * @param pageNum pageNo
     */
    private void getBooks(Integer userid, int pageNum) {
        getBookPresenter().getMyBooks(userid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        ArrayList<MyBookDetailVO> books = (ArrayList<MyBookDetailVO>) obj;
                        if (books == null || books.isEmpty()) {
                            UIUtil.showToastSafe("没有更多书籍了!");
                            if(isLoadingMore){
                                mPageNum--;
                                if(mPageNum<0)mPageNum=0;
                                isLoadingMore=false;
                            }
                        } else {
                            mBooks.addAll(books);
                            myBookAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                dismissProgress();
                mPullRefreshListView.onRefreshComplete();
                mPullRefreshListView.clearAnimation();
                if(isLoadingMore){
                    mPageNum--;
                    if(mPageNum<0)mPageNum=0;
                    isLoadingMore = false;
                }
                UIUtil.showToastSafe("未能获取书籍信息!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pop!=null){
            pop.dismiss();
        }
    }
}
