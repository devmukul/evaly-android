package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.adapter.ShopCategoryAdapter;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.util.Utils;

public class BrandFragment extends Fragment {

    private String slug="", title="", categoryString="", imgUrl="", categorySlug="";
    private ImageView logo;
    private TextView name,categoryName,address,number;
    private NestedScrollView nestedSV;
    private ShopCategoryAdapter adapter;
    private View view;
    private Context context;
    private MainActivity mainActivity;
    private ProductGrid productGrid;
    private ImageView placeHolder;
    private ProgressBar progressBar;

    public BrandFragment(){
        // Required empty public constructor

    }


    private void refreshFragment(){
        if (getFragmentManager() != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFragmentManager().beginTransaction().detach(this).commitNow();
                getFragmentManager().beginTransaction().attach(this).commitNow();
            } else {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_brand, container, false);

        context = getContext();
        mainActivity = (MainActivity) getActivity();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }
                @Override
                public void onBackPress() {
                    if (getFragmentManager() != null)
                        NavHostFragment.findNavController(BrandFragment.this).navigate(R.id.homeFragment);

                }
            });


        new InitializeActionBar( view.findViewById(R.id.header_logo), mainActivity, "brand");

        name = view.findViewById(R.id.name);
        categoryName = view.findViewById(R.id.categoryName);
        address = view.findViewById(R.id.address);
        number = view.findViewById(R.id.number);
        logo = view.findViewById(R.id.logo);
        placeHolder=view.findViewById(R.id.placeholder_image);


        LinearLayout homeSearch=view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        slug = getArguments().getString("brand_slug");
        title = getArguments().getString("brand_name");
        categorySlug = getArguments().getString("category");

        if(categorySlug.equals("root"))
            categoryString = "All Categories";

        else {
            categoryString = categorySlug.replace('-', ' ');
            categoryString = capitalize(categoryString);
            categoryString = categoryString.replaceAll("\\w+$", "");
        }

        imgUrl = getArguments().getString("image_url");

        name.setText(title);
        categoryName.setText(categoryString);

        if (getContext() != null)
            Glide.with(getContext())
                    .load(imgUrl)
                    .listener(new RequestListener<Drawable>() {
                                  @Override
                                  public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                      return false;
                                  }
                                  @Override
                                  public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                      Bitmap bitmap = Utils.changeColor(((BitmapDrawable)resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                      logo.setImageBitmap(bitmap);
                                      return true;
                                  }
                              }
                    )
                    .into(logo);

        nestedSV = view.findViewById(R.id.stickyScrollView);
        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    try{
                        productGrid.loadNextBrandProducts();
                    }catch(Exception e){
                        Log.e("scroll error", e.toString());
                    }
                }
            });
        }


        productGrid = new ProductGrid(mainActivity, view.findViewById(R.id.products), slug, categorySlug, "",2, view.findViewById(R.id.progressBar));
        productGrid.setScrollView(nestedSV);
        productGrid.setListener(new ProductListener() {
            @Override
            public void onSuccess(int count) {
                if(count==0){
                    LinearLayout noItem = view.findViewById(R.id.noItem);
                    noItem.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void buyNow(String product_slug) {

            }
        });



    }


    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }



}
