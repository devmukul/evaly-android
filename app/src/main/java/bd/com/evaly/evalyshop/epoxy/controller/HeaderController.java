package bd.com.evaly.evalyshop.epoxy.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.epoxy.models.GridItemModel_;
import bd.com.evaly.evalyshop.epoxy.models.Header2Model_;
import bd.com.evaly.evalyshop.epoxy.models.HeaderModel_;
import bd.com.evaly.evalyshop.epoxy.models.ItemModel_;
import bd.com.evaly.evalyshop.epoxy.models.LoadingModel_;
import bd.com.evaly.evalyshop.models.product.ProductItem;

public class HeaderController extends EpoxyController {

    private List<ProductItem> items = new ArrayList<>();

    @AutoModel
    HeaderModel_ headerModel;

    @AutoModel
    Header2Model_ headerModel2;

    @AutoModel
    ItemModel_ itemModel;

    @AutoModel
    LoadingModel_ loader;

    private boolean loadingMore = true;


    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        headerModel
                .title("My Photos")
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 1)
                .addTo(this);

        headerModel2
                .title("My Photos")
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 1)
                .addTo(this);

        for (ProductItem productItem: items) {
            new GridItemModel_()
                    .id(productItem.getSlug())
                    .title(productItem.getName())
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> totalSpanCount / 2)
                    .addTo(this);
        }


        loader
                .addIf(loadingMore, this);


    }


    public void addData(List<ProductItem> productItems){
        this.items.addAll(productItems);
        requestModelBuild();
    }

}
