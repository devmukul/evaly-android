package bd.com.evaly.evalyshop.ui.cart.model;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartShopBinding;
import bd.com.evaly.evalyshop.util.Utils;

@EpoxyModelClass(layout = R.layout.item_cart_shop)
public abstract class CartShopModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CartEntity model;

    @EpoxyAttribute
    boolean noMargin;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCartShopBinding binding = (ItemCartShopBinding) holder.getDataBinding();
        if (noMargin) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, Utils.convertDpToPixel(15), 0, 0);
            binding.container.setLayoutParams(params);
        }
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}