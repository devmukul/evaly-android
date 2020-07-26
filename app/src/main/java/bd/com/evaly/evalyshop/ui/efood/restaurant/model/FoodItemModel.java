package bd.com.evaly.evalyshop.ui.efood.restaurant.model;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.EfoodItemRestaurantFoodBinding;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.efood_item_restaurant_food)
public abstract class FoodItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String name;
    @EpoxyAttribute
    String slug;
    @EpoxyAttribute
    String image;
    @EpoxyAttribute
    String description;
    @EpoxyAttribute
    double price;
    @EpoxyAttribute
    int quantity = 1;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        EfoodItemRestaurantFoodBinding binding = (EfoodItemRestaurantFoodBinding) holder.getDataBinding();

        binding.tvFoodName.setText(name);
        binding.tvFoodInfo.setText(description);
        binding.etFoodPrice.setText(Utils.formatPriceSymbol(price));

        Glide.with(binding.foodImage.getContext())
                .asBitmap()
                .skipMemoryCache(true)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.bg_fafafa_round)
                .error(R.drawable.bg_fafafa_round)
                .into(binding.foodImage);

        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
