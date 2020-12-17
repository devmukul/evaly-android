package bd.com.evaly.evalyshop.ui.notification.adapter;

import android.content.Context;
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
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private ArrayList<NotificationItem> notifications;
    private Context context;
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(String url);
    }

    public NotificationAdapter(ArrayList<NotificationItem> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup, false);
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
                .load(notifications.get(i).getThumbUrl())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.shopImage);

        myViewHolder.view.setOnClickListener(v -> {
            clickListener.onClick(notifications.get(i).getWebUrl());
        });

        myViewHolder.time.setText(Utils.formattedDateFromString("", "hh:mm aa - d',' MMMM", notifications.get(i).getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messages, time;
        CircleImageView shopImage;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            messages = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            shopImage = itemView.findViewById(R.id.shop_image);
            view = itemView;
        }
    }


}
