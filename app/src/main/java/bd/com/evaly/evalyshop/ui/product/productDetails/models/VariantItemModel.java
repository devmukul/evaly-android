package bd.com.evaly.evalyshop.ui.product.productDetails.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ItemVariationSizeBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_variation_image)
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
        if (isSelected) {
            binding.cardSize.setBackground(AppController.getmContext().getDrawable(R.drawable.bg_variation_size_selected));
        } else {
            binding.cardSize.setBackground(AppController.getmContext().getDrawable(R.drawable.bg_variation_size_default));
        }
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}