package bd.com.evaly.evalyshop.ui.appointment.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAppointmentBinding;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;

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
        binding.status.setText(model.getStatus());
        binding.date.setText(String.format("%s(%s)", model.getDate(), model.getTimeSlot()));

        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

