package bd.com.evaly.evalyshop.ui.search.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.search.SearchHitResponse;
import bd.com.evaly.evalyshop.ui.search.model.SearchProductGridModel_;

public class GlobalSearchController extends EpoxyController {

    List<SearchHitResponse> list = new ArrayList<>();

    @Override
    protected void buildModels() {
        for (Object o : list) {
            if (o instanceof SearchHitResponse) {
                SearchHitResponse item = (SearchHitResponse) o;
                new SearchProductGridModel_()
                        .id("product", item.getShopItemId())
                        .name(item.getName())
                        .slug(item.getSlug())
                        .image(item.getProductImage())
                        .minPrice(item.getMinPrice())
                        .maxPrice(item.getMaxPrice())
                        .discountedPrice(item.getDiscountedPrice())
                        .addTo(this);
            }
        }
    }

    public void setList(List<SearchHitResponse> list) {
        this.list = list;
    }
}
