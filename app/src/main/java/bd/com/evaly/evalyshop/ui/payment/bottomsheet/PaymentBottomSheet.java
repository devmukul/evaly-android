package bd.com.evaly.evalyshop.ui.payment.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetPaymentBinding;
import bd.com.evaly.evalyshop.models.payment.PaymentMethodModel;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.controller.PaymentMethodController;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class PaymentBottomSheet extends BottomSheetDialogFragment implements PaymentBottomSheetNavigator {

    private static PaymentOptionListener paymentOptionRedirceListener;
    private PaymentBottomSheetViewModel viewModel;
    private BottomSheetPaymentBinding binding;
    private String invoice_no, enteredAmount;
    private double total_amount = 0, paid_amount = 0.0;
    private AppCompatActivity activityInstance;
    private PaymentMethodController controller;
    private ViewDialog dialog;
    private boolean isFood = false;
    private String[] paymentMethods;
    private String balanceText, deliveryFee;
    private boolean applyDeliveryFee;
    private String disabledPaymentMethods = "", disabledPaymentMethodText = "", nagadBadgeText = "", nagadDescription = "";


    public static PaymentBottomSheet newInstance(String invoiceNo,
                                                 double totalAmount,
                                                 double paidAmount,
                                                 boolean isFood,
                                                 String[] paymentMethods,
                                                 String balanceText,
                                                 String disabledPaymentMethods,
                                                 String disabledPaymentMethodText,
                                                 String nagadBadgeText,
                                                 String nagadDescription,
                                                 PaymentOptionListener paymentOptionListener,
                                                 boolean applyDeliveryFee,
                                                 String deliveryFee) {
        PaymentBottomSheet instance = new PaymentBottomSheet();
        paymentOptionRedirceListener = paymentOptionListener;
        Bundle bundle = new Bundle();
        bundle.putString("invoice_no", invoiceNo);
        bundle.putDouble("total_amount", totalAmount);
        bundle.putDouble("paid_amount", paidAmount);
        bundle.putBoolean("is_food", isFood);
        Logger.d(paymentMethods);
        bundle.putStringArray("payment_methods", paymentMethods);
        bundle.putString("balance_text", balanceText);
        bundle.putString("disabled_payment_methods", disabledPaymentMethods);
        bundle.putBoolean("apply_delivery_fee", applyDeliveryFee);
        bundle.putString("delivery_fee", deliveryFee);
        bundle.putString("disabled_payment_method_text", disabledPaymentMethodText);
        bundle.putString("nagadDescription", nagadDescription);
        bundle.putString("nagadBadgeText", nagadBadgeText);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_payment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PaymentBottomSheetViewModel.class);
        viewModel.setNavigator(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);

        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            total_amount = getArguments().getDouble("total_amount");
            paid_amount = getArguments().getDouble("paid_amount");
            if (getArguments().containsKey("is_food"))
                isFood = getArguments().getBoolean("is_food");
            if (getArguments().getStringArray("payment_methods") != null) {
                paymentMethods = getArguments().getStringArray("payment_methods");
            }
            if (getArguments().getString("balance_text") != null) {
                balanceText = getArguments().getString("balance_text");
            }
            disabledPaymentMethods = getArguments().getString("disabled_payment_methods");
            disabledPaymentMethodText = getArguments().getString("disabled_payment_method_text");
            nagadDescription = getArguments().getString("nagadDescription");
            nagadBadgeText = getArguments().getString("nagadBadgeText");
            applyDeliveryFee = getArguments().getBoolean("apply_delivery_fee");
            deliveryFee = getArguments().getString("delivery_fee");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ViewDialog(getActivity());
        activityInstance = (AppCompatActivity) getActivity();
        controller = new PaymentMethodController();
        controller.setActivity(activityInstance);
        controller.setFocusListener(object -> {
            binding.amountPay.clearFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(binding.amountPay.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        });

        binding.amountPay.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.amountPay.clearFocus();
            }
            return false;
        });

        binding.recyclerView.setAdapter(controller.getAdapter());
        setPaymentMethodViewData();
        setAmountText();

        binding.confirm.setOnClickListener(view1 -> {
            onPaymentConfirmOnClick();
        });

        if (applyDeliveryFee) {
            binding.llCashCollect.setVisibility(View.VISIBLE);
            binding.tvDeliveryFee.setText(Html.fromHtml("Please Pay Delivery Fee <b>৳" + deliveryFee + "</b> Cash to Delivery Hero."));

        } else {
            binding.llCashCollect.setVisibility(View.GONE);
        }

    }


    private void onPaymentConfirmOnClick() {
        double amountToPay = total_amount - paid_amount;
        enteredAmount = binding.amountPay.getText().toString().trim();
        double enteredAmountDouble = 0;

        try {
            enteredAmountDouble = Double.parseDouble(enteredAmount);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please enter valid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredAmount.equals("")) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        } else if (enteredAmountDouble > amountToPay) {
            Toast.makeText(getContext(), "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
            return;
        } else if (binding.amountPay.getText().toString().equals("0")) {
            Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
            return;
        }

        PaymentMethodModel method = controller.getSelectedMethod();

        if (method == null) {
            ToastUtils.show("Please select a payment method");
            return;
        }
        if (method.getName().equals(Constants.EVALY_ACCOUNT)) {
            dialog.showDialog();
            viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());
        } else if (method.getName().equalsIgnoreCase(Constants.CASH_ON_DELIVERY)) {
            if (paymentOptionRedirceListener != null && invoice_no != null && !enteredAmount.equals(""))
                viewModel.makeCashOnDelivery(invoice_no);
            dismissAllowingStateLoss();
        } else if (method.getName().equalsIgnoreCase(Constants.BKASH)) {
            Toast.makeText(getContext(), "Opening bKash payment gateway!", Toast.LENGTH_SHORT).show();
            if (paymentOptionRedirceListener != null && invoice_no != null && !enteredAmount.equals(""))
                paymentOptionRedirceListener.onPaymentRedirect(BuildConfig.BKASH_URL, enteredAmount, invoice_no);
            dismissAllowingStateLoss();
        } else if (method.getName().equalsIgnoreCase(Constants.BALANCE_WITH_CASH)) {
            if (paymentOptionRedirceListener != null && invoice_no != null && !enteredAmount.equals(""))
                viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());
            dismissAllowingStateLoss();
        } else if (method.getName().equalsIgnoreCase(Constants.CARD)) {
            Toast.makeText(getContext(), "Opening to payment gateway!", Toast.LENGTH_SHORT).show();
            viewModel.payViaCard(invoice_no, enteredAmount);
        } else if (method.getName().equalsIgnoreCase(Constants.NAGAD)) {
            Toast.makeText(getContext(), "Opening to Nagad gateway!", Toast.LENGTH_SHORT).show();
            viewModel.payViaNagad(invoice_no, enteredAmount);
        } else {
            ToastUtils.show("Payment is not possible, please try again");
        }
    }

    private void setAmountText() {
        double amount = (total_amount - paid_amount);
        binding.amountPay.setText(String.format("%s", (int) (amount)));
    }

    private void setPaymentMethodViewData() {
        List<PaymentMethodModel> methodList = new ArrayList<>();

        if (isFood)
            balanceText = "For express food shops, you can pay up to 100% of the order amount from Evaly Account.";

        if (paymentMethods == null)
            return;

        for (int i = 0; i < paymentMethods.length; i++) {

            boolean isEnabled = !disabledPaymentMethods.contains(paymentMethods[i]);

            if (paymentMethods[i].equalsIgnoreCase("balance")) {
                methodList.add(new PaymentMethodModel(
                        "Evaly Account",
                        balanceText,
                        disabledPaymentMethodText,
                        R.drawable.payment_icon_evaly,
                        false,
                        isEnabled));
            } else if (paymentMethods[i].equalsIgnoreCase("cod+balance")) {
                methodList.add(new PaymentMethodModel(
                        Constants.BALANCE_WITH_CASH,
                        balanceText,
                        disabledPaymentMethodText,
                        R.drawable.payment_icon_evaly,
                        false,
                        isEnabled));
            } else if (paymentMethods[i].equalsIgnoreCase("cod")) {
                methodList.add(new PaymentMethodModel(
                        Constants.CASH_ON_DELIVERY,
                        "Payment on delivery.",
                        disabledPaymentMethodText,
                        R.drawable.ic_cash,
                        false,
                        isEnabled));
            } else if (paymentMethods[i].equalsIgnoreCase("card")) {
                methodList.add(new PaymentMethodModel(
                        Constants.CARD,
                        "Pay from your debit/visa/master card using \nSSL payment gateway.",
                        disabledPaymentMethodText,
                        R.drawable.payment_cards,
                        false,
                        isEnabled));
            } else if (paymentMethods[i].equalsIgnoreCase("bkash")) {
                methodList.add(new PaymentMethodModel(
                        Constants.BKASH,
                        "Pay from your bKash account using \nbKash payment gateway.",
                        disabledPaymentMethodText,
                        R.drawable.payment_bkash_square,
                        false,
                        isEnabled));
            } else if (paymentMethods[i].equalsIgnoreCase("nagad")) {
                methodList.add(new PaymentMethodModel(
                        Constants.NAGAD,
                        nagadDescription.equals("") ? "Pay from your Nagad account using \nNagad payment gateway." : nagadDescription,
                        disabledPaymentMethodText,
                        R.drawable.ic_nagad2,
                        false,
                        isEnabled));
            }
        }

        controller.loadData(methodList, true);
    }

    @Override
    public void onPaymentSuccess(String message) {
        if (getActivity() != null && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
            dialog.hideDialog();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            if (isVisible()) {
                if (activityInstance != null && activityInstance instanceof OrderDetailsActivity && !activityInstance.isDestroyed() && !activityInstance.isFinishing())
                    ((OrderDetailsActivity) getActivity()).updatePage();
                else {
                    NavHostFragment.findNavController(this).popBackStack(R.id.paymentFragment, true);
                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                    intent.putExtra("orderID", invoice_no);
                    startActivity(intent);
                }
                dismissAllowingStateLoss();
            }
        }
    }

    @Override
    public void onPaymentFailed(String message) {
        dialog.hideDialog();
        ToastUtils.show(message);
    }

    @Override
    public void payViaCard(String url) {

        if (getContext() != null) {
            if (paymentOptionRedirceListener == null || url == null || invoice_no == null || invoice_no.equals("") || url.equals(""))
                Toast.makeText(getContext(), "Unable to make payment, try again later!", Toast.LENGTH_SHORT).show();
            else {
                if (getActivity() != null) {
                    paymentOptionRedirceListener.onPaymentRedirect(url, enteredAmount, invoice_no);
                }
                if (isVisible() && !isRemoving() && !isDetached())
                    dismissAllowingStateLoss();
            }
        }
    }

    public interface PaymentOptionListener {
        void onPaymentRedirect(String url, String amount, String invoiceNo);
    }


}
