package bd.com.evaly.evalyshop.ui.campaign.model;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignHeaderBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_header)
public abstract class CampaignHeaderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public boolean isStaggered = true;

    @NonNull
    @EpoxyAttribute
    String headerText;

    @NonNull
    @EpoxyAttribute
    String primaryColor;

    @NonNull
    @EpoxyAttribute
    String subText;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCampaignHeaderBinding binding = (ItemCampaignHeaderBinding) holder.getDataBinding();

        if (isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);
        }

        binding.headerText.setText(headerText);
        binding.subText.setText(subText.replace(".00", ""));

        ViewCompat.setBackgroundTintList(
                binding.holder,
                ColorStateList.valueOf(Color.parseColor(primaryColor)));

        binding.getRoot().setOnClickListener(clickListener);

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

