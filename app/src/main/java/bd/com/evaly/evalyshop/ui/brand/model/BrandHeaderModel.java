package bd.com.evaly.evalyshop.ui.brand.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BrandModelHeaderBinding;

@EpoxyModelClass(layout = R.layout.brand_model_header)
public abstract class BrandHeaderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    String brandName;
    @EpoxyAttribute
    String categoryName;
    @EpoxyAttribute
    String brandLogo;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        BrandModelHeaderBinding binding = (BrandModelHeaderBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
        binding.name.setText(brandName);
        binding.categoryName.setText(categoryName);

        Glide.with(binding.getRoot())
                .load(brandLogo)
                .into(binding.logo);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
