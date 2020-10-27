package bd.com.evaly.evalyshop.ui.newsfeed.notification.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.ui.newsfeed.comment.CommentBottomSheet;
import bd.com.evaly.evalyshop.util.Utils;


public class NewsfeedNotificationAdapter extends RecyclerView.Adapter<NewsfeedNotificationAdapter.MyViewHolder> {

    private List<NotificationItem> notifications;
    private Context context;
    private FragmentManager fragmentManager;

    public NewsfeedNotificationAdapter(List<NotificationItem> notifications, Context context, FragmentManager fragmentManager) {
        this.notifications = notifications;
        this.context = context;
        this.fragmentManager = fragmentManager;
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
            myViewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.fafafa));
        else
            myViewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.fff));

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

            JsonObject metaData = new Gson().fromJson(notifications.get(i).getMetaData(), JsonObject.class);

            String postSlug = metaData.get("post_slug").getAsString();

            if (metaData.has("reply_id")) {

                int commentId = metaData.get("comment_id").getAsInt();
                int replyId = metaData.get("reply_id").getAsInt();

                NewsfeedPost newsfeedPost = new NewsfeedPost();
                newsfeedPost.setSlug(postSlug);

                CommentItem commentItem = new CommentItem();
                commentItem.setId(commentId);

                CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(newsfeedPost);
                commentBottomSheet.show(fragmentManager, postSlug + "_" + commentId);

//                ReplyBottomSheet replyBottomSheet = ReplyBottomSheet.newInstance(commentItem, postSlug);
//                replyBottomSheet.show(fragmentManager, postSlug + "_" + commentId + "_" + replyId);

            } else if (metaData.has("comment_id")) {

                int commentId = metaData.get("comment_id").getAsInt();

                NewsfeedPost newsfeedPost = new NewsfeedPost();
                newsfeedPost.setSlug(postSlug);

                CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(newsfeedPost);
                commentBottomSheet.show(fragmentManager, postSlug + "_" + commentId);

            } else {

                NewsfeedPost newsfeedPost = new NewsfeedPost();
                newsfeedPost.setSlug(postSlug);
                CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(newsfeedPost);
                commentBottomSheet.show(fragmentManager, postSlug);

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
