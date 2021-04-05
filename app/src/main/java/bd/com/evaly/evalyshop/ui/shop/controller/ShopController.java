package bd.com.evaly.evalyshop.ui.shop.controller;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.EpoxyController;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ShopModelHeaderBinding;
import bd.com.evaly.evalyshop.databinding.ShopModelTitleCategoryBinding;
import bd.com.evaly.evalyshop.databinding.ShopModelTitleProductBinding;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.basic.TextBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.epoxy.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxy.NoProductModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.ui.shop.models.BindShopHeaderModel;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryCarouselModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryItemModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryTitleModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopHeaderModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopProductTitleModel_;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class ShopController extends EpoxyController {

    @AutoModel
    ShopHeaderModel_ headerModel;
    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    NoProductModel_ noProductModel;
    @AutoModel
    ShopCategoryCarouselModel_ categoryCarouselModel;
    @AutoModel
    ShopCategoryTitleModel_ categoryTitleModel;
    @AutoModel
    ShopProductTitleModel_ productTitleModel;

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private List<TabsItem> categoryItems = new ArrayList<>();
    private String campaignSlug;

    private ShopDetailsResponse shopInfo;
    private int cashbackRate = 0;
    private ShopViewModel viewModel;
    private boolean loadingMore = true;
    private boolean emptyPage = false;
    private boolean categoriesLoading = false;
    private String categoryTitle = null;
    private String description = null;
    private boolean isSubscribed = false;
    private ReviewSummaryModel reviewSummaryModel;
    private int subscriberCount = 0;


    public void setReviewSummaryModel(ReviewSummaryModel reviewSummaryModel) {
        this.reviewSummaryModel = reviewSummaryModel;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ShopController() {
        setDebugLoggingEnabled(true);
    }

    public boolean isCategoriesLoading() {
        return categoriesLoading;
    }

    public void setCategoriesLoading(boolean categoriesLoading) {
        this.categoriesLoading = categoriesLoading;
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void showEmptyPage(boolean emptyPage, boolean build) {
        this.emptyPage = emptyPage;
        if (build)
            requestModelBuild();
    }

    public void setAttr(ShopDetailsResponse info) {
        this.shopInfo = info;
    }

    @Override
    protected void buildModels() {

        headerModel
                .shopInfo(shopInfo)
                .description(description)
                .subCount(subscriberCount)
                .isSubscribed(isSubscribed)
                .ratingSummary(reviewSummaryModel)
                .onBind((model, view, position) -> {
                    BindShopHeaderModel.bind((ShopModelHeaderBinding) view.getDataBinding(),
                            model.shopInfo(),
                            model.description(),
                            model.isSubscribed(),
                            model.subCount(),
                            model.ratingSummary());
                })
                .btn1OnClick((model, parentView, clickedView, position) -> {
                    String phone = shopInfo.getContactNumber();
                    if (fragment.getView() == null)
                        return;
                    final Snackbar snackBar = Snackbar.make(fragment.getView(), phone + "\n", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Call", v12 -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + shopInfo.getContactNumber()));
                        activity.startActivity(intent);
                        snackBar.dismiss();
                    });
                    snackBar.show();
                })
                .btn2OnClick((model, parentView, clickedView, position) -> {
                    String address = shopInfo.getShopAddress();
                    if (fragment.getView() == null)
                        return;
                    final Snackbar snackBar = Snackbar.make(fragment.getView(), address + "\n", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Copy", v1 -> {
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("address", shopInfo.getShopAddress());
                        if (clipboard != null)
                            clipboard.setPrimaryClip(clip);

                        snackBar.dismiss();
                    });
                    snackBar.show();
                })
                .btn3OnClick((model, parentView, clickedView, position) -> {
                    if (description == null) {
                        ToastUtils.show("Delivery information is not provided");
                    } else {
                        description = "Not available now";
                        TextBottomSheetFragment textBottomSheetFragment = TextBottomSheetFragment.newInstance("Terms & Conditions", description);
                        textBottomSheetFragment.show(fragment.getParentFragmentManager(), "tc");
                    }
                })
                .btn4OnClick((model, parentView, clickedView, position) -> {
                    Intent intent = new Intent(activity, ReviewsActivity.class);
                    intent.putExtra("ratingJson", new Gson().toJson(model.ratingSummary()));
                    intent.putExtra("type", "shop");
                    intent.putExtra("item_value", shopInfo.getSlug());
                    activity.startActivity(intent);
                })
                .followOnClick((model, parentView, clickedView, position) -> {
                    if (AppController.getInstance().getPreferenceRepository().getToken().equals("")) {
                        ToastUtils.show("You need to login first to follow a shop");
                        return;
                    }
                    ShopModelHeaderBinding binding = (ShopModelHeaderBinding) parentView.getDataBinding();
                    boolean subscribe = true;

                    if (binding.followText.getText().toString().contains("Unfollow")) {
                        subscribe = false;
                        binding.followText.setText(String.format("Follow (%d)", --subscriberCount));
                    } else
                        binding.followText.setText(String.format("Unfollow (%d)", ++subscriberCount));

                    viewModel.subscribe(subscribe);
                })
                .messageOnClick((model, parentView, clickedView, position) -> {
                    viewModel.setOnChatClickLiveData(true);
                })
                .addTo(this);

        initCategory();
        initProducts();

        noProductModel
                .text("No Products Available")
                .image(R.drawable.ic_empty_product)
                .addIf(items.size() == 0 && !loadingMore, this);

        loader
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.itemView.getLayoutParams();
                    params.setFullSpan(true);
                    if (categoryTitle != null && items.size() == 0) {
                        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        params.topMargin = 100;
                    } else {
                        params.topMargin = 0;
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    }
                })
                .addIf(loadingMore, this);

    }

    private void initCategory() {
        categoryTitleModel
                .clickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("shop_name", shopInfo.getShopName());
                    bundle.putString("shop_slug", shopInfo.getSlug());
                    bundle.putString("campaign_slug", campaignSlug);
                    NavHostFragment.findNavController(fragment).navigate(R.id.shopQuickViewFragment, bundle);
                })
                .onBind((model, view, position) -> {
                    if (categoriesLoading && categoryItems.size() == 1) {
                        ShopModelTitleCategoryBinding binding = (ShopModelTitleCategoryBinding) view.getDataBinding();
                        binding.quickView.setVisibility(View.GONE);
                    }
                })
                .addIf(!categoriesLoading && categoryItems.size() > 0, this);

        List<ShopCategoryItemModel_> categoryModelList = new ArrayList<>();
        for (int i = 0; i < categoryItems.size(); i++) {
            categoryModelList.add(new ShopCategoryItemModel_()
                    .id("category_" + categoryItems.get(i))
                    .model(categoryItems.get(i))
                    .clickListener((model, parentView, clickedView, position) -> {
                        viewModel.setSelectedCategoryLiveData(model.getModel());
                    })
                    .onBind((model, view, position) -> {
                        if (position >= categoryItems.size() - 4)
                            viewModel.loadShopCategories();
                    })
            );
        }

        categoryCarouselModel
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                })
                .padding(Carousel.Padding.dp(10, 5, 50, 0, 0))
                .models(categoryModelList)
                .addTo(this);

    }

    private void initProducts() {
        productTitleModel
                .title(categoryTitle)
                .clickListener((model, parentView, clickedView, position) -> viewModel.setOnResetLiveData(true))
                .onBind((model, view, position) -> {
                    ShopModelTitleProductBinding binding = (ShopModelTitleProductBinding) view.getDataBinding();
                    if (categoryTitle == null) {
                        binding.categoryTitle.setText(R.string.all_products);
                        binding.resetBtn.setVisibility(View.GONE);
                    } else {
                        binding.categoryTitle.setText(categoryTitle);
                        binding.resetBtn.setVisibility(View.VISIBLE);
                    }
                })
                .addTo(this);

        for (ProductItem productItem : items) {

            new HomeProductGridModel_()
                    .id(productItem.getUniqueId())
                    .model(productItem)
                    .cashbackRate(cashbackRate)
                    .isShop(true)
                    .buyNowClickListener((model, parentView, clickedView, position) -> viewModel.setBuyNowLiveData(model.getModel().getSlug()))
                    .clickListener((model, parentView, clickedView, position) -> {
                        ProductItem item = model.getModel();

                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item.getSlug());
                        intent.putExtra("product_name", item.getName());
                        intent.putExtra("product_price", (int) Double.parseDouble(item.getMaxPrice()));
                        if (model.cashbackRate() > 0)
                            intent.putExtra("cashback_text", model.cashbackRate() + "% Cashback");
                        if (model.isShop())
                            intent.putExtra("shop_slug", shopInfo.getSlug());

                        if (item.getImageUrls().size() > 0)
                            intent.putExtra("product_image", item.getImageUrls().get(0));

                        activity.startActivity(intent);
                    })
                    .addTo(this);
        }
    }

    public void addData(List<ProductItem> productItems) {
        this.items = productItems;
        requestModelBuild();
    }

    public void addCategoryData(List<TabsItem> categoryItems, boolean build) {
        this.categoryItems = categoryItems;
        if (build)
            requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void clear() {
        items.clear();
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getCashbackRate() {
        return cashbackRate;
    }

    public void setCashbackRate(int cashbackRate) {
        this.cashbackRate = cashbackRate;
    }

    public void setViewModel(ShopViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public ShopDetailsResponse getShopInfo() {
        return shopInfo;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
        requestModelBuild();
    }

    public String getCampaignSlug() {
        return campaignSlug;
    }

    public void setCampaignSlug(String campaignSlug) {
        this.campaignSlug = campaignSlug;
    }
}

