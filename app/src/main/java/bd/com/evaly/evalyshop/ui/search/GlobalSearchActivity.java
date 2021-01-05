package bd.com.evaly.evalyshop.ui.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.search.SearchFilterItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.BrandApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.ui.product.productList.ProductGrid;
import bd.com.evaly.evalyshop.ui.product.productList.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.ui.search.adapter.AutoCompleteAdapter;
import bd.com.evaly.evalyshop.ui.search.adapter.SearchFilterAdapter;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class GlobalSearchActivity extends BaseActivity {

    String searchSlug = "product", selectedCategory = "";
    Spinner searchType, sortby;
    AutoCompleteTextView searchText;
    ProductGrid productGrid;
    ArrayList<TabsItem> itemList;
    List<ProductItem> itemListProduct, itemListProductWithCategory;
    TabsAdapter adapter;
    ProductGridAdapter adapterProduct, adapterProductCategory;
    RecyclerView recyclerView, filterRecyclerView;
    StickyScrollView nestedSV;
    int page = 1, filterCategoryPage = 1, gotProductCounter = 0;
    ProgressBar progressBar;
    LinearLayout filter, drawerRel;
    DrawerLayout mDrawerLayout;
    Map<String, String> map, categoryMapping;
    String filterURL = "";
    Button applyFilterBtn;
    ArrayList<String> categoryList, allCategories;
    ArrayAdapter<String> arrayAdapter;
    boolean searched = false;
    LinearLayout noResult;
    RequestQueue rq;
    ImageView searchClear;
    EditText minimum, maximum;
    String highestSlug = "";
    boolean isLoading = false;
    Button clearFilters;

    String userAgent;

    String sortIndexName = "products"; //"products_price_asc";
    String filterJSON = "[]";
    String priceFilterJSON = "[\"price>=10\"]";


    ArrayList<SearchFilterItem> filterItemlist;
    SearchFilterAdapter filterAdapter;

    LinearLayout sortingBtn;

    boolean fromFilter = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);
        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }


        sortingBtn = findViewById(R.id.sortBtn);
        searchClear = findViewById(R.id.searchClear);
        progressBar = findViewById(R.id.progressBar);
        searchType = findViewById(R.id.search_type);
        searchText = findViewById(R.id.search_text);

        searchText.requestFocus();

        filter = findViewById(R.id.filter);
        applyFilterBtn = findViewById(R.id.header);
        noResult = findViewById(R.id.noResult);
        minimum = findViewById(R.id.minimum);
        maximum = findViewById(R.id.maximum);
        clearFilters = findViewById(R.id.clear);
        // clearFilters.setVisibility(View.GONE);

        categoryList = new ArrayList<>();
        allCategories = new ArrayList<>();
        categoryMapping = new HashMap<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.category_filter_item, categoryList);

        rq = Volley.newRequestQueue(GlobalSearchActivity.this);
        map = new HashMap<>();
        mDrawerLayout = findViewById(R.id.layout_drawer);
        drawerRel = findViewById(R.id.drawer_rel);


        filterRecyclerView = findViewById(R.id.filterRecycler);

        LinearLayoutManager managerFilter = new LinearLayoutManager(this);
        filterRecyclerView.setLayoutManager(managerFilter);

        filterItemlist = new ArrayList<>();
        filterAdapter = new SearchFilterAdapter(this, filterItemlist);

        filterRecyclerView.setAdapter(filterAdapter);


        filter.setOnClickListener(v -> {


            if (searchType.getSelectedItemPosition() == 1 || searchType.getSelectedItemPosition() == 2) {
                Toast.makeText(GlobalSearchActivity.this, "You can use filters while searching for products", Toast.LENGTH_LONG).show();
                return;
            }


            if (mDrawerLayout.isDrawerOpen(drawerRel)) {
                mDrawerLayout.closeDrawer(drawerRel);
            } else {
                mDrawerLayout.openDrawer(drawerRel);
            }
        });

        applyFilterBtn.setOnClickListener(v -> {
            // hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //or try following:
            //InputMethodManager imm = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            searchText.clearFocus();

            // ["brand_name:Individual Collections","brand_name:Savlon"],["category_name:Casing","category_name:Sanitary Pads"]

            JSONArray category_name = new JSONArray();
            JSONArray brand_name = new JSONArray();
            JSONArray color = new JSONArray();

            for (int i = 0; i < filterItemlist.size(); i++) {

                SearchFilterItem item = filterItemlist.get(i);
                if (item.isSelected()) {

                    if (item.getType().equals("category_name"))
                        category_name.put("category_name:" + item.getName());
                    else if (item.getType().equals("brand_name"))
                        brand_name.put("brand_name:" + item.getName());
                    else if (item.getType().equals("color"))
                        color.put("color:" + item.getName());
                }
            }

            // filter json generator

            JSONArray facetFiltersJSON = new JSONArray();

            if (category_name.length() > 0)
                facetFiltersJSON.put(category_name);

            if (brand_name.length() > 0)
                facetFiltersJSON.put(brand_name);

            if (color.length() > 0)
                facetFiltersJSON.put(color);
            filterJSON = facetFiltersJSON.toString();


            // min max price filter generator

            String min = minimum.getText().toString();
            String max = maximum.getText().toString();

            if (min.equals("") && max.equals(""))
                priceFilterJSON = "[\"price>=10\"]";
            else if (min.equals(""))
                priceFilterJSON = "[\"price>=10\",\"price<=" + max + "\"]";
            else if (max.equals(""))
                priceFilterJSON = "[\"price>=" + min + "\"]";
            else
                priceFilterJSON = "[\"price>=" + min + "\",\"price<=" + max + "\"]";

            Log.d("json price filter", priceFilterJSON);

            itemListProduct.clear();

            fromFilter = true;

            page = 1;

            getProducts(page);

            if (mDrawerLayout.isDrawerOpen(drawerRel)) {
                mDrawerLayout.closeDrawer(drawerRel);
            } else {
                mDrawerLayout.openDrawer(drawerRel);
            }
        });

        clearFilters.setOnClickListener(v -> {

            page = 1;

            filterJSON = "[]";
            priceFilterJSON = "[\"price>=10\"]";
            minimum.setText("");
            maximum.setText("");

            itemListProduct.clear();
            page = 1;
            getSearchedItems(page);

            for (int i = 0; i < filterItemlist.size(); i++) {

                SearchFilterItem item = filterItemlist.get(i);
                item.setSelected(false);
                filterAdapter.notifyItemChanged(i);

            }


            if (mDrawerLayout.isDrawerOpen(drawerRel)) {
                mDrawerLayout.closeDrawer(drawerRel);
            } else {
                mDrawerLayout.openDrawer(drawerRel);
            }
        });

        // searchText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

        recyclerView = findViewById(R.id.recycle);
        nestedSV = findViewById(R.id.sticky);
        LinearLayoutManager manager = new LinearLayoutManager(GlobalSearchActivity.this);
        itemList = new ArrayList<>();
        itemListProduct = new ArrayList<>();
        int type = 1;

        adapter = new TabsAdapter(GlobalSearchActivity.this, this, itemList, type);
        adapterProduct = new ProductGridAdapter(GlobalSearchActivity.this, itemListProduct);

        recyclerView.setLayoutManager(manager);

        searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                searchText.setText("");
                fromFilter = false;
                if (parent.getItemAtPosition(position).equals("Products")) {
                    searchSlug = "product";
                    filter.setVisibility(View.VISIBLE);
                    getProducts(1);
                } else if (parent.getItemAtPosition(position).equals("Shops")) {
                    filterURL = "";
                    noFilterText();

                    searchSlug = "shop";
                    page = 1;
                    searched = false;
                    itemList = new ArrayList<>();
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new TabsAdapter(GlobalSearchActivity.this, GlobalSearchActivity.this, itemList, 3);
                    recyclerView.setAdapter(adapter);
                    getShops(page);
                } else if (parent.getItemAtPosition(position).equals("Brands")) {
                    filterURL = "";
                    noFilterText();

                    searchSlug = "brands";
                    page = 1;
                    searched = false;
                    itemList = new ArrayList<>();
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new TabsAdapter(GlobalSearchActivity.this, GlobalSearchActivity.this, itemList, 2);
                    recyclerView.setAdapter(adapter);
                    getBrands(page);
                }
                page = 1;
                minimum.setText("");
                maximum.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AutoCompleteAdapter adapterAuto = new AutoCompleteAdapter(GlobalSearchActivity.this, android.R.layout.simple_list_item_1);
        searchText.setAdapter(adapterAuto);
        searchText.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        searchText.setDropDownAnchor(R.id.lineafterSearch);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("type")) {
                int iType = extras.getInt("type");
                if (iType == 1) {
                    searchType.setSelection(0);
                    searchText.setAdapter(adapterAuto);
                } else if (iType == 2) {
                    searchType.setSelection(2);
                    searchText.setAdapter(null);
                } else if (iType == 3) {
                    searchType.setSelection(1);
                    searchText.setAdapter(null);
                }
            }
        } else
            getProducts(1);

        searchText.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            nestedSV.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
            noResult.setVisibility(View.GONE);

            fromFilter = false;

            page = 1;
            filterURL = "";
            itemList.clear();
            itemListProduct.clear();
            searchText.setAdapter(adapterAuto);
            // hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //or try following:
            //InputMethodManager imm = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            searchText.clearFocus();
            RecyclerView.LayoutManager mLayoutManager;
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapterProduct);

            categoryList.clear();
            allCategories.clear();
            getSearchedItems(page);
        });

        searchClear.setOnClickListener(v -> {
            if (searchClear.getTag().equals("clear"))
                searchText.setText("");
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    searchClear.setImageDrawable(getDrawable(R.drawable.ic_close_smallest));
                    searchClear.setTag("clear");
                } else {
                    searchClear.setImageDrawable(getDrawable(R.drawable.ic_search));
                    searchClear.setTag("search");
                }
            }
        });

        searchText.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && (event.getAction() == KeyEvent.ACTION_DOWN))) {

                nestedSV.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
                noResult.setVisibility(View.GONE);

                minimum.setText("");
                maximum.setText("");

                fromFilter = false;

                sortIndexName = "products";
                filterJSON = "[]";
                priceFilterJSON = "[\"price>=10\"]";

                itemList.clear();
                itemListProduct.clear();
                Log.d("json", "Enter pressed");
                RecyclerView.LayoutManager mLayoutManager;
                int type1 = 1;
                if (searchSlug.equals("product")) {
                    type1 = 1;
                    // don't let show suggestion after search
                    AutoCompleteAdapter adapterAuto1 = new AutoCompleteAdapter(GlobalSearchActivity.this, android.R.layout.simple_list_item_1);
                    searchText.setAdapter(adapterAuto1);
                } else if (searchSlug.equals("shop")) {
                    type1 = 3;
                    searchText.setAdapter(null);
                } else if (searchSlug.equals("brands")) {
                    type1 = 3;
                    searchText.setAdapter(null);
                }
                if (type1 == 1) {
                    mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setAdapter(adapterProduct);
                } else {
                    mLayoutManager = new GridLayoutManager(GlobalSearchActivity.this, 3);
                    recyclerView.setAdapter(adapter);
                }
                recyclerView.setLayoutManager(mLayoutManager);
                page = 1;

                categoryList.clear();
                allCategories.clear();
                getSearchedItems(page);
                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //or try following:
                //InputMethodManager imm = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                searchText.clearFocus();
                return true;
            } else {
                return false;
            }
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        //handler.post(runnableCode);

        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                String TAG = "nested_sync";
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(TAG, "BOTTOM SCROLL");
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        // Toast.makeText(GlobalSearchActivity.this, "products", Toast.LENGTH_SHORT).show();
                        if (searchSlug.equals("shop")) {
                            loadNextShops();
                        } else if (searchSlug.equals("brands")) {
                            loadNextBrands();
                        } else {
                            loadNextSearchPage();
                        }
                    } catch (Exception e) {
                        Log.e("load more product", e.toString());
                    }
                }
            });
        }


        sortingBtn.setOnClickListener(v -> {

            if (searchType.getSelectedItemPosition() == 1 || searchType.getSelectedItemPosition() == 2) {
                Toast.makeText(GlobalSearchActivity.this, "You can use sorting while searching for products", Toast.LENGTH_LONG).show();
                return;
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchActivity.this);
            builder.setTitle("Sort Products By\n");
            final String[] a = {"Relevance", "Price low to high", "Price high to low"};

            int checkItem = 0;

            if (sortIndexName.equals("products_price_asc"))
                checkItem = 1;
            else if (sortIndexName.equals("products_price_desc"))
                checkItem = 2;

            builder.setSingleChoiceItems(a, checkItem, (dialogInterface, i) -> {
                if (i == 0)
                    sortIndexName = "products";
                else if (i == 1)
                    sortIndexName = "products_price_asc";
                else if (i == 2)
                    sortIndexName = "products_price_desc";
                page = 1;
                itemListProduct.clear();
                getProducts(page);
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }


    public void loadNextSearchPage() {
        getSearchedItems(++page);
    }

    public void loadNextShops() {
        getShops(++page);
    }

    public void loadNextBrands() {
        getBrands(++page);
    }

    public void getSearchedItems(int p) {
        minimum.setText("");
        maximum.setText("");
        progressBar.setVisibility(View.VISIBLE);
        if (searchSlug.equals("product")) {
            filter.setVisibility(View.VISIBLE);
        }
        if (searchSlug.equals("product")) {
            getProducts(page);
            return;
        }
        if (searchSlug.equals("brands")) {
            //page=1;
            getBrands(page);

            return;
        }
        if (searchSlug.equals("shop")) {
            //page=1;
            getShops(page);

            return;
        }
    }


    public void getProducts(int page) {

        String searchQuery = searchText.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("query", searchQuery);
        paramsMap.put("maxValuesPerFacet", "20");
        paramsMap.put("page", page - 1);
        paramsMap.put("highlightPreTag", "<ais-highlight-0000000000>");
        paramsMap.put("highlightPostTag", "</ais-highlight-0000000000>");
        paramsMap.put("facets", "[\"price\",\"category_name\",\"brand_name\",\"color\"]");
        paramsMap.put("tagFilters", "");

        if (!filterJSON.equals("[]"))
            paramsMap.put("facetFilters", filterJSON);

        if (!priceFilterJSON.equals("[\"price>=10\"]"))
            paramsMap.put("numericFilters", priceFilterJSON);


        JSONObject payload = new JSONObject();

        JSONObject paramsJson = new JSONObject();

        try {

            paramsJson.put("indexName", sortIndexName);
            paramsJson.put("params", Utils.urlEncodeUTF8(paramsMap));

            payload.putOpt("requests", new JSONArray());
            payload.getJSONArray("requests").put(paramsJson);

        } catch (Exception e) {
        }


        if (page < 2) {

            RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapterProduct);
            recyclerView.setLayoutManager(mLayoutManager);

        }

        String url = "https://eza2j926q5-dsn.algolia.net/1/indexes/*/queries?x-algolia-application-id=EZA2J926Q5&x-algolia-api-key=ca9abeea06c16b7d531694d6783a8f04";
        Log.d("json", url);


        if (isLoading)
            return;
        isLoading = true;


        Log.d("json params", payload.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    if (nestedSV != null)
                        nestedSV.fling(0);

                    Log.d("json search_result", response.toString());
                    try {

                        noResult.setVisibility(View.GONE);

                        progressBar.setVisibility(View.INVISIBLE);

                        isLoading = false;
                        JSONArray jsonArray = response.getJSONArray("results");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        searched = true;
                        JSONArray productList = jsonObject.getJSONArray("hits");
                        ArrayList<String> slugStore = new ArrayList<>();

                        for (int i = 0; i < productList.length(); i++) {

                            JSONObject ob = productList.getJSONObject(i);
                            ProductItem tabsItem = new ProductItem();
                            tabsItem.setName(ob.getString("name"));

                            List<String> images = new ArrayList<>();
                            images.add(ob.getString("product_image").replace("\n", "").replace("\r", ""));
                            tabsItem.setImageUrls(images);
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setMaxPrice(String.valueOf(ob.isNull("max_price") ? 0 : ob.getInt("max_price")));
                            tabsItem.setMinDiscountedPrice(String.valueOf(ob.isNull("discounted_price") ? 0 : ob.getInt("discounted_price")));
                            tabsItem.setMinPrice(String.valueOf(ob.getInt("price")));

                            if (!slugStore.contains(ob.getString("slug"))) {
                            }
                            itemListProduct.add(tabsItem);
                            adapterProduct.notifyItemInserted(itemListProduct.size());
                            slugStore.add(ob.getString("slug"));
                        }

                        if (jsonObject.getInt("nbHits") > 10) {
                            if (itemListProduct.size() < 10 && page < 4)
                                loadNextSearchPage();
                        }

                        // Getting filters

                        if (!fromFilter) {

                            filterItemlist.clear();
                            filterAdapter.notifyDataSetChanged();

                            JSONObject facets = jsonObject.getJSONObject("facets");
                            Iterator<String> iter;

                            if (facets.has("category_name")) {
                                JSONObject category_name = facets.getJSONObject("category_name");
                                iter = category_name.keys();
                                while (iter.hasNext()) {
                                    try {
                                        String name = iter.next();
                                        int count = category_name.getInt(name);
                                        SearchFilterItem filterItem = new SearchFilterItem();
                                        filterItem.setType("category_name");
                                        filterItem.setName(name);
                                        filterItem.setCount(count);
                                        filterItemlist.add(filterItem);
                                        //filterAdapter.notifyItemInserted(filterItemlist.size());

                                    } catch (JSONException e) {
                                    }
                                }
                            }

                            if (facets.has("brand_name")) {

                                JSONObject brand_name = facets.getJSONObject("brand_name");
                                iter = brand_name.keys();
                                while (iter.hasNext()) {
                                    try {
                                        String name = iter.next();
                                        int count = brand_name.getInt(name);
                                        SearchFilterItem filterItem = new SearchFilterItem();
                                        filterItem.setType("brand_name");
                                        filterItem.setName(name);
                                        filterItem.setCount(count);
                                        filterItemlist.add(filterItem);

                                        //filterAdapter.notifyItemInserted(filterItemlist.size());

                                    } catch (JSONException e) {
                                    }
                                }
                            }

                            if (facets.has("color")) {
                                JSONObject color = facets.getJSONObject("color");
                                iter = color.keys();
                                while (iter.hasNext()) {
                                    try {
                                        String name = iter.next();
                                        int count = color.getInt(name);
                                        SearchFilterItem filterItem = new SearchFilterItem();
                                        filterItem.setType("color");
                                        filterItem.setName(name);
                                        filterItem.setCount(count);
                                        filterItemlist.add(filterItem);

                                        //filterAdapter.notifyItemInserted(filterItemlist.size());

                                    } catch (JSONException e) {
                                    }
                                }
                            }
                            filterAdapter.notifyDataSetChanged();
                            //filterAdapter.notifyDataSetChanged();
                            Log.d("json filter added", filterItemlist.toString());
                            fromFilter = false;
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                        try {

                            if ((productList.length() < 1 && page == 1) || jsonObject.getInt("nbHits") < 1) {
                                nestedSV.setBackgroundColor(ContextCompat.getColor(this, R.color.fff));
                                noResult.setVisibility(View.VISIBLE);

                                if (!GlobalSearchActivity.this.isFinishing()) {
                                    try {
                                        Glide.with(GlobalSearchActivity.this)
                                                .load(R.drawable.ic_search_not_found)
                                                .apply(new RequestOptions().override(800, 800))
                                                .into((ImageView) findViewById(R.id.noImage));
                                    } catch (Exception e) {

                                    }
                                }
                            } else {

                                nestedSV.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
                                noResult.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {

                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        if (!searchSlug.equals("product") && page == 1)
                            loadNextSearchPage();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            error.printStackTrace();
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }


    public void getShops(int p) {

        if (isLoading)
            return;
        isLoading = true;
        String query = null;
        if (!searchText.getText().toString().equals(""))
            query = searchText.getText().toString();


        ShopApiHelper.getShops(null, query, p, new ResponseListenerAuth<CommonDataResponse<List<ShopListResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopListResponse>> response, int statusCode) {
                for (ShopListResponse item : response.getData()) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(item.getShopName());
                    tabsItem.setImage(item.getShopImage());
                    tabsItem.setSlug(item.getSlug());
                    tabsItem.setCategory("root");
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                }

                progressBar.setVisibility(View.INVISIBLE);

                if (itemList.size() > 0)
                    setNotFound(false);
                else
                    setNotFound(true);

                isLoading = false;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void getBrands(int p) {
        if (isLoading)
            return;
        isLoading = true;

        String query = null;
        if (!searchText.getText().toString().equals(""))
            query = searchText.getText().toString();

        BrandApiHelper.getBrands(null, query, p, new ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<BrandResponse>> response, int statusCode) {
                for (BrandResponse item : response.getData()) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(item.getName());
                    tabsItem.setImage(item.getImageUrl());
                    tabsItem.setSlug(item.getSlug());
                    tabsItem.setCategory("root");
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                }

                progressBar.setVisibility(View.INVISIBLE);


                if (itemList.size() > 0)
                    setNotFound(false);
                else
                    setNotFound(true);

                isLoading = false;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void setNotFound(boolean notFound) {

        if (notFound && !GlobalSearchActivity.this.isFinishing()) {
            nestedSV.setBackgroundColor(ContextCompat.getColor(this, R.color.fff));
            noResult.setVisibility(View.VISIBLE);
            try {
                Glide.with(GlobalSearchActivity.this)
                        .load(R.drawable.ic_search_not_found)
                        .apply(new RequestOptions().override(800, 800))
                        .into((ImageView) findViewById(R.id.noImage));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Search result not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            nestedSV.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
            noResult.setVisibility(View.GONE);
        }
    }

    public void noFilterText() {
        TextView valueTV = new TextView(this);
        valueTV.setText("No filter attribute available for the searched item");
        valueTV.setTextColor(Color.BLACK);
        valueTV.setTextSize(16f);
        valueTV.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 100, 0, 0);
        params.gravity = Gravity.CENTER;
        valueTV.setLayoutParams(params);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        // Glide.with(getApplicationContext()).pauseRequests();
        super.onDestroy();
    }
}
