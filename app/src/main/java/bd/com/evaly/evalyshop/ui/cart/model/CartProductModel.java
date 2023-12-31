package bd.com.evaly.evalyshop.ui.cart.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartProductBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_cart_product)
public abstract class CartProductModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CartEntity model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener shopClickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener increaseQuantity;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener deceaseQuantity;

    @EpoxyAttribute(DoNotHash)
    CheckedListener checkedListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCartProductBinding binding = (ItemCartProductBinding) holder.getDataBinding();
        binding.imageHolder.setOnClickListener(clickListener);
        binding.productName.setOnClickListener(clickListener);
        binding.price.setOnClickListener(clickListener);
        binding.priceTotal.setOnClickListener(clickListener);
        binding.plus.setOnClickListener(increaseQuantity);
        binding.minus.setOnClickListener(deceaseQuantity);
        binding.checkBox.setClickable(false);
        binding.checkBoxHolder.setOnClickListener(view -> {
            checkedListener.onCheckedChanged(binding.checkBox, !binding.checkBox.isChecked(), model);
        });
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}