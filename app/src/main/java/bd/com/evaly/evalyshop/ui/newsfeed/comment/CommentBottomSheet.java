package bd.com.evaly.evalyshop.ui.newsfeed.comment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.CommentBottomSheetFragmentBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.Utils;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class CommentBottomSheet extends BottomSheetDialogFragment {

    private CommentViewModel viewModel;
    private CommentBottomSheetFragmentBinding binding;
    private List<CommentItem> itemList = new ArrayList<>();
    private int currentPage = 1;
    private boolean loading = false;
    private int totalCount = 0;
    private CommentAdapter adapter;
    private NewsfeedPost newsfeedPostModel;

    public static CommentBottomSheet newInstance(NewsfeedPost postModel) {
        CommentBottomSheet bottomSheet = new CommentBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("postModel", postModel);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        if (getArguments() == null)
            dismiss();
        else
            newsfeedPostModel = (NewsfeedPost) getArguments().getSerializable("postModel");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CommentBottomSheetFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (newsfeedPostModel.getCreatedAt() == null)
            loadDetailsFromApi();
        else
            initCommentHeader(newsfeedPostModel);

        adapter = new CommentAdapter(itemList, getContext(), getFragmentManager(), newsfeedPostModel.getSlug(), viewModel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);


        viewModel.getCommentLiveData().observe(getViewLifecycleOwner(), commentItems -> {

            loading = false;

            if (currentPage == 2)
                binding.progressContainer.setVisibility(View.GONE);
            else
                binding.progressBarBottom.setVisibility(View.INVISIBLE);

            itemList.addAll(commentItems);
            adapter.notifyItemRangeInserted(itemList.size() - commentItems.size(), commentItems.size());

            if (commentItems.size() == 0) {
                binding.not.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCommentCreatedLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response.has("data")) {
                reloadRecycler();
                binding.commentInput.setText("");
                binding.commentInput.setEnabled(true);
                binding.submitComment.setEnabled(true);
            } else
                Toast.makeText(getContext(), "Couldn't create comment", Toast.LENGTH_SHORT).show();
        });

        if (newsfeedPostModel.getAttachment() != null)
            binding.postImage.setVisibility(View.VISIBLE);
        else
            binding.postImage.setVisibility(View.GONE);


        binding.progressBarBottom.setVisibility(View.GONE);
        binding.progressContainer.setVisibility(View.VISIBLE);

        currentPage = 1;

        viewModel.loadComments(currentPage++, newsfeedPostModel.getSlug());

        viewModel.getTotalCount().observe(getViewLifecycleOwner(), integer -> totalCount = integer);

        binding.stickyScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(0).getBottom()
                    <= (v.getHeight() + v.getScrollY())) {

                if (!loading && itemList.size() < totalCount) {
                    binding.progressBarBottom.setVisibility(View.VISIBLE);
                    viewModel.loadComments(currentPage, newsfeedPostModel.getSlug());
                    currentPage++;
                    loading = true;
                }
            }
        });

        binding.refresh.setOnClickListener(v -> {

            reloadRecycler();

        });


        binding.uploadImage.setOnClickListener(view1 -> Toast.makeText(getContext(), "Photo reply is disabled now.", Toast.LENGTH_SHORT).show());

        binding.submitComment.setOnClickListener(v -> {


            if (!isVisible())
                return;
            else if (binding.commentInput.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), "Write something first before posting comment.", Toast.LENGTH_SHORT).show();


            binding.commentInput.setEnabled(false);
            binding.submitComment.setEnabled(false);

            JsonObject parameters = new JsonObject();
            JsonObject parametersPost = new JsonObject();

            parameters.addProperty("body", binding.commentInput.getText().toString());
            parametersPost.add("comment", parameters);

            viewModel.createComment(parametersPost, newsfeedPostModel.getSlug());


        });


        if (CredentialManager.getToken().equals("")) {
            binding.commentInput.setHint("You need to login before commenting.");
            binding.commentInput.setEnabled(false);
            binding.submitComment.setEnabled(false);
        }

    }

    private void loadDetailsFromApi() {

        NewsfeedApiHelper.getPostDetails(CredentialManager.getToken(), newsfeedPostModel.getSlug(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject ob, int statusCode) {
                if (ob != null && ob.has("body")) {
                    JsonObject author = ob.getAsJsonObject("author");
                    String authorName = author.get("full_name").getAsString();
                    String authorImage = author.get("compressed_image").getAsString();
                    boolean isAdmin = author.get("is_admin").getAsBoolean();
                    String postText = ob.get("body").getAsString();
                    String date = ob.get("created_at").getAsString();
                    String postImageUrl = ob.get("attachment").isJsonNull() ? "" : ob.get("attachment").getAsString();

                    newsfeedPostModel.setAuthorFullName(authorName);
                    newsfeedPostModel.setAuthorCompressedImage(authorImage);
                    newsfeedPostModel.setAuthorIsAdmin(isAdmin ? 1 : 0);
                    newsfeedPostModel.setBody(postText);
                    newsfeedPostModel.setCreatedAt(date);
                    newsfeedPostModel.setAttachment(postImageUrl);
                    initCommentHeader(newsfeedPostModel);

                } else {
                    Toast.makeText(getContext(), "Post is not available", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Toast.makeText(getContext(), "Post is not available", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    private void reloadRecycler() {
        currentPage = 1;
        itemList.clear();
        adapter.notifyDataSetChanged();
        viewModel.loadComments(currentPage, newsfeedPostModel.getSlug());
    }

    private void initCommentHeader(NewsfeedPost postModel) {


        if (getContext() != null) {

            binding.userName.setText(postModel.getAuthorFullName());
            binding.text.setText(postModel.getBody());

            String timeAgo = Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", postModel.getCreatedAt()));
            binding.date.setText(timeAgo);

            if (postModel.getAuthorIsAdmin() == 1) {
                int sizeInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
                img.setBounds(0, 0, sizeInPixel, sizeInPixel);
                binding.userName.setCompoundDrawables(null, null, img, null);
                binding.userName.setCompoundDrawablePadding(15);
                binding.date.setText(Html.fromHtml("<b>Admin</b> · " + timeAgo));

            } else {
                binding.userName.setCompoundDrawables(null, null, null, null);
                binding.date.setText(timeAgo);
            }

            Glide.with(getContext())
                    .load(postModel.getAuthorImage())
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.user_image)
                    .apply(new RequestOptions().override(200, 200))
                    .into(binding.picture);

            if (postModel.getAttachment() == null || postModel.getAttachment().equals("")) {
            } else {

                Glide.with(getContext())
                        .load(postModel.getAttachment())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(900, 900))
                        .into(binding.postImage);

                binding.postImage.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ImagePreview.class);
                    intent.putExtra("image", postModel.getAttachment());
                    getContext().startActivity(intent);
                });

            }

            if (Utils.isJSONValid(postModel.getBody())) {
                try {
                    JSONObject object = new JSONObject(postModel.getBody());
                    assert binding.linkPreview != null;
                    binding.linkPreview.setLink(object.getString("url"), new ViewListener() {
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
                        binding.text.setVisibility(View.GONE);
                    } else {
                        binding.text.setVisibility(View.VISIBLE);
                        binding.text.setText(Html.fromHtml(body));
                    }
                    assert binding.cardLink != null;
                    binding.cardLink.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                binding.text.setVisibility(View.VISIBLE);
                assert binding.cardLink != null;
                binding.cardLink.setVisibility(View.GONE);
                binding.text.setText(postModel.getBody());
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialogz -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialogz;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (getContext() != null && bottomSheet != null) {
                ScreenUtils screenUtils = new ScreenUtils(getContext());

                LinearLayout dialogLayoutReply = dialog.findViewById(R.id.container2);
                assert dialogLayoutReply != null;
                dialogLayoutReply.setMinimumHeight(screenUtils.getHeight());

                //BottomSheetBehavior.from(bottomSheet).setPeekHeight(screenUtils.getHeight());
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }

        });
        return bottomSheetDialog;
    }


}
