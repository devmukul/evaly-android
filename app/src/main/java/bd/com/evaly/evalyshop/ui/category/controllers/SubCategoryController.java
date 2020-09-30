package bd.com.evaly.evalyshop.ui.category.controllers;


import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.ui.category.CategoryViewModel;
import bd.com.evaly.evalyshop.ui.category.models.SubCategoryModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryTitleModel_;

public class SubCategoryController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    NoItemModel_ noItemModel;
    @AutoModel
    ShopCategoryTitleModel_ categoryTitleModel;

    private List<CategoryEntity> items = new ArrayList<>();
    private CategoryViewModel viewModel;
    private NavController navController;
    private boolean loadingMore = true;
    private boolean emptyPage = false;
    private String categoryTitle = null;

    public SubCategoryController() {
        setDebugLoggingEnabled(true);
        setSpanCount(2);
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

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    @Override
    protected void buildModels() {

        for (CategoryEntity item : items) {
            new SubCategoryModel_()
                    .id(item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("slug", model.model().getSlug());
                        bundle.putString("category", model.model().getSlug());
                        navController.navigate(R.id.browseProductFragment, bundle);
                    })
                    .addTo(this);
        }

        noItemModel
                .text("No categories found")
                .image(R.drawable.ic_category)
                .width(60)
                .imageTint("#888888")
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 3)
                .addIf(items.size() == 0 && !loadingMore, this);

        loader
                .onBind((model, view, position) -> {
                    GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.itemView.getLayoutParams();
                    if (items.size() == 0) {
                        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        params.topMargin = 100;
                    } else {
                        params.topMargin = 0;
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    }
                })
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 3)
                .addIf(loadingMore, this);

    }

    public void addData(List<CategoryEntity> productItems) {
        this.items = productItems;
        requestModelBuild();
    }

    public void clear() {
        items.clear();
    }

    public void setViewModel(CategoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
        requestModelBuild();
    }
}

