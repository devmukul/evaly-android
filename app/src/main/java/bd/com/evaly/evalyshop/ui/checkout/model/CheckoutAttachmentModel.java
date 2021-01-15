package bd.com.evaly.evalyshop.ui.checkout.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCheckoutProductBinding;

@EpoxyModelClass(layout = R.layout.item_checkout_attachment)
public abstract class CheckoutAttachmentModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCheckoutProductBinding binding = (ItemCheckoutProductBinding) holder.getDataBinding();

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}