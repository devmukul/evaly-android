package bd.com.evaly.evalyshop.ui.campaign.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_banner_skeleton)
public abstract class CampaignBannerSkeletonModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CampaignCategoryResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}