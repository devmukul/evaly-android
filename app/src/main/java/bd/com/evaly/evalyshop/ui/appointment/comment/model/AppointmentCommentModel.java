package bd.com.evaly.evalyshop.ui.appointment.comment.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemAppointmentBinding;
import bd.com.evaly.evalyshop.databinding.ItemCommentBinding;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_comment)
public abstract class AppointmentCommentModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    AppointmentCommentResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemCommentBinding binding = (ItemCommentBinding) holder.getDataBinding();
        binding.userName.setText(model.getCommentedBy().getFullName());
        binding.text.setText(model.getComment());
        binding.date.setText(Utils.getTimeAgoSmall(Utils.formattedDateFromStringTimestamp("", "", model.getCreatedAt())));
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}

