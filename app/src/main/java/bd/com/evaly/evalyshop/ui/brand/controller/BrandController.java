package bd.com.evaly.evalyshop.ui.brand.controller;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BrandModelHeaderBinding;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.brand.model.BrandHeaderModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class BrandController extends EpoxyController {

    @AutoModel
    BrandHeaderModel_ headerModel_;
    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    NoProductModel_ noProductModel;
    private AppCompatActivity activity;
    private List<ProductItem> items = new ArrayList<>();
    private String brandName;
    private String brandLogo;
    private String categoryName;
    private boolean loadingMore = false;
    private boolean emptyPage = false;

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
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

        for (ProductItem productItem : items) {
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

        noProductModel
                .text("No Products Available")
                .image(R.drawable.ic_empty_product)
                .addIf(emptyPage, this);

        loader.addIf(loadingMore, this);
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

