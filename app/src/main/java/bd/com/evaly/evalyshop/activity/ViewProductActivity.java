package bd.com.evaly.evalyshop.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.AvailableShopAdapter;
import bd.com.evaly.evalyshop.adapter.SpecificationAdapter;
import bd.com.evaly.evalyshop.adapter.ViewProductSliderAdapter;
import bd.com.evaly.evalyshop.models.ProductVariants;
import bd.com.evaly.evalyshop.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.reviewratings.RatingReviews;
import bd.com.evaly.evalyshop.util.Data;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.models.WishList;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;
import bd.com.evaly.evalyshop.models.AvailableShop;
import bd.com.evaly.evalyshop.models.CartItem;
import bd.com.evaly.evalyshop.models.Products;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;
import bd.com.evaly.evalyshop.views.SliderViewPager;


import java.util.TreeMap;


public class ViewProductActivity extends BaseActivity {

    ImageView back;
    TextView productName, sku, description;
    String slug = "", category = "", name = "";
    ArrayList<Products> products;
    NestedScrollView nestedSV;
    ArrayList<AvailableShop> availableShops;
    AvailableShopAdapter adapter;
    RecyclerView recyclerView;
    SliderViewPager sliderPager;
    TabLayout sliderIndicator;
    ArrayList<String> sliderImages;
    ViewProductSliderAdapter sliderAdapter;
    RadioGroup ll;
    Map<String, String> map, shopMap;
    LinearLayout colorRel;


    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;


    View specView, descriptionView;
    RelativeLayout specRel, descriptionRel;
    DbHelperCart db;

    CartItem cartItem;

    DbHelperWishList dbWish;
    LinearLayout productHolder;
    LinearLayout stickyButtons;
    TextView relatedTitle;


    int buttonCount = 0;

    String productJson = "{}";

    Context context;
    WishList wishListItem;
    boolean isAddedToWishList;
    RatingReviews ratingReviews;
    int raters[];
    RecyclerView specList;
    ArrayList<String> specTitle, specValue;
    SpecificationAdapter specificationAdapter;
    String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";

    TextView viewAllReviews;

    boolean isShopLoading = false, callFirst = false;


    String shareURL = "https://evaly.com.bd";
    TreeMap<String, TreeMap<String, String>> varyingMap;
    LinearLayout variationParentLayout;
    int buttonID = 0;
    String shopURL = "";
    ArrayList<String> parameters, values;
    RequestQueue rqShop;

    RequestQueue rq;
    Map<Integer, ProductVariants> productVariantsMap;
    ArrayList<Integer> buttonIDs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        ratingReviews = findViewById(R.id.rating_reviews);
        viewAllReviews = findViewById(R.id.viewAllReviews);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.z_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setTitle("View Product");

        rqShop = Volley.newRequestQueue(context);
        rq = Volley.newRequestQueue(context);

