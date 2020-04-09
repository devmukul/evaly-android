package bd.com.evaly.evalyshop.ui.express.products.controller;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.express.products.model.ExpressTitleModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class ExpressProductController extends EpoxyController {

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private AppDatabase appDatabase;
    private boolean loadingMore = false;
    private boolean emptyPage = false;
    private String title;

    @AutoModel
    HomeExpressModel_ expressModel;

    @AutoModel
    HomeTabsModel_ tabsModel;

    @AutoModel
    LoadingModel_ loader;

    @AutoModel
    ExpressTitleModel_ titleModel;


    @AutoModel
    NoProductModel_ noProductModel;

    public void showEmptyPage(boolean emptyPage, boolean build) {
        this.emptyPage = emptyPage;
        if (build)
            requestModelBuild();
    }


    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        expressModel
                .fragment(fragment)
                .activity(activity)
                .appDatabase(appDatabase)
                .addTo(this);

        titleModel
                .title(title)
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


        noProductModel
                .text("No Products Found")
                .image(R.drawable.ic_empty_product)
                .addIf(emptyPage, this);

        loader.addIf(loadingMore, this);
    }

    public void addData(List<ProductItem> productItems){
        this.items.addAll(productItems);
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        appDatabase = AppDatabase.getInstance(activity);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void clear(){
        items.clear();
        requestModelBuild();
    }

    public int listSize(){
        return items.size();
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
