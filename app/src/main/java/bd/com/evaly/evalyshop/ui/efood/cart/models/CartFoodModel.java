package bd.com.evaly.evalyshop.ui.efood.cart.models;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.EfoodItemCartProductBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.efood_item_cart_product)
public abstract class CartFoodModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    Double price;
    @EpoxyAttribute
    Integer quantity = 1;
    @EpoxyAttribute
    String restaurantSlug;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener minusClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener plusClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        EfoodItemCartProductBinding binding = (EfoodItemCartProductBinding) holder.getDataBinding();

        binding.tvFoodName.setText(title);
        binding.tvFoodPrice.setText(String.format("%s", price));
        binding.tvCount.setText(String.format("%d", quantity));

        binding.ivSub.setOnClickListener(minusClick);
        binding.ivSub.setOnClickListener(plusClick);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
