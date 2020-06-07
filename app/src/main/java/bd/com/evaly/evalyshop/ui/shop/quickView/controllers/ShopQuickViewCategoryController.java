package bd.com.evaly.evalyshop.ui.shop.quickView.controllers;


import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.databinding.ItemShopCategorySmallBinding;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LinearLoadingModel_;
import bd.com.evaly.evalyshop.ui.shop.quickView.ShopQuickViewModel;
import bd.com.evaly.evalyshop.ui.shop.quickView.models.SmallCategoryModel_;

public class ShopQuickViewCategoryController extends EpoxyController {

    @AutoModel
    LinearLoadingModel_ loader;

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<TabsItem> categoryItems = new ArrayList<>();
    private ShopQuickViewModel viewModel;
    private boolean loadingMore = true;
    private boolean emptyPage = false;
    private boolean categoriesLoading = false;
    private String categoryTitle = null;

    public boolean isCategoriesLoading() {
        return categoriesLoading;
    }

    public void setCategoriesLoading(boolean categoriesLoading) {
        this.categoriesLoading = categoriesLoading;
    }

    public ShopQuickViewCategoryController() {
        setDebugLoggingEnabled(true);
    }

    public void setLoadingMore(boolean loadingMore, boolean build) {
        this.loadingMore = loadingMore;
        if (build)
            requestModelBuild();
    }

    public void showEmptyPage(boolean emptyPage, boolean build) {
        this.emptyPage = emptyPage;
        if (build)
            requestModelBuild();
    }

    @Override
    protected void buildModels() {

        for (int i = 0; i < categoryItems.size(); i++) {
            new SmallCategoryModel_()
                    .id("category_" + categoryItems.get(i))
                    .model(categoryItems.get(i))
                    .clickListener((model, parentView, clickedView, position) -> {
                        viewModel.setSelectedCategoryLiveData(model.getModel());
                    })
                    .onBind((model, view, position) -> {
                        ItemShopCategorySmallBinding binding = (ItemShopCategorySmallBinding) view.getDataBinding();
                        String selectedCategorySlug = viewModel.getCategorySlug();
                        if ((position == 0 && selectedCategorySlug == null) ||
                                (selectedCategorySlug != null && selectedCategorySlug.equalsIgnoreCase(model.getModel().getSlug())))
                            binding.selectedBrd.setVisibility(View.VISIBLE);
                        else
                            binding.selectedBrd.setVisibility(View.GONE);

                        if (position >= categoryItems.size() - 4)
                            viewModel.loadShopCategories();
                    })
                    .addTo(this);
        }

        loader.addIf(loadingMore, this);

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
        categoryItems.clear();
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setViewModel(ShopQuickViewModel viewModel) {
        this.viewModel = viewModel;
    }

}

