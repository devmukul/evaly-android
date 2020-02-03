package bd.com.evaly.evalyshop.ui.product.productDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.databinding.ActivityViewProductBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.newsfeed.CreatePostModel;
import bd.com.evaly.evalyshop.models.product.ProductShareModel;
import bd.com.evaly.evalyshop.models.product.ProductVariants;
import bd.com.evaly.evalyshop.models.product.Products;
import bd.com.evaly.evalyshop.models.shop.AvailableShop;
import bd.com.evaly.evalyshop.models.wishlist.WishList;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.chat.invite.ContactShareAdapter;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.RoomWIthRxViewModel;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.AvailableShopAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.SpecificationAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.ViewProductSliderAdapter;
import bd.com.evaly.evalyshop.ui.product.productList.ProductGrid;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class ViewProductActivity extends BaseActivity {

    private String slug = "", category = "", name = "", productImage = "";
    private int productPrice;
    private ArrayList<Products> products;
    private ArrayList<AvailableShop> availableShops;
    private ArrayList<String> sliderImages;
    private ViewProductSliderAdapter sliderAdapter;
    private Map<String, String> map, shopMap;
    private CartEntity cartItem;
    private RoomWIthRxViewModel xmppViewModel;
    private String productJson = "{}";
    private Context context;
    private WishList wishListItem;
    private boolean isAddedToWishList;
    private int raters[];
    private ArrayList<String> specTitle, specValue;
    private SpecificationAdapter specificationAdapter;
    private String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";
    private boolean isShopLoading = false, callFirst = false;
    private String shareURL = "https://evaly.com.bd";
    private TreeMap<String, TreeMap<String, String>> varyingMap;
    private String shopURL = "";
    private ArrayList<String> parameters, values;
    private RequestQueue rqShop;
    private RequestQueue rq;
    private Map<Integer, ProductVariants> productVariantsMap;
    private ArrayList<Integer> buttonIDs;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog newsfeedShareDialog;
    private AppDatabase appDatabase;
    private WishListDao wishListDao;
    private CartDao cartDao;
    private ActivityViewProductBinding binding;
    private ViewProductViewModel viewModel;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = ViewModelProviders.of(this).get(ViewProductViewModel.class);
        xmppViewModel = ViewModelProviders.of(this).get(RoomWIthRxViewModel.class);

        context = this;

        setSupportActionBar(binding.zToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("View Product");

        productPrice = getIntent().getIntExtra("product_price", -1);
        productImage = getIntent().getStringExtra("product_image");

        rq = Volley.newRequestQueue(context);

        appDatabase = AppDatabase.getInstance(this);
        wishListDao = appDatabase.wishListDao();
        cartDao = appDatabase.cartDao();

        cartDao.getLiveCount().observe(this, integer -> binding.cartCount.setText(integer.toString()));


        wishListItem = new WishList();
        cartItem = new CartEntity();

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


        binding.specList.setLayoutManager(new LinearLayoutManager(this));
        specTitle = new ArrayList<>();
        specValue = new ArrayList<>();
        specificationAdapter = new SpecificationAdapter(this, specTitle, specValue);
        binding.specList.setAdapter(specificationAdapter);

        products = new ArrayList<>();
        availableShops = new ArrayList<>();
        sliderImages = new ArrayList<>();
        map = new TreeMap<>();

        sliderAdapter = new ViewProductSliderAdapter(context, this, sliderImages, new ArrayList<>());
        binding.sliderPager.setAdapter(sliderAdapter);
        binding.sliderIndicator.setupWithViewPager(binding.sliderPager, true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            slug = extras.getString("product_slug");
            name = extras.getString("product_name");
            binding.productName.setVisibility(View.GONE);

            if (name != null) {
                binding.productName.setText(Html.fromHtml(name));
                binding.collapsingToolbar.setTitle(Html.fromHtml(name));
            }

            binding.collapsingToolbar.setVisibility(View.INVISIBLE);
            getProductData(slug);
        }

        binding.back.setOnClickListener(v -> onBackPressed());
        hideProductHolder();

        binding.cart.setOnClickListener(v -> {
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        });


        binding.addToWishlist.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            if (isAddedToWishList) {

                binding.addToWishlist.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                Executors.newSingleThreadExecutor().execute(() -> wishListDao.deleteBySlug(wishListItem.getProductSlug()));
                Toast.makeText(context, "Removed from wish list", Toast.LENGTH_SHORT).show();
                isAddedToWishList = false;

            } else {

                WishListEntity wishListEntity = new WishListEntity();
                wishListEntity.setSlug(wishListItem.getProductSlug());
                wishListEntity.setName(wishListItem.getName());
                wishListEntity.setImage(wishListItem.getImage());
                wishListEntity.setPrice(wishListItem.getPrice());
                wishListEntity.setTime(calendar.getTimeInMillis());

                Executors.newSingleThreadExecutor().execute(() -> wishListDao.insert(wishListEntity));
                Toast.makeText(context, "Added to wish list", Toast.LENGTH_SHORT).show();
                binding.addToWishlist.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                isAddedToWishList = true;
            }
        });


        binding.addToCart.setOnClickListener(v -> {

            binding.stickyScroll.postDelayed(() -> {
                binding.appBar.setExpanded(false, true);
                int scrollTo = ((View) binding.avlshop.getParent()).getTop() + binding.avlshop.getTop();
                binding.stickyScroll.smoothScrollTo(0, scrollTo - binding.productName.getHeight());
            }, 100);

        });

        binding.share.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(context, binding.share);
            popup.getMenuInflater().inflate(R.menu.share_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_share_contacts:
                        if (CredentialManager.getUserName().equals("") && CredentialManager.getPassword().equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Please login to share products", Toast.LENGTH_LONG).show();
                        } else {
                            shareWithContacts();
                        }
                        break;
                    case R.id.action_share_newsfeed:
                        if (CredentialManager.getUserName().equals("") && CredentialManager.getPassword().equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Please login to share products", Toast.LENGTH_LONG).show();
                        } else {
                            shareToNewsFeed(shareURL);
                        }
                        break;
                    case R.id.action_share_other:
                        try {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareURL);
                            startActivity(Intent.createChooser(sharingIntent, "Share Via"));
                        } catch (Exception e) {
                            Toast.makeText(context, "Can't share the product.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                return true;
            });
            popup.show();
        });
    }

    private void shareToNewsFeed(String shareURL) {
        newsfeedShareDialog = new BottomSheetDialog(ViewProductActivity.this, R.style.BottomSheetDialogTheme);
        newsfeedShareDialog.setContentView(R.layout.share_to_newsfeed_view);

        View bottomSheetInternal = newsfeedShareDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(ViewProductActivity.this, bottomSheetInternal);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    bottomSheet.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        newsfeedShareDialog.setCanceledOnTouchOutside(false);
        ImageView ivBack = newsfeedShareDialog.findViewById(R.id.close);
        EditText etBody = newsfeedShareDialog.findViewById(R.id.etBody);
        Button btnShare = newsfeedShareDialog.findViewById(R.id.btnShare);

        RichLinkView linkPreview = newsfeedShareDialog.findViewById(R.id.linkPreview);

        linkPreview.setLink(shareURL, new ViewListener() {
            @Override
            public void onSuccess(boolean status) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        btnShare.setOnClickListener(view -> {
            HashMap<String, String> data = new HashMap<>();
            data.put("body", etBody.getText().toString());
            data.put("url", shareURL);
            data.put("type", "product");

            createPost(new Gson().toJson(data));

        });

        ivBack.setOnClickListener(view -> newsfeedShareDialog.dismiss());

        newsfeedShareDialog.show();
    }

    private void createPost(String postBody) {
        CreatePostModel createPostModel = new CreatePostModel("", "public", postBody, null);
        HashMap<String, CreatePostModel> data = new HashMap<>();
        data.put("post", createPostModel);

        ViewDialog dialog = new ViewDialog(this);
        AuthApiHelper.createPost(data, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
            @Override
            public void onDataFetched(retrofit2.Response<JsonObject> response) {

                dialog.showDialog();
                if (response.code() == 200 || response.code() == 201) {
//                    createPostDialog.dismiss();
                    dialog.hideDialog();
                    newsfeedShareDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Your post has successfully posted. It may take few hours to get approved.", Toast.LENGTH_LONG).show();

                } else if (response.code() == 401) {
                    AuthApiHelper.refreshToken(ViewProductActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            createPost(postBody);
                        }

                        @Override
                        public void onFailed(int status) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    dialog.hideDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int status) {
                dialog.hideDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void shareWithContacts() {
        bottomSheetDialog = new BottomSheetDialog(ViewProductActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.share_with_contact_view);

        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(ViewProductActivity.this, bottomSheetInternal);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    bottomSheet.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        bottomSheetDialog.setCanceledOnTouchOutside(false);
        RecyclerView rvContacts = bottomSheetDialog.findViewById(R.id.rvContacts);
        ImageView ivBack = bottomSheetDialog.findViewById(R.id.back);
        EditText etSearch = bottomSheetDialog.findViewById(R.id.etSearch);
        TextView tvCount = bottomSheetDialog.findViewById(R.id.tvCount);
        LinearLayout llSend = bottomSheetDialog.findViewById(R.id.llSend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewProductActivity.this);
        rvContacts.setLayoutManager(layoutManager);
        runOnUiThread(() -> {
            xmppViewModel.loadRosterList(CredentialManager.getUserName(), 1, 10000);
            xmppViewModel.rosterList.observe(this, rosterTables -> {
                List<RosterTable> selectedRosterList = new ArrayList<>();
                List<RosterTable> rosterList = rosterTables;
                ContactShareAdapter contactShareAdapter = new ContactShareAdapter(ViewProductActivity.this, rosterList, new ContactShareAdapter.OnUserSelectedListener() {
                    @Override
                    public void onUserSelected(Object object, boolean status) {
                        RosterTable table = (RosterTable) object;

                        if (status && !selectedRosterList.contains(table)) {
                            selectedRosterList.add(table);
                        } else {
                            if (selectedRosterList.contains(table)) {
                                selectedRosterList.remove(table);
                            }
                        }

                        tvCount.setText("(" + selectedRosterList.size() + ") ");
                    }
                });

                llSend.setOnClickListener(view -> {
                    ProductShareModel model = new ProductShareModel(slug, name, productImage, String.valueOf(productPrice));

                    if (AppController.getmService() != null) {
                        if (AppController.getmService().xmpp.isLoggedin()) {
                            try {
                                for (RosterTable rosterTable : selectedRosterList) {
                                    ChatItem chatItem = new ChatItem(new Gson().toJson(model), CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), CredentialManager.getUserData().getImage_sm(), CredentialManager.getUserData().getFirst_name(), System.currentTimeMillis(), CredentialManager.getUserName() + "@" + Constants.XMPP_HOST, rosterTable.id, Constants.TYPE_PRODUCT, true, "");
                                    AppController.getmService().xmpp.sendMessage(chatItem);
                                }
                                for (int i = 0; i < rosterList.size(); i++) {
                                    rosterList.get(i).isSelected = false;
                                }
                                contactShareAdapter.notifyDataSetChanged();
                                selectedRosterList.clear();
                                tvCount.setText("(" + selectedRosterList.size() + ") ");
                                Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_LONG).show();
                            } catch (SmackException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AppController.getmService().xmpp.connect();
                        }
                    }
                });

                rvContacts.setAdapter(contactShareAdapter);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        rvContacts.getRecycledViewPool().clear();
                        contactShareAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        rvContacts.getRecycledViewPool().clear();
                        contactShareAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            });

        });

        ivBack.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    public void hideProductHolder() {
        binding.productInfo.setVisibility(View.GONE);
        binding.relatedTitle.setVisibility(View.GONE);
        binding.progressBar.setBackgroundColor(Color.parseColor("#ffffff"));
        binding.stickyButtons.setVisibility(View.GONE);
    }

    public void showProductHolder() {
        binding.productInfo.setVisibility(View.VISIBLE);
        binding.relatedTitle.setVisibility(View.VISIBLE);
        binding.stickyButtons.setVisibility(View.VISIBLE);
        binding.progressBar.setBackgroundColor(Color.parseColor("#fafafa"));
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

    public void getProductData(String slug) {

        String url = UrlUtils.BASE_URL + "public/products/" + slug + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                responseMain -> {
                    binding.productName.setVisibility(View.VISIBLE);
                    binding.collapsingToolbar.setVisibility(View.VISIBLE);
                    try {

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
                            name = product_variants.getJSONObject(0).getString("product_name");
                            binding.productName.setText(name);
                            binding.collapsingToolbar.setTitle(Html.fromHtml(name));

                            if (response.getJSONArray("attributes").length() == 0) {
                                JSONArray variantArr = product_variants.getJSONObject(i).getJSONArray("product_images");
                                for (int j = 0; j < variantArr.length(); j++) {
                                    sliderImages.add(variantArr.getString(j));
                                    sliderAdapter.notifyDataSetChanged();
                                }
                                if (variantArr.length() == 1)
                                    binding.sliderIndicator.setVisibility(View.GONE);

                                getAvailableShops(product_variants.getJSONObject(i).getInt("variant_id"));

                            } else {
                                JSONArray variantArr = product_variants.getJSONObject(i).getJSONArray("product_images");
                                int variantID = product_variants.getJSONObject(i).getInt("variant_id");
                                name = product_variants.getJSONObject(i).getString("product_name");
                                category = product_variants.getJSONObject(i).getString("category_slug");
                                int minPrice = (int) Math.round(product_variants.getJSONObject(i).getDouble("min_price"));
                                int maxPrice = (int) Math.round(product_variants.getJSONObject(i).getDouble("max_price"));
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
                            binding.colorRel.setVisibility(View.VISIBLE);
                        }


                        if (product_variants.length() == 0) {
                            Toast.makeText(this, "Product is not found!", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                        JSONObject firstVariant = product_variants.getJSONObject(0);


                        binding.productName.setText(Html.fromHtml(firstVariant.getString("product_name")));
                        if (firstVariant.getString("product_description").equals("")) {
                            binding.descriptionView.setVisibility(View.GONE);
                            binding.descriptionRel.setVisibility(View.GONE);
                        } else {
                            binding.tvDescription.setText(firstVariant.getString("product_description"));
                        }


                        JSONArray productSpecifications = response.getJSONArray("product_specifications");


                        for (int i = 0; i < productSpecifications.length(); i++) {
                            specTitle.add(productSpecifications.getJSONObject(i).getString("specification_name"));
                            specValue.add(productSpecifications.getJSONObject(i).getString("specification_value"));
                            specificationAdapter.notifyItemInserted(specTitle.size());
                        }


                        if (specValue.size() < 2) {
                            binding.specView.setVisibility(View.GONE);
                            binding.specRel.setVisibility(View.GONE);
                        }

                        Executors.newSingleThreadExecutor().execute(() -> {
                            int c = wishListDao.checkExists(slug);
                            runOnUiThread(() -> {
                                if (c > 0) {
                                    binding.addToWishlist.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                                    isAddedToWishList = true;
                                }
                            });
                        });

                        productJson = firstVariant.toString();

                        showProductHolder();

                        // for cart order
                        cartItem.setName(firstVariant.getString("product_name"));
                        cartItem.setImage(firstVariant.getString("color_image"));
                        cartItem.setSlug(slug);

                        // for wishlist

                        int price = 0;
                        try {
                            price = (int) Math.round(firstVariant.getDouble("min_price"));
                        } catch (Exception ignored) {

                        }

                        wishListItem.setId("0");
                        wishListItem.setName(firstVariant.getString("product_name"));
                        //wishListItem.setImage(firstVariant.getString("thumbnail"));
                        wishListItem.setImage(firstVariant.getJSONArray("product_images").getString(0));
                        wishListItem.setProductSlug(slug);
                        wishListItem.setPrice(String.valueOf(price));


                        binding.collapsingToolbar.setTitle(firstVariant.getString("product_name"));

                        binding.sku.setText(firstVariant.getString("sku"));


                        binding.sliderPager.setVisibility(View.VISIBLE);

                        shareURL = "https://evaly.com.bd/products/" + slug;

                        ProductGrid productGrid = new ProductGrid(context, binding.products, firstVariant.getString("category_slug"), findViewById(R.id.progressBar));

                        if (binding.stickyScroll != null) {
                            productGrid.setScrollView(binding.stickyScroll);
                            binding.stickyScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                        try {
                                            binding.progressBar.setVisibility(View.VISIBLE);
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
                }, error -> error.printStackTrace());
        // RequestQueue rq = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            button.setOnClickListener(v -> {

                try {

                    ProductVariants productVariants = productVariantsMap.get(button.getId());
                    sliderImages.clear();
                    sliderAdapter.notifyDataSetChanged();
                    binding.sliderPager.setAdapter(null);

                    for (int j = 0; j < productVariants.getImages().size(); j++) {
                        sliderImages.add(productVariants.getImages().get(j));
                        sliderAdapter.notifyDataSetChanged();
                    }

                    binding.sliderPager.setAdapter(sliderAdapter);

                    sliderAdapter.notifyDataSetChanged();

                    for (int k = 0; k < buttonIDs.size(); k++) {
                        try {
                            Button btn = findViewById(buttonIDs.get(k));
                            if (btn.getTag().equals(button.getTag())) {
                                GradientDrawable drawable1 = (GradientDrawable) btn.getBackground();
                                drawable1.setColor(Color.parseColor("#eeeeee"));
                            }
                        } catch (Exception e) {

                        }
                    }
                    GradientDrawable drawable1 = (GradientDrawable) button.getBackground();
                    drawable1.setColor(Color.parseColor("#d1ecf2"));
                    getAvailableShops(productVariants.getVariantID());

                } catch (Exception e) {

                    Crashlytics.logException(e);

                    Toast.makeText(context, "Couldn't select the variant", Toast.LENGTH_SHORT).show();

                }

            });
            button.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    v.performClick();
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
        binding.variationParent.addView(layout2);
        //variationParentLayout.addView(variationLayout);
    }


    public void getAvailableShops(int variationID) {


        if (rqShop != null) {
            rqShop.cancelAll(this);
        }

        binding.progressBarShop.setVisibility(View.VISIBLE);

        availableShops.clear();
        binding.availableShops.setAdapter(null);

        isShopLoading = true;

        Log.d("json_shop", shopURL);
        shopURL = UrlUtils.BASE_URL + "public/product/shops/" + variationID + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, shopURL, new JSONObject(),
                response -> {

                    Logger.json(String.valueOf(response));
                    isShopLoading = false;
                    availableShops.clear();
                    AvailableShopAdapter adapterm = new AvailableShopAdapter(context, binding.rootView, availableShops, cartDao, cartItem);
                    binding.availableShops.setAdapter(adapterm);
                    ArrayList<String> shopname = new ArrayList<>();
                    binding.progressBarShop.setVisibility(View.GONE);
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
                                    item.setShopSlug(ob.getString("shop_slug"));

                                    if (ob.getString("discounted_price").equals("null"))
                                        item.setPrice(String.valueOf((int) Math.round(ob.getDouble("price"))));
                                    else
                                        item.setPrice(String.valueOf((int) Math.round(ob.getDouble("discounted_price"))));

                                    item.setSlug(slug);
                                    item.setProductId(ob.getString("shop_item_id"));

                                    if (!ob.getString("discount_value").equals("null"))
                                        item.setDiscountValue(Math.round(ob.getDouble("discount_value")));
                                    else
                                        item.setDiscountValue(0.0);

                                    item.setPhone(phone);
                                    item.setAddress(ob.getString("shop_address"));
                                    item.setShopJson(ob.toString());
                                    item.setStock(true);
                                    item.setMaximumPrice(String.valueOf((int) Math.round(ob.getDouble("price"))));
                                    availableShops.add(item);
                                    adapterm.notifyItemInserted(availableShops.size());
                                }

                            } catch (Exception e) {

                                Log.e("json expection", e.toString());

                                continue;

                            }
                        }

                        if (availableShops.size() < 1) {
                            binding.empty.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> error.printStackTrace());


        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        request.setTag(this);
        rq.getCache().clear();
        rq.add(request);
    }


}
