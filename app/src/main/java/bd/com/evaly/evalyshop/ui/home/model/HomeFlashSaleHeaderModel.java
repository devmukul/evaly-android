package bd.com.evaly.evalyshop.ui.home.model;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressHeaderBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_express_header)
public abstract class HomeFlashSaleHeaderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;

    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    boolean showMore;
    @EpoxyAttribute
    boolean transparentBackground;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelExpressHeaderBinding binding = (HomeModelExpressHeaderBinding) holder.getDataBinding();

        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        if (title != null)
            binding.title.setText(title);
        if (showMore)
            binding.help.setVisibility(View.VISIBLE);
        else
            binding.help.setVisibility(View.GONE);

        if (transparentBackground)
            binding.container.setBackgroundColor(Color.TRANSPARENT);
        else
            binding.container.setBackgroundColor(Color.WHITE);

        binding.help.setOnClickListener(clickListener);

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }
}
