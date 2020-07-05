package bd.com.evaly.evalyshop.ui.payment.bottomsheet.model;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemPaymentMethodBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_payment_method)
public abstract class PaymentMothodItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    String description;

    @EpoxyAttribute
    int image;

    @EpoxyAttribute
    boolean isSelected;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemPaymentMethodBinding binding = (ItemPaymentMethodBinding) holder.getDataBinding();

        binding.radioButton.setText(title);
        binding.description.setText(description);

        Glide.with(binding.getRoot())
                .load(image)
                .into(binding.image);

        binding.container.setOnClickListener(clickListener);
        binding.radioButton.setChecked(isSelected);

        if (isSelected)
            binding.container.setBackgroundColor(Color.parseColor("#f9f9f9"));
        else
            binding.container.setBackgroundColor(Color.parseColor("#ffffff"));
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}