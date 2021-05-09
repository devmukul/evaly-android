package bd.com.evaly.evalyshop.ui.product.productDetails.models.variation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemVariationSizeBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_variation_size)
public abstract class VariantItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AttributeValuesItem model;

    @EpoxyAttribute
    boolean isSelected;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemVariationSizeBinding binding = (ItemVariationSizeBinding) holder.getDataBinding();
        binding.tvSize.setText(model.getValue());
        BindingUtils.markVariation(binding.cardSize, isSelected);
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}