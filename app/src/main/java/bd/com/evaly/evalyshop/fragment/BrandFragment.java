package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.adapter.ShopCategoryAdapter;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.TransactionItem;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class BrandFragment extends Fragment {

    String slug="", title="", categoryString="", imgUrl="", categorySlug="";
    ImageView logo;
    TextView name,categoryName,address,number;
    StickyScrollView nestedSV;

    ShimmerFrameLayout shimmer;


    RecyclerView recyclerView;
    ShopCategoryAdapter adapter;
    ArrayList<TabsItem> itemList;

    View view;

    Context context;
    MainActivity mainActivity;

    ProductGrid productGrid;
    ImageView placeHolder;
    ProgressBar progressBar;

    private TextView categoryTitle;

    private TextView reset;

    int currentPage = 1;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    public BrandFragment(){
        // Required empty public constructor

    }


    RequestQueue rq;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_brand, container, false);

        context = getContext();
        mainActivity = (MainActivity) getActivity();

        rq = Volley.newRequestQueue(context);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        InitializeActionBar InitializeActionbar = new InitializeActionBar((LinearLayout) view.findViewById(R.id.header_logo), mainActivity, "brand");

        name = view.findViewById(R.id.name);
        categoryName = view.findViewById(R.id.categoryName);
        address = view.findViewById(R.id.address);
        number = view.findViewById(R.id.number);
        logo = view.findViewById(R.id.logo);
        placeHolder=view.findViewById(R.id.placeholder_image);
        progressBar=view.findViewById(R.id.progressBar);


        LinearLayout homeSearch=view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), GlobalSearchActivity.class);

                intent.putExtra("type", 1);

                startActivity(intent);


            }
        });


        itemList = new ArrayList<>();

        // type 4 means shop's category

        //adapter = new ShopCategoryAdapter(context,itemList, this);
       // recyclerView.setAdapter(adapter);


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

        Glide.with(getContext())
                .load(imgUrl)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
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

        // getSubCategories(currentPage);

        getProductCount(slug);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        CoordinatorLayout rootLayout = view.findViewById(R.id.root_coordinator);

        NestedScrollView nestedSV = view.findViewById(R.id.stickyScrollView);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";

//
//                    if (oldScrollY-scrollY >= 300) {
//                        // Log.i(TAG, "Scroll UP");
//
//
//                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//                        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//                        if (behavior != null && behavior.getTopAndBottomOffset() !=0) {
//                            behavior.setTopAndBottomOffset(0);
//                            appBarLayout.setExpanded(true, true);
//                        }
//
//                    }


                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        try{
                            productGrid.loadNextBrandProducts();
                        }catch(Exception e){
                            Log.e("scroll error", e.toString());
                        }
                    }
                }
            });
        }
//
//
//        reset = view.findViewById(R.id.resetBtn);
//
//        reset.setVisibility(View.GONE);
//
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                showProductsByCategory("All Products", "", 0);
//
//
//                reset.setVisibility(View.GONE);
//            }
//        });
    }

    public void getProductCount(String slug){

        String url = "https://api.evaly.com.bd/core/public/products/?page=1&limit=12&brand="+slug;
        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        if(response.getInt("count")>0){
                            productGrid = new ProductGrid(mainActivity, (RecyclerView) view.findViewById(R.id.products), slug, categorySlug, 2, view.findViewById(R.id.progressBar));
                        }else{



                            LinearLayout noItem = view.findViewById(R.id.noItem);
                            noItem.setVisibility(View.VISIBLE);

                            Glide.with(getContext())
                                    .load(R.drawable.ic_emptycart)
                                    .apply(new RequestOptions().override(600, 600))
                                    .into(placeHolder);


                            progressBar.setVisibility(View.GONE);
                           // Toast.makeText(context, "No product is available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rq = Volley.newRequestQueue(getContext());
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        rq.add(request);
    }

    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public void showProductsByCategory(String categoryName, String categorySlug, int position){

        reset.setVisibility(View.VISIBLE);
        categoryTitle.setText(categoryName);

        productGrid = new ProductGrid(mainActivity, (RecyclerView) view.findViewById(R.id.products), slug, categorySlug, 1, view.findViewById(R.id.progressBar));



    }


    public void getSubCategories(int currentPage){

        String url = "https://api-prod.evaly.com.bd/api/categories/?products__items__shop_items__shop__slug="+slug+"&page="+currentPage;

        Log.d("json", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.d("category_brands",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);


                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("name"));
                            tabsItem.setImage(ob.getString("thumbnail"));
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategory(slug);

                            itemList.add(tabsItem);

                            adapter.notifyItemInserted(itemList.size());

                        }

                        try {

                            shimmer.stopShimmer();
                        } catch (Exception e){

                        }
                        shimmer.setVisibility(View.GONE);


                        loading = true;


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "brand_error", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // RequestQueue rq = Volley.newRequestQueue(context);
        request.setShouldCache(false);

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                getSubCategories(currentPage);

            }
        });
        rq.add(request);
    }





}
