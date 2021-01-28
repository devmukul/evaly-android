package bd.com.evaly.evalyshop.ui.home.controller;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
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
import bd.com.evaly.evalyshop.ui.home.model.HomeDefaultCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeRsCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeRsGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneBannerModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneBottomBarModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneBrandModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneProductModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneSectionTitleModel_;
import bd.com.evaly.evalyshop.ui.home.model.cyclone.CycloneShopModel_;
import bd.com.evaly.evalyshop.util.Constants;

public class HomeController extends EpoxyController {

    @AutoModel
    HomeSliderModel_ sliderModel;
    @AutoModel
    HomeWidgetModel_ widgetModel;
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
    HomeDefaultCarouselModel_ campaignCategoryCarousel;

    @AutoModel
    CycloneBannerModel_ cycloneBannerModel;
    @AutoModel
    CycloneSectionTitleModel_ flashSaleProductTitle;
    @AutoModel
    CycloneSectionTitleModel_ flashSaleBrandTitle;
    @AutoModel
    CycloneSectionTitleModel_ flashSaleShopTitle;

    @AutoModel
    CycloneCarouselModel_ flashSaleProductsCarousel;
    @AutoModel
    CycloneCarouselModel_ flashSaleBrandsCarousel;
    @AutoModel
    CycloneCarouselModel_ flashSaleShopCarousel;
    @AutoModel
    CycloneBottomBarModel_ cycloneBottomBarModel;

    @AutoModel
    HomeRsCarouselModel_ flashSaleCarousel;
    @AutoModel
    HomeExpressHeaderModel_ flashSaleHeaderModel_;

    @AutoModel
    HomeRsCarouselModel_ categoryCarousel;
    @AutoModel
    HomeRsCarouselModel_ shopCarousel;
    @AutoModel
    HomeRsCarouselModel_ brandCarousel;
    @AutoModel
    HomeExpressHeaderModel_ categoryHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ shopsHeaderModel_;
    @AutoModel
    HomeExpressHeaderModel_ brandsHeaderModel_;
    @AutoModel
    EmptySpaceModel_ emptySpaceModel_;

    private AppCompatActivity activity;
    private Fragment fragment;
    private HomeViewModel homeViewModel;
    private List<ProductItem> items = new ArrayList<>();
    private List<CampaignProductResponse> flashSaleProducts = new ArrayList<>();
    private List<CampaignBrandResponse> flashSaleBrands = new ArrayList<>();
    private List<CampaignShopResponse> flashSaleShops = new ArrayList<>();
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private List<BannerItem> bannerList = new ArrayList<>();
    private List<CampaignCategoryResponse> campaignCategoryList = new ArrayList<>();
    private List<RsEntity> rsBrandList = new ArrayList<>();
    private List<RsEntity> rsCategoryList = new ArrayList<>();
    private List<RsEntity> rsShopList = new ArrayList<>();
    private boolean loadingMore = true;
    private boolean isExpressLoading = true;
    private boolean isCampaignLoading = true;
    private boolean isCycloneOngoing = false;
    private String cycloneBanner = "https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/images/cyclone1.gif";
    private SliderController sliderController;
    private ClickListener clickListener;

    public HomeController() {
        setFilterDuplicates(true);
        if (sliderController == null) {
            sliderController = new SliderController();
            sliderController.setActivity(activity);
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void buildModels() {

        // slider model
        sliderModel
                .controller(sliderController)
                .activity(activity)
                .list(bannerList)
                .onBind((model, view, position) -> {
                    sliderController.setData(bannerList);
                })
                .addTo(this);

        // home widget buttons
        widgetModel
                .fragment(fragment)
                .activity(activity)
                .addTo(this);

        initCampaignCarousel();

        initFlashSaleCarousel();

        initExpressCarousel();

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

        emptySpaceModel_
                .height(15)
                .addTo(this);

        // product listing
        for (ProductItem productItem : items) {
            new HomeProductGridModel_()
                    .id("product_listing", productItem.getSlug())
                    .model(productItem)
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onProductClick(model.getModel());
                    })
                    .addTo(this);
        }
    }

