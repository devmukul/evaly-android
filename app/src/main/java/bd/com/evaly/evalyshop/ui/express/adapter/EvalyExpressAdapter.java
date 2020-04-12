package bd.com.evaly.evalyshop.ui.express.adapter;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.util.Utils;

public class EvalyExpressAdapter extends ListAdapter<GroupShopModel, EvalyExpressAdapter.MyViewHolder> {

    private Context context;
    private NavController navController;

    private static final DiffUtil.ItemCallback<GroupShopModel> diffCallback = new DiffUtil.ItemCallback<GroupShopModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull GroupShopModel oldItem, @NonNull GroupShopModel newItem) {
            return oldItem.getShopSlug().equals(newItem.getShopSlug());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GroupShopModel oldItem, @NonNull GroupShopModel newItem) {
            return oldItem.getShopSlug().equals(newItem.getShopSlug());
        }
    };

    public EvalyExpressAdapter(Context context, NavController navController) {
        super(diffCallback);
        this.context = context;
        this.navController = navController;
    }

    @Override
    public void submitList(final List<GroupShopModel> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    @Override
    public EvalyExpressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_shop, parent, false);

        return new EvalyExpressAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EvalyExpressAdapter.MyViewHolder holder, int position) {

        GroupShopModel model = getItem(position);

        holder.tv.setText(Utils.titleBeautify(model.getShopName()));

       // holder.view.setTag(position);

        Glide.with(context)
                .load(model.getLogoImage())
                .apply(new RequestOptions().override(240, 240))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.iv);
    }


    public void clear(){

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
                        GroupShopModel model = getItem(getAdapterPosition());
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_name", model.getShopName());
                        bundle.putString("logo_image", model.getLogoImage());
                        bundle.putString("shop_slug", model.getShopSlug());
                        navController.navigate(R.id.shopFragment, bundle);
            });

        }
    }


}
