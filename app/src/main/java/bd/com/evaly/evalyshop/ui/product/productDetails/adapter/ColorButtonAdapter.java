package bd.com.evaly.evalyshop.ui.product.productDetails.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.product.productDetails.AttributeValuesItem;

public class ColorButtonAdapter extends RecyclerView.Adapter<ColorButtonAdapter.ColorViewHolder> {

    private List<AttributeValuesItem> list;
    private List<String> colorList;
    private Activity context;
    private RecyclerViewOnItemClickListener listener;

    public ColorButtonAdapter(List<AttributeValuesItem> list, Activity context, RecyclerViewOnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorViewHolder(LayoutInflater.from(context).inflate(R.layout.item_variation_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        AttributeValuesItem value = list.get(position);

        Glide.with(context)
                .load(value.getColor_image())
                .into(holder.image);

        if (value.isSelected())
            holder.holder.setBackground(context.getResources().getDrawable(R.drawable.variation_brd_selected));
        else
            holder.holder.setBackground(context.getResources().getDrawable(R.drawable.variation_brd));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        RelativeLayout holder;
        TextView text;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);


            holder = itemView.findViewById(R.id.holder);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);


            itemView.setOnClickListener(view -> {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                }
                list.get(getLayoutPosition()).setSelected(true);
                notifyDataSetChanged();

                listener.onRecyclerViewItemClicked(list.get(getLayoutPosition()));
            });
        }
    }
}
