package bd.com.evaly.evalyshop.ui.campaign.model;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignBannerBinding;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_banner)
public abstract class CampaignBannerModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CampaignCategoryResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCampaignBannerBinding binding = (ItemCampaignBannerBinding) holder.getDataBinding();
        binding.getRoot().setOnClickListener(clickListener);

        ViewCompat.setBackgroundTintList(
                binding.gradient,
                ColorStateList.valueOf(Color.parseColor(model.getBannerPrimaryBgColor())));
        BindingUtils.setImage(binding.image, model.getBannerImage(), R.drawable.bg_fafafa_round, R.drawable.ic_evaly_placeholder, 1450, 460, false);

        binding.headerText.setText(model.getBannerHeaderText().toUpperCase());
        binding.subText.setText(model.getBannerSubText());
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}