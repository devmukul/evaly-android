package bd.com.evaly.evalyshop.activity;


import bd.com.evaly.evalyshop.BaseActivity;

public class GlobalSearchActivityOld extends BaseActivity {

    /*
    String searchSlug="product",selectedCategory="";
    Spinner searchType,categorySpinner;
    AutoCompleteTextView searchText;
    ProductGrid productGrid;
    ArrayList<TabsItem> itemList;
    ArrayList<ProductListItem> itemListProduct,itemListProductWithCategory;
    TabsAdapter adapter;
    ProductGridAdapter adapterProduct,adapterProductCategory;
    RecyclerView recyclerView;
    StickyScrollView nestedSV;
    int page=1,filterCategoryPage=1,gotProductCounter=0;
    ProgressBar progressBar;
    LinearLayout filter,drawerRel;
    DrawerLayout mDrawerLayout;
    Map<String,String> map,categoryMapping;
    String filterURL="";
    Button filterButton;
    View linearLayout;
    ArrayList<String> categoryList,allCategories;
    ArrayAdapter<String> arrayAdapter;
    boolean searched=false;
    LinearLayout noResult;
    RequestQueue rq;
    ImageView searchClear;
    EditText minimum,maximum;
    String highestSlug="";
    boolean isLoading = false;
    RelativeLayout clearFilters;

    String userAgent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);
        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }
        searchClear = findViewById(R.id.searchClear);
        progressBar = findViewById(R.id.progressBar);
        searchType = findViewById(R.id.search_type);
        searchText = findViewById(R.id.search_text);
        filter=findViewById(R.id.filter);
        filterButton=findViewById(R.id.header);
        linearLayout =  findViewById(R.id.lin);
        categorySpinner=findViewById(R.id.category_spinner);
        noResult = findViewById(R.id.noResult);
        minimum=findViewById(R.id.minimum);
        maximum=findViewById(R.id.maximum);
        clearFilters=findViewById(R.id.clear_filters);
        clearFilters.setVisibility(View.GONE);

        categoryList=new ArrayList<>();
        allCategories=new ArrayList<>();
        categoryMapping=new HashMap<>();
        categorySpinner.setVisibility(View.GONE);
        arrayAdapter=new ArrayAdapter<>(this,R.layout.category_filter_item,categoryList);
        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoryName=parent.getItemAtPosition(position).toString();
                if(searched){
                    if(categoryName.equals("All Categories") && searched){
                        recyclerView.setAdapter(adapterProduct);
                    }else{
                        minimum.setVisibility(View.GONE);
                        maximum.setVisibility(View.GONE);
                        minimum.setText("");
                        maximum.setText("");
                        selectedCategory=categoryName;
                        itemListProductWithCategory=new ArrayList<>();
                        adapterProductCategory=new ProductGridAdapter(GlobalSearchActivityOld.this,itemListProductWithCategory);
                        recyclerView.setAdapter(adapterProductCategory);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                        filterCategoryPage=1;
                        getProductsWithCategories(filterCategoryPage);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rq = Volley.newRequestQueue(GlobalSearchActivityOld.this);
        map=new HashMap<>();
        mDrawerLayout=findViewById(R.id.layout_drawer);
        drawerRel=findViewById(R.id.drawer_rel);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawerLayout.isDrawerOpen(drawerRel)){
                    mDrawerLayout.closeDrawer(drawerRel);
                }else {
                    mDrawerLayout.openDrawer(drawerRel);
                }
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters.setVisibility(View.VISIBLE);
                if(filterURL.equals("")){
                    int minPrice=0,maxPrice=0;
                    if(!minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
                        minPrice=Integer.parseInt(minimum.getText().toString());
                        maxPrice=Integer.parseInt(maximum.getText().toString());
                    }else if(!minimum.getText().toString().equals("") && maximum.getText().toString().equals("")){
                        minPrice=Integer.parseInt(minimum.getText().toString());
                    }else if(minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
                        maxPrice=Integer.parseInt(maximum.getText().toString());
                    }
                    if(minPrice!=0 || maxPrice!=0){
                        try{
                            if(searchText.getText().toString().equals("")) {
                                productGrid = new ProductGrid(GlobalSearchActivityOld.this, findViewById(R.id.recycle), "root", findViewById(R.id.progressBar), 3, minPrice, maxPrice);
                                if (nestedSV != null) {
                                    nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                        @Override
                                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                            String TAG = "nested_sync";
                                            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                                Log.i(TAG, "BOTTOM SCROLL");
                                                if(!isLoading){
                                                    try {
                                                        progressBar.setVisibility(View.VISIBLE);
                                                        productGrid.loadNextPageWithPrice();
                                                    } catch (Exception e) {
                                                        Log.e("load more product", e.toString());
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }else{
                                if(itemListProduct.size()>0){
                                    itemListProduct.clear();
                                    RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                                    recyclerView.setAdapter(adapterProduct);
                                }
                                page=1;
                                gotProductCounter=1;
                                getSearchedProductsWithPrice(page);
                            }
                        }catch(Exception e){
                            Toast.makeText(GlobalSearchActivityOld.this, "Please enter valid minimum and maximum price", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    //page=1;
                    getFilteredItems(page);
                }
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
                clearFilters.setVisibility(View.GONE);
                if(searchText.getText().toString().equals("")){
                    getProducts();
                }else{
                    page=1;
                    filterURL="";
                    if(itemListProduct.size()>0){
                        itemListProduct.clear();
                        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setAdapter(adapterProduct);
                    }
                    getSearchedItems(page);
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
        LinearLayoutManager manager = new LinearLayoutManager(GlobalSearchActivityOld.this);
        itemList = new ArrayList<>();
        itemListProduct = new ArrayList<>();
        int type=1;
        if(searchSlug.equals("product")){
            type=1;
            filter.setVisibility(View.VISIBLE);
        }else if(searchSlug.equals("shop")){
            type=3;
            filter.setVisibility(View.GONE);
        }else if(searchSlug.equals("brands")){
            type=3;
            filter.setVisibility(View.GONE);
        }
        adapter = new TabsAdapter(GlobalSearchActivityOld.this, this, itemList, type);
        adapterProduct = new ProductGridAdapter(GlobalSearchActivityOld.this, itemListProduct);

        recyclerView.setLayoutManager(manager);

        searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                searchText.setText("");
                if (parent.getItemAtPosition(position).equals("Products")) {
                    searchSlug = "product";
                    filter.setVisibility(View.VISIBLE);
                    ((LinearLayout) linearLayout).removeAllViews();
                    if(searched){
                        categorySpinner.setVisibility(View.VISIBLE);
                        categoryList.clear();
                        categoryList.add(0,"All Categories");
                        categorySpinner.setSelection(arrayAdapter.getPosition("All Categories"));
                    }
                    getProducts();
                } else if (parent.getItemAtPosition(position).equals("Shops")) {
                    filterURL = "";
                    noFilterText();
                    filter.setVisibility(View.GONE);
                    searchSlug = "shop";
                    page = 1;
                    searched=false;
                    categorySpinner.setVisibility(View.GONE);
                    itemList = new ArrayList<>();
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new TabsAdapter(GlobalSearchActivityOld.this, GlobalSearchActivityOld.this, itemList, 3);
                    recyclerView.setAdapter(adapter);
                    getShops(page);
                } else if (parent.getItemAtPosition(position).equals("Brands")) {
                    filterURL = "";
                    noFilterText();
                    filter.setVisibility(View.GONE);
                    searchSlug = "brands";
                    page = 1;
                    searched=false;
                    categorySpinner.setVisibility(View.GONE);
                    itemList = new ArrayList<>();
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new TabsAdapter(GlobalSearchActivityOld.this, GlobalSearchActivityOld.this, itemList, 2);
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

        AutoCompleteAdapter adapterAuto = new AutoCompleteAdapter(GlobalSearchActivityOld.this, android.R.layout.simple_list_item_1);
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
            getProducts();

        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
                noResult.setVisibility(View.GONE);
                ((LinearLayout) linearLayout).removeAllViews();
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
                if(searchText.getText().toString().equals("")){
                    searched=false;
                    categorySpinner.setVisibility(View.GONE);
                }
                categoryList.clear();
                allCategories.clear();
                getSearchedItems(page);
                minimum.setText("");
                maximum.setText("");
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
                    ((LinearLayout) linearLayout).removeAllViews();
                    filterURL = "";
                    minimum.setText("");
                    maximum.setText("");
                    itemList.clear();
                    itemListProduct.clear();
                    Log.d("json", "Enter pressed");
                    RecyclerView.LayoutManager mLayoutManager;
                    int type=1;
                    if(searchSlug.equals("product")){
                        type=1;
                        // don't let show suggestion after search
                        AutoCompleteAdapter adapterAuto = new AutoCompleteAdapter(GlobalSearchActivityOld.this, android.R.layout.simple_list_item_1);
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
                        mLayoutManager = new GridLayoutManager(GlobalSearchActivityOld.this, 3);
                        recyclerView.setAdapter(adapter);
                    }
                    recyclerView.setLayoutManager(mLayoutManager);
                    page=1;
                    if(searchText.getText().toString().equals("")){
                        searched=false;
                        categorySpinner.setVisibility(View.GONE);
                    }else{
                        //getSearchedCategories();
                    }
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
    }

    public void getProductsWithCategories(int p){
        getFilterAttributes(categoryMapping.get(selectedCategory));
        final boolean[] gotProduct = {false};
        String url="https://api-prod.evaly.com.bd/api/product"+"/?&search="+searchText.getText().toString()+"&page="+p;
        Log.d("abcdefg",url);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            if(ob.getJSONObject("category").getString("slug").equals(categoryMapping.get(selectedCategory))){
                                ProductListItem tabsItem = new ProductListItem();
                                tabsItem.setName(ob.getString("name"));
                                tabsItem.setThumbnailSM(ob.getString("thumbnail"));
                                try {
                                    tabsItem.setPriceMin((int) ob.getJSONObject("price_range").getDouble("price__min"));
                                    tabsItem.setPriceMax((int) ob.getJSONObject("price_range").getDouble("price__max"));
                                } catch (Exception e) {
                                    tabsItem.setPriceMin(0);
                                    tabsItem.setPriceMax(0);
                                }
                                tabsItem.setSlug(ob.getString("slug"));
                                tabsItem.setCategorySlug(ob.getJSONObject("category").getString("slug"));
                                itemListProductWithCategory.add(tabsItem);
                                adapterProductCategory.notifyItemInserted(itemListProductWithCategory.size());
                                gotProduct[0] =true;
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        if(itemListProductWithCategory.size()<10 && filterCategoryPage<=5){
                            getProductsWithCategories(++filterCategoryPage);
                        }else if(!gotProduct[0] && filterCategoryPage-gotProductCounter<=5){
                            gotProductCounter=filterCategoryPage;
                            getProductsWithCategories(++filterCategoryPage);
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

            }
        });
        rq.add(request);
        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            getProductsWithCategories(++filterCategoryPage);
                        } catch (Exception e) {
                            Log.e("load more product", e.toString());
                        }

                    }
                }
            });
        }
    }

    public void getProducts(){
        //noFilterText();
        progressBar.setVisibility(View.VISIBLE);
        productGrid = new ProductGrid(GlobalSearchActivityOld.this, findViewById(R.id.recycle),"root", findViewById(R.id.progressBar));
        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            // Toast.makeText(GlobalSearchActivityOld.this, "products", Toast.LENGTH_SHORT).show();
                            if (searchSlug.equals("shop")) {
                                loadNextShops();
                            } else if (searchSlug.equals("brands")) {
                                loadNextBrands();
                            } else {
                                productGrid.loadNextPage();
                            }
                        } catch (Exception e) {
                            Log.e("load more product", e.toString());
                        }
                    }
                }
            });
        }
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

    public void getSearchedItems(int p)  {
        minimum.setText("");
        maximum.setText("");
        progressBar.setVisibility(View.VISIBLE);
        if(searchSlug.equals("product")){
            filter.setVisibility(View.VISIBLE);
        }
        if(searchText.getText().toString().equals("")){
            if(searchSlug.equals("product")){
                getProducts();
            }if(searchSlug.equals("brands")){
                //page=1;
                getBrands(page);
            }if(searchSlug.equals("shop")){
                //page=1;
                getShops(page);
            }
            return;
        }
        String textForSearch=searchText.getText().toString();
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M || android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            try {
                textForSearch=URLEncoder.encode(textForSearch, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String url="https://api-prod.evaly.com.bd/api/"+searchSlug+"/?&search="+textForSearch+"&page="+p;
        Log.d("json", url);
        if (isLoading)
            return;
        isLoading = true;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        isLoading = false;
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.d("search_result",response.toString());
                        categorySpinner.setVisibility(View.VISIBLE);
                        searched=true;
                        Log.d("json_array_length",jsonArray.length()+"");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            if(searchSlug.equals("product")) {
                                ProductListItem tabsItem = new ProductListItem();
                                tabsItem.setName(ob.getString("name"));
                                if (searchSlug.equals("product") || searchSlug.equals("brands")) {
                                    tabsItem.setThumbnailSM(ob.getString("thumbnail"));
                                } else {
                                    tabsItem.setThumbnailSM(ob.getString("logo"));
                                }
                                try {
                                    tabsItem.setPriceMin((int) ob.getJSONObject("price_range").getDouble("price__min"));
                                    tabsItem.setPriceMax((int) ob.getJSONObject("price_range").getDouble("price__max"));
                                } catch (Exception e) {
                                    tabsItem.setPriceMin(0);
                                    tabsItem.setPriceMax(0);
                                }
                                tabsItem.setSlug(ob.getString("slug"));
                                tabsItem.setCategorySlug(ob.getJSONObject("category").getString("slug"));
                                itemListProduct.add(tabsItem);
                                adapterProduct.notifyItemInserted(itemListProduct.size());
                                if(i==jsonArray.length()-1){
                                    Map<ProductListItem,Integer> frequencyMap=new HashMap<>();
                                    for(int k=0;k<itemListProduct.size();k++){
                                        if(frequencyMap.containsKey(itemListProduct.get(k))){
                                            frequencyMap.put(itemListProduct.get(k),frequencyMap.get(itemListProduct.get(k))+1);
                                        }else{
                                            frequencyMap.put(itemListProduct.get(k),1);
                                        }
                                    }
                                    int max=0;
                                    for (Map.Entry<ProductListItem, Integer> entry : frequencyMap.entrySet()) {
                                        if(entry.getValue()>max){
                                            max=entry.getValue();
                                        }
                                    }
                                    for (Map.Entry<ProductListItem, Integer> entry : frequencyMap.entrySet()) {
                                        if (entry.getValue().equals(max)) {
                                            highestSlug=entry.getKey().getCategorySlug();
                                            break;
                                        }
                                    }
                                    getFilterAttributes(highestSlug);
                                }
                                allCategories.add(ob.getJSONObject("category").getString("name"));
                                if(i==jsonArray.length()-1){
                                    Set<String> set = new HashSet<>(allCategories);
                                    categoryList.clear();
                                    categoryList.add(0,"All Categories");
                                    categoryList.addAll(set);
                                    arrayAdapter.notifyDataSetChanged();
                                    categorySpinner.setSelection(arrayAdapter.getPosition("All Categories"));
                                }
                                categoryMapping.put(ob.getJSONObject("category").getString("name"),ob.getJSONObject("category").getString("slug"));
                            } else {
                                if(i==0){
                                    noFilterText();
                                }
                                if(searchSlug.equals("brands")){
                                    //if(ob.getInt("products_count")>0){
                                    TabsItem tabsItem = new TabsItem();
                                    tabsItem.setTitle(ob.getString("name"));
                                    tabsItem.setImage(ob.getString("thumbnail"));
                                    tabsItem.setSlug(ob.getString("slug"));
                                    tabsItem.setCategory("root");
                                    itemList.add(tabsItem);
                                    adapter.notifyItemInserted(itemList.size());
                                    //}
                                }else{
                                    //if(ob.getInt("selling_items__count")>0){
                                    TabsItem tabsItem = new TabsItem();
                                    tabsItem.setTitle(ob.getString("name"));
                                    tabsItem.setImage(ob.getString("logo"));
                                    tabsItem.setSlug(ob.getString("slug"));
                                    tabsItem.setCategory("root");
                                    itemList.add(tabsItem);
                                    adapter.notifyItemInserted(itemList.size());
                                    //}
                                }
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        try {

                            if(jsonArray.length() < 1 && page==1) {
                                nestedSV.setBackgroundColor(Color.parseColor("#ffffff"));
                                noResult.setVisibility(View.VISIBLE);

                                Glide.with(GlobalSearchActivityOld.this)
                                        .load(R.drawable.ic_search_not_found)
                                        .apply(new RequestOptions().override(800, 800))
                                        .into((ImageView) findViewById(R.id.noImage));


                            } else {

                                nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
                                noResult.setVisibility(View.GONE);
                            }
                        } catch (Exception e){

                        }
                        if(!searchSlug.equals("product")){

                            categorySpinner.setVisibility(View.GONE);

                        } else{
                            categorySpinner.setVisibility(View.VISIBLE);
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
        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");


                        if(!isLoading) {
                            try {

                                progressBar.setVisibility(View.VISIBLE);

                                loadNextSearchPage();
                            } catch (Exception e) {
                                Log.e("load more product", e.toString());
                            }
                        }
                    }
                }
            });
        }
    }

    public void getSearchedProductsWithPrice(int p){
        String textForSearch=searchText.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M) {
            try {
                textForSearch=URLEncoder.encode(textForSearch, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        final boolean[] gotProduct = {false};
        String url="https://api-prod.evaly.com.bd/api/product/?&search="+textForSearch+"&page="+p;
        Log.d("json", url);
        if (isLoading)
            return;
        isLoading = true;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        isLoading = false;
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.d("search_result",response.toString());
                        categorySpinner.setVisibility(View.VISIBLE);
                        searched=true;
                        Log.d("json_array_length",jsonArray.length()+"");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            ProductListItem tabsItem = new ProductListItem();
                            tabsItem.setName(ob.getString("name"));
                            tabsItem.setThumbnailSM(ob.getString("thumbnail"));
                            try {
                                tabsItem.setPriceMin((int) ob.getJSONObject("price_range").getDouble("price__min"));
                                tabsItem.setPriceMax((int) ob.getJSONObject("price_range").getDouble("price__max"));
                            } catch (Exception e) {
                                tabsItem.setPriceMin(0);
                                tabsItem.setPriceMax(0);
                            }
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategorySlug(ob.getJSONObject("category").getString("slug"));
                            if(!minimum.getText().toString().equals("") && maximum.getText().toString().equals("")){
                                int minPrice=Integer.parseInt(minimum.getText().toString());
                                try{
                                    if(ob.getJSONObject("price_range").getDouble("price__min")>=minPrice){
                                        itemListProduct.add(tabsItem);
                                        adapterProduct.notifyItemInserted(itemListProduct.size());
                                        gotProduct[0] =true;
                                    }
                                }catch(Exception e){

                                }
                            }else if(minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
                                int maxPrice=Integer.parseInt(maximum.getText().toString());
                                try{
                                    if(ob.getJSONObject("price_range").getDouble("price__min")<=maxPrice){
                                        itemListProduct.add(tabsItem);
                                        adapterProduct.notifyItemInserted(itemListProduct.size());
                                        gotProduct[0] =true;
                                    }
                                }catch(Exception e){

                                }
                            }else if(!minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
                                int minPrice=Integer.parseInt(minimum.getText().toString());
                                int maxPrice=Integer.parseInt(maximum.getText().toString());
                                try{
                                    if(ob.getJSONObject("price_range").getDouble("price__min")>=minPrice && ob.getJSONObject("price_range").getDouble("price__min")<=maxPrice){
                                        itemListProduct.add(tabsItem);
                                        adapterProduct.notifyItemInserted(itemListProduct.size());
                                        gotProduct[0] =true;
                                    }
                                }catch(Exception e){

                                }
                            }
                            //itemListProduct.add(tabsItem);
                            //adapterProduct.notifyItemInserted(itemListProduct.size());
                            allCategories.add(ob.getJSONObject("category").getString("name"));
                            if(i==jsonArray.length()-1){
                                Set<String> set = new HashSet<>(allCategories);
                                categoryList.clear();
                                categoryList.add(0,"All Categories");
                                categoryList.addAll(set);
                                arrayAdapter.notifyDataSetChanged();
                                categorySpinner.setSelection(arrayAdapter.getPosition("All Categories"));
                                categoryMapping.put(ob.getJSONObject("category").getString("name"),ob.getJSONObject("category").getString("slug"));
                            }
                            if(i==jsonArray.length()-1){
                                Map<ProductListItem,Integer> frequencyMap=new HashMap<>();
                                for(int k=0;k<itemListProduct.size();k++){
                                    if(frequencyMap.containsKey(itemListProduct.get(k))){
                                        frequencyMap.put(itemListProduct.get(k),frequencyMap.get(itemListProduct.get(k))+1);
                                    }else{
                                        frequencyMap.put(itemListProduct.get(k),1);
                                    }
                                }
                                int max=0;
                                String highestSlug="";
                                for (Map.Entry<ProductListItem, Integer> entry : frequencyMap.entrySet()) {
                                    if(entry.getValue()>max){
                                        max=entry.getValue();
                                    }
                                }
                                for (Map.Entry<ProductListItem, Integer> entry : frequencyMap.entrySet()) {
                                    if (entry.getValue().equals(max)) {
                                        highestSlug=entry.getKey().getCategorySlug();
                                        break;
                                    }
                                }
                                getFilterAttributes(highestSlug);
                            }
                        }
                        if(itemListProduct.size()<10 && page<=5){
                            getSearchedProductsWithPrice(++page);
                        }else if(!gotProduct[0] && page-gotProductCounter<=5){
                            gotProductCounter=page;
                            getSearchedProductsWithPrice(++page);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            if(itemListProduct.size()==0 && page==5) {
                                nestedSV.setBackgroundColor(Color.parseColor("#ffffff"));
                                noResult.setVisibility(View.VISIBLE);
                                Glide.with(GlobalSearchActivityOld.this)
                                        .load(R.drawable.ic_search_not_found)
                                        .apply(new RequestOptions().override(800, 800))
                                        .into((ImageView) findViewById(R.id.noImage));
                            } else {
                                nestedSV.setBackgroundColor(Color.parseColor("#fafafa"));
                                noResult.setVisibility(View.GONE);
                            }
                        } catch (Exception e){

                        }
                        categorySpinner.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        //if(!searchSlug.equals("product") && page == 1)
                        //    loadNextSearchPage();
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
        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        if(!isLoading) {
                            try {
                                getSearchedProductsWithPrice(++page);
                            } catch (Exception e) {
                                Log.e("load more product", e.toString());
                            }
                        }
                    }
                }
            });
        }

    }

    public void getFilteredItems(int p){
        //int index=filterURL.indexOf("page=");
        //filterURL=filterURL.replace(filterURL.charAt(index+5)+"",p+"");
        Log.d("filter_url",filterURL);
        itemListProduct.clear();
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapterProduct);
        recyclerView.setLayoutManager(mLayoutManager);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, filterURL,(String) null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.d("search_result",response.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            ProductListItem tabsItem = new ProductListItem();
                            tabsItem.setName(ob.getString("name"));
                            tabsItem.setThumbnailSM(ob.getString("thumbnail"));
                            try {
                                tabsItem.setPriceMin((int) ob.getJSONObject("price_range").getDouble("price__min"));
                                tabsItem.setPriceMax((int) ob.getJSONObject("price_range").getDouble("price__max"));
                            } catch (Exception e) {
                                tabsItem.setPriceMin(0);
                                tabsItem.setPriceMax(0);
                            }
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategorySlug(ob.getJSONObject("category").getString("slug"));
                            if(!minimum.getText().toString().equals("") && maximum.getText().toString().equals("")){
                                int minPrice=Integer.parseInt(minimum.getText().toString());
                                if((int) ob.getJSONObject("price_range").getDouble("price__min")>=minPrice){
                                    itemListProduct.add(tabsItem);
                                    adapterProduct.notifyItemInserted(itemListProduct.size());
                                }
                            }else if(minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
                                int maxPrice=Integer.parseInt(maximum.getText().toString());
                                if((int) ob.getJSONObject("price_range").getDouble("price__min")<=maxPrice){
                                    itemListProduct.add(tabsItem);
                                    adapterProduct.notifyItemInserted(itemListProduct.size());
                                }
                            }else if(!minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
                                int minPrice=Integer.parseInt(minimum.getText().toString());
                                int maxPrice=Integer.parseInt(maximum.getText().toString());
                                if((int) ob.getJSONObject("price_range").getDouble("price__min")>=minPrice && (int) ob.getJSONObject("price_range").getDouble("price__min")<=maxPrice){
                                    itemListProduct.add(tabsItem);
                                    adapterProduct.notifyItemInserted(itemListProduct.size());
                                }
                            }else{
                                itemListProduct.add(tabsItem);
                                adapterProduct.notifyItemInserted(itemListProduct.size());
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

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

            }
        });
        rq.add(request);
    }

    public void getShops(int p){
        String url="https://api-prod.evaly.com.bd/api/shop/?shadow_category__slug=root&page="+p;
        Log.d("json", url);
        if (isLoading)
            return;
        isLoading = true;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    isLoading = false;
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            if(ob.getInt("selling_items__count")>0){}
                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("name"));
                            tabsItem.setImage(ob.getString("logo"));
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategory("root");
                            itemList.add(tabsItem);

                            adapter.notifyItemInserted(itemList.size());
                            Log.d("json shop item", ob.getString("name"));
                        }
                        if(page == 1)
                            loadNextShops();
                        else
                            progressBar.setVisibility(View.INVISIBLE);

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

    public void getFilterAttributes(String filterSlug){
        String url="https://api-prod.evaly.com.bd/api/attributes/?category__slug="+filterSlug;
        Log.d("filter_url",url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        if(response.length()==0){
                            noFilterText();
                        }else{
                            Log.d("filtered_attr",response.toString());
                            for(int i=0;i<response.length();i++){
                                JSONObject ob=response.getJSONObject(i);
                                getAttributeOptions(ob.getString("name"),ob.getString("slug"),filterSlug);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

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

            }
        });
        rq.add(request);
    }

    public void getAttributeOptions(String header,String slug,String filterSlug){
        ((LinearLayout) linearLayout).removeAllViews();
        String url="https://api-prod.evaly.com.bd/api/options/?attribute__slug="+slug;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        ArrayList<String> values=new ArrayList<>();
                        values.add("Select Filter");
                        for(int i=0;i<response.length();i++){
                            JSONObject ob=response.getJSONObject(i);
                            values.add(ob.getString("value"));
                            map.put(ob.getString("value"),ob.getString("slug"));
                            //Log.d("abcdefg",header+"  "+ob.getString("value"));
                            if(i==response.length()-1){
                                TextView valueTV = new TextView(this);
                                valueTV.setText(header);
                                valueTV.setTextColor(Color.BLACK);
                                valueTV.setTypeface(null, Typeface.BOLD);
                                valueTV.setTextSize(16f);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20,60,0,0);
                                valueTV.setLayoutParams(params);
                                ((LinearLayout) linearLayout).addView(valueTV);
                                Spinner spinner = new Spinner(this);
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, values){
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View v = super.getView(position, convertView, parent);
                                        TextView tv = ((TextView) v);
                                        tv.setTextColor(Color.parseColor("#777777"));
                                        tv.setTypeface(null, Typeface.NORMAL);
                                        tv.setEllipsize(TextUtils.TruncateAt.END);
                                        tv.setTextSize(16f);
                                        return v;
                                    }
                                };
                                spinner.setAdapter(spinnerArrayAdapter);
                                ((LinearLayout) linearLayout).addView(spinner);
                                View v=new View(this);
                                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                                v.setBackgroundColor(Color.parseColor("#eeeeee"));
                                ((LinearLayout) linearLayout).addView(v);
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        if(!parentView.getItemAtPosition(position).equals("Select Filter")){
                                            if(filterURL.equals("")){
                                                //filterURL="https://api-prod.evaly.com.bd/api/product_filter/?category__slug="+filterSlug+"&page="+page+"&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString());
                                                filterURL="https://api-prod.evaly.com.bd/api/product_filter/?category__slug="+filterSlug+"&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString());
                                            }else{
                                                filterURL+="&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString());
                                            }
                                        }else{
                                            if(!filterURL.equals("")){
                                                filterURL=filterURL.replace("&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString()),"");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });
                                filterButton.setClickable(true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

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

            }
        });
        rq.add(request);

    }

    public void noFilterText(){
        ((LinearLayout) linearLayout).removeAllViews();
        TextView valueTV = new TextView(this);
        valueTV.setText("No filter attribute available for the searched item");
        valueTV.setTextColor(Color.BLACK);
        valueTV.setTextSize(16f);
        valueTV.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,100,0,0);
        params.gravity = Gravity.CENTER;
        valueTV.setLayoutParams(params);
        ((LinearLayout) linearLayout).addView(valueTV);
    }

    public void getBrands(int p){
        if (isLoading)
            return;
        isLoading = true;
        String url = "https://api-prod.evaly.com.bd/api/brands/?shadow_category__slug=root&page="+p;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        isLoading = false;
                        JSONArray jsonArray = response.getJSONArray("results");
                        // Log.d("category_brands",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            if(ob.getInt("products_count")>0){
                                TabsItem tabsItem = new TabsItem();
                                tabsItem.setTitle(ob.getString("name"));
                                tabsItem.setImage(ob.getString("thumbnail"));
                                tabsItem.setSlug(ob.getString("slug"));
                                tabsItem.setCategory("root");
                                itemList.add(tabsItem);
                                adapter.notifyItemInserted(itemList.size());
                            }
                        }
                        if(page == 1)
                            loadNextBrands();
                        else
                            progressBar.setVisibility(View.INVISIBLE);
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
        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        if(!isLoading && page > 1) {
                            try {
                                progressBar.setVisibility(View.VISIBLE);
                                if (searchSlug.equals("shop")) {
                                    loadNextShops();
                                } else if (searchSlug.equals("brands")) {
                                    loadNextBrands();
                                } else {
                                    productGrid.loadNextPage();
                                }
                            } catch (Exception e) {
                                Log.e("load more product", e.toString());
                            }
                        }
                    }
                }
            });
        }
    }


*/

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
