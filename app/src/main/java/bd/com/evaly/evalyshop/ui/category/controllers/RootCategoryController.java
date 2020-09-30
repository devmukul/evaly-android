package bd.com.evaly.evalyshop.ui.category.controllers;


import android.graphics.Color;
import android.view.View;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.databinding.ItemRootCategoryBinding;
import bd.com.evaly.evalyshop.ui.category.CategoryViewModel;
import bd.com.evaly.evalyshop.ui.category.models.RootCategoryModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LinearLoadingModel_;

public class RootCategoryController extends EpoxyController {

    @AutoModel
    LinearLoadingModel_ loader;

    private List<CategoryEntity> categoryItems = new ArrayList<>();
    private CategoryViewModel viewModel;
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

    public RootCategoryController() {
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
            new RootCategoryModel_()
                    .id("category_" + categoryItems.get(i))
                    .model(categoryItems.get(i))
                    .title(categoryItems.get(i).getName())
                    .image(categoryItems.get(i).getImageUrl())
                    .isSelected(categoryItems.get(i).isSelected())
                    .clickListener((model, parentView, clickedView, position) -> {
                        CategoryEntity selectedCategory = model.model;
                        for (int j = 0; j < categoryItems.size(); j++) {
                            if (categoryItems.get(j).getSlug().equals(selectedCategory.getSlug()))
                                categoryItems.get(j).setSelected(true);
                            else
                                categoryItems.get(j).setSelected(false);
                        }
                        viewModel.setSelectedCategoryLiveData(selectedCategory);
                        requestModelBuild();
                    })
                    .onBind((model, view, position) -> {
                        ItemRootCategoryBinding binding = (ItemRootCategoryBinding) view.getDataBinding();
                        if (model.model.isSelected()) {
                            binding.categoryItem.setBackgroundColor(Color.parseColor("#ffffff"));
                            binding.selectedBrd.setVisibility(View.VISIBLE);
                        } else {
                            binding.categoryItem.setBackgroundColor(Color.parseColor("#FBFCFF"));
                            binding.selectedBrd.setVisibility(View.GONE);
                        }
                    })
                    .addTo(this);
        }

        loader.addIf(loadingMore, this);

    }

    public void addCategoryData(List<CategoryEntity> categoryItems, boolean build) {
        this.categoryItems = categoryItems;
        if (build)
            requestModelBuild();
    }

    public void clear() {
        categoryItems.clear();
    }

    public void setViewModel(CategoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void deselectCategory() {
        for (int j = 0; j < categoryItems.size(); j++) {
            categoryItems.get(j).setSelected(false);
        }
        requestModelBuild();
    }
}

