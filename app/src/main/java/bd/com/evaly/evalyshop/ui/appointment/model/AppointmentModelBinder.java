package bd.com.evaly.evalyshop.ui.appointment.model;

import android.graphics.Color;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import bd.com.evaly.evalyshop.databinding.ItemAppointmentBinding;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.util.Utils;

public class AppointmentModelBinder {

    public static void bind(ItemAppointmentBinding binding, AppointmentResponse model){
        binding.appId.setText(model.getAppointmentId());
        binding.counter.setText(model.getCounter());

        SimpleDateFormat df_input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df_output = new SimpleDateFormat("dd\nMMM");
        df_input.setTimeZone(TimeZone.getTimeZone("gmt"));
        df_output.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));

        try {
            Date parsed = df_input.parse(model.getDate());
            String outputDate = df_output.format(parsed);
            binding.date.setText(outputDate);
        } catch (Exception e) {
            binding.date.setText("");
        }

        if (model.getTimeSlot().contains("-")) {
            String[] str = model.getTimeSlot().split("-");
            binding.startTime.setText(str[0]);
            binding.endTime.setText(str[1]);
        }
        binding.category.setText(Utils.capitalize(model.getCategory().getName()));

        String status = model.getStatus();
        binding.status.setText(Utils.capitalize(status.replaceAll("_", " ")));
        if (status.equals("booked"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#5E80D9"));
        else if (status.equals("rejected"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#D2D60D"));
        else if (status.equals("canceled"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#CA293D"));
        else if (status.equals("completed"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#2ECD64"));
        else if (status.equals("on_going"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#88BA44"));

        if (!status.equals("booked"))
            binding.cancel.setVisibility(View.GONE);
        else
            binding.cancel.setVisibility(View.VISIBLE);
    }

}
