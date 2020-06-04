package bd.com.evaly.evalyshop.ui.shop.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ShopModelTitleProductBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.shop_model_title_product)
public abstract class ShopProductTitleModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ShopModelTitleProductBinding binding = (ShopModelTitleProductBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        if (title == null) {
            binding.categoryTitle.setText(R.string.all_products);
            binding.resetBtn.setVisibility(View.GONE);
        } else {
            binding.categoryTitle.setText(title);
            binding.resetBtn.setVisibility(View.VISIBLE);
        }

        binding.resetBtn.setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}