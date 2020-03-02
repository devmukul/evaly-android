package bd.com.evaly.evalyshop.ui.shop.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.HomeHeaderItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class ShopProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;
    private List<ProductItem> productsList;
    private HashMap<String, String> data;

    View.OnClickListener itemViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            Intent intent = new Intent(context, ViewProductActivity.class);
            intent.putExtra("product_slug", productsList.get(position).getSlug());
            intent.putExtra("product_name", productsList.get(position).getName());
            intent.putExtra("product_price", productsList.get(position).getMaxPrice());
            if (productsList.get(position).getImageUrls().size() > 0)
                intent.putExtra("product_image", productsList.get(position).getImageUrls().get(0));
            context.startActivity(intent);
        }
    };

    public ShopProductAdapter(Context context, List<ProductItem> a, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController, HashMap<String, String> data) {
        this.context = context;
        productsList = a;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
        this.navController = navController;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.recycler_header_shops, parent, false);
            return new ShopProductHeader(v, context, activityInstance, fragmentInstance, navController, data);
        } else {
            View v = inflater.inflate(R.layout.item_home_product_grid, parent, false);
            return new VHItem(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderz, int position) {
        if (holderz instanceof ShopProductHeader) {

        } else if (holderz instanceof VHItem) {

            VHItem holder = (VHItem) holderz;
            ProductItem model = productsList.get(position);

            if (model.getName().contains("-")) {
                String str = model.getName();
                str = str.trim();
                String regex = "-[a-zA-Z0-9]+$";

                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(str);

                if (matcher.find()) {
                    String output = str.replaceAll(regex, "");
                    holder.textViewAndroid.setText(Html.fromHtml(output));
                } else
                    holder.textViewAndroid.setText(Html.fromHtml(model.getName()));
            } else
                holder.textViewAndroid.setText(Html.fromHtml(model.getName()));

            if (context != null)
                Glide.with(context)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .apply(new RequestOptions().override(260, 260))
                        .load(model.getImageUrls().get(0))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.ic_placeholder_small)
                        .into(holder.imageViewAndroid);


            if ((model.getMinPriceD() == 0) || (model.getMaxPriceD() == 0))
                holder.price.setText("Call For Price");

            else if (model.getMinDiscountedPriceD() != 0) {

                if (model.getMinDiscountedPriceD() < model.getMinPriceD()) {
                    holder.priceDiscount.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                    holder.priceDiscount.setVisibility(View.VISIBLE);
                    holder.priceDiscount.setPaintFlags(holder.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinDiscountedPriceD()));
                } else {
                    holder.priceDiscount.setVisibility(View.GONE);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                }

            } else
                holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(itemViewListener);

            holder.buyNow.setVisibility(View.GONE);

            if ((model.getMinPriceD() == 0) || (model.getMaxPriceD() == 0)) {
                holder.buyNow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    private boolean isPositionHeader(int position) {
        return productsList.get(position) instanceof HomeHeaderItem;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public void setFilter(ArrayList<ProductItem> ar) {
        productsList = new ArrayList<>();
        productsList.addAll(ar);
        notifyDataSetChanged();
    }


    class VHItem extends RecyclerView.ViewHolder {
        TextView textViewAndroid, price, priceDiscount, buyNow, tvCashback;
        ImageView imageViewAndroid;
        View itemView;
        CardView cardView;

        public VHItem(final View itemView) {
            super(itemView);
            textViewAndroid = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            imageViewAndroid = itemView.findViewById(R.id.image);
            priceDiscount = itemView.findViewById(R.id.priceDiscount);
            buyNow = itemView.findViewById(R.id.buy_now);
            tvCashback = itemView.findViewById(R.id.tvCashback);
            cardView = itemView.findViewById(R.id.cardView);
            this.itemView = itemView;
        }
    }


}

