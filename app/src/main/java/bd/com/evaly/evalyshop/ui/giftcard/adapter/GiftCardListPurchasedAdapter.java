package bd.com.evaly.evalyshop.ui.giftcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardMyFragment;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardPurchasedFragment;
import bd.com.evaly.evalyshop.util.Utils;

public class GiftCardListPurchasedAdapter extends RecyclerView.Adapter<GiftCardListPurchasedAdapter.MyViewHolder> {

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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gift_card_purchased, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.tv.setText(itemList.get(i).getGiftCard());


        if (itemList.get(i).getGiftCardImage() == null)
            Glide.with(context).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").into(myViewHolder.iv);
        else
            Glide.with(context).load(itemList.get(i).getGiftCardImage()).into(myViewHolder.iv);

        myViewHolder.amount.setText(String.format("৳ %s", Utils.formatePrice(itemList.get(i).getGiftCardPrice())));
        myViewHolder.quantity.setText(String.format("x %d", itemList.get(i).getQuantity()));
        myViewHolder.invoiceId.setText(itemList.get(i).getInvoiceNo());
        myViewHolder.giftFrom.setText(itemList.get(i).getFrom());

        if (itemList.get(i).getGiftCardStatus().equals("active") && itemList.get(i).getPaymentStatus().equals("paid")) {
            myViewHolder.status.setText("Active");
            myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_paid_bg));
        } else {
            myViewHolder.status.setText("Pending");
            myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_pending_bg));
        }

        myViewHolder.button.setOnClickListener(v -> {
            if (type == 0)
                GiftCardPurchasedFragment.getInstance().toggleBottomSheet(itemList.get(i));
            else
                GiftCardMyFragment.getInstance().toggleBottomSheet(itemList.get(i));
        });


        if (type == 0) {
            myViewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.gift_buy_btn));
            myViewHolder.button.setText("PAY");
            myViewHolder.balanceHolder.setVisibility(View.GONE);
            myViewHolder.fromTotext.setText("To");
            myViewHolder.giftTo.setText(itemList.get(i).getTo());

            if (itemList.get(i).getPaymentStatus().equals("paid")) {
                myViewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.gift_card_paid));
                myViewHolder.button.setText("PAID");
                myViewHolder.button.setEnabled(false);
            } else {
                myViewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.gift_buy_btn));
                myViewHolder.button.setText("Pay");
                myViewHolder.button.setEnabled(true);
            }

        } else {

            myViewHolder.button.setBackground(context.getResources().getDrawable(R.drawable.gift_redeem_btn));
            myViewHolder.button.setText("Redeem");
            myViewHolder.balanceHolder.setVisibility(View.VISIBLE);
            myViewHolder.balance.setText(String.format("৳ %s", Utils.formatePrice(itemList.get(i).getAvailableBalance())));
            myViewHolder.fromTotext.setText("From");
            myViewHolder.giftTo.setText(itemList.get(i).getFrom());


            if (itemList.get(i).getGiftCardStatus().equals("pending")) {
                myViewHolder.status.setText("Pending");
                myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_pending_bg));

//                myViewHolder.button.setOnClickListener(view -> {
//                    Toast.makeText(context,"You can redeem the gift card after it's activated", Toast.LENGTH_LONG).show();
//                    return;
//                });

            } else if (itemList.get(i).getGiftCardStatus().equals("active")) {
                myViewHolder.status.setText("Active");
                myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_paid_bg));
            }

            if (itemList.get(i).getAvailableBalance() == 0) {
                myViewHolder.status.setText("Used");
                myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_pending_bg));
                myViewHolder.button.setOnClickListener(view -> {
                    Toast.makeText(context, "This gift card's balance is already used", Toast.LENGTH_LONG).show();
                    return;
                });


            } else if (itemList.get(i).getAvailableBalance() > 0 && itemList.get(i).getGiftCardStatus().equals("active")) {

                myViewHolder.status.setText("Available");
                myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_paid_bg));
                myViewHolder.button.setEnabled(true);
            }

        }


        if (itemList.get(i).getGiftCardStatus().equals("cancelled")) {

            myViewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.gift_card_cancelled));
            myViewHolder.button.setVisibility(View.GONE);

            myViewHolder.status.setText("Cancelled");

        } else {
            myViewHolder.button.setVisibility(View.VISIBLE);
        }


        myViewHolder.time.setText(Utils.formattedDateFromString("", "h:mm a',' d MMM", itemList.get(i).getCreatedAt()));


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv, amount, quantity, invoiceId, giftFrom, giftTo, status, balance, fromTotext, time;
        LinearLayout lin, balanceHolder;
        View view;
        Button button;


        public MyViewHolder(final View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.image);
            tv = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.price);
            invoiceId = itemView.findViewById(R.id.invoice_id);
            giftFrom = itemView.findViewById(R.id.giftFrom);
            giftTo = itemView.findViewById(R.id.giftTo);
            quantity = itemView.findViewById(R.id.quantity);
            status = itemView.findViewById(R.id.status);
            button = itemView.findViewById(R.id.button);
            balanceHolder = itemView.findViewById(R.id.balance_holder);
            balance = itemView.findViewById(R.id.balance);
            fromTotext = itemView.findViewById(R.id.fromToText);
            lin = itemView.findViewById(R.id.lin);
            time = itemView.findViewById(R.id.time);
            view = itemView;
        }
    }
}
