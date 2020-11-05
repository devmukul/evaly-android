package bd.com.evaly.evalyshop.ui.search.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemFilterRootBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_filter_root)
public abstract class SearchFilterRootModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String name;

    @EpoxyAttribute
    String subText;

    @EpoxyAttribute
    boolean selected;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemFilterRootBinding binding = (ItemFilterRootBinding) holder.getDataBinding();
        binding.title.setText(name);
        binding.value.setText(subText);

        if (subText == null)
            binding.value.setVisibility(View.VISIBLE);
        else
            binding.value.setVisibility(View.GONE);

        if (selected)
            binding.selectedBrd.setVisibility(View.VISIBLE);
        else
            binding.selectedBrd.setVisibility(View.GONE);

        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

