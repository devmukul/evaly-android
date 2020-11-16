package bd.com.evaly.evalyshop.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.databinding.FragmentAddressBinding;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.ui.address.controller.AddressController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressFragment extends Fragment {

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
            controller.setList(addressResponses);
            controller.requestModelBuild();
        });
    }

    private void setupAdapter() {
        if (controller == null)
            controller = new AddressController();
        controller.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void updateViews() {

    }


    private void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

}
