package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.models.OrderDetailsProducts;

public class OrderDetailsProductAdapter extends RecyclerView.Adapter<OrderDetailsProductAdapter.MyViewHolder>{

    Context context;
    ArrayList<OrderDetailsProducts> orderDetailsProducts;

    public OrderDetailsProductAdapter(Context context, ArrayList<OrderDetailsProducts> orderDetailsProducts) {
        this.context = context;
        this.orderDetailsProducts = orderDetailsProducts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_details_products,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.number.setText(""+(i+1));

        myViewHolder.productName.setText(orderDetailsProducts.get(i).getProductName());


        if(orderDetailsProducts.get(i).getVariation().equals("")){
            myViewHolder.variation.setVisibility(View.GONE);
        }else{
            myViewHolder.variation.setText(orderDetailsProducts.get(i).getVariation());
        }




        myViewHolder.amount.setText(String.format("%.2f",Double.parseDouble(orderDetailsProducts.get(i).getAmount())));
        myViewHolder.productQuantity.setText(orderDetailsProducts.get(i).getProductRate()+" x "+orderDetailsProducts.get(i).getProductQuantity());
        Glide.with(context).
                load(orderDetailsProducts.get(i).getImageUrl())
                .apply(new RequestOptions().override(300, 300))
                .into(myViewHolder.productImage);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewProductActivity.class);

                intent.putExtra("product_slug",orderDetailsProducts.get(i).getProductSlug());
                intent.putExtra("product_name",orderDetailsProducts.get(i).getProductName());
                context.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return orderDetailsProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView number,productName,productQuantity,amount;
        ImageView productImage;
        View view;

        TextView variation;
        public MyViewHolder(final View itemView) {
            super(itemView);
            number=itemView.findViewById(R.id.number);
            productName=itemView.findViewById(R.id.productName);
            productQuantity=itemView.findViewById(R.id.price_quan);
            amount=itemView.findViewById(R.id.price);
            productImage=itemView.findViewById(R.id.productImage);

            variation = itemView.findViewById(R.id.variation);
            view = itemView;
        }
    }
}
