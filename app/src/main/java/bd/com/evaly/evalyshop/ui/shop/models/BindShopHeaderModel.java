package bd.com.evaly.evalyshop.ui.shop.models;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ShopModelHeaderBinding;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;

public class BindShopHeaderModel {

    public static void bind(ShopModelHeaderBinding binding, ShopDetailsResponse shopInfo, String description, boolean isSubscribed, int subCount, ReviewSummaryModel ratingModel) {
        if (shopInfo == null)
            return;

        binding.name.setText(shopInfo.getShopName());

        Glide.with(binding.getRoot())
                .load(shopInfo.getShopImage())
                .skipMemoryCache(true)
                .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_evaly_placeholder))
                .into(binding.logo);

        if (isSubscribed)
            binding.followText.setText("Unfollow");
        else
            binding.followText.setText("Follow");

        if (description == null) {
            binding.btn3Title.setText("Delivery");
            binding.btn3Image.setImageDrawable(AppController.getContext().getResources().getDrawable(R.drawable.ic_delivery));
        } else {
            binding.btn3Title.setText("T&C");
            binding.btn3Image.setImageDrawable(AppController.getContext().getResources().getDrawable(R.drawable.ic_terms_and_conditions));
        }

        if (shopInfo.isCodAllowed()) {
            binding.tvOffer.setVisibility(View.VISIBLE);
            binding.tvOffer.setText("COD");
        } else
            binding.tvOffer.setVisibility(View.GONE);

        if (ratingModel != null) {
            binding.ratingsCount.setText(String.format(Locale.ENGLISH, "(%d)", ratingModel.getTotalRatings()));
            binding.ratingBar.setRating((float) ratingModel.getAvgRating());
        }
    }
}
