package bd.com.evaly.evalyshop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.fragment.HomeFragment;
import bd.com.evaly.evalyshop.util.ProductListItem;
import bd.com.evaly.evalyshop.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.util.UrlUtils;


public class ProductGrid {

    RecyclerView recyclerView;
    StaggeredGridLayoutManager mLayoutManager;
    String categorySlug;
    String shopSlug;
    public HomeFragment main;
    public Context context;
    ArrayList<ProductListItem> products;
    Map<String, String> mp;
    Map<String, ProductListItem> map;
    int current = 1, type = 0, maxPrice = 0, minPrice = 0;
    private ProductGridAdapter adapterViewAndroid;
    private ProgressBar progressBar;
    RequestQueue rq;

    private boolean isLoading = false;

    private ProductListener listener;

    public interface ProductListener {
        void onSuccess(int count);
    }


    public void setListener(ProductListener listener) {
        this.listener = listener;
    }


    public ProductGrid(Context contextz, RecyclerView recyclerView, ProgressBar progressBar) {
        this.context = contextz;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;


        rq = Volley.newRequestQueue(context);

    }


    public ProductGrid(Context contextz, RecyclerView recyclerView, String categorySlug, ProgressBar progressBar, ProductListener productListener) {
        this.context = contextz;
        this.recyclerView = recyclerView;
        this.categorySlug = categorySlug;
        this.progressBar = progressBar;
        this.listener = productListener;


        rq = Volley.newRequestQueue(context);

        products = new ArrayList<>();
        map = new HashMap<>();
        mp = new HashMap<>();
        // recyclerView.setHasFixedSize(true);
        // recyclerView.getItemAnimator().setChangeDuration(0);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(null);
        recyclerView.setNestedScrollingEnabled(false);
        adapterViewAndroid = new ProductGridAdapter(context, products);
        products.clear();
        adapterViewAndroid.notifyDataSetChanged();
        adapterViewAndroid.setHasStableIds(true);
        progressBar.setVisibility(View.VISIBLE);
        getCategoryProducts(categorySlug, 1);
        recyclerView.setAdapter(adapterViewAndroid);


    }

    public ProductGrid(Context contextz, RecyclerView recyclerView, String categorySlug, ProgressBar progressBar) {
        this.context = contextz;
        this.recyclerView = recyclerView;
        this.categorySlug = categorySlug;
        this.progressBar = progressBar;


        rq = Volley.newRequestQueue(context);

        products = new ArrayList<>();
        map = new HashMap<>();
        mp = new HashMap<>();
        // recyclerView.setHasFixedSize(true);
        // recyclerView.getItemAnimator().setChangeDuration(0);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(null);
        recyclerView.setNestedScrollingEnabled(false);
        adapterViewAndroid = new ProductGridAdapter(context, products);
        products.clear();
        adapterViewAndroid.notifyDataSetChanged();
        adapterViewAndroid.setHasStableIds(true);
        progressBar.setVisibility(View.VISIBLE);
        getCategoryProducts(categorySlug, 1);
        recyclerView.setAdapter(adapterViewAndroid);


    }

    // product grid constructer for shop and brand only
    public ProductGrid(Context contextz, RecyclerView recyclerView, String shopSlug, String categorySlug, int type2, ProgressBar progressBar) {

        this.context = contextz;
        this.recyclerView = recyclerView;
        this.categorySlug = categorySlug;
        this.shopSlug = shopSlug;
        this.progressBar = progressBar;


        rq = Volley.newRequestQueue(context);

        products = new ArrayList<>();

//            recyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(null);


        recyclerView.setNestedScrollingEnabled(false);

        adapterViewAndroid = new ProductGridAdapter(context, products);


        adapterViewAndroid.setHasStableIds(true);
        products.clear();
        adapterViewAndroid.notifyDataSetChanged();

        if (type2 == 1)
            getShopProducts(shopSlug, categorySlug, 1);
        else
            getBrandProducts(shopSlug, categorySlug, 1);

        recyclerView.setAdapter(adapterViewAndroid);


    }

