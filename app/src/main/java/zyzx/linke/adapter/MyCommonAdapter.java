package zyzx.linke.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import zyzx.linke.R;
import zyzx.linke.activity.SkillSwapPage;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.utils.StringUtil;

public abstract class MyCommonAdapter<T> extends RecyclerView.Adapter<MyViewHolder>{
    //上拉加载更多
    public static final int STATUS_PULLUP_LOAD_MORE =0;
    //正在加载中
    public static final int STATUS_LOADING_MORE =1;
    //没有加载的时候
    public static final int STATUS_LOADING_END = 2;
    //没有更多内容了
    public static final int STATUS_NO_MORE_DATE = 3;

    //上拉加载更多状态-默认为0
    public int load_more_status=0;

    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView

    protected Context mContext;
    private List<T> mDatas;
    private LayoutInflater mInflater;
    private Integer itemLayoutId;
    private Integer footerLayoutId;

    public MyCommonAdapter(Context context,List<T> datas,int itemLayoutResId){
        this.mDatas = datas;
        this.mContext = context;
        mInflater = ((Activity)context).getLayoutInflater();
        this.itemLayoutId = itemLayoutResId;
    }
    public MyCommonAdapter(Context context,List<T> datas,int itemLayoutResId,int footerLayoutId){
        this(context,datas,itemLayoutResId);
        this.footerLayoutId = footerLayoutId;
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
            setListener(parent, itemViewHolder, viewType);
            itemViewHolder.setHolderType(MyViewHolder.HOLDER_TYPE_NORMAL);
            return itemViewHolder;

        }else if(viewType==TYPE_FOOTER){
            MyViewHolder footViewHolder = MyViewHolder.createViewHolder(mContext,parent,footerLayoutId);
            footViewHolder.setHolderType(MyViewHolder.HOLDER_TYPE_FOOTER);
            return footViewHolder;
        }
        return null;
    }

    public void onViewHolderCreated(MyViewHolder holder,View itemView){

    }

    public abstract void convert(MyViewHolder holder, T t,int position);

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(holder.getHolderType() == MyViewHolder.HOLDER_TYPE_NORMAL){
            convert(holder, mDatas.get(position),position);
        }else if(holder.getHolderType() == MyViewHolder.HOLDER_TYPE_FOOTER){
            convert(holder, null,position);
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
    public void changeMoreStatus(int status){
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
    protected void setListener(final ViewGroup parent, final MyViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder , position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder,  int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder,  int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
