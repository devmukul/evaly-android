package bd.com.evaly.evalyshop.ui.shop.delivery.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shop.shopDetails.DeliveryOption;

public class DeliveryOptionAdapter extends RecyclerView.Adapter<DeliveryOptionAdapter.DeliveryOptionViewHolder> {

    private List<DeliveryOption> list;
    private Context context;

    public DeliveryOptionAdapter(List<DeliveryOption> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeliveryOptionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_delivery_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryOptionViewHolder holder, int position) {
        holder.tvDeliveryOption.setText(list.get(position).getName());
        holder.tvDeliveryOptionDesc.setText(list.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DeliveryOptionViewHolder extends RecyclerView.ViewHolder {

        TextView tvDeliveryOption;
        TextView tvDeliveryOptionDesc;


        public DeliveryOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeliveryOption = itemView.findViewById(R.id.tvDeliveryOption);
            tvDeliveryOptionDesc = itemView.findViewById(R.id.tvDeliveryOptionDesc);
        }
    }
}
