package bd.com.evaly.evalyshop.adapter;

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
import bd.com.evaly.evalyshop.fragment.VoucherListFragment;
import bd.com.evaly.evalyshop.models.VoucherDetails;

public class VoucherDetailsAdapter extends RecyclerView.Adapter<VoucherDetailsAdapter.MyViewHolder>{

    Context context;
    ArrayList<VoucherDetails> voucherDetails;

    public VoucherDetailsAdapter(Context context, ArrayList<VoucherDetails> voucherDetails) {
        this.context = context;
        this.voucherDetails = voucherDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_voucher_list,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tv.setText(voucherDetails.get(i).getName());
        myViewHolder.amount.setText("৳ "+voucherDetails.get(i).getAmount());
        Glide.with(context).load(voucherDetails.get(i).getThumnailImage()).placeholder(R.drawable.ic_placeholder_small).into(myViewHolder.iv);

        myViewHolder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                VoucherListFragment.getInstance().toggleBottomSheet(voucherDetails.get(i).getSlug());
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherDetails.size();
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
