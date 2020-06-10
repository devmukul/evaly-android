package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductSpecificationsItem;

public class SpecificationAdapter extends RecyclerView.Adapter<SpecificationAdapter.MyViewHolder> {

    Context context;
    List<ProductSpecificationsItem> itemList;

    public SpecificationAdapter(Context context, List<ProductSpecificationsItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_specification, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(itemList.get(i).getSpecificationName());
        myViewHolder.value.setText(itemList.get(i).getSpecificationValue());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, value;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.spec_title);
            value = itemView.findViewById(R.id.spec_value);
            view = itemView;
        }
    }
}
