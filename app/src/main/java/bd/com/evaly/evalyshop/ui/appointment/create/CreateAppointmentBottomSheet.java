package bd.com.evaly.evalyshop.ui.appointment.create;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetCreateAppointmentBinding;
import bd.com.evaly.evalyshop.models.appointment.AppointmentCategoryResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentRequest;
import bd.com.evaly.evalyshop.models.appointment.AppointmentTimeSlotResponse;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class CreateAppointmentBottomSheet extends BottomSheetDialogFragment {

    @Inject
    CreateAppointmentViewModel viewModel;
    private MainViewModel mainViewModel;
    private AppointmentRequest createBody;
    private BottomSheetCreateAppointmentBinding binding;
    DatePickerDialog.OnDateSetListener datePickerListener = (datePicker, year, month, day) -> {
        String date = String.format("%d-%01d-%01d", year, ++month, day);
        createBody.setDate(date);
        viewModel.getTimeSlot(date);
        binding.date.setText(date);
    };
    private ViewDialog dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ViewDialog(getActivity());
        createBody = new AppointmentRequest();
        clickListeners();
        liveEventObservers();
    }

    private void liveEventObservers() {
        viewModel.timeSlotLiveList.observe(getViewLifecycleOwner(), list -> {
            List<String> strList = new ArrayList<>();
            for (AppointmentTimeSlotResponse item : list) {
                if (item.isAvailable())
                    strList.add(item.getTimeSlot());
            }
            updateTimeSlotSpinner(strList);
        });

        viewModel.categoryLiveList.observe(getViewLifecycleOwner(), list -> {
            List<String> strList = new ArrayList<>();
            for (AppointmentCategoryResponse item : list) {
                strList.add(item.getName());
            }
            updateCategorySpinner(strList);
        });

        viewModel.timeErrorMessage.observe(getViewLifecycleOwner(), s -> ToastUtils.show(s));

        viewModel.createdLiveList.observe(getViewLifecycleOwner(), response -> {
            ToastUtils.show(response.getMessage());
            dialog.hideDialog();
            if (response.getSuccess())
                dismissAllowingStateLoss();
            mainViewModel.setRefreshCurrentFragment();
        });
    }

    private void updateTimeSlotSpinner(List<String> list) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTimeSlot.setAdapter(dataAdapter);
    }

    private void updateCategorySpinner(List<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategory.setAdapter(dataAdapter);
    }

    private void clickListeners() {
        binding.date.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    datePickerListener,
                    year, month, day
            );

            dialog.show();
        });

        binding.submitBtn.setOnClickListener(view -> {

            if (binding.spCategory.getSelectedItem() == null) {
                ToastUtils.show("Appointment category is unavailable");
                return;
            } else if (binding.spTimeSlot.getSelectedItem() == null) {
                ToastUtils.show("Appointment time slot is unavailable");
                return;
            }

            String timeSlot = binding.spTimeSlot.getSelectedItem().toString();
            String categoryName = binding.spCategory.getSelectedItem().toString();

            String error = null;
            if (!binding.date.getText().toString().contains("-") || createBody.getDate() == null)
                error = "Please select appointment date";
            else if (viewModel.timeSlotLiveList.getValue() == null || viewModel.timeSlotLiveList.getValue().size() == 0)
                error = "Appointment time slot is unavailable";
            else if (viewModel.getCategorySlug(categoryName) == null)
                error = "Appointment category is unavailable";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            createBody.setCategory(viewModel.getCategorySlug(categoryName));

            SimpleDateFormat df_input = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df_output = new SimpleDateFormat("E");
            df_input.setTimeZone(TimeZone.getTimeZone("gmt"));
            df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));

            try {
                Date parsed = df_input.parse(createBody.getDate());
                String outputDate = df_output.format(parsed);
                createBody.setDay(outputDate.toLowerCase());
            } catch (Exception ignored) {
            }

            createBody.setTimeSlot(timeSlot);
            viewModel.createAppointment(createBody);
            dialog.showDialog();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCreateAppointmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

}
