package bd.com.evaly.evalyshop.ui.newsfeed.replies;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.models.newsfeed.comment.Author;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.util.Utils;


public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MyViewHolder> {

    private List<RepliesItem> itemsList;
    private Context context;
    private ReplyViewModel viewModel;
    private PreferenceRepository preferenceRepository;

    public ReplyAdapter(List<RepliesItem> itemsList, Context context, ReplyViewModel viewModel, PreferenceRepository preferenceRepository) {
        this.itemsList = itemsList;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ReplyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_reply, viewGroup, false);
        return new ReplyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.MyViewHolder myViewHolder, int i) {

        RepliesItem RepliesItem = itemsList.get(i);
        Author author = RepliesItem.getAuthor();

        myViewHolder.userNameView.setText(author.getFullName());

        if (author.getIsAdmin()) {
            int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

            Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
            img.setBounds(0, 0, sizeInPixel, sizeInPixel);
            myViewHolder.userNameView.setCompoundDrawables(null, null, img, null);
            myViewHolder.userNameView.setCompoundDrawablePadding(15);
        } else {
            myViewHolder.userNameView.setCompoundDrawables(null, null, null, null);
        }


        if (author.getFullName().trim().equals(""))
            myViewHolder.userNameView.setText("User");

        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", RepliesItem.getCreatedAt())));
        myViewHolder.statusView.setText(Html.fromHtml(RepliesItem.getBody()));

        Glide.with(context)
                .load(author.getCompressedImage())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(100, 100))
                .placeholder(R.drawable.user_image)
                .into(myViewHolder.userImage);


        Object postImageURL = RepliesItem.getAttachement();

        if (postImageURL != null) {
            if (postImageURL.equals("null")) {

                myViewHolder.postImage.setVisibility(View.GONE);

            } else {

                myViewHolder.postImage.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(RepliesItem.getAttachement().toString())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(900, 900))
                        .into(myViewHolder.postImage);
            }
        }


        myViewHolder.view.setLongClickable(true);

        myViewHolder.view.setOnLongClickListener(
                view -> {
                    if (preferenceRepository.getToken().equals(""))
                        return false;
                    if (preferenceRepository.getUserData() != null && !preferenceRepository.getUserData().getGroups().contains("EvalyEmployee"))
                        return false;

                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("YES", (dialog, whichButton) -> {
                                viewModel.deleteReply(itemsList.get(i).getId());
                                itemsList.remove(i);
                                notifyItemRemoved(i);

                            })
                            .setNegativeButton("NO", null).show();

                    return false;
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
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
