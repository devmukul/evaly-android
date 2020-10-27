package bd.com.evaly.evalyshop.ui.product.productDetails.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemVariationImageBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_variation_image)
public abstract class VariantImageItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AttributeValuesItem model;

    @EpoxyAttribute
    boolean isSelected;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemVariationImageBinding binding = (ItemVariationImageBinding) holder.getDataBinding();

        Glide.with(binding.image)
                .load(model.getColor_image())
                .into(binding.image);

        binding.text.setText(model.getValue());
        BindingUtils.markImageVariation(binding.holder, isSelected);
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}