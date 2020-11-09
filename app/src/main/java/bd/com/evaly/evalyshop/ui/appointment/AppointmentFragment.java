package bd.com.evaly.evalyshop.ui.appointment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentAppointmentBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.ui.appointment.controller.AppointmentController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppointmentFragment extends Fragment implements AppointmentController.ClickListener {

    @Inject
    AppointmentViewModel viewModel;
    private MainViewModel mainViewModel;
    private FragmentAppointmentBinding binding;
    private AppointmentController controller;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clickListeners();
        initRecycler();
        liveEventObservers();
    }

    private void liveEventObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), appointmentResponses -> {
            isLoading = false;
            controller.setLoading(false);
            controller.setList(appointmentResponses);
            controller.requestModelBuild();
        });

        mainViewModel.getRefreshCurrentFragment().observe(getViewLifecycleOwner(), aVoid -> {
            viewModel.clear();
            viewModel.loadList();
            binding.recycler.setAdapter(null);
            binding.recycler.setAdapter(controller.getAdapter());
        });

        viewModel.cancelResponse.observe(getViewLifecycleOwner(), responseCommonDataResponse -> {
            ToastUtils.show(responseCommonDataResponse.getMessage());
            if (responseCommonDataResponse.getSuccess()) {
                viewModel.clear();
                viewModel.loadList();
                binding.recycler.setAdapter(null);
                binding.recycler.setAdapter(controller.getAdapter());
            }
        });
    }

    private void initRecycler() {
        if (controller == null)
            controller = new AppointmentController();
        controller.setClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(controller.getAdapter());
        binding.recycler.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.loadList();
                    isLoading = true;
                    controller.setLoading(true);
                    controller.requestModelBuild();
                }
            }
        });

        controller.requestModelBuild();
    }

    private void clickListeners() {
        binding.toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            NavHostFragment.findNavController(this).navigate(R.id.createAppointmentBottomSheet);
            return false;
        });
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    @Override
    public void onCancelClick(AppointmentResponse model) {
        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to cancel the appointment?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", (dialog, whichButton) -> viewModel.cancelAppointment(model.getAppointmentId()))
                .setNegativeButton("NO", null).show();
    }
}
