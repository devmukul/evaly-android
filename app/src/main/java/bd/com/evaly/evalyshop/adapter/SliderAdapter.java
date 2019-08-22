package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.List;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.fragment.BrandFragment;
import bd.com.evaly.evalyshop.fragment.ShopFragment;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private List<String> img,url;
    private int type = 1;
    FragmentTransaction ft;
    AppCompatActivity activity;

    public SliderAdapter(Context context, AppCompatActivity activity, List<String> img, List<String> url) {
        this.context = context;
        this.img = img;
        this.url=url;
        this.activity = activity;

        ft = activity.getSupportFragmentManager().beginTransaction();



    }

    public SliderAdapter(Context context, AppCompatActivity activity, List<String> img, List<String> url, int type) {
        this.context = context;
        this.img = img;
        this.url=url;
        this.type = type;
        this.activity = activity;

        ft = activity.getSupportFragmentManager().beginTransaction();
    }

    @Override
    public int getCount() {
        return img.size();
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
                    .load(img.get(position))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(900, 900))
                    .into(imageView);

        } else {

            Glide.with(context)
                    .asBitmap()
                    .load(img.get(position))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(3000, 900))
                    .into(imageView);
        }

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(url.size()==0){
                    return;
                }else if(url.get(position).equals("https://evaly.com.bd")){

                    Toast.makeText(context, "It's just a banner. No page to open.", Toast.LENGTH_LONG).show();

                    return;
                }


                String su[] = url.get(position).split("/");
                String extractedUrl = su[su.length-1]+"::"+su[su.length-2];

                String s[]= extractedUrl.split("::");


                if (s.length>1 && url.get(position).contains("evaly.com.bd")) {

                    if (s[1].equals("products")) {
                        Intent intent = new Intent(context, ViewProductActivity.class);
                        intent.putExtra("product_slug", s[0]);
                        context.startActivity(intent);
                    } else if (s[1].equals("shop")) {

                        Fragment fragment3 = new ShopFragment();

                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);

                        bundle.putString("shop_name", s[0]);

                        bundle.putString("shop_slug", s[0]);
                        bundle.putString("category", s[0]);

                        fragment3.setArguments(bundle);

                        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.abc_popup_exit, 0, 0);
                        ft.replace(R.id.fragment_container, fragment3, s[0]);
                        ft.addToBackStack(s[0]);
                        ft.commit();
                    } else if (s[1].equals("brands")) {

                        Fragment fragment3 = new BrandFragment();

                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putString("brand_slug", s[0]);
                        bundle.putString("category", s[0]);

                        fragment3.setArguments(bundle);


                        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.abc_popup_exit, 0, 0);
                        ft.replace(R.id.fragment_container, fragment3, s[0]);
                        ft.addToBackStack(s[0]);
                        ft.commit();
                    }
                } else {



                    new FinestWebView.Builder(activity)
                            .titleDefault("Evaly Advertisement")
                            .webViewBuiltInZoomControls(false)
                            .webViewDisplayZoomControls(false)
                            .dividerHeight(0)
                            .gradientDivider(false)
                            .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
                            .show(url.get(position));


                }

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