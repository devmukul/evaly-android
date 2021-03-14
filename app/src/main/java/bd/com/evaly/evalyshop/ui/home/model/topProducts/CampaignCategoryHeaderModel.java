package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignHeaderNewBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_header_new)
public abstract class CampaignCategoryHeaderModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    public boolean isTop = true;

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
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        ItemCampaignHeaderNewBinding binding = (ItemCampaignHeaderNewBinding) baseBinding;
        if (isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCampaignHeaderNewBinding binding = (ItemCampaignHeaderNewBinding) holder.getDataBinding();

        binding.headerText.setText(headerText);
        binding.subText.setText(subText.replace(".00", ""));
        binding.holder.setBackground(binding.holder.getResources().getDrawable(isTop ? R.drawable.bg_top_product_header_top : R.drawable.bg_top_product_header));

        ViewCompat.setBackgroundTintList(
                binding.indicatorColored,
                ColorStateList.valueOf(Color.parseColor(primaryColor)));

        binding.holder.setOnClickListener(clickListener);

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}

