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
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.databinding.ActivityViewProductBinding;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.Post;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributesItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopResponse;
import bd.com.evaly.evalyshop.models.product.productDetails.Data;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductSpecificationsItem;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductVariantsItem;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;
import bd.com.evaly.evalyshop.models.shop.AvailableShop;
import bd.com.evaly.evalyshop.models.wishlist.WishList;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.cart.CartViewModel;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.AvailableShopAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.SpecificationAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.adapter.ViewProductSliderAdapter;
import bd.com.evaly.evalyshop.ui.product.productDetails.bottomsheet.SkuBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.product.productDetails.controller.RelatedProductsController;
import bd.com.evaly.evalyshop.ui.product.productDetails.controller.VariantsController;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

@AndroidEntryPoint
public class ViewProductActivity extends BaseActivity<ActivityViewProductBinding, ViewProductViewModel> implements VariantsController.SelectListener, AvailableShopAdapter.AvailableShopClickListener, RelatedProductsController.ClickListener {

    @Inject
    RecommenderViewModel recommenderViewModel;

    @Inject
    PreferenceRepository preferenceRepository;

    long startTime = 0;
    ProductVariantsItem firstProductVariantsItem;
    private RelatedProductsController relatedProductsController;
    private String slug = "", category = "", name = "", productImage = "";
    private double productPrice;
    private ArrayList<AvailableShop> availableShops;
    private ArrayList<String> sliderImages;
    private ViewProductSliderAdapter sliderAdapter;
    private CartEntity cartItem;
    private Context context;
    private WishList wishListItem;
    private boolean isAddedToWishList;
    private SpecificationAdapter specificationAdapter;
    private String shareURL = "https://evaly.com.bd";
    private BottomSheetDialog newsfeedShareDialog;
    private CartViewModel cartViewModel;
    private List<ProductSpecificationsItem> specificationsItemList = new ArrayList<>();
    private List<AttributesItem> productAttributesItemList;
    private List<ProductVariantsItem> productVariantsItemList;
    private int shopItemId = 0;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private LocationManager lm;
    private String shopSlug = null;
    private String cashbackText = null;
    private AvailableShopResponse toRemoveModel = null;
    private VariantsController variantsController;

