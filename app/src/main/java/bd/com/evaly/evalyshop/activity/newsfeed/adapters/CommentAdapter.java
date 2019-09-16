package bd.com.evaly.evalyshop.activity.newsfeed.adapters;

import android.content.Context;
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
import java.util.List;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.newsfeed.comment.Author;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.util.Utils;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    ArrayList<CommentItem> itemsList;
    Context context;

    public CommentAdapter(ArrayList<CommentItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment,viewGroup,false);
        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder myViewHolder, int i) {

        CommentItem commentItem = itemsList.get(i);
        Author author = commentItem.getAuthor();
        List<RepliesItem> repliesList = commentItem.getReplies();


        myViewHolder.userNameView.setText(author.getFullName());
        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS","hh:mm aa - d',' MMMM", commentItem.getCreatedAt())));
        myViewHolder.statusView.setText(itemsList.get(i).getBody());




        Glide.with(context)
                .load(author.getCompressedImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .into(myViewHolder.userImage);


        Object postImageURL = commentItem.getAttachement();


        if (postImageURL != null) {
            if (postImageURL.equals("null")) {

                myViewHolder.postImage.setVisibility(View.GONE);

            } else {


                myViewHolder.postImage.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(commentItem.getAttachement().toString())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(900, 900))
                        .into(myViewHolder.postImage);
            }
        }







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
        TextView userNameView, timeView, statusView, likeCountView, replyCountView;
        ImageView userImage, likeIcon, commentIcon, menuIcon, postImage;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);

            userNameView = itemView.findViewById(R.id.user_name);
            timeView = itemView.findViewById(R.id.date);
            statusView = itemView.findViewById(R.id.text);
            likeCountView = itemView.findViewById(R.id.likeCount);
            replyCountView = itemView.findViewById(R.id.replyCount);

            userImage = itemView.findViewById(R.id.picture);
            likeIcon = itemView.findViewById(R.id.like_icon);
            commentIcon = itemView.findViewById(R.id.comment_icon);
            menuIcon = itemView.findViewById(R.id.menu);
            postImage = itemView.findViewById(R.id.postImage);



            view = itemView;
        }
    }


}
