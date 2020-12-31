package bd.com.evaly.evalyshop.ui.browseProduct.controller;


import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.OnModelClickListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.category.ChildCategoryResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.browseProduct.model.GridItemModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class ProductBrowseController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;
    private AppCompatActivity activity;
    private List<BaseModel> list = new ArrayList<>();
    private String categorySlug;
    private boolean loadingMore = true;

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setList(List<BaseModel> list) {
        this.list = list;
    }

    @Override
    protected void buildModels() {

        for (BaseModel item : list) {
            if (item instanceof ProductItem)
                new HomeProductGridModel_()
                        .id(((ProductItem) item).getSlug())
                        .model((ProductItem) item)
                        .clickListener((models, parentView, clickedView, position) -> {
                            ProductItem model = models.getModel();
                            Intent intent = new Intent(activity, ViewProductActivity.class);
                            intent.putExtra("product_slug", model.getSlug());
                            intent.putExtra("product_name", model.getName());
                            intent.putExtra("product_price", model.getMaxPrice());
                            if (model.getImageUrls().size() > 0)
                                intent.putExtra("product_image", model.getImageUrls().get(0));
                            activity.startActivity(intent);
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
                        .clickListener(new OnModelClickListener<GridItemModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                            @Override
                            public void onClick(GridItemModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {

                            }
                        })
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