    public void loadNextPage() {

        if (!isLoading)
            getCategoryProducts(categorySlug, ++current);
    }


    public void loadNextShopProducts() {

        if (!isLoading)
            getShopProducts(shopSlug, categorySlug, ++current);

    }

    public void loadNextBrandProducts() {

        if (!isLoading)
            getBrandProducts(shopSlug, categorySlug, ++current);

    }


    public void getShopProducts(String slug, String categorySlug, int currentPage) {


        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        current = currentPage;
        String url;

        if (categorySlug.equals(""))
            url = UrlUtils.SHOP_ITEMS + slug + "/?page=" + currentPage;
        else
            url = UrlUtils.SHOP_ITEMS + slug + "/?page=" + currentPage + "&category_slug=" + categorySlug;

        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {
                    try {
                        Log.d("shop_products", response.toString());

                        JSONObject data = response.getJSONObject("data");

                        JSONArray jsonArray = data.getJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject response2 = jsonArray.getJSONObject(i);

                            int priceMin = 0, priceMax = 0;
                            try {
                                priceMin = (int) Double.parseDouble(response2.getString("item_price"));
                                priceMax = (int) Double.parseDouble(response2.getString("item_price"));


                            } catch (Exception e) {

                                Log.d("json exc", e.toString());
                            }
                            ProductListItem item = new ProductListItem();
                            item.setThumbnailSM(response2.getJSONArray("item_images").get(0).toString());
                            item.setSlug(response2.getString("shop_item_slug"));
                            item.setName(response2.getString("item_name"));
                            item.setPriceMax(priceMax);
                            item.setPriceMin(priceMin);
                            products.add(item);

                            adapterViewAndroid.notifyItemInserted(products.size());

                        }

                        isLoading = false;
                        if (listener != null)
                            listener.onSuccess(products.size());

                        // adapterViewAndroid.notifyDataSetChanged();


                        progressBar.setVisibility(View.INVISIBLE);

                        if (products.size() < 10)
                            progressBar.setVisibility(View.GONE);

                        // Log.d("json","dataset changed");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                if (listener != null)
                    listener.onSuccess(0);
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
        if (currentPage > 2) {
            request.setShouldCache(false);
            //rq.getCache().clear();
        }
        rq.add(request);
    }


    public void getBrandProducts(String slug, String categorySlug, int currentPage) {
        current = currentPage;
        String url;

        progressBar.setVisibility(View.VISIBLE);

        isLoading = true;

        if (categorySlug.equals("root"))
            url = UrlUtils.PUBLIC_PRODUCTS + "?page=" + currentPage + "&limit=12&brand=" + slug;
        else
            url = UrlUtils.PUBLIC_PRODUCTS + "?page=" + currentPage + "&limit=12&brand=" + slug + "&category=" + categorySlug;

        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {
                    try {
                        Log.d("shop_products", response.toString());
                        if (response.getInt("count") == 0) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "Sorry no product available in this shop", Toast.LENGTH_SHORT).show();
                        }
                        JSONArray jsonArray = response.getJSONArray("results");


                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject response2 = jsonArray.getJSONObject(i);

                            int priceMin = 0, priceMax = 0;
                            try {
                                priceMin = (int) Double.parseDouble(response2.getString("min_price"));
                                priceMax = (int) Double.parseDouble(response2.getString("max_price"));


                            } catch (Exception e) {

                                Log.d("json exc", e.toString());
                            }
                            ProductListItem item = new ProductListItem();
                            item.setThumbnailSM(response2.getJSONArray("image_urls").get(0).toString());
                            item.setSlug(response2.getString("slug"));
                            item.setName(response2.getString("name"));
                            item.setPriceMax(priceMax);
                            item.setPriceMin(priceMin);
                            products.add(item);

                            adapterViewAndroid.notifyItemInserted(products.size());

                        }

                        isLoading = false;

                        if (listener != null)
                            listener.onSuccess(products.size());

                        // adapterViewAndroid.notifyDataSetChanged();


                        progressBar.setVisibility(View.INVISIBLE);

                        if (products.size() < 10)
                            progressBar.setVisibility(View.GONE);


                        // Log.d("json","dataset changed");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                if (listener != null)
                    listener.onSuccess(0);
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
        if (currentPage > 2) {
            request.setShouldCache(false);
            //rq.getCache().clear();
        }
        rq.add(request);
    }

