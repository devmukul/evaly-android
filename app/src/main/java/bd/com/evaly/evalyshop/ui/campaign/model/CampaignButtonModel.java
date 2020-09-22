package bd.com.evaly.evalyshop.ui.campaign.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignButtonBinding;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;

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

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getImage())
                .error(R.drawable.cover)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.image);

        binding.name.setText(model.getName());

        binding.image.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}