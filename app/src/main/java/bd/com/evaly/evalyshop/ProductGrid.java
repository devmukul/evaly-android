package bd.com.evaly.evalyshop;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.fragment.HomeFragment;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.ProductListItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;


public class ProductGrid {

    RecyclerView recyclerView;
    StaggeredGridLayoutManager mLayoutManager;
    String categorySlug;
    String shopSlug = "";
    String campaignSlug = "";
    public HomeFragment main;
    public Context context;
    List<ProductItem> products;
    int current = 1, type = 0, maxPrice = 0, minPrice = 0;
    private ProductGridAdapter adapterViewAndroid;
    private ProgressBar progressBar;
    RequestQueue rq;
    private NestedScrollView scrollView;
    private boolean isLoading = false;
    private ProductListener listener;
    private int cashbackRate = 0;




    public void setListener(ProductListener listener) {
        this.listener = listener;
        if (adapterViewAndroid!=null)
            adapterViewAndroid.setproductListener(listener);
    }


    public void setScrollView(NestedScrollView scrollView){
        this.scrollView = scrollView;
    }


    public ProductGrid(Context contextz, RecyclerView recyclerView, String categorySlug, ProgressBar progressBar) {
        this.context = contextz;
        this.recyclerView = recyclerView;
        this.categorySlug = categorySlug;
        this.progressBar = progressBar;


        rq = Volley.newRequestQueue(context);

        products = new ArrayList<>();
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(null);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setHasFixedSize(false);

        adapterViewAndroid = new ProductGridAdapter(context, products);
        products.clear();
        adapterViewAndroid.notifyDataSetChanged();
        adapterViewAndroid.setHasStableIds(true);
        progressBar.setVisibility(View.VISIBLE);
        getCategoryProducts(categorySlug, 1);
        recyclerView.setAdapter(adapterViewAndroid);


    }

    // product grid constructer for shop and brand only
    public ProductGrid(Context contextz, RecyclerView recyclerView, String shopSlug, String categorySlug, String campaignSlug, int type2, ProgressBar progressBar) {

        this.context = contextz;
        this.recyclerView = recyclerView;
        this.categorySlug = categorySlug;
        this.campaignSlug = campaignSlug;
        this.shopSlug = shopSlug;
        this.progressBar = progressBar;


        rq = Volley.newRequestQueue(context);

        products = new ArrayList<>();

//            recyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(null);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setHasFixedSize(false);

        adapterViewAndroid = new ProductGridAdapter(context, products);
        adapterViewAndroid.setHasStableIds(true);
        products.clear();
        adapterViewAndroid.notifyDataSetChanged();

        if (type2 == 1) {
            getShopProducts(1);
            adapterViewAndroid.setShopSlug(shopSlug);
        }
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
            getShopProducts(++current);

    }

    public void loadNextBrandProducts() {

        if (!isLoading)
            getBrandProducts(shopSlug, categorySlug, ++current);

    }




