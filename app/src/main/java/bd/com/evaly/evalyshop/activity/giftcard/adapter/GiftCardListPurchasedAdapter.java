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
import bd.com.evaly.evalyshop.activity.giftcard.GiftCardPurchasedFragment;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;

public class GiftCardListPurchasedAdapter extends RecyclerView.Adapter<GiftCardListPurchasedAdapter.MyViewHolder>{

    Context context;
    ArrayList<GiftCardListPurchasedItem> itemList;

    public GiftCardListPurchasedAdapter(Context context, ArrayList<GiftCardListPurchasedItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gift_card_purchased,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.tv.setText(itemList.get(i).getGiftCard());


        if (itemList.get(i).getGiftCardImage() == null)
            Glide.with(context).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").placeholder(R.drawable.ic_placeholder_small).into(myViewHolder.iv);
        else
            Glide.with(context).load(itemList.get(i).getGiftCardImage()).placeholder(R.drawable.ic_placeholder_small).into(myViewHolder.iv);

        myViewHolder.amount.setText("৳ " + itemList.get(i).getGiftCardPrice());
        myViewHolder.quantity.setText("x "+itemList.get(i).getQuantity());
        myViewHolder.invoiceId.setText(itemList.get(i).getInvoiceNo());
        myViewHolder.giftFrom.setText(itemList.get(i).getFrom());
        myViewHolder.giftTo.setText(itemList.get(i).getTo());


        myViewHolder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               GiftCardPurchasedFragment.getInstance().toggleBottomSheet(itemList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv;
        TextView tv,amount,quantity, invoiceId, giftFrom, giftTo;
        LinearLayout lin;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            iv= itemView.findViewById(R.id.image);
            tv=itemView.findViewById(R.id.name);
            amount=itemView.findViewById(R.id.price);

            invoiceId=itemView.findViewById(R.id.invoice_id);
            giftFrom=itemView.findViewById(R.id.giftFrom);
            giftTo=itemView.findViewById(R.id.giftTo);
            quantity = itemView.findViewById(R.id.quantity);


            lin=itemView.findViewById(R.id.lin);
            view=itemView;
        }
    }
}
