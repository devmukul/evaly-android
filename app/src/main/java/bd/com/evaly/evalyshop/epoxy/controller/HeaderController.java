package bd.com.evaly.evalyshop.epoxy.controller;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.DividerViewBindingModel_;
import bd.com.evaly.evalyshop.epoxy.models.GridItemModel_;
import bd.com.evaly.evalyshop.epoxy.models.ItemModel_;
import bd.com.evaly.evalyshop.epoxy.models.LoadingModel_;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;

public class HeaderController extends EpoxyController {

    @AutoModel
    HomeSliderModel_ sliderModel;
    @AutoModel
    HomeWidgetModel_ widgetModel;
    @AutoModel
    HomeTabsModel_ tabsModel;
    @AutoModel
    ItemModel_ itemModel;
    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    DividerViewBindingModel_ divider;
    @AutoModel
    DividerViewBindingModel_ divider2;
    private AppCompatActivity activity;
    private List<ProductItem> items = new ArrayList<>();
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
                .addTo(this);

        tabsModel.addTo(this);


        for (ProductItem productItem : items) {
            // new EpxyGridItemBindingModel_().id(productItem.getSlug()).name(productItem.getName()).addTo(this);
            new GridItemModel_().id(productItem.getSlug()).text(productItem.getName()).addTo(this);
        }

        loader.addIf(loadingMore, this);

    }


    public void addData(List<ProductItem> productItems) {
        this.items.addAll(productItems);
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}
