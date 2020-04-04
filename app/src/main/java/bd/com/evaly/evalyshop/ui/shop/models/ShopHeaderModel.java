package bd.com.evaly.evalyshop.ui.shop.models;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ShopModelHeaderBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.ui.shop.delivery.DeliveryBottomSheetFragment;

@EpoxyModelClass(layout = R.layout.shop_model_header)
public abstract class ShopHeaderModel extends EpoxyModelWithHolder<ShopHeaderModel.ShopHeaderHolder> {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    ShopDetailsModel shopInfo;
    @EpoxyAttribute
    ShopViewModel viewModel;
    int subCount = 0;
    private String ratingJson = "{}";

    @Override
    public void bind(@NonNull ShopHeaderHolder holder) {
        super.bind(holder);
    }


    class ShopHeaderHolder extends EpoxyHolder {
        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            ShopModelHeaderBinding binding = ShopModelHeaderBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            Shop shop = shopInfo.getData().getShop();

            binding.name.setText(shop.getName());

            Glide.with(binding.getRoot())
                    .load(shop.getLogoImage())
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_evaly_placeholder)
                    .into(binding.logo);

            subCount = shopInfo.getData().getSubscriberCount();

            if (shopInfo.getData().isSubscribed())
                binding.followText.setText(String.format(Locale.ENGLISH, "Unfollow (%d)", subCount));
            else
                binding.followText.setText(String.format(Locale.ENGLISH, "Follow (%d)", subCount));

            binding.followBtn.setOnClickListener(v -> {

                if (CredentialManager.getToken().equals("")) {
                    Toast.makeText(activity, "You need to login first to follow a shop", Toast.LENGTH_LONG).show();
                    return;
                }
                boolean subscribe = true;

                if (binding.followText.getText().toString().contains("Unfollow")) {
                    subscribe = false;
                    binding.followText.setText(String.format("Follow (%d)", --subCount));
                } else
                    binding.followText.setText(String.format("Unfollow (%d)", ++subCount));

                viewModel.subscribe(subscribe);
            });

            binding.btn1Image.setOnClickListener(v -> {
                String phone = shop.getContactNumber();
                if (fragment.getView() == null)
                    return;
                final Snackbar snackBar = Snackbar.make(fragment.getView(), phone + "", Snackbar.LENGTH_LONG);
                snackBar.setAction("Call", v12 -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + shop.getContactNumber()));
                    activity.startActivity(intent);
                    snackBar.dismiss();
                });
                snackBar.show();
            });

            binding.btn2Image.setOnClickListener(v -> {
                String phone = shop.getAddress();
                if (fragment.getView() == null)
                    return;
                final Snackbar snackBar = Snackbar.make(fragment.getView(), phone + "", Snackbar.LENGTH_LONG);
                snackBar.setAction("Copy", v1 -> {
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("address", shop.getAddress());
                    if (clipboard != null)
                        clipboard.setPrimaryClip(clip);

                    snackBar.dismiss();
                });
                snackBar.show();
            });

            binding.btn3Image.setOnClickListener(v -> {
                DeliveryBottomSheetFragment deliveryBottomSheetFragment = DeliveryBottomSheetFragment.newInstance(shopInfo.getShopDeliveryOptions());
                deliveryBottomSheetFragment.show(fragment.getParentFragmentManager(), "delivery option");
            });

            binding.btn4Image.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ReviewsActivity.class);
                intent.putExtra("ratingJson", ratingJson);
                intent.putExtra("type", "shop");
                intent.putExtra("item_value", shop.getSlug());
                activity.startActivity(intent);
            });

            binding.llInbox.setOnClickListener(v -> {
                viewModel.setOnChatClickLiveData(true);
            });

            viewModel.getRatingSummary().observe(fragment.getViewLifecycleOwner(), response -> {
                response = response.getAsJsonObject("data");
                ratingJson = response.toString();
                double avg = response.get("avg_rating").getAsDouble();
                int totalRatings = response.get("total_ratings").getAsInt();
                binding.ratingsCount.setText(String.format(Locale.ENGLISH, "(%d)", totalRatings));
                binding.ratingBar.setRating((float) avg);
            });

            viewModel.loadRatings();
        }

    }

}
