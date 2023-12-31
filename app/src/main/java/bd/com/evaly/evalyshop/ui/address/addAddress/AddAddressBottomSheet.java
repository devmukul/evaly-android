package bd.com.evaly.evalyshop.ui.address.addAddress;

import android.app.AlertDialog;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.BottomSheetAddAddressBinding;
import bd.com.evaly.evalyshop.models.catalog.location.LocationResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddAddressBottomSheet extends BaseBottomSheetFragment<BottomSheetAddAddressBinding, AddAddressViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;

    public AddAddressBottomSheet() {
        super(AddAddressViewModel.class, R.layout.bottom_sheet_add_address);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void clickListeners() {
        binding.region.setOnClickListener(view1 -> {
            if (viewModel.divisionLiveData.getValue() != null)
                showSingleChoiceDialog(viewModel.divisionLiveData.getValue(), "Division");
        });

        binding.city.setOnClickListener(view1 -> {
            if (viewModel.cityLiveData.getValue() != null)
                showSingleChoiceDialog(viewModel.cityLiveData.getValue(), "City");
            else
                ToastUtils.show("Please select division first");
        });

        binding.area.setOnClickListener(view1 -> {
            if (viewModel.areaLiveData.getValue() != null)
                showSingleChoiceDialog(viewModel.areaLiveData.getValue(), "Area");
            else
                ToastUtils.show("Please select city first");
        });

        binding.save.setOnClickListener(view2 -> {
            String address = binding.address.getText().toString().trim();
            String area = getLocationName(viewModel.selectedAreaLiveData.getValue());
            String city = getLocationName(viewModel.selectedCityLiveData.getValue());
            String region = getLocationName(viewModel.selectedDivisionLiveData.getValue());

            String phoneNumber = binding.contactNumber.getText().toString().trim();
            String fullName = binding.fullName.getText().toString().trim();

            String error = null;

            if (address.isEmpty())
                error = "Please enter address line 1";
            else if (area == null || area.isEmpty())
                error = "Please enter area";
            else if (city == null || city.isEmpty())
                error = "Please enter city";
            else if (region == null || region.isEmpty())
                error = "Please enter region";
            else if (phoneNumber.equals(""))
                error = "Please enter phone number";
            else if (!Utils.isValidNumber(phoneNumber))
                error = "Please enter valid phone number";
            else if (fullName.equals(""))
                error = "Please enter full name";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            AddressRequest body = new AddressRequest();
            body.setAddress(address);
            body.setArea(area);
            body.setAreaSlug(viewModel.selectedAreaLiveData.getValue().getSlug());
            body.setCity(city);
            body.setCitySlug(viewModel.selectedCityLiveData.getValue().getSlug());
            body.setRegion(region);
            body.setRegionSlug(viewModel.selectedDivisionLiveData.getValue().getSlug());
            body.setFullName(fullName);
            body.setPhoneNumber(phoneNumber);

            if (!viewModel.isEdit() && viewModel.modelLiveData.getValue() == null)
                viewModel.addAddress(body);
            else
                viewModel.editAddress(body);

            viewModel.saveAddress();
        });
    }

    private String getLocationName(LocationResponse model) {
        if (model == null)
            return "";
        else
            return model.getName();
    }

    private void showSingleChoiceDialog(List<LocationResponse> list, String type) {
        String[] listItems = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            listItems[i] = list.get(i).getName();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Select " + type);
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if (type.equals("Division"))
                viewModel.selectedDivisionLiveData.setValue(list.get(i));
            else if (type.equals("City"))
                viewModel.selectedCityLiveData.setValue(list.get(i));
            else if (type.equals("Area"))
                viewModel.selectedAreaLiveData.setValue(list.get(i));

            dialogInterface.dismiss();
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.dismissBottomSheet.observe(getViewLifecycleOwner(), aVoid -> dismissAllowingStateLoss());

        viewModel.selectedDivisionLiveData.observe(getViewLifecycleOwner(), model -> {
            if (model == null)
                binding.region.setText("Select division");
            else {
                viewModel.selectedCityLiveData.setValue(null);
                viewModel.selectedAreaLiveData.setValue(null);
                viewModel.cityLiveData.setValue(null);
                viewModel.areaLiveData.setValue(null);
                binding.region.setText(model.getName());
                viewModel.loadLocationList(model.getSlug(), "city");
            }
        });

        viewModel.selectedCityLiveData.observe(getViewLifecycleOwner(), model -> {
            if (model == null)
                binding.city.setText("Select city");
            else {
                binding.city.setText(model.getName());
                viewModel.selectedAreaLiveData.setValue(null);
                viewModel.areaLiveData.setValue(null);
                viewModel.loadLocationList(model.getSlug(), "area");
            }
        });

        viewModel.selectedAreaLiveData.observe(getViewLifecycleOwner(), model -> {
            if (model == null)
                binding.area.setText("Select area");
            else
                binding.area.setText(model.getName());
        });

        viewModel.modelLiveData.observe(getViewLifecycleOwner(), model -> {
            if (model != null) {
                binding.address.setText(model.getAddress());
                binding.area.setText(model.getArea());
                binding.city.setText(model.getCity());
                binding.region.setText(model.getRegion());
                if (model.getFullName() == null || model.getFullName().equals(""))
                    binding.fullName.setText(preferenceRepository.getUserData().getFullName());
                else
                    binding.fullName.setText(model.getFullName());
                if (model.getPhoneNumber() == null || model.getPhoneNumber().equals(""))
                    binding.contactNumber.setText(preferenceRepository.getUserData().getContact());
                else
                    binding.contactNumber.setText(model.getPhoneNumber());

                viewModel.selectedDivisionLiveData.setValue(new LocationResponse(model.getRegion(), model.getRegionSlug()));
                viewModel.selectedCityLiveData.setValue(new LocationResponse(model.getCity(), model.getCitySlug()));
                viewModel.selectedAreaLiveData.setValue(new LocationResponse(model.getArea(), model.getAreaSlug()));
            }
        });
    }
}