    public ViewProductActivity() {
        super(ViewProductViewModel.class, R.layout.activity_view_product);
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


    @Override
    protected void initViews() {
        startTime = System.currentTimeMillis();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setLightStatusBar(this);

        binding.selectedShopHolder.setVisibility(View.GONE);
        viewModel = new ViewModelProvider(this).get(ViewProductViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        context = this;

        setSupportActionBar(binding.zToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("View Product");

        handleIntent();

        wishListItem = new WishList();
        cartItem = new CartEntity();
        availableShops = new ArrayList<>();

        binding.specList.setLayoutManager(new LinearLayoutManager(this));
        specificationAdapter = new SpecificationAdapter(this, specificationsItemList);
        binding.specList.setAdapter(specificationAdapter);


        initProductImageSlider();
        loadFromApis();
        hideProductHolder();
        initVariantRecycler();
        initRelatedProductsRecycler();
    }

    private void loadFromApis() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("product_slug"))
                slug = extras.getString("product_slug");
            if (extras.containsKey("product_name"))
                name = extras.getString("product_name");
            if (name != null) {
                binding.productName.setText(Html.fromHtml(name));
                binding.collapsingToolbar.setTitle(Html.fromHtml(name));
            }
            viewModel.getProductDetails(slug);
            viewModel.loadRatings(slug);
            binding.collapsingToolbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void clickListeners() {

        binding.back.setOnClickListener(v -> onBackPressed());
        binding.cart.setOnClickListener(v -> {
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        });

        binding.addToWishlist.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (isAddedToWishList) {

                binding.addToWishlist.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                Executors.newSingleThreadExecutor().execute(() -> viewModel.deleteBySlugWishList(wishListItem.getProductSlug()));
                Toast.makeText(context, "Removed from wish list", Toast.LENGTH_SHORT).show();
                isAddedToWishList = false;

            } else {
                WishListEntity wishListEntity = new WishListEntity();
                wishListEntity.setSlug(wishListItem.getProductSlug());
                wishListEntity.setName(wishListItem.getName());
                wishListEntity.setImage(wishListItem.getImage());
                wishListEntity.setPrice(wishListItem.getPrice());
                wishListEntity.setTime(calendar.getTimeInMillis());

                viewModel.insertWishList(wishListEntity);
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
                    case R.id.action_share_newsfeed:
                        if (preferenceRepository.getUserName().equals("") && preferenceRepository.getPassword().equalsIgnoreCase("")) {
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

    private void initProductImageSlider() {
        sliderImages = new ArrayList<>();
        sliderAdapter = new ViewProductSliderAdapter(context, this, sliderImages);
        binding.sliderPager.setAdapter(sliderAdapter);
        binding.sliderIndicator.setupWithViewPager(binding.sliderPager, true);
    }

    private void handleIntent() {
        productPrice = getIntent().getDoubleExtra("product_price", -1);
        productImage = getIntent().getStringExtra("product_image");
        slug = getIntent().getStringExtra("slug");

        if (getIntent().hasExtra("shop_slug"))
            shopSlug = getIntent().getStringExtra("shop_slug");

        if (getIntent().hasExtra("cashback_text"))
            cashbackText = getIntent().getStringExtra("cashback_text");
    }

    private void initRelatedProductsRecycler() {
        relatedProductsController = new RelatedProductsController();
        relatedProductsController.setFilterDuplicates(true);
        relatedProductsController.setClickListener(this);
        int spacing = (int) Utils.convertDpToPixel(10, this);
        binding.products.addItemDecoration(new StaggeredSpacingItemDecoration(2, spacing, true));
        binding.products.setAdapter(relatedProductsController.getAdapter());
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.cartLiveCount.observe(this, integer -> binding.cartCount.setText(integer.toString()));

        viewModel.relatedProductLiveList.observe(this, productItems -> {
            relatedProductsController.setProductList(productItems);
            relatedProductsController.requestModelBuild();
            binding.products.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });

        viewModel.productDetailsModel.observe(this, this::populateShopDetails);
        viewModel.ratingSummary.observe(this, this::populateRatingsSummary);
    }

    private void initRecommender() {
        recommenderViewModel.insert("category",
                firstProductVariantsItem.getCategorySlug(),
                firstProductVariantsItem.getCategoryName(),
                firstProductVariantsItem.getCategoryImage());

        recommenderViewModel.insert("brand",
                firstProductVariantsItem.getBrandSlug(),
                firstProductVariantsItem.getBrandName(),
                firstProductVariantsItem.getBrandImage());
    }

    private void updateRecommender() {
        if (firstProductVariantsItem == null)
            return;

        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;

        recommenderViewModel.updateSpentTime("category",
                firstProductVariantsItem.getCategorySlug(),
                diff);

        recommenderViewModel.updateSpentTime("brand",
                firstProductVariantsItem.getBrandSlug(),
                diff);
    }

    @Override
    protected void onDestroy() {
        updateRecommender();
        super.onDestroy();
    }

    private void initVariantRecycler() {
        if (variantsController == null)
            variantsController = new VariantsController();
        variantsController.setSelectListener(this);
        binding.rvVariant.setAdapter(variantsController.getAdapter());
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

        binding.review.ratingAverage.setText(Utils.formatPrice(summaryModel.getAvgRating()));
        binding.review.ratingCounter.setText(summaryModel.getTotalRatings() + "");
        binding.review.ratingBar.setRating((float) summaryModel.getAvgRating());

        if (summaryModel.getTotalRatings() == 0)
            summaryModel.setTotalRatings(1);

        binding.review.ratingReviews.createRatingBars(summaryModel.getTotalRatings(), BarLabels.STYPE1, colors, raters);

        binding.viewAllReviews.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReviewsActivity.class);
            intent.putExtra("ratingJson", jsonObject.toString());
            intent.putExtra("item_value", slug);
            intent.putExtra("type", "product");
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

        if (productVariantsItemList.size() == 0) {
            Toast.makeText(context, "Product is not available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<ProductSpecificationsItem> productSpecificationsItemList = data.getProductSpecifications();

        if (productAttributesItemList.size() > 0) {
            binding.variantHolder.setVisibility(View.VISIBLE);
            variantsController.setList(productAttributesItemList);
            variantsController.setSelectedVariants(new ArrayList<>(productVariantsItemList.get(0).getAttributeValues()));
            variantsController.requestModelBuild();

            StringBuilder variantDetails = new StringBuilder();

            for (HashMap.Entry<String, String> entry : variantsController.getSelectedVariantsMap().entrySet()) {
                String val = entry.getKey() + ": " + entry.getValue();
                variantDetails.append(val).append(", ");
            }

            if (variantDetails != null && variantDetails.length() > 0) {
                String val = variantDetails.toString();
                val = val.replaceAll(", $", "");
                cartItem.setVariantDetails(val);
            }

        } else {
            binding.variantHolder.setVisibility(View.GONE);
        }

        showProductHolder();

        binding.sliderPager.setVisibility(View.VISIBLE);
        binding.collapsingToolbar.setVisibility(View.VISIBLE);

        firstProductVariantsItem = productVariantsItemList.get(0);

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
        viewModel.getRelatedProducts(firstProductVariantsItem.getCategorySlug());
        initRecommender();
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
        binding.skuHolder.setOnClickListener(view -> {
            SkuBottomSheetFragment fragment = SkuBottomSheetFragment.newInstance(item.getSku().toUpperCase());
            fragment.show(getSupportFragmentManager(), "SKU");
        });

        sliderImages.clear();
        sliderImages.addAll(item.getProductImages());
        sliderAdapter.notifyDataSetChanged();

        if (sliderImages.size() == 1) {
            binding.sliderIndicator.setVisibility(View.GONE);
            binding.sliderIndicatorBg.setVisibility(View.GONE);
        }

        shopItemId = item.getVariantId();

        getAvailableShops(item.getVariantId());

        Executors.newSingleThreadExecutor().execute(() -> {
            int c = viewModel.checkExistsWishList(slug);
            runOnUiThread(() -> {
                if (c > 0) {
                    binding.addToWishlist.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_color));
                    isAddedToWishList = true;
                }
            });
        });

        // for cart order
        cartItem.setName(name);
        if (item.getProductImages().size() > 0)
            cartItem.setImage(item.getProductImages().get(0));
        cartItem.setSlug(slug);
        cartItem.setTime(Calendar.getInstance().getTimeInMillis());

        // for wishlist
        int price = 0;
        try {
            price = Math.round(item.getMinPrice());
        } catch (Exception ignored) {

        }

        wishListItem.setId("0");
        wishListItem.setName(name);
        if (item.getProductImages().size() > 0)
            wishListItem.setImage(item.getProductImages().get(0));
        wishListItem.setProductSlug(slug);
        wishListItem.setPrice(String.valueOf(price));

        updateVariantDetails();
    }

    private void updateVariantDetails() {
        StringBuilder variantDetails = new StringBuilder();
        for (HashMap.Entry<String, String> entry : variantsController.getSelectedVariantsMap().entrySet()) {
            String val = entry.getKey() + ": " + entry.getValue();
            variantDetails.append(val).append(", ");
        }

        if (variantDetails != null && variantDetails.length() > 0) {
            String val = variantDetails.toString();
            val = val.replaceAll(", $", "");
            cartItem.setVariantDetails(val);
        }
    }

    private void inflateShopDetails(AvailableShopResponse shop) {

        if (cashbackText != null && !cashbackText.equals("")) {
            binding.tvCashback.setVisibility(View.VISIBLE);
            binding.tvCashback.setText(cashbackText.replaceAll(".00", ""));
        } else
            binding.tvCashback.setVisibility(View.GONE);

        binding.selectedShopHolder.setVisibility(View.VISIBLE);
        binding.shopName.setText(shop.getShopName());

        Glide.with(this)
                .asBitmap()
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_evaly_placeholder))
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

        if (shop.getInStock() < 1)
            binding.stock.setText(R.string.stock_color_contact_seller);
        else
            binding.stock.setText(R.string.stock_colon_available);

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

        if (shop.getPrice() == 0)
            binding.buyNowHolder.setVisibility(View.GONE);
        else
            binding.buyNowHolder.setVisibility(View.VISIBLE);

        binding.selectedShopClickHolder.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", shop.getShopSlug());
            intent.putExtra("shop_name", shop.getShopName());
            intent.putExtra("category", shop.getShopSlug());
            startActivity(intent);
        });

        binding.addCart.setOnClickListener(v -> {
            updateVariantDetails();
            Calendar calendar = Calendar.getInstance();
            String price = Utils.formatPrice(shop.getPrice());

            String sellerJson = new Gson().toJson(shop);

            CartEntity cartEntity = cartItem;
            cartEntity.setPriceRound(price);
            cartEntity.setDiscountedPrice(shop.getDiscountedPrice());
            cartEntity.setTime(calendar.getTimeInMillis());
            cartEntity.setExpressShop(shop.isExpressShop());
            cartEntity.setQuantity(1);
            cartEntity.setShopSlug(shop.getShopSlug());
            cartEntity.setShopName(shop.getShopName());
            cartEntity.setSlug(slug);
            cartEntity.setProductID(String.valueOf(shop.getShopItemId()));

            cartViewModel.insert(cartEntity);

            Snackbar snackBar = Snackbar.make(getWindow().getDecorView(), "Added to cart", 1500);
            snackBar.setAction("Go to Cart", view -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                snackBar.dismiss();
            });
            snackBar.show();
        });

