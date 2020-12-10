package bd.com.evaly.evalyshop.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetAddAddressBinding;
import bd.com.evaly.evalyshop.databinding.FragmentAddressBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.ui.address.controller.AddressController;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressFragment extends Fragment implements AddressController.ClickListener {

    AddressViewModel viewModel;
    private FragmentAddressBinding binding;
    private AddressController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateViews();
        setupAdapter();
        clickListeners();
        liveEvents();
    }

    private void liveEvents() {
        viewModel.getAddressLiveData().observe(getViewLifecycleOwner(), addressResponses -> {
            controller.setLoading(false);
            controller.setList(addressResponses);
            controller.requestModelBuild();
        });
    }

    private void setupAdapter() {
        if (controller == null)
            controller = new AddressController();
        controller.setFilterDuplicates(true);
        controller.setClickListener(this);
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void updateViews() {

    }

    private void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add)
                addAddress(null, 0);
            return false;
        });
    }

    public void deleteAddress(AddressItem model) {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("Are you sure you want to delete the address?")
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                    viewModel.deleteAddress(model);
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {

                })
                .show();
    }

    public void addAddress(AddressItem model, int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        final BottomSheetAddAddressBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.bottom_sheet_add_address, null, false);

        if (model != null) {
            dialogBinding.address.setText(model.getAddress());
            dialogBinding.area.setText(model.getArea());
            dialogBinding.city.setText(model.getCity());
            dialogBinding.region.setText(model.getRegion());
            if (model.getFullName() == null || model.getFullName().equals(""))
                dialogBinding.fullName.setText(CredentialManager.getUserData().getFullName());
            else
                dialogBinding.fullName.setText(model.getFullName());
            if (model.getPhoneNumber() == null || model.getPhoneNumber().equals(""))
                dialogBinding.contactNumber.setText(CredentialManager.getUserData().getContact());
            else
                dialogBinding.contactNumber.setText(model.getPhoneNumber());
        } else {
            dialogBinding.fullName.setText(CredentialManager.getUserData().getFullName());
            dialogBinding.contactNumber.setText(CredentialManager.getUserData().getContact());
        }

        dialogBinding.save.setOnClickListener(view -> {
            String address = dialogBinding.address.getText().toString().trim();
            String area = dialogBinding.area.getText().toString().trim();
            String city = dialogBinding.city.getText().toString().trim();
            String region = dialogBinding.region.getText().toString().trim();
            String phoneNumber = dialogBinding.contactNumber.getText().toString().trim();
            String fullName = dialogBinding.fullName.getText().toString().trim();

            String error = null;
            if (address.isEmpty())
                error = "Please enter address line 1";
            else if (area.isEmpty())
                error = "Please enter area";
            else if (city.isEmpty())
                error = "Please enter city";
            else if (phoneNumber.equals(""))
                error = "Pleae enter phone number";
            else if (fullName.equals(""))
                error = "Please enter full name";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            AddressItem body = new AddressItem();
            body.setAddress(address);
            body.setArea(area);
            body.setCity(city);
            body.setRegion(region);
            body.setFullName(fullName);
            body.setPhoneNumber(phoneNumber);

            if (model == null)
                viewModel.addAddress(body);
            else
                viewModel.editAddress(body, position);

            viewModel.saveAddress();
            dialog.cancel();
        });

        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
    }

    @Override
    public void onDelete(AddressItem model) {
        deleteAddress(model);
    }

    @Override
    public void onEdit(AddressItem model, int position) {
        addAddress(model, position);
    }
}
