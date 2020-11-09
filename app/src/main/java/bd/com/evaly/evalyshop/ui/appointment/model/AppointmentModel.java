package bd.com.evaly.evalyshop.ui.appointment.model;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAppointmentBinding;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_appointment)
public abstract class AppointmentModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AppointmentResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemAppointmentBinding binding = (ItemAppointmentBinding) holder.getDataBinding();
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
        binding.status.setText(Utils.capitalize(status));
        if (status.equals("booked"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#5E80D9"));
        else if (status.equals("rejected"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#D2D60D"));
        else if (status.equals("canceled"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#CA293D"));
        else if (status.equals("completed"))
            binding.bgCard.setCardBackgroundColor(Color.parseColor("#2ECD64"));

        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

