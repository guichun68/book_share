/*
package zyzx.linke.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudItem;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

import zyzx.linke.R;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.utils.AMApCloudImageCache;

*/
/**
 * 云图poi列表页Adapter
 *
 * @author ligen
 *//*

public class CloudItemListAdapter2  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_HEADER = 3;

    private Context mContext;
    private List<CloudItem> mItemList;
    private int mIndex = -1;
    private ImageLoader imageLoader = null;

    public CloudItemListAdapter2(Context context, List<CloudItem> list) {
        this.mContext = context;
        this.mItemList = list;
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        imageLoader = new ImageLoader(mQueue, new AMApCloudImageCache());
    }

    public void setData(List<CloudItem> list) {
        this.mItemList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            CardView view = new CardView(mContext);
//            RecyclerView.LayoutParams param = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.dip2px((int)context.getResources().getDimension(R.dimen.tabLayout_size)));
            RecyclerView.LayoutParams param = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(param);
            HeadViewHolder holder = new HeadViewHolder(view);
            return holder;
        }
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_cloud_item_list, parent,false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return  mItemList.size();
    }


    public void setSelectUpdate(int sel) {
        if (mIndex != sel) {
            mIndex = sel;
            notifyDataSetChanged();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView title;
        private final TextView address;
        private final TextView dis;
        private final NetworkImageView imgCliam;
        private final ImageView imageCorner;
        private final RelativeLayout rlayout_map;
        public ItemViewHolder(View root){
            super(root);
            this.root = root;
            this.title = (TextView) root.findViewById(R.id.tv_title);
            this.address = (TextView) root.findViewById(R.id.tv_address);
            this.dis = (TextView) root.findViewById(R.id.tv_dis);
            this.imgCliam = (NetworkImageView) root.findViewById(R.id.img_temp_cliam);
            this.imageCorner = (ImageView) root.findViewById(R.id.image_corner);
            this.rlayout_map = (RelativeLayout) root.findViewById(R.id.llayout_map);
        }
    }

    static class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View view) {
            super(view);
        }
    }
    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}
*/
