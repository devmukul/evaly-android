package bd.com.evaly.evalyshop.epoxy.controller;

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
import bd.com.evaly.evalyshop.ui.home.model.HomeWidgetModel_;

public class HeaderController extends EpoxyController {

    private List<ProductItem> items = new ArrayList<>();

    @AutoModel
    HomeSliderModel_ headerModel;

    @AutoModel
    HomeWidgetModel_ headerModel2;

    @AutoModel
    ItemModel_ itemModel;

    @AutoModel
    LoadingModel_ loader;

    @AutoModel
    DividerViewBindingModel_ divider;


    @AutoModel
    DividerViewBindingModel_ divider2;



    private boolean loadingMore = true;


    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        headerModel
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 1)
                .addTo(this);

        headerModel2
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 1)
                .addTo(this);


        for (ProductItem productItem: items) {
            // new EpxyGridItemBindingModel_().id(productItem.getSlug()).name(productItem.getName()).addTo(this);
            new GridItemModel_().id(productItem.getSlug()).text(productItem.getName()).addTo(this);
        }

        loader.addIf(loadingMore, this);

    }


    public void addData(List<ProductItem> productItems){
        this.items.addAll(productItems);
        requestModelBuild();
    }

}
