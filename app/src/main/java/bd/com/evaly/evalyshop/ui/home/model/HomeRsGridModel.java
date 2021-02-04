package bd.com.evaly.evalyshop.ui.home.model;


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
import bd.com.evaly.evalyshop.databinding.HomeModelCarouselGridBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_carousel_grid)
public abstract class HomeRsGridModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    String slug;

    @EpoxyAttribute
    String image;

    @EpoxyAttribute
    String type;

    @EpoxyAttribute
    String color;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelCarouselGridBinding binding = (HomeModelCarouselGridBinding) holder.getDataBinding();
        binding.title.setText(title);
        Glide.with(binding.getRoot())
                .load(image)
                .apply(new RequestOptions().override(260, 260))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_evaly_placeholder))
                .into(binding.image);
//        if (color != null)
//            binding.cardView.setCardBackgroundColor(Color.parseColor(color));
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

