package bd.com.evaly.evalyshop.ui.search.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemFilterSubBinding;

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

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemFilterSubBinding binding = (ItemFilterSubBinding) holder.getDataBinding();
        binding.value.setText(value);
        binding.count.setText(count);

        binding.radioButton.setSelected(selected);
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

