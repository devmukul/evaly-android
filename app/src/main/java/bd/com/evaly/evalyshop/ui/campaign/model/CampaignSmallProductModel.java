package bd.com.evaly.evalyshop.ui.campaign.model;

import android.graphics.Paint;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCampaignSmallProductBinding;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_campaign_small_product)
public abstract class CampaignSmallProductModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public CampaignProductResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener buyNowClickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCampaignSmallProductBinding binding = (ItemCampaignSmallProductBinding) holder.getDataBinding();
        if (binding.getRoot().getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(false);
        }

        binding.title.setText(Html.fromHtml(model.getName()));
        BindingUtils.setImage(binding.image, model.getImage(), R.drawable.ic_evaly_placeholder, R.drawable.ic_evaly_placeholder, 300, 300, false);

        binding.priceDiscount.setVisibility(View.GONE);
        if (model.getPrice() == 0) {
            binding.price.setVisibility(View.GONE);
        } else if (model.getDiscountedPrice() != 0) {
            binding.price.setVisibility(View.VISIBLE);
            if (model.getDiscountedPrice() < model.getPrice()) {
                binding.priceDiscount.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getPrice()));
                binding.priceDiscount.setVisibility(View.VISIBLE);
                binding.priceDiscount.setPaintFlags(binding.priceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getDiscountedPrice()));
            } else {
                binding.priceDiscount.setVisibility(View.GONE);
                binding.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getPrice()));
            }
        } else {
            binding.price.setVisibility(View.VISIBLE);
            binding.price.setText(String.format(Locale.ENGLISH, "৳ %d", (int) model.getPrice()));
        }

        binding.getRoot().setOnClickListener(clickListener);
        if (model.getCashbackText() == null || model.getCashbackText().length() == 0)
            binding.tvCashback.setVisibility(View.GONE);
        else {
            binding.tvCashback.setText(Utils.toFirstCharUpperAll(model.getCashbackText().replace(".00", "")));
            binding.tvCashback.setVisibility(View.VISIBLE);
        }
        // binding.bottomText.setText(model.getBottomText());
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

