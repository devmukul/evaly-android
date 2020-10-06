package bd.com.evaly.evalyshop.ui.image.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemTouchImageBinding;

@EpoxyModelClass(layout = R.layout.item_touch_image)
public abstract class ImageSliderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String image;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemTouchImageBinding binding = (ItemTouchImageBinding) holder.getDataBinding();

        Glide.with(binding.image)
                .asBitmap()
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (binding.image.getContext() != null)
                            binding.image.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }
}