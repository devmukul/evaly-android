package bd.com.evaly.evalyshop.ui.shop.search;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;

public class ShopSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<ItemsItem> productsList;
    private String shopSlug;
    private String campaignSlug;

    View.OnClickListener itemViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); // get item for position

            Intent intent = new Intent(context, ViewProductActivity.class);
            intent.putExtra("product_slug", productsList.get(position).getShopItemSlug());
            intent.putExtra("product_name", productsList.get(position).getItemName());
            intent.putExtra("product_price", productsList.get(position).getItemPrice());
            if (shopSlug != null)
                intent.putExtra("shop_slug", shopSlug);
            if (productsList.get(position).getItemImages().size() > 0)
                intent.putExtra("product_image", productsList.get(position).getItemImages().get(0));
            context.startActivity(intent);
        }
    };
    private NetworkState networkState;
    private HashMap<String, String> data;
    private ShopViewModel viewModel;
    private ShopDetailsModel shopDetails;
    private int cashback_rate = 0;

    public ShopSearchAdapter(Context context, List<ItemsItem> a, HashMap<String, String> data, ShopViewModel viewModel) {
        this.context = context;
        productsList = a;
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
        return new ShopSearchAdapter.VHItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderz, int position) {
        if (holderz instanceof ShopSearchAdapter.VHItem) {

            ShopSearchAdapter.VHItem holder = (ShopSearchAdapter.VHItem) holderz;
            ItemsItem model = productsList.get(position);

            holder.textViewAndroid.setText(Html.fromHtml(model.getItemName()));

            if (context != null)
                Glide.with(context)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .apply(new RequestOptions().override(260, 260))
                        .load(model.getItemImages().get(0))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.ic_evaly_placeholder)
                        .into(holder.imageViewAndroid);


            if ((model.getDiscountedPrice() == 0) || (model.getItemPrice() == 0))
                holder.price.setText("Not Available");

            else if (model.getDiscountedPrice() != 0) {

                if (model.getDiscountedPrice() < model.getItemPrice()) {
                    holder.priceDiscount.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getItemPrice()));
                    holder.priceDiscount.setVisibility(View.VISIBLE);
                    holder.priceDiscount.setPaintFlags(holder.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getDiscountedPrice()));
                } else {
                    holder.priceDiscount.setVisibility(View.GONE);
                    holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getItemPrice()));
                }

            } else
                holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getItemPrice()));

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(itemViewListener);

            if ((model.getItemPrice() == 0) || (model.getDiscountedPrice() == 0)) {
                holder.buyNow.setVisibility(View.GONE);
            }

            if (cashback_rate == 0)
                holder.tvCashback.setVisibility(View.GONE);
            else {
                holder.tvCashback.setVisibility(View.VISIBLE);
                holder.tvCashback.bringToFront();
                holder.tvCashback.setText(String.format(Locale.ENGLISH, "%d%% Cashback", cashback_rate));
            }

            holder.buyNow.setTag(position);
            holder.buyNow.setVisibility(View.VISIBLE);
            holder.buyNow.setOnClickListener(v -> {
                viewModel.setBuyNowLiveData(model.getShopItemSlug());
            });

            if ((model.getDiscountedPrice() == 0) || (model.getItemPrice() == 0)) {
                holder.buyNow.setVisibility(View.GONE);
            }
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

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public String getCampaignSlug() {
        return campaignSlug;
    }

    public void setCampaignSlug(String campaignSlug) {
        this.campaignSlug = campaignSlug;
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


