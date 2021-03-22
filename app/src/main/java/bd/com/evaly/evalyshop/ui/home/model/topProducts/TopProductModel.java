package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import android.graphics.Paint;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCycloneProductBinding;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_cyclone_product)
public abstract class TopProductModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public ProductItem model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCycloneProductBinding binding = (ItemCycloneProductBinding) holder.getDataBinding();

        binding.title.setText(Html.fromHtml(model.getName()));
        BindingUtils.setImage(binding.image, model.getFirstImage(), R.drawable.ic_evaly_placeholder, R.drawable.ic_evaly_placeholder, 300, 300, false);

        binding.priceDiscount.setVisibility(View.GONE);
        binding.tvCashback.setVisibility(View.GONE);

//        if (cashbackRate == 0)
//            binding.tvCashback.setVisibility(View.GONE);
//        else {
//            binding.tvCashback.setVisibility(View.VISIBLE);
//            binding.tvCashback.bringToFront();
//            binding.tvCashback.setText(String.format(Locale.ENGLISH, "%d%% Cashback", cashbackRate));
//        }

        if ((model.getMinPriceD() == 0) || (model.getMaxPriceD() == 0)) {
            binding.tvCashback.setVisibility(View.GONE);
            binding.price.setVisibility(View.GONE);
            binding.price.setText(Utils.formatPriceSymbol(0));
            binding.priceDiscount.setVisibility(View.GONE);
        } else if (model.getMinDiscountedPriceD() != 0) {
            binding.price.setVisibility(View.VISIBLE);
            if (model.getMinDiscountedPriceD() < model.getMinPriceD()) {
                binding.priceDiscount.setText(Utils.formatPriceSymbol(model.getMinPriceD()));
                binding.priceDiscount.setVisibility(View.VISIBLE);
                binding.priceDiscount.setPaintFlags(binding.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.price.setText(Utils.formatPriceSymbol(model.getMinDiscountedPrice()));
            } else {
                binding.priceDiscount.setVisibility(View.GONE);
                binding.price.setText(Utils.formatPriceSymbol(model.getMinPriceD()));
            }
        } else {
            binding.price.setVisibility(View.VISIBLE);
            binding.price.setText(Utils.formatPriceSymbol(model.getMinPriceD()));
        }

        binding.getRoot().setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

