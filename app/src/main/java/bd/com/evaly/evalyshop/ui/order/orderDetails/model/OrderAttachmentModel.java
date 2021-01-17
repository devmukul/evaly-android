package bd.com.evaly.evalyshop.ui.order.orderDetails.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemOrderAttachmentBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_order_attachment)
public abstract class OrderAttachmentModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String url;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemOrderAttachmentBinding binding = (ItemOrderAttachmentBinding) holder.getDataBinding();

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(url.replace("'", ""))
                .placeholder(R.drawable.ic_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.image);
        binding.image.setPadding(0, 0, 0, 0);

        binding.image.setOnClickListener(clickListener);

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}