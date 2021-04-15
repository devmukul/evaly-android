package bd.com.evaly.evalyshop.ui.product.productDetails.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;

public class RelatedProductsController extends EpoxyController {

    private List<ProductItem> productList = new ArrayList<>();
    private ClickListener clickListener;

    @Override
    protected void buildModels() {

        for (ProductItem item : productList) {
            new HomeProductGridModel_()
                    .id(((ProductItem) item).getSlug())
                    .model((ProductItem) item)
                    .clickListener((models, parentView, clickedView, position) -> {
                        clickListener.onProductClick(models.model);
                    })
                    .addTo(this);
        }

    }


    public void setProductList(List<ProductItem> productList) {
        this.productList = productList;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onProductClick(ProductItem item);
    }

}
