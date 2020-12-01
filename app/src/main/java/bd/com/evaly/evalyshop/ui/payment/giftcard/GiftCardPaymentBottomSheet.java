package bd.com.evaly.evalyshop.ui.payment.giftcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetPayViaGiftcardBinding;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardPaymentBottomSheet extends BottomSheetDialogFragment {

    @Inject
    SharedObservers sharedObservers;
    private BottomSheetPayViaGiftcardBinding binding;
    private GiftCardPaymentViewModel viewModel;
    private String invoice;
    private double amount;
    private ViewDialog dialog;

    public static GiftCardPaymentBottomSheet newInstance(String invoice, double amount) {
        Bundle args = new Bundle();
        args.putString("invoice", invoice);
        args.putDouble("amount", amount);
        GiftCardPaymentBottomSheet fragment = new GiftCardPaymentBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetPayViaGiftcardBinding.inflate(inflater);
        viewModel = new ViewModelProvider(this).get(GiftCardPaymentViewModel.class);
        invoice = requireArguments().getString("invoice");
        amount = requireArguments().getDouble("amount");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ViewDialog(getActivity());
        binding.amount.setText(Utils.formatPrice(amount));
        clickListeners();
        liveEvents();
    }

    private void liveEvents() {
        viewModel.onPaymentSuccess.observe(getViewLifecycleOwner(), s -> {
            ToastUtils.show(s.getMessage());
            dialog.hideDialog();
            if (s.getSuccess())
                giftCardSuccessDialog();
        });

        viewModel.onPaymentFailed.observe(getViewLifecycleOwner(), s -> {
            ToastUtils.show(s);
            dialog.hideDialog();
        });
    }

    private void clickListeners() {
        binding.closeBtn.setOnClickListener(v -> {
            if (binding.code.getText().toString().trim().isEmpty())
                dismissAllowingStateLoss();
            else {
                new AlertDialog.Builder(getActivity())
                        .setCancelable(false)
                        .setMessage("Are you sure you want to discard gift card payment?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            dismissAllowingStateLoss();
                        })
                        .show();
            }
        });
        binding.submit.setOnClickListener(v -> {
            if (binding.amount.getText().toString().equals("")) {
                ToastUtils.show("Please enter an amount.");
                return;
            } else if (binding.code.getText().toString().equals("")) {
                ToastUtils.show("Please enter gift card coupon code.");
                return;
            }
            double partial_amount = Double.parseDouble(binding.amount.getText().toString().trim());
            if (partial_amount > amount) {
                ToastUtils.show("You have entered an amount that is larger than your due amount.");
                return;
            }
            dialog.showDialog();
            viewModel.makePaymentViaGiftCard(binding.code.getText().toString().trim(), invoice, String.valueOf((int) partial_amount));
        });
    }

    private void giftCardSuccessDialog() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage("Thank you for your payment. We are updating your order and you will be notified soon. If your order is not updated, please contact us.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    sharedObservers.giftCardPaymentSuccess.call();
                    dismissAllowingStateLoss();
                })
                .show();
    }
}
