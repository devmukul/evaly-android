package bd.com.evaly.evalyshop.ui.search.model;

import android.graphics.Paint;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelProductGridBinding;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_product_grid)
public abstract class SearchProductGridModel extends DataBindingEpoxyModel {


    @EpoxyAttribute
    String name;

    @EpoxyAttribute
    String image;

    @EpoxyAttribute
    String slug;

    @EpoxyAttribute
    double minPrice;

    @EpoxyAttribute
    double maxPrice;

    @EpoxyAttribute
    double discountedPrice;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        HomeModelProductGridBinding binding = (HomeModelProductGridBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(false);

        binding.title.setText(Html.fromHtml(name));

        BindingUtils.setImage(binding.image, image, R.drawable.ic_evaly_placeholder, R.drawable.ic_evaly_placeholder, 300, 300, false);

        binding.tvCashback.setVisibility(View.GONE);

        if ((minPrice == 0) || (maxPrice == 0)) {
            binding.tvCashback.setVisibility(View.GONE);
            binding.price.setVisibility(View.GONE);
            binding.price.setText(Utils.formatPriceSymbol(0));
        } else if (discountedPrice != 0) {
            if (discountedPrice < minPrice) {
                binding.priceDiscount.setText(Utils.formatPriceSymbol(minPrice));
                binding.priceDiscount.setVisibility(View.VISIBLE);
                binding.priceDiscount.setPaintFlags(binding.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.price.setText(Utils.formatPriceSymbol(discountedPrice));
            } else {
                binding.priceDiscount.setVisibility(View.GONE);
                binding.price.setText(Utils.formatPriceSymbol(minPrice));
            }
        } else
            binding.price.setText(Utils.formatPriceSymbol(minPrice));

        binding.getRoot().setOnClickListener(clickListener);
        binding.buyNow.setVisibility(View.GONE);
        binding.stock.setVisibility(View.GONE);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

    @Override
    public void unbind(@NonNull DataBindingHolder holder) {
        super.unbind(holder);
        holder.getDataBinding().unbind();
    }

}

