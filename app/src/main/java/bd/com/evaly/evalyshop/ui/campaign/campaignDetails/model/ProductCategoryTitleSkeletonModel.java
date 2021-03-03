package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignProductCategoryTitleSkeletonBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

@EpoxyModelClass(layout = R.layout.item_campaign_product_category_title_skeleton)
public abstract class ProductCategoryTitleSkeletonModel extends BaseDataBindingEpoxyModel {


    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        ItemCampaignProductCategoryTitleSkeletonBinding binding = (ItemCampaignProductCategoryTitleSkeletonBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);


    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}