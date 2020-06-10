package bd.com.evaly.evalyshop.ui.efood.home.model;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.EfoodItemRestaurantHorizontalBinding;
import bd.com.evaly.evalyshop.databinding.EfoodItemRestaurantVerticalBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.efood_item_restaurant_vertical)
public abstract class RestaurantVerticalModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    String slug;
    @EpoxyAttribute
    String coverUrl;
    @EpoxyAttribute
    String type;
    @EpoxyAttribute
    Double rating;
    @EpoxyAttribute
    Integer favCount;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        EfoodItemRestaurantVerticalBinding binding = (EfoodItemRestaurantVerticalBinding) holder.getDataBinding();

        Glide.with(binding.ivRestaurantBanner.getContext())
                .asBitmap()
                .skipMemoryCache(true)
                .load(coverUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.bg_fafafa_round)
                .error(R.drawable.bg_fafafa_round)
                .into(binding.ivRestaurantBanner);

        binding.tvRestaurantName.setText(title);

        if (type == null)
            binding.tvRestaurantType.setText("Fastfood");
        else
            binding.tvRestaurantType.setText(type);

        binding.getRoot().setOnClickListener(clickListener);
        binding.tvRatingValue.setText(String.format("%s", rating));

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
