package bd.com.evaly.evalyshop.ui.order.orderDetails.refund;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.BottomSheetRefundRequestBinding;
import bd.com.evaly.evalyshop.models.payment.PaymentMethodModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsViewModel;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.controller.PaymentMethodController;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RefundBottomSheet extends BaseBottomSheetFragment<BottomSheetRefundRequestBinding, RefundViewModel> {

    @Inject
    FirebaseRemoteConfig remoteConfig;
    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;

    private String invoice_no;
    private String paymentType;
    private ViewDialog dialog;
    private OrderDetailsViewModel orderDetailsViewModel;
    private ArrayAdapter<String> spinnerAdapter;
    private PaymentMethodController paymentMethodController;

    public RefundBottomSheet() {
        super(RefundViewModel.class, R.layout.bottom_sheet_refund_request);
    }

    public static RefundBottomSheet newInstance(String invoiceNo, String orderStatus, String paymentMethod, String paymentStatus, boolean is_eligible, String orderDate) {

        RefundBottomSheet instance = new RefundBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("invoice_no", invoiceNo.toLowerCase());
        bundle.putString("order_status", orderStatus.toLowerCase());
        bundle.putString("payment_method", paymentMethod.toLowerCase());
        bundle.putString("payment_status", paymentStatus.toLowerCase());
        bundle.putBoolean("is_eligible", is_eligible);
        bundle.putString("order_date", orderDate);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    protected void initViews() {
        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
        }
        orderDetailsViewModel = new ViewModelProvider(requireActivity()).get(OrderDetailsViewModel.class);
        dialog = new ViewDialog(getActivity());

        initPaymentMethodRecycler();
    }

    private void initPaymentMethodRecycler() {
        paymentMethodController = new PaymentMethodController();
        paymentMethodController.setActivity((AppCompatActivity) getActivity());
        paymentMethodController.setFocusListener(object -> {

        });

        binding.recyclerView.setAdapter(paymentMethodController.getAdapter());

        List<PaymentMethodModel> list = new ArrayList<>();
        list.add(new PaymentMethodModel("Evaly Account", "Get refund on Evaly Account", null, R.drawable.payment_icon_evaly, false, true));
        list.add(new PaymentMethodModel("Automated", "Get refund through your paid methods", null, R.drawable.all_payment_methods, false, true));
        paymentMethodController.loadData(list, true);
    }


    @Override
    protected void liveEventsObservers() {
        viewModel.responseLiveData.observe(getViewLifecycleOwner(), aBoolean -> {
            dialog.hideDialog();
            if (aBoolean)
                onSuccess();
        });
    }

    @Override
    protected void clickListeners() {
        binding.submitBtn.setOnClickListener(v -> {

            PaymentMethodModel model = paymentMethodController.getSelectedMethod();
            if (model == null) {
                ToastUtils.show("Please select refund method");
                return;
            }

            paymentType = model.getName();

            if (paymentType == null)
                return;
            if (paymentType.equals("Automated"))
                paymentType = "Non_balance";
            else if (paymentType.equals("Evaly Account"))
                paymentType = "Balance";

            HashMap<String, String> body = new HashMap<>();
            body.put("invoice_no", invoice_no.toUpperCase());
            if (paymentType.equals("Evaly Account") || paymentType.equals("Balance")) {
                body.put("refund_type", "Balance");
            } else if (paymentType.equals("Automated") || paymentType.equals("Non Balance") || paymentType.equals("Non_balance"))
                body.put("refund_type", "Non_balance");

            viewModel.requestRefund(body);
        });
    }

    private void onSuccess() {
        if (getContext() != null) {
            if (isVisible()) {
                if (getActivity() != null) {
                    orderDetailsViewModel.refresh();
                    dismissAllowingStateLoss();
                }
            }
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

}
