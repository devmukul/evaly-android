package bd.com.evaly.evalyshop.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.CartActivity;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.models.AvailableShop;
import bd.com.evaly.evalyshop.models.CartItem;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;

public class AvailableShopAdapter extends RecyclerView.Adapter<AvailableShopAdapter.MyViewHolder>{

    ArrayList<AvailableShop> availableShops;
    Context context;
    DbHelperCart db;
    CartItem cartItem;
    View view;
    Set<AvailableShop> set;

    public AvailableShopAdapter(Context context, View view,ArrayList<AvailableShop> availableShops, DbHelperCart db, CartItem cartItem) {
        this.availableShops = availableShops;
        this.context=context;
        this.db = db;
        this.cartItem = cartItem;
        this.view = view;
    }

    @NonNull
    @Override
    public AvailableShopAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.available_shop,viewGroup,false);
        return new MyViewHolder(view);
    }


    View.OnClickListener storeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            int i = Integer.parseInt(v.getTag().toString());

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", availableShops.get(i).getShopSlug());
            intent.putExtra("shop_name", availableShops.get(i).getName());
            intent.putExtra("category", availableShops.get(i).getSlug());

            context.startActivity(intent);

        }
    };


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.shopName.setText(availableShops.get(i).getName());

        if(availableShops.get(i).getAddress().equals("xxx"))
            myViewHolder.address.setText("Dhaka, Bangladesh");
        else
            myViewHolder.address.setText(availableShops.get(i).getAddress());





        if(availableShops.get(i).getStock()==false){
            myViewHolder.buyBtn.setText("Out of Stock");
            myViewHolder.buyBtn.setEnabled(false);
        }




