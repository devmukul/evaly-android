package bd.com.evaly.evalyshop.ui.home.model;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelFlashsaleHeaderBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_flashsale_header)
public abstract class HomeExpressHeaderModel extends DataBindingEpoxyModel {

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

       HomeModelFlashsaleHeaderBinding binding = (HomeModelFlashsaleHeaderBinding) holder.getDataBinding();
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
        else {
            if (binding.container.getContext() != null)
                binding.container.setBackgroundColor(binding.container.getContext().getResources().getColor(R.color.white));
        }
        binding.help.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}
