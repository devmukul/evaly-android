package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.image.ImageSliderActivity;
import bd.com.evaly.evalyshop.util.ImagePreview;

public class ViewProductSliderAdapter extends PagerAdapter {

    FragmentTransaction ft;
    AppCompatActivity activity;
    private Context context;
    private ArrayList<String> img;
    private int type = 1;

    public ViewProductSliderAdapter(Context context, AppCompatActivity activity, ArrayList<String> img) {
        this.context = context;
        this.img = img;
        this.activity = activity;
        ft = activity.getSupportFragmentManager().beginTransaction();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
                .placeholder(R.drawable.ic_evaly_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(900, 900))
                .into(imageView);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        view.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, ImageSliderActivity.class);
                intent.putStringArrayListExtra("image_list", img);
                //Toast.makeText(context, imgURL, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "High resolution image not available.", Toast.LENGTH_SHORT).show();
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
