package bd.com.evaly.evalyshop.ui.express.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;

public class ExpressDistrictAdapter extends RecyclerView.Adapter<ExpressDistrictAdapter.MyViewHolder> {

    private List<String> itemList;
    private RecyclerViewOnItemClickListener<String> listener;

    public ExpressDistrictAdapter(ArrayList<String> itemList, RecyclerViewOnItemClickListener<String> listener) {
        this.listener = listener;
        this.itemList = itemList;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public ExpressDistrictAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_list, parent, false);

        return new ExpressDistrictAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpressDistrictAdapter.MyViewHolder holder, int position) {
        holder.tv.setText(itemList.get(position));
    }

    public void clear() {
        if (itemList != null) {
            itemList.clear();
            notifyDataSetChanged();
        }
    }

    public void setFilter(ArrayList<String> itemList){
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(v -> {
                listener.onRecyclerViewItemClicked(
                        itemList.get(getLayoutPosition())
                );
            });
        }
    }


}
