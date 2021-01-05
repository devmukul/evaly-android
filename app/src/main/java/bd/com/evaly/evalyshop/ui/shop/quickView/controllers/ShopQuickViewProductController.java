package bd.com.evaly.evalyshop.ui.shop.quickView.controllers;


import android.content.Intent;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.models.ShopCategoryTitleModel_;
import bd.com.evaly.evalyshop.ui.shop.quickView.ShopQuickViewModel;
import bd.com.evaly.evalyshop.ui.shop.quickView.models.SmallProductGridModel_;

public class ShopQuickViewProductController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    NoProductModel_ noProductModel;
    @AutoModel
    ShopCategoryTitleModel_ categoryTitleModel;

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();

    private String shopSlug;
    private int cashbackRate = 0;
    private ShopQuickViewModel viewModel;
    private boolean loadingMore = true;
    private boolean emptyPage = false;
    private String categoryTitle = null;

    public ShopQuickViewProductController() {
        setDebugLoggingEnabled(true);
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
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


        for (ProductItem productItem : items) {
            new SmallProductGridModel_()
                    .id(productItem.getUniqueId())
                    .model(productItem)
                    .cashbackRate(cashbackRate)
                    .isShop(true)
                    .buyNowClickListener((model, parentView, clickedView, position) -> viewModel.setBuyNowLiveData(model.getModel().getSlug()))
                    .clickListener((model, parentView, clickedView, position) -> {
                        ProductItem item = model.getModel();

                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item.getSlug());
                        intent.putExtra("product_name", item.getName());
                        intent.putExtra("product_price", (int) Double.parseDouble(item.getMaxPrice()));

                        if (model.isShop())
                            intent.putExtra("shop_slug", shopSlug);

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

    public void addData(List<ProductItem> productItems) {
        this.items = productItems;
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void clear() {
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

    public void setViewModel(ShopQuickViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
        requestModelBuild();
    }
}

