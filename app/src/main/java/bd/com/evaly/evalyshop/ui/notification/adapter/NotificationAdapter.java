package bd.com.evaly.evalyshop.ui.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.notification.Notifications;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    ArrayList<Notifications> notifications;
    Context context;

    public NotificationAdapter(ArrayList<Notifications> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (!notifications.get(i).isRead())
            myViewHolder.messages.setTypeface(null, Typeface.BOLD);
        else
            myViewHolder.messages.setTypeface(null, Typeface.NORMAL);

        myViewHolder.messages.setText(notifications.get(i).getMessage());


        Glide.with(context)
                .load(notifications.get(i).getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.shopImage);


        myViewHolder.view.setOnClickListener(v -> {

            if (notifications.get(i).getContent_type().equals("order")){

                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderID", notifications.get(i).getContent_url());
                context.startActivity(intent);

            } else if (notifications.get(i).getContent_type().equals("shop")){

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra("shop_slug", notifications.get(i).getContent_url());
                intent.putExtra("shop_name", notifications.get(i).getContent_url());
                context.startActivity(intent);

            } else if (notifications.get(i).getContent_type().equals("product")) {

                Intent intent=new Intent(context, ViewProductActivity.class);
                intent.putExtra("product_slug",notifications.get(i).getContent_url());
                intent.putExtra("product_name",notifications.get(i).getContent_url());
                context.startActivity(intent);
            }

        });


        myViewHolder.time.setText(Utils.formattedDateFromString("","hh:mm aa - d',' MMMM", notifications.get(i).getTime()));

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView messages, time;
        CircleImageView shopImage;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            messages=itemView.findViewById(R.id.message);
            time=itemView.findViewById(R.id.time);
            shopImage=itemView.findViewById(R.id.shop_image);
            view = itemView;
        }
    }


}
