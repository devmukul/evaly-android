package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.fragment.BrowseProductFragment;

public class HomeCategoryAdapter2 extends RecyclerView.Adapter<HomeCategoryAdapter2.MyViewHolder>{

    Context context;
    ArrayList<String> titleCategory;
    ArrayList<Integer> imageCategory;

    public HomeCategoryAdapter2(Context ctx,ArrayList s,ArrayList i){
        context=ctx;
        titleCategory= s;
        imageCategory= i;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vector_home_category,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(titleCategory.get(position));


        try {

            if (position == 3 || position == 6 || position == 38) {

                // Log.d("image position ", position+"");
                Glide.with(context)
                        .asBitmap()
                        .load(imageCategory.get(position))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(300, 200)).into(holder.iv);

            }
            else {


                //holder.iv.setImageResource(imageCategory.get(position));
                holder.iv.post( new Runnable() {
                    public void run() {
                        holder.iv.setImageDrawable(context.getResources().getDrawable(imageCategory.get(position)));
                    }
                });


            }

        } catch (Exception e){

            Glide.with(context)
                    .asBitmap()
                    .load(imageCategory.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(300, 200)).into(holder.iv);

        }

    }

    @Override
    public int getItemCount() {
        return titleCategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv;
        ImageView iv;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.text);
            iv= itemView.findViewById(R.id.image);
            itemView.setOnClickListener(view -> {


                String category = "";

                if(tv.getText().toString().contains("Bags")){
                    category = "bags-clutches-241ff3da6";
                }else if(tv.getText().toString().contains("Beauty")){
                    category = "beauty-bodycare-172ec0d98";
                }else if(tv.getText().toString().contains("Books")){
                    category = "books-f810ccc6a";
                }else if(tv.getText().toString().contains("Construction")){
                    category = "construction-materials-d113194a4";
                }else if(tv.getText().toString().contains("Decoration")){
                    category = "decoration-materials-8410044cf";
                }else if(tv.getText().toString().contains("Electronics")){
                    category = "electronics-and-appliance-5ce8939d4";
                }else if(tv.getText().toString().contains("Electric")){
                    category = "electric-parts-e69e50d2f";
                }else if(tv.getText().toString().contains("Event")){
                    category = "event-media-4c778a622";
                }else if(tv.getText().toString().contains("Food & Beverage")){
                    category = "food-beverage-d976e98aa";
                }else if(tv.getText().toString().contains("Burmese")){
                    category = "burmiz-products-d72f4aae7";
                }else if(tv.getText().toString().contains("Dhaka Bank")){
                    category = "dhaka-bank-s-m-e-bazar-e3c7b0e66";
                }else if(tv.getText().toString().contains("Food & Restaurants")){
                    category = "food-7144381ff";
                }else if(tv.getText().toString().contains("Furniture")){
                    category = "furniture-a0121d623";
                }else if(tv.getText().toString().contains("Glasses")){
                    category = "glasses-80fed353d";
                }else if(tv.getText().toString().contains("Grocery")){
                    category = "grocery-f93e6cdf1";
                }else if(tv.getText().toString().contains("Handmade")){
                    category = "handmade-2a7770404";
                }else if(tv.getText().toString().contains("Harvesting & Agriculture")){
                    category = "agriculture-2-db8bf1a1b";
                }else if(tv.getText().toString().contains("Health Care and Pharmaceutical")){
                    category = "health-care-and-pharmaceutical-461de4f12";
                }else if(tv.getText().toString().contains("Home & Living")){
                    category = "home-living-63562e055";
                }else if(tv.getText().toString().contains("Home Garden")){
                    category = "home-garden-6b4911201";
                }else if(tv.getText().toString().contains("Hotel Booking")){
                    category = "hotel-booking-95e415b49";
                }else if(tv.getText().toString().contains("Jewellery")){
                    category = "jewellery-0ac268845";
                }else if(tv.getText().toString().contains("Kids")){
                    category = "kids-be1ec41b2";
                }else if(tv.getText().toString().contains("Kitchen & Dining")){
                    category = "kitchen-dining-dining-5e34f4934";
                }else if(tv.getText().toString().contains("Leather Goods")){
                    category = "leather-goods-goods-6a0b80ea7";
                }else if(tv.getText().toString().contains("LP Gas")){
                    category = "lp-gas-f9f11d790";
                }else if(tv.getText().toString().contains("Machineries")){
                    category = "machineries-2f97319bf";
                }else if(tv.getText().toString().contains("Women")){
                    category = "women-3bcf0256d";
                } else if(tv.getText().toString().toLowerCase().contains("men")){
                    category = "men-fd29e91e8";
                }else if(tv.getText().toString().contains("Paints")){
                    category = "paints-e0476142d";
                }else if(tv.getText().toString().contains("Pet & Poultry Supplies")){
                    category = "pet-supplies-3320cabf0";
                }else if(tv.getText().toString().contains("Plastic made Products")){
                    category = "plastic-made-products-4fdf5f0db";
                }else if(tv.getText().toString().contains("Property")){
                    category = "property-23ca7d84e";
                }else if(tv.getText().toString().contains("Service")){
                    category = "service-cc45c2b85";
                }else if(tv.getText().toString().contains("Shoes")){
                    category = "shoes-4-e89d62c12";
                }else if(tv.getText().toString().contains("Sports")){
                    category = "sports-fcb1cb312";
                }else if(tv.getText().toString().contains("Stationaries")){
                    category = "stationaries-78d4dd73f";
                }else if(tv.getText().toString().contains("Vehicles & Parts")){
                    category = "vehicles-parts-2ab45393f";
                }else if(tv.getText().toString().contains("Voucher")){
                    category = "voucher";
                }else if(tv.getText().toString().contains("Watch & Clock")){
                    category = "watch-clock-5f60b3d5d";
                }




                Fragment fragment3 = new BrowseProductFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("slug", category);
                bundle.putString("category", category);

                fragment3.setArguments(bundle);

                FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(R.animator.slide_in_left,R.animator.abc_popup_exit, 0, 0);
                ft.replace(R.id.fragment_container, fragment3, category);
                ft.addToBackStack(category);
                ft.commit();



            });
        }
    }

    public void setFilter(ArrayList s,ArrayList i){
        titleCategory=new ArrayList<>();
        imageCategory=new ArrayList<>();
        titleCategory.addAll(s);
        imageCategory.addAll(i);
        notifyDataSetChanged();
    }

    public static String getTypeOfDrawable(int drawableId,Context context) {
        Drawable resImg = context.getResources().getDrawable(drawableId);
        return resImg.getClass().toString().replace("class android.graphics.drawable.","");
    }

}
