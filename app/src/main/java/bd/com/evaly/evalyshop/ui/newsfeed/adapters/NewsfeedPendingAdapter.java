package bd.com.evaly.evalyshop.ui.newsfeed.adapters;


import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ImagePreview;
import bd.com.evaly.evalyshop.activity.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedPendingFragment;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class NewsfeedPendingAdapter extends RecyclerView.Adapter<NewsfeedPendingAdapter.MyViewHolder>{

    ArrayList<NewsfeedItem> itemsList;
    Context context;
    NewsfeedPendingFragment fragment;
    UserDetails userDetails;

    public NewsfeedPendingAdapter(ArrayList<NewsfeedItem> itemsList, Context context, NewsfeedPendingFragment fragment) {
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
        this.userDetails = fragment.getUserDetails();
    }

    @NonNull
    @Override
    public NewsfeedPendingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_newsfeed_list_confirmation,viewGroup,false);
        return new NewsfeedPendingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedPendingAdapter.MyViewHolder myViewHolder, int i) {


        myViewHolder.userNameView.setText(itemsList.get(i).getAuthorFullName());

        if (itemsList.get(i).getAuthorFullName().trim().equals(""))
            myViewHolder.userNameView.setText("User");

        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS","hh:mm aa - d',' MMMM", itemsList.get(i).getUpdatedAt())));

        myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));

        myViewHolder.statusView.setOnClickListener(view -> {

            if (myViewHolder.statusView.getText().toString().contains("Show more"))
                myViewHolder.statusView.setText(itemsList.get(i).getBody());
            else
                myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));

        });

        if (isJSONValid(itemsList.get(i).getBody())) {
            try {
                JSONObject object = new JSONObject(itemsList.get(i).getBody());
                myViewHolder.linkPreview.setLink(object.getString("url"), new ViewListener() {
                    @Override
                    public void onSuccess(boolean status) {
                        Logger.d("Success");
                    }

                    @Override
                    public void onError(Exception e) {
                        Logger.e(e.getMessage());
                    }
                });

                String body = object.getString("body");
                if (body == null || body.equalsIgnoreCase("")){
                    myViewHolder.statusView.setVisibility(View.GONE);
                }else {
                    myViewHolder.statusView.setVisibility(View.VISIBLE);
                    myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(body, 180, "... <b>Show more</b>")));
                }
                myViewHolder.cardLink.setVisibility(View.VISIBLE);
                myViewHolder.cardLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Logger.json(object.toString());
                        try {
                            context.startActivity(new Intent(new Intent(context, ViewProductActivity.class))
                                    .putExtra("product_slug", object.getString("url").replace(UrlUtils.PRODUCT_BASE_URL, "")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            myViewHolder.statusView.setVisibility(View.VISIBLE);
            myViewHolder.cardLink.setVisibility(View.GONE);
            myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));
        }

        Glide.with(context)
                .load(itemsList.get(i).getAuthorImage())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .placeholder(R.drawable.user_image)
                .into(myViewHolder.userImage);

        String postImage = itemsList.get(i).getAttachment();

        if (postImage.equals("null")){

            myViewHolder.postImage.setVisibility(View.GONE);

        } else {

            myViewHolder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(itemsList.get(i).getAttachmentCompressed())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .fitCenter()
                    .apply(new RequestOptions().override(900, 900))
                    .into(myViewHolder.postImage);

            myViewHolder.postImage.setOnClickListener(view -> {

                Intent intent = new Intent(context, ImagePreview.class);
                intent.putExtra("image", itemsList.get(i).getAttachment());
                context.startActivity(intent);
            });
        }


        myViewHolder.approveHolder.setOnClickListener(view -> fragment.action(itemsList.get(i).getSlug(), "approve", i));
        myViewHolder.deleteHolder.setOnClickListener(view -> fragment.action(itemsList.get(i).getSlug(), "delete", i));
        myViewHolder.rejectHolder.setOnClickListener(view -> fragment.action(itemsList.get(i).getSlug(), "reject", i));

    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userNameView, timeView, statusView;
        ImageView userImage, postImage;
        View view;
        LinearLayout deleteHolder, rejectHolder, approveHolder;
        RichLinkView linkPreview;
        CardView cardLink;

        public MyViewHolder(final View itemView) {
            super(itemView);

            userNameView = itemView.findViewById(R.id.user_name);
            timeView = itemView.findViewById(R.id.date);
            statusView = itemView.findViewById(R.id.text);

            userImage = itemView.findViewById(R.id.picture);
            postImage = itemView.findViewById(R.id.postImage);

            deleteHolder = itemView.findViewById(R.id.deleteHolder);
            rejectHolder = itemView.findViewById(R.id.rejectHolder);
            approveHolder = itemView.findViewById(R.id.approveHolder);
            cardLink = itemView.findViewById(R.id.cardLink);
            linkPreview = itemView.findViewById(R.id.linkPreview);

            view = itemView;
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}
