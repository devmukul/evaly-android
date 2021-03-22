package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.item_campaign_product_category_skeleton)
public abstract class ProductCategorySkeletonModel extends DataBindingEpoxyModel {

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}