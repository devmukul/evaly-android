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
import bd.com.evaly.evalyshop.databinding.ItemCampaignShopViewBinding;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_shop_view)
public abstract class CampaignShopModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public CampaignShopResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCampaignShopViewBinding binding = (ItemCampaignShopViewBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(false);

        binding.title.setText(Html.fromHtml(model.getName()));


        BindingUtils.setImage(binding.image, model.getImage(), R.drawable.bg_fafafa_round, R.drawable.ic_evaly_placeholder, 300, 300);


        binding.getRoot().setOnClickListener(clickListener);

        binding.campaignName.setText(model.getBadgeText());
        binding.campaignCashback.setText(model.getCashbackText().replace(".00", ""));
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