    public void getShopProducts(int currentPage){


        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        current = currentPage;

        ProductApiHelper.getShopProducts(shopSlug, currentPage, 21, categorySlug, campaignSlug, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                if (scrollView != null)
                    scrollView.fling(0);

                try {

                    JsonObject data = response.getAsJsonObject("data");

                    JsonArray jsonArray = data.getAsJsonArray("items");

                    if (data.has("meta")){
                        cashbackRate = data.getAsJsonObject("meta").get("cashback_rate").getAsInt();
                        adapterViewAndroid.setCashback_rate(cashbackRate);
                    }

                    for (int i = 0; i < jsonArray.size(); i++) {

                        JsonObject response2 = jsonArray.get(i).getAsJsonObject();

                        int priceMin = 0, priceMax = 0;
                        try {
                            priceMin = (int) Double.parseDouble(response2.get("item_price").getAsString());
                            priceMax = (int) Double.parseDouble(response2.get("item_price").getAsString());


                        } catch (Exception e) {

                            Log.d("json exc", e.toString());
                        }
                        ProductItem item = new ProductItem();

                        List<String> images  = new ArrayList<>();
                        images.add(response2.getAsJsonArray("item_images").get(0).getAsString());

                        item.setImageUrls(images);
                        item.setSlug(response2.get("shop_item_slug").getAsString());
                        item.setName(response2.get("item_name").getAsString());
                        item.setMaxPrice(String.valueOf(priceMax));
                        item.setMinPrice(String.valueOf(priceMin));

                        if (response2.has("discounted_price")) {

                            String d_price = response2.get("discounted_price").getAsString();
                            if (d_price.equals("null"))
                                item.setMinDiscountedPrice("0");
                            else
                                item.setMinDiscountedPrice(d_price);
                        }
                        products.add(item);
                        adapterViewAndroid.notifyItemInserted(products.size());
                    }

                    isLoading = false;
                    if (listener != null)
                        listener.onSuccess(products.size());
                    progressBar.setVisibility(View.INVISIBLE);

                    if (products.size() < 10)
                        progressBar.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {


                progressBar.setVisibility(View.GONE);

                if (listener != null)
                    listener.onSuccess(0);
            }
        });

    }

    public void getShopProductsz(int currentPage) {


        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        current = currentPage;
        String url;

        if (!campaignSlug.equals("")){

            if (categorySlug.equals(""))
                url = UrlUtils.CAMPAIGNS+"/" + campaignSlug + "/shops/"+shopSlug+"/items?page=" + currentPage;
            else
                url = UrlUtils.CAMPAIGNS+"/" + campaignSlug + "/shops/"+shopSlug+"/items?page=" + currentPage + "&category_slug=" + categorySlug;

        } else {


            if (categorySlug.equals(""))
                url = UrlUtils.SHOP_ITEMS + shopSlug + "/?page=" + currentPage;
            else
                url = UrlUtils.SHOP_ITEMS + shopSlug + "/?page=" + currentPage + "&category_slug=" + categorySlug;

        }

        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {


                    if (scrollView != null)
                        scrollView.fling(0);

                    try {
                        // Log.d("shop_products", response.toString());

                        JSONObject data = response.getJSONObject("data");

                        JSONArray jsonArray = data.getJSONArray("items");


                        if (data.has("meta")){

                            cashbackRate = data.getJSONObject("meta").getInt("cashback_rate");
                            adapterViewAndroid.setCashback_rate(cashbackRate);

                        }


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

                            if (response2.has("discounted_price")) {

                                String d_price = response2.getString("discounted_price");
                                if (d_price.equals("null"))
                                    item.setDiscountedPrice(0);
                                else
                                    item.setDiscountedPrice((int)Double.parseDouble(d_price));
                            }


                           // products.add(item);

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
                }, error -> {
                    progressBar.setVisibility(View.GONE);

                    if (listener != null)
                        listener.onSuccess(0);
                });


        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
            //rq.getCache().clear();

        rq.add(request);
    }


    public void getBrandProducts(String brandSlug, String categorySlug, int currentPage){
        getCategoryBrandsProducts(currentPage, categorySlug, brandSlug);
    }

    public void getCategoryProducts(String categorySlug, int currentPage){
        getCategoryBrandsProducts(currentPage, categorySlug, null);
    }


    public void getCategoryBrandsProducts(int currentPage, String category, String brand){

        current = currentPage;
        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;

        if (products.size() > 0 && type != 0) {
            products.clear();
        }

        ProductApiHelper.getCategoryBrandProducts(currentPage, category, brand, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                if (scrollView != null)
                    scrollView.fling(0);

                products.addAll(response.getData());

                adapterViewAndroid.notifyItemRangeInserted(products.size()-response.getData().size(), response.getData().size());

                isLoading = false;

                if (listener != null)
                    listener.onSuccess(products.size());

                if (products.size() < 10)
                    progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                progressBar.setVisibility(View.GONE);

                if (listener != null)
                    listener.onSuccess(0);

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }






}

