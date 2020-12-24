package bd.com.evaly.evalyshop.ui.checkout.model;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCheckoutProductBinding;
import bd.com.evaly.evalyshop.util.Utils;

@EpoxyModelClass(layout = R.layout.item_checkout_product)
public abstract class CheckoutProductModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    CartEntity model;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCheckoutProductBinding binding = (ItemCheckoutProductBinding) holder.getDataBinding();
        binding.price.setText(Utils.formatPriceSymbol(model.getPriceDouble() * model.getQuantity()));
        binding.priceQuan.setText(String.format(Locale.ENGLISH, "%s x %s",
                Utils.formatPriceSymbol(model.getPriceDouble()), model.getQuantity()));

        Glide.with(binding.productImage).
                load(model.getImage())
                .apply(new RequestOptions().override(300, 300))
                .into(binding.productImage);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}