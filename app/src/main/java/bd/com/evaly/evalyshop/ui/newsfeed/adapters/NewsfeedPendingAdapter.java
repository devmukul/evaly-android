package bd.com.evaly.evalyshop.ui.newsfeed.adapters;


import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedPendingFragment;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.Utils;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class NewsfeedPendingAdapter extends RecyclerView.Adapter<NewsfeedPendingAdapter.MyViewHolder> {

    ArrayList<NewsfeedItem> itemsList;
    Context context;
    NewsfeedPendingFragment fragment;

    public NewsfeedPendingAdapter(ArrayList<NewsfeedItem> itemsList, Context context, NewsfeedPendingFragment fragment) {
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NewsfeedPendingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_newsfeed_list_confirmation, viewGroup, false);
        return new NewsfeedPendingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedPendingAdapter.MyViewHolder myViewHolder, int i) {
        NewsfeedItem model = itemsList.get(i);
        myViewHolder.userNameView.setText(model.getAuthorFullName());
        if (model.getAuthorFullName().trim().equals(""))
            myViewHolder.userNameView.setText("User");

        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", model.getUpdatedAt())));
        myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(model.getBody(), 180, "... <b>Show more</b>")));

        myViewHolder.statusView.setOnClickListener(view -> {
            if (myViewHolder.statusView.getText().toString().contains("Show more"))
                myViewHolder.statusView.setText(model.getBody());
            else
                myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(model.getBody(), 180, "... <b>Show more</b>")));
        });

        if (isJSONValid(model.getBody())) {
            try {
                JSONObject object = new JSONObject(model.getBody());
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
                if (body.equalsIgnoreCase("")) {
                    myViewHolder.statusView.setVisibility(View.GONE);
                } else {
                    myViewHolder.statusView.setVisibility(View.VISIBLE);
                    myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(body, 180, "... <b>Show more</b>")));
                }
                myViewHolder.cardLink.setVisibility(View.VISIBLE);
                myViewHolder.cardLink.setOnClickListener(view -> {
                    Logger.json(object.toString());
                    try {
                        context.startActivity(new Intent(new Intent(context, ViewProductActivity.class))
                                .putExtra("product_slug", object.getString("url").replace(UrlUtils.PRODUCT_BASE_URL, "")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            myViewHolder.statusView.setVisibility(View.VISIBLE);
            myViewHolder.cardLink.setVisibility(View.GONE);
            myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(model.getBody(), 180, "... <b>Show more</b>")));
        }

        Glide.with(context)
                .load(model.getAuthorImage())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .placeholder(R.drawable.user_image)
                .into(myViewHolder.userImage);

        String postImage = model.getAttachment();

        if (postImage.equals("null")) 
            myViewHolder.postImage.setVisibility(View.GONE);
        else {

            myViewHolder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(model.getAttachmentCompressed())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .fitCenter()
                    .apply(new RequestOptions().override(900, 900))
                    .into(myViewHolder.postImage);

            myViewHolder.postImage.setOnClickListener(view -> {
                Intent intent = new Intent(context, ImagePreview.class);
                intent.putExtra("image", model.getAttachment());
                context.startActivity(intent);
            });
        }
        myViewHolder.approveHolder.setOnClickListener(view -> fragment.action(model.getSlug(), "approve", i));
        myViewHolder.deleteHolder.setOnClickListener(view -> fragment.action(model.getSlug(), "delete", i));
        myViewHolder.rejectHolder.setOnClickListener(view -> fragment.action(model.getSlug(), "reject", i));
    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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

}
