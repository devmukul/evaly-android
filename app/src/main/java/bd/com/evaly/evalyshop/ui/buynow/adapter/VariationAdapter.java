package bd.com.evaly.evalyshop.ui.buynow.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shopItem.ShopItem;

public class VariationAdapter extends RecyclerView.Adapter<VariationAdapter.MyViewHolder>{

    private ArrayList<ShopItem> itemsList;
    private Context context;
    private ClickListenerVariation clickListenerVariation;

    public interface ClickListenerVariation {

        public void selectVariation(int position);
    }

    public VariationAdapter(ArrayList<ShopItem> itemsList, Context context, ClickListenerVariation clickListenerVariation) {
        this.itemsList = itemsList;
        this.context = context;
        this.clickListenerVariation = clickListenerVariation;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_variation_image,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


        if (itemsList.get(i).getAttributes().size() > 0)
            myViewHolder.text.setText(itemsList.get(i).getAttributes().get(0).getValue());


        Glide.with(context)
                .load(itemsList.get(i).getShopItemImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_image_placeholder)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.image);

        if (itemsList.get(i).isSelected())
            myViewHolder.holder.setBackground(context.getResources().getDrawable(R.drawable.variation_brd_selected));
        else
            myViewHolder.holder.setBackground(context.getResources().getDrawable(R.drawable.variation_brd));


        myViewHolder.holder.setOnClickListener(view -> {

            clickListenerVariation.selectVariation(i);

        });


    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time,status,note;
        ImageView image;
        RelativeLayout holder;
        TextView text;


        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);

            holder = itemView.findViewById(R.id.holder);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);

            view = itemView;


        }
    }
}