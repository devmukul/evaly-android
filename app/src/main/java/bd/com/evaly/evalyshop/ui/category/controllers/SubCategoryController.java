package bd.com.evaly.evalyshop.ui.category.controllers;


import android.widget.LinearLayout;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.ui.category.CategoryViewModel;
import bd.com.evaly.evalyshop.ui.category.models.SubCategoryModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryTitleModel_;

public class SubCategoryController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    NoProductModel_ noProductModel;
    @AutoModel
    ShopCategoryTitleModel_ categoryTitleModel;

    private List<CategoryEntity> items = new ArrayList<>();
    private CategoryViewModel viewModel;
    private boolean loadingMore = true;
    private boolean emptyPage = false;

    private String categoryTitle = null;

    public SubCategoryController() {
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


        for (CategoryEntity item : items) {
            new SubCategoryModel_()
                    .id(item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {

                    })
                    .addTo(this);
        }

        noProductModel
                .text("No sub category Available")
                .image(R.drawable.ic_empty_product)
                .addIf(emptyPage, this);

        loader
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.itemView.getLayoutParams();
                    params.setFullSpan(true);
                    if (items.size() == 0) {
                        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        params.topMargin = 100;
                    } else {
                        params.topMargin = 0;
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    }
                })
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

