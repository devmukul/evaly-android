package bd.com.evaly.evalyshop.ui.home.model.slider;

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
import bd.com.evaly.evalyshop.databinding.HomeModelSliderItemBinding;
import bd.com.evaly.evalyshop.models.banner.BannerItem;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_slider_item)
public abstract class HomeSliderItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    BannerItem model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    public BannerItem getModel() {
        return model;
    }


    @Override
    public void bind(@NonNull DataBindingEpoxyModel.DataBindingHolder holder) {
        super.bind(holder);
        HomeModelSliderItemBinding binding = (HomeModelSliderItemBinding) holder.getDataBinding();

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(1450, 460))
                .into(binding.sliderImage);

        binding.sliderImage.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}