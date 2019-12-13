package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.models.ProductListItem;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<ProductListItem> productsList;
    private DbHelperWishList db;
    private String shopSlug = "";
    private int cashback_rate  = 0;

    private ProductListener productListener;

    public void setproductListener(ProductListener productListener){
        this.productListener = productListener;
    }
    
    public void setShopSlug(String shopSlug){
        this.shopSlug = shopSlug;
    }

    public ProductGridAdapter(Context context, ArrayList<ProductListItem> a) {
        mContext = context;
        productsList=a;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_product_grid_item, null);
        db=new DbHelperWishList(mContext);
        return new MyViewHolder(view);
    }


    public int getCashback_rate() {
        return cashback_rate;
    }

    public void setCashback_rate(int cashback_rate) {
        this.cashback_rate = cashback_rate;
    }

    View.OnClickListener itemViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            Intent intent=new Intent(mContext, ViewProductActivity.class);
            intent.putExtra("product_slug",productsList.get(position).getSlug());
            intent.putExtra("product_name",productsList.get(position).getName());
            intent.putExtra("product_price",productsList.get(position).getPriceMax());
            intent.putExtra("product_image", productsList.get(position).getThumbnailSM());

            mContext.startActivity(intent);

        }
    };


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(productsList.get(position).getName().contains("-")){
            String str = productsList.get(position).getName();

            str = str.trim();


            String regex = "-[a-zA-Z0-9]+$";

            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(str);

            if(matcher.find()){
                String output = str.replaceAll(regex, "");
                holder.textViewAndroid.setText(Html.fromHtml(output));
            }
            else

            holder.textViewAndroid.setText(Html.fromHtml(productsList.get(position).getName()));


        }else {
            holder.textViewAndroid.setText(Html.fromHtml(productsList.get(position).getName()));

        }


        Glide.with(mContext)
                .asBitmap()
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(260, 260))
                .load(productsList.get(position).getThumbnailSM())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_placeholder_small)
                .into(holder.imageViewAndroid);


        if((productsList.get(position).getPriceMin()==0) || (productsList.get(position).getPriceMax()==0)){

            holder.price.setText("Call For Price");

        } else if(productsList.get(position).getDiscountedPrice() != 0){

            if (productsList.get(position).getDiscountedPrice() < productsList.get(position).getPriceMin()){

                holder.priceDiscount.setText("৳ " +productsList.get(position).getPriceMin());
                holder.priceDiscount.setVisibility(View.VISIBLE);
                holder.priceDiscount.setPaintFlags(holder.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                holder.price.setText("৳ " +productsList.get(position).getDiscountedPrice());

            } else {
                holder.priceDiscount.setVisibility(View.GONE);
                holder.price.setText("৳ " +productsList.get(position).getPriceMin());
            }
        } else {
            holder.price.setText("৳ " + productsList.get(position).getPriceMin());
        }


        //holder.sku.setText(productsList.get(position).getSlug());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(itemViewListener);

        if (cashback_rate==0)
            holder.tvCashback.setVisibility(View.GONE);
        else {
            holder.tvCashback.setVisibility(View.VISIBLE);
            holder.tvCashback.bringToFront();
            holder.tvCashback.setText(cashback_rate+"% Cashback");
        }


        if (!shopSlug.equals("") && productListener != null) {
            holder.buyNow.setTag(position);
            holder.buyNow.setVisibility(View.VISIBLE);
            holder.buyNow.setOnClickListener(v -> productListener.buyNow(productsList.get(position).getSlug()));
        }
        else
            holder.buyNow.setVisibility(View.GONE);


        if ((productsList.get(position).getPriceMin()==0) || (productsList.get(position).getPriceMax()==0)){
            holder.buyNow.setVisibility(View.GONE);
        }





        //holder.favorite.setTag("no");



//
//        if(db.isSlugExist(productsList.get(position).getSlug())) {
//            holder.favorite.setImageResource(R.drawable.ic_favorite_color);
//            holder.favorite.setTag("yes");
//        }


//        holder.favorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Calendar calendar = Calendar.getInstance();
//
//                if(holder.favorite.getTag().equals("yes")){
//                    holder.favorite.setImageResource(R.drawable.ic_favorite);
//
//                    Toast.makeText(mContext, "Removed from wish list", Toast.LENGTH_SHORT).show();
//                    holder.favorite.setTag("no");
//                    db.deleteDataBySlug(productsList.get(position).getSlug());
//
//                } else {
//                    holder.favorite.setTag("yes");
//
//                    if (db.insertData(productsList.get(position).getSlug(), productsList.get(position).getName(), productsList.get(position).getThumbnailSM(), productsList.get(position).getPriceMin(), calendar.getTimeInMillis())) {
//                        Toast.makeText(mContext, "Added to wish list", Toast.LENGTH_SHORT).show();
//                        holder.favorite.setImageResource(R.drawable.ic_favorite_color);
//                    }
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewAndroid,price,priceDiscount, sku, buyNow, tvCashback;
        ImageView imageViewAndroid,favorite;
        View itemView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            textViewAndroid=itemView.findViewById(R.id.title);
            price=itemView.findViewById(R.id.price);
            imageViewAndroid=itemView.findViewById(R.id.image);
            priceDiscount = itemView.findViewById(R.id.priceDiscount);
            buyNow = itemView.findViewById(R.id.buy_now);
            tvCashback = itemView.findViewById(R.id.tvCashback);

            // favorite=itemView.findViewById(R.id.favorite);
            //sku=itemView.findViewById(R.id.sku);



            this.itemView = itemView;
        }
    }

    public void addItem(ProductListItem pr){
        productsList.add(pr);
        notifyDataSetChanged();
    }

    public void setFilter(ArrayList<ProductListItem> ar){
        productsList=new ArrayList<>();
        productsList.addAll(ar);
        notifyDataSetChanged();
    }
}

