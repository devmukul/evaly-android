package bd.com.evaly.evalyshop.ui.order.orderDetails.refund;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetRefundRequestBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RefundBottomSheet extends BottomSheetDialogFragment {

    @Inject
    FirebaseRemoteConfig remoteConfig;
    private BottomSheetRefundRequestBinding binding;
    private String invoice_no;
    private String order_status;
    private String payment_method;
    private String payment_status;
    private String paymentType;
    private ViewDialog dialog;
    private boolean is_eligible = false;
    private int selectedOtp = 0;
    private Dialog otpAlert;
    private OrderDetailsViewModel orderDetailsViewModel;
    private String orderRefundMinDate = "";
    private String orderDate = "";
    private String nonBalanceType = "";

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetRefundRequestBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            order_status = getArguments().getString("order_status");
            payment_method = getArguments().getString("payment_method");
            payment_status = getArguments().getString("payment_status");
            is_eligible = getArguments().getBoolean("is_eligible");
            orderDate = getArguments().getString("order_date");
        }

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderDetailsViewModel = new ViewModelProvider(requireActivity()).get(OrderDetailsViewModel.class);
        dialog = new ViewDialog(getActivity());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_default);
        spinnerAdapter.add("Evaly Account");
        spinnerAdapter.add("Non Balance");

        binding.spRefundOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymentType = spinnerAdapter.getItem(position);
                if (paymentType == null)
                    return;
                if (paymentType.equals("Non Balance"))
                    paymentType = "Non_balance";
                else if (paymentType.equals("Evaly Account"))
                    paymentType = "Balance";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spRefundOption.setAdapter(spinnerAdapter);

        binding.submitBtn.setOnClickListener(v -> {

            int selectedPosition = binding.spRefundOption.getSelectedItemPosition();
            if (paymentType == null)
                paymentType = spinnerAdapter.getItem(selectedPosition);

            HashMap<String, String> body = new HashMap<>();
            body.put("invoice_no", invoice_no.toUpperCase());
            if (paymentType.equals("Evaly Account") || paymentType.equals("Balance")) {
                body.put("refund_type", "Balance");
            } else if (paymentType.equals("Non Balance") || paymentType.equals("Non_balance"))
                body.put("refund_type", "Non_balance");

            requestRefund(body);
        });
    }


    private void requestRefund(HashMap<String, String> body) {

        dialog.showDialog();

        OrderApiHelper.requestRefund(CredentialManager.getToken(), body, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                dialog.hideDialog();
                ToastUtils.show(response.getMessage());
                if (response.getSuccess())
                    onSuccess();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                if (getContext() != null) {
                    dialog.hideDialog();
                    ToastUtils.show(errorBody);
                }
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    requestRefund(body);
            }
        });
    }


    private void onSuccess() {
        if (getContext() != null) {
            if (isVisible()) {
                if (getActivity() != null) {
                    ((OrderDetailsActivity) getActivity()).updatePage();
                    dismiss();
                }
            }
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
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
