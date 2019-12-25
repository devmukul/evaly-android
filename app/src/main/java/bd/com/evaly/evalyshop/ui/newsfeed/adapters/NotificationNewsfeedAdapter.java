package bd.com.evaly.evalyshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedNotification;
import bd.com.evaly.evalyshop.models.notification.Notifications;
import bd.com.evaly.evalyshop.util.Utils;

public class NotificationNewsfeedAdapter extends RecyclerView.Adapter<NotificationNewsfeedAdapter.MyViewHolder>{

    ArrayList<Notifications> notifications;
    Context context;

    public NotificationNewsfeedAdapter(ArrayList<Notifications> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_newfeed,viewGroup,false);
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
                .load(notifications.get(i).getImageURL())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .placeholder(R.drawable.user_image)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  return false;
                              }
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Bitmap bitmap = changeColor(((BitmapDrawable)resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                  myViewHolder.shopImage.setImageBitmap(bitmap);
                                  return true;
                              }
                          }
                )
                .into(myViewHolder.shopImage);


        myViewHolder.view.setOnClickListener(v -> {

            try {

                JSONObject metaData = new JSONObject(notifications.get(i).getMeta_data());

                Intent intent = new Intent();

                if (metaData.has("post_slug"))
                    intent.putExtra("status_id", metaData.getString("post_slug"));
                else
                    intent.putExtra("status_id", "");

                if (metaData.has("comment_id"))
                    intent.putExtra("comment_id", metaData.getString("comment_id"));
                else
                    intent.putExtra("comment_id", "");


                if (notifications.get(i).getContent_type().equals("comment") || notifications.get(i).getContent_type().equals("reply")) {
                    ((NewsfeedNotification) context).setResult(Activity.RESULT_OK, intent);
                    ((NewsfeedNotification) context).finish();
                } else {

                    ((NewsfeedNotification) context).finish();
                }


            }catch (Exception e){

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
