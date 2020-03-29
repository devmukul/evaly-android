package bd.com.evaly.evalyshop.ui.shop.controller;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.epoxy.models.LoadingModel_;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryModel_;
import bd.com.evaly.evalyshop.ui.shop.models.ShopHeaderModel_;

public class ShopController extends EpoxyController {

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private Shop shopInfo;
    private int cashbackRate = 0;

    @AutoModel
    ShopHeaderModel_ headerModel;

    @AutoModel
    LoadingModel_ loader;

    @AutoModel
    ShopCategoryModel_ categoryModel;

    private boolean loadingMore = true;

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setAttr(Shop shopInfo){
        this.shopInfo = shopInfo;
    }

    @Override
    protected void buildModels() {


        headerModel
                .activity(activity)
                .fragment(fragment)
                .shopInfo(shopInfo)
                .addTo(this);

        categoryModel
                .activity(activity)
                .fragment(fragment)
                .addTo(this);



        for (ProductItem productItem: items) {
            new HomeProductGridModel_()
                    .id(productItem.getSlug())
                    .model(productItem)
                    .cashbackRate(cashbackRate)
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

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getCashbackRate() {
        return cashbackRate;
    }

    public void setCashbackRate(int cashbackRate) {
        this.cashbackRate = cashbackRate;
    }
}

