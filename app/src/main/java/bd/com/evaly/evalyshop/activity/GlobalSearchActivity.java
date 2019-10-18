package bd.com.evaly.evalyshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
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
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.AutoCompleteAdapter;
import bd.com.evaly.evalyshop.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.adapter.SearchFilterAdapter;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.TransactionItem;
import bd.com.evaly.evalyshop.models.ProductListItem;
import bd.com.evaly.evalyshop.models.SearchFilterItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class GlobalSearchActivity extends BaseActivity {

    String searchSlug="product",selectedCategory="";
    Spinner searchType,sortby;
    AutoCompleteTextView searchText;
    ProductGrid productGrid;
    ArrayList<TabsItem> itemList;
    ArrayList<ProductListItem> itemListProduct,itemListProductWithCategory;
    TabsAdapter adapter;
    ProductGridAdapter adapterProduct,adapterProductCategory;
    RecyclerView recyclerView, filterRecyclerView;
    StickyScrollView nestedSV;
    int page=1,filterCategoryPage=1,gotProductCounter=0;
    ProgressBar progressBar;
    LinearLayout filter,drawerRel;
    DrawerLayout mDrawerLayout;
    Map<String,String> map,categoryMapping;
    String filterURL="";
    Button applyFilterBtn;
    ArrayList<String> categoryList,allCategories;
    ArrayAdapter<String> arrayAdapter;
    boolean searched=false;
    LinearLayout noResult;
    RequestQueue rq;
    ImageView searchClear;
    EditText minimum,maximum;
    String highestSlug="";
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

        filter=findViewById(R.id.filter);
        applyFilterBtn=findViewById(R.id.header);
        noResult = findViewById(R.id.noResult);
        minimum=findViewById(R.id.minimum);
        maximum=findViewById(R.id.maximum);
        clearFilters=findViewById(R.id.clear);
        // clearFilters.setVisibility(View.GONE);

        categoryList=new ArrayList<>();
        allCategories=new ArrayList<>();
        categoryMapping=new HashMap<>();
        arrayAdapter=new ArrayAdapter<>(this,R.layout.category_filter_item,categoryList);

        rq = Volley.newRequestQueue(GlobalSearchActivity.this);
        map=new HashMap<>();
        mDrawerLayout=findViewById(R.id.layout_drawer);
        drawerRel=findViewById(R.id.drawer_rel);



        filterRecyclerView = findViewById(R.id.filterRecycler);

        LinearLayoutManager managerFilter=new LinearLayoutManager(this);
        filterRecyclerView.setLayoutManager(managerFilter);

        filterItemlist = new ArrayList<>();
        filterAdapter = new SearchFilterAdapter(this, filterItemlist);

        filterRecyclerView.setAdapter(filterAdapter);







        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (searchType.getSelectedItemPosition() == 1 || searchType.getSelectedItemPosition() == 2) {
                    Toast.makeText(GlobalSearchActivity.this, "You can use filters while searching for products", Toast.LENGTH_LONG).show();
                    return;
                }


                if(mDrawerLayout.isDrawerOpen(drawerRel)){
                    mDrawerLayout.closeDrawer(drawerRel);
                }else {
                    mDrawerLayout.openDrawer(drawerRel);
                }
            }
        });

        applyFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

                for (int i = 0; i < filterItemlist.size(); i++){

                    SearchFilterItem item = filterItemlist.get(i);

                    if(item.isSelected()){


                        if (item.getType().equals("category_name"))
                            category_name.put("category_name:"+item.getName());
                        else if(item.getType().equals("brand_name"))
                            brand_name.put("brand_name:"+item.getName());
                        else if(item.getType().equals("color"))
                            color.put("color:"+item.getName());


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

                if(min.equals("") && max.equals(""))
                    priceFilterJSON = "[\"price>=10\"]";
                else if (min.equals(""))
                    priceFilterJSON = "[\"price>=10\",\"price<="+ max  +"\"]";
                else if (max.equals(""))
                    priceFilterJSON = "[\"price>="+ min  +"\"]";
                else
                    priceFilterJSON = "[\"price>="+ min  +"\",\"price<="+max+"\"]";


                Log.d("json price filter", priceFilterJSON);


                itemListProduct.clear();


                fromFilter = true;


                page = 1;

                getProducts(page);



                if(mDrawerLayout.isDrawerOpen(drawerRel)){
                    mDrawerLayout.closeDrawer(drawerRel);
                }else {
                    mDrawerLayout.openDrawer(drawerRel);
                }
            }
        });

        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    page=1;

                filterJSON = "[]";
                priceFilterJSON = "[\"price>=10\"]";
                minimum.setText("");
                maximum.setText("");

                itemListProduct.clear();
                page=1;
                getSearchedItems(page);

                for (int i = 0; i < filterItemlist.size(); i++){

                    SearchFilterItem item = filterItemlist.get(i);
                    item.setSelected(false);
                    filterAdapter.notifyItemChanged(i);

                }


                if(mDrawerLayout.isDrawerOpen(drawerRel)){
                    mDrawerLayout.closeDrawer(drawerRel);
                }else {
                    mDrawerLayout.openDrawer(drawerRel);
                }
            }
        });

        // searchText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

        recyclerView = findViewById(R.id.recycle);
        nestedSV = findViewById(R.id.sticky);
        LinearLayoutManager manager = new LinearLayoutManager(GlobalSearchActivity.this);
        itemList = new ArrayList<>();
        itemListProduct = new ArrayList<>();
        int type=1;

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
                    searched=false;
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
                    searched=false;
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
                if(iType == 1) {
                    searchType.setSelection(0);
                    searchText.setAdapter(adapterAuto);
                }
                else if (iType == 2) {
                    searchType.setSelection(2);
                    searchText.setAdapter(null);
                }
                else if (iType == 3) {
                    searchType.setSelection(1);
                    searchText.setAdapter(null);
                }
            }
        } else
            getProducts(1);

        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
                noResult.setVisibility(View.GONE);

                fromFilter = false;

                page=1;
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
            }
        });

        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchClear.getTag().equals("clear"))
                    searchText.setText("");
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    searchClear.setImageDrawable(getDrawable(R.drawable.ic_close_smallest));
                    searchClear.setTag("clear");
                }
                else {
                    searchClear.setImageDrawable(getDrawable(R.drawable.ic_search));
                    searchClear.setTag("search");
                }
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
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
                    int type=1;
                    if(searchSlug.equals("product")){
                        type=1;
                        // don't let show suggestion after search
                        AutoCompleteAdapter adapterAuto = new AutoCompleteAdapter(GlobalSearchActivity.this, android.R.layout.simple_list_item_1);
                        searchText.setAdapter(adapterAuto);
                    }else if(searchSlug.equals("shop")){
                        type=3;
                        searchText.setAdapter(null);
                    }else if(searchSlug.equals("brands")){
                        type=3;
                        searchText.setAdapter(null);
                    }
                    if(type == 1) {
                        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setAdapter(adapterProduct);
                    }
                    else {
                        mLayoutManager = new GridLayoutManager(GlobalSearchActivity.this, 3);
                        recyclerView.setAdapter(adapter);
                    }
                    recyclerView.setLayoutManager(mLayoutManager);
                    page=1;

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
            }
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //handler.post(runnableCode);

        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
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
                }
            });
        }







        sortingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchType.getSelectedItemPosition() == 1 || searchType.getSelectedItemPosition() == 2) {
                    Toast.makeText(GlobalSearchActivity.this, "You can use sorting while searching for products", Toast.LENGTH_LONG).show();
                    return;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchActivity.this);
                builder.setTitle("Sort Products By\n");
                final String[] a={"Relevance","Price low to high","Price high to low"};

                int checkItem = 0;

                if (sortIndexName.equals("products_price_asc"))
                    checkItem = 1;
                else if (sortIndexName.equals("products_price_desc"))
                    checkItem = 2;




                builder.setSingleChoiceItems(a, checkItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i==0)
                            sortIndexName = "products";
                        else if(i==1)
                            sortIndexName = "products_price_asc";
                        else if (i==2)
                            sortIndexName = "products_price_desc";


                        page = 1;
                        itemListProduct.clear();
                        getProducts(page);


                        dialogInterface.dismiss();

                    }
                });

