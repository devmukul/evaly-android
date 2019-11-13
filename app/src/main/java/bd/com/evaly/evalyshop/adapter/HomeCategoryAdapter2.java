package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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
import bd.com.evaly.evalyshop.models.category.CategoryItem;

public class HomeCategoryAdapter2 extends RecyclerView.Adapter<HomeCategoryAdapter2.MyViewHolder>{

    Context context;
    ArrayList<CategoryItem> itemList;

    public HomeCategoryAdapter2(Context ctx,ArrayList<CategoryItem> list){
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

        try {

            if (itemList.get(position).getDrawable() == 0) {
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
                            .load(itemList.get(position).getDrawable())
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(new RequestOptions().override(300, 200)).into(holder.iv);
                } else {
                    holder.iv.post(new Runnable() {
                        public void run() {
                            holder.iv.setImageDrawable(context.getResources().getDrawable(itemList.get(position).getDrawable()));
                        }
                    });
                }
            }

        } catch (Exception e){

            Glide.with(context)
                    .asBitmap()
                    .load(itemList.get(position).getDrawable())
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


    public static String getTypeOfDrawable(int drawableId,Context context) {
        Drawable resImg = context.getResources().getDrawable(drawableId);
        return resImg.getClass().toString().replace("class android.graphics.drawable.","");
    }

}
