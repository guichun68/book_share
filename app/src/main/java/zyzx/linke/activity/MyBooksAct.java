package zyzx.linke.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.AllMyBookAdapter;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserBooks;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/3/16.
 * Desc: 我的所有书籍列表页(除借入书籍外)--我的书架
 */

public class MyBooksAct extends BaseActivity{
    private LinearLayout llRoot;
    private MyRecyclerViewWapper mMyRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private AllMyBookAdapter myBookAdapter;
    private ArrayList<MyBookDetailVO> mBooks;
    private int mPageNum = 1;
    private PopupWindow pop;
    private int mWindowWidth;
    private boolean isLoadingMore;//是否是加载更多的动作
    private AlertDialog mDialogExchange;

    @Override
    protected int getLayoutId() {
        return R.layout.act_all_my_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mBooks = new ArrayList<>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                isLoadingMore = false;
                mPageNum = 1;
                getBooks(GlobalParams.getLastLoginUser().getUid(), mPageNum);
            }
        });
        mMyRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);
//        mMyRecyclerView.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.HORIZONTAL));
        myBookAdapter = new AllMyBookAdapter(this, mBooks,R.layout.item_my_books,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);
        mMyRecyclerView.setAdapter(myBookAdapter);
        mMyRecyclerView.AddMyOnScrollListener(new MyRecyclerViewWapper.MyOnScrollListener() {
            @Override
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore) {
                if(isLoadMore){
                    mPageNum++;
                    isLoadingMore = true;
                    getBooks(GlobalParams.getLastLoginUser().getUid(), mPageNum);
                    myBookAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWindowWidth = size.x;
//        mWindowHeight = size.y;

        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        mTitleText.setText("我的书架");


        myBookAdapter.setOnClickListener(new AllMyBookAdapter.OnClickListener() {
            @Override
            public boolean onLongItemClickListener(View view, MyBookDetailVO bookDetailVO, int position) {
                View popView = View.inflate(mContext, R.layout.pop_modify_book, null);

                setPopwinViewControls(popView, bookDetailVO, position);
                //测量布局的大小
                popView.measure(0, 0);
                int popWidth = popView.getMeasuredWidth();
                int popHeight = popView.getMeasuredHeight();
//                pop = new PopupWindow(popView, popView.getMeasuredWidth(), popView.getMeasuredHeight(), true);
                pop = new PopupWindow(popView);
                // 加上这个popupwindow中的ListView才可以接收点击事件
                pop.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

                pop.setOutsideTouchable(true);
                pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pop.setContentView(popView);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                if(popHeight<view.getMeasuredHeight()){
                    pop.showAtLocation(view, Gravity.TOP + Gravity.START, mWindowWidth / 2 - popWidth / 2, location[1] + UIUtil.dip2px(10));
                }else{
                    pop.showAtLocation(view, Gravity.TOP + Gravity.START, mWindowWidth / 2 - popWidth / 2, location[1] -((popHeight-view.getMeasuredHeight())/2));
                }
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

            @Override
            public void onItemClickListener(View view, MyBookDetailVO myBookDetailVO, int position) {
                //进入图书详情页
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                    return;
                }
                Intent intent = new Intent(MyBooksAct.this, BookDetailAct.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("book",myBookDetailVO);
                intent.putExtra(BundleFlag.SHOWADDRESS, false);
                intent.putExtras(bundle);
                MyBooksAct.this.startActivity(intent);
            }
        });



      /*  mMyRecyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
    }

    private Dialog mPromptDialog;
//    private int tempPosition;//临时记录点击条目跳转到分享页面时的position

    /**
     * 初始化并设置popupWin中的控件
     *
     * @param popView      popWindow View
     * @param bookDetailVO book detail bean
     */
    private void setPopwinViewControls(final View popView, final MyBookDetailVO bookDetailVO, final int position) {

        final TextView item1 = (TextView) popView.findViewById(R.id.tv_item1);//删除
        final TextView item2 = (TextView) popView.findViewById(R.id.tv_item2);//分享
        final TextView item3 = (TextView) popView.findViewById(R.id.tv_item3);//交换
        switch (bookDetailVO.getBookStatusId()) {
            case Const.BOOK_STATUS_ONSHELF://在书架上
                item1.setText("删除此书");
                item2.setText("分享");
                item3.setText("交换");
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(MyBooksAct.this, "确定要删除《" + bookDetailVO.getBook().getTitle() + "》这本书么?", "确定", "保留",
                                new PopItemClickListener(bookDetailVO, position, PopItemClickListener.DELETE), null);
                        mPromptDialog.show();
                    }
                });
                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        Bundle ex = new Bundle();
                        ex.putParcelable("book",bookDetailVO.getBook());
                        ex.putString("userBookId",bookDetailVO.getUserBookId());
                        ex.putInt("bookIndex",position);
                        gotoActivityForResult(ShareBookAct.class,777,ex);
                    }
                });
                item3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        showExchangeDialog(bookDetailVO.getBook().getId(),bookDetailVO.getUserBookId(),position);
                    }
                });
                break;
            case Const.BOOK_STATUS_EXCHANGING:
                item1.setText("删除此书");
                item2.setText("取消交换");
                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(MyBooksAct.this, "确定要取消交换《" + bookDetailVO.getBook().getTitle() + "》这本书么?", "确定", "取消",
                                new PopItemClickListener(bookDetailVO, position, PopItemClickListener.CANCEL_SWAP_BOOK), null);
                        mPromptDialog.show();
                    }
                });
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(MyBooksAct.this, "确定删除《" + bookDetailVO.getBook().getTitle() + "》这本书么?", "确定", "取消",
                                new PopItemClickListener(bookDetailVO, position, PopItemClickListener.DELETE), null);
                        mPromptDialog.show();
                    }
                });
                item3.setVisibility(View.GONE);
                break;
            case Const.BOOK_STATUS_SHARED://分享中。。。
                item1.setText("删除此书");
                item2.setText("取消分享");
                item3.setVisibility(View.GONE);

                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(MyBooksAct.this, "确定删除《" + bookDetailVO.getBook().getTitle() + "》这本书么?", "确定", "取消",
                                new PopItemClickListener(bookDetailVO, position, PopItemClickListener.DELETE), null);
                        mPromptDialog.show();

                    }
                });
                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(MyBooksAct.this, "确定取消分享《" + bookDetailVO.getBook().getTitle() + "》这本书么?", "确定", "取消",
                                new PopItemClickListener(bookDetailVO, position, PopItemClickListener.CANCEL_SHARE), null);
                        mPromptDialog.show();

                    }
                });
                break;
            case Const.BOOK_STATUS_BORROWED://已借出

                break;
        }
    }

    private void showExchangeDialog(final String bookId, final String userBookId,final int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) MyBooksAct.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_exchange, null);
        final AppCompatEditText etBookTitle = (AppCompatEditText) view.findViewById(R.id.acet_title);
        final AppCompatEditText etBookAuthor = (AppCompatEditText) view.findViewById(R.id.acet_author);
        final AppCompatEditText etMsg = (AppCompatEditText) view.findViewById(R.id.acet_msg);
        Button btnOK = (Button) view.findViewById(R.id.dialog_btn);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn2);
        mDialogExchange = adb.create();
        mDialogExchange.setView(view, 0, 0, 0, 0);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtil.isEmpty(etBookTitle.getText().toString())){
                    etBookTitle.setError("请输入书名");
                    UIUtil.showToastSafe("请输入书名");
                    return;
                }
                String bookTitle = etBookTitle.getText().toString();
                String bookAuthor = etBookAuthor.getText().toString();
                String msg = etMsg.getText().toString();
                showDefProgress();
                getUserPresenter().swapBook(userBookId,bookId,bookTitle,bookAuthor,msg,new CallBack(){

                    @Override
                    public void onSuccess(Object obj, int... code) {
                        mDialogExchange.dismiss();
                        dismissProgress();
                        String response = (String) obj;
                        DefindResponseJson drj = new DefindResponseJson(response);
                        if(DefindResponseJson.NO_DATA == drj.errorCode){
                            UIUtil.showToastSafe("访问出错");
                            return;
                        }

                        switch (drj.errorCode) {
                            case 2:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        UIUtil.showToastSafe("交换设置成功");
                                        mBooks.get(position).setBookStatusId(Const.BOOK_STATUS_EXCHANGING);
                                        myBookAdapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            case 3:
                                UIUtil.showToastSafe("状态修改失败");
                                break;
                            default:
                                UIUtil.showToastSafe("访问出错");
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
                        UIUtil.showToastSafe("访问出错");
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogExchange.dismiss();
            }
        });
        mDialogExchange.show();
    }

    @Override
    protected void onDestroy() {
        if(mDialogExchange!=null && mDialogExchange.isShowing()){
            mDialogExchange.dismiss();
        }
        if(mPromptDialog!=null && mPromptDialog.isShowing()){
            mPromptDialog.dismiss();
        }
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            return;
        }
        super.onDestroy();
    }

    private class PopItemClickListener implements View.OnClickListener {
        private int operId;
        private MyBookDetailVO bookDetailVO;
        private static final int DELETE = 0;//从书架删除（无论之前什么状态）
        private static final int CANCEL_SHARE = 1;//取消分享
        private static final int CANCEL_SWAP_BOOK = 2;//取消图书交换
        private int position;//在listView中的位置索引

        /**
         * @param bookDetailVO 操作的书籍
         * @param operId       操作id
         */
        PopItemClickListener(MyBookDetailVO bookDetailVO, int position, Integer operId) {
            this.operId = operId;
            this.bookDetailVO = bookDetailVO;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            CustomProgressDialog.dismissDialog(mPromptDialog);
            switch (operId) {
                case DELETE://从书架删除
                    showDefProgress();
                    getBookPresenter().deleteUserBook(GlobalParams.getLastLoginUser().getUid(),bookDetailVO.getUserBookId(), bookDetailVO.getBook().getId(), new CallBack() {

                        @Override
                        public void onSuccess(final Object obj, int... code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgress();
                                    if (pop != null && pop.isShowing()) {
                                        pop.dismiss();
                                    }
                                    String json = (String) obj;
                                    ResponseJson rj = new ResponseJson(json);
                                    if (rj.errorCode != null && rj.errorCode != ResponseJson.NO_DATA) {
                                        switch (rj.errorCode) {
                                            case 1://成功
                                                UIUtil.showToastSafe(rj.errorMsg);
                                                mBooks.remove(bookDetailVO);
                                                myBookAdapter.notifyDataSetChanged();
                                                break;
                                            default:
                                                UIUtil.showToastSafe(rj.errorMsg);
                                                break;
                                        }
                                    }else{
                                        UIUtil.showToastSafe("未能成功删除");
                                    }
                                }
                            });

                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgress();
                                    Snackbar.make(llRoot, "删除失败", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    break;
                case CANCEL_SHARE://取消分享
                    showDefProgress();
                    getBookPresenter().cancelShare(bookDetailVO.getUserBookId(),new CallBack(){
                        @Override
                        public void onSuccess(Object obj, int... code) {
                            dismissProgress();
                            if (pop != null && pop.isShowing()) {
                                pop.dismiss();
                            }
                            String json = (String) obj;
                            final ResponseJson rj = new ResponseJson(json);
                            if (rj.errorCode != null && rj.errorCode != ResponseJson.NO_DATA) {
                                switch (rj.errorCode) {
                                    case 1://成功
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                UIUtil.showToastSafe(rj.errorMsg);
                                                UserBooks ub = JSON.parseObject(JSONObject.parseObject((String)((JSONObject)rj.data.get(0)).get("userBook")).get("ub").toString(),UserBooks.class);
                                                mBooks.get(position).setUserBook(ub);
                                                myBookAdapter.notifyDataSetChanged();
                                            }
                                        });

                                        break;
                                    default:
                                        UIUtil.showToastSafe(rj.errorMsg);
                                        break;
                                }
                            }else{
                                UIUtil.showToastSafe("未能取消分享");
                            }
                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
                            if(obj instanceof String){
                                UIUtil.showToastSafe((String) obj);
                            }dismissProgress();
                        }
                    });
                    break;

                case CANCEL_SWAP_BOOK:
                    showDefProgress();
                    getUserPresenter().cancelSwapBook(bookDetailVO.getUserBookId(),bookDetailVO.getSwapId(),new CallBack(){
                        @Override
                        public void onSuccess(Object obj, int... code) {
                            dismissProgress();
                            String response = (String) obj;
                            DefindResponseJson drj = new DefindResponseJson(response);
                            if(DefindResponseJson.NO_DATA == drj.errorCode){
                                UIUtil.showToastSafe("访问出错");
                                return;
                            }

                            switch (drj.errorCode) {
                                case 2:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UIUtil.showToastSafe("已取消交换");
                                            mBooks.get(position).setBookStatusId(Const.BOOK_STATUS_ONSHELF);
                                            myBookAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    break;
                                case 3:
                                    UIUtil.showToastSafe("状态修改失败");
                                    break;
                                default:
                                    UIUtil.showToastSafe("访问出错");
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
                            dismissProgress();
                            UIUtil.showToastSafe("状态修改失败-访问出错");
                        }
                    });
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 777 && resultCode==888){
            //分享成功
            int bookIndex = data.getIntExtra("bookIndex",-1);
            if(bookIndex== -1 ){
                initData();
            }else{
                UserBooks ub = data.getParcelableExtra("userBook");
                mBooks.get(bookIndex).setBookStatusId(Const.BOOK_STATUS_SHARED);
                mBooks.get(bookIndex).setShareAreaId(ub.getShareAreaId());
                mBooks.get(bookIndex).setShareMsg(ub.getShareMsg());
                mBooks.get(bookIndex).setShareType(ub.getShareType());
                myBookAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void initData() {
        mBooks.clear();
        showDefProgress();
        getBooks(GlobalParams.getLastLoginUser().getUid(), 1);
    }

    /**
     * 获取我的所有书籍（不包含借入的书籍）
     *
     * @param uid     user's uuid
     * @param pageNum pageNo
     */
    private void getBooks(String uid, int pageNum) {
        getBookPresenter().getMyBooks(uid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj, int... code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        ArrayList<MyBookDetailVO> books = (ArrayList<MyBookDetailVO>) obj;
                        if (books == null || books.isEmpty()) {
                            UIUtil.showToastSafe("没有更多书籍了!");
                            myBookAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
                            if (isLoadingMore) {
                                mPageNum--;
                                if (mPageNum < 0) mPageNum = 0;
                                isLoadingMore = false;
                            }
                        } else {
                            if(isLoadingMore){
                                mBooks.addAll(books);
                            }else{
                                mBooks.clear();
                                mBooks.addAll(books);
                            }
                            myBookAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj, int... code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myBookAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mMyRecyclerView.clearAnimation();
                        if (isLoadingMore) {
                            mPageNum--;
                            if (mPageNum < 0) mPageNum = 0;
                            isLoadingMore = false;
                        }
                        UIUtil.showToastSafe("未能获取书籍信息!");
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pop != null) {
            pop.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(pop!=null && pop.isShowing()){
            pop.dismiss();
            return;
        }
        super.onBackPressed();
    }
}
