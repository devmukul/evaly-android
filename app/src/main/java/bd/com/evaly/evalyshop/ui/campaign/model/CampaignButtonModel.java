package bd.com.evaly.evalyshop.ui.campaign.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignButtonBinding;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_button)
public abstract class CampaignButtonModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CampaignCategoryResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCampaignButtonBinding binding = (ItemCampaignButtonBinding) holder.getDataBinding();

        BindingUtils.setImage(binding.image, model.getImage(), R.drawable.bg_eee_round, R.drawable.ic_evaly_placeholder, 400, 400);

        binding.name.setText(model.getName());
        binding.image.setOnClickListener(clickListener);
    }

    @Override
    public boolean shouldSaveViewState() {
        return true;
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}