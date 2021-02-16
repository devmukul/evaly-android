package bd.com.evaly.evalyshop.ui.brand.controller;


import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AsyncEpoxyController;
import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.EpoxyController;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BrandModelHeaderBinding;
import bd.com.evaly.evalyshop.databinding.ShopModelTitleCategoryBinding;
import bd.com.evaly.evalyshop.databinding.ShopModelTitleProductBinding;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.brand.BrandViewModel;
import bd.com.evaly.evalyshop.ui.brand.model.BrandHeaderModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryCarouselModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryItemModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryTitleModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopProductTitleModel_;
import bd.com.evaly.evalyshop.util.Utils;

public class BrandController extends AsyncEpoxyController {

    @AutoModel
    BrandHeaderModel_ headerModel_;
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
    private boolean categoriesLoading;
    private String brandName;
    private String brandLogo;
    private String categoryName;
    private boolean loadingMore = false;
    private boolean emptyPage = false;
    private BrandResponse brandInfo;
    private BrandViewModel viewModel;
    private String categoryTitle = null;

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public void setViewModel(BrandViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setBrandInfo(BrandResponse brandInfo) {
        this.brandInfo = brandInfo;
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public void showEmptyPage(boolean emptyPage, boolean build) {
        this.emptyPage = emptyPage;
        if (build)
            requestModelBuild();
    }

    public void setAttr(String brandName, String brandLogo, String categoryName) {
        this.brandName = brandName;
        this.brandLogo = brandLogo;
        this.categoryName = categoryName;
    }

    @Override
    protected void buildModels() {

        headerModel_
                .brandName(brandName)
                .brandLogo(brandLogo)
                .categoryName(categoryName)
                .onBind((model, view, position) -> {
                    BrandModelHeaderBinding binding = (BrandModelHeaderBinding) view.getDataBinding();
                    binding.name.setText(brandName);
                    binding.categoryName.setText(Utils.capitalize(categoryName));
                    Glide.with(binding.getRoot())
                            .load(brandLogo)
                            .into(binding.logo);
                })
                .addTo(this);

        initCategory();

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

        noProductModel
                .text("No Products Available")
                .image(R.drawable.ic_empty_product)
                .addIf(items.size() == 0 && !loadingMore, this);

        loader.addIf(loadingMore, this);
    }

    private void initCategory() {
        categoryTitleModel
                .onBind((model, view, position) -> {
                    ShopModelTitleCategoryBinding binding = (ShopModelTitleCategoryBinding) view.getDataBinding();
                    binding.quickView.setVisibility(View.GONE);
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
                        if ((position >= categoryItems.size() - 4) && categoryItems.size() > 3)
                            viewModel.loadCategories();
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
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(5, activity),
                        50,
                        (int) Utils.convertDpToPixel(5, activity),
                        0))
                .models(categoryModelList)
                .addTo(this);
    }

    public void setCategoriesLoading(boolean categoriesLoading) {
        this.categoriesLoading = categoriesLoading;
    }

    public void setCategoryItems(List<TabsItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public void addData(List<ProductItem> productItems) {
        this.items = productItems;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void clear() {
        items.clear();
    }
}

