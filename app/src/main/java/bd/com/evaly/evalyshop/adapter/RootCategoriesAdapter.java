package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.fragment.BrowseProductFragment;

public class RootCategoriesAdapter extends RecyclerView.Adapter<RootCategoriesAdapter.MyViewHolder>{

    Context context;
    List<CategoryEntity> itemList;

    public RootCategoriesAdapter(Context ctx, List<CategoryEntity> list){
        context=ctx;
        itemList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vector_home_category,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv.setText(itemList.get(position).getName());
        holder.view.setTag(position);
        holder.view.setOnClickListener(clickListener);

        int drawableID = getDrawableFromName(itemList.get(position).getName());

        try {

            if (drawableID == 0) {
                Glide.with(context)
                        .asBitmap()
                        .load(itemList.get(position).getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(300, 200)).into(holder.iv);
            }
            else {

                if (itemList.get(position).getName().contains("Dhaka") ||  itemList.get(position).getName().contains("Burmese")){
                    Glide.with(context)
                            .asBitmap()
                            .load(drawableID)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(new RequestOptions().override(300, 200)).into(holder.iv);
                } else {
                    holder.iv.post(() -> holder.iv.setImageDrawable(context.getResources().getDrawable(drawableID)));
                }
            }

        } catch (Exception e){

            Glide.with(context)
                    .asBitmap()
                    .load(drawableID)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(300, 200)).into(holder.iv);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            int p = (int) view.getTag();

            String category = itemList.get(p).getSlug();

            Fragment fragment3 = new BrowseProductFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            bundle.putString("slug", category);
            bundle.putString("category", category);

            fragment3.setArguments(bundle);

            FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();

            //ft.setCustomAnimations(R.animator.slide_in_left,R.animator.abc_popup_exit, 0, 0);
            ft.replace(R.id.fragment_container, fragment3, category);
            ft.addToBackStack(category);
            ft.commit();


        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv;
        ImageView iv;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv =itemView.findViewById(R.id.text);
            iv = itemView.findViewById(R.id.image);
            view = itemView;
        }
    }


    private int getDrawableFromName(String name){

        int drawable = 0;

        if(name.contains("Bags"))
            drawable = R.drawable.ic_bags_set;
        else if(name.contains("Beauty"))
            drawable = R.drawable.ic_color_lotion;
        else if(name.contains("Books"))
            drawable = R.drawable.ic_color_books;
        else if(name.contains("Construction"))
            drawable = R.drawable.ic_color_construction;
        else if(name.contains("Decoration"))
            drawable = R.drawable.ic_color_decoration;
        else if(name.contains("Electronics"))
            drawable = R.drawable.ic_color_multiple_devices;
        else if(name.contains("Electric"))
            drawable = R.drawable.ic_color_electric;
        else if(name.contains("Event"))
            drawable = R.drawable.ic_color_event;
        else if(name.contains("Food & Beverage"))
            drawable = R.drawable.ic_color_beverage;
        else if(name.contains("Burmese"))
            drawable = R.drawable.burmes_item;
        else if(name.contains("Dhaka Bank"))
            drawable = R.drawable.dhaka_bank_logo;
        else if(name.contains("Food & Restaurants"))
            drawable = R.drawable.ic_color_food_plate;
        else if(name.contains("Furniture"))
            drawable = R.drawable.ic_color_sliding_door_closet;
        else if(name.contains("Glasses"))
            drawable = R.drawable.ic_color_glasses_new;
        else if(name.contains("Grocery"))
            drawable = R.drawable.ic_color_ingredients;
        else if(name.contains("Handmade"))
            drawable = R.drawable.ic_color_potters_wheel;
        else if(name.contains("Harvesting & Agriculture"))
            drawable = R.drawable.ic_color_harvest;
        else if(name.contains("Health"))
            drawable = R.drawable.ic_color_health_checkup_1;
        else if(name.contains("Home & Living"))
            drawable = R.drawable.ic_color_open_curtains;
        else if(name.contains("Home Garden"))
            drawable = R.drawable.ic_color_orchid;
        else if(name.contains("Hotel"))
            drawable = R.drawable.ic_color_hotel_building;
        else if(name.contains("Jewellery"))
            drawable = R.drawable.ic_color_jewelry;
        else if(name.contains("Kids"))
            drawable = R.drawable.ic_color_kids;
        else if(name.contains("Kitchen & Dining"))
            drawable = R.drawable.ic_color_kitchen;
        else if(name.contains("Leather Goods"))
            drawable = R.drawable.ic_color_jacket_bag;
        else if(name.contains("LP Gas"))
            drawable = R.drawable.ic_color_gas;
        else if(name.contains("Machineries"))
            drawable = R.drawable.ic_color_sewing_machine;
        else if(name.contains("Women"))
            drawable = R.drawable.female_fashion;
        else if(name.toLowerCase().contains("men"))
            drawable = R.drawable.men_fashion;
        else if(name.contains("Paints"))
            drawable = R.drawable.ic_color_paint_bucket;
        else if(name.contains("Pet & Poultry Supplies"))
            drawable = R.drawable.ic_color_dog;
        else if(name.contains("Plastic made Products"))
            drawable = R.drawable.ic_color_bucket;
        else if(name.contains("Property"))
            drawable = R.drawable.ic_color_building;
        else if(name.contains("Service"))
            drawable = R.drawable.ic_color_maintenance;
        else if(name.contains("Shoes"))
            drawable = R.drawable.ic_color_new_shoes2;
        else if(name.contains("Sports"))
            drawable = R.drawable.ic_color_sports;
        else if(name.contains("Stationeries"))
            drawable = R.drawable.ic_color_pot;
        else if(name.contains("Vehicles & Parts"))
            drawable = R.drawable.ic_color_vehicles;
        else if(name.contains("Voucher"))
            drawable = 0;
        else if(name.contains("Watch & Clock"))
            drawable = R.drawable.ic_color_wathces;
        else
            drawable = 0;

        return drawable;

    }


}
