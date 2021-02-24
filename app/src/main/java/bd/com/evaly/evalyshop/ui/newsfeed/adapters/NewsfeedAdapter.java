package bd.com.evaly.evalyshop.ui.newsfeed.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedFragment;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.Utils;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.MyViewHolder> {

    ArrayList<NewsfeedItem> itemsList;
    Context context;
    NewsfeedFragment fragment;
    NewsFeedShareListener listener;

    public NewsfeedAdapter(ArrayList<NewsfeedItem> itemsList, Context context, NewsfeedFragment fragment, NewsFeedShareListener listener) {
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsfeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_newsfeed_list, viewGroup, false);
        return new NewsfeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.userNameView.setText(itemsList.get(i).getAuthorFullName());

        if (itemsList.get(i).getAuthorFullName().trim().equals(""))
            myViewHolder.userNameView.setText("User");

        String timeAgo = Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("gmt","yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", itemsList.get(i).getCreatedAt()));

        if (itemsList.get(i).getIsAdmin()) {

            int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

            Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
            img.setBounds(0, 0, sizeInPixel, sizeInPixel);

            myViewHolder.userNameView.setCompoundDrawables(null, null, img, null);
            myViewHolder.userNameView.setCompoundDrawablePadding(15);
            myViewHolder.timeView.setText(Html.fromHtml("<b>Admin</b> · " + timeAgo));

        } else {

            myViewHolder.userNameView.setCompoundDrawables(null, null, null, null);
            myViewHolder.timeView.setText(timeAgo);
        }

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
                if (body == null || body.equalsIgnoreCase("")) {
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
            myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));

        }

        myViewHolder.llShareHolder.setOnClickListener(view -> listener.onSharePost(itemsList.get(i)));
        myViewHolder.commentCountView.setText(wordBeautify(itemsList.get(i).getCommentsCount(), false));

        Glide.with(context)
                .load(itemsList.get(i).getAuthorImage())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.user_image)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.userImage);

        String postImage = itemsList.get(i).getAttachment();

        if (postImage.equals("null")) {

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
        }


        ImageView favorite = myViewHolder.likeIcon;
        final TextView likeCount = myViewHolder.likeCountView;
        likeCount.setText(wordBeautify(itemsList.get(i).getFavoriteCount(), true));

        if (itemsList.get(i).isFavorited() || (favorite.getTag() != null && favorite.getTag().toString().equals("yes"))) {
            favorite.setImageResource(R.drawable.ic_favorite_color);
            favorite.setTag("yes");

        } else {
            favorite.setTag("no");
            favorite.setImageResource(R.drawable.ic_favorite);
        }

        myViewHolder.likeHolder.setOnClickListener(v -> {

            if (CredentialManager.getToken().equals("")) {
                Toast.makeText(context, R.string.you_need_to_login_first, Toast.LENGTH_SHORT).show();
                return;
            }

            if (favorite.getTag().equals("yes")) {

                fragment.sendLike(itemsList.get(i).getSlug(), true);
                favorite.setImageResource(R.drawable.ic_favorite);
                favorite.setTag("no");
                itemsList.get(i).setFavoriteCount(itemsList.get(i).getFavoriteCount() - 1);
                likeCount.setText(wordBeautify(itemsList.get(i).getFavoriteCount(), true));

            } else {

                fragment.sendLike(itemsList.get(i).getSlug(), false);
                favorite.setImageResource(R.drawable.ic_favorite_color);
                favorite.setTag("yes");
                itemsList.get(i).setFavoriteCount(itemsList.get(i).getFavoriteCount() + 1);
                likeCount.setText(wordBeautify(itemsList.get(i).getFavoriteCount(), true));

            }
        });

        View.OnClickListener commentOpener = view -> fragment.openCommentBottomSheet(itemsList.get(i).getSlug(), itemsList.get(i).getAuthorFullName(), itemsList.get(i).getAuthorImage(), itemsList.get(i).getIsAdmin(), itemsList.get(i).getBody(), itemsList.get(i).getCreatedAt(), itemsList.get(i).getAttachment());

        myViewHolder.commentHolder.setOnClickListener(commentOpener);
        myViewHolder.statusView.setOnClickListener(commentOpener);
        myViewHolder.postImage.setOnClickListener(commentOpener);

        myViewHolder.menuIcon.setOnClickListener(view -> {

            PopupMenu popup = new PopupMenu(context, myViewHolder.menuIcon);

            if (CredentialManager.getUserData().getGroups().toString().contains("EvalyEmployee"))
                popup.getMenuInflater().inflate(R.menu.newsfeed_menu_super, popup.getMenu());
            else
                popup.getMenuInflater().inflate(R.menu.newsfeed_menu_user, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        Intent in = new Intent(Intent.ACTION_SEND);
                        in.setType("text/plain");
                        in.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                        in.putExtra(Intent.EXTRA_TEXT, "https://evaly.com.bd/feeds/" + itemsList.get(i).getSlug());
                        context.startActivity(Intent.createChooser(in, "Share Post"));
                        break;
                    case R.id.action_delete:
                        if (!CredentialManager.getUserData().getGroups().toString().contains("EvalyEmployee"))
                            break;
                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("YES", (dialog, whichButton) -> fragment.deletePost(itemsList.get(i).getSlug(), "post"))
                                .setNegativeButton("NO", null).show();
                        break;
                    case R.id.action_edit:
//                        NewsfeedActivity activity = (NewsfeedActivity) context;
//                        activity.openEditBottomSheet(itemsList.get(i));
                }
                return true;
            });

            popup.show();
        });
    }

    private String wordBeautify(int count, boolean like) {

        if (count == 0)
            if (like)
                return context.getResources().getString(R.string.like);
            else
                return context.getResources().getString(R.string.comment);
        else if (count == 1)
            if (like)
                return "1 " + context.getResources().getString(R.string.like);
            else
                return "1 " + context.getResources().getString(R.string.comment);
        else if (like)
            return count + " " + context.getResources().getString(R.string.likes);
        else
            return count + " " + context.getResources().getString(R.string.comments);
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

    public interface NewsFeedShareListener<T> {
        void onSharePost(T object);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userNameView, timeView, statusView, likeCountView, commentCountView;
        ImageView userImage, likeIcon, commentIcon, menuIcon, postImage;
        View view;
        LinearLayout likeHolder, commentHolder, llShareHolder;
        RichLinkView linkPreview;
        CardView cardLink;

        public MyViewHolder(final View itemView) {
            super(itemView);

            userNameView = itemView.findViewById(R.id.user_name);
            timeView = itemView.findViewById(R.id.date);
            statusView = itemView.findViewById(R.id.text);
            likeCountView = itemView.findViewById(R.id.likeCount);
            commentCountView = itemView.findViewById(R.id.commentCount);

            userImage = itemView.findViewById(R.id.picture);
            likeIcon = itemView.findViewById(R.id.like_icon);
            commentIcon = itemView.findViewById(R.id.comment_icon);
            menuIcon = itemView.findViewById(R.id.menu);
            postImage = itemView.findViewById(R.id.postImage);

            likeHolder = itemView.findViewById(R.id.likeHolder);
            commentHolder = itemView.findViewById(R.id.commentHolder);
            commentHolder = itemView.findViewById(R.id.commentHolder);
            cardLink = itemView.findViewById(R.id.cardLink);
            linkPreview = itemView.findViewById(R.id.linkPreview);
            llShareHolder = itemView.findViewById(R.id.llShareHolder);

            view = itemView;
        }
    }

}
