package bd.com.evaly.evalyshop.ui.checkout.controller;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.ui.checkout.model.CheckoutProductModel_;

public class CheckoutProductController extends EpoxyController {

    private List<CartEntity> list = new ArrayList<>();

    public void setList(List<CartEntity> list) {
        this.list = list;
    }

    public CheckoutProductController() {
        setFilterDuplicates(true);
    }

    @Override
    protected void buildModels() {
        for (CartEntity item : list) {
            new CheckoutProductModel_()
                    .id(item.getProductID() + " " + item.getShopSlug())
                    .model(item)
                    .addTo(this);
        }
    }
}
