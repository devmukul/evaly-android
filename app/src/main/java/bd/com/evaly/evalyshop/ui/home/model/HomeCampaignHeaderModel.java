package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelCampaignHeaderBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_campaign_header)
public abstract class HomeCampaignHeaderModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;

    @EpoxyAttribute
    public Fragment fragment;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        HomeModelCampaignHeaderBinding binding = (HomeModelCampaignHeaderBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelCampaignHeaderBinding binding = (HomeModelCampaignHeaderBinding) holder.getDataBinding();
        binding.help.setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }
}
