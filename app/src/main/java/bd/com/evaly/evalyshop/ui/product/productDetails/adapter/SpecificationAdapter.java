package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;

public class SpecificationAdapter extends RecyclerView.Adapter<SpecificationAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> title,value;

    public SpecificationAdapter(Context context, ArrayList<String> title, ArrayList<String> value) {
        this.context = context;
        this.title = title;
        this.value = value;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_specification,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(title.get(i));
        myViewHolder.value.setText(value.get(i));
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title,value;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.spec_title);
            value=itemView.findViewById(R.id.spec_value);
            view = itemView;
        }
    }
}