//                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {

//                    }
//                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builder.show();


            }
        });





    }




    public void loadNextSearchPage(){
        getSearchedItems(++page);
    }

    public void loadNextShops(){
        getShops(++page);
    }

    public void loadNextBrands(){
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




    public void getProducts(int page){
        //noFilterText();
//        if (searchText.getText().toString().equals("")) {
//            progressBar.setVisibility(View.VISIBLE);
//            productGrid = new ProductGrid(GlobalSearchActivity.this, findViewById(R.id.recycle), "root", findViewById(R.id.progressBar));
//            return;
//        }

        String searchQuery = searchText.getText().toString();

//        try {
//            searchQuery=URLEncoder.encode(searchQuery, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }




        Map<String,Object> paramsMap = new HashMap<String,Object>();

        paramsMap.put("query", searchQuery);
        paramsMap.put("maxValuesPerFacet", "20");
        paramsMap.put("page", page-1);
        paramsMap.put("highlightPreTag", "<ais-highlight-0000000000>");
        paramsMap.put("highlightPostTag", "</ais-highlight-0000000000>");
        paramsMap.put("facets", "[\"price\",\"category_name\",\"brand_name\",\"color\"]");
        paramsMap.put("tagFilters", "");
        paramsMap.put("facetFilters", filterJSON);
        paramsMap.put("numericFilters", priceFilterJSON);




        JSONObject payload = new JSONObject();

        JSONObject paramsJson = new JSONObject();

        try {

            paramsJson.put("indexName", sortIndexName);
            paramsJson.put("params", Utils.urlEncodeUTF8(paramsMap));

            payload.putOpt("requests", new JSONArray());
            payload.getJSONArray("requests").put(paramsJson);

        } catch (Exception e){}




        if (page < 2){



            RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapterProduct);
            recyclerView.setLayoutManager(mLayoutManager);

        }


        String url="https://eza2j926q5-dsn.algolia.net/1/indexes/*/queries?x-algolia-application-id=EZA2J926Q5&x-algolia-api-key=ca9abeea06c16b7d531694d6783a8f04";
        Log.d("json", url);


        if (isLoading)
            return;
        isLoading = true;


        Log.d("json params",payload.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {


                    if (nestedSV!=null)
                        nestedSV.fling(0);

                    Log.d("json search_result",response.toString());


                    try {
                        isLoading = false;
                        JSONArray jsonArray = response.getJSONArray("results");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        searched = true;
                        JSONArray productList = jsonObject.getJSONArray("hits");
                        ArrayList<String> slugStore = new ArrayList<>();

                        for (int i = 0; i < productList.length(); i++) {

                            JSONObject ob = productList.getJSONObject(i);
                            ProductListItem tabsItem = new ProductListItem();
                            tabsItem.setName(ob.getString("name"));
                            tabsItem.setThumbnailSM(ob.getString("product_image").replace("\n", "").replace("\r", ""));
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setPriceMax(ob.getInt("max_price"));
                            tabsItem.setPriceMin(ob.getInt("price"));

                            if (!slugStore.contains(ob.getString("slug"))) {
                                itemListProduct.add(tabsItem);
                                adapterProduct.notifyItemInserted(itemListProduct.size());
                            }

                            slugStore.add(ob.getString("slug"));

                        }


                        if(jsonObject.getInt("nbHits") > 10) {
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

                            if((productList.length() < 1 && page==1) || jsonObject.getInt("nbHits") < 2) {
                                nestedSV.setBackgroundColor(Color.parseColor("#ffffff"));
                                noResult.setVisibility(View.VISIBLE);

                                Glide.with(GlobalSearchActivity.this)
                                        .load(R.drawable.ic_search_not_found)
                                        .apply(new RequestOptions().override(800, 800))
                                        .into((ImageView) findViewById(R.id.noImage));


                            } else {

                                nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
                                noResult.setVisibility(View.GONE);
                            }
                        } catch (Exception e){

                        }


                        progressBar.setVisibility(View.INVISIBLE);
                        if(!searchSlug.equals("product") && page == 1)
                            loadNextSearchPage();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading=false;
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);


    }


    public void getShops(int p){

        if (isLoading)
            return;
        isLoading = true;

        String query = "root";
        if(!searchText.getText().toString().equals(""))
            query = searchText.getText().toString();


        String url = UrlUtils.BASE_URL+"custom/shops/?page="+p+"&limit=15";
        if(!query.equals("root"))
            url = UrlUtils.BASE_URL+"custom/shops/?page="+p+"&limit=15&search="+query;


        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {

                    if (nestedSV!=null)
                        nestedSV.fling(0);

                    isLoading = false;
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);

                                TabsItem tabsItem = new TabsItem();
                                tabsItem.setTitle(ob.getString("shop_name"));
                                tabsItem.setImage(ob.getString("shop_image"));
                                tabsItem.setSlug(ob.getString("slug"));
                                tabsItem.setCategory("root");
                                itemList.add(tabsItem);

                                adapter.notifyItemInserted(itemList.size());

                        }
                        if(page == 1)
                            loadNextShops();
                        else
                            progressBar.setVisibility(View.INVISIBLE);


                        if (itemList.size() > 0)
                            setNotFound(false);
                        else
                            setNotFound(true);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }




    public void getBrands(int p){
        if (isLoading)
            return;
        isLoading = true;


        String query = "root";
        if(!searchText.getText().toString().equals(""))
            query = searchText.getText().toString();


        String url = UrlUtils.BASE_URL+"public/brands/?page="+p+"&limit=15";
        if(!query.equals("root"))
            url = UrlUtils.BASE_URL+"public/brands/?page="+p+"&limit=15&search="+query;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {


                    if (nestedSV!=null)
                        nestedSV.fling(0);

                    try {
                        isLoading = false;
                        JSONArray jsonArray = response.getJSONArray("results");
                        // Log.d("category_brands",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                                TabsItem tabsItem = new TabsItem();
                                tabsItem.setTitle(ob.getString("name"));
                                tabsItem.setImage(ob.getString("image_url"));
                                tabsItem.setSlug(ob.getString("slug"));
                                tabsItem.setCategory("root");
                                itemList.add(tabsItem);
                                adapter.notifyItemInserted(itemList.size());

                        }
                        if(page == 1)
                            loadNextBrands();
                        else
                            progressBar.setVisibility(View.INVISIBLE);


                        if (itemList.size() > 0)
                            setNotFound(false);
                        else
                            setNotFound(true);




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        request.setShouldCache(false);

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(request);
    }



    public void setNotFound(boolean notFound){

        if(notFound) {
            nestedSV.setBackgroundColor(Color.parseColor("#ffffff"));
            noResult.setVisibility(View.VISIBLE);

            Glide.with(GlobalSearchActivity.this)
                    .load(R.drawable.ic_search_not_found)
                    .apply(new RequestOptions().override(800, 800))
                    .into((ImageView) findViewById(R.id.noImage));


        } else {

            nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
            noResult.setVisibility(View.GONE);
        }


    }

    

    public void noFilterText(){
        
        TextView valueTV = new TextView(this);
        valueTV.setText("No filter attribute available for the searched item");
        valueTV.setTextColor(Color.BLACK);
        valueTV.setTextSize(16f);
        valueTV.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,100,0,0);
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
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
