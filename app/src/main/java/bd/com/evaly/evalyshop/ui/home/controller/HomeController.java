package bd.com.evaly.evalyshop.ui.home.controller;


import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.EpoxyModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.recommender.database.table.RsEntity;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignBannerModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignBannerSkeletonModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSmallProductModel_;
import bd.com.evaly.evalyshop.ui.epoxy.EpoxyDividerModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.home.HomeViewModel;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeRsCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeRsGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class HomeController extends EpoxyController {

    @AutoModel
    HomeSliderModel_ sliderModel;
    @AutoModel
    HomeWidgetModel_ widgetModel;
    @AutoModel
    HomeExpressModel_ expressModel;
    @AutoModel
    HomeTabsModel_ tabsModel;
    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    HomeExpressCarouselModel_ expressCarouselModel_;
    @AutoModel
    EpoxyDividerModel_ dividerModel_;
    @AutoModel
    HomeExpressHeaderModel_ expressHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ campaignHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ productHeaderModel_;
    @AutoModel
    HomeExpressSkeletonModel_ expressSkeletonBindingModel_;
    @AutoModel
    HomeRsCarouselModel_ campaignCategoryCarousel;
    @AutoModel
    HomeRsCarouselModel_ flashSaleCarousel;
    @AutoModel
    HomeRsCarouselModel_ categoryCarousel;
    @AutoModel
    HomeRsCarouselModel_ shopCarousel;
    @AutoModel
    HomeRsCarouselModel_ brandCarousel;
    @AutoModel
    HomeExpressHeaderModel_ flashSaleHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ categoryHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ shopsHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ brandsHeaderModel_;

    private AppCompatActivity activity;
    private Fragment fragment;
    private HomeViewModel homeViewModel;
    private List<ProductItem> items = new ArrayList<>();
    private List<CampaignProductResponse> flashSaleProducts = new ArrayList<>();
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private List<CampaignCategoryResponse> campaignCategoryList = new ArrayList<>();
    private List<RsEntity> rsBrandList = new ArrayList<>();
    private List<RsEntity> rsCategoryList = new ArrayList<>();
    private List<RsEntity> rsShopList = new ArrayList<>();
    private AppDatabase appDatabase;
    private boolean loadingMore = true;
    private boolean isExpressLoading = true;
    private boolean isCampaignLoading = true;


    @Override
    protected void buildModels() {

        // slider model
        sliderModel
                .activity(activity)
                .fragment(fragment)
                .appDatabase(appDatabase)
                .addTo(this);

        // home widget buttons
        widgetModel
                .fragment(fragment)
                .activity(activity)
                .addTo(this);


//        initCampaignCarousel();
//
//        initFlashSaleCarousel();

      //  initExpressCarousel();

        initCategoryCarousel();

        initShopCarousel();

        initBrandsCarousel();


        initProductGrid();

        // bottom loading bar
        loader.addIf(loadingMore, this);
    }

    private void initProductGrid() {
        productHeaderModel_
                .title("Products")
                .showMore(false)
                .transparentBackground(true)
                .addTo(this);

        new EmptySpaceModel_()
                .id("empty_space_10")
                .height(15)
                .addTo(this);


        // product listing
        for (ProductItem productItem : items) {
            new HomeProductGridModel_()
                    .id(productItem.getSlug())
                    .model(productItem)
                    .clickListener((model, parentView, clickedView, position) -> {
                        ProductItem item = model.getModel();
                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item.getSlug());
                        intent.putExtra("product_name", item.getName());
                        intent.putExtra("product_price", item.getMaxPrice());
                        if (item.getImageUrls().size() > 0)
                            intent.putExtra("product_image", item.getImageUrls().get(0));
                        activity.startActivity(intent);
                    })
                    .addTo(this);
        }
    }

    private void initCategoryCarousel() {

        categoryHeaderModel_
                .activity(activity)
                .showMore(true)
                .title("Categories for you")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    NavHostFragment.findNavController(fragment).navigate(R.id.categoryFragment);
                })
                .addIf(rsCategoryList.size() > 0, this);

        List<DataBindingEpoxyModel> models = new ArrayList<>();

        for (RsEntity item : rsCategoryList) {
            models.add(new HomeRsGridModel_()
                    .id("rs_Category", item.getSlug())
                    .title(item.getName())
                    .image(item.getImageUrl())
                    .slug(item.getSlug())
                    .type(item.getType())
                    .color("#175A82D3")
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("slug", model.slug());
                        bundle.putString("category", model.slug());
                        NavHostFragment.findNavController(fragment).navigate(R.id.browseProductFragment, bundle);
                    }));
        }

        categoryCarousel
                .models(models)
                .initialPrefetchItemCount(5)
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                })
                .spanSizeOverride(new EpoxyModel.SpanSizeOverrideCallback() {
                    @Override
                    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
                        return 2;
                    }
                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(15, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity)))
                .addIf(rsCategoryList.size() > 0, this);
    }


    private void initBrandsCarousel() {

        brandsHeaderModel_
                .activity(activity)
                .showMore(true)
                .title("Brands for you")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    Intent intent = new Intent(activity, GlobalSearchActivity.class);
                    intent.putExtra("type", 2);
                    activity.startActivity(intent);
                })
                .addIf(rsBrandList.size() > 0, this);

        List<DataBindingEpoxyModel> models = new ArrayList<>();

        for (RsEntity item : rsBrandList) {
            models.add(new HomeRsGridModel_()
                    .id("rs_brand", item.getSlug())
                    .title(item.getName())
                    .image(item.getImageUrl())
                    .slug(item.getSlug())
                    .type(item.getType())
                    .color("#12A11818")
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("brand_slug", model.slug());
                        bundle.putString("brand_name", model.title());
                        bundle.putString("image_url", model.image());
                        NavHostFragment.findNavController(fragment).navigate(R.id.brandFragment, bundle);
                    }));
        }

        brandCarousel
                .models(models)
                .initialPrefetchItemCount(5)
