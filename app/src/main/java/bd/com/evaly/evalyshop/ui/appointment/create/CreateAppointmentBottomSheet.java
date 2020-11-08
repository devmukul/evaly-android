package bd.com.evaly.evalyshop.ui.appointment.create;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetCreateAppointmentBinding;
import bd.com.evaly.evalyshop.models.appointment.AppointmentCategoryResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentRequest;
import bd.com.evaly.evalyshop.models.appointment.AppointmentTimeSlotResponse;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class CreateAppointmentBottomSheet extends BottomSheetDialogFragment {

    @Inject
    CreateAppointmentViewModel viewModel;
    private AppointmentRequest createBody;
    DatePickerDialog.OnDateSetListener datePickerListener = (datePicker, year, month, day) -> {
        String date = year + "-" + month + "-" + day;
        createBody.setDate(date);
        viewModel.getTimeSlot(date);
    };
    private BottomSheetCreateAppointmentBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

}
