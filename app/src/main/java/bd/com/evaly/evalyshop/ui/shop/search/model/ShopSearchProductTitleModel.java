package bd.com.evaly.evalyshop.ui.shop.search.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemShopProductSearchTitleBinding;

@EpoxyModelClass(layout = R.layout.item_shop_product_search_title)
public abstract class ShopSearchProductTitleModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String search;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemShopProductSearchTitleBinding binding = (ItemShopProductSearchTitleBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}