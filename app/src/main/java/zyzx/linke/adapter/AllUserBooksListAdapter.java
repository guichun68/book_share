package zyzx.linke.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import zyzx.linke.activity.BookDetailAct;
import zyzx.linke.activity.DetailActivity;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.model.bean.IndexItem;
import zyzx.linke.model.bean.ResponseBooks;
import zyzx.linke.utils.AMApCloudImageCache;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.Utils;

/**
 * 所有用户的书籍的listView的adapter（其集合个数>=地图中的坐标点数--因为一个用户(坐标点)可能分享了多本书籍）
 *
 * @author ligen
 */
public class AllUserBooksListAdapter extends BaseAdapter {
    private Context mContext;
    private List<IndexItem> mItemList;
    private int mIndex = -1;
    private ImageLoader imageLoader = null;

    public AllUserBooksListAdapter(Context context, List<IndexItem> list) {
        this.mContext = context;
        this.mItemList = list;
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        imageLoader = new ImageLoader(mQueue, new AMApCloudImageCache());
    }

    public void setData(List<IndexItem> list) {
        this.mItemList = list;
    }

    ;

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public IndexItem getItem(int arg0) {
        return mItemList.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectUpdate(int sel) {
        if (mIndex != sel) {
            mIndex = sel;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_cloud_item_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext.getApplicationContext(),
                        BookDetailAct.class);
//                intent.putExtra(BundleFlag.CLOUD_ITEM, getItem(position).getBookDetail());
                intent.putExtra(BundleFlag.BOOK, getItem(position).getBookDetail());
//                intent.putExtra(BundleFlag.isFromIndexAct,true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        if (getItem(position) != null && !StringUtil.isEmpty(getItem(position).getBookDetail().getImage_medium())) {
            String imageUrl = getItem(position).getBookDetail().getImage_medium();
            if(StringUtil.isEmpty(imageUrl)){
                imageUrl = getItem(position).getBookDetail().getImages().getMedium();
            }
            holder.imgCliam.setImageUrl(imageUrl, imageLoader);
            holder.imageCorner.setVisibility(View.GONE);
        } else {
            holder.imgCliam.setImageUrl(null, null);
            holder.imageCorner.setVisibility(View.GONE);
        }

        holder.title.setText(getItem(position).getmTitle());
        holder.address.setText(getItem(position).getAddress());
        float dis = getItem(position).getDistance();
        dis = Math.round(dis * 10) / 10;
        if (dis > 0) {
            holder.dis.setText(Utils.getDisDsrc(dis));
        } else {
            holder.dis.setText("");
        }

        return convertView;
    }

    private class ViewHolder {
        private final View root;
        private final TextView title;
        private final TextView address;
        private final TextView dis;
        private final NetworkImageView imgCliam;
        private final ImageView imageCorner;
        private final RelativeLayout rlayout_map;
        ViewHolder(View root){
            this.root = root;
            this.title = (TextView) root.findViewById(R.id.tv_title);
            this.address = (TextView) root.findViewById(R.id.tv_address);
            this.dis = (TextView) root.findViewById(R.id.tv_dis);
            this.imgCliam = (NetworkImageView) root.findViewById(R.id.img_temp_cliam);
            this.imageCorner = (ImageView) root.findViewById(R.id.image_corner);
            this.rlayout_map = (RelativeLayout) root.findViewById(R.id.llayout_map);
        }
    }
}
