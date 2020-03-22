package bd.com.evaly.evalyshop.ui.express;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shop.shopGroup.ShopsItem;
import bd.com.evaly.evalyshop.util.Utils;

public class EvalyExpressAdapter extends RecyclerView.Adapter<EvalyExpressAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ShopsItem> itemlist;
    private NavController navController;
    View.OnClickListener productListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();

            ShopsItem model = itemlist.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("shop_name", model.getName());
            bundle.putString("logo_image", model.getLogoImage());
            bundle.putString("shop_slug", model.getSlug());
            navController.navigate(R.id.shopFragment, bundle);
        }
    };


    public EvalyExpressAdapter(Context ctx, ArrayList<ShopsItem> item, NavController navController) {
        context = ctx;
        itemlist = item;
        this.navController = navController;
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    @Override
    public EvalyExpressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_shop, parent, false);

        return new EvalyExpressAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EvalyExpressAdapter.MyViewHolder holder, int position) {

        ShopsItem model = itemlist.get(position);

        holder.tv.setText(Utils.titleBeautify(model.getName()));

        holder.view.setOnClickListener(productListener);
        holder.view.setTag(position);

        Glide.with(context)
                .load(model.getLogoImage())
                .apply(new RequestOptions().override(240, 240))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.iv);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text);
            iv = itemView.findViewById(R.id.image);
            view = itemView;

        }
    }

}