//                .onBind((model, view, position) -> {
//                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT
//                    );
//                    params.setFullSpan(true);
//                    view.setLayoutParams(params);
//                    view.setMinimumHeight(1000);
//                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(15, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity)))
                .addIf(rsBrandList.size() > 0, this);
    }


    private void initShopCarousel() {

        shopsHeaderModel_
                .activity(activity)
                .showMore(true)
                .title("Shops for you")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    Intent intent = new Intent(activity, GlobalSearchActivity.class);
                    intent.putExtra("type", 3);
                    activity.startActivity(intent);
                })
                .addIf(rsShopList.size() > 0, this);

        List<DataBindingEpoxyModel> models = new ArrayList<>();

        for (RsEntity item : rsShopList) {
            models.add(new HomeRsGridModel_()
                    .id("rs_brand", item.getSlug())
                    .title(item.getName())
                    .image(item.getImageUrl())
                    .slug(item.getSlug())
                    .type(item.getType())
                    .color("#1BECBC26")
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_slug", model.slug());
                        bundle.putString("shop_name", model.title());
                        NavHostFragment.findNavController(fragment).navigate(R.id.shopFragment, bundle);
                    }));
        }

        shopCarousel
                .models(models)
                .initialPrefetchItemCount(5)
//                .onBind((model, view, position) -> {
//                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT
//                    );
//                    params.setFullSpan(true);
//                    view.setLayoutParams(params);
//                    view.setMinimumHeight(1000);
//                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(15, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity)))
                .addIf(rsShopList.size() > 0, this);
    }

    private void initCampaignCarousel() {
        //campaign carousel

        campaignHeaderModel_
                .activity(activity)
                .showMore(true)
                .title("Ongoing Campaigns")
                .transparentBackground(false)
                .clickListener((model, parentView, clickedView, position) -> NavHostFragment.findNavController(fragment).navigate(R.id.campaignFragment))
                .addTo(this);

        List<DataBindingEpoxyModel> campaignSkeletonItemModels = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            campaignSkeletonItemModels.add(new CampaignBannerSkeletonModel_().id("cam_ske", i));

        List<DataBindingEpoxyModel> campaignModels = new ArrayList<>();
        for (CampaignCategoryResponse item : campaignCategoryList) {
            campaignModels.add(new CampaignBannerModel_()
                    .id("camp", item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model.model());
                        NavHostFragment.findNavController(fragment).navigate(R.id.campaignDetails, bundle);
                    }));
        }

        campaignCategoryCarousel
                .models(isCampaignLoading ? campaignSkeletonItemModels : campaignModels)
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                    view.setBackground(ContextCompat.getDrawable(activity, R.drawable.white_to_grey_gradient));
                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(5, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(15, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        0))
                .addTo(this);

    }

    private void initExpressCarousel() {
        //express services carousel
        expressHeaderModel_
                .activity(activity)
                .showMore(true)
                .title("Evaly Express")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> NavHostFragment.findNavController(fragment).navigate(R.id.expressProductSearchFragment))
                .addTo(this);

        List<DataBindingEpoxyModel> expressItemModels = new ArrayList<>();
        int count = 0;
        for (ExpressServiceModel model : itemsExpress) {
            if (count > 7)
                break;
            expressItemModels.add(new HomeExpressServiceModel_()
                    .clickListener((model1, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model1.getModel());
                        NavHostFragment.findNavController(fragment).navigate(R.id.evalyExpressFragment, bundle);
                    })
                    .id(model.getSlug())
                    .model(model));
            count++;
        }

        List<HomeExpressServiceSkeletonModel_> expressDummyItemModels = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expressDummyItemModels.add(new HomeExpressServiceSkeletonModel_()
                    .id("express_dummy" + i));
        }

        expressCarouselModel_
                .models(isExpressLoading ? expressDummyItemModels : expressItemModels)
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                    // view.setBackgroundColor(Color.parseColor("#ffffff"));
                    // view.setBackground(AppController.getmContext().getDrawable(R.drawable.white_to_grey_gradient));
                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(0, activity),
                        0))
                .addTo(this);
    }

    private void initFlashSaleCarousel() {
        //flash sale carousel

        flashSaleHeaderModel_
                .activity(activity)
                .showMore(true)
                .title("Flash Sale")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    for (CampaignCategoryResponse s : campaignCategoryList) {
                        if (s.getSlug().equals(Constants.FLASH_SALE_SLUG)) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("model", s);
                            NavHostFragment.findNavController(fragment).navigate(R.id.campaignDetails, bundle);
                            return;
                        }
                    }
                    ToastUtils.show("Please visit campaign page");
                })
                .addIf(flashSaleProducts.size() > 0, this);

        List<DataBindingEpoxyModel> flashSaleModels = new ArrayList<>();

        for (CampaignProductResponse item : flashSaleProducts) {
            flashSaleModels.add(new CampaignSmallProductModel_()
                    .id("flashsale", item.getSlug())
                    .clickListener((model, parentView, clickedView, position) -> {
                        CampaignProductResponse item1 = model.model();
                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item1.getSlug());
                        intent.putExtra("product_name", item1.getName());
                        intent.putExtra("product_price", item1.getPrice());
                        if (item.getShopSlug() != null)
                            intent.putExtra("shop_slug", item.getShopSlug());
                        intent.putExtra("product_image", item1.getImage());
                        intent.putExtra("cashback_text", item1.getCashbackText());
                        activity.startActivity(intent);
                    })
                    .model(item));
        }

        flashSaleCarousel
                .models(flashSaleModels)
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(15, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(10, activity)))
                .addIf(flashSaleModels.size() > 0, this);
    }

    public void setRsBrandList(List<RsEntity> rsBrandList) {
        this.rsBrandList = rsBrandList;
    }

    public void setRsCategoryList(List<RsEntity> rsCategoryList) {
        this.rsCategoryList = rsCategoryList;
    }

    public void setRsShopList(List<RsEntity> rsShopList) {
        this.rsShopList = rsShopList;
    }

    public void setFlashSaleProducts(List<CampaignProductResponse> flashSaleProducts) {
        this.flashSaleProducts = flashSaleProducts;
    }

    public void setCampaignLoading(boolean campaignLoading) {
        isCampaignLoading = campaignLoading;
    }

    public void setCampaignCategoryList(List<CampaignCategoryResponse> campaignCategoryList) {
        this.campaignCategoryList = campaignCategoryList;
    }

    public void addData(List<ProductItem> productItems) {
        this.items = productItems;
    }

    public void addExpressData(List<ExpressServiceModel> items) {
        this.itemsExpress.clear();
        this.itemsExpress.addAll(items);
        requestModelBuild();
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setExpressLoading(boolean expressLoading) {
        isExpressLoading = expressLoading;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        appDatabase = AppDatabase.getInstance(activity);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public HomeViewModel getHomeViewModel() {
        return homeViewModel;
    }

    public void setHomeViewModel(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }
}

