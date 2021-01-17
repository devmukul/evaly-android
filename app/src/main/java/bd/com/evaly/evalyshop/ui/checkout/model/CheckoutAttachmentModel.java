package bd.com.evaly.evalyshop.ui.checkout.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.item_checkout_attachment)
public abstract class CheckoutAttachmentModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}