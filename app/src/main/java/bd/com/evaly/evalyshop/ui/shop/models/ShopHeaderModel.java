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
import androidx.core.content.ContextCompat;
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
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ShopModelHeaderBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;

@EpoxyModelClass(layout = R.layout.shop_model_header)
public abstract class ShopHeaderModel extends EpoxyModelWithHolder<ShopHeaderModel.ShopHeaderHolder> {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    ShopDetailsResponse shopInfo;
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

            if (shopInfo == null)
                return;

            binding.name.setText(shopInfo.getShopName());

            Glide.with(binding.getRoot())
                    .load(shopInfo.getShopImage())
                    .skipMemoryCache(true)
                    .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_evaly_placeholder))
                    .into(binding.logo);

            subCount = shopInfo.getSubscriberCount();

            if (shopInfo.isSubscribed())
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
                String phone = shopInfo.getContactNumber();
                if (fragment.getView() == null)
                    return;
                final Snackbar snackBar = Snackbar.make(fragment.getView(), phone + "\n", Snackbar.LENGTH_LONG);
                snackBar.setAction("Call", v12 -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + shopInfo.getContactNumber()));
                    activity.startActivity(intent);
                    snackBar.dismiss();
                });
                snackBar.show();
            });

            binding.btn2Image.setOnClickListener(v -> {
                String address = shopInfo.getShopAddress();
                if (fragment.getView() == null)
                    return;
                final Snackbar snackBar = Snackbar.make(fragment.getView(), address + "\n", Snackbar.LENGTH_LONG);
                snackBar.setAction("Copy", v1 -> {
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("address", shopInfo.getShopAddress());
                    if (clipboard != null)
                        clipboard.setPrimaryClip(clip);

                    snackBar.dismiss();
                });
                snackBar.show();
            });


            binding.btn3Title.setText("Delivery");
            binding.btn3Image.setImageDrawable(AppController.getmContext().getResources().getDrawable(R.drawable.ic_delivery));
            binding.btn3Image.setOnClickListener(v -> {
//                DeliveryBottomSheetFragment deliveryBottomSheetFragment = DeliveryBottomSheetFragment.newInstance(shopInfo.getShopDeliveryOptions());
//                deliveryBottomSheetFragment.show(fragment.getParentFragmentManager(), "delivery option");
//                });
//            } else {
//                binding.btn3Title.setText("T&C");
//                binding.btn3Image.setImageDrawable(AppController.getmContext().getResources().getDrawable(R.drawable.ic_terms_and_conditions));
//                binding.btn3Image.setOnClickListener(v -> {
//                    String description = shop.getCampaign().getDescription();
//                    if (description == null)
//                        description = "Not available now";
//                    TextBottomSheetFragment textBottomSheetFragment = TextBottomSheetFragment.newInstance("Terms & Conditions", description);
//                    textBottomSheetFragment.show(fragment.getParentFragmentManager(), "tc");
            });


            binding.btn4Image.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ReviewsActivity.class);
                intent.putExtra("ratingJson", ratingJson);
                intent.putExtra("type", "shop");
                intent.putExtra("item_value", shopInfo.getSlug());
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
