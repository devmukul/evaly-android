package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
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
import bd.com.evaly.evalyshop.fragment.ShopFragment;
import bd.com.evaly.evalyshop.models.TabsItem;

public class ShopCategoryAdapter extends RecyclerView.Adapter<ShopCategoryAdapter.MyViewHolder>{

    Context context;
    ArrayList<TabsItem> itemlist;
    int type;
    ShopFragment shopFragment;

    public ShopCategoryAdapter(Context ctx, ArrayList<TabsItem> item, ShopFragment shopFragment){
        context=ctx;
        itemlist = item;
        this.shopFragment = shopFragment;
    }


    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_category_item,parent,false);
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
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Bitmap bitmap = changeColor(((BitmapDrawable)resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                  holder.iv.setImageBitmap(bitmap);
                                  return true;
                              }
                          }
                )
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.iv);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    shopFragment.showProductsByCategory(itemlist.get(position).getTitle(), itemlist.get(position).getSlug(),position);


            }
        });


    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv;
        ImageView iv;
        View view;


        public MyViewHolder(final View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.text);
            iv= itemView.findViewById(R.id.image);
            view = itemView;

        }
    }






    private double colorDistance(int a, int b) {
        return Math.sqrt(Math.pow(Color.red(a) - Color.red(b), 2) + Math.pow(Color.blue(a) - Color.blue(b), 2) + Math.pow(Color.green(a) - Color.green(b), 2));
    }


    public Bitmap changeColor(Bitmap src,int fromColor, int targetColor) {
        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {

            if(colorDistance(pixels[x], fromColor) < 10)
                pixels[x] = targetColor;

        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }


}
