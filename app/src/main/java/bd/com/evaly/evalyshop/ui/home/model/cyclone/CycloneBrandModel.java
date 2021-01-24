package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCycloneGridBinding;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.util.BindingUtils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_cyclone_grid)
public abstract class CycloneBrandModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public CampaignBrandResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCycloneGridBinding binding = (ItemCycloneGridBinding) holder.getDataBinding();

        binding.title.setText(Html.fromHtml(model.getName()));
        BindingUtils.setImage(binding.image, model.getImage(), R.drawable.ic_evaly_placeholder, R.drawable.ic_evaly_placeholder, 300, 300, true);
        binding.getRoot().setOnClickListener(clickListener);

        binding.tvCashback.setText(model.getBadgeText2().replace(".00", ""));

        if (model.getBadgeText2().equals(""))
            binding.tvCashback.setVisibility(View.GONE);
        else
            binding.tvCashback.setVisibility(View.VISIBLE);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

