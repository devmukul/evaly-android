package bd.com.evaly.evalyshop.ui.payment.bottomsheet.model;

import android.graphics.Color;
import android.text.Html;
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
public abstract class PaymentMethodItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    String description;

    @EpoxyAttribute
    String badgeText;

    @EpoxyAttribute
    int image;

    @EpoxyAttribute
    boolean isSelected;

    @EpoxyAttribute
    boolean isEnabled;

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

        binding.radioButton.setChecked(isSelected);

        if (isSelected)
            binding.container.setBackgroundColor(binding.container.getContext().getResources().getColor(R.color.f9f9f9));
        else
            binding.container.setBackgroundColor(binding.container.getContext().getResources().getColor(R.color.fff));

        if (isEnabled) {
            binding.container.setAlpha(1);
            binding.container.setOnClickListener(clickListener);
        } else {
            binding.container.setAlpha(0.7f);
        }

        if ((badgeText != null && !badgeText.equals("")) && !isEnabled)
            binding.radioButton.setText(Html.fromHtml(title + " <small>(" + badgeText + ")</small>"));
        else
            binding.radioButton.setText(title);

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}