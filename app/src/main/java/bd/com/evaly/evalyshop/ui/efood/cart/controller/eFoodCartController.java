package bd.com.evaly.evalyshop.ui.efood.cart.controller;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.OnModelClickListener;

import bd.com.evaly.evalyshop.ui.efood.cart.models.CartFoodModel_;

public class eFoodCartController extends EpoxyController {

    @Override
    protected void buildModels() {
        new CartFoodModel_()
                .id(1)
                .title("12 Inch Chicken Pizza")
                .price(1200.0)
                .minusClick(new OnModelClickListener<CartFoodModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                    @Override
                    public void onClick(CartFoodModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {
                        // TODO, increase quantity
                    }
                })
                .plusClick(new OnModelClickListener<CartFoodModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                    @Override
                    public void onClick(CartFoodModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {
                        // TODO, decrease quantity
                    }
                })
                .addTo(this);
    }

}
