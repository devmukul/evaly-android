package bd.com.evaly.evalyshop.ui.address.model;

import bd.com.evaly.evalyshop.databinding.ItemAddressBinding;
import bd.com.evaly.evalyshop.models.user.AddressItem;

public class BindAddressModel {
    public static void bind(ItemAddressBinding binding, AddressItem model) {
        binding.fullName.setText(model.getFullName());
        binding.addressLine1.setText(model.getAddress() + ", " + model.getArea());
        binding.addressLine2.setText(model.getCity() + ", " + model.getRegion());
    }
}
