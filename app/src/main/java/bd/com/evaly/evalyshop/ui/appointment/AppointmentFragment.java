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
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppointmentFragment extends BaseFragment<FragmentAppointmentBinding, AppointmentViewModel> implements AppointmentController.ClickListener {

    private MainViewModel mainViewModel;
    private AppointmentController controller;
    private boolean isLoading = false;
    private boolean shouldScrollToTop = false;

    public AppointmentFragment(){
        super(AppointmentViewModel.class, R.layout.fragment_appointment);
    }

    @Override
    protected void initViews() {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), appointmentResponses -> {
            isLoading = false;
            controller.setLoading(false);
            controller.setList(appointmentResponses);
            controller.requestModelBuild();
            if (shouldScrollToTop)
                if (appointmentResponses.size() > 0)
                    binding.recycler.postDelayed(() -> binding.recycler.smoothScrollToPosition(0), 100);
            shouldScrollToTop = false;
        });

        mainViewModel.getRefreshCurrentFragment().observe(getViewLifecycleOwner(), aVoid -> {
            shouldScrollToTop = true;
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

    @Override
    protected void setupRecycler() {
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

    @Override
    protected void clickListeners() {
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

    @Override
    public void onClick(AppointmentResponse model) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        NavHostFragment.findNavController(this).navigate(R.id.appointmentCommentBottomSheet, bundle);
    }
}
