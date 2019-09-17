package bd.com.evaly.evalyshop.activity.newsfeed.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.newsfeed.NewsfeedFragment;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.Utils;


public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.MyViewHolder>{

    ArrayList<NewsfeedItem> itemsList;
    Context context;
    NewsfeedFragment fragment;

    public NewsfeedAdapter(ArrayList<NewsfeedItem> itemsList, Context context, NewsfeedFragment fragment) {
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NewsfeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_newsfeed_list,viewGroup,false);
        return new NewsfeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedAdapter.MyViewHolder myViewHolder, int i) {


        myViewHolder.userNameView.setText(itemsList.get(i).getAuthorFullName());

        if (itemsList.get(i).getAuthorFullName().trim().equals(""))
            myViewHolder.userNameView.setText("User");

        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS","hh:mm aa - d',' MMMM", itemsList.get(i).getUpdatedAt())));
        myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "<b>Show more</b>")));
        myViewHolder.commentCountView.setText(wordBeautify(itemsList.get(i).getCommentsCount(), false));

        Glide.with(context)
                .load(itemsList.get(i).getAuthorImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.userImage);

        String postImage = itemsList.get(i).getAttachment();

        if (postImage.equals("null")){

            myViewHolder.postImage.setVisibility(View.GONE);

        } else {

            myViewHolder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(itemsList.get(i).getAttachment())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(900, 900))
                    .into(myViewHolder.postImage);
        }

        ImageView favorite = myViewHolder.likeIcon;
        final TextView likeCount = myViewHolder.likeCountView;
        likeCount.setText(wordBeautify(itemsList.get(i).getFavoriteCount(), true));

        if(itemsList.get(i).isFavorited() || (favorite.getTag() != null && favorite.getTag().toString().equals("yes"))){

            favorite.setImageResource(R.drawable.ic_favorite_color);
            favorite.setTag("yes");

        } else {
            favorite.setTag("no");
            favorite.setImageResource(R.drawable.ic_favorite);
        }

        myViewHolder.likeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fragment.getUserDetails().getToken().equals("")) {
                    Toast.makeText(context, "You need to login first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(favorite.getTag().equals("yes")){

                    fragment.sendLike(itemsList.get(i).getSlug(), true);
                    favorite.setImageResource(R.drawable.ic_favorite);
                    favorite.setTag("no");
                    itemsList.get(i).setFavoriteCount(itemsList.get(i).getFavoriteCount()-1 );
                    likeCount.setText(wordBeautify(itemsList.get(i).getFavoriteCount(), true));

                } else {

                    fragment.sendLike(itemsList.get(i).getSlug(), false);
                    favorite.setImageResource(R.drawable.ic_favorite_color);
                    favorite.setTag("yes");
                    itemsList.get(i).setFavoriteCount(itemsList.get(i).getFavoriteCount()+1 );
                    likeCount.setText(wordBeautify(itemsList.get(i).getFavoriteCount(), true));

                }
            }
        });



        View.OnClickListener commentOpener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.openCommentBottomSheet(itemsList.get(i).getSlug(), itemsList.get(i).getAuthorFullName(), itemsList.get(i).getAuthorImage(), itemsList.get(i).getBody(), itemsList.get(i).getCreatedAt(), itemsList.get(i).getAttachment());
            }
        };

        myViewHolder.commentHolder.setOnClickListener(commentOpener);

        myViewHolder.statusView.setOnClickListener(commentOpener);

        final String shareURL = "https://evaly.com.bd/feeds/"+itemsList.get(i).getSlug();


        myViewHolder.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PopupMenu popup = new PopupMenu(context, myViewHolder.menuIcon);

                if (fragment.getUserDetails().getGroups().contains("EvalyEmployee"))
                    popup.getMenuInflater().inflate(R.menu.newsfeed_menu_super, popup.getMenu());
                else
                    popup.getMenuInflater().inflate(R.menu.newsfeed_menu_user, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {


                        switch (item.getItemId()){
                            case R.id.action_share:
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                                i.putExtra(Intent.EXTRA_TEXT, shareURL);
                                context.startActivity(Intent.createChooser(i, "Share Post"));

                                break;
                            case R.id.action_delete:


                                break;


                        }

                        return true;
                    }
                });

                popup.show();



            }
        });



    }

    private String wordBeautify(int count, boolean like){

        if (count  == 0)
            if (like)
                return "Like";
            else
                return "Comment";
        else if (count == 1)
            if (like)
                return "1 Like";
            else
                return "1 Comment";
        else
        if (like)
            return count + " Likes";
        else
            return count +" Comments";

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
        TextView userNameView, timeView, statusView, likeCountView, commentCountView;
        ImageView userImage, likeIcon, commentIcon, menuIcon, postImage;
        View view;
        LinearLayout likeHolder, commentHolder;
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



            view = itemView;
        }
    }




    
}
