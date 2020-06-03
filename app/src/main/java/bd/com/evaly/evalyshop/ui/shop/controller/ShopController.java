package bd.com.evaly.evalyshop.ui.shop.controller;


import android.content.Intent;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryCarouselModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryItemModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryTitleModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopHeaderModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopProductTitleModel_;
import bd.com.evaly.evalyshop.util.Utils;

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

    private ShopDetailsModel shopInfo;
    private int cashbackRate = 0;
    private ShopViewModel viewModel;
    private boolean loadingMore = true;
    private boolean emptyPage = false;

    public ShopController() {
        setDebugLoggingEnabled(true);
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

    public void setAttr(ShopDetailsModel info) {
        this.shopInfo = info;
    }

    @Override
    protected void buildModels() {
        headerModel
                .activity(activity)
                .fragment(fragment)
                .shopInfo(shopInfo)
                .viewModel(viewModel)
                .addTo(this);

        categoryTitleModel.addTo(this);

        List<ShopCategoryItemModel_> categoryModelList = new ArrayList<>();
        for (int i = 0; i < categoryItems.size(); i++) {
            categoryModelList.add(new ShopCategoryItemModel_()
                    .id("category_" + categoryItems.get(i))
                    .model(categoryItems.get(i))
                    .onBind((model, view, position) -> {
                        if (position >= categoryItems.size() - 4)
                            viewModel.loadShopCategories();
                    })
            );
        }

        categoryCarouselModel
                .onBind((model, view, position) -> {
                    if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                        params.setFullSpan(true);
                    } else {
                        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setFullSpan(true);
                        view.setLayoutParams(params);
                    }
                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(5, activity),
                        (int) Utils.convertDpToPixel(5, activity), 50,
                        (int) Utils.convertDpToPixel(20, activity),
                        0))
                .models(categoryModelList)
                .addTo(this);

        productTitleModel.addTo(this);

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

                        if (model.isShop())
                            intent.putExtra("shop_slug", shopInfo.getData().getShop().getSlug());

                        if (item.getImageUrls().size() > 0)
                            intent.putExtra("product_image", item.getImageUrls().get(0));

                        activity.startActivity(intent);
                    })
                    .addTo(this);
        }

        noProductModel
                .text("No Products Available")
                .image(R.drawable.ic_empty_product)
                .addIf(emptyPage, this);

        loader.addIf(loadingMore, this);

    }

    public void addData(List<ProductItem> productItems) {
        this.items.addAll(productItems);
        requestModelBuild();
    }

    public void addCategoryData(List<TabsItem> categoryItems) {
        this.categoryItems.addAll(categoryItems);
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

    public ShopDetailsModel getShopInfo() {
        return shopInfo;
    }
}

