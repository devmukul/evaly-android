package bd.com.evaly.evalyshop.epoxy.models;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.EpxyGridItemBinding;

@EpoxyModelClass(layout = R.layout.epxy_grid_item)
public abstract class GridItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String text;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        EpxyGridItemBinding binding = (EpxyGridItemBinding) holder.getDataBinding();
        binding.text.setText(text);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

       //  binding.setVariable(BR.textRes, textRes);

    }



}
