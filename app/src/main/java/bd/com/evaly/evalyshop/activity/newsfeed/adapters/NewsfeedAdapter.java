package bd.com.evaly.evalyshop.activity.newsfeed.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.models.NewsfeedItem;
import bd.com.evaly.evalyshop.util.Utils;


public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.MyViewHolder>{

    ArrayList<NewsfeedItem> itemsList;
    Context context;

    public NewsfeedAdapter(ArrayList<NewsfeedItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsfeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification,viewGroup,false);
        return new NewsfeedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedAdapter.MyViewHolder myViewHolder, int i) {


        myViewHolder.userNameView.setText(itemsList.get(i).getAuthorUsername());
        myViewHolder.timeView.setText(Utils.formattedDateFromString("","hh:mm aa - d',' MMMM", itemsList.get(i).getCreatedAt()));
        myViewHolder.statusView.setText(itemsList.get(i).getBody());


        myViewHolder.commentCountView.setText(itemsList.get(i).getCommentsCount());



        Glide.with(context)
                .load(itemsList.get(i).getAuthorImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.userImage);


        String postImage = itemsList.get(i).getAttachment();

        if (!postImage.equals("null")){

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



        myViewHolder.likeCountView.setText(itemsList.get(i).getFavoriteCount());


        if(itemsList.get(i).isFavorited()){

            favorite.setImageResource(R.drawable.ic_favorite_color);
            favorite.setTag("yes");
            likeCount.setText(itemsList.get(i).getFavoriteCount()+1 +" Likes");

        } else {

            favorite.setTag("no");
            favorite.setImageResource(R.drawable.ic_favorite);

            likeCount.setText(itemsList.get(i).getFavoriteCount()-1 +" Likes");

        }




        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(favorite.getTag().equals("yes")){
                    favorite.setImageResource(R.drawable.ic_favorite);
                    favorite.setTag("no");
                    likeCount.setText("32 Likes");

                } else {

                    favorite.setTag("yes");
                    favorite.setImageResource(R.drawable.ic_favorite_color);
                    likeCount.setText("33 Likes");

                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userNameView, timeView, statusView, likeCountView, commentCountView;
        ImageView userImage, likeIcon, commentIcon, menuIcon, postImage;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_name);
            timeView = itemView.findViewById(R.id.date);
            statusView = itemView.findViewById(R.id.text);
            likeCountView = itemView.findViewById(R.id.likeCount);
            commentCountView = itemView.findViewById(R.id.comment);

            userImage = itemView.findViewById(R.id.image);
            likeIcon = itemView.findViewById(R.id.like_icon);
            commentIcon = itemView.findViewById(R.id.comment_icon);
            menuIcon = itemView.findViewById(R.id.menu);
            postImage = itemView.findViewById(R.id.postImage);



            view = itemView;
        }
    }

    
}
