package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignHeaderNewSkeletonBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

@EpoxyModelClass(layout = R.layout.item_campaign_header_new_skeleton)
public abstract class CampaignCategoryHeaderSkeletonModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    public boolean isStaggered = true;

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        ItemCampaignHeaderNewSkeletonBinding binding = (ItemCampaignHeaderNewSkeletonBinding) baseBinding;
        if (isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}

