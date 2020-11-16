package bd.com.evaly.evalyshop.ui.address.model;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.Objects;

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
        binding.fullName.setText(model.getFullName());
        binding.addressLine1.setText(model.getAddress());
        binding.addressLine2.setText(model.getArea() + ", " + model.getCity());
        binding.editBtn.setOnClickListener(onEditClick);
        binding.deleteBtn.setOnClickListener(onDeleteClick);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressListModel)) return false;
        if (!super.equals(o)) return false;
        AddressListModel that = (AddressListModel) o;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }
}

