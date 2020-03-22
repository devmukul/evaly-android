package bd.com.evaly.evalyshop.ui.home.controller;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.epoxy.models.GridItemModel_;
import bd.com.evaly.evalyshop.epoxy.models.LoadingModel_;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;

public class HomeController extends EpoxyController {

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();

    @AutoModel
    HomeSliderModel_ sliderModel;

    @AutoModel
    HomeWidgetModel_ widgetModel;

    @AutoModel
    HomeTabsModel_ tabsModel;


    @AutoModel
    LoadingModel_ loader;

    private boolean loadingMore = true;


    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        sliderModel
                .activity(activity)
                .addTo(this);

        widgetModel
                .fragment(fragment)
                .activity(activity)
                .addTo(this);

        tabsModel
                .fragmentInstance(fragment)
                .addTo(this);


        for (ProductItem productItem: items) {
            new GridItemModel_().id(productItem.getSlug()).text(productItem.getName()).addTo(this);
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
}

