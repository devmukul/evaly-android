package bd.com.evaly.evalyshop.activity.giftcard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.giftcard.GiftCardMyFragment;
import bd.com.evaly.evalyshop.activity.giftcard.GiftCardPurchasedFragment;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.util.Utils;

public class GiftCardListPurchasedAdapter extends RecyclerView.Adapter<GiftCardListPurchasedAdapter.MyViewHolder>{

    Context context;
    ArrayList<GiftCardListPurchasedItem> itemList;
    private int type = 0;

    public GiftCardListPurchasedAdapter(Context context, ArrayList<GiftCardListPurchasedItem> itemList, int type) {
        this.context = context;
        this.itemList = itemList;
        this.type = type;
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
        myViewHolder.status.setText(Utils.toFirstCharUpperAll(itemList.get(i).getPaymentStatus()));

        if (itemList.get(i).getPaymentStatus().equals("paid"))
            myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_paid_bg));
        else
            myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_pending_bg));


        myViewHolder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type==0)
                    GiftCardPurchasedFragment.getInstance().toggleBottomSheet(itemList.get(i));
                else
                    GiftCardMyFragment.getInstance().toggleBottomSheet(itemList.get(i));
            }
        });


        if (type==0) {
            myViewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.gift_buy_btn));
            myViewHolder.button.setText("PAY");
        }
        else {
            myViewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.gift_redeem_btn));
            myViewHolder.button.setText("Redeem");
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv;
        TextView tv,amount,quantity, invoiceId, giftFrom, giftTo, status;
        LinearLayout lin;
        View view;
        Button button;

        public MyViewHolder(final View itemView) {
            super(itemView);
            iv= itemView.findViewById(R.id.image);
            tv=itemView.findViewById(R.id.name);
            amount=itemView.findViewById(R.id.price);

            invoiceId=itemView.findViewById(R.id.invoice_id);
            giftFrom=itemView.findViewById(R.id.giftFrom);
            giftTo=itemView.findViewById(R.id.giftTo);
            quantity = itemView.findViewById(R.id.quantity);
            status = itemView.findViewById(R.id.status);
            button = itemView.findViewById(R.id.button);


            lin=itemView.findViewById(R.id.lin);
            view=itemView;
        }
    }
}
