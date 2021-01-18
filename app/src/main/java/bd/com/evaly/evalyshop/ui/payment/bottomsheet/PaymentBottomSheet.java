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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetPaymentBinding;
import bd.com.evaly.evalyshop.models.payment.PaymentMethodModel;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.controller.PaymentMethodController;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentBottomSheet extends BottomSheetDialogFragment implements PaymentBottomSheetNavigator {

    private static PaymentOptionListener paymentOptionRedirectListener;
    @Inject
    FirebaseRemoteConfig remoteConfig;
    private PaymentBottomSheetViewModel viewModel;
    private BottomSheetPaymentBinding binding;
    private String invoice_no, enteredAmount;
    private double total_amount = 0, paid_amount = 0.0;
    private AppCompatActivity activityInstance;
    private PaymentMethodController controller;
    private ViewDialog dialog;
    private ArrayList<String> paymentMethods;
    private String balanceText, deliveryFee;
    private boolean applyDeliveryFee;
    private String disabledPaymentMethods = "", disabledPaymentMethodText = "", nagadBadgeText = "", nagadDescription = "";

    public static PaymentBottomSheet newInstance(String invoiceNo,
                                                 double totalAmount,
                                                 double paidAmount,
                                                 ArrayList<String> paymentMethods,
                                                 boolean applyDeliveryFee,
                                                 String deliveryFee,
                                                 PaymentOptionListener paymentOptionListener) {
        PaymentBottomSheet instance = new PaymentBottomSheet();
        paymentOptionRedirectListener = paymentOptionListener;
        Bundle bundle = new Bundle();
        bundle.putString("invoice_no", invoiceNo);
        bundle.putDouble("total_amount", totalAmount);
        bundle.putDouble("paid_amount", paidAmount);
        bundle.putStringArrayList("payment_methods", paymentMethods);
        bundle.putBoolean("apply_delivery_fee", applyDeliveryFee);
        bundle.putString("delivery_fee", deliveryFee);
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
            paymentMethods = getArguments().getStringArrayList("payment_methods");
            applyDeliveryFee = getArguments().getBoolean("apply_delivery_fee");
            deliveryFee = getArguments().getString("delivery_fee");

            balanceText = remoteConfig.getString("evaly_pay_text");
            disabledPaymentMethods = remoteConfig.getString("disabled_payment_methods");
            disabledPaymentMethodText = remoteConfig.getString("disabled_payment_text");
            nagadBadgeText = remoteConfig.getString("nagad_badge_text");
            nagadDescription = remoteConfig.getString("nagad_description");

            prioritySorting();
        }
    }

    private void prioritySorting() {
        ArrayList<String> priorityList = new ArrayList<>();
        priorityList.add("bkash");
        priorityList.add("nagad");
        priorityList.add("sebl_gateway");
        priorityList.add("citybank_gateway");
        priorityList.add("balance");
        priorityList.add("cod+balance");
        priorityList.add("cod");
        priorityList.add("sslcommerz_gateway");

        ArrayList<String> newList = intersection(priorityList, paymentMethods);
        for (String s : paymentMethods)
            if (!newList.contains(s))
                newList.add(s);
        paymentMethods = newList;
    }

    private ArrayList<String> intersection(ArrayList<String> list, ArrayList<String> list2) {
        ArrayList<String> result = new ArrayList<>(list);
        result.retainAll(list2);
        return result;
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

        String paymentMethod = method.getName();

        switch (paymentMethod) {
            case Constants.EVALY_ACCOUNT:
                dialog.showDialog();
                viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());
                break;
            case Constants.CASH_ON_DELIVERY:
                if (paymentOptionRedirectListener != null && invoice_no != null && !enteredAmount.equals(""))
                    viewModel.makeCashOnDelivery(invoice_no);
                dismissAllowingStateLoss();
                break;
            case Constants.BKASH:
                Toast.makeText(getContext(), "Opening bKash payment gateway!", Toast.LENGTH_SHORT).show();
                if (paymentOptionRedirectListener != null && invoice_no != null && !enteredAmount.equals(""))
                    paymentOptionRedirectListener.onPaymentRedirect(BuildConfig.BKASH_URL, enteredAmount, invoice_no);
                dismissAllowingStateLoss();
                break;
            case Constants.BALANCE_WITH_CASH:
                if (paymentOptionRedirectListener != null && invoice_no != null && !enteredAmount.equals(""))
                    viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());
                dismissAllowingStateLoss();
                break;
            case Constants.CARD:
                Toast.makeText(getContext(), "Opening to payment gateway!", Toast.LENGTH_SHORT).show();
                viewModel.payViaSEBL(invoice_no, enteredAmount);
                break;
            case Constants.CITYBANK:
                Toast.makeText(getContext(), "Opening to City Bank payment gateway!", Toast.LENGTH_SHORT).show();
                viewModel.payViaCityBank(invoice_no, enteredAmount);
                break;
            case Constants.OTHERS:
                Toast.makeText(getContext(), "Opening to payment gateway!", Toast.LENGTH_SHORT).show();
                viewModel.payViaCard(invoice_no, enteredAmount);
                break;
            case Constants.NAGAD:
                Toast.makeText(getContext(), "Opening to Nagad gateway!", Toast.LENGTH_SHORT).show();
                viewModel.payViaNagad(invoice_no, enteredAmount);
                break;
            default:
                ToastUtils.show("Payment is not possible, please try again");
                break;
        }
    }

    private void setAmountText() {
        double amount = (total_amount - paid_amount);
        binding.amountPay.setText(String.format("%s", (int) (amount)));
    }

    private void setPaymentMethodViewData() {
        List<PaymentMethodModel> methodList = new ArrayList<>();

        if (paymentMethods == null)
            return;

        for (int i = 0; i < paymentMethods.size(); i++) {

            String paymentMethod = paymentMethods.get(i);

            boolean isEnabled = !disabledPaymentMethods.contains(paymentMethod);
            String name = null, description = null, redText = null;
            int image = 0;

            if (paymentMethod.equalsIgnoreCase("bkash")) {
                name = Constants.BKASH;
                description = "Pay from your bKash account using \nbKash payment gateway.";
                image = R.drawable.payment_bkash_square;
            } else if (paymentMethod.equalsIgnoreCase("nagad")) {
                name = Constants.NAGAD;
                description = nagadDescription.equals("") ? "Pay from your Nagad account using \nNagad payment gateway." : nagadDescription;
                image = R.drawable.ic_nagad2;
                redText = nagadBadgeText;
            } else if (paymentMethod.equalsIgnoreCase("sebl_gateway")) {
                name = Constants.CARD;
                description = "Pay from your debit/visa/mastercard using \nSEBL payment gateway.";
                image = R.drawable.payment_cards;
            } else if (paymentMethod.equalsIgnoreCase("citybank_gateway")) {
                name = Constants.CITYBANK;
                description = "Pay from your Amex card using City bank payment gateway.";
                image = R.drawable.city_amex;
            } else if (paymentMethod.equalsIgnoreCase("balance")) {
                name = "Evaly Account";
                description = balanceText;
                image = R.drawable.payment_icon_evaly;
            } else if (paymentMethod.equalsIgnoreCase("cod+balance")) {
                name = Constants.BALANCE_WITH_CASH;
                description = balanceText;
                image = R.drawable.payment_icon_evaly;
            } else if (paymentMethod.equalsIgnoreCase("cod")) {
                name = Constants.CASH_ON_DELIVERY;
                description = "Payment on delivery.";
                image = R.drawable.ic_cash;
            } else if (paymentMethod.equalsIgnoreCase("sslcommerz_gateway")) {
                name = Constants.OTHERS;
                description = "Pay using SSLCommerz gateway\nfrom your card.";
                image = R.drawable.sslcommerz;
            }

            PaymentMethodModel model = new PaymentMethodModel();
            model.setName(name);
            model.setDescription(description);
            model.setRedText(redText);
            model.setEnabled(isEnabled);
            model.setSelected(false);
            model.setImage(image);
            model.setBadgeText(disabledPaymentMethodText);

            if (name != null)
                methodList.add(model);
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
        if (paymentOptionRedirectListener == null || url == null || url.equals("")) {
            // Toast.makeText(getContext(), "Unable to make payment, try again later!", Toast.LENGTH_SHORT).show();
        } else {
            if (isVisible() && !isRemoving() && !isDetached())
                dismissAllowingStateLoss();
            if (getActivity() != null) {
                paymentOptionRedirectListener.onPaymentRedirect(url, enteredAmount, invoice_no);
            }
        }
    }

    public interface PaymentOptionListener {
        void onPaymentRedirect(String url, String amount, String invoiceNo);
    }


}
