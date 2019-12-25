package bd.com.evaly.evalyshop.ui.transaction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.util.Utils;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder>{

    ArrayList<TransactionItem> itemList;
    Context context;

    public TransactionHistoryAdapter(ArrayList<TransactionItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transaction,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.messages.setText(itemList.get(i).getEvent());

        myViewHolder.time.setText(Utils.formattedDateFromString("","hh:mm aa - d',' MMMM", itemList.get(i).getDate_time()));

        myViewHolder.amount.setText(String.format(Locale.ENGLISH, "৳ %d", itemList.get(i).getAmount()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView messages, time, amount;
        ImageView shopImage;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            messages=itemView.findViewById(R.id.message);
            time=itemView.findViewById(R.id.time);
            shopImage=itemView.findViewById(R.id.shop_image);
            amount = itemView.findViewById(R.id.amount);
            view = itemView;
        }
    }

}
