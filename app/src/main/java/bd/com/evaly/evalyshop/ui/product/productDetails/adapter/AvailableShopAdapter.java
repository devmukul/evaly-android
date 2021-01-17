package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class AvailableShopAdapter extends RecyclerView.Adapter<AvailableShopAdapter.MyViewHolder> {

    private List<AvailableShopModel> availableShops;
    private Context context;
    private CartEntity cartItem;
    private View view;

    View.OnClickListener storeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = Integer.parseInt(v.getTag().toString());
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", availableShops.get(i).getShopSlug());
            intent.putExtra("shop_name", availableShops.get(i).getShopName());
            intent.putExtra("category", availableShops.get(i).getShopSlug());
            context.startActivity(intent);
        }
    };
    private CartDao cartDao;

    public AvailableShopAdapter(Context context, View view, List<AvailableShopModel> availableShops, CartDao cartDao, CartEntity cartItem) {
        this.availableShops = availableShops;
        this.context = context;
        this.cartDao = cartDao;
        this.cartItem = cartItem;
        this.view = view;
    }

    @NonNull
    @Override
    public AvailableShopAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.available_shop, viewGroup, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {

        AvailableShopModel shop = availableShops.get(i);

        viewHolder.shopName.setText(shop.getShopName());

        if (shop.getShopAddress().equals("xxx"))
            viewHolder.address.setText("Dhaka, Bangladesh");
        else
            viewHolder.address.setText(shop.getShopAddress());

        if (shop.getInStock() < 1){
            viewHolder.stock.setText("Contact Seller");
        } else {
            viewHolder.stock.setText("Stock Available");
        }

        if (shop.getDiscountedPrice() == null ||
                shop.getDiscountedPrice() < 1 ||
                shop.getDiscountedPrice() >= shop.getPrice()
        ) {
            viewHolder.price.setText(Utils.formatPriceSymbol(shop.getPrice()));
            viewHolder.maximumPrice.setVisibility(View.GONE);
        } else {
            viewHolder.maximumPrice.setVisibility(View.VISIBLE);
            viewHolder.maximumPrice.setText(Utils.formatPriceSymbol(shop.getPrice()));
            viewHolder.price.setText(Utils.formatPriceSymbol(shop.getDiscountedPrice()));
            viewHolder.maximumPrice.setPaintFlags(viewHolder.maximumPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (shop.getPrice() == 0 && shop.getDiscountedPrice() == 0) {
            viewHolder.price.setVisibility(View.INVISIBLE);
            viewHolder.buyBtn.setText(R.string.not_available);
            viewHolder.buyBtn.getBackground().setAlpha(140);
            viewHolder.buyBtn.setEnabled(false);
            viewHolder.maximumPrice.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(shop.getShopImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(300, 200))
                .into(viewHolder.shopImage);

        viewHolder.chatBtn.setOnClickListener(v -> Toast.makeText(context, "Chat system coming soon!", Toast.LENGTH_SHORT).show());

        if (viewHolder.buyBtn.getText().toString().equals("Out of Stock"))
            viewHolder.buyBtn.setEnabled(false);

        viewHolder.buyBtn.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            String price = Utils.formatPrice(shop.getPrice());

            if (shop.getDiscountedPrice() != null)
                if (shop.getDiscountedPrice() > 0)
                    price = Utils.formatPrice(shop.getDiscountedPrice());

            String sellerJson = new Gson().toJson(shop);

            CartEntity cartEntity = new CartEntity();
            cartEntity.setName(cartItem.getName());
            cartEntity.setImage(cartItem.getImage());
            cartEntity.setPriceRound(price);
            cartEntity.setTime(calendar.getTimeInMillis());
            cartEntity.setShopJson(sellerJson);
            cartEntity.setQuantity(1);
            cartEntity.setShopSlug(shop.getShopSlug());
            cartEntity.setShopName(shop.getShopName());
            cartEntity.setSlug(cartItem.getSlug());
            cartEntity.setProductID(String.valueOf(shop.getShopItemId()));

            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
                if (dbItem.size() == 0)
                    cartDao.insert(cartEntity);
                else
                    cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + 1);
            });
            Snackbar snackBar = Snackbar.make(view, "Added to cart", 1500);
            snackBar.setAction("Go to Cart", v1 -> {
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);
                snackBar.dismiss();
            });
            snackBar.show();
        });

        // circle buttons clicks

        viewHolder.shop.setTag(i);
        viewHolder.shopName.setTag(i);

        viewHolder.shop.setOnClickListener(storeClick);
        viewHolder.shopImage.setOnClickListener(storeClick);
        viewHolder.shopName.setOnClickListener(storeClick);

        viewHolder.call.setOnClickListener(v -> {
            final String phone = shop.getContactNumber();
            final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
            snackBar.setAction("Call", v12 -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                } catch (Exception ignored) {
                }
                snackBar.dismiss();
            });
            snackBar.show();
        });

        viewHolder.location.setOnClickListener(v -> {
            final String location;
            if (shop.getShopAddress().equals("xxx"))
                location = "Dhaka, Bangladesh";
            else
                location = shop.getShopAddress();

            final Snackbar snackBar = Snackbar.make(view, location + "", Snackbar.LENGTH_LONG);
            snackBar.setAction("Copy", v13 -> {
                try {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("address", location);
                    clipboard.setPrimaryClip(clip);
                } catch (Exception e) {
                }
                snackBar.dismiss();
            });
            snackBar.show();
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return availableShops.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView shopName, price, chatBtn, buyBtn, address, maximumPrice, priceOff, stock;
        ImageView shopImage, shop, call, location;
        LinearLayout discount;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            shopImage = itemView.findViewById(R.id.shop_image);
            shopName = itemView.findViewById(R.id.shop_name);
            address = itemView.findViewById(R.id.address);
            shop = itemView.findViewById(R.id.shopPage);
            call = itemView.findViewById(R.id.call);
            discount = itemView.findViewById(R.id.discount);
            location = itemView.findViewById(R.id.location);
            maximumPrice = itemView.findViewById(R.id.max_price);
            priceOff = itemView.findViewById(R.id.price_off);
            stock = itemView.findViewById(R.id.stock);
            chatBtn = itemView.findViewById(R.id.chat);
            buyBtn = itemView.findViewById(R.id.buy);

            price = itemView.findViewById(R.id.price);
            view = itemView;
        }
    }
}
