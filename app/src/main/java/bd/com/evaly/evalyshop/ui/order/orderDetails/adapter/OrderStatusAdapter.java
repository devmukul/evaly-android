package bd.com.evaly.evalyshop.ui.order.orderDetails.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.order.OrderStatus;
import bd.com.evaly.evalyshop.util.Utils;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder> {

    private List<OrderStatus> orderStatuses;
    private Context context;
    private boolean showAll = false;


    public OrderStatusAdapter(List<OrderStatus> orderStatuses, Context context) {
        this.orderStatuses = orderStatuses;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_status, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String str = orderStatuses.get(i).getStatus();

        orderStatuses.get(i).setTime(orderStatuses.get(i).getTime() + "Z");
        String statusDate = orderStatuses.get(i).getTime().replaceAll("\\.\\d*Z", "Z").replace("ZZ", "Z");
        myViewHolder.time.setText(Utils.formattedDateFromString("yyyy-MM-dd'T'HH:mm:ss'Z'", "d MMM'\n'hh:mm a", statusDate));
        myViewHolder.status.setText(String.format("%s%s", str.substring(0, 1).toUpperCase(), str.substring(1)));
        myViewHolder.note.setText(Html.fromHtml(orderStatuses.get(i).getNote()));

        if (i == 0)
            myViewHolder.lineTop.setBackgroundColor(Color.TRANSPARENT);

        if (i < getItemCount() - 1)
            myViewHolder.lineBottom.setBackgroundColor(context.getResources().getColor(R.color.bgOrderTimeLine));
        else
            myViewHolder.lineBottom.setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    public int getItemCount() {
        if (showAll || orderStatuses.size() < 4)
            return orderStatuses.size();
        else
            return 4;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView time, status, note;
        View view, lineTop, lineBottom;

        public MyViewHolder(final View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            note = itemView.findViewById(R.id.note);

            lineTop = itemView.findViewById(R.id.lineTop);
            lineBottom = itemView.findViewById(R.id.lineBottom);

            view = itemView;
        }
    }
}
