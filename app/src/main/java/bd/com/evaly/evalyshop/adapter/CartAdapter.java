package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.CartActivity;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.models.CartItem;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

    ArrayList<CartItem> itemList;
    Context context;
    DbHelperCart db;

    CartAdapter instance;

    public CartListener listener;

    public interface CartListener{
        void updateCartFromRecycler();
        void uncheckSelectAllBtn(boolean isChecked);
    }

    public void setListener(CartListener listener){

        this.listener = listener;
    }

    public CartAdapter(ArrayList<CartItem> itemList, Context context, DbHelperCart db) {
        this.itemList = itemList;
        this.context = context;
        this.db = db;
        instance = this;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        //Log.d("wish_product",itemList.get(i).getName());


        if (i>0){

            if (itemList.get(i).getShopSlug().equals(itemList.get(i-1).getShopSlug())) {
                myViewHolder.sellerHolder.setVisibility(View.GONE);
                myViewHolder.dividerView.setVisibility(View.GONE);
            }
            else {
                myViewHolder.sellerHolder.setVisibility(View.VISIBLE);
                myViewHolder.dividerView.setVisibility(View.VISIBLE);
            }

        }


        JSONObject jsonObject = new JSONObject();


        myViewHolder.wholeSalePrice.setPaintFlags(myViewHolder.wholeSalePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        try {

            jsonObject = new JSONObject(itemList.get(i).getSellerJson());


            Log.d("json cart", itemList.get(i).getSellerJson());

            String shopName = jsonObject.getString("shop_name");
            myViewHolder.shop.setText("Seller: " + shopName);


        } catch (Exception e){
            myViewHolder.shop.setText("Seller: Unknown");

        }


        myViewHolder.checkBox.post(() -> myViewHolder.checkBox.setChecked(itemList.get(i).isSelected()));

        myViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            itemList.get(i).setSelected(isChecked);

            if (context instanceof CartActivity) {

                ((CartActivity) context).updateCartFromRecycler();
                ((CartActivity) context).uncheckSelectAllBtn(isChecked);
            }

            if (listener != null){
                listener.updateCartFromRecycler();
                listener.uncheckSelectAllBtn(isChecked);
            }


        });


        // quantity counter

        myViewHolder.plus.setOnClickListener(v -> {


            int price = itemList.get(i).getPrice();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp = price;

            JSONObject jsonObject1 = new JSONObject();
            try {

                jsonObject1 = new JSONObject(itemList.get(i).getSellerJson());
                wholeSaleMin = jsonObject1.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject1.getString("wholesale_price"));


            } catch (Exception e) {

            }


            myViewHolder.quantity.requestFocus();


            int current = itemList.get(i).getQuantity();
            itemList.get(i).setQuantity(++current);
            myViewHolder.quantity.setText(current+"");



            if (current >= wholeSaleMin && wholeSaleMin > 1) {
                myViewHolder.wholeSalePrice.setVisibility(View.VISIBLE);
                priceTemp = wholesalePrice;
            } else {


                myViewHolder.wholeSalePrice.setVisibility(View.GONE);
                priceTemp = price;

            }


            myViewHolder.totalPrice.setText("৳ "+(priceTemp * itemList.get(i).getQuantity()));
            myViewHolder.price.setText("৳ "+priceTemp + " x " + itemList.get(i).getQuantity());

            myViewHolder.wholeSalePrice.setText("৳ "+(price * itemList.get(i).getQuantity()));

            db.updateQuantity(itemList.get(i).getId(), itemList.get(i).getQuantity());

            if (context instanceof CartActivity)
                ((CartActivity) context).updateCartFromRecycler();

            if (listener != null)
                listener.updateCartFromRecycler();

        });
        myViewHolder.minus.setOnClickListener(v -> {


            int price = itemList.get(i).getPrice();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp = price;

            JSONObject jsonObject12 = new JSONObject();
            try {

                jsonObject12 = new JSONObject(itemList.get(i).getSellerJson());
                wholeSaleMin = jsonObject12.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject12.getString("wholesale_price"));


            } catch (Exception e) {

            }


            myViewHolder.quantity.requestFocus();

            int current = itemList.get(i).getQuantity();
            if(current >= 2) {
                itemList.get(i).setQuantity(--current);
                myViewHolder.quantity.setText(current + "");


                if (current >= wholeSaleMin && wholeSaleMin > 1) {
                    myViewHolder.wholeSalePrice.setVisibility(View.VISIBLE);
                    priceTemp = wholesalePrice;
                } else {


                    myViewHolder.wholeSalePrice.setVisibility(View.GONE);
                    priceTemp = price;

                }

                myViewHolder.totalPrice.setText("৳ "+(priceTemp * itemList.get(i).getQuantity()));
                myViewHolder.price.setText("৳ "+priceTemp + " x " + itemList.get(i).getQuantity());
                myViewHolder.wholeSalePrice.setText("৳ "+(price * itemList.get(i).getQuantity()));

                db.updateQuantity(itemList.get(i).getId(), itemList.get(i).getQuantity());

            }

            if (context instanceof CartActivity)
                ((CartActivity) context).updateCartFromRecycler();

            if (listener != null)
                listener.updateCartFromRecycler();
        });


        myViewHolder.quantity.setOnEditorActionListener((v, actionId, event) -> {


            int price = itemList.get(i).getPrice();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp = price;

            JSONObject jsonObject13 = new JSONObject();
            try {

                jsonObject13 = new JSONObject(itemList.get(i).getSellerJson());
                wholeSaleMin = jsonObject13.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject13.getString("wholesale_price"));

            } catch (Exception e) {

            }

            try{
                String val = myViewHolder.quantity.getText().toString();
                int value = Integer.parseInt(val);
                if(value >=1) {
                    itemList.get(i).setQuantity(value);

                }

                if (value >= wholeSaleMin && wholeSaleMin > 1) {
                    myViewHolder.wholeSalePrice.setVisibility(View.VISIBLE);
                    priceTemp = wholesalePrice;
                } else {


                    myViewHolder.wholeSalePrice.setVisibility(View.GONE);
                    priceTemp = price;
                }

            } catch (Exception e){

                itemList.get(i).setQuantity(1);
                myViewHolder.quantity.setText("1");


            }

            myViewHolder.totalPrice.setText(String.format(Locale.ENGLISH, "৳ %d", priceTemp * itemList.get(i).getQuantity()));
            myViewHolder.price.setText(String.format(Locale.ENGLISH, "৳ %d x %d", priceTemp, itemList.get(i).getQuantity()));
            myViewHolder.wholeSalePrice.setText(String.format(Locale.ENGLISH, "৳ %d", price * itemList.get(i).getQuantity()));


            db.updateQuantity(itemList.get(i).getId(), itemList.get(i).getQuantity());
            // hide keyboard

            if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myViewHolder.quantity.getWindowToken(), 0);

                myViewHolder.quantity.clearFocus();
            }

            if (context instanceof CartActivity)
                ((CartActivity) context).updateCartFromRecycler();

            if (listener != null)
                listener.updateCartFromRecycler();
                return true;
        });


        myViewHolder.productName.setText(Html.fromHtml(itemList.get(i).getName()));
        myViewHolder.time.setText(Utils.getTimeAgo(itemList.get(i).getTime()));
        myViewHolder.quantity.setText(itemList.get(i).getQuantity()+"");

        {

            int price = itemList.get(i).getPrice();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp = price;

            try {

                wholeSaleMin = jsonObject.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject.getString("wholesale_price"));


            } catch (Exception e) {

            }


            int current = itemList.get(i).getQuantity();


            if (current >= wholeSaleMin && wholeSaleMin > 1) {
                myViewHolder.wholeSalePrice.setVisibility(View.VISIBLE);
                priceTemp = wholesalePrice;
            } else {


                myViewHolder.wholeSalePrice.setVisibility(View.GONE);
                priceTemp = price;

            }
            myViewHolder.totalPrice.setText(String.format(Locale.ENGLISH, "৳ %d", priceTemp * itemList.get(i).getQuantity()));
            myViewHolder.price.setText(String.format(Locale.ENGLISH, "৳ %d x %d", priceTemp, itemList.get(i).getQuantity()));

        }

        Glide.with(context)
                .load(itemList.get(i).getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
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


        myViewHolder.productName.setOnClickListener(v -> openProductPage(i));

        myViewHolder.productImage.setOnClickListener(v -> openProductPage(i));

        myViewHolder.trash.setOnClickListener(v -> {
            db.deleteData(itemList.get(i).getId());
            itemList.remove(i);
            instance.notifyItemRemoved(i);
            notifyItemRangeChanged(i, itemList.size());
        });
        myViewHolder.variation.setVisibility(View.GONE);
    }



    private void openProductPage(int i){
        try {
            Intent intent = new Intent(context, ViewProductActivity.class);
            intent.putExtra("product_slug", itemList.get(i).getSlug());
            intent.putExtra("product_name", itemList.get(i).getName());
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "This product is not available in Evaly right now.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView productName,time,price,shop,totalPrice,wholeSalePrice;
        ImageView productImage,trash, plus, minus;
        EditText quantity;
        CheckBox checkBox;
        TextView variation;
        View view, dividerView;
        LinearLayout sellerHolder;
        public MyViewHolder(final View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.product_name);
            time=itemView.findViewById(R.id.time);
            shop = itemView.findViewById(R.id.shop);
            price=itemView.findViewById(R.id.price);
            totalPrice=itemView.findViewById(R.id.priceTotal);
            productImage= itemView.findViewById(R.id.product_image);
            trash= itemView.findViewById(R.id.trash);
            checkBox = itemView.findViewById(R.id.checkBox);
            wholeSalePrice = itemView.findViewById(R.id.wholeSalePrice);
            variation = itemView.findViewById(R.id.variation);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            quantity = itemView.findViewById(R.id.quantity);
            sellerHolder = itemView.findViewById(R.id.sellerHolder);
            dividerView = itemView.findViewById(R.id.dividerView);

            view = itemView;
        }
    }

    public ArrayList<CartItem>  getItemList(){

        return itemList;

    }

}
