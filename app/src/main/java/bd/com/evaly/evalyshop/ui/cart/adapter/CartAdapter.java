package bd.com.evaly.evalyshop.ui.cart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

    private List<CartEntity> itemList;
    private Context context;
    private CartAdapter instance;
    private CartDao cartDao;

    public CartListener listener;

    public interface CartListener{
        void updateCartFromRecycler();
        void uncheckSelectAllBtn(boolean isChecked);
    }

    public void setListener(CartListener listener){

        this.listener = listener;
    }

    public CartAdapter(Context context, CartDao cartDao) {
        this.context = context;
        instance = this;
        this.cartDao = cartDao;

        itemList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart,viewGroup,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


        
        JSONObject jsonObject = new JSONObject();

        myViewHolder.wholeSalePrice.setPaintFlags(myViewHolder.wholeSalePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        try {
            jsonObject = new JSONObject(itemList.get(i).getShopJson());
            String shopName = jsonObject.getString("shop_name");
            myViewHolder.shop.setText("Seller: " + shopName);

        } catch (Exception e){
            myViewHolder.shop.setText("Seller: Unknown");

        }

        myViewHolder.checkBox.post(() -> myViewHolder.checkBox.setChecked(itemList.get(i).isSelected()));

        myViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            itemList.get(i).setSelected(isChecked);

            Executors.newSingleThreadExecutor().execute(() -> cartDao.markSelected(itemList.get(i).getProductID(), isChecked));
            if (listener != null){
                listener.updateCartFromRecycler();
                listener.uncheckSelectAllBtn(isChecked);
            }
        });


        myViewHolder.plus.setOnClickListener(v -> {

            int price = itemList.get(i).getPriceInt();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp = price;

            try {

                JSONObject jsonObject1 = new JSONObject(itemList.get(i).getShopJson());
                wholeSaleMin = jsonObject1.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject1.getString("wholesale_price"));

            } catch (Exception e) { }

            myViewHolder.quantity.requestFocus();
            int current = itemList.get(i).getQuantity();
            itemList.get(i).setQuantity(++current);
            myViewHolder.quantity.setText(String.format("%d", current));

            if (current >= wholeSaleMin && wholeSaleMin > 1) {
                myViewHolder.wholeSalePrice.setVisibility(View.VISIBLE);
                priceTemp = wholesalePrice;
            } else {
                myViewHolder.wholeSalePrice.setVisibility(View.GONE);
                priceTemp = price;
            }

            myViewHolder.totalPrice.setText(String.format("৳ %d", priceTemp * itemList.get(i).getQuantity()));
            myViewHolder.price.setText(String.format("৳ %d x %d", priceTemp, itemList.get(i).getQuantity()));
            myViewHolder.wholeSalePrice.setText(String.format("৳ %d", price * itemList.get(i).getQuantity()));

            Executors.newSingleThreadExecutor().execute(() ->  cartDao.updateQuantity(itemList.get(i).getProductID(), itemList.get(i).getQuantity()));

        });

        myViewHolder.minus.setOnClickListener(v -> {


            int price = itemList.get(i).getPriceInt();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp;

            try {
                JSONObject jsonObject12 = new JSONObject(itemList.get(i).getShopJson());
                wholeSaleMin = jsonObject12.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject12.getString("wholesale_price"));

            } catch (Exception e) { }

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

                myViewHolder.totalPrice.setText(String.format("৳ %d", priceTemp * itemList.get(i).getQuantity()));
                myViewHolder.price.setText(String.format("৳ %d x %d", priceTemp, itemList.get(i).getQuantity()));
                myViewHolder.wholeSalePrice.setText(String.format("৳ %d", price * itemList.get(i).getQuantity()));

                Executors.newSingleThreadExecutor().execute(() ->  cartDao.updateQuantity(itemList.get(i).getProductID(), itemList.get(i).getQuantity()));
            }
        });


        myViewHolder.quantity.setOnEditorActionListener((v, actionId, event) -> {

            int price = itemList.get(i).getPriceInt();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp = price;

            try {

                JSONObject jsonObject13 = new JSONObject(itemList.get(i).getShopJson());
                wholeSaleMin = jsonObject13.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject13.getString("wholesale_price"));

            } catch (Exception e) { }

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

            Executors.newSingleThreadExecutor().execute(() ->  cartDao.updateQuantity(itemList.get(i).getProductID(), itemList.get(i).getQuantity()));
            // hide keyboard

            if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myViewHolder.quantity.getWindowToken(), 0);
                myViewHolder.quantity.clearFocus();
            }

            return true;
        });


        myViewHolder.productName.setText(Html.fromHtml(itemList.get(i).getName()));
        myViewHolder.time.setText(Utils.getTimeAgo(itemList.get(i).getTime()));
        myViewHolder.quantity.setText(String.format(Locale.ENGLISH, "%d", itemList.get(i).getQuantity()));

        {
            int price = itemList.get(i).getPriceInt();
            int wholeSaleMin = 0;
            int wholesalePrice = price;
            int priceTemp;

            try {
                wholeSaleMin = jsonObject.getInt("minimum_wholesale_quantity");
                wholesalePrice = (int) Double.parseDouble(jsonObject.getString("wholesale_price"));

            } catch (Exception e) { }

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
                .into(myViewHolder.productImage);

        myViewHolder.productName.setOnClickListener(v -> openProductPage(i));
        myViewHolder.productImage.setOnClickListener(v -> openProductPage(i));

        myViewHolder.trash.setOnClickListener(v -> {

            Executors.newSingleThreadExecutor().execute(() ->  cartDao.deleteBySlug(itemList.get(i).getProductID()));
            itemList.remove(i);
            instance.notifyItemRemoved(i);
            notifyItemRangeChanged(i, itemList.size());

        });

        myViewHolder.variation.setVisibility(View.GONE);

        if (!itemList.get(i).isShowShopTitle()) {
            myViewHolder.sellerHolder.setVisibility(View.GONE);
            myViewHolder.dividerView.setVisibility(View.GONE);
        } else {
            myViewHolder.sellerHolder.setVisibility(View.VISIBLE);
            myViewHolder.dividerView.setVisibility(View.VISIBLE);
        }


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

    public List<CartEntity>  getItemList(){

        return itemList;

    }


    @Override
    public long getItemId(int position) {
        return itemList == null ? 0 : itemList.get(position).getId();
    }

    public void setItemList(final List<CartEntity> productList) {

        List<String> shopSlugList =  new ArrayList<>();

        for (int i=0; i<productList.size(); i++){

            if (!shopSlugList.contains(productList.get(i).getShopSlug())){
                productList.get(i).setShowShopTitle(true);
                shopSlugList.add(productList.get(i).getShopSlug());
            } else
                productList.get(i).setShowShopTitle(false);
        }


        if (itemList == null) {
            itemList = productList;
            notifyItemRangeInserted(0, productList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return itemList.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(itemList.get(oldItemPosition).getProductID(), productList.get(newItemPosition).getProductID());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CartEntity newProduct = productList.get(newItemPosition);
                    CartEntity oldProduct = itemList.get(oldItemPosition);

                    return Objects.equals(newProduct.getProductID(), oldProduct.getProductID())
                            && Objects.equals(newProduct.getName(), oldProduct.getName())
                            && newProduct.getQuantity() == oldProduct.getQuantity()
                            && newProduct.isSelected() == oldProduct.isSelected()
                            && newProduct.isShowShopTitle() == oldProduct.isShowShopTitle();
                }
            });


            itemList = productList;
            result.dispatchUpdatesTo(this);

        }
    }



}
