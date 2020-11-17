package bd.com.evaly.evalyshop.ui.address.model;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAddressBinding;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_address)

public abstract class AddressListModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AddressResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener onEditClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener onDeleteClick;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemAddressBinding binding = (ItemAddressBinding) holder.getDataBinding();
        BindAddressModel.bind(binding, model);
        binding.editBtn.setOnClickListener(onEditClick);
        binding.deleteBtn.setOnClickListener(onDeleteClick);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

