package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCycloneBottomBarBinding;

@EpoxyModelClass(layout = R.layout.item_cyclone_bottom_bar)
public abstract class CycloneBottomBarModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCycloneBottomBarBinding binding = (ItemCycloneBottomBarBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

