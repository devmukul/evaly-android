package bd.com.evaly.evalyshop.ui.browseProduct.tabs.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.MyViewHolder> {

    Context context;
    ArrayList<TabsItem> itemlist;
    int type;

    AppCompatActivity activity;

    private NavController navController;
    View.OnClickListener productListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            TabsItem model = itemlist.get(position);

            if (context instanceof MainActivity) {

                if (type == 1) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("type", model.getType());
                    bundle.putString("slug", model.getSlug());
                    bundle.putString("category", model.getCategory());

                    navController.navigate(R.id.browseProductFragment, bundle);

                } else if (type == 2) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("type", model.getType());
                    bundle.putString("brand_slug", model.getSlug());
                    bundle.putString("brand_name", model.getTitle());
                    bundle.putString("category", model.getCategory());
                    bundle.putString("image_url", model.getImage());

                    navController.navigate(R.id.brandFragment, bundle);


                } else if (type == 3 || type == 6) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("type", model.getType());
                    bundle.putString("shop_name", model.getTitle());
                    bundle.putString("logo_image", model.getImage());
                    bundle.putString("shop_slug", model.getSlug());
                    bundle.putString("category", model.getCategory());
                    bundle.putString("campaign", model.getCampaignSlug());

                    navController.navigate(R.id.shopFragment, bundle);

                }
            } else {


                if (type == 2) {

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("brand_slug", model.getSlug());
                    intent.putExtra("brand_name", model.getTitle());
                    intent.putExtra("category", model.getCategory());
                    intent.putExtra("image_url", model.getImage());

                    context.startActivity(intent);

                } else if (type == 3 || type == 6) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("type", 3);
                    intent.putExtra("shop_name", model.getTitle());
                    intent.putExtra("logo_image", model.getImage());
                    intent.putExtra("shop_slug", model.getSlug());
                    intent.putExtra("category", model.getCategory());
                    intent.putExtra("campaign_slug", model.getCampaignSlug());
                    context.startActivity(intent);

                }

            }
        }
    };

    public TabsAdapter(Context ctx, AppCompatActivity activity, ArrayList<TabsItem> item, int type) {
        context = ctx;
        itemlist = item;
        this.type = type;
        this.activity = activity;
        if (activity instanceof MainActivity)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    public TabsAdapter(Context ctx, AppCompatActivity activity, ArrayList<TabsItem> item, int type, NavController navController) {
        context = ctx;
        itemlist = item;
        this.type = type;
        this.activity = activity;
        this.navController = navController;
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_brands, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TabsItem model = itemlist.get(position);

        holder.tv.setText(Utils.titleBeautify(model.getTitle()));

        holder.view.setOnClickListener(productListener);
        holder.view.setTag(position);

        Glide.with(context)
                .load(model.getImage())
                .apply(new RequestOptions().override(240, 240))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_placeholder_small)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Bitmap bitmap = Utils.changeColor(((BitmapDrawable) resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                  holder.iv.setImageBitmap(bitmap);
                                  return true;
                              }
                          }
                )
                .into(holder.iv);

    }

    public void setFilter(ArrayList<TabsItem> ar) {
        itemlist = new ArrayList<>();
        itemlist.addAll(ar);
        notifyDataSetChanged();
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
