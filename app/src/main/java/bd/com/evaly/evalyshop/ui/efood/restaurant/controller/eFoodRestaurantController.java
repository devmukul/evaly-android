package bd.com.evaly.evalyshop.ui.efood.restaurant.controller;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.OnModelClickListener;

import bd.com.evaly.evalyshop.ui.efood.restaurant.model.FoodItemModel_;

public class eFoodRestaurantController extends EpoxyController {

    @Override
    protected void buildModels() {

        new FoodItemModel_()
                .id(1)
                .name("12 inch check pizza")
                .description("Food description goes here")
                .clickListener(new OnModelClickListener<FoodItemModel_, DataBindingEpoxyModel.DataBindingHolder>() {
                    @Override
                    public void onClick(FoodItemModel_ model, DataBindingEpoxyModel.DataBindingHolder parentView, View clickedView, int position) {
                        // increase quantity
                    }
                })
                .addTo(this);

    }


}
