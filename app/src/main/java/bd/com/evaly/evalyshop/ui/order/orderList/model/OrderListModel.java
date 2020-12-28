package bd.com.evaly.evalyshop.ui.order.orderList.model;


import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemOrderListBinding;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_order_list)
public abstract class OrderListModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    OrderListItem model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemOrderListBinding binding = (ItemOrderListBinding) holder.getDataBinding();
        binding.orderID.setText(model.getInvoiceNo());

        try {
            binding.date.setText(Utils.formattedDateFromString("", "d MMM, hh:mm aa", model.getDate()));
        } catch (Exception e) {

            String str = model.getDate();
            String s[] = str.split("T");
            binding.date.setText(s[0]);
        }

        String orderStatus = model.getOrderStatus();
        String paymentStatus = model.getPaymentStatus();

        try {
            if (orderStatus.toLowerCase().equals("cancel"))
                binding.status.setText("Cancelled");
            else
                binding.status.setText(Utils.toFirstCharUpperAll(orderStatus));
        } catch (Exception e) {
            binding.status.setText(orderStatus);
        }

        try {
            binding.paymentStatus.setText(Utils.toFirstCharUpperAll(paymentStatus));
        } catch (Exception e) {
            binding.paymentStatus.setText(paymentStatus);
        }

        if (paymentStatus.toLowerCase().equals("refund_requested"))
            binding.paymentStatus.setText("Refund Req");

        if (paymentStatus.toLowerCase().equals("paid")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#33d274"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.toLowerCase().equals("unpaid")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.toLowerCase().equals("partial")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#009688"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.toLowerCase().equals("refunded")) {
            binding.paymentStatus.setTextColor(Color.parseColor("#333333"));
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#eeeeee"));
        } else if (paymentStatus.toLowerCase().equals("refund_requested")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#c45da8"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
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

        binding.phone.setText(Utils.formatPriceSymbol(model.getTotal()));

        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

