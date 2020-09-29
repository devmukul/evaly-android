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
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxy.EpoxyDividerModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.home.HomeViewModel;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressCarouselModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.Utils;

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
    HomeExpressCarouselModel_ expressCarouselModel_;
    @AutoModel
    EpoxyDividerModel_ dividerModel_;
    @AutoModel
    HomeExpressHeaderModel_ expressHeaderModel_;
    @AutoModel
    HomeExpressSkeletonModel_ expressSkeletonBindingModel_;

    private AppCompatActivity activity;
    private Fragment fragment;
    private HomeViewModel homeViewModel;
    private List<ProductItem> items = new ArrayList<>();
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private AppDatabase appDatabase;
    private boolean loadingMore = true;
    private boolean isExpressLoading = true;


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
                .clickListener((model, parentView, clickedView, position) -> NavHostFragment.findNavController(fragment).navigate(R.id.expressProductSearchFragment))
                .addTo(this);

        List<DataBindingEpoxyModel> expressItemModels = new ArrayList<>();
        int count = 0;
        for (ExpressServiceModel model : itemsExpress) {
            if (count > 7)
                break;
            expressItemModels.add(new HomeExpressServiceModel_()
                    .clickListener((model1, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model1.getModel());
                        NavHostFragment.findNavController(fragment).navigate(R.id.evalyExpressFragment, bundle);
                    })
                    .id(model.getSlug())
                    .model(model));
            count++;
        }

        List<HomeExpressServiceSkeletonModel_> expressDummyItemModels = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expressDummyItemModels.add(new HomeExpressServiceSkeletonModel_()
                    .id("express_dummy" + i));
        }

        expressCarouselModel_
                .models(isExpressLoading ? expressDummyItemModels : expressItemModels)
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                    // view.setBackground(AppController.getmContext().getDrawable(R.drawable.white_to_grey_gradient));
                })
                .padding(new Carousel.Padding(
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(12, activity),
                        (int) Utils.convertDpToPixel(10, activity),
                        (int) Utils.convertDpToPixel(5, activity),
                        0))
                .addTo(this);

        tabsModel.activity(activity)
                .fragmentInstance(fragment)
                .homeViewModel(homeViewModel)
                .addTo(this);


        // product listing
        for (ProductItem productItem : items) {
            new HomeProductGridModel_()
                    .id(productItem.getSlug())
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
        this.items = productItems;
    }

    public void addExpressData(List<ExpressServiceModel> items) {
        this.itemsExpress.clear();
        this.itemsExpress.addAll(items);
        requestModelBuild();
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setExpressLoading(boolean expressLoading) {
        isExpressLoading = expressLoading;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        appDatabase = AppDatabase.getInstance(activity);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public HomeViewModel getHomeViewModel() {
        return homeViewModel;
    }

    public void setHomeViewModel(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }
}

