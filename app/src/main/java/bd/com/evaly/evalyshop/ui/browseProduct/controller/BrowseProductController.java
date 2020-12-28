package bd.com.evaly.evalyshop.ui.browseProduct.controller;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.browseProduct.BrowseProductViewModel;
import bd.com.evaly.evalyshop.ui.browseProduct.model.BrowseProductTabsModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class BrowseProductController extends EpoxyController {

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private String categorySlug;
    private BrowseProductViewModel browseProductViewModel;

    public BrowseProductViewModel getBrowseProductViewModel() {
        return browseProductViewModel;
    }

    public void setBrowseProductViewModel(BrowseProductViewModel browseProductViewModel) {
        this.browseProductViewModel = browseProductViewModel;
    }

    @AutoModel
    BrowseProductTabsModel_ tabsModel;

    @AutoModel
    LoadingModel_ loader;

    private boolean loadingMore = true;

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        tabsModel
                .fragmentInstance(fragment)
                .category(categorySlug)
                .browseProductViewModel(browseProductViewModel)
                .addTo(this);

        for (ProductItem productItem: items) {
            new HomeProductGridModel_()
                    .id(productItem.getUniqueId())
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

        loader.addIf(loadingMore, this);
    }

    public void addData(List<ProductItem> productItems){
        this.items.addAll(productItems);
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public void clear(){
        items.clear();
    }
}

