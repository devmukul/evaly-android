package bd.com.evaly.evalyshop.ui.appointment.comment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetAppointmentCommentBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentRequest;
import bd.com.evaly.evalyshop.ui.appointment.comment.controller.AppointmentCommentController;
import bd.com.evaly.evalyshop.ui.appointment.model.AppointmentModelBinder;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
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

        viewModel.isLoadingLiveData.observe(getViewLifecycleOwner(), aBoolean -> {
            controller.setLoading(aBoolean);
            controller.requestModelBuild();
        });

        viewModel.getListLiveData().observe(getViewLifecycleOwner(), appointmentCommentResponses -> {
            controller.setLoading(false);
            controller.setList(appointmentCommentResponses);
            controller.requestModelBuild();
            binding.commentInput.setText("");
            binding.submitComment.setEnabled(true);
        });

        viewModel.getAppointmentLiveData().observe(getViewLifecycleOwner(), appointmentResponse -> {
            AppointmentModelBinder.bind(binding.appointmentBinding, appointmentResponse);
            binding.appointmentBinding.cancel.setVisibility(View.GONE);
            String status = appointmentResponse.getStatus();
            if (status.equals("completed") || status.equals("rejected") || status.equals("absent") || status.equals("canceled"))
                binding.commentHolder.setVisibility(View.GONE);
        });
    }

    private void clickListeners() {
        binding.submitComment.setOnClickListener(view -> {
            createComment();
        });
    }

    private void createComment() {
        String text = binding.commentInput.getText().toString().trim();
        if (text.length() == 0) {
            ToastUtils.show("Please write something!");
            return;
        }

        AppointmentCommentRequest body = new AppointmentCommentRequest();
        body.setAppointment(viewModel.getAppointmentLiveData().getValue().getAppointmentId());
        body.setComment(text);
        viewModel.createComment(body);
        binding.submitComment.setEnabled(false);
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
