package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.OrderStatus;
import bd.com.evaly.evalyshop.util.Utils;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder>{

    ArrayList<OrderStatus> orderStatuses;
    Context context;

    public OrderStatusAdapter(ArrayList<OrderStatus> orderStatuses, Context context) {
        this.orderStatuses = orderStatuses;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_status,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String str=orderStatuses.get(i).getStatus();
        myViewHolder.time.setText(Utils.formattedDateFromString("","d MMM'\n'hh:mm a",orderStatuses.get(i).getTime()));
        myViewHolder.status.setText(str.substring(0, 1).toUpperCase() + str.substring(1));
        myViewHolder.note.setText(Html.fromHtml(orderStatuses.get(i).getNote()));


        if(i == 0)
            myViewHolder.lineTop.setBackgroundColor(Color.WHITE);

        if(i == orderStatuses.size()-1)
            myViewHolder.lineBottom.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return orderStatuses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time,status,note;
        View view,lineTop,lineBottom;
        public MyViewHolder(final View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.time);
            status=itemView.findViewById(R.id.status);
            note=itemView.findViewById(R.id.note);

            lineTop = itemView.findViewById(R.id.lineTop);
            lineBottom = itemView.findViewById(R.id.lineBottom);

            view = itemView;
        }
    }
}
