package bd.com.evaly.evalyshop.ui.product.productList.adapter;

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

import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.models.product.Products;

public class ViewProductAdapter extends RecyclerView.Adapter<ViewProductAdapter.MyViewHolder>{

    Context context;
    ArrayList<Products> products;

    public ViewProductAdapter(Context context, ArrayList<Products> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_product_grid_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(products.get(position).getName().contains("-")){
            String[] s=products.get(position).getName().split("-");
            String str = "";
            for(int m=0;m<s.length-1;m++){
                str+=s[m];
            }
            holder.title.setText(str);
        }else
            holder.title.setText(products.get(position).getName());

        Glide.with(context)
                .asBitmap()
                .load(products.get(position).getThumbnailSM())
                .into(holder.image);

        holder.price.setText(String.format(Locale.ENGLISH, "৳ %d", products.get(position).getPriceMin()));

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title,price,sku;
        ImageView image;

        public MyViewHolder(final View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            price=itemView.findViewById(R.id.price);
            image= itemView.findViewById(R.id.image);
            //sku=itemView.findViewById(R.id.sku);
            itemView.setOnClickListener(view -> context.startActivity(new Intent(context, MainActivity.class)));
        }
    }
}
