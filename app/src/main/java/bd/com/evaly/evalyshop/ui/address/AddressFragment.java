package bd.com.evaly.evalyshop.ui.address;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentAddressBinding;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.ui.address.addAddress.AddAddressBottomSheet;
import bd.com.evaly.evalyshop.ui.address.controller.AddressController;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressFragment extends BaseBottomSheetFragment<FragmentAddressBinding, AddressViewModel> implements AddressController.ClickListener {

    @Inject
    SharedObservers sharedObservers;
    private AddressController controller;
    private boolean isPicker = false;

    public AddressFragment() {
        super(AddressViewModel.class, R.layout.fragment_address);
    }

    @Override
    protected void initViews() {
        if (getArguments() != null && getArguments().containsKey("is_picker"))
            isPicker = true;
        updateViews();
        setupAdapter();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.addressLiveData.observe(getViewLifecycleOwner(), addressResponses -> {
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
        if (isPicker)
            binding.toolbar.setTitle(getString(R.string.select_address));
        else
            binding.toolbar.setTitle(getString(R.string.addresses));
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> {
            if (isPicker)
                dismissAllowingStateLoss();
            else
                getActivity().onBackPressed();
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add)
                addAddress(null, false);
            return false;
        });
    }

    public void deleteAddress(AddressResponse model) {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("Are you sure you want to delete the address?")
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                    viewModel.deleteAddress(model.getId());
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {

                })
                .show();
    }

    public void addAddress(AddressResponse model, boolean isUpdate) {
        AddAddressBottomSheet bottomSheet = new AddAddressBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        bundle.putBoolean("is_edit", isUpdate);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getParentFragmentManager(), "add address");
    }

    @Override
    public void onClick(AddressResponse model) {
        if (isPicker) {
            sharedObservers.onAddressChanged.setValue(model);
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDelete(AddressResponse model) {
        deleteAddress(model);
    }

    @Override
    public void onEdit(AddressResponse model) {
        addAddress(model, true);
    }
}
