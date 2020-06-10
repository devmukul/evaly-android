package bd.com.evaly.evalyshop.ui.product.productDetails;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jivesoftware.smack.SmackException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.Post;
import bd.com.evaly.evalyshop.models.product.ProductShareModel;
import bd.com.evaly.evalyshop.models.product.Products;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributesItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.product.productDetails.Data;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductSpecificationsItem;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductVariantsItem;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;
import bd.com.evaly.evalyshop.models.shop.AvailableShop;
import bd.com.evaly.evalyshop.models.wishlist.WishList;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.chat.invite.ContactShareAdapter;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.RoomWIthRxViewModel;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.AvailableShopAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.ColorButtonAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.SizeButtonAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.SpecificationAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.ViewProductSliderAdapter;
import bd.com.evaly.evalyshop.ui.product.productList.ProductGrid;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.util.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class ViewProductActivity extends BaseActivity {

    private String slug = "", category = "", name = "", productImage = "";
    private double productPrice;
    private ArrayList<Products> products;
    private ArrayList<AvailableShop> availableShops;
    private ArrayList<String> sliderImages;
    private ViewProductSliderAdapter sliderAdapter;
    private Map<String, String> map, shopMap;
    private CartEntity cartItem;
    private RoomWIthRxViewModel xmppViewModel;
    private Context context;
    private WishList wishListItem;
    private boolean isAddedToWishList;
    private SpecificationAdapter specificationAdapter;
    private String shareURL = "https://evaly.com.bd";
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog newsfeedShareDialog;
    private WishListDao wishListDao;
    private CartDao cartDao;
    private ActivityViewProductBinding binding;
    private ViewProductViewModel viewModel;
    private List<ProductSpecificationsItem> specificationsItemList = new ArrayList<>();
    private List<AttributesItem> productAttributesItemList;
    private List<ProductVariantsItem> productVariantsItemList;
    private int variantKey1 = 0, variantKey2 = 0;
    private int shopItemId = 0;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private LocationManager lm;

    private String shopSlug = null;
    private AvailableShopModel toRemoveModel = null;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onLoginFailed(String msg) {
            if (msg.contains("already logged in")) {

            }
        }

    };

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
        xmppEventReceiver = mChatApp.getEventReceiver();
        binding = ActivityViewProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setLightStatusBar(this);

        binding.selectedShopHolder.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(this).get(ViewProductViewModel.class);
        xmppViewModel = new ViewModelProvider(this).get(RoomWIthRxViewModel.class);

        context = this;

        setSupportActionBar(binding.zToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("View Product");

        productPrice = getIntent().getDoubleExtra("product_price", -1);
        productImage = getIntent().getStringExtra("product_image");
        slug = getIntent().getStringExtra("slug");

        if (getIntent().hasExtra("shop_slug"))
            shopSlug = getIntent().getStringExtra("shop_slug");

        AppDatabase appDatabase = AppDatabase.getInstance(this);
        wishListDao = appDatabase.wishListDao();
        cartDao = appDatabase.cartDao();

        cartDao.getLiveCount().observe(this, integer -> binding.cartCount.setText(integer.toString()));

        wishListItem = new WishList();
        cartItem = new CartEntity();
        shopMap = new HashMap<>();

        binding.specList.setLayoutManager(new LinearLayoutManager(this));
        specificationAdapter = new SpecificationAdapter(this, specificationsItemList);
        binding.specList.setAdapter(specificationAdapter);

        products = new ArrayList<>();
        availableShops = new ArrayList<>();
        sliderImages = new ArrayList<>();
        map = new TreeMap<>();

        sliderAdapter = new ViewProductSliderAdapter(context, this, sliderImages);
        binding.sliderPager.setAdapter(sliderAdapter);
        binding.sliderIndicator.setupWithViewPager(binding.sliderPager, true);

        viewModel.productDetailsModel.observe(this, this::populateShopDetails);

        viewModel.ratingSummary.observe(this, this::populateRatingsSummary);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey("product_slug"))
                slug = extras.getString("product_slug");

            if (extras.containsKey("product_name"))
                name = extras.getString("product_name");

            // binding.productName.setVisibility(View.GONE);

            if (name != null) {
                binding.productName.setText(Html.fromHtml(name));
                binding.collapsingToolbar.setTitle(Html.fromHtml(name));
            }

            viewModel.getProductDetails(slug);
            viewModel.loadRatings(slug);
            binding.collapsingToolbar.setVisibility(View.INVISIBLE);
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
                binding.stickyScroll.smoothScrollTo(0, getRelativeTop(binding.stickyScroll, binding.scrollToDivider) - 50);
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

        binding.availableShopsTypeHolder.setOnClickListener(v -> {
            String[] type = new String[]{"All", "Nearest"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(type, (dialog, which) -> {

                if (type[which].equals("All")) {
                    getAvailableShops(shopItemId);
                } else {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        loadNearestShopByLocation();
                    else
                        ActivityCompat.requestPermissions(this, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                1212);
                }

            });
            builder.show();

        });
    }

    private void populateRatingsSummary(JsonObject jsonObject) {

        ReviewSummaryModel summaryModel = new Gson().fromJson(jsonObject.get("data").getAsJsonObject(), ReviewSummaryModel.class);

        int colors[] = new int[]{
                Color.parseColor("#0e9d58"),
                Color.parseColor("#0e9d58"),
                Color.parseColor("#a6ba5d"),
                Color.parseColor("#ef7e14"),
                Color.parseColor("#d36259")};

        int raters[] = new int[]{
                summaryModel.getStar5(),
                summaryModel.getStar4(),
                summaryModel.getStar3(),
                summaryModel.getStar2(),
                summaryModel.getStar1(),
        };

        binding.review.ratingAverage.setText(summaryModel.getAvgRating()+"");
        binding.review.ratingCounter.setText(summaryModel.getTotalRatings()+"");
        binding.review.ratingBar.setRating((float) summaryModel.getAvgRating());

        if (summaryModel.getTotalRatings() == 0)
            summaryModel.setTotalRatings(1);

        binding.review.ratingReviews.createRatingBars(summaryModel.getTotalRatings(), BarLabels.STYPE1, colors, raters);

        binding.viewAllReviews.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReviewsActivity.class);
            intent.putExtra("ratingJson", jsonObject.toString());
            intent.putExtra("item_value", slug);
            intent.putExtra("type",  "product");
            startActivity(intent);
        });
    }

    public int getRelativeTop(View rootView, View childView) {
        if (childView.getParent() == rootView) return childView.getTop();
        else return childView.getTop() + getRelativeTop(rootView, (View) childView.getParent());
    }

    private void loadNearestShopByLocation() {

        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        if (!gps_enabled && !network_enabled) {
            buildAlertMessageNoGps(this);
            return;
        }

        binding.availableShops.setAdapter(null);
        binding.progressBarShop.setVisibility(View.VISIBLE);
        binding.empty.setVisibility(View.GONE);

        LocationUtils locationUtils = new LocationUtils();
        locationUtils.getLocation(this, new LocationUtils.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                runOnUiThread(() -> {
                    if (location != null) {
                        binding.tvShopType.setText("Nearest");
                        getNearestAvailableShops(shopItemId, location.getLongitude(), location.getLatitude());
                    } else {
                        Toast.makeText(context, "Couldn't find location, please try again later", Toast.LENGTH_SHORT).show();
                        getAvailableShops(shopItemId);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        xmppEventReceiver.setListener(xmppCustomEventListener);
    }

    private void buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("To see nearest shops, please turn on device location")
                .setTitle("Enable Location")
                .setCancelable(false)
                .setPositiveButton("Go to Settings", (dialog, id) -> context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1212:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    loadNearestShopByLocation();
                } else {
                    Toast.makeText(context, "Location permission is required to see nearby shops. Go to app settings and allow location permission of Evaly app. ", Toast.LENGTH_LONG).show();
                }
                break;
            case 0:
                break;
        }
    }

    public void populateShopDetails(ProductDetailsModel productDetailsModel) {

        Data data = productDetailsModel.getData();
        productAttributesItemList = data.getAttributes();
        productVariantsItemList = data.getProductVariants();
        List<ProductSpecificationsItem> productSpecificationsItemList = data.getProductSpecifications();

        showProductHolder();

        if (productVariantsItemList.size() == 0) {
            Toast.makeText(context, "Product is not available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.sliderPager.setVisibility(View.VISIBLE);
        binding.collapsingToolbar.setVisibility(View.VISIBLE);

        ProductVariantsItem firstProductVariantsItem = productVariantsItemList.get(0);

        specificationsItemList.clear();
        specificationsItemList.add(new ProductSpecificationsItem("Brand", firstProductVariantsItem.getBrandName(), 0));
        specificationsItemList.add(new ProductSpecificationsItem("Category", firstProductVariantsItem.getCategoryName(), 1));

        for (int i = 0; i < productSpecificationsItemList.size(); i++) {
            if (!productSpecificationsItemList.get(i).getSpecificationName().equals(""))
                specificationsItemList.add(productSpecificationsItemList.get(i));
        }
        specificationAdapter.notifyDataSetChanged();
        shareURL = "https://evaly.com.bd/products/" + slug;
        populateProductByVariant(firstProductVariantsItem);
//
//        for (int i = 0; i < productVariantsItemList.size(); i++) {
//            for (int j = 0; j < productVariantsItemList.get(i).getAttributeValues().size(); j++) {
//                int variantValue = productVariantsItemList.get(i).getAttributeValues().get(j);
//                for (int k = 0; k < productAttributesItemList.size(); k++) {
//                    for (int l = 0; l < productAttributesItemList.get(k).getAttributeValues().size(); l++) {
//                        AttributeValuesItem attributeValuesItem = productAttributesItemList.get(k).getAttributeValues().get(l);
//
//                        if (attributeValuesItem.getKey() == variantValue)
//                            attributeValuesItem.setColor_image(productVariantsItemList.get(i).getColorImage());
//                    }
//                }
//            }
//        }

        if (productAttributesItemList.size() > 0) {
            if (productAttributesItemList.get(0).getAttributeData().getType().equalsIgnoreCase("Size"))
                populateSizeOption(productAttributesItemList.get(0).getAttributeData().getValues());
            else if (productAttributesItemList.get(0).getAttributeName().equalsIgnoreCase("Color"))
                populateColorOption(productAttributesItemList.get(0).getAttributeData().getValues());
        }

        if (productAttributesItemList.size() > 1) {
            if (productAttributesItemList.get(1).getAttributeData().getType().equalsIgnoreCase("Color"))
                populateColorOption(productAttributesItemList.get(1).getAttributeData().getValues());
            else if (productAttributesItemList.get(1).getAttributeName().equalsIgnoreCase("Size"))
                populateSizeOption(productAttributesItemList.get(1).getAttributeData().getValues());
        }

    }

    private void populateProductByVariant(ProductVariantsItem item) {

        name = item.getProductName();
        binding.productName.setText(name);
        binding.collapsingToolbar.setTitle(Html.fromHtml(name));

        if (item.getProductDescription().equals("")) {
            binding.descriptionRel.setVisibility(View.GONE);
            binding.descriptionView.setVisibility(View.GONE);
        } else
            binding.tvDescription.setText(item.getProductDescription().trim());

        binding.sku.setText(item.getSku().toUpperCase());

        sliderImages.clear();
        sliderImages.addAll(item.getProductImages());
        sliderAdapter.notifyDataSetChanged();

        if (sliderImages.size() == 1) {
            binding.sliderIndicator.setVisibility(View.GONE);
            binding.sliderIndicatorBg.setVisibility(View.GONE);
        }

        shopItemId = item.getVariantId();

        getAvailableShops(item.getVariantId());
        getRelatedProducts(item.getCategorySlug());

        Executors.newSingleThreadExecutor().execute(() -> {
            int c = wishListDao.checkExists(slug);
            runOnUiThread(() -> {
                if (c > 0) {
                    binding.addToWishlist.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                    isAddedToWishList = true;
                }
            });
        });

        // for cart order
        cartItem.setName(name);
        cartItem.setImage(item.getColorImage());
        cartItem.setSlug(slug);

        // for wishlist

        int price = 0;
        try {
            price = Math.round(item.getMinPrice());
        } catch (Exception ignored) {

        }
        wishListItem.setId("0");
        wishListItem.setName(name);
        wishListItem.setImage(item.getColorImage());
        wishListItem.setProductSlug(slug);
        wishListItem.setPrice(String.valueOf(price));

    }

    private void populateSizeOption(List<AttributeValuesItem> attribute_values) {

        if (attribute_values.size() > 0 && productVariantsItemList.get(0).getAttributeValues().size() > 0) {
            binding.variant1Holder.setVisibility(View.VISIBLE);
            for (int i = 0; i < attribute_values.size(); i++) {
                if (productAttributesItemList.size() > 1 &&
                        productVariantsItemList.get(0).getAttributeValues().size() > 1) {
                    if (attribute_values.get(i).getKey() == productVariantsItemList.get(0).getAttributeValues().get(0) ||
                            attribute_values.get(i).getKey() == productVariantsItemList.get(0).getAttributeValues().get(1)) {
                        attribute_values.get(i).setSelected(true);
                        variantKey1 = attribute_values.get(i).getKey();
                    }
                } else {
                    if (attribute_values.get(i).getKey() == productVariantsItemList.get(0).getAttributeValues().get(0)) {
                        attribute_values.get(i).setSelected(true);
                        variantKey1 = attribute_values.get(i).getKey();
                    }
                }
            }
        }

        SizeButtonAdapter adapter = new SizeButtonAdapter(attribute_values, this, object -> {
            AttributeValuesItem attributesValue = (AttributeValuesItem) object;
            if (productAttributesItemList.size() == 1) {
                for (int i = 0; i < productVariantsItemList.size(); i++) {
                    ProductVariantsItem variantItem = productVariantsItemList.get(i);
                    for (int j = 0; j < variantItem.getAttributeValues().size(); j++) {
                        int attrValue = variantItem.getAttributeValues().get(j);
                        if (attrValue == attributesValue.getKey()) {
                            populateProductByVariant(productVariantsItemList.get(i));
                        }
                    }
                }
            } else if (productAttributesItemList.size() == 2) {

                for (int i = 0; i < productVariantsItemList.size(); i++) {
                    ProductVariantsItem variantItem = productVariantsItemList.get(i);
                    if (variantItem.getAttributeValues().size() > 1) {
                        int key1 = variantItem.getAttributeValues().get(0);
                        int key2 = variantItem.getAttributeValues().get(1);
                        if ((key1 == attributesValue.getKey() && key2 == variantKey2) ||
                                (key2 == attributesValue.getKey() && key1 == variantKey2)) {

                            populateProductByVariant(productVariantsItemList.get(i));
                            variantKey1 = attributesValue.getKey();
                            break;
                        }
                    }
                }
            }
        });
        binding.rvVariant1.setAdapter(adapter);
    }

    private void populateColorOption(List<AttributeValuesItem> attribute_values) {
        if (attribute_values.size() > 0 && productVariantsItemList.get(0).getAttributeValues().size() > 0) {
            binding.variant2Holder.setVisibility(View.VISIBLE);
            for (int i = 0; i < attribute_values.size(); i++) {
                if (productAttributesItemList.size() > 1 && productVariantsItemList.get(0).getAttributeValues().size() > 1) {

                    if (attribute_values.get(i).getKey() == productVariantsItemList.get(0).getAttributeValues().get(0) ||
                            attribute_values.get(i).getKey() == productVariantsItemList.get(0).getAttributeValues().get(1)) {
                        attribute_values.get(i).setSelected(true);
                        variantKey2 = attribute_values.get(i).getKey();
                    }
                } else {
                    if (attribute_values.get(i).getKey() == productVariantsItemList.get(0).getAttributeValues().get(0)) {
                        attribute_values.get(i).setSelected(true);
                        variantKey2 = attribute_values.get(i).getKey();
                    }
                }
            }
        }

        ColorButtonAdapter adapter = new ColorButtonAdapter(attribute_values, this, object -> {
            AttributeValuesItem attributesValue = (AttributeValuesItem) object;
            if (productAttributesItemList.size() == 1) {
                for (int i = 0; i < productVariantsItemList.size(); i++) {
                    ProductVariantsItem variantItem = productVariantsItemList.get(i);
                    for (int j = 0; j < variantItem.getAttributeValues().size(); j++) {
                        int attrValue = variantItem.getAttributeValues().get(j);
                        if (attrValue == attributesValue.getKey()) {
                            populateProductByVariant(productVariantsItemList.get(i));
                        }
                    }
                }
            } else if (productAttributesItemList.size() == 2) {
                for (int i = 0; i < productVariantsItemList.size(); i++) {
                    ProductVariantsItem variantItem = productVariantsItemList.get(i);
                    if (variantItem.getAttributeValues().size() > 1) {
                        int key1 = variantItem.getAttributeValues().get(0);
                        int key2 = variantItem.getAttributeValues().get(1);

                        if ((key1 == attributesValue.getKey() && key2 == variantKey1) || (key2 == attributesValue.getKey() && key1 == variantKey1)) {
                            populateProductByVariant(productVariantsItemList.get(i));

                            variantKey2 = attributesValue.getKey();
                            break;
                        }
                    }
                }
            }
        });
        binding.rvVariant2.setAdapter(adapter);
    }

    private void getRelatedProducts(String categorySlug) {
        ProductGrid productGrid = new ProductGrid(context, binding.products, categorySlug, binding.progressBar);
        productGrid.setListener(new ProductListener() {
            @Override
            public void onSuccess(int count) {
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void buyNow(String product_slug) {

            }
        });
    }

    private void inflateShopDetails(AvailableShopModel shop) {

        binding.selectedShopHolder.setVisibility(View.VISIBLE);
        binding.shopName.setText(shop.getShopName());

        Glide.with(this)
                .asBitmap()
                .placeholder(R.drawable.ic_evaly_placeholder)
                .load(shop.getShopImage())
                .into(binding.shopLogo);

        if (shop.getShopAddress() == null || shop.getShopAddress().equals(""))
            binding.shopLocationHolder.setVisibility(View.GONE);
        else
            binding.shopLocation.setText(shop.getShopAddress());

        if (shop.getContactNumber() == null || shop.getContactNumber().equals("") || shop.getContactNumber().equals("0"))
            binding.shopPhoneHolder.setVisibility(View.GONE);
        else
            binding.shopPhone.setText(shop.getContactNumber());

        binding.avlshop.setText(R.string.also_available_at);
        productPrice = shop.getPrice();

        if (shop.getDiscountedPrice() == null ||
                shop.getDiscountedPrice() < 1 ||
                shop.getDiscountedPrice() >= productPrice
        ) {
            binding.price.setText(Utils.formatPriceSymbol(shop.getPrice()));
            binding.maxPrice.setVisibility(View.GONE);
        } else {
            productPrice = shop.getDiscountedPrice();
            binding.maxPrice.setVisibility(View.VISIBLE);
            binding.maxPrice.setText(Utils.formatPriceSymbol(shop.getPrice()));
            binding.price.setText(Utils.formatPriceSymbol(shop.getDiscountedPrice()));
            binding.maxPrice.setPaintFlags(binding.maxPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        binding.selectedShopClickHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", shop.getShopSlug());
            intent.putExtra("shop_name", shop.getShopName());
            intent.putExtra("category", shop.getShopSlug());
            context.startActivity(intent);
        });

        binding.addCart.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            String price = Utils.formatPrice(shop.getPrice());

            if (shop.getDiscountedPrice() != null)
                if (shop.getDiscountedPrice() > 0)
                    price = Utils.formatPrice(shop.getDiscountedPrice());

            String sellerJson = new Gson().toJson(shop);

            CartEntity cartEntity = new CartEntity();
            cartEntity.setName(name);
            cartEntity.setImage(productImage);
            cartEntity.setPriceRound(price);
            cartEntity.setTime(calendar.getTimeInMillis());
            cartEntity.setShopJson(sellerJson);
            cartEntity.setQuantity(1);
            cartEntity.setShopSlug(shop.getShopSlug());
            cartEntity.setSlug(slug);
            cartEntity.setProductID(String.valueOf(shop.getShopItemId()));

            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
                if (dbItem.size() == 0)
                    cartDao.insert(cartEntity);
                else
                    cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + 1);
            });

            Snackbar snackBar = Snackbar.make(binding.rootView, "Added to cart", 1500);
            snackBar.setAction("Go to Cart", view -> {
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);
                snackBar.dismiss();
            });
            snackBar.show();
        });

        binding.buyNow.setOnClickListener(view -> {
            BuyNowFragment buyNowFragment = BuyNowFragment.createInstance(cartItem, toRemoveModel);
            buyNowFragment.show(getSupportFragmentManager(), "Buy Now");
        });
    }


    public void getAvailableShops(int variationID) {

        binding.progressBarShop.setVisibility(View.VISIBLE);
        availableShops.clear();
        binding.availableShops.setAdapter(null);
        binding.empty.setVisibility(View.GONE);
        binding.tvShopType.setText(R.string.all);

        viewModel.availableShops.observe(this, response -> inflateAvailableShops(response, false));
        viewModel.getAvailableShops(variationID);

    }

    public void getNearestAvailableShops(int variationID, double longitude, double latitude) {
        binding.progressBarShop.setVisibility(View.VISIBLE);
        binding.empty.setVisibility(View.GONE);
        availableShops.clear();
        binding.availableShops.setAdapter(null);

        viewModel.availableNearestShops.observe(this, response -> inflateAvailableShops(response, true));
        viewModel.getNearestAvailableShops(variationID, longitude, latitude);
    }

    private void inflateAvailableShops(CommonDataResponse<List<AvailableShopModel>> response, boolean isNearestShops) {

        toRemoveModel = null;

        List<AvailableShopModel> list = new ArrayList<>(response.getData());

        if (shopSlug != null || response.getData().size() == 1) {
            for (AvailableShopModel model : list) {
                if (model.getShopSlug().equals(shopSlug) || list.size() == 1)
                    toRemoveModel = model;
            }
            if (toRemoveModel != null) {
                list.remove(toRemoveModel);
                inflateShopDetails(toRemoveModel);
            } else {
                binding.selectedShopHolder.setVisibility(View.GONE);
                binding.avlshop.setText(R.string.available_at_shop);
            }
        }

        AvailableShopAdapter adapter = new AvailableShopAdapter(context, binding.rootView, list, cartDao, cartItem);
        binding.availableShops.setAdapter(adapter);
        binding.progressBarShop.setVisibility(View.GONE);

        if ((shopSlug != null || response.getData().size() == 1) && toRemoveModel != null) {
            if (list.size() == 0)
                binding.availableShopsHolder.setVisibility(View.GONE);
            if (toRemoveModel.getDiscountedPrice() == 0 && toRemoveModel.getPrice() == 0) {
                binding.selectedShopHolder.setVisibility(View.GONE);
                binding.avlshop.setText(R.string.available_at_shop);
                if (list.size() == 0) {
                    binding.availableShopsHolder.setVisibility(View.VISIBLE);
                    binding.empty.setVisibility(View.VISIBLE);
                }
            }
        } else if (response.getData().size() < 1) {
            binding.empty.setVisibility(View.VISIBLE);
            if (isNearestShops)
                binding.tvNoShop.setText("This product is currently not \navailable at any nearest shop");
            else
                binding.tvNoShop.setText("This product is currently \nnot available at any shop");
        }
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
        CreatePostModel createPostModel = new CreatePostModel();
        Post post = new Post();
        post.setBody(postBody);
        post.setType("public");
        createPostModel.setPost(post);

        ViewDialog dialog = new ViewDialog(this);
        dialog.showDialog();

        if (!viewModel.createPostResponse.hasObservers())
            viewModel.createPostResponse.observe(this, jsonObject -> {

                if (jsonObject == null) {
                    dialog.hideDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                } else {
                    dialog.hideDialog();
                    newsfeedShareDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Your post has successfully posted. It may take few hours to get approved.", Toast.LENGTH_LONG).show();
                }
            });
        viewModel.createPost(createPostModel);
    }

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
        } else {
            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
            }
        }
    }

    private void shareWithContacts() {

        if (xmppHandler != null) {
            if (!xmppHandler.isLoggedin() || !xmppHandler.isConnected()) {
                startXmppService();
            }
        } else {
            startXmppService();
        }

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
                ContactShareAdapter contactShareAdapter = new ContactShareAdapter(ViewProductActivity.this, rosterList, (object, status) -> {
                    RosterTable table = (RosterTable) object;

                    if (status && !selectedRosterList.contains(table)) {
                        selectedRosterList.add(table);
                    } else {
                        if (selectedRosterList.contains(table)) {
                            selectedRosterList.remove(table);
                        }
                    }

                    tvCount.setText("(" + selectedRosterList.size() + ") ");
                });

                llSend.setOnClickListener(view -> {
                    ProductShareModel model = new ProductShareModel(slug, name, productImage, String.valueOf(productPrice));

                    if (xmppHandler.isLoggedin()) {
                        for (RosterTable rosterTable : selectedRosterList) {

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("p_slug", slug);
                                jsonObject.put("p_name", name);
                                jsonObject.put("p_image", productImage);
                                jsonObject.put("p_price", String.valueOf(productPrice));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
                            ChatItem chatItem = new ChatItem(jsonObject.toString(), CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), CredentialManager.getUserData().getImage_sm(), CredentialManager.getUserData().getFirst_name(), System.currentTimeMillis(), CredentialManager.getUserName() + "@" + Constants.XMPP_HOST, rosterTable.id, Constants.TYPE_PRODUCT, true, "");
                            chatItem.setReceiver_name(rosterTable.name);
                            if (rosterTable.imageUrl != null && !rosterTable.imageUrl.isEmpty()) {
                                chatItem.setReceiver_image(rosterTable.imageUrl);
                            }

                            try {
                                xmppHandler.sendMessage(chatItem);
                            } catch (SmackException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < rosterList.size(); i++) {
                            rosterList.get(i).isSelected = false;
                        }
                        contactShareAdapter.notifyDataSetChanged();
                        selectedRosterList.clear();
                        tvCount.setText("(" + selectedRosterList.size() + ") ");
                        Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_LONG).show();

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
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            activity.getWindow().setStatusBarColor(Color.BLACK);
        }
    }


}
