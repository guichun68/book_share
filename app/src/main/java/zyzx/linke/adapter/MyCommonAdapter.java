package zyzx.linke.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import zyzx.linke.R;

public abstract class MyCommonAdapter<T> extends RecyclerView.Adapter<MyViewHolder>{
    //上拉加载更多
    private static Status STATUS_PULLUP_LOAD_MORE = Status.STATUS_PULLUP_LOAD_MORE;
    //正在加载中
    private static Status STATUS_LOADING_MORE =Status.STATUS_LOADING_MORE;
    //没有加载的时候
    private static Status STATUS_LOADING_END = Status.STATUS_LOADING_END;
    //没有更多内容了
    private static Status STATUS_NO_MORE_DATE = Status.STATUS_NO_MORE_DATE;

    //上拉加载更多状态-默认为0
    public Status load_more_status = Status.STATUS_PULLUP_LOAD_MORE;
    Status d;

    public enum Status{
        STATUS_PULLUP_LOAD_MORE,
        STATUS_LOADING_MORE,
        STATUS_LOADING_END,//隐藏footer
        STATUS_NO_MORE_DATE;
    }

    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView

    protected Context mContext;
    private List<T> mDatas;
    private LayoutInflater mInflater;
    private Integer itemLayoutId;
    private Integer footerLayoutId;
    private View footerView;
    private int footerProgressResId,footerTextTipResId;
    private ProgressBar footerProgressBar;
    private TextView footerTextTip;

    public MyCommonAdapter(Context context,List<T> datas,int itemLayoutResId){
        this.mDatas = datas;
        this.mContext = context;
        mInflater = ((Activity)context).getLayoutInflater();
        this.itemLayoutId = itemLayoutResId;
    }
    public MyCommonAdapter(Context context,List<T> datas,int itemLayoutResId,int footerLayoutId,int footerProgressResId,int footerTextTipResId){
        this(context,datas,itemLayoutResId);
        this.footerLayoutId = footerLayoutId;
        this.footerProgressResId = footerProgressResId;
        this.footerTextTipResId = footerTextTipResId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据条目类型不同来创建并返回不同的ViewHolder
        if(viewType==TYPE_ITEM){
//            View view=mInflater.inflate(itemLayoutId,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            MyViewHolder itemViewHolder = MyViewHolder.createViewHolder(mContext, parent, itemLayoutId);
            onViewHolderCreated(itemViewHolder,itemViewHolder.getConvertView());

            itemViewHolder.setHolderType(MyViewHolder.HOLDER_TYPE_NORMAL);
            return itemViewHolder;

        }else if(viewType==TYPE_FOOTER){
            footerView = LayoutInflater.from(mContext).inflate(footerLayoutId, parent, false);
            footerProgressBar = (ProgressBar) footerView.findViewById(footerProgressResId);
            footerTextTip = (TextView) footerView.findViewById(footerTextTipResId);
            MyViewHolder footViewHolder = MyViewHolder.createViewHolder(mContext,footerView);
            footViewHolder.setHolderType(MyViewHolder.HOLDER_TYPE_FOOTER);
            return footViewHolder;
        }
        return null;
    }

    public void onViewHolderCreated(MyViewHolder holder,View itemView){

    }

    public void convert(MyViewHolder holder, T t,int position){
        setListener(holder, t,position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(holder.getHolderType() == MyViewHolder.HOLDER_TYPE_NORMAL){
            convert(holder, mDatas.get(position),position);

        }else if(holder.getHolderType() == MyViewHolder.HOLDER_TYPE_FOOTER){
                switch (load_more_status){
                    case STATUS_PULLUP_LOAD_MORE:
                        footerView.setVisibility(View.VISIBLE);
                        footerView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                        footerProgressBar.setVisibility(View.VISIBLE);
                        footerTextTip.setText("加载更多内容");
                        break;
                    case STATUS_LOADING_MORE:
                        footerView.setVisibility(View.VISIBLE);
                        footerProgressBar.setVisibility(View.VISIBLE);
                        footerTextTip.setText("正在加载...");
                        break;
                    case STATUS_LOADING_END:
                        footerView.setVisibility(View.GONE);
                        footerView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.transparent));
                        break;
                    case STATUS_NO_MORE_DATE:
                        footerView.setVisibility(View.VISIBLE);
                        footerProgressBar.setVisibility(View.GONE);
                        footerView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.white));
                        footerTextTip.setText("无更多内容");
                        break;
                }
//            convert(holder, null,position);
        }
    }

    /**
     * //上拉加载更多
     * STATUS_PULLUP_LOAD_MORE=0;
     * //正在加载中
     * STATUS_LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void setFooterStatus(Status status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(footerLayoutId != null){
            return mDatas.size()+1;
        }else{
            return mDatas.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(footerLayoutId != null){
            // 最后一个item设置为footerView
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }else{
            return TYPE_ITEM;
        }
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }
    protected OnItemClickListener mOnItemClickListener;
    protected void setListener(final MyViewHolder viewHolder,final T t,int position) {
        if (!isEnabled(viewHolder.getItemViewType())) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder ,t,position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, t,position);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, RecyclerView.ViewHolder holder,T t,  int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, T t ,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}

