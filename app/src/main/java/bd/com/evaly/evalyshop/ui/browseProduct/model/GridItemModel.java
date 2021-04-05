package bd.com.evaly.evalyshop.ui.browseProduct.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemShopBrandBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_shop_brand)
public abstract class GridItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    String image;
    @EpoxyAttribute
    String slug;
    @EpoxyAttribute
    String type;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemShopBrandBinding binding = (ItemShopBrandBinding) holder.getDataBinding();
        binding.text.setText(title);
        Glide.with(binding.getRoot())
                .asBitmap()
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(1450, 460))
                .into(binding.image);
        binding.getRoot().setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}