package bd.com.evaly.evalyshop.ui.issue.create;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetCreateIssueBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class CreateIssueBottomSheet extends BottomSheetDialogFragment {

    private CreateIssueViewModel viewModel;
    private BottomSheetCreateIssueBinding binding;
    private List<IssueCategoryModel> itemList = new ArrayList<>();
    private String orderStatus, invoice, seller, shop, imageUrl;
    private IssueCreateBody model;
    private List<String> options;

    public static CreateIssueBottomSheet newInstance(String invoiceNo, String orderStatus, String sellerName, String shopSlug) {
        CreateIssueBottomSheet bottomSheet = new CreateIssueBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("orderStatus", orderStatus);
        bundle.putString("invoiceNo", invoiceNo);
        bundle.putString("seller", sellerName);
        bundle.putString("shop", shopSlug);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);
        viewModel = new ViewModelProvider(this).get(CreateIssueViewModel.class);
        orderStatus = getArguments().getString("orderStatus");
        invoice = getArguments().getString("invoice");
        seller = getArguments().getString("seller");
        shop = getArguments().getString("shop");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCreateIssueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new IssueCreateBody();
        options = new ArrayList<>();
        liveEvents();
    }

    private void liveEvents() {
        viewModel.categoryLiveList.observe(getViewLifecycleOwner(), issueCategoryModels -> {
            if (issueCategoryModels == null) {
                ToastUtils.show(getResources().getString(R.string.something_wrong));
                return;
            }
            options = new ArrayList<>();
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, options);
            binding.spnDelivery.setAdapter(adapter);
            itemList.clear();
            itemList.addAll(issueCategoryModels);
            for (IssueCategoryModel item : issueCategoryModels) {
                if (orderStatus.equals("pending")) {
                    if (item.getName().equals("Bank Payment") || item.getName().equals("Payment"))
                        options.add(item.getName());
                } else
                    options.add(item.getName());
            }
            adapter.notifyDataSetChanged();
            updateViews();
        });
    }

    private void updateViews() {

        binding.spnDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                model.setCategory(viewModel.getCategoryModelByName(options.get(i)).getId());
                String catName = options.get(i).toLowerCase();
                if (catName.contains("payment") || catName.contains("bank") || catName.contains("cashback") || catName.contains("return"))
                    model.setPriority("urgent");
                else
                    model.setPriority("medium");

                String paymentType = viewModel.getCategoryModelByName(options.get(i)).getName();
                if (paymentType == null)
                    return;

                if (paymentType.equals("Balance Refund") || paymentType.equals("Card Refund")) {
                    binding.llBkashHolder.setVisibility(View.GONE);
                    binding.llBankInfoHolder.setVisibility(View.GONE);
                } else if (paymentType.equals("bKash Refund") || paymentType.equals("Nagad Refund")) {
                    binding.llBkashHolder.setVisibility(View.VISIBLE);
                    binding.llBankInfoHolder.setVisibility(View.GONE);
                    binding.descriptionHolder.setVisibility(View.GONE);
                } else if (paymentType.equals("Bank Refund")) {
                    binding.llBkashHolder.setVisibility(View.GONE);
                    binding.llBankInfoHolder.setVisibility(View.VISIBLE);
                    binding.descriptionHolder.setVisibility(View.GONE);
                } else {
                    binding.llBkashHolder.setVisibility(View.GONE);
                    binding.llBankInfoHolder.setVisibility(View.GONE);
                    binding.descriptionHolder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.addPhoto.setOnClickListener(view -> openImageSelector());

        binding.btnSubmit.setOnClickListener(view -> {

            model.setChannel("customer_app");
            model.setContext("order");
            model.setCustomer(CredentialManager.getUserName());
            model.setInvoiceNumber(invoice);

            model.setSeller(seller);
            model.setShop(shop);

            if (imageUrl != null && !imageUrl.equals("")) {
                List<String> imageUrls = new ArrayList<>();
                imageUrls.add(imageUrl);
                model.setAttachments(imageUrls);
            }

            int selectedPosition = binding.spnDelivery.getSelectedItemPosition();

            String paymentType = viewModel.getCategoryModelByName(options.get(selectedPosition)).getName();

            String description = "";

            if (paymentType.equals("bKash Refund")) {
                String bkashNumber = binding.etNumber.getText().toString().trim();
                if (bkashNumber.equals("")) {
                    ToastUtils.show("Please enter your bKash account number.");
                    return;
                } else if (!Utils.isValidNumber(bkashNumber)) {
                    ToastUtils.show("Please enter valid bKash account number.");
                    return;
                }
                description = "bKash Account: " + bkashNumber;
            } else if (paymentType.equals("Nagad Refund")) {
                String number = binding.etNumber.getText().toString().trim();
                if (number.equals("")) {
                    ToastUtils.show("Please enter your Nagad account number.");
                    return;
                } else if (!Utils.isValidNumber(number)) {
                    ToastUtils.show("Please enter valid Nagad account number.");
                    return;
                }
                description = "Nagad Account: " + number;
            } else if (paymentType.equals("Bank Refund")) {
                String bankName = binding.etBankName.getText().toString().trim();
                String branchName = binding.etBranch.getText().toString().trim();
                String routingNumber = binding.etBranchRouting.getText().toString();
                String accountName = binding.etAccountName.getText().toString().trim();
                String accountNumber = binding.etAccountNumber.getText().toString().trim();

                String errorMessage = "";
                if (bankName.equals(""))
                    errorMessage = "Please enter bank name.";
                else if (branchName.equals(""))
                    errorMessage = "Please enter branch name";
                else if (routingNumber.equals(""))
                    errorMessage = "Please enter routing number.";
                else if (accountName.equals(""))
                    errorMessage = "Please enter account name.";
                else if (accountNumber.equals(""))
                    errorMessage = "Please enter account number.";

                if (!errorMessage.equals("")) {
                    ToastUtils.show(errorMessage);
                    return;
                }
                description = "Account Name: " + accountName + " \n" +
                        "Account Number: " + accountNumber + " \n" +
                        "Bank Name: " + bankName + "\n" +
                        "Branch Name: " + branchName + "\n" +
                        "Routing Number: " + routingNumber;
            } else
                description = binding.etDescription.getText().toString();

            model.setAdditionalInfo(description);
            viewModel.submitIssue(model);
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialogz -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialogz;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (getContext() != null && bottomSheet != null) {
                ScreenUtils screenUtils = new ScreenUtils(getContext());

                LinearLayout dialogLayoutReply = dialog.findViewById(R.id.container2);
                assert dialogLayoutReply != null;
                dialogLayoutReply.setMinimumHeight(screenUtils.getHeight());

                //BottomSheetBehavior.from(bottomSheet).setPeekHeight(screenUtils.getHeight());
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }

        });
        return bottomSheetDialog;
    }
}
