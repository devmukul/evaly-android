package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.shop.AvailableShop;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;

public class AvailableShopAdapter extends RecyclerView.Adapter<AvailableShopAdapter.MyViewHolder> {

    List<AvailableShopModel> availableShops;
    Context context;
    CartEntity cartItem;
    View view;
    Set<AvailableShop> set;
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

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.shopName.setText(availableShops.get(i).getShopName());

        if (availableShops.get(i).getShopAddress().equals("xxx"))
            myViewHolder.address.setText("Dhaka, Bangladesh");
        else
            myViewHolder.address.setText(availableShops.get(i).getShopAddress());

//        if (availableShops.get(i).getInStock() == 0) {
//            myViewHolder.buyBtn.setText("Out of Stock");
//            myViewHolder.buyBtn.setEnabled(false);
//        }

        try {
            String actualPrice = Integer.toString((int) Math.round(availableShops.get(i).getPrice()));
            String discountPrice = Integer.toString((int) Math.round(availableShops.get(i).getDiscountedPrice()));

            if (availableShops.get(i).getDiscountValue() == 0) {

                myViewHolder.price.setText(String.format(Locale.ENGLISH, "৳ %s", actualPrice));
                myViewHolder.maximumnPrice.setVisibility(View.GONE);

                if (actualPrice.equals("0")) {
                    myViewHolder.price.setVisibility(View.INVISIBLE);
                    myViewHolder.buyBtn.setText("Not Available");
                    myViewHolder.buyBtn.getBackground().setAlpha(140);
                    myViewHolder.buyBtn.setEnabled(false);
                    myViewHolder.maximumnPrice.setVisibility(View.GONE);
                }

            } else {
                myViewHolder.maximumnPrice.setPaintFlags(myViewHolder.maximumnPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                myViewHolder.price.setText(String.format("৳ %s", discountPrice));
                myViewHolder.maximumnPrice.setText(String.format("৳ %s", actualPrice));
                myViewHolder.maximumnPrice.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            myViewHolder.price.setText(String.format(" %s", availableShops.get(i).getPrice()));
        }

        Glide.with(context)
                .load(availableShops.get(i).getShopImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(300, 200))
                .into(myViewHolder.shopImage);


        myViewHolder.chatBtn.setOnClickListener(v -> Toast.makeText(context, "Chat system coming soon!", Toast.LENGTH_SHORT).show());

        if (myViewHolder.buyBtn.getText().toString().equals("Out of Stock"))
            myViewHolder.buyBtn.setEnabled(false);

        myViewHolder.buyBtn.setOnClickListener(v -> {

            if (myViewHolder.buyBtn.getText().toString().equals("Out of Stock")) {
                Toast.makeText(context, "Sorry the product is out of stock in this shop. Please select another shop.", Toast.LENGTH_LONG).show();


            } else {

                cartItem.setProductID(String.valueOf(availableShops.get(i).getShopItemId()));

                if (availableShops.get(i).getDiscountValue() == 0) {
                    if (availableShops.get(i).getPrice() < 1) {
                        Toast.makeText(context, "Can't add this product to cart.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        cartItem.setPriceRound(String.valueOf(availableShops.get(i).getPrice()));

                    } catch (Exception e) {
                        e.printStackTrace();
                        cartItem.setPrice("0");
                    }
                } else {

                    if (availableShops.get(i).getDiscountedPrice() < 1) {
                        Toast.makeText(context, "Can't add this product to cart.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        cartItem.setPriceRound(String.valueOf(availableShops.get(i).getDiscountedPrice()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        cartItem.setPrice("0");
                    }
                }

                cartItem.setQuantity(1);
                cartItem.setSelected(true);
                cartItem.setShopJson(new Gson().toJson(availableShops.get(i)));
                cartItem.setShopSlug(availableShops.get(i).getShopSlug());

                Executors.newSingleThreadExecutor().execute(() -> {

                    List<CartEntity> dbItem = cartDao.checkExistsEntity(cartItem.getProductID());

                    if (dbItem.size() > 0)
                        cartDao.updateQuantity(cartItem.getProductID(), dbItem.get(0).getQuantity() + 1);
                    else
                        cartDao.insert(cartItem);

                });

                Snackbar snackBar = Snackbar.make(view, "Added to cart", 1500);
                snackBar.setAction("Go to Cart", v1 -> {
                    try {
                        Intent intent = new Intent(context, CartActivity.class);
                        context.startActivity(intent);
                    } catch (Exception ignored) {
                    }

                    snackBar.dismiss();
                });
                snackBar.show();

            }
        });


        // circle buttons clicks

        myViewHolder.shop.setTag(i);
        myViewHolder.shopName.setTag(i);

        myViewHolder.shop.setOnClickListener(storeClick);
        myViewHolder.shopName.setOnClickListener(storeClick);

        myViewHolder.call.setOnClickListener(v -> {

            final String phone = availableShops.get(i).getContactNumber();

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

        myViewHolder.location.setOnClickListener(v -> {

            final String location;
            if (availableShops.get(i).getShopAddress().equals("xxx"))
                location = "Dhaka, Bangladesh";
            else
                location = availableShops.get(i).getShopAddress();

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

        TextView shopName, price, chatBtn, buyBtn, address, maximumnPrice, priceOff;
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
            maximumnPrice = itemView.findViewById(R.id.max_price);
            priceOff = itemView.findViewById(R.id.price_off);

            chatBtn = itemView.findViewById(R.id.chat);
            buyBtn = itemView.findViewById(R.id.buy);

            price = itemView.findViewById(R.id.price);
            view = itemView;
        }
    }
}
