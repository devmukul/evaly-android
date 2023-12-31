package bd.com.evaly.evalyshop.ui.order.orderDetails.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.order.OrderDetailsProducts;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class OrderDetailsProductAdapter extends RecyclerView.Adapter<OrderDetailsProductAdapter.MyViewHolder> {

    Context context;
    ArrayList<OrderDetailsProducts> orderDetailsProducts;

    public OrderDetailsProductAdapter(Context context, ArrayList<OrderDetailsProducts> orderDetailsProducts) {
        this.context = context;
        this.orderDetailsProducts = orderDetailsProducts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_details_products, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.productName.setText(orderDetailsProducts.get(i).getProductName());

        if (orderDetailsProducts.get(i).getVariation().equals(""))
            myViewHolder.variation.setVisibility(View.GONE);
        else
            myViewHolder.variation.setText(orderDetailsProducts.get(i).getVariation());

        myViewHolder.amount.setText(Utils.formatPriceSymbol(orderDetailsProducts.get(i).getAmount()));
        myViewHolder.productQuantity.setText(String.format(Locale.ENGLISH, "%s x %s",
                Utils.formatPriceSymbol(orderDetailsProducts.get(i).getProductRate()), orderDetailsProducts.get(i).getProductQuantity()));

        Glide.with(context).
                load(orderDetailsProducts.get(i).getImageUrl())
                .apply(new RequestOptions().override(300, 300))
                .into(myViewHolder.productImage);

        myViewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewProductActivity.class);
            intent.putExtra("product_slug", orderDetailsProducts.get(i).getProductSlug());
            intent.putExtra("product_name", orderDetailsProducts.get(i).getProductName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailsProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView number, productName, productQuantity, amount;
        ImageView productImage;
        View view;

        TextView variation;

        public MyViewHolder(final View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.price_quan);
            amount = itemView.findViewById(R.id.price);
            productImage = itemView.findViewById(R.id.productImage);
            variation = itemView.findViewById(R.id.variation);
            view = itemView;
        }
    }
}
