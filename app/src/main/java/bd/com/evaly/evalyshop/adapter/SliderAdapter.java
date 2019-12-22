package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.CampaignShopActivity;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.models.BannerItem;
import bd.com.evaly.evalyshop.util.Utils;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private List<BannerItem> itemList;
    private int type = 1;
    FragmentTransaction ft;
    AppCompatActivity activity;

    public SliderAdapter(Context context, AppCompatActivity activity, List<BannerItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.activity = activity;
        ft = activity.getSupportFragmentManager().beginTransaction();
    }

    public SliderAdapter(Context context, AppCompatActivity activity, List<BannerItem> itemList, int type) {
        this.context = context;
        this.itemList = itemList;
        this.type = type;
        this.activity = activity;
        ft = activity.getSupportFragmentManager().beginTransaction();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        view = inflater.inflate(R.layout.item_slider, null);

        ImageView imageView = view.findViewById(R.id.sliderImage);

        if(activity instanceof ViewProductActivity){
            Glide.with(context)
                    .asBitmap()
                    .load(itemList.get(position).getImage())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(900, 900))
                    .into(imageView);

        } else {

            Glide.with(context)
                    .asBitmap()
                    .load(itemList.get(position).getImage())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(3000, 900))
                    .into(imageView);
        }

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        view.setOnClickListener(v -> {


            if(itemList.get(position).getSlug().equals("")){
                return;
            }else if(itemList.get(position).getUrl().equals("https://evaly.com.bd")){

                Toast.makeText(context, "It's just a banner. No page to open.", Toast.LENGTH_LONG).show();

                return;
            }


            if (itemList.get(position).getUrl().contains("shops-grand-brand-days")){

                Intent ni = new Intent(context, CampaignShopActivity.class);
                ni.putExtra("title", "Grand Brand Days");
                ni.putExtra("slug", "grandbranddays");
                context.startActivity(ni);

                return;

            }


            if (itemList.get(position).getUrl().contains("evaly.com.bd/shops")) {

                    String[] ar = itemList.get(position).getUrl().split("/shops/");

                    if (ar.length > 1) {

                        String slug = ar[1];
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putString("shop_name", itemList.get(position).getName());
                        bundle.putString("shop_slug", slug);
                        bundle.putString("category", "root");

                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.shopFragment, bundle);

                    } else {

                        Toast.makeText(context, "It's just a banner. No page to open.", Toast.LENGTH_LONG).show();

                    }

            } else {


                Utils.CustomTab(itemList.get(position).getUrl(),  context);
            }

        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

}