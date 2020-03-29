package bd.com.evaly.evalyshop.ui.home.model;

import android.graphics.Paint;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelProductGridBinding;
import bd.com.evaly.evalyshop.models.product.ProductItem;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_product_grid)
public abstract class HomeProductGridModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public ProductItem model;
    @EpoxyAttribute
    public int cashbackRate = 0;
    @EpoxyAttribute
    public boolean isShop;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener buyNowClickListener;

    public ProductItem getModel() {
        return model;
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        HomeModelProductGridBinding binding = (HomeModelProductGridBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(false);

        binding.title.setText(Html.fromHtml(model.getName()));

        Glide.with(binding.getRoot())
                .asBitmap()
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(260, 260))
                .load(model.getImageUrls().get(0))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_evaly_placeholder)
                .into(binding.image);

        if ((model.getMinPriceD() == 0) || (model.getMaxPriceD() == 0))
            binding.price.setText("Not Available");

        else if (model.getMinDiscountedPriceD() != 0) {

            if (model.getMinDiscountedPriceD() < model.getMinPriceD()) {
                binding.priceDiscount.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
                binding.priceDiscount.setVisibility(View.VISIBLE);
                binding.priceDiscount.setPaintFlags(binding.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinDiscountedPriceD()));
            } else {
                binding.priceDiscount.setVisibility(View.GONE);
                binding.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));
            }

        } else
            binding.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getMinPriceD()));


        binding.getRoot().setOnClickListener(clickListener);

        if (isShop) {
            binding.buyNow.setVisibility(View.VISIBLE);
            binding.buyNow.setOnClickListener(buyNowClickListener);
        }
        else
            binding.buyNow.setVisibility(View.GONE);

        if ((model.getMinPriceD() == 0) || (model.getMaxPriceD() == 0)) {
            binding.buyNow.setVisibility(View.GONE);
        }

        if (cashbackRate == 0)
            binding.tvCashback.setVisibility(View.GONE);
        else {
            binding.tvCashback.setVisibility(View.VISIBLE);
            binding.tvCashback.bringToFront();
            binding.tvCashback.setText(String.format(Locale.ENGLISH, "%d%% Cashback", cashbackRate));
        }
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
