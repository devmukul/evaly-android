package bd.com.evaly.evalyshop.ui.shop.quickView.controllers;


import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryItemModel_;

public class ShopQuickViewCategoryController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<TabsItem> categoryItems = new ArrayList<>();
    private ShopViewModel viewModel;
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

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
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
            new ShopCategoryItemModel_()
                    .id("category_" + categoryItems.get(i))
                    .model(categoryItems.get(i))
                    .clickListener((model, parentView, clickedView, position) -> {
                        viewModel.setSelectedCategoryLiveData(model.getModel());
                    })
                    .onBind((model, view, position) -> {
                        if (position >= categoryItems.size() - 4)
                            viewModel.loadShopCategories();
                    })
                    .addTo(this);
        }


        loader.onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.itemView.getLayoutParams();
                    params.setFullSpan(true);
                    if (categoryTitle != null && categoryItems.size() == 0) {
                        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        params.topMargin = 100;
                    } else {
                        params.topMargin = 0;
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    }
                })
                .addIf(loadingMore, this);

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

    public void setViewModel(ShopViewModel viewModel) {
        this.viewModel = viewModel;
    }

}

