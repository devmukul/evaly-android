package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SizeButtonAdapter extends RecyclerView.Adapter<SizeButtonAdapter.SizeViewHolder> {

    private List<AttributeValuesItem> list;
    private Activity context;
    private RecyclerViewOnItemClickListener listener;

    public SizeButtonAdapter(List<AttributeValuesItem> list, Activity context, RecyclerViewOnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SizeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_variation_size, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        AttributeValuesItem value = list.get(position);

        holder.tvSize.setText(value.getValue());
        if (value.isSelected()) {
            holder.cardSize.setCardBackgroundColor(context.getResources().getColor(R.color.color4));
        } else {
            holder.cardSize.setCardBackgroundColor(context.getResources().getColor(R.color.gray_border));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SizeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSize)
        TextView tvSize;
        @BindView(R.id.cardSize)
        CardView cardSize;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                listener.onRecyclerViewItemClicked(list.get(getLayoutPosition()));
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                }
                list.get(getLayoutPosition()).setSelected(true);
                notifyDataSetChanged();
            });
        }
    }
}
