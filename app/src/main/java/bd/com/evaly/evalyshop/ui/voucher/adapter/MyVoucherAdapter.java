package bd.com.evaly.evalyshop.ui.voucher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.voucher.MyVoucherInfo;
import bd.com.evaly.evalyshop.util.Utils;

public class MyVoucherAdapter extends RecyclerView.Adapter<MyVoucherAdapter.MyViewHolder>{

    Context context;
    ArrayList<MyVoucherInfo> myVoucherInfos;

    public MyVoucherAdapter(Context context, ArrayList<MyVoucherInfo> myVoucherInfos) {
        this.context = context;
        this.myVoucherInfos = myVoucherInfos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_voucher_list,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        try{
            myViewHolder.orderID.setText(myVoucherInfos.get(i).getInvoiceNumber());

            try {
                myViewHolder.orderDate.setText(Utils.formattedDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","dd MMM, hh:mm a", myVoucherInfos.get(i).getDate()));
            } catch (Exception e) {
                myViewHolder.orderDate.setText( myVoucherInfos.get(i).getDate());
                e.printStackTrace();
            }

            try {
                myViewHolder.applicableDate.setText(Utils.formattedDateFromString("yyyy-MM-dd'T'HH:mm:ss'Z'","dd MMM, hh:mm a", myVoucherInfos.get(i).getApplicableDate()));
            } catch (Exception e) {
                myViewHolder.applicableDate.setText(myVoucherInfos.get(i).getApplicableDate());
                e.printStackTrace();
            }

            myViewHolder.quantity.setText(String.format(Locale.ENGLISH, "TK. %d x %d", myVoucherInfos.get(i).getAmount(), myVoucherInfos.get(i).getQuantity()));
            myViewHolder.status.setText(myVoucherInfos.get(i).getApplyStatus());
            myViewHolder.holdingAmount.setText(String.format(Locale.ENGLISH,"%d", myVoucherInfos.get(i).getClaimAmount()));
            myViewHolder.totalPrice.setText(String.format(Locale.ENGLISH, "%d BDT", myVoucherInfos.get(i).getTotalPrice()));
            myViewHolder.totalPaid.setText(String.format(Locale.ENGLISH, "%d BDT", myVoucherInfos.get(i).getTotalPaid()));
            myViewHolder.paymentStatus.setText(myVoucherInfos.get(i).getPaymentStatus());
            if(myVoucherInfos.get(i).getPaymentStatus().equals("Submitted") || myVoucherInfos.get(i).getPaymentStatus().equals("Paid")){
                myViewHolder.pay.setVisibility(View.GONE);
            }else{
                myViewHolder.pay.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(myVoucherInfos.get(i).getVoucherImage()).placeholder(R.drawable.ic_placeholder_small).into(myViewHolder.voucherImage);
            myViewHolder.pay.setOnClickListener(v -> {
                String str[]=myViewHolder.totalPrice.getText().toString().split(" ");
                int amountPay=Integer.parseInt(str[0]);
                Toast.makeText(context,"Payment time is over", Toast.LENGTH_SHORT).show();

            });
        }catch(Exception e){
            //Toast.makeText(context, "Sorry something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return myVoucherInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        View view;
        ImageView voucherImage;
        TextView orderDate,applicableDate,orderID,quantity,status,holdingAmount,totalPrice,totalPaid,paymentStatus;
        Button pay;

        public MyViewHolder(final View itemView) {
            super(itemView);
            voucherImage=itemView.findViewById(R.id.voucherImage);
            orderDate=itemView.findViewById(R.id.orderDate);
            applicableDate=itemView.findViewById(R.id.applicableDate);
            orderID=itemView.findViewById(R.id.orderID);
            quantity=itemView.findViewById(R.id.quantity);
            status=itemView.findViewById(R.id.status);
            holdingAmount=itemView.findViewById(R.id.holdingAmount);
            totalPrice=itemView.findViewById(R.id.total_price);
            totalPaid=itemView.findViewById(R.id.total_paid);
            paymentStatus=itemView.findViewById(R.id.paymentStatus);
            pay=itemView.findViewById(R.id.pay);
            view=itemView;
        }
    }
}