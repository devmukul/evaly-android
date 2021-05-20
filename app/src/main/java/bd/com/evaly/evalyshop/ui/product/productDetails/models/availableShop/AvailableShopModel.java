package bd.com.evaly.evalyshop.ui.product.productDetails.models.availableShop;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAvailableShopBinding;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopResponse;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_available_shop)
public abstract class AvailableShopModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    AvailableShopResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener storeClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener cartClick;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemAvailableShopBinding binding = (ItemAvailableShopBinding) holder.getDataBinding();


        binding.shopName.setText(model.getShopName());

        if (model.getShopAddress().equals("xxx"))
            binding.address.setText("Dhaka, Bangladesh");
        else
            binding.address.setText(model.getShopAddress());

        if (model.getInStock() < 1) {
            binding.stock.setText("Contact Seller");
        } else {
            binding.stock.setText("Stock Available");
        }

        if (model.getDiscountedPrice() == null ||
                model.getDiscountedPrice() < 1 ||
                model.getDiscountedPrice() >= model.getPrice()
        ) {
            binding.price.setText(Utils.formatPriceSymbol(model.getPrice()));
            binding.maximumPrice.setVisibility(View.GONE);
        } else {
            binding.maximumPrice.setVisibility(View.VISIBLE);
            binding.maximumPrice.setText(Utils.formatPriceSymbol(model.getPrice()));
            binding.price.setText(Utils.formatPriceSymbol(model.getDiscountedPrice()));
            binding.maximumPrice.setPaintFlags(binding.maximumPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (model.getPrice() == 0 && model.getDiscountedPrice() == 0) {
            binding.price.setVisibility(View.INVISIBLE);
            binding.buy.setText(R.string.not_available);
            binding.buy.getBackground().setAlpha(140);
            binding.buy.setEnabled(false);
            binding.maximumPrice.setVisibility(View.GONE);
        }

        Glide.with(binding.shopImage)
                .load(model.getShopImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(300, 200))
                .into(binding.shopImage);

        if (binding.buy.getText().toString().equals("Out of Stock"))
            binding.buy.setEnabled(false);

        binding.buy.setOnClickListener(cartClick);

        binding.shopPage.setOnClickListener(storeClick);
        binding.shopImage.setOnClickListener(storeClick);
        binding.shopName.setOnClickListener(storeClick);

        binding.call.setOnClickListener(v -> {
            final String phone = model.getContactNumber();
            final Snackbar snackBar = Snackbar.make(binding.getRoot(), phone + "", Snackbar.LENGTH_LONG);
            snackBar.setAction("Call", v12 -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    binding.getRoot().getContext().startActivity(intent);
                } catch (Exception ignored) {
                }
                snackBar.dismiss();
            });
            snackBar.show();
        });

        binding.location.setOnClickListener(v -> {
            final String location;
            if (model.getShopAddress().equals("xxx"))
                location = "Dhaka, Bangladesh";
            else
                location = model.getShopAddress();

            final Snackbar snackBar = Snackbar.make(binding.getRoot(), location + "", Snackbar.LENGTH_LONG);
            snackBar.setAction("Copy", v13 -> {
                try {
                    ClipboardManager clipboard = (ClipboardManager) binding.getRoot().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("address", location);
                    clipboard.setPrimaryClip(clip);
                } catch (Exception e) {
                }
                snackBar.dismiss();
            });
            snackBar.show();
        });

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }
}