        binding.buyNow.setOnClickListener(view -> {
            updateVariantDetails();
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

    private void inflateAvailableShops(CommonDataResponse<List<AvailableShopResponse>> response, boolean isNearestShops) {

        toRemoveModel = null;

        binding.selectedShopHolder.setVisibility(View.GONE);
        binding.availableShopsHolder.setVisibility(View.VISIBLE);

        List<AvailableShopResponse> list = new ArrayList<>(response.getData());

        if (shopSlug != null || list.size() == 1) {
            for (AvailableShopResponse model : list) {
                if (model.getShopSlug().equals(shopSlug) || list.size() == 1) {
                    toRemoveModel = model;
                    break;
                }
            }
            if (toRemoveModel != null) {
                list.remove(toRemoveModel);
                inflateShopDetails(toRemoveModel);
            } else {
                binding.selectedShopHolder.setVisibility(View.GONE);
                binding.availableShopsHolder.setVisibility(View.VISIBLE);
                binding.avlshop.setText(R.string.available_at_shop);
            }
        }

        AvailableShopAdapter adapter = new AvailableShopAdapter(this, binding.rootView, list, cartItem);
        adapter.setClickListener(this);
        binding.availableShops.setAdapter(adapter);
        binding.progressBarShop.setVisibility(View.GONE);

        if ((shopSlug != null || response.getData().size() == 1) && toRemoveModel != null) {
            if (list.size() == 0)
                binding.availableShopsHolder.setVisibility(View.GONE);
            if (toRemoveModel.getDiscountedPrice() == 0 && toRemoveModel.getPrice() == 0) {
                binding.selectedShopHolder.setVisibility(View.GONE);
                binding.availableShopsHolder.setVisibility(View.VISIBLE);
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
                    ToastUtils.show(getResources().getString(R.string.something_wrong));
                } else {
                    dialog.hideDialog();
                    newsfeedShareDialog.cancel();
                    ToastUtils.show("Your post has successfully posted. It may take few hours to get approved.");
                }
            });
        viewModel.createPost(createPostModel);
    }


    public void hideProductHolder() {
        binding.productInfo.setVisibility(View.GONE);
        binding.relatedTitle.setVisibility(View.GONE);
        binding.progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.fff));
        binding.stickyButtons.setVisibility(View.GONE);
    }

    public void showProductHolder() {
        binding.productInfo.setVisibility(View.VISIBLE);
        binding.relatedTitle.setVisibility(View.VISIBLE);
        binding.stickyButtons.setVisibility(View.VISIBLE);
        binding.progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.fafafa));
    }

    private void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    @Override
    public void findVariantByController() {
        List<Integer> selectedAttr = variantsController.getSelectedVariants();
        for (ProductVariantsItem variantModel : productVariantsItemList) {
            if (variantModel.getAttributeValues().containsAll(selectedAttr) && selectedAttr.containsAll(variantModel.getAttributeValues())) {
                populateProductByVariant(variantModel);
                break;
            }
        }
    }

    @Override
    public void onAddToCartClick(AvailableShopResponse shop) {
        updateVariantDetails();
        String price = Utils.formatPrice(shop.getPrice());
        CartEntity cartEntity = new CartEntity();
        cartEntity.setName(cartItem.getName());
        cartEntity.setImage(cartItem.getImage());
        cartEntity.setPriceRound(price);
        cartEntity.setDiscountedPrice(shop.getDiscountedPrice());
        cartEntity.setExpressShop(shop.isExpressShop());
        cartEntity.setQuantity(1);
        cartEntity.setShopSlug(shop.getShopSlug());
        cartEntity.setShopName(shop.getShopName());
        cartEntity.setSlug(cartItem.getSlug());
        cartEntity.setVariantDetails(cartItem.getVariantDetails());
        cartEntity.setProductID(String.valueOf(shop.getShopItemId()));
        cartEntity.setTime(Calendar.getInstance().getTimeInMillis());

        cartViewModel.insert(cartEntity);

        Snackbar snackBar = Snackbar.make(getWindow().getDecorView(), "Added to cart", 1500);
        snackBar.setAction("Go to Cart", v1 -> {
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
            snackBar.dismiss();
        });
        snackBar.show();
    }

    @Override
    public void onProductClick(ProductItem model) {
        Intent intent = new Intent(this, ViewProductActivity.class);
        intent.putExtra("product_slug", model.getSlug());
        intent.putExtra("product_name", model.getName());
        intent.putExtra("product_price", model.getMaxPrice());
        if (model.getImageUrls().size() > 0)
            intent.putExtra("product_image", model.getImageUrls().get(0));
        startActivity(intent);
    }

}
