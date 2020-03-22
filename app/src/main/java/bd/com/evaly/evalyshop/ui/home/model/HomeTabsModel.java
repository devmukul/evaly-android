package bd.com.evaly.evalyshop.ui.home.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelSliderBinding;
import bd.com.evaly.evalyshop.databinding.HomeModelTabsBinding;

@EpoxyModelClass(layout = R.layout.home_model_tabs)
public abstract class HomeTabsModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelTabsBinding binding = (HomeModelTabsBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) { }

}