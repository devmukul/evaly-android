package bd.com.evaly.evalyshop.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.ReviewsActivity;
import bd.com.evaly.evalyshop.adapter.ShopCategoryAdapter;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.TransactionItem;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class ShopFragment extends Fragment {

    String slug="", title="", groups="";
    ImageView logo;
    TextView name,address,number,tvOffer,followText;
    StickyScrollView nestedSV;
    ShimmerFrameLayout shimmer;
    RecyclerView recyclerView;
    ShopCategoryAdapter adapter;
    ArrayList<TabsItem> itemList;
    LinearLayout callButton,location,link,reviews,share,followBtn;
    View view;
    Context context;
    MainActivity mainActivity;
    ProductGrid productGrid;
    private TextView categoryTitle;
    private TextView reset;
    int currentPage = 1;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ImageView placeholder;
    ProgressBar progressBar;
    String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";
    RequestQueue rq;
    UserDetails userDetails;



    public ShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop, container, false);
        context = getContext();
        mainActivity = (MainActivity) getActivity();
        rq = Volley.newRequestQueue(context);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitializeActionBar InitializeActionbar = new InitializeActionBar((LinearLayout) view.findViewById(R.id.header_logo), mainActivity, "shop");
        LinearLayout homeSearch=view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GlobalSearchActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });
        name = view.findViewById(R.id.name);
        tvOffer = view.findViewById(R.id.tvOffer);
        address = view.findViewById(R.id.address);
        number = view.findViewById(R.id.number);
        logo = view.findViewById(R.id.logo);
        shimmer = view.findViewById(R.id.shimmer);
        callButton=view.findViewById(R.id.call_button);
        location=view.findViewById(R.id.location);
        link = view.findViewById(R.id.link);
        reviews = view.findViewById(R.id.reviews);
        share = view.findViewById(R.id.share_btn);
        followText = view.findViewById(R.id.follow_text);

        placeholder=view.findViewById(R.id.placeholder_image);
        progressBar=view.findViewById(R.id.progressBar);
        categoryTitle = view.findViewById(R.id.categoryTitle);
        followBtn = view.findViewById(R.id.follow_btn);

        userDetails = new UserDetails(context);

        try {

            shimmer.startShimmer();
        } catch (Exception e){

        }


        recyclerView = view.findViewById(R.id.categoriesRecycler);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dx >0) //check for scroll down
                {
                    GridLayoutManager mLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount =itemList.size();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            getSubCategories(++currentPage);
                        }

                    }
                }
            }
        });




        title = getArguments().getString("shop_name");

        name.setText(title);

        itemList = new ArrayList<>();

        // type 4 means shop's category

        adapter = new ShopCategoryAdapter(context,itemList, this);
        recyclerView.setAdapter(adapter);
        Logger.d(getArguments());

        slug = getArguments().getString("shop_slug");

        if (groups != null && groups.equalsIgnoreCase("evaly1919")){
            tvOffer.setVisibility(View.VISIBLE);
        }else {
            tvOffer.setVisibility(View.GONE);
        }


        getShopProductCount();
        getShopData();
        getProductRating(slug);
        getSubCategories(currentPage);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        CoordinatorLayout rootLayout = view.findViewById(R.id.root_coordinator);

        NestedScrollView nestedSV = view.findViewById(R.id.stickyScrollView);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";


                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");

                        try {
                            productGrid.loadNextShopProducts();
                        }catch (Exception e){
                            Log.e("scroll error", e.toString());
                        }
                    }
                }
            });
        }

        reset = view.findViewById(R.id.resetBtn);

        reset.setVisibility(View.GONE);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showProductsByCategory("All Products", "", 0);
                reset.setVisibility(View.GONE);
            }
        });

    }


    public void showProductsByCategory(String categoryName, String categorySlug, int position){

        reset.setVisibility(View.VISIBLE);
        categoryTitle.setText(categoryName);
        productGrid = new ProductGrid(mainActivity, (RecyclerView) view.findViewById(R.id.products), slug, categorySlug, 1, view.findViewById(R.id.progressBar));

    }

    public void getShopProductCount(){



        String url  = "https://api.evaly.com.bd/core/public/shops/items/"+slug+"/?page="+currentPage;

        Log.d("shop_url",url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        //Log.d("shop_response_int",response.getInt("count")+"");

                        JSONObject data = response.getJSONObject("data");
                        JSONObject jsonObject = data.getJSONObject("shop");

                        boolean subscribed = data.getBoolean("subscribed");


                        if (subscribed)
                            followText.setText("Unfollow");
                        else
                            followText.setText("Follow");


                        if(response.getInt("count")>0){
                            productGrid = new ProductGrid(mainActivity, (RecyclerView) view.findViewById(R.id.products), slug, "", 1, view.findViewById(R.id.progressBar));


                            try {


                                followBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        subscribe();
                                    }
                                });



                            }catch (Exception e){

                            }



                            try {
                                name.setText(jsonObject.getString("name"));
                                address.setText(jsonObject.getString("address"));
                                try {
                                    Glide.with(context)
                                            .load(jsonObject.getString("logo_image"))
                                            .listener(new RequestListener<Drawable>() {
                                                          @Override
                                                          public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                              return false;
                                                          }
                                                          @Override
                                                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                              Bitmap bitmap = Utils.changeColor(((BitmapDrawable) resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                                              logo.setImageBitmap(bitmap);
                                                              return true;
                                                          }
                                                      }
                                            )
                                            .into(logo);
                                } catch (Exception e){

                                }

                                try{
                                    number.setText(jsonObject.getString("contact_number"));
                                    callButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String phone = "Not provided";
                                            try {
                                                phone = jsonObject.getString("contact_number");
                                                final Snackbar snackBar = Snackbar.make(view, phone+"", Snackbar.LENGTH_LONG);
                                                snackBar.setAction("Call", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        try {
                                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                                            intent.setData(Uri.parse("tel:" + jsonObject.getString("contact_number")));
                                                            startActivity(intent);
                                                        } catch (Exception e){}
                                                        snackBar.dismiss();
                                                    }
                                                });
                                                snackBar.show();

                                            } catch (JSONException e) {
                                                Toast.makeText(context, "Sorry shop number is not available", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    });



                                }catch(Exception e){
                                    number.setText("");
                                }

                                location.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String phone = "Not provided";
                                        try {
                                            phone = jsonObject.getString("address");
                                            final Snackbar snackBar = Snackbar.make(view, phone+"", Snackbar.LENGTH_LONG);
                                            snackBar.setAction("Copy", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    try {
                                                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                                        ClipData clip = ClipData.newPlainText("address", jsonObject.getString("address"));
                                                        clipboard.setPrimaryClip(clip);
                                                    } catch (Exception e){}

                                                    snackBar.dismiss();
                                                }
                                            });
                                            snackBar.show();

                                        } catch (JSONException e) {
                                            Toast.makeText(context, "Sorry shop number is not available", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }

                                    }
                                });


                                link.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String phone = "https://evaly.com.bd/";
                                        try {
                                            phone = "https://evaly.com.bd/shops/"+jsonObject.getString("slug");
                                            final Snackbar snackBar = Snackbar.make(view, phone+"", Snackbar.LENGTH_LONG);
                                            snackBar.setAction("Copy", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    try {
                                                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                                        ClipData clip = ClipData.newPlainText("Link", "https://evaly.com.bd/shops/"+jsonObject.getString("slug"));
                                                        clipboard.setPrimaryClip(clip);
                                                    } catch (Exception e){}

                                                    snackBar.dismiss();
                                                }
                                            });
                                            snackBar.show();

                                        } catch (JSONException e) {
                                            Toast.makeText(context, "Sorry shop link is not available", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }

                                    }
                                });



                                share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String shareURL = "https://evaly.com.bd/";

                                        try {
                                            shareURL = "https://evaly.com.bd/shops/"+jsonObject.getString("slug");
                                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                            sharingIntent.setType("text/plain");
                                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareURL);
                                            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
                                        } catch (Exception e) {
                                            Toast.makeText(context, "Can't share the shop.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });




                                reviews.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                            String shop_id = "98989";
                                            shop_id = slug;
                                            Intent intent = new Intent(context, ReviewsActivity.class);
                                            intent.putExtra("ratingJson", ratingJson);
                                            intent.putExtra("type", "shop");
                                            intent.putExtra("item_value", shop_id);
                                            startActivity(intent);

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {


                            ((TextView) view.findViewById(R.id.categoryTitle)).setText(" ");
                            LinearLayout noItem = view.findViewById(R.id.noItem);
                            noItem.setVisibility(View.VISIBLE);

                            try {
                                Glide.with(context)
                                        .load(R.drawable.ic_emptycart)
                                        .apply(new RequestOptions().override(600, 600))
                                        .into(placeholder);
                            } catch (Exception e ){

                            }

                            progressBar.setVisibility(View.GONE);

                           // Toast.makeText(context, "No product is available", Toast.LENGTH_SHORT).show();
                        }

                        JSONArray jsonArray = data.getJSONArray("groups");
                        if (jsonArray.length()>0){
                            JSONObject ob = jsonArray.getJSONObject(0);
                            String offers = ob.getString("slug");
                            if (offers != null && offers.equalsIgnoreCase("evaly1919")){
                                tvOffer.setVisibility(View.VISIBLE);
                            }else {
                                tvOffer.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");

                String userAgent;

                try {
                    userAgent = WebSettings.getDefaultUserAgent(context);
                } catch (Exception e) {
                    userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
                }



                headers.put("User-Agent", userAgent);
                return headers;
            }
        };;
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
        rq.add(request);
    }





    public void getProductRating(final String sku) {
        String url= "https://nsuer.club/evaly/reviews/?sku="+sku+"&type=shop&isRating=true";
        Log.d("json rating", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    Log.d("json varying", response.toString());
                    try {

                        ratingJson = response.toString();
                        double avg = response.getDouble("avg_ratings");
                        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                        TextView ratingsCount = view.findViewById(R.id.ratings_count);
                        int tratings = response.getInt("total_ratings");
                        ratingsCount.setText("("+tratings+")");
                        ratingBar.setRating((float)avg);

                    } catch (Exception e) {
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

    public void getShopData(){
    }

    public void getSubCategories(int currentPage){

        String url = "https://api.evaly.com.bd/core/public/shops/categories/"+slug+"/?page="+currentPage;

        Log.d("json", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        Log.d("category_brands",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);

                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("category_name"));
                            tabsItem.setImage(ob.getString("category_image"));
                            tabsItem.setSlug(ob.getString("category_slug"));
                            tabsItem.setCategory(slug);
                            itemList.add(tabsItem);
                            adapter.notifyItemInserted(itemList.size());

                        }

                        if(itemList.size()<4) {

                            GridLayoutManager mLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);

                            recyclerView.setLayoutManager(mLayoutManager);
                        }



                        if(itemList.size() < 1)
                            ((TextView)view.findViewById(R.id.catTitle)).setText(" ");



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




    public void subscribe(){


        String url="https://api.evaly.com.bd/core/shop-subscriptions";


        int requestMethod =  Request.Method.POST;

        if (followText.getText().toString().equals("Unfollow")) {
            requestMethod =  Request.Method.DELETE;
            followText.setText("Follow");
            url = "https://api.evaly.com.bd/core/unsubscribe-shop/" + slug + "/";
        } else
            followText.setText("Unfollow");

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("shop_slug", slug);
        } catch (Exception e) {
        }



        JsonObjectRequest request = new JsonObjectRequest(requestMethod, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("onResponse", response.toString());

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");

                String userAgent;

                try {
                    userAgent = WebSettings.getDefaultUserAgent(context);
                } catch (Exception e) {
                    userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
                }



                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }





}