//        if(Double.parseDouble(availableShops.get(i).getMaximumPrice())-Double.parseDouble(availableShops.get(i).getPrice())>0){
//            myViewHolder.maximumnPrice.setText("৳ "+availableShops.get(i).getMaximumPrice());
//            myViewHolder.maximumnPrice.setPaintFlags(myViewHolder.maximumnPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            Double a=Double.parseDouble(availableShops.get(i).getMaximumPrice());
//            Double b=Double.parseDouble(availableShops.get(i).getPrice());
//            Double percentage=((a-b)*100)/a;
//            myViewHolder.priceOff.setText((int)Math.ceil(percentage)+" % off");
//        }else{
//            myViewHolder.discount.setVisibility(View.INVISIBLE);
//        }

        try {

            Log.d("json price", "pr" + availableShops.get(i).getPrice() + " mp " +availableShops.get(i).getMaximumPrice());

            String actualPrice = Integer.toString((int) Double.parseDouble(availableShops.get(i).getMaximumPrice()));
            String discountPrice = Integer.toString((int) Double.parseDouble(availableShops.get(i).getPrice()));


            if (actualPrice.equals(discountPrice)) {


                myViewHolder.price.setText("৳ " + actualPrice);
                myViewHolder.maximumnPrice.setVisibility(View.GONE);


            }
            else {

                myViewHolder.maximumnPrice.setPaintFlags(myViewHolder.maximumnPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                myViewHolder.price.setText("৳ " + discountPrice);
                myViewHolder.maximumnPrice.setText("৳ " + actualPrice);
                myViewHolder.maximumnPrice.setVisibility(View.VISIBLE);


            }


            if(discountPrice.equals("0") || actualPrice.equals("0")){

                myViewHolder.price.setVisibility(View.INVISIBLE);
                myViewHolder.buyBtn.setText("Call For Price");
                myViewHolder.buyBtn.getBackground().setAlpha(140);
                myViewHolder.buyBtn.setEnabled(false);
                myViewHolder.maximumnPrice.setVisibility(View.GONE);
            }




        } catch (Exception e){

            myViewHolder.price.setText(" " + availableShops.get(i).getPrice());

        }

        Glide.with(context)
                .load(availableShops.get(i).getLogo())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(300, 200))
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  return false;
                              }
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Bitmap bitmap = Utils.changeColor(((BitmapDrawable)resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                  myViewHolder.shopImage.setImageBitmap(bitmap);
                                  return true;
                              }
                          }
                )
                .into(myViewHolder.shopImage);


        myViewHolder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"Chat system coming soon!", Toast.LENGTH_SHORT).show();
            }
        });


        if(myViewHolder.buyBtn.getText().toString().equals("Out of Stock"))
            myViewHolder.buyBtn.setEnabled(false);


        myViewHolder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myViewHolder.buyBtn.getText().toString().equals("Out of Stock")){
                    Toast.makeText(context, "Sorry the product is out of stock in this shop. Please select another shop.", Toast.LENGTH_LONG).show();


                }else{

                    if(((int) Double.parseDouble(availableShops.get(i).getPrice())) < 1) {
                        Toast.makeText(context, "Can't add this product to cart.", Toast.LENGTH_LONG).show();
                        return;
                    }



                    Calendar calendar = Calendar.getInstance();

                    cartItem.setSlug(availableShops.get(i).getSlug());
                    cartItem.setProductId(availableShops.get(i).getProductId());

                    try {
                        cartItem.setPrice((int) Double.parseDouble(availableShops.get(i).getPrice()));
                    } catch (Exception e){
                        cartItem.setPrice(0);
                    }

                    cartItem.setQuantity(1);
                    cartItem.setSelected(true);
                    cartItem.setSellerJson(availableShops.get(i).getShopJson());
                    cartItem.setShopSlug(availableShops.get(i).getShopSlug());

                    if(db.insertData(cartItem.getSlug(),cartItem.getName(),cartItem.getImage(),cartItem.getPrice(), calendar.getTimeInMillis(), cartItem.getSellerJson(), 1, cartItem.getShopSlug(), cartItem.getProductId())){

                        Snackbar snackBar = Snackbar.make(view,"Added to cart", 1500);
                        snackBar.setAction("Go to Cart", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {

                                    Intent intent = new Intent(context, CartActivity.class);

                                    context.startActivity(intent);

                                } catch (Exception e){}

                                snackBar.dismiss();
                            }
                        });
                        snackBar.show();

                    }

                    ((ViewProductActivity) context).cartCount();
                }
            }
        });














        // circle buttons clicks

        myViewHolder.shop.setTag(i);
        myViewHolder.shopName.setTag(i);

        myViewHolder.shop.setOnClickListener(storeClick);
        myViewHolder.shopName.setOnClickListener(storeClick);

        myViewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final String phone = availableShops.get(i).getPhone();

                    final Snackbar snackBar = Snackbar.make(view, phone+"", Snackbar.LENGTH_LONG);

                    snackBar.setAction("Call", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {


                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone));

                                context.startActivity(intent);

                            } catch (Exception e){}

                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();


            }
        });

        myViewHolder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                    final String location;

                    if(availableShops.get(i).getAddress().equals("xxx"))
                        location = "Dhaka, Bangladesh";
                    else
                       location = availableShops.get(i).getAddress();


                    final Snackbar snackBar = Snackbar.make(view, location+"", Snackbar.LENGTH_LONG);

                    snackBar.setAction("Copy", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("address", location);
                                clipboard.setPrimaryClip(clip);
                            } catch (Exception e){}

                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();






            }
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

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView shopName,price,chatBtn,buyBtn,address,maximumnPrice,priceOff;
        ImageView shopImage,shop,call,location;
        LinearLayout discount;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            shopImage=itemView.findViewById(R.id.shop_image);
            shopName= itemView.findViewById(R.id.shop_name);
            address= itemView.findViewById(R.id.address);
            shop = itemView.findViewById(R.id.shopPage);
            call = itemView.findViewById(R.id.call);
            discount = itemView.findViewById(R.id.discount);
            location = itemView.findViewById(R.id.location);
            maximumnPrice = itemView.findViewById(R.id.max_price);
            priceOff = itemView.findViewById(R.id.price_off);

            chatBtn=itemView.findViewById(R.id.chat);
            buyBtn= itemView.findViewById(R.id.buy);

            price=itemView.findViewById(R.id.price);
            view=itemView;
        }
    }
}
