package bd.com.evaly.evalyshop.ui.newsfeed.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
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

import org.json.JSONObject;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.util.Utils;


public class NewsfeedNotificationAdapter extends RecyclerView.Adapter<NewsfeedNotificationAdapter.MyViewHolder> {

    private List<NotificationItem> notifications;
    private Context context;

    public NewsfeedNotificationAdapter(List<NotificationItem> notifications, Context context) {
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
            myViewHolder.view.setBackgroundColor(Color.parseColor("#fafafa"));
        else
            myViewHolder.view.setBackgroundColor(Color.parseColor("#ffffff"));

        myViewHolder.messages.setText(Html.fromHtml(notifications.get(i).getMessage()));

        Glide.with(context)
                .load(notifications.get(i).getThumbUrl())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .placeholder(R.drawable.user_image)
                .into(myViewHolder.shopImage);


        myViewHolder.view.setOnClickListener(v -> {

            try {

                JSONObject metaData = new JSONObject(notifications.get(i).getMetaData());

                Intent intent = new Intent();

                if (metaData.has("post_slug"))
                    intent.putExtra("status_id", metaData.getString("post_slug"));
                else
                    intent.putExtra("status_id", "");

                if (metaData.has("comment_id"))
                    intent.putExtra("comment_id", metaData.getString("comment_id"));
                else
                    intent.putExtra("comment_id", "");

//
//                if (notifications.get(i).getContent_type().equals("comment") || notifications.get(i).getContent_type().equals("reply")) {
//                    ((NewsfeedNotification) context).setResult(Activity.RESULT_OK, intent);
//                    ((NewsfeedNotification) context).finish();
//                } else {
//
//                    ((NewsfeedNotification) context).finish();
//                }


            } catch (Exception e) {

            }

        });


        myViewHolder.time.setText(Utils.formattedDateFromString("", "hh:mm aa - d',' MMMM", notifications.get(i).getCreatedAt()));

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messages, time;
        ImageView shopImage;
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
