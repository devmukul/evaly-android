package bd.com.evaly.evalyshop.epoxy.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;

import java.util.List;

import bd.com.evaly.evalyshop.epoxy.models.Header2Model_;
import bd.com.evaly.evalyshop.epoxy.models.HeaderModel_;
import bd.com.evaly.evalyshop.epoxy.models.ItemModel_;
import bd.com.evaly.evalyshop.models.product.ProductItem;

public class HeaderController extends Typed2EpoxyController<List<ProductItem>, Boolean> {

    @AutoModel
    HeaderModel_ headerModel;

    @AutoModel
    Header2Model_ headerModel2;

    @AutoModel
    ItemModel_ itemModel;

    @Override
    protected void buildModels(List<ProductItem> photos, Boolean loadingMore) {

        headerModel
                .title("My Photos")
                .addTo(this);

        headerModel2
                .title("My Photos")
                .addTo(this);

        for (int i = 0; i < 100; i++)
            new ItemModel_()
                    .id(i)
                    .title("Item")
                    .addTo(this);
    }

}
