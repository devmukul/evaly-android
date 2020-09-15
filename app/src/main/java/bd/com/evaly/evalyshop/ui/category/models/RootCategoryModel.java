package bd.com.evaly.evalyshop.ui.category.models;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.databinding.ItemRootCategoryBinding;
import bd.com.evaly.evalyshop.util.Utils;

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
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap bitmap = Utils.changeColor(((BitmapDrawable) resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                        binding.image.setImageBitmap(bitmap);
                        return true;
                    }
                })
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(160, 160))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_evaly_placeholder)
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

