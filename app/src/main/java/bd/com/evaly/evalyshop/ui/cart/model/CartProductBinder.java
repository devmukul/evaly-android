package bd.com.evaly.evalyshop.ui.cart.model;

import android.annotation.SuppressLint;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.ItemCartProductBinding;
import bd.com.evaly.evalyshop.util.Utils;

public class CartProductBinder {

    @SuppressLint("DefaultLocale")
    public static void bind(ItemCartProductBinding binding, CartEntity model) {
        Logger.d(new Gson().toJson(model));

        binding.productName.setText(model.getName());
        Glide.with(binding.getRoot())
                .load(model.getImage())
                .placeholder(R.drawable.ic_placeholder_small)
                .apply(new RequestOptions().override(250, 250))
                .into(binding.productImage);

        if (model.getVariantDetails() == null) {
            binding.variant.setVisibility(View.GONE);
        } else {
            binding.variant.setVisibility(View.VISIBLE);
            binding.variant.setText(model.getVariantDetails());
        }

        binding.checkBox.setChecked(model.isSelected(), binding.checkBox.isChecked() != model.isSelected());
        binding.quantity.setText(String.format("%d", model.getQuantity()));
        binding.priceTotal.setText(Utils.formatPriceSymbol(model.getPriceInt() * model.getQuantity()));
        binding.price.setText(String.format("%s x %d", Utils.formatPriceSymbol(model.getPriceInt()), model.getQuantity()));
    }

}
