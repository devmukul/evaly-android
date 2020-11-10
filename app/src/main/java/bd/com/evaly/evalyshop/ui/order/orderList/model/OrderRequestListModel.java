package bd.com.evaly.evalyshop.ui.order.orderList.model;


import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemOrderRequestBinding;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_order_request)
public abstract class OrderRequestListModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    OrderRequestResponse model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemOrderRequestBinding binding = (ItemOrderRequestBinding) holder.getDataBinding();
        binding.orderID.setText(model.getShopName());

        try {
            binding.date.setText(Utils.formattedDateFromString("", "d MMM, hh:mm aa", model.getCreatedAt()));
        } catch (Exception e) {

            String str = model.getCreatedAt();
            String s[] = str.split("T");
            binding.date.setText(s[0]);
        }

        String orderStatus = model.getRequestStatus();

        try {
            if (orderStatus.toLowerCase().equals("cancel"))
                binding.status.setText("Cancelled");
            else
                binding.status.setText(Utils.toFirstCharUpperAll(orderStatus));
        } catch (Exception e) {
            binding.status.setText(orderStatus);
        }

        if (orderStatus.toLowerCase().equals("cancel"))
            binding.status.setBackgroundColor(Color.parseColor("#d9534f"));
        else if (orderStatus.toLowerCase().equals("delivered"))
            binding.status.setBackgroundColor(Color.parseColor("#4eb950"));
        else if (orderStatus.toLowerCase().equals("pending"))
            binding.status.setBackgroundColor(Color.parseColor("#e79e03"));
        else if (orderStatus.toLowerCase().equals("confirmed"))
            binding.status.setBackgroundColor(Color.parseColor("#7abcf8"));
        else if (orderStatus.toLowerCase().equals("shipped"))
            binding.status.setBackgroundColor(Color.parseColor("#db9803"));
        else if (orderStatus.toLowerCase().equals("picked"))
            binding.status.setBackgroundColor(Color.parseColor("#3698db"));
        else if (orderStatus.toLowerCase().equals("processing"))
            binding.status.setBackgroundColor(Color.parseColor("#5ac1de"));

        binding.phone.setText("৳ " + Utils.formatPrice(model.getOrderTotal()));

        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

