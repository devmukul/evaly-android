package bd.com.evaly.evalyshop.ui.giftcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardListFragment;

public class GiftCardListAdapter extends RecyclerView.Adapter<GiftCardListAdapter.MyViewHolder>{

    Context context;
    ArrayList<GiftCardListItem> itemList;

    public GiftCardListAdapter(Context context, ArrayList<GiftCardListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gift_card,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tv.setText(itemList.get(i).getName());


        if (itemList.get(i).getImageUrl() == null)
            Glide.with(context).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").into(myViewHolder.iv);
        else
            Glide.with(context).load(itemList.get(i).getImageUrl()).into(myViewHolder.iv);

        myViewHolder.amount.setText("৳ " + itemList.get(i).getPrice());
        myViewHolder.button.setOnClickListener(v -> GiftCardListFragment.getInstance().toggleBottomSheet(itemList.get(i)));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv;
        TextView tv,amount;
        LinearLayout lin;
        View view;
        Button button;

        public MyViewHolder(final View itemView) {
            super(itemView);
            iv= itemView.findViewById(R.id.image);
            tv=itemView.findViewById(R.id.name);
            amount=itemView.findViewById(R.id.price);
            lin=itemView.findViewById(R.id.lin);
            button = itemView.findViewById(R.id.button);
            view=itemView;
        }
    }
}
