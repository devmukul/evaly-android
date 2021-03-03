package bd.com.evaly.evalyshop.ui.search.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.search.product.response.ProductsItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.search.model.SearchProductGridModel_;

public class GlobalSearchController extends EpoxyController {

    @AutoModel
    LoadingModel_ loaderBottom;

    private List<BaseModel> list = new ArrayList<>();
    private boolean isLoadingMore = true;

    @Override
    protected void buildModels() {
        for (BaseModel o : list) {
            if (o instanceof ProductsItem) {
                ProductsItem item = (ProductsItem) o;
                new SearchProductGridModel_()
                        .id("product", item.getShopItemId())
                        .name(item.getName())
                        .slug(item.getSlug())
                        .image(item.getProductImage())
                        .minPrice(item.getPrice())
                        .maxPrice(item.getPrice())
                        .discountedPrice(item.getDiscountedPrice())
                        .addTo(this);
            }
        }

        loaderBottom.addIf(isLoadingMore, this);
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public void setList(List<BaseModel> list) {
        this.list = list;
    }
}
