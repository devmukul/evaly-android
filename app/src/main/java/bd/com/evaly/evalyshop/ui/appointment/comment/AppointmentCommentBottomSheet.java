package bd.com.evaly.evalyshop.ui.appointment.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetAppointmentCommentBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.appointment.comment.controller.AppointmentCommentController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AppointmentCommentBottomSheet extends BottomSheetDialogFragment {

    AppointmentCommentViewModel viewModel;
    private MainViewModel mainViewModel;
    private BottomSheetAppointmentCommentBinding binding;
    private ViewDialog dialog;
    private AppointmentCommentController controller;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ViewDialog(getActivity());
        setupRecycler();
        clickListeners();
        liveEventObservers();
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new AppointmentCommentController();
        controller.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(controller.getAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                viewModel.loadFromApi();
            }
        });
    }

    private void liveEventObservers() {

    }


    private void clickListeners() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAppointmentCommentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
        viewModel = new ViewModelProvider(this).get(AppointmentCommentViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

}
