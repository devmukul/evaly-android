package bd.com.evaly.evalyshop.ui.search.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.search.product.response.ProductsItem;
import bd.com.evaly.evalyshop.ui.browseProduct.model.GridItemModel_;
import bd.com.evaly.evalyshop.ui.epoxy.EmptySpaceModel_;
import bd.com.evaly.evalyshop.ui.epoxy.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxy.NoItemModel_;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchViewModel;
import bd.com.evaly.evalyshop.ui.search.model.SearchProductGridModel_;

public class GlobalSearchController extends EpoxyController {

    @AutoModel
    LoadingModel_ loaderBottom;

    private List<BaseModel> list = new ArrayList<>();
    private boolean isLoadingMore = true;
    private GlobalSearchViewModel viewModel;
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setViewModel(GlobalSearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

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
                        .clickListener((model, parentView, clickedView, position) -> clickListener.onProductClick(item))
                        .addTo(this);
            } else if (o instanceof ShopListResponse) {
                new GridItemModel_()
                        .id(((ShopListResponse) o).getSlug())
                        .title(((ShopListResponse) o).getShopName())
                        .slug(((ShopListResponse) o).getSlug())
                        .image(((ShopListResponse) o).getShopImage())
                        .type("shop")
                        .clickListener((model, parentView, clickedView, position) -> clickListener.onGridItemClick(model.type(), model.title(), model.image(), model.slug()))
                        .addTo(this);
            } else if (o instanceof BrandResponse) {
                new GridItemModel_()
                        .id("brand", ((BrandResponse) o).getSlug())
                        .title(((BrandResponse) o).getName())
                        .slug(((BrandResponse) o).getSlug())
                        .image(((BrandResponse) o).getImageUrl())
                        .type("brand").clickListener((model, parentView, clickedView, position) -> clickListener.onGridItemClick(model.type(), model.title(), model.image(), model.slug()))
                        .addTo(this);
            }
        }

        new EmptySpaceModel_()
                .id("empty_space_id")
                .height(200)
                .addIf(isLoadingMore && viewModel.getPage() < 2, this);

        loaderBottom.addIf(isLoadingMore, this);

        new NoItemModel_()
                .id("empty_page_id")
                .image(R.drawable.ic_search_not_found)
                .text("No results found")
                .width(600)
                .addIf(!isLoadingMore && list.size() == 0, this);

    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public void setList(List<BaseModel> list) {
        this.list = list;
    }

    public interface ClickListener {
        void onProductClick(ProductsItem item);

        void onGridItemClick(String type, String title, String image, String slug);
    }
}
