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
import bd.com.evaly.evalyshop.activity.ImagePreview;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.fragment.BrandFragment;
import bd.com.evaly.evalyshop.fragment.ShopFragment;

public class ViewProductSliderAdapter extends PagerAdapter {

    private Context context;
    private List<String> img,url;
    private int type = 1;
    FragmentTransaction ft;
    AppCompatActivity activity;

    public ViewProductSliderAdapter(Context context, AppCompatActivity activity, List<String> img, List<String> url) {
        this.context = context;
        this.img = img;
        this.url=url;
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
        view = inflater.inflate(R.layout.view_product_item_slider, null);
        ImageView imageView = view.findViewById(R.id.sliderImage);
        final String imgURL = img.get(position);

        Glide.with(context)
                .asBitmap()
                .load(img.get(position))
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(900, 900))
                .into(imageView);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, ImagePreview.class);
                    intent.putExtra("image", imgURL);
                    //Toast.makeText(context, imgURL, Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(context, "High resolution image not available.", Toast.LENGTH_SHORT).show();
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
