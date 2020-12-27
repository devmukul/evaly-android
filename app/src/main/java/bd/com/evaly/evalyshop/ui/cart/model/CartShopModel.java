package bd.com.evaly.evalyshop.ui.cart.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartShopBinding;

@EpoxyModelClass(layout = R.layout.item_cart_shop)
public abstract class CartShopModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CartEntity model;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCartShopBinding binding = (ItemCartShopBinding) holder.getDataBinding();
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}