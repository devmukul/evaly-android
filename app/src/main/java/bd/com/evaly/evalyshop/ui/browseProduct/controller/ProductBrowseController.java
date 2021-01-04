package bd.com.evaly.evalyshop.ui.browseProduct.controller;


import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.category.ChildCategoryResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.browseProduct.model.GridItemModel_;
import bd.com.evaly.evalyshop.ui.browseProduct.model.GridItemSkeletonModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;

public class ProductBrowseController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;
    private AppCompatActivity activity;
    private List<BaseModel> list = new ArrayList<>();
    private String categorySlug;
    private boolean loadingMore = true;
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onProductClick(ProductItem item);

        void onGridItemClick(String type, String title, String image, String slug);
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setList(List<BaseModel> list) {
        this.list = list;
    }

    public void clearList(){
        list.clear();
        loadingMore = true;
    }

    @Override
    protected void buildModels() {

        for (int i = 0; i < 6; i++) {
            new GridItemSkeletonModel_()
                    .id("grid_skeleton", i)
                    .addIf(list.size() == 0 && loadingMore, this);
        }

        for (BaseModel item : list) {
            if (item instanceof ProductItem)
                new HomeProductGridModel_()
                        .id(((ProductItem) item).getSlug())
                        .model((ProductItem) item)
                        .clickListener((models, parentView, clickedView, position) -> {
                            clickListener.onProductClick(models.model);
                        })
                        .addTo(this);
            else {
                String type = null;
                String title = null;
                String slug = null;
                String image = null;

                if (item instanceof ChildCategoryResponse) {
                    type = "category";
                    title = ((ChildCategoryResponse) item).getName();
                    slug = ((ChildCategoryResponse) item).getSlug();
                    image = ((ChildCategoryResponse) item).getImageUrl();
                } else if (item instanceof ShopListResponse) {
                    type = "shop";
                    title = ((ShopListResponse) item).getShopName();
                    slug = ((ShopListResponse) item).getSlug();
                    image = ((ShopListResponse) item).getShopImage();
                } else if (item instanceof BrandResponse) {
                    type = "brand";
                    title = ((BrandResponse) item).getName();
                    slug = ((BrandResponse) item).getSlug();
                    image = ((BrandResponse) item).getImageUrl();
                }

                new GridItemModel_()
                        .id(type, slug)
                        .title(title)
                        .type(type)
                        .slug(slug)
                        .image(image)
                        .slug(slug)
                        .clickListener((model, parentView, clickedView, position) -> clickListener.onGridItemClick(model.type(), model.title(), model.image(), model.slug()))
                        .addIf(title != null, this);


            }
        }

        loader.addIf(loadingMore, this);
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }


}

