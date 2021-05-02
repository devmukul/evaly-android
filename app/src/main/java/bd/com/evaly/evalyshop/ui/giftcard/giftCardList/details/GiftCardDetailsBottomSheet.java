package bd.com.evaly.evalyshop.ui.giftcard.giftCardList.details;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.BottomSheetGiftCardsBinding;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardDetailsBottomSheet extends BaseBottomSheetFragment<BottomSheetGiftCardsBinding, GiftCardDetailsViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;
    private ViewDialog dialog;
    private int giftCardAmount = 0;

    public GiftCardDetailsBottomSheet() {
        super(GiftCardDetailsViewModel.class, R.layout.bottom_sheet_gift_cards);
    }

    @Override
    protected void initViews() {
        dialog = new ViewDialog(getActivity());
        binding.privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        binding.privacyText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    protected void liveEventsObservers() {
        viewModel.giftCardDetailsLiveData.observe(getViewLifecycleOwner(), response -> {
            dialog.hideDialog();
            if (response.getSuccess()) {
                GiftCardListItem item = response.getData();

                binding.name.setText(item.getName());
                binding.details.setText(item.getDescription());
                giftCardAmount = item.getPrice();
                binding.amount.setText(String.format("৳ %s", Utils.formatPrice(giftCardAmount)));
                binding.total.setText(String.format("৳ %s", Utils.formatPrice(giftCardAmount)));
                binding.cardValue.setText(String.format("৳ %d", item.getValue()));

                if (getContext() != null) {
                    if (item.getImageUrl() == null)
                        Glide.with(getContext())
                                .load("https://beta.evaly.com.bd/static/images/gift-card.jpg")
                                .placeholder(R.drawable.ic_placeholder_small)
                                .into(binding.image);
                    else
                        Glide.with(getContext())
                                .load(item.getImageUrl())
                                .placeholder(R.drawable.ic_placeholder_small)
                                .into(binding.image);
                }

            } else {
                ToastUtils.show("Sorry the gift card is not available");
                dismissAllowingStateLoss();
            }
        });

        viewModel.onResponse.observe(getViewLifecycleOwner(), aBoolean -> {
            dialog.hideDialog();
            if (aBoolean) {
                NavHostFragment.findNavController(this).popBackStack();
                Bundle bundle = new Bundle();
                bundle.putString("type", "purchased");
                NavHostFragment.findNavController(this).navigate(R.id.giftCardFragment, bundle);
            }
        });
    }

    @Override
    protected void clickListeners() {
        binding.plus.setOnClickListener(v -> {
            int quan;
            try {
                quan = Integer.parseInt(binding.quantity.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * giftCardAmount));
            quan += 1;
            binding.quantity.setText(String.format(Locale.ENGLISH, "%d", quan));
        });

        binding.minus.setOnClickListener(v -> {
            int quan;
            try {
                quan = Integer.parseInt(binding.quantity.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            quan -= 1;
            if (quan < 1) {
                quan = 1;
            }
            binding.total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * giftCardAmount));
            binding.quantity.setText(String.format(Locale.ENGLISH, "%d", quan));
        });

        binding.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int quan = Integer.parseInt(s.toString());
                    binding.total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * giftCardAmount));
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.placeOrder.setOnClickListener(v -> {

            if (binding.phone.getText().toString().equals(preferenceRepository.getUserName())) {
                Toast.makeText(getContext(), "You can't buy gift cards for yourself", Toast.LENGTH_LONG).show();
                return;
            }

            if (binding.phone.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_LONG).show();
                return;
            }

            if (!Utils.isValidNumber(binding.phone.getText().toString())) {
                Toast.makeText(getContext(), "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isNumeric(binding.quantity.getText().toString())) {
                if (Integer.parseInt(binding.quantity.getText().toString()) > 10) {
                    Toast.makeText(getContext(), "Quantity must be less than 10", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(getContext(), "Enter valid quantity", Toast.LENGTH_LONG).show();
                return;
            }

            if (!binding.checkBox.isChecked()) {
                Toast.makeText(getContext(), "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }

            viewModel.createOrder(binding.phone.getText().toString(), binding.quantity.getText().toString());
        });
    }
}
