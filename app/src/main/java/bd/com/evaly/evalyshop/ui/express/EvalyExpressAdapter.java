package bd.com.evaly.evalyshop.ui.express;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.util.Utils;

public class EvalyExpressAdapter extends RecyclerView.Adapter<EvalyExpressAdapter.MyViewHolder> {

    private Context context;
    private List<GroupShopModel> itemList;
    private NavController navController;

    public EvalyExpressAdapter(Context ctx, List<GroupShopModel> itemList, NavController navController) {
        context = ctx;
        // this.itemList = itemList;
        this.navController = navController;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public EvalyExpressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_shop, parent, false);

        return new EvalyExpressAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EvalyExpressAdapter.MyViewHolder holder, int position) {

        GroupShopModel model = itemList.get(position);

        holder.tv.setText(Utils.titleBeautify(model.getShopName()));

       // holder.view.setTag(position);

        Glide.with(context)
                .load(model.getLogoImage())
                .apply(new RequestOptions().override(240, 240))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.iv);
    }


    public void addData(List<GroupShopModel> newList) {

        if (itemList == null) {
            itemList = newList;
            notifyItemRangeInserted(0, newList.size());
        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return itemList.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(itemList.get(oldItemPosition).getShopSlug(), newList.get(newItemPosition).getShopSlug());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return false;
//                    return Objects.equals(itemList.get(oldItemPosition).getShopSlug(), newList.get(newItemPosition).getShopSlug());
                }
            });


            itemList = newList;
            result.dispatchUpdatesTo(this);
        }

    }


    public void clear(){
        if (itemList != null) {
            itemList.clear();
            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text);
            iv = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(v -> {
                        GroupShopModel model = itemList.get(getLayoutPosition());
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_name", model.getShopName());
                        bundle.putString("logo_image", model.getLogoImage());
                        bundle.putString("shop_slug", model.getShopSlug());
                        navController.navigate(R.id.shopFragment, bundle);
            });

        }
    }


}
