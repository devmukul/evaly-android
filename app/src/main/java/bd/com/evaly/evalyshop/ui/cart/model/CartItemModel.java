package bd.com.evaly.evalyshop.ui.cart.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_cart)
public abstract class CartItemModel extends DataBindingEpoxyModel {

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

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCartBinding binding = (ItemCartBinding) holder.getDataBinding();
        binding.container.setOnClickListener(clickListener);
        binding.plus.setOnClickListener(increaseQuantity);
        binding.minus.setOnClickListener(deceaseQuantity);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}