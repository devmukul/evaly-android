package bd.com.evaly.evalyshop.ui.campaign.model;

import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignSubBinding;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_sub)
public abstract class CampaignSubModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public SubCampaignResponse model;

    @EpoxyAttribute
    public boolean isStaggered = true;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCampaignSubBinding binding = (ItemCampaignSubBinding) holder.getDataBinding();

        if (isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(false);
        }

        binding.title.setText(Html.fromHtml(model.getName()));
        BindingUtils.setImage(binding.image, model.getImage(), R.drawable.ic_evaly_placeholder, R.drawable.ic_evaly_placeholder, 300, 300, true);

        binding.getRoot().setOnClickListener(clickListener);


        if (model.getCashbackText() == null || model.getCashbackText().equals("") || model.getCashbackText().contains("00.00")) {
            binding.cashBackText.setVisibility(View.GONE);
        } else {
            binding.cashBackText.setVisibility(View.VISIBLE);
            binding.cashBackText.setText(model.getCashbackText().replace(".00", "").replace("cashback", "Cashback"));
        }

        if (model.getBadgeText() == null || model.getBadgeText().equals("")) {
            binding.badgeText.setVisibility(View.GONE);
        } else {
            binding.badgeText.setVisibility(View.VISIBLE);
            binding.badgeText.setText(model.getBadgeText());
        }
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

    @Override
    public void unbind(@NonNull DataBindingHolder holder) {
        super.unbind(holder);
        holder.getDataBinding().unbind();
    }

}

