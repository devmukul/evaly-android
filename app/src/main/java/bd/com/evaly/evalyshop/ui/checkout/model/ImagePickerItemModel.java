package bd.com.evaly.evalyshop.ui.checkout.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemImagePickBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_image_pick)
public abstract class ImagePickerItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String url;

    @EpoxyAttribute
    boolean isAdd;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener deleteClickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemImagePickBinding binding = (ItemImagePickBinding) holder.getDataBinding();

        if (isAdd) {
            binding.remove.setVisibility(View.GONE);
            Glide.with(binding.getRoot())
                    .asBitmap()
                    .load(R.drawable.ic_upload_image_large)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.image);
            binding.image.setPadding(50, 50, 50, 50);
        } else {
            Glide.with(binding.getRoot())
                    .asBitmap()
                    .load(url.replace("'", ""))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.image);

            binding.remove.setVisibility(View.VISIBLE);
            binding.image.setPadding(0, 0, 0, 0);
        }

        binding.image.setOnClickListener(clickListener);
        binding.remove.setOnClickListener(deleteClickListener);

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}