    private void initCategoryCarousel() {

        categoryHeaderModel_
                .showMore(true)
                .title("Categories for you")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    clickListener.onShowMoreCategoryClick();
                })
                .addIf(rsCategoryList.size() > 0, this);

        List<DataBindingEpoxyModel> models = new ArrayList<>();

        for (RsEntity item : rsCategoryList) {
            models.add(new HomeRsGridModel_()
                    .id("rs_Category", item.getSlug() + item.getType())
                    .title(item.getName())
                    .image(item.getImageUrl())
                    .slug(item.getSlug())
                    .type(item.getType())
                    .color("#175A82D3")
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onCategoryClick(model.slug(), model.slug());
                    }));
        }

        categoryCarousel
                .models(models)
                .padding(Carousel.Padding.dp(12, 12, 10, 10, 10))
                .addIf(rsCategoryList.size() > 0, this);
    }

    private void initBrandsCarousel() {

        brandsHeaderModel_
                .showMore(true)
                .title("Brands for you")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    clickListener.onShowMoreBrandClick();
                })
                .addIf(rsBrandList.size() > 0, this);

        List<DataBindingEpoxyModel> models = new ArrayList<>();

        for (RsEntity item : rsBrandList) {
            models.add(new HomeRsGridModel_()
                    .id("rs_brand", item.getSlug() + item.getType())
                    .title(item.getName())
                    .image(item.getImageUrl())
                    .slug(item.getSlug())
                    .type(item.getType())
                    .color("#12A11818")
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onBrandClick(model.slug(), model.title(), model.image());
                    }));
        }

        brandCarousel
                .models(models)
                .padding(Carousel.Padding.dp(12, 12, 10, 10, 10))
                .addIf(rsBrandList.size() > 0, this);
    }

    private void initShopCarousel() {

        shopsHeaderModel_
                .showMore(true)
                .title("Shops for you")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    clickListener.onShowMoreShopClick();
                })
                .addIf(rsShopList.size() > 0, this);

        List<DataBindingEpoxyModel> models = new ArrayList<>();

        for (RsEntity item : rsShopList) {
            models.add(new HomeRsGridModel_()
                    .id("rs_brand", item.getSlug() + item.getType())
                    .title(item.getName())
                    .image(item.getImageUrl())
                    .slug(item.getSlug())
                    .type(item.getType())
                    .color("#1BECBC26")
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onShopClick(model.slug(), model.title());
                    }));
        }

        shopCarousel
                .models(models)
                .padding(Carousel.Padding.dp(12, 12, 10, 10, 10))
                .addIf(rsShopList.size() > 0, this);
    }

    private void initCampaignCarousel() {
        //campaign carousel

        campaignHeaderModel_
                .showMore(true)
                .title("Ongoing Campaigns")
                .transparentBackground(false)
                .clickListener((model, parentView, clickedView, position) -> {
                    clickListener.onShowMoreCampaignClick();
                })
                .addTo(this);

        List<DataBindingEpoxyModel> campaignSkeletonItemModels = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            campaignSkeletonItemModels.add(new CampaignBannerSkeletonModel_().id("cam_cat_skel", i));

        List<DataBindingEpoxyModel> campaignModels = new ArrayList<>();
        for (CampaignCategoryResponse item : campaignCategoryList) {
            campaignModels.add(new CampaignBannerModel_()
                    .id("campaign_categories_banner", item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onCampaignCategoryClick(model.model());
                    }));
        }

        campaignCategoryCarousel
                .models(isCampaignLoading ? campaignSkeletonItemModels : campaignModels)
                .onBind((model, view, position) -> {
                    view.setBackground(ContextCompat.getDrawable(activity, R.drawable.white_to_grey_gradient));
                })
                .padding(Carousel.Padding.dp(2, 12, 12, 10, 0))
                .addTo(this);

    }

    private void initExpressCarousel() {
        //express services carousel
        expressHeaderModel_
                .showMore(true)
                .title("Evaly Express")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    clickListener.onShowMoreExpressClick();
                })
                .addTo(this);

        List<DataBindingEpoxyModel> expressItemModels = new ArrayList<>();
        int count = 0;
        for (ExpressServiceModel model : itemsExpress) {
            if (count > 7)
                break;
            expressItemModels.add(new HomeExpressServiceModel_()
                    .id("express_shop", model.getSlug())
                    .clickListener((model1, parentView, clickedView, position) -> {
                        clickListener.onExpressClick(model1.model());
                    })
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
                .padding(Carousel.Padding.dp(7, 12, 7, 0, 0))
                .addTo(this);
    }


    private void initFlashSaleCarousel() {

        //flash sale carousel
        flashSaleHeaderModel_
                .showMore(true)
                .title("Flash Sale")
                .transparentBackground(true)
                .clickListener((model, parentView, clickedView, position) -> {
                    for (CampaignCategoryResponse s : campaignCategoryList) {
                        if (s.getSlug().equals(Constants.FLASH_SALE_SLUG)) {
                            clickListener.onCampaignCategoryClick(s);
                            return;
                        }
                    }
                    clickListener.onFlashSaleClick(Constants.FLASH_SALE_SLUG);
                })
                .addIf(!isCycloneOngoing && flashSaleProducts.size() > 0, this);

        List<DataBindingEpoxyModel> flashSaleModels = new ArrayList<>();

        for (CampaignProductResponse item : flashSaleProducts) {
            flashSaleModels.add(new CampaignSmallProductModel_()
                    .id("flash_sale_item", item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onCampaignProductClick(model.model());
                    }));
        }

        flashSaleCarousel
                .models(flashSaleModels)
                .padding(Carousel.Padding.dp(15, 12, 10, 10, 10))
                .addIf(!isCycloneOngoing && flashSaleModels.size() > 0, this);

        initCycloneCarousel();
    }

    private void initCycloneCarousel() {

        cycloneBannerModel
                .image("https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/images/cyclone1.gif")
                .addIf(isCycloneOngoing && (flashSaleShops.size() > 0 || flashSaleProducts.size() > 0 || flashSaleBrands.size() > 0), this);

        //flash sale carousel
        flashSaleProductTitle
                .showMore(true)
                .title("Products")
                .clickListener((model, parentView, clickedView, position) -> {
                    for (CampaignCategoryResponse s : campaignCategoryList) {
                        if (s.getSlug().equals(Constants.FLASH_SALE_SLUG)) {
                            clickListener.onCampaignCategoryClick(s);
                            return;
                        }
                    }
                    clickListener.onFlashSaleClick(Constants.FLASH_SALE_SLUG);
                })
                .addIf(isCycloneOngoing && flashSaleProducts.size() > 0, this);

        List<DataBindingEpoxyModel> flashSaleProductModels = new ArrayList<>();

        for (CampaignProductResponse item : flashSaleProducts) {
            flashSaleProductModels.add(new CycloneProductModel_()
                    .id("flash_sale_product_item", item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        clickListener.onCampaignProductClick(model.model());
                    }));
        }

        flashSaleProductsCarousel
                .models(flashSaleProductModels)
                .padding(Carousel.Padding.dp(28, 12, 30, 10, 10))
                .addIf(isCycloneOngoing && flashSaleProductModels.size() > 0, this);

        // brands

        flashSaleBrandTitle
                .showMore(true)
                .title("Brands")
                .clickListener((model, parentView, clickedView, position) -> {
                    for (CampaignCategoryResponse s : campaignCategoryList) {
                        if (s.getSlug().equals(Constants.FLASH_SALE_SLUG)) {
                            clickListener.onCampaignCategoryClick(s);
                            return;
                        }
                    }
                    clickListener.onFlashSaleClick(Constants.FLASH_SALE_SLUG);
                })
                .addIf(isCycloneOngoing && flashSaleBrands.size() > 0, this);

        List<DataBindingEpoxyModel> flashSaleBrandsModels = new ArrayList<>();

        for (CampaignBrandResponse item : flashSaleBrands) {
            flashSaleBrandsModels.add(new CycloneBrandModel_()
                    .id("flash_sale_brand_item", item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        // clickListener.onCampaignProductClick(model.model());
                    }));
        }

        flashSaleBrandsCarousel
                .models(flashSaleBrandsModels)
                .padding(Carousel.Padding.dp(28, 12, 30, 10, 10))
                .addIf(isCycloneOngoing && flashSaleBrandsModels.size() > 0, this);

        // shops

        flashSaleShopTitle
                .showMore(true)
                .title("Shops")
                .clickListener((model, parentView, clickedView, position) -> {
                    for (CampaignCategoryResponse s : campaignCategoryList) {
                        if (s.getSlug().equals(Constants.FLASH_SALE_SLUG)) {
                            clickListener.onCampaignCategoryClick(s);
                            return;
                        }
                    }
                    clickListener.onFlashSaleClick(Constants.FLASH_SALE_SLUG);
                })
                .addIf(isCycloneOngoing && flashSaleShops.size() > 0, this);

        List<DataBindingEpoxyModel> flashSaleShopModels = new ArrayList<>();

        for (CampaignShopResponse item : flashSaleShops) {
            flashSaleShopModels.add(new CycloneShopModel_()
                    .id("flash_sale_shop_item", item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        // clickListener.onCampaignProductClick(model.model());
                    }));
        }

        flashSaleShopCarousel
                .models(flashSaleShopModels)
                .padding(Carousel.Padding.dp(28, 12, 30, 10, 10))
                .addIf(isCycloneOngoing && flashSaleShopModels.size() > 0, this);

        cycloneBottomBarModel
                .addIf(isCycloneOngoing && (flashSaleProductModels.size() > 0 || flashSaleBrandsModels.size() > 0 || flashSaleShopModels.size() > 0), this);
    }

    public void setCycloneBanner(String cycloneBanner) {
        if (cycloneBanner != null && !cycloneBanner.equals(""))
            this.cycloneBanner = cycloneBanner;
    }


    public void setCycloneOngoing(boolean cycloneOngoing) {
        isCycloneOngoing = cycloneOngoing;
    }

    public void setFlashSaleBrands(List<CampaignBrandResponse> flashSaleBrands) {
        this.flashSaleBrands = flashSaleBrands;
    }

    public void setFlashSaleShops(List<CampaignShopResponse> flashSaleShops) {
        this.flashSaleShops = flashSaleShops;
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

    public void setProductData(List<ProductItem> productItems) {
        this.items = productItems;
    }

    public void setExpressData(List<ExpressServiceModel> items) {
        this.itemsExpress = items;
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public void setExpressLoading(boolean expressLoading) {
        isExpressLoading = expressLoading;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
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

    public void setBannerList(List<BannerItem> bannerList) {
        this.bannerList = bannerList;
    }

    public interface ClickListener {
        void onProductClick(ProductItem item);

        void onCategoryClick(String slug, String category);

        void onBrandClick(String brand_slug, String brand_name, String image_url);

        void onShopClick(String shop_slug, String shop_name);

        void onCampaignCategoryClick(CampaignCategoryResponse model);

        void onCampaignProductClick(CampaignProductResponse model);

        void onFlashSaleClick(String slug);

        void onExpressClick(ExpressServiceModel model);

        void onShowMoreCategoryClick();

        void onShowMoreShopClick();

        void onShowMoreBrandClick();

        void onShowMoreExpressClick();

        void onShowMoreCampaignClick();
    }
}

