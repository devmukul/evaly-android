package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;
import bd.com.evaly.evalyshop.models.WishList;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.MyViewHolder>{

    ArrayList<WishList> wishlist;
    Context context;
    DbHelperWishList db;

    WishListAdapter instance;

    public WishListAdapter(ArrayList<WishList> wishlist, Context context) {
        this.wishlist = wishlist;
        this.context = context;
        db=new DbHelperWishList(context);

        instance = this;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_wishlist,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Log.d("wish_product",wishlist.get(i).getName());
        myViewHolder.productName.setText(Html.fromHtml(wishlist.get(i).getName()));
        myViewHolder.time.setText(Utils.getTimeAgo(wishlist.get(i).getTime()));
        myViewHolder.price.setText("৳ "+wishlist.get(i).getPrice());
        Glide.with(context)
                .load(wishlist.get(i).getImage())
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  return false;
                              }
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Bitmap bitmap = Utils.changeColor(((BitmapDrawable)resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                  myViewHolder.productImage.setImageBitmap(bitmap);
                                  return true;
                              }
                          }
                )
                .into(myViewHolder.productImage);
        myViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewProductActivity.class);
                intent.putExtra("product_slug",wishlist.get(i).getProductSlug());
                intent.putExtra("product_name",wishlist.get(i).getName());
                context.startActivity(intent);
            }
        });
        myViewHolder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteData(wishlist.get(i).getId());

                wishlist.remove(i);
                instance.notifyItemRemoved(i);
                notifyItemRangeChanged(i, wishlist.size());

            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView productName,time,price;
        ImageView productImage,trash;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.product_name);
            time=itemView.findViewById(R.id.time);
            price=itemView.findViewById(R.id.price);
            productImage= itemView.findViewById(R.id.product_image);
            trash= itemView.findViewById(R.id.trash);
            view = itemView;
        }
    }

}
