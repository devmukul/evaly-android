package bd.com.evaly.evalyshop.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentAppointmentBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.ui.appointment.controller.AppointmentController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppointmentFragment extends Fragment {

    @Inject
    AppointmentViewModel viewModel;
    private FragmentAppointmentBinding binding;
    private AppointmentController controller;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater);
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
    }

    private void initRecycler() {
        if (controller == null)
            controller = new AppointmentController();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
    }

    private void clickListeners() {
        binding.createAppointment.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createAppointmentBottomSheet);
        });
    }


}
