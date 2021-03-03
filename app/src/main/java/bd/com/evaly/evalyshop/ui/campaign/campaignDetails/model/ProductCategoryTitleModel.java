package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignProductCategoryTitleBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_product_category_title)
public abstract class ProductCategoryTitleModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    String title2;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute
    boolean showClear;

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        ItemCampaignProductCategoryTitleBinding binding = (ItemCampaignProductCategoryTitleBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }
    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCampaignProductCategoryTitleBinding binding = (ItemCampaignProductCategoryTitleBinding) holder.getDataBinding();

        binding.title.setText(title);
        binding.clear.setText(title2);
        binding.clear.setOnClickListener(clickListener);

        if (showClear)
            binding.clear.setVisibility(View.VISIBLE);
        else
            binding.clear.setVisibility(View.GONE);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}