package bd.com.evaly.evalyshop.activity.newsfeed.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.NewsfeedFragment;
import bd.com.evaly.evalyshop.models.newsfeed.comment.Author;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.util.Utils;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    ArrayList<CommentItem> itemsList;
    Context context;
    NewsfeedFragment fragment;

    public CommentAdapter(ArrayList<CommentItem> itemsList, Context context, NewsfeedFragment fragment) {
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
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

        if (repliesList.size() > 0){
            if (repliesList.size() == 1){
                myViewHolder.replyMoreCount.setVisibility(View.GONE);
            }else {
                myViewHolder.replyMoreCount.setVisibility(View.VISIBLE);
                myViewHolder.replyHolder.setVisibility(View.VISIBLE);

                if (repliesList.size() == 2)
                    myViewHolder.replyMoreCount.setText("Show previous 1 more reply");
                else {
                    String smText = "Show previous " +(repliesList.size()-1)+ " more replies";
                    myViewHolder.replyMoreCount.setText(smText);
                }
            }

            myViewHolder.reply1Name.setText(repliesList.get(0).getAuthor().getFullName());
            myViewHolder.reply1Text.setText(repliesList.get(0).getBody());
            Glide.with(context)
                    .load(repliesList.get(0).getAuthor().getCompressedImage())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .fitCenter()
                    .centerCrop()
                    .apply(new RequestOptions().override(50, 50))
                    .placeholder(R.drawable.user_image)
                    .into(myViewHolder.reply1Image);

        } else
        {
            myViewHolder.replyMoreCount.setVisibility(View.GONE);
            myViewHolder.replyHolder.setVisibility(View.GONE);

        }


        myViewHolder.userNameView.setText(author.getFullName());

        if (author.getFullName().trim().replaceAll("\\s+","").equals(""))
            myViewHolder.userNameView.setText("User");

        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS","hh:mm aa - d',' MMMM", commentItem.getCreatedAt())));
        myViewHolder.statusView.setText(Html.fromHtml(commentItem.getBody()));

        Glide.with(context)
                .load(author.getCompressedImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fitCenter()
                .centerCrop()
                .apply(new RequestOptions().override(100, 100))
                .into(myViewHolder.userImage);


        Object postImageURL = commentItem.getAttachement();


        if (postImageURL != null) {
            if (postImageURL.equals("null")) {

                myViewHolder.postImage.setVisibility(View.GONE);

            } else {

                myViewHolder.postImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(commentItem.getAttachmentCompressedUrl().toString())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(900, 900))
                        .into(myViewHolder.postImage);
            }
        }

        View.OnClickListener openReply = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.openReplyBottomSheet(String.valueOf(commentItem.getId()), author.getFullName(), author.getCompressedImage(), commentItem.getBody(), commentItem.getCreatedAt(), commentItem.getAttachement());
            }
        };

        myViewHolder.replyCountView.setOnClickListener(openReply);
        myViewHolder.replyIcon.setOnClickListener(openReply);
        myViewHolder.replyHolder.setOnClickListener(openReply);
        myViewHolder.replyMoreCount.setOnClickListener(openReply);


        myViewHolder.view.setLongClickable(true);

        myViewHolder.view.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        if (!fragment.getUserDetails().getGroups().contains("EvalyEmployee"))
                            return false;

                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        fragment.deletePost(commentItem.getId()+"", "comment");


                                    }})
                                .setNegativeButton("NO", null).show();




                        return false;
                    }
                }
        );


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
        TextView userNameView, timeView, statusView, likeCountView, replyCountView, reply1Name, reply1Text, replyMoreCount;
        ImageView userImage, likeIcon, replyIcon, menuIcon, postImage, reply1Image;
        LinearLayout replyHolder;
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
            replyIcon = itemView.findViewById(R.id.replyIcon);
            menuIcon = itemView.findViewById(R.id.menu);
            postImage = itemView.findViewById(R.id.postImage);

            reply1Image = itemView.findViewById(R.id.pictureReply1);
            reply1Name = itemView.findViewById(R.id.reply1Name);
            reply1Text = itemView.findViewById(R.id.reply1Text);
            replyMoreCount = itemView.findViewById(R.id.replyViewCount);
            replyHolder = itemView.findViewById(R.id.replyHolder);



            view = itemView;
        }
    }


}
