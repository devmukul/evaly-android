package bd.com.evaly.evalyshop.ui.newsfeed.comment.pagedList;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.comment.Author;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.ui.newsfeed.comment.CommentViewModel;
import bd.com.evaly.evalyshop.util.Utils;


public class CommentAdapter extends PagedListAdapter<CommentItem, RecyclerView.ViewHolder> {

    private ArrayList<CommentItem> itemsList;
    private Context context;
    private NewsfeedFragment fragment;
    private int ITEM_VIEW = 1;
    private int PROGRESS_VIEW = 0;
    private int HEADER_VIEW = 2;

    private CommentViewModel viewModel;
    private FragmentManager fragmentManager;
    private NetworkState networkState;

    public CommentAdapter(Context context, CommentViewModel viewModel, FragmentManager fragmentManager) {

        super(CommentItem.DIFF_CALLBACK);
        this.context = context;
        this.viewModel = viewModel;
        this.fragmentManager = fragmentManager;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {

        if (itemType == PROGRESS_VIEW)
            return new CommentAdapter.ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar_layout, viewGroup, false));
        else if (itemType == HEADER_VIEW)
            return new CommentAdapter.HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_header, viewGroup, false));
        else
            return new CommentAdapter.ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, int i) {

        if (myViewHolder instanceof ProgressViewHolder) {

        } else if (myViewHolder instanceof  HeaderViewHolder){

            ((HeaderViewHolder) myViewHolder).llHolder.setVisibility(View.VISIBLE);

        } else {
            populateComment((ItemViewHolder) myViewHolder, i);
        }

    }

    private void populateComment(ItemViewHolder myViewHolder, int i) {

        CommentItem commentItem = getItem(i-1);

        Author author = commentItem.getAuthor();
        List<RepliesItem> repliesList = commentItem.getReplies();


        if (author.getFullName() == null)
            author.setFullName("");

        if (repliesList.size() > 0) {
            if (repliesList.size() == 1) {
                myViewHolder.replyMoreCount.setVisibility(View.GONE);
                myViewHolder.replyHolder.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.replyMoreCount.setVisibility(View.VISIBLE);
                myViewHolder.replyHolder.setVisibility(View.VISIBLE);

                if (repliesList.size() == 2)
                    myViewHolder.replyMoreCount.setText("Show previous 1 more reply");
                else {
                    String smText = "Show previous " + (repliesList.size() - 1) + " more replies";
                    myViewHolder.replyMoreCount.setText(smText);
                }

            }

            myViewHolder.reply1Name.setText(repliesList.get(0).getAuthor().getFullName());

            if (repliesList.get(0).getAuthor().getIsAdmin()) {
                int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

                Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
                img.setBounds(0, 0, sizeInPixel, sizeInPixel);
                myViewHolder.userNameView.setCompoundDrawables(null, null, img, null);
                myViewHolder.userNameView.setCompoundDrawablePadding(15);
            } else {
                myViewHolder.userNameView.setCompoundDrawables(null, null, null, null);
            }


            myViewHolder.reply1Text.setText(repliesList.get(0).getBody());

            if (!repliesList.get(0).getAuthor().getCompressedImage().equals("null")) {
                Glide.with(context)
                        .load(repliesList.get(0).getAuthor().getCompressedImage())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .fitCenter()
                        .centerCrop()
                        .apply(new RequestOptions().override(50, 50))
                        .placeholder(R.drawable.user_image)
                        .into(myViewHolder.reply1Image);
            }

        } else {

            myViewHolder.replyMoreCount.setVisibility(View.GONE);
            myViewHolder.replyHolder.setVisibility(View.GONE);

        }


        myViewHolder.userNameView.setText(author.getFullName());

        if (author.getFullName().trim().replaceAll("\\s+", "").equals(""))
            myViewHolder.userNameView.setText("User");


        if (author.getIsAdmin()) {
            int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

            Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
            img.setBounds(0, 0, sizeInPixel, sizeInPixel);
            myViewHolder.userNameView.setCompoundDrawables(null, null, img, null);
            myViewHolder.userNameView.setCompoundDrawablePadding(15);
        } else {
            myViewHolder.userNameView.setCompoundDrawables(null, null, null, null);
        }


        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", commentItem.getCreatedAt())));
        // myViewHolder.statusView.setText(Html.fromHtml(commentItem.getBody()));


        myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(commentItem.getBody(), 180, "... <b>Show more</b>")));

        myViewHolder.statusView.setOnClickListener(view -> {

            if (myViewHolder.statusView.getText().toString().contains("Show more"))
                myViewHolder.statusView.setText(itemsList.get(i).getBody());
            else
                myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));

        });

        Glide.with(context)
                .load(author.getCompressedImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.user_image)
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

        //View.OnClickListener openReply = view -> fragment.openReplyBottomSheet(String.valueOf(commentItem.getId()), author.getFullName(), author.getCompressedImage(), author.getIsAdmin(), commentItem.getBody(), commentItem.getCreatedAt(), commentItem.getAttachement());

//        myViewHolder.replyCountView.setOnClickListener(openReply);
//        myViewHolder.replyIcon.setOnClickListener(openReply);
//        myViewHolder.replyHolder.setOnClickListener(openReply);
//        myViewHolder.replyMoreCount.setOnClickListener(openReply);
//

        myViewHolder.view.setLongClickable(true);

        myViewHolder.view.setOnLongClickListener(
                view -> {

                    if (!CredentialManager.getUserData().getGroups().contains("EvalyEmployee"))
                        return false;

                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            // .setPositiveButton("YES", (dialog, whichButton) -> fragment.deletePost(commentItem.getId() + "", "comment"))
                            .setNegativeButton("NO", null).show();
                    return false;
                }
        );


    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private boolean hasExtraRow() {
        if (networkState != null && networkState != NetworkState.LOADED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return super.getItemCount()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_VIEW;
        else if (hasExtraRow() && position == getItemCount() - 1)
            return PROGRESS_VIEW;
        else
            return ITEM_VIEW;
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressViewHolder(final View itemView) {
            super(itemView);
        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llHolder;

        public HeaderViewHolder(final View itemView) {
            super(itemView);

            llHolder = itemView.findViewById(R.id.holder);

        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView userNameView, timeView, statusView, likeCountView, replyCountView, reply1Name, reply1Text, replyMoreCount;
        ImageView userImage, likeIcon, replyIcon, menuIcon, postImage, reply1Image;
        LinearLayout replyHolder;
        View view;

        public ItemViewHolder(final View itemView) {
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
