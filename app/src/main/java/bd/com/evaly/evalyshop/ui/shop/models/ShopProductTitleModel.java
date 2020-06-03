package bd.com.evaly.evalyshop.ui.shop.models;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ShopModelTitleProductBinding;

@EpoxyModelClass(layout = R.layout.shop_model_title_product)
public abstract class ShopProductTitleModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ShopModelTitleProductBinding binding = (ShopModelTitleProductBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}