package bd.com.evaly.evalyshop.ui.campaign.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignSliderBinding;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_slider)
public abstract class CampaignSliderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CampaignCategoryResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCampaignSliderBinding binding = (ItemCampaignSliderBinding) holder.getDataBinding();

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getBannerImage())
                .apply(new RequestOptions().override(1450, 460))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.sliderImage);

        binding.sliderImage.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}