        ImageView shareBtn = findViewById(R.id.share);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareURL);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
                } catch (Exception e) {
                    Toast.makeText(context, "Can't share the product.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        wishListItem = new WishList();
        cartItem = new CartItem();
        db = new DbHelperCart(context);

        dbWish = new DbHelperWishList(context);
        varyingMap = new TreeMap<>(Collections.reverseOrder());
        parameters = new ArrayList<>();
        values = new ArrayList<>();
        shopMap = new HashMap<>();
        productVariantsMap = new HashMap<>();
        buttonIDs = new ArrayList<>();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setLightStatusBar(this);

        appBarLayout = findViewById(R.id.appBar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        coordinatorLayout = findViewById(R.id.rootView);
        variationParentLayout = findViewById(R.id.variationParent);

        specList = findViewById(R.id.spec_list);
        specList.setLayoutManager(new LinearLayoutManager(this));
        specTitle = new ArrayList<>();
        specValue = new ArrayList<>();
        specificationAdapter = new SpecificationAdapter(this, specTitle, specValue);
        specList.setAdapter(specificationAdapter);

        // productImage=findViewById(R.id.image);
        productName = findViewById(R.id.product_name);
        recyclerView = findViewById(R.id.available);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        sku = findViewById(R.id.sku);
        description = findViewById(R.id.specText);
        nestedSV = findViewById(R.id.sticky_scroll);
        sliderPager = findViewById(R.id.sliderPager);
        sliderIndicator = findViewById(R.id.sliderIndicator);
        colorRel = findViewById(R.id.color_rel);
        back = findViewById(R.id.back);
        specView = findViewById(R.id.spec_view);
        specRel = findViewById(R.id.spec_rel);
        descriptionRel = findViewById(R.id.description_rel);
        descriptionView = findViewById(R.id.description_view);
        products = new ArrayList<>();
        availableShops = new ArrayList<>();
        sliderImages = new ArrayList<>();
        map = new TreeMap<>();

        productHolder = findViewById(R.id.productInfo);
        stickyButtons = findViewById(R.id.stickyButtons);
        relatedTitle = findViewById(R.id.relatedTitle);


        sliderAdapter = new ViewProductSliderAdapter(context, this, sliderImages, new ArrayList<>());
        sliderPager.setAdapter(sliderAdapter);
        sliderIndicator.setupWithViewPager(sliderPager, true);


        //adapter = new AvailableShopAdapter(context, findViewById(R.id.rootView),availableShops, db, cartItem);
        //recyclerView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            slug = extras.getString("product_slug");

            name = extras.getString("product_name");


            productName.setVisibility(View.GONE);

            productName.setText(Html.fromHtml(name));

            collapsingToolbarLayout.setTitle(Html.fromHtml(name));

            collapsingToolbarLayout.setVisibility(View.INVISIBLE);

            getProductData(slug);
            //getProductImages(slug);

            //getAvailableShops(slug, null);
            //getCategoryProducts();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // show related products
        //ProductGrid productGrid = new ProductGrid(context, (RecyclerView) findViewById(R.id.products));

        hideProductHolder();

        cartCount();


        LinearLayout cartPage = findViewById(R.id.cart);
        cartPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);

            }


        });

        final ImageView addToWishList = findViewById(R.id.addToWishlist);
        addToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar calendar = Calendar.getInstance();

                int price = 0;

                try {
                    price = Integer.parseInt(wishListItem.getPrice());


                } catch (Exception e) {

                }


                if (isAddedToWishList) {

                    addToWishList.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                    dbWish.deleteDataBySlug(wishListItem.getProductSlug());

                    Toast.makeText(context, "Removed from wish list", Toast.LENGTH_SHORT).show();

                    isAddedToWishList = false;

                } else {

                    if (dbWish.insertData(wishListItem.getProductSlug(), wishListItem.getName(), wishListItem.getImage(), price, calendar.getTimeInMillis())) {
                        Toast.makeText(context, "Added to wish list", Toast.LENGTH_SHORT).show();
                        addToWishList.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                    }

                    isAddedToWishList = true;

                }

            }


        });

        TextView addToCart = findViewById(R.id.addToCart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.sticky_scroll);
                scrollView.postDelayed(new Runnable() {
                    public void run() {


                        appBarLayout.setExpanded(false, true);

                        TextView availableShopTitle = findViewById(R.id.avlshop);
                        int scrollTo = ((View) availableShopTitle.getParent()).getTop() + availableShopTitle.getTop();
                        scrollView.smoothScrollTo(0, scrollTo - 30);
                    }
                }, 100);

            }


        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            dbWish.close();
            db.close();
            // finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            cartCount();
            dbWish = new DbHelperWishList(context);
            db = new DbHelperCart(context);
        } catch (Exception e) {

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // destroy data here


        dbWish.close();
        db.close();

    }


    public void cartCount() {

        if (db.size() > 0) {

            TextView cartCounter = findViewById(R.id.cartCount);
            cartCounter.setText(db.size() + "");

        }


    }


    public void hideProductHolder() {

        productHolder.setVisibility(View.GONE);
        relatedTitle.setVisibility(View.GONE);
        ((ProgressBar) findViewById(R.id.progressBar)).setBackgroundColor(Color.parseColor("#ffffff"));
        stickyButtons.setVisibility(View.GONE);

    }


    public void showProductHolder() {

        productHolder.setVisibility(View.VISIBLE);
        relatedTitle.setVisibility(View.VISIBLE);
        stickyButtons.setVisibility(View.VISIBLE);
        ((ProgressBar) findViewById(R.id.progressBar)).setBackgroundColor(Color.parseColor("#fafafa"));

    }


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;   // add LIGHT_STATUS_BAR to flag
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {

            activity.getWindow().setStatusBarColor(Color.BLACK);


        }
    }


    public void getProductRating(final String sku) {


        String url = "https://nsuer.club/evaly/reviews/?sku=" + sku + "&type=product&isRating=true";
        Log.d("json rating", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {
                    Log.d("json varying", response.toString());
                    try {


                        ratingJson = response.toString();

                        viewAllReviews.setClickable(true);

                        ratingReviews = findViewById(R.id.rating_reviews);

                        int total_ratings = response.getInt("total_ratings");
                        double avg_ratings = response.getDouble("avg_ratings");
                        int star_5 = response.getInt("star_5");
                        int star_4 = response.getInt("star_4");
                        int star_3 = response.getInt("star_3");
                        int star_2 = response.getInt("star_2");
                        int star_1 = response.getInt("star_2");

                        int colors[] = new int[]{
                                Color.parseColor("#0e9d58"),
                                Color.parseColor("#0e9d58"),
                                Color.parseColor("#a6ba5d"),
                                Color.parseColor("#ef7e14"),
                                Color.parseColor("#d36259")};

                        int raters[] = new int[]{
                                star_5,
                                star_4,
                                star_3,
                                star_2,
                                star_1
                        };


                        ((TextView) findViewById(R.id.rating_average)).setText(avg_ratings + "");
                        ((TextView) findViewById(R.id.rating_counter)).setText(total_ratings + " ratings");
                        ((RatingBar) findViewById(R.id.ratingBar)).setRating((float) avg_ratings);


                        if (total_ratings == 0)
                            total_ratings = 1;


                        ratingReviews.createRatingBars(total_ratings, BarLabels.STYPE1, colors, raters);


                        viewAllReviews.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, ReviewsActivity.class);

                                intent.putExtra("ratingJson", ratingJson);

                                intent.putExtra("type", "product");

                                intent.putExtra("item_value", sku);

                                startActivity(intent);


                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // RequestQueue rq = Volley.newRequestQueue(context);
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

        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);


    }


    public void getProductData(String slug) {
        String url = UrlUtils.BASE_URL + "public/products/" + slug + "/";
        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                responseMain -> {
                    productName.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setVisibility(View.VISIBLE);
                    try {
                        Log.d("product_details", responseMain.toString());
                        JSONObject response = responseMain.getJSONObject("data");
                        JSONArray product_variants = response.getJSONArray("product_variants");

                        for (int i = 0; i < product_variants.length(); i++) {
                            if (response.getJSONArray("attributes").length() == 0) {

                            } else {

                                int attr = product_variants.getJSONObject(i).getJSONArray("attribute_values").length();
                                if (attr == 0) {
                                    product_variants.remove(i);
                                }

                            }
                        }

                        for (int i = 0; i < product_variants.length(); i++) {
                            if (response.getJSONArray("attributes").length() == 0) {
                                JSONArray variantArr = product_variants.getJSONObject(i).getJSONArray("product_images");
                                for (int j = 0; j < variantArr.length(); j++) {
                                    sliderImages.add(variantArr.getString(j));
                                    sliderAdapter.notifyDataSetChanged();
                                }
                                getAvailableShops(product_variants.getJSONObject(i).getInt("variant_id"));
                            } else {
                                JSONArray variantArr = product_variants.getJSONObject(i).getJSONArray("product_images");
                                int variantID = product_variants.getJSONObject(i).getInt("variant_id");
                                String name = product_variants.getJSONObject(i).getString("product_name");
                                int minPrice = product_variants.getJSONObject(i).getInt("min_price");
                                int maxPrice = product_variants.getJSONObject(i).getInt("max_price");
                                String description = product_variants.getJSONObject(i).getString("product_description");
                                String brandName = product_variants.getJSONObject(i).getString("brand_name");
                                ArrayList<String> productImages = new ArrayList<>();
                                for (int j = 0; j < variantArr.length(); j++) {
                                    productImages.add(variantArr.getString(j));
                                }

                                try {

                                    int attribute = product_variants.getJSONObject(i).getJSONArray("attribute_values").getInt(0);
                                    String colorImage = product_variants.getJSONObject(i).getString("color_image");
                                    ProductVariants productVariants = new ProductVariants(variantID, minPrice, maxPrice, attribute, name,
                                            description, brandName, colorImage, productImages);
                                    productVariantsMap.put(attribute, productVariants);

                                } catch (Exception e) {

                                }
                            }
                        }

                        JSONArray attributes = response.getJSONArray("attributes");
                        for (int i = 0; i < attributes.length(); i++) {
                            String caption = attributes.getJSONObject(i).getString("attribute_name");
                            JSONArray attributeValues = attributes.getJSONObject(i).getJSONArray("attribute_values");
                            ArrayList<String> attributeValuesArr = new ArrayList<>();
                            ArrayList<Integer> attributeKeysArr = new ArrayList<>();
                            for (int j = 0; j < attributeValues.length(); j++) {
                                attributeValuesArr.add(attributeValues.getJSONObject(j).getString("value"));
                                attributeKeysArr.add(attributeValues.getJSONObject(j).getInt("key"));
                            }
                            addButtons(caption, attributeValuesArr, attributeKeysArr);
                            colorRel.setVisibility(View.VISIBLE);
                        }

                        JSONObject firstVariant = product_variants.getJSONObject(0);
                        productName.setText(Html.fromHtml(firstVariant.getString("product_name")));
                        if (firstVariant.getString("product_description").equals("")) {
                            descriptionView.setVisibility(View.GONE);
                            descriptionRel.setVisibility(View.GONE);
                        } else {
                            description.setText(firstVariant.getString("product_description"));
                        }

                        JSONArray productSpecifications = response.getJSONArray("product_specifications");
                        if (productSpecifications.length() == 0) {
                            specView.setVisibility(View.GONE);
                            specRel.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < productSpecifications.length(); i++) {
                            specTitle.add(productSpecifications.getJSONObject(i).getString("specification_name"));
                            specValue.add(productSpecifications.getJSONObject(i).getString("specification_value"));
                            specificationAdapter.notifyItemInserted(specTitle.size());
                        }

                        Log.d("json product", responseMain.toString());


                        if (dbWish.isSlugExist(slug)) {

                            ImageView addToWishList = findViewById(R.id.addToWishlist);
                            addToWishList.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                            isAddedToWishList = true;

                        }


                        productJson = firstVariant.toString();

                        showProductHolder();


                        // for cart order
                        cartItem.setId("0");
                        cartItem.setName(firstVariant.getString("product_name"));
                        cartItem.setImage(firstVariant.getString("color_image"));
                        cartItem.setSlug(slug);

                        // for wishlist

                        int price = 0;
                        try {
                            price = (int) Double.parseDouble(firstVariant.getString("min_price"));
                        } catch (Exception e) {

                        }

                        wishListItem.setId("0");
                        wishListItem.setName(firstVariant.getString("product_name"));
                        //wishListItem.setImage(firstVariant.getString("thumbnail"));
                        wishListItem.setImage(firstVariant.getJSONArray("product_images").getString(0));
                        wishListItem.setProductSlug(slug);
                        wishListItem.setPrice(String.valueOf(price));


                        collapsingToolbarLayout.setTitle(firstVariant.getString("product_name"));

                        sku.setText(firstVariant.getString("variant_id"));


//                        getProductRating(firstVariant.getString("variant_id"));


//                        if(firstVariant.getString("description").equals("")){
//                            descriptionRel.setVisibility(View.GONE);
//                            descriptionView.setVisibility(View.GONE);
//                        }else{
//                            description.setText(response.getString("description"));
//                        }
                        //Glide.with(context).asBitmap().load(response.getString("thumbnail")).into(productImage);


                        sliderPager.setVisibility(View.VISIBLE);

                        //sliderImages.add(response.getString("thumbnail"));
                        //sliderAdapter.notifyDataSetChanged();

                        //JSONObject ob=response.getJSONObject("category");
                        //category=ob.getString("slug");
                        //getCategoryProducts();


                        //JSONObject obBrand=response.getJSONObject("brand");
                        //String brand =ob.getString("slug");

                        shareURL = "https://evaly.com.bd/products/" + slug;


//                        JSONArray varyingOptions=response.getJSONArray("varying_options");
//                        for(int i=0;i<product_variants.length();i++){
//                            JSONObject varyingOB=varyingOptions.getJSONObject(i);
//                            Log.d("varying_ob",varyingOB.toString());
//                            JSONArray variations=varyingOB.getJSONArray("variations");
//                            TreeMap<String,String> tempMap=new TreeMap<>(Collections.reverseOrder());
//                            for(int j=0;j<variations.length();j++){
//                                tempMap.put(variations.getJSONObject(j).getString("value"),variations.getJSONObject(j).getString("slug"));
//                                if(j==variations.length()-1){
//                                    varyingMap.put(varyingOB.getJSONObject("attribute").getString("name"),tempMap);
//                                }
//                            }
//                            map.put(varyingOB.getJSONObject("attribute").getString("name"),varyingOB.getJSONObject("attribute").getString("slug"));
//                            if(i==varyingOptions.length()-1){
//                                addButtons();
//                            }
//                        }


                        Random Dice = new Random();
                        int n = Dice.nextInt(Data.homeRandomCategory.length);
                        String defaultCategory = Data.homeRandomCategory[n];


                        ProductGrid productGrid = new ProductGrid(context, findViewById(R.id.products), defaultCategory, findViewById(R.id.progressBar));
                        if (nestedSV != null) {
                            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                    String TAG = "nested_sync";

                                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                        Log.i(TAG, "BOTTOM SCROLL");


                                        try {

                                            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

                                            productGrid.loadNextPage();
                                        } catch (Exception e) {


                                        }
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // RequestQueue rq = Volley.newRequestQueue(context);
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

        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);
    }


    public void addButtons(String caption, ArrayList<String> name, ArrayList<Integer> id) {
        Log.d("buttons_called", caption + "   " + name.size());
        TextView tv = new TextView(getApplicationContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
        textParams.setMargins(10, 10, 10, 10);
        tv.setLayoutParams(textParams);
        tv.setText(caption);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(16f);
        LinearLayout layout2 = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layParams.setMargins(20, 20, 20, 20);
        layout2.setLayoutParams(layParams);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.addView(tv);
        LinearLayout layout3 = new LinearLayout(getApplicationContext());
        layout3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        layout3.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < name.size(); i++) {
            Button button = new Button(getApplicationContext());
            button.setText(name.get(i));
            LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            buttonsParams.setMargins(10, 10, 10, 10);
            button.setLayoutParams(buttonsParams);
            button.setTypeface(null, Typeface.NORMAL);
            button.setFocusable(true);
            button.setTag(caption);
            button.setId(id.get(i));
            buttonIDs.add(id.get(i));
            //button.setTag(id.get(i));
            button.setBackgroundResource(R.drawable.variation_btn_default);

            GradientDrawable drawable = (GradientDrawable) button.getBackground();
            drawable.setColor(Color.parseColor("#eeeeee"));

            //button.setBackgroundColor(Color.parseColor("#eeeeee"));
            button.setStateListAnimator(null);
            button.setFocusableInTouchMode(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ProductVariants productVariants = productVariantsMap.get(button.getId());
                    sliderImages.clear();
                    sliderAdapter.notifyDataSetChanged();

                    sliderPager.setAdapter(null);

                    for (int j = 0; j < productVariants.getImages().size(); j++) {
                        sliderImages.add(productVariants.getImages().get(j));
                        sliderAdapter.notifyDataSetChanged();

                        Log.d("hmt", "yes clicked");
                    }

                    sliderPager.setAdapter(sliderAdapter);

                    sliderAdapter.notifyDataSetChanged();

                    for (int k = 0; k < buttonIDs.size(); k++) {
                        try {
                            Button btn = findViewById(buttonIDs.get(k));
                            if (btn.getTag().equals(button.getTag())) {
                                GradientDrawable drawable = (GradientDrawable) btn.getBackground();
                                drawable.setColor(Color.parseColor("#eeeeee"));
                            }
                        } catch (Exception e) {

                        }
                    }
                    GradientDrawable drawable = (GradientDrawable) button.getBackground();
                    drawable.setColor(Color.parseColor("#d1ecf2"));
                    getAvailableShops(productVariants.getVariantID());
                }
            });
            button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        v.performClick();
                    }
                }
            });
            if (!callFirst) {
                button.performClick();
                callFirst = true;
            }
            layout3.addView(button);
            //buttonID++;
        }
        layout2.addView(layout3);
        variationParentLayout.addView(layout2);
        //variationParentLayout.addView(variationLayout);
    }


    public void getAvailableShops(int variationID) {


        if (rqShop != null) {
            rqShop.cancelAll(this);
        }


        ((ProgressBar) findViewById(R.id.progressBarShop)).setVisibility(View.VISIBLE);


        availableShops.clear();
        recyclerView.setAdapter(null);


        isShopLoading = true;

        Log.d("json_shop", shopURL);
        shopURL = "https://api.evaly.com.bd/core/public/product/shops/" + variationID + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, shopURL, (String) null,
                response -> {

                    Logger.json(String.valueOf(response));
                    isShopLoading = false;
                    //adapter.notifyItemRangeRemoved(0, availableShops.size());
                    availableShops.clear();
                    AvailableShopAdapter adapterm = new AvailableShopAdapter(context, findViewById(R.id.rootView), availableShops, db, cartItem);
                    //recyclerView.setAdapter(null);
                    recyclerView.setAdapter(adapterm);
                    ArrayList<String> shopname = new ArrayList<>();
                    ((ProgressBar) findViewById(R.id.progressBarShop)).setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int ii = 0; ii < jsonArray.length(); ii++) {
                            try {
                                JSONObject ob = jsonArray.getJSONObject(ii);
                                String phone = "";
                                try {
                                    phone = ob.getString("contact_number");
                                } catch (Exception e) {
                                    phone = "Not Given";
                                }
                                boolean duplicateShop = false;
                                for (int j = 0; j < shopname.size(); j++) {
                                    if (shopname.get(j).equals(ob.getString("shop_name"))) {
                                        duplicateShop = true;
                                        break;
                                    }
                                }
                                if (!duplicateShop) {
                                    Log.d("check_shop", ob.toString());
                                    AvailableShop item = new AvailableShop();
                                    shopname.add(ob.getString("shop_name"));
                                    item.setName(ob.getString("shop_name"));
                                    item.setLogo(ob.getString("shop_image"));

                                    if (ob.getString("discounted_price").equals("null"))
                                        item.setPrice(ob.getString("price"));
                                    else
                                        item.setPrice(ob.getString("discounted_price"));

                                    item.setSlug(ob.getString("shop_slug"));
                                    item.setPhone(phone);
                                    item.setAddress(ob.getString("shop_address"));
                                    item.setShopJson(ob.toString());
                                    item.setStock(true);
                                    item.setMaximumPrice(ob.getString("price"));
                                    availableShops.add(item);
                                    adapterm.notifyItemInserted(availableShops.size());
                                }
//                                if(ii==jsonArray.length()-1){
//                                    Toast.makeText(this, ""+availableShops.size(), Toast.LENGTH_SHORT).show();
//                                    adapterm.notifyDataSetChanged();
//                                }
                            } catch (Exception e) {

                                Log.e("json expection", e.toString());

                                continue;

                            }
                        }
                        //adapterm.notifyDataSetChanged();
                        if (availableShops.size() < 1) {
                            LinearLayout empty = findViewById(R.id.empty);
                            empty.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
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


        request.setShouldCache(false);
        request.setTag(this);
        rqShop.getCache().clear();
        rqShop.add(request);
    }


}
