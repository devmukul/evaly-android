package bd.com.evaly.evalyshop.ui.home.controller;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxy.EpoxyDividerModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeCarouselModelModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressItemModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class HomeController extends EpoxyController {

    @AutoModel
    HomeSliderModel_ sliderModel;
    @AutoModel
    HomeWidgetModel_ widgetModel;
    @AutoModel
    HomeExpressModel_ expressModel;
    @AutoModel
    HomeTabsModel_ tabsModel;
    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    HomeCarouselModelModel_ expressCarouselModel_;
    @AutoModel
    EpoxyDividerModel_ dividerModel_;
    @AutoModel
    HomeExpressHeaderModel_ expressHeaderModel_;
    @AutoModel
    HomeExpressSkeletonModel_ expressSkeletonBindingModel_;

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private AppDatabase appDatabase;
    private boolean loadingMore = true;


    @Override
    protected void buildModels() {

        // slider model
        sliderModel
                .activity(activity)
                .fragment(fragment)
                .appDatabase(appDatabase)
                .addTo(this);

        // home widget buttons
        widgetModel
                .fragment(fragment)
                .activity(activity)
                .addTo(this);

        //express services carousel
        expressHeaderModel_
                .activity(activity)
                .addTo(this);

        List<HomeExpressItemModel_> expressItemModels = new ArrayList<>();
        for (ExpressServiceModel model : itemsExpress) {
            expressItemModels.add(new HomeExpressItemModel_()
                    .clickListener((model1, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model1.getModel());
                        NavHostFragment.findNavController(fragment).navigate(R.id.evalyExpressFragment, bundle);
                    })
                    .id(model.getSlug())
                    .model(model));
        }

        List<HomeExpressSkeletonModel_> expressDummyItemModels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            expressDummyItemModels.add(new HomeExpressSkeletonModel_()
                    .id("express_dummy" + i));
        }

        expressCarouselModel_
                .onBind((model, view, position) -> {
                    if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                        params.setFullSpan(true);
                    } else {
                        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setFullSpan(true);
                        view.setLayoutParams(params);
                    }
                    view.setBackgroundColor(Color.WHITE);
                })
                .padding(new Carousel.Padding(0, 0, 50, 0, 0))
                .models(expressItemModels.size() > 0 ? expressItemModels : expressDummyItemModels)
                .addTo(this);


        dividerModel_.addTo(this);

        // category, brand, shop tabs
        tabsModel
                .fragmentInstance(fragment)
                .addTo(this);

        // product listing
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

        // bottom loading bar
        loader.addIf(loadingMore, this);
    }

    public void addData(List<ProductItem> productItems) {
        this.items.addAll(productItems);
        requestModelBuild();
    }

    public void addExpressData(List<ExpressServiceModel> items) {
        this.itemsExpress.addAll(items);
        requestModelBuild();
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        appDatabase = AppDatabase.getInstance(activity);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}

