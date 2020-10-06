package bd.com.evaly.evalyshop.ui.campaign.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignTitleBinding;

@EpoxyModelClass(layout = R.layout.item_campaign_title)
public abstract class CampaignTitleModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCampaignTitleBinding binding = (ItemCampaignTitleBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
        binding.title.setText(title);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}