    public void getCategoryProducts(String slug, int currentPage) {
        current = currentPage;
        progressBar.setVisibility(View.VISIBLE);


        isLoading = true;

        String url;

        if (slug.equals("root"))
            url = UrlUtils.PUBLIC_PRODUCTS + "?limit=20&&page=" + currentPage;
        else
            url = UrlUtils.PUBLIC_PRODUCTS + "?limit=20&category=" + slug + "&page=" + currentPage;

        Log.d("json", url);
        if (products.size() > 0 && type != 0) {
            products.clear();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {
                    try {
                        Log.d("product_json", response.toString());
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject response2 = jsonArray.getJSONObject(i);

                            int priceMin = 0, priceMax = 0;
                            try {
                                priceMin = (int) Double.parseDouble(response2.getString("min_price"));
                                priceMax = (int) Double.parseDouble(response2.getString("max_price"));


                            } catch (Exception e) {

                                Log.d("json exc", e.toString());
                            }
                            ProductListItem item = new ProductListItem();
                            item.setThumbnailSM(response2.getJSONArray("image_urls").get(0).toString());
                            item.setSlug(response2.getString("slug"));
                            item.setName(response2.getString("name"));
                            item.setPriceMax(priceMax);
                            item.setPriceMin(priceMin);
                            products.add(item);

                            adapterViewAndroid.notifyItemInserted(products.size());

                        }


                        isLoading = false;


                        progressBar.setVisibility(View.INVISIBLE);

                        if (listener != null)
                            listener.onSuccess(products.size());

                        if (products.size() < 10)
                            progressBar.setVisibility(View.GONE);


                        //Log.d("json","dataset changed");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                if (listener != null)
                    listener.onSuccess(0);
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
        if (currentPage > 2) {
            request.setShouldCache(false);
            //rq.getCache().clear();
        }
        rq.add(request);
    }

//    public void getCategoryProductsWithPrice(String slug, int currentPage) {
//        current = currentPage;
//        progressBar.setVisibility(View.VISIBLE);
//        isLoading = true;
//        String url = "https://api-prod.evaly.com.bd/api/product/?shadow_category__slug=" + slug + "&page=" + currentPage;
//        Log.d("json", url);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
//                response -> {
//                    try {
//                        Log.d("product_json2", response.toString());
//                        JSONArray jsonArray = response.getJSONArray("results");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject response2 = jsonArray.getJSONObject(i);
//                            JSONObject category = response2.getJSONObject("category");
//                            JSONObject brand = response2.getJSONObject("brand");
//                            JSONObject price = response2.getJSONObject("price_range");
//                            if (response2.getBoolean("approved")) {
//                                int priceMin = 0, priceMax = 0;
//                                try {
//                                    priceMin = price.getInt("price__min");
//                                    priceMax = price.getInt("price__max");
//                                } catch (Exception e) {
//
//                                }
//                                ProductListItem item = new ProductListItem();
//                                item.setThumbnailSM(response2.getString("thumbnail_sm"));
//                                item.setSlug(response2.getString("slug"));
//                                item.setName(response2.getString("name"));
//                                item.setCategoryName(category.getString("name"));
//                                item.setCategorySlug(category.getString("slug"));
//                                item.setBrandName(brand.getString("name"));
//                                item.setBrandSlug(brand.getString("slug"));
//                                item.setPriceMax(priceMax);
//                                item.setPriceMin(priceMin);
//                                if (minPrice == 0) {
//                                    if (priceMin <= maxPrice) {
//                                        products.add(item);
//                                        adapterViewAndroid.notifyItemInserted(products.size());
//                                    }
//                                } else if (maxPrice == 0) {
//                                    if (priceMin >= minPrice) {
//                                        products.add(item);
//                                        adapterViewAndroid.notifyItemInserted(products.size());
//                                    }
//                                } else {
//                                    if (priceMin >= minPrice && priceMin <= maxPrice) {
//                                        products.add(item);
//                                        adapterViewAndroid.notifyItemInserted(products.size());
//                                    }
//                                }
//                            }
//                        }
//                        isLoading = false;
//                        progressBar.setVisibility(View.INVISIBLE);
//                        if (listener != null)
//                            listener.onSuccess(products.size());
//                        if (products.size() < 10) {
//                            progressBar.setVisibility(View.GONE);
//                            getCategoryProductsWithPrice(slug, ++current);
//                        }
//                        //Log.d("json","dataset changed");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
//
//                if (listener != null)
//                    listener.onSuccess(0);
//            }
//        });
//        request.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//        if (currentPage > 2) {
//            request.setShouldCache(false);
//            //rq.getCache().clear();
//        }
//        rq.add(request);
//    }

//    public void getSortedCategoryProducts(String slug, int currentPage, int type) {
//        current = currentPage;
//        progressBar.setVisibility(View.VISIBLE);
//
//
//        isLoading = true;
//
//        String url = "https://api-prod.evaly.com.bd/api/product/?shadow_category__slug=" + slug + "&page=" + currentPage;
//        Log.d("json", url);
//        products.clear();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
//                response -> {
//                    try {
//                        Log.d("json", response.toString());
//                        JSONArray jsonArray = response.getJSONArray("results");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject response2 = jsonArray.getJSONObject(i);
//                            JSONObject category = response2.getJSONObject("category");
//                            JSONObject brand = response2.getJSONObject("brand");
//                            JSONObject price = response2.getJSONObject("price_range");
//                            if (response2.getBoolean("approved")) {
//                                int priceMin = 0, priceMax = 0;
//                                try {
//                                    priceMin = price.getInt("price__min");
//                                    priceMax = price.getInt("price__max");
//                                } catch (Exception e) {
//
//                                }
//                                ProductListItem item = new ProductListItem();
//                                item.setThumbnailSM(response2.getString("thumbnail_sm"));
//                                item.setSlug(response2.getString("slug"));
//                                item.setName(response2.getString("name"));
//                                item.setCategoryName(category.getString("name"));
//                                item.setCategorySlug(category.getString("slug"));
//                                item.setBrandName(brand.getString("name"));
//                                item.setBrandSlug(brand.getString("slug"));
//                                item.setPriceMax(priceMax);
//                                item.setPriceMin(priceMin);
//                                products.add(item);
//
//                                adapterViewAndroid.notifyItemInserted(products.size());
//                            }
//                        }
//                        Collections.sort(products, new Comparator<ProductListItem>() {
//                            @Override
//                            public int compare(ProductListItem lhs, ProductListItem rhs) {
//                                if (lhs.getPriceMin() > rhs.getPriceMin()) {
//                                    return lhs.getPriceMin();
//                                } else {
//                                    return rhs.getPriceMin();
//                                }
//                            }
//                        });
//
//                        if (listener != null)
//                            listener.onSuccess(products.size());
//
//                        isLoading = false;
//
//
//                        progressBar.setVisibility(View.INVISIBLE);
//                        if (products.size() < 10)
//                            progressBar.setVisibility(View.GONE);
//                        //Log.d("json","dataset changed");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
//
//                if (listener != null)
//                    listener.onSuccess(0);
//            }
//        });
//
//
//        request.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//        if (currentPage > 2) {
//            request.setShouldCache(false);
//            //rq.getCache().clear();
//        }
//        rq.add(request);
//    }

    public void sortByPriceHigh() {
        Collections.sort(products, new Comparator<ProductListItem>() {
            @Override
            public int compare(ProductListItem lhs, ProductListItem rhs) {
                if (lhs.getPriceMin() > rhs.getPriceMin()) {
                    return lhs.getPriceMin();
                } else {
                    return rhs.getPriceMin();
                }
            }
        });

    }
}

