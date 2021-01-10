package bd.com.evaly.evalyshop.ui.brand.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.util.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BrandCategoryAdapter extends RecyclerView.Adapter<BrandCategoryAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TabsItem> itemlist;
    private int type;
    private ShopViewModel shopViewModel;

    public BrandCategoryAdapter(Context ctx, ArrayList<TabsItem> item, ShopViewModel shopViewModel) {
        context = ctx;
        itemlist = item;
        this.shopViewModel = shopViewModel;
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(itemlist.get(position).getTitle());

        Glide.with(context)
                .load(itemlist.get(position).getImage())
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  return false;
                              }

                              @SuppressLint("CheckResult")
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Observable.fromCallable(() -> Utils.changeColor(((BitmapDrawable) resource).getBitmap(),
                                          Color.parseColor("#ecf3f9"), Color.WHITE))
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(holder.iv::setImageBitmap, throwable -> Log.e("error", "Throwable " + throwable.getMessage()));
                                  return true;
                              }
                          }
                )
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.iv);

        holder.view.setOnClickListener(view -> shopViewModel.setSelectedCategoryLiveData(itemlist.get(position)));
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
