package bd.com.evaly.evalyshop.ui.express.products;

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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;

public class ExpressProductSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_PROGRESS = 2;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;
    private List<ProductItem> productsList;
    View.OnClickListener itemViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            Intent intent = new Intent(context, ViewProductActivity.class);
            intent.putExtra("product_slug", productsList.get(position).getSlug());
            intent.putExtra("product_name", productsList.get(position).getName());
            intent.putExtra("product_price", productsList.get(position).getMaxPrice());
            if (productsList.get(position).getImageUrls() != null && productsList.get(position).getImageUrls().size() > 0)
                intent.putExtra("product_image", productsList.get(position).getImageUrls().get(0));
            context.startActivity(intent);
        }
    };
    private NetworkState networkState;
    private HashMap<String, String> data;
    private ShopViewModel viewModel;
    private ShopDetailsModel shopDetails;
    private int cashback_rate = 0;

    public ExpressProductSearchAdapter(Context context, List<ProductItem> a, AppCompatActivity activityInstance, HashMap<String, String> data, ShopViewModel viewModel) {
        this.context = context;
        productsList = a;
        this.activityInstance = activityInstance;
        this.data = data;
        this.viewModel = viewModel;
    }

    public void setCashbackRate(int cashback_rate) {
        this.cashback_rate = cashback_rate;
    }

    public void setShopDetails(ShopDetailsModel shopDetails) {
        this.shopDetails = shopDetails;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_home_product_grid, parent, false);
        return new ExpressProductSearchAdapter.VHItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderz, int position) {
        if (holderz instanceof ExpressProductSearchAdapter.VHItem) {

            ExpressProductSearchAdapter.VHItem holder = (ExpressProductSearchAdapter.VHItem) holderz;
            ProductItem model = productsList.get(position);

            holder.textViewAndroid.setText(Html.fromHtml(model.getName()));

            if (context != null && model.getImageUrls() != null)
                Glide.with(context)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .apply(new RequestOptions().override(260, 260))
                        .load(model.getImageUrls().get(0))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.ic_evaly_placeholder)
                        .into(holder.imageViewAndroid);


            if ((model.getMinDiscountedPriceD() == 0) || (model.getMinPriceD() == 0)) {
                holder.price.setVisibility(View.GONE);
                holder.tvCashback.setVisibility(View.VISIBLE);
                holder.tvCashback.setText("Unavailable");
            } else if (model.getMinDiscountedPriceD() != 0) {

                holder.tvCashback.setVisibility(View.GONE);
                if (model.getMinDiscountedPriceD() < model.getMinPriceD()) {
                    holder.priceDiscount.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                    holder.priceDiscount.setVisibility(View.VISIBLE);
                    holder.priceDiscount.setPaintFlags(holder.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinDiscountedPriceD()));
                } else {
                    holder.priceDiscount.setVisibility(View.GONE);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                }

            } else {
                holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                holder.tvCashback.setVisibility(View.GONE);
            }

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(itemViewListener);

            if ((model.getMinPriceD() == 0) || (model.getMinDiscountedPriceD() == 0)) {
                holder.buyNow.setVisibility(View.GONE);
            }

            holder.buyNow.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
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


