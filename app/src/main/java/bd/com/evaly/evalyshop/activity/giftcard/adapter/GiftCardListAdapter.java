package bd.com.evaly.evalyshop.activity.giftcard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.giftcard.GiftCardListFragment;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;

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
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_voucher_list,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tv.setText(itemList.get(i).getName());
        Glide.with(context).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").placeholder(R.drawable.ic_placeholder_small).into(myViewHolder.iv);

        myViewHolder.amount.setText("৳ " + itemList.get(i).getPrice());

        myViewHolder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GiftCardListFragment.getInstance().toggleBottomSheet(itemList.get(i));
            }
        });
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

        public MyViewHolder(final View itemView) {
            super(itemView);
            iv= itemView.findViewById(R.id.voucher_image);
            tv=itemView.findViewById(R.id.voucher_name);
            amount=itemView.findViewById(R.id.voucher_amount);
            lin=itemView.findViewById(R.id.lin);
            view=itemView;
        }
    }
}
