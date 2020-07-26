package bd.com.evaly.evalyshop.ui.order.orderList.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    ArrayList<OrderListItem> orders;
    Context context;

    public OrderAdapter(Context context, ArrayList<OrderListItem> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_list, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.orderID.setText(orders.get(i).getInvoiceNo());


        try {
            myViewHolder.date.setText(Utils.formattedDateFromString("", "d MMM, hh:mm aa", orders.get(i).getDate()));
        } catch (Exception e) {

            String str = orders.get(i).getDate();
            String s[] = str.split("T");
            myViewHolder.date.setText(s[0]);
        }

        String orderStatus = orders.get(i).getOrderStatus();
        String paymentStatus = orders.get(i).getPaymentStatus();

        try {

            if (orderStatus.toLowerCase().equals("cancel"))
                myViewHolder.status.setText("Cancelled");
            else
                myViewHolder.status.setText(Utils.toFirstCharUpperAll(orderStatus));

        } catch (Exception e) {
            myViewHolder.status.setText(orderStatus);
        }

        try {
            myViewHolder.paymentStatus.setText(Utils.toFirstCharUpperAll(paymentStatus));
        } catch (Exception e) {
            myViewHolder.paymentStatus.setText(paymentStatus);
        }


        if (paymentStatus.toLowerCase().equals("refund_requested"))
            myViewHolder.paymentStatus.setText("Refund Req");

        if (paymentStatus.toLowerCase().equals("paid")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#33d274"));
            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.toLowerCase().equals("unpaid")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.toLowerCase().equals("partial")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#009688"));
            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.toLowerCase().equals("refunded")) {
            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#333333"));
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#eeeeee"));
        } else if (paymentStatus.toLowerCase().equals("refund_requested")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#c45da8"));
            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        }

        if (orderStatus.toLowerCase().equals("cancel"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#d9534f"));
        else if (orderStatus.toLowerCase().equals("delivered"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#4eb950"));
        else if (orderStatus.toLowerCase().equals("pending"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#e79e03"));
        else if (orderStatus.toLowerCase().equals("confirmed"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#7abcf8"));
        else if (orderStatus.toLowerCase().equals("shipped"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#db9803"));
        else if (orderStatus.toLowerCase().equals("picked"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#3698db"));
        else if (orderStatus.toLowerCase().equals("processing"))
            myViewHolder.status.setBackgroundColor(Color.parseColor("#5ac1de"));


        myViewHolder.phone.setText("৳ " + Utils.formatPrice(orders.get(i).getTotal()));
        myViewHolder.view.setOnClickListener(v -> {

            try {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderID", orders.get(i).getInvoiceNo());
                context.startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderID", orders.get(i).getInvoiceNo());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, date, phone, status, paymentStatus;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            status = itemView.findViewById(R.id.status);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            phone = itemView.findViewById(R.id.phone);
            date = itemView.findViewById(R.id.date);
            view = itemView;
        }
    }

}
