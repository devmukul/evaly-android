package bd.com.evaly.evalyshop.ui.category.models;

import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.databinding.ItemRootCategoryBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_root_category)
public abstract class RootCategoryModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    String image;

    @EpoxyAttribute
    boolean isSelected;

    @EpoxyAttribute
    public CategoryEntity model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemRootCategoryBinding binding = (ItemRootCategoryBinding) holder.getDataBinding();

        binding.text.setText(Html.fromHtml(title));

        Glide.with(binding.getRoot())
                .load(image)
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(260, 260))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_evaly_placeholder))
                .into(binding.image);

        binding.categoryItem.setOnClickListener(clickListener);

        binding.selectedBrd.bringToFront();

        if (isSelected)
            binding.selectedBrd.setVisibility(View.VISIBLE);
        else
            binding.selectedBrd.setVisibility(View.GONE);
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

