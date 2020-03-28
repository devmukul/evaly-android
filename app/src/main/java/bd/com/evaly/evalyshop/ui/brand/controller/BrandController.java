package bd.com.evaly.evalyshop.ui.brand.controller;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.epoxy.models.LoadingModel_;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.brand.model.BrandHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class BrandController extends EpoxyController {

    private AppCompatActivity activity;
    private List<ProductItem> items = new ArrayList<>();
    private String brandName;
    private String brandLogo;
    private String categoryName;

    @AutoModel
    BrandHeaderModel_ headerModel_;

    @AutoModel
    LoadingModel_ loader;

    private boolean loadingMore = true;

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }


    public void setAttr(String brandName, String brandLogo, String categoryName){
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
                .addTo(this);

        for (ProductItem productItem: items) {
            new HomeProductGridModel_()
                    .id(productItem.getSlug())
                    .model(productItem)
                    .clickListener((model, parentView, clickedView, position) -> {
                        ProductItem item = model.getModel();
                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item.getSlug());
                        intent.putExtra("product_name", item.getName());
                        intent.putExtra("product_price", item.getMaxPrice());
                        if (items.get(position).getImageUrls().size() > 0)
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

    public void clear(){
        items.clear();
    }
}

