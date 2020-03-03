package bd.com.evaly.evalyshop.ui.newsfeed.post;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ProgressBarBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.ui.newsfeed.comment.CommentBottomSheet;
import bd.com.evaly.evalyshop.util.Utils;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class NewsfeedPostAdapter extends PagedListAdapter<NewsfeedPost, RecyclerView.ViewHolder> {


    private Context context;
    private int ITEM_VIEW = 1;
    private int PROGRESS_VIEW = 0;
    private NewsfeedPostViewModel viewModel;
    private FragmentManager fragmentManager;
    private NetworkState networkState;
    private NewsFeedShareListener<NewsfeedPost> shareListener;

    public NewsfeedPostAdapter(Context context, NewsfeedPostViewModel viewModel, FragmentManager fragmentManager, NewsFeedShareListener<NewsfeedPost> shareListener) {

        super(NewsfeedPost.DIFF_CALLBACK);
        this.context = context;
        this.viewModel = viewModel;
        this.fragmentManager = fragmentManager;
        this.shareListener = shareListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        if (itemType == PROGRESS_VIEW) {
            ProgressBarBinding binding = ProgressBarBinding.inflate(layoutInflater, viewGroup, false);
            return new ProgressViewHolder(binding);
        } else {
            NewsfeedItemBinding binding = NewsfeedItemBinding.inflate(layoutInflater, viewGroup, false);
            return new ItemViewHolder(binding);
        }
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
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (getItem(i) == null) {
            Toast.makeText(context, "Placeholder", Toast.LENGTH_SHORT).show();
        } else if (viewHolder instanceof ProgressViewHolder) {

            ((ProgressViewHolder) viewHolder).bindView(networkState);

        } else {

            NewsfeedPost model = getItem(i);

            ItemViewHolder myViewHolder = (ItemViewHolder) viewHolder;

            myViewHolder.binding.userName.setText(model.getAuthorFullName());

            if (model.getAuthorFullName().trim().equals(""))
                myViewHolder.binding.userName.setText("User");

            String timeAgo = Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", model.getCreatedAt()));

            if (model.getAuthorIsAdmin() == 1) {

                int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);
                Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
                img.setBounds(0, 0, sizeInPixel, sizeInPixel);
                myViewHolder.binding.userName.setCompoundDrawables(null, null, img, null);
                myViewHolder.binding.userName.setCompoundDrawablePadding(15);
                myViewHolder.binding.date.setText(Html.fromHtml("<b>Admin</b> · " + timeAgo));

            } else {

                myViewHolder.binding.userName.setCompoundDrawables(null, null, null, null);
                myViewHolder.binding.date.setText(timeAgo);
            }

            if (isJSONValid(model.getBody())) {
                try {
                    JSONObject object = new JSONObject(model.getBody());
                    myViewHolder.binding.linkPreview.setLink(object.getString("url"), new ViewListener() {
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
                        myViewHolder.binding.text.setVisibility(View.GONE);
                    } else {
                        myViewHolder.binding.text.setVisibility(View.VISIBLE);
                        myViewHolder.binding.text.setText(Html.fromHtml(Utils.truncateText(body, 180, "... <b>Show more</b>")));
                    }

                    myViewHolder.binding.cardLink.setVisibility(View.VISIBLE);
                    myViewHolder.binding.cardLink.setOnClickListener(view -> {
                        Logger.json(object.toString());
//                    try {
//                        context.startActivity(new Intent(new Intent(context, ViewProductActivity.class))
//                                .putExtra("product_slug", object.getString("url").replace(UrlUtils.PRODUCT_BASE_URL, "")));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                myViewHolder.binding.text.setVisibility(View.VISIBLE);
                myViewHolder.binding.cardLink.setVisibility(View.GONE);
                myViewHolder.binding.text.setText(Html.fromHtml(Utils.truncateText(model.getBody(), 180, "... <b>Show more</b>")));
            }

            myViewHolder.binding.shareCount.setOnClickListener(view -> shareListener.onSharePost(model));
            myViewHolder.binding.shareIcon.setOnClickListener(view -> shareListener.onSharePost(model));

            myViewHolder.binding.commentCount.setText(wordBeautify(model.getCommentsCount(), false));

            Glide.with(context)
                    .load(model.getAuthorCompressedImage())
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.user_image)
                    .apply(new RequestOptions().override(200, 200))
                    .into(myViewHolder.binding.picture);

            String postImage = model.getAttachment();

            if (postImage == null) myViewHolder.binding.postImage.setVisibility(View.GONE);
            else {
                myViewHolder.binding.postImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(model.getAttachmentCompressedUrl())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .fitCenter()
                        .apply(new RequestOptions().override(900, 900))
                        .into(myViewHolder.binding.postImage);
            }

            ImageView favorite = myViewHolder.binding.likeIcon;
            final TextView likeCount = myViewHolder.binding.likeCount;
            likeCount.setText(wordBeautify(model.getFavoritesCount(), true));

            if (model.getFavorited() == 1 || (favorite.getTag() != null && favorite.getTag().toString().equals("yes"))) {
                favorite.setTag("yes");
                favorite.setImageResource(R.drawable.ic_favorite_color);

            } else {
                favorite.setTag("no");
                favorite.setImageResource(R.drawable.ic_favorite);
            }

            myViewHolder.binding.likeIcon.setOnClickListener(v -> {

                if (CredentialManager.getToken().equals("")) {
                    Toast.makeText(context, "You need to login first", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (favorite.getTag().equals("yes")) {

                    viewModel.sendLike(model.getSlug(), true);
                    favorite.setImageResource(R.drawable.ic_favorite);
                    favorite.setTag("no");
                    model.setFavoritesCount(model.getFavoritesCount() - 1);
                    likeCount.setText(wordBeautify(model.getFavoritesCount(), true));

                } else {
                    viewModel.sendLike(model.getSlug(), false);
                    favorite.setImageResource(R.drawable.ic_favorite_color);
                    favorite.setTag("yes");
                    model.setFavoritesCount(model.getFavoritesCount() + 1);
                    likeCount.setText(wordBeautify(model.getFavoritesCount(), true));
                }
            });

            View.OnClickListener commentOpener = view -> {


                CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(model);
                commentBottomSheet.show(fragmentManager, "comment");


            };

            myViewHolder.binding.commentIcon.setOnClickListener(commentOpener);
            myViewHolder.binding.commentCount.setOnClickListener(commentOpener);
            myViewHolder.binding.text.setOnClickListener(commentOpener);
            myViewHolder.binding.postImage.setOnClickListener(commentOpener);

//        myViewHolder.menuIcon.setOnClickListener(view -> {
//
//            PopupMenu popup = new PopupMenu(context, myViewHolder.menuIcon);
//
//            if (fragment.getUserDetails().getGroups().contains("EvalyEmployee"))
//                popup.getMenuInflater().inflate(R.menu.newsfeed_menu_super, popup.getMenu());
//            else
//                popup.getMenuInflater().inflate(R.menu.newsfeed_menu_user, popup.getMenu());
//
//            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                public boolean onMenuItemClick(MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.action_share:
//                            Intent in = new Intent(Intent.ACTION_SEND);
//                            in.setType("text/plain");
//                            in.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
//                            in.putExtra(Intent.EXTRA_TEXT, "https://evaly.com.bd/feeds/" + model.getSlug());
//                            context.startActivity(Intent.createChooser(in, "Share CreatePostModel"));
//                            break;
//                        case R.id.action_delete:
//                            if (!fragment.getUserDetails().getGroups().contains("EvalyEmployee"))
//                                break;
//                            new AlertDialog.Builder(context)
//                                    .setMessage("Are you sure you want to delete?")
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .setPositiveButton("YES", (dialog, whichButton) -> fragment.deletePost(model.getSlug(), "post"))
//                                    .setNegativeButton("NO", null).show();
//                            break;
//                        case R.id.action_edit:
//                            NewsfeedActivity activity = (NewsfeedActivity) context;
//                            activity.openEditBottomSheet(model);
//                    }
//
//                    return true;
//                }
//            });
//
//            popup.show();
//        });

        }
    }

    private String wordBeautify(int count, boolean like) {
        if (count == 0)
            return "";
        else
            return count + "";
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1)
            return PROGRESS_VIEW;
        else
            return ITEM_VIEW;
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        NewsfeedItemBinding binding;

        ItemViewHolder(NewsfeedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBarBinding binding;

        ProgressViewHolder(ProgressBarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText("Can't load! Check internet connection");
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }


}
