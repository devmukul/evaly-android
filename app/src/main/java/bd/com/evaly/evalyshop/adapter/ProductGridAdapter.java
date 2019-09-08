package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.models.ProductListItem;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<ProductListItem> productsList;
    DbHelperWishList db;

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



    View.OnClickListener itemViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            Intent intent=new Intent(mContext, ViewProductActivity.class);

            intent.putExtra("product_slug",productsList.get(position).getSlug());
            intent.putExtra("product_name",productsList.get(position).getName());
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


//        holder.imageViewAndroid.post(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        });

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

        }else{
            holder.price.setText("৳ " + productsList.get(position).getPriceMin());
        }
        //holder.sku.setText(productsList.get(position).getSlug());



        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(itemViewListener);



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

        TextView textViewAndroid,price,sku;
        ImageView imageViewAndroid,favorite;
        View itemView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            textViewAndroid=itemView.findViewById(R.id.title);
            price=itemView.findViewById(R.id.price);
            imageViewAndroid=itemView.findViewById(R.id.image);
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

