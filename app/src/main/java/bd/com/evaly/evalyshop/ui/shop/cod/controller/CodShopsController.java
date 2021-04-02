package bd.com.evaly.evalyshop.ui.shop.cod.controller;

import com.airbnb.epoxy.EpoxyModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.ui.base.BaseEpoxyController;
import bd.com.evaly.evalyshop.ui.browseProduct.model.GridItemModel_;
import bd.com.evaly.evalyshop.ui.epoxy.NoItemModel_;

public class CodShopsController extends BaseEpoxyController {

    private List<ShopListResponse> list = new ArrayList<>();
    private ClickListener clickListener;
    private boolean loading = true;

    public interface ClickListener {
        void onClick(String name, String slug);
    }

    @Override
    protected void buildModels() {
        for (ShopListResponse item : list) {
            new GridItemModel_()
                    .id(item.getSlug())
                    .title(item.getShopName())
                    .image(item.getShopImage())
                    .slug(item.getSlug())
                    .clickListener((model, parentView, clickedView, position) -> clickListener.onClick(model.title(), model.slug()))
                    .addTo(this);
        }

        new NoItemModel_()
                .id("empty_pppp")
                .image(R.drawable.ic_shop)
                .width(80)
                .text("No shops found")
                .spanSizeOverride((totalSpanCount, position, itemCount) -> 3)
                .addIf(!loading && list.size() == 0, this);
    }

    public void setList(List<ShopListResponse> list) {
        this.list = list;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
