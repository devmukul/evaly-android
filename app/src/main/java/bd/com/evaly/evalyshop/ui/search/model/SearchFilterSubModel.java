package bd.com.evaly.evalyshop.ui.search.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemFilterSubBinding;
import bd.com.evaly.evalyshop.ui.search.controller.CheckedListener;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_filter_sub)
public abstract class SearchFilterSubModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String value;

    @EpoxyAttribute
    String count;

    @EpoxyAttribute
    boolean selected;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    CheckedListener checkedListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemFilterSubBinding binding = (ItemFilterSubBinding) holder.getDataBinding();
        binding.value.setText(value);
        binding.count.setText(count);

        binding.checkBox.setSelected(selected);
        binding.checkBox.setClickable(false);

        binding.holder.setOnClickListener(view -> {
            binding.checkBox.toggleAnimated();
            checkedListener.onCheckedChanged(binding.checkBox, binding.checkBox.isChecked(), value);
        });
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

