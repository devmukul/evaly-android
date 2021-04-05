package bd.com.evaly.evalyshop.ui.browseProduct.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemShopBrandSkeletonBinding;

@EpoxyModelClass(layout = R.layout.item_shop_brand_skeleton)
public abstract class GridItemSkeletonModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemShopBrandSkeletonBinding binding = (ItemShopBrandSkeletonBinding) holder.getDataBinding();

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}