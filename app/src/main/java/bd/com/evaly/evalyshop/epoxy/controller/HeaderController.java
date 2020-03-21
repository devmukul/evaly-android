package bd.com.evaly.evalyshop.epoxy.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;

import java.util.List;

import bd.com.evaly.evalyshop.epoxy.models.GridItemModel_;
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
                .spanSizeOverride((totalSpanCount, position, itemCount) -> totalSpanCount / 1)
                .addTo(this);

        headerModel2
                .title("My Photos")
                .spanSizeOverride((totalSpanCount, position, itemCount) -> totalSpanCount / 1)
                .addTo(this);

        for (int i = 0; i < 5; i++)
            new ItemModel_()
                    .id(i)
                    .title("Item")
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> totalSpanCount / 1)
                    .addTo(this);

        String item = "item ";

        for (int i = 0; i < 10; i++) {
            item = item + item;
            new GridItemModel_()
                    .id(i)
                    .title(item)
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> totalSpanCount / 2)
                    .addTo(this);
        }
    }

}
