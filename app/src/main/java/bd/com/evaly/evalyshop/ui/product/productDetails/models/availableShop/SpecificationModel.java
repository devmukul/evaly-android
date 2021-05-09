package bd.com.evaly.evalyshop.ui.product.productDetails.models.availableShop;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemSpecificationBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductSpecificationsItem;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

@EpoxyModelClass(layout = R.layout.item_variation_size)
public abstract class SpecificationModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    ProductSpecificationsItem model;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemSpecificationBinding binding = (ItemSpecificationBinding) holder.getDataBinding();
        binding.specTitle.setText(model.getSpecificationName());
        binding.specValue.setText(model.getSpecificationValue());
    }

}