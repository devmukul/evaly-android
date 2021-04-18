package bd.com.evaly.evalyshop.ui.followedShops.controller;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shop.FollowResponse;
import bd.com.evaly.evalyshop.ui.base.BaseEpoxyController;
import bd.com.evaly.evalyshop.ui.browseProduct.model.GridItemModel_;
import bd.com.evaly.evalyshop.ui.epoxy.NoItemModel_;

public class FollowedShopsController extends BaseEpoxyController {

    private List<FollowResponse> list = new ArrayList<>();
    private ClickListener clickListener;
    private boolean loading = true;

    public interface ClickListener {
        void onClick(String name, String slug);
    }

    @Override
    protected void buildModels() {
        for (FollowResponse item : list) {
            new GridItemModel_()
                    .id(item.getShopSlug())
                    .title(item.getShopName())
                    .image(item.getShopImageUrl())
                    .slug(item.getShopSlug())
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

    public void setList(List<FollowResponse> list) {
        this.list = list;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
