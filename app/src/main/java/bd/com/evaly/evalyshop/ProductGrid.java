package bd.com.evaly.evalyshop;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.fragment.HomeFragment;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;


public class ProductGrid {

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private String categorySlug;
    private String shopSlug = "";
    private String campaignSlug = "";
    private HomeFragment main;
    private Context context;
    private List<ProductItem> products;
    private int current = 1, type = 0, maxPrice = 0, minPrice = 0;
    private ProductGridAdapter adapterViewAndroid;
    private ProgressBar progressBar;
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

        ProductApiHelper.getShopProducts(shopSlug, currentPage, 21, categorySlug, campaignSlug, new ResponseListenerAuth<JsonObject, String>() {
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
                            priceMin = (int) Math.round(Double.parseDouble(response2.get("item_price").getAsString()));
                            priceMax = (int) Math.round(Double.parseDouble(response2.get("item_price").getAsString()));

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

                        if (response2.has("discounted_price"))
                            item.setMinDiscountedPrice(response2.get("discounted_price").isJsonNull()? "0" : response2.get("discounted_price").getAsString());

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

                    if (currentPage == 1 && listener != null)
                        listener.onSuccess(0);

                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                progressBar.setVisibility(View.GONE);

                if (currentPage == 1 && listener != null)
                    listener.onSuccess(0);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

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

