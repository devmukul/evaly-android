package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.activity.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.models.Notifications;
import bd.com.evaly.evalyshop.util.Utils;

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
        ImageView shopImage;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            messages=itemView.findViewById(R.id.message);
            time=itemView.findViewById(R.id.time);
            shopImage=itemView.findViewById(R.id.shop_image);
            view = itemView;
        }
    }

    private double colorDistance(int a, int b) {
        return Math.sqrt(Math.pow(Color.red(a) - Color.red(b), 2) + Math.pow(Color.blue(a) - Color.blue(b), 2) + Math.pow(Color.green(a) - Color.green(b), 2));
    }


    public Bitmap changeColor(Bitmap src, int fromColor, int targetColor) {
        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {

            if(colorDistance(pixels[x], fromColor) < 10)
                pixels[x] = targetColor;

        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }
}
