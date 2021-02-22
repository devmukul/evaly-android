package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCycloneBottomBarBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

@EpoxyModelClass(layout = R.layout.item_cyclone_bottom_bar)
public abstract class CycloneBottomBarModel extends BaseDataBindingEpoxyModel {

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        ItemCycloneBottomBarBinding binding = (ItemCycloneBottomBarBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

