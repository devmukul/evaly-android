package bd.com.evaly.evalyshop.ui.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.CommentAdapter;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.NewsfeedAdapter;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.ReplyAdapter;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StickyScrollView;
import dagger.hilt.android.AndroidEntryPoint;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

@AndroidEntryPoint
public class NewsfeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, NewsfeedAdapter.NewsFeedShareListener {

    @Inject
    ApiRepository apiRepository;

    @Inject
    PreferenceRepository preferenceRepository;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    SkeletonScreen skeletonCommentHeader;
    private String type;
    private RecyclerView recyclerView;
    private NewsfeedAdapter adapter;
    private ArrayList<NewsfeedItem> itemsList;
    private Context context;
    private LinearLayout not, progressContainer;
    // newfeed scroller
    private boolean loading = true;
    private int currentPage;
    private ProgressBar bottomProgressBar;
    private SwipeRefreshLayout swipeLayout;
    // comment bottom sheet items
    private BottomSheetBehavior bottomSheetBehaviorComment;
    private BottomSheetDialog commentDialog;
    private String selectedPostID = "";
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<CommentItem> commentItems;
    private LinearLayout commentNot;
    private LinearLayout commentProgressContainer;
    private int currentCommentPage;
    private EditText commentInput;
    private ImageView submitComment;
    private ImageView uploadImage;
    private ImageView reloadComment;


    // reply bottom sheet items
    private boolean isCommentLoading = false;
    private BottomSheetBehavior bottomSheetBehaviorReply;
    private BottomSheetDialog replyDialog;
    private int selectedCommentID;
    private RecyclerView replyRecyclerView;
    private ReplyAdapter replyAdapter;
    private ArrayList<RepliesItem> replyItems;
    private LinearLayout replyNot;
    private LinearLayout replyProgressContainer;
    private int currentReplyPage;
    private EditText replyInput;
    private ImageView submitReply;
    private ImageView uploadImageReply;
    private ImageView reloadReply;
    private boolean isReplyLoading = false;
    private int maxCountNewfeed;
    private int maxCountComment;
    private int maxCountReply;
    private BottomSheetDialog bottomSheetDialog;

    public NewsfeedFragment() {
        // Required empty public constructor
    }

    public static NewsfeedFragment newInstance(String type) {
        NewsfeedFragment fragment = new NewsfeedFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override
    public void onRefresh() {
        itemsList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        swipeLayout.setRefreshing(false);
        selectedPostID = "";
        selectedCommentID = 0;
        getPosts(currentPage);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        maxCountNewfeed = -1;
        maxCountComment = -1;
        maxCountReply = -1;
        currentReplyPage = 1;


        replyDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        replyDialog.setContentView(R.layout.alert_replies);

        View bottomSheetInternalReply = replyDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheetInternalReply != null;
        bottomSheetInternalReply.setPadding(0, 0, 0, 0);
        bottomSheetBehaviorReply = BottomSheetBehavior.from(bottomSheetInternalReply);
        bottomSheetBehaviorReply.setState(BottomSheetBehavior.STATE_EXPANDED);

        ScreenUtils screenUtils = new ScreenUtils(context);
        LinearLayout dialogLayout = replyDialog.findViewById(R.id.container2);
        assert dialogLayout != null;
        dialogLayout.setMinimumHeight(screenUtils.getHeight());

        bottomSheetBehaviorReply.setPeekHeight(screenUtils.getHeight());
        replyNot = replyDialog.findViewById(R.id.not);
        replyProgressContainer = replyDialog.findViewById(R.id.progressContainer);


        // Reply recyclerView

        replyItems = new ArrayList<>();
        replyAdapter = new ReplyAdapter(replyItems, context, this, preferenceRepository);
        replyRecyclerView = replyDialog.findViewById(R.id.recyclerView);

        LinearLayoutManager managerReply = new LinearLayoutManager(context);
        replyRecyclerView.setLayoutManager(managerReply);
        replyRecyclerView.setAdapter(replyAdapter);


        bottomSheetBehaviorReply.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    currentReplyPage = 1;
                    selectedCommentID = 0;
                    replyItems.clear();
                    replyAdapter.notifyDataSetChanged();
                    replyDialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        NestedScrollView nestedScrollViewReply = replyDialog.findViewById(R.id.stickyScrollView);

        if (nestedScrollViewReply != null) {
            nestedScrollViewReply.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (nestedScrollViewReply.getChildAt(0).getBottom()
                        <= (nestedScrollViewReply.getHeight() + nestedScrollViewReply.getScrollY())) {
                    try {
                        if (!isReplyLoading)
                            loadReplies(selectedCommentID);

                    } catch (Exception e) {
                        Log.e("load more error", e.toString());
                    }
                }
            });
        }


        // create Reply

        replyInput = replyDialog.findViewById(R.id.commentInput);
        uploadImage = replyDialog.findViewById(R.id.uploadImage);
        submitReply = replyDialog.findViewById(R.id.submitComment);
        reloadReply = replyDialog.findViewById(R.id.refresh);

        if (preferenceRepository.getToken().equals("")) {

            replyInput.setText(R.string.you_need_to_login_to_create_reply);
            replyInput.setEnabled(false);
            submitReply.setEnabled(false);

        } else {
            replyInput.setEnabled(true);
            submitReply.setEnabled(true);
        }


        uploadImage.setOnClickListener(view1 -> Toast.makeText(context, R.string.photo_reply_is_disabled_now, Toast.LENGTH_SHORT).show());
        reloadReply.setOnClickListener(view1 -> reloadRecyclerReply());

        submitReply.setOnClickListener(view13 -> {

            if (selectedPostID.equals(""))
                Toast.makeText(context, R.string.post_error_message, Toast.LENGTH_SHORT).show();
            else if (replyInput.getText().toString().trim().equals(""))
                Toast.makeText(context, R.string.wirte_something_message, Toast.LENGTH_SHORT).show();
            else
                createReply();

        });

        // comment bottom sheet

        currentCommentPage = 1;

        commentDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        commentDialog.setContentView(R.layout.alert_comments);

        View bottomSheetInternalComment = commentDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternalComment.setPadding(0, 0, 0, 0);
        bottomSheetBehaviorComment = BottomSheetBehavior.from(bottomSheetInternalComment);
        bottomSheetBehaviorComment.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehaviorComment.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    currentCommentPage = 1;
                    selectedPostID = "";
                    commentItems.clear();
                    commentAdapter.notifyDataSetChanged();
                    commentDialog.hide();

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        LinearLayout dialogLayoutReply = commentDialog.findViewById(R.id.container2);
        assert dialogLayoutReply != null;
        dialogLayoutReply.setMinimumHeight(screenUtils.getHeight());

        bottomSheetBehaviorComment.setPeekHeight(screenUtils.getHeight());
        commentNot = commentDialog.findViewById(R.id.not);
        commentProgressContainer = commentDialog.findViewById(R.id.progressContainer);

        // comment recyclerView

        commentItems = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentItems, context, this, preferenceRepository);
        commentRecyclerView = commentDialog.findViewById(R.id.recyclerView);
        LinearLayoutManager managerComment = new LinearLayoutManager(context);
        commentRecyclerView.setLayoutManager(managerComment);
        commentRecyclerView.setAdapter(commentAdapter);

        StickyScrollView nestedScrollViewComment = commentDialog.findViewById(R.id.stickyScrollView);

        if (nestedScrollViewComment != null) {
            nestedScrollViewComment.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                if (nestedScrollViewComment.getChildAt(0).getBottom()
                        <= (nestedScrollViewComment.getHeight() + nestedScrollViewComment.getScrollY())) {
                    try {
                        if (!isCommentLoading)
                            loadComments(selectedPostID);
                    } catch (Exception e) {
                        Log.e("load more error", e.toString());
                    }
                }
            });
        }

        commentRecyclerView.setFocusable(false);
        assert nestedScrollViewComment != null;
        nestedScrollViewComment.requestFocus();


        // create comment

        commentInput = commentDialog.findViewById(R.id.commentInput);
        uploadImage = commentDialog.findViewById(R.id.uploadImage);
        submitComment = commentDialog.findViewById(R.id.submitComment);
        reloadComment = commentDialog.findViewById(R.id.refresh);

        if (preferenceRepository.getToken().equals("")) {

            commentInput.setEnabled(false);
            commentInput.setText(R.string.login_to_comment);
            submitComment.setEnabled(false);

        }


        uploadImage.setOnClickListener(view1 -> Toast.makeText(context, R.string.photo_comment_disabled, Toast.LENGTH_SHORT).show());
        reloadComment.setOnClickListener(view1 -> reloadRecyclerComment());

        submitComment.setOnClickListener(view12 -> {

            if (selectedPostID.equals(""))
                Toast.makeText(context, R.string.comment_create_error_message, Toast.LENGTH_SHORT).show();
            else if (commentInput.getText().toString().trim().equals(""))
                Toast.makeText(context, R.string.write_before_submitting_message, Toast.LENGTH_SHORT).show();
            else {
                commentNot.setVisibility(View.GONE);
                createComment();
            }
        });

        // pull to refresh
        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        try {
            swipeLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                    android.R.color.holo_red_dark,
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_orange_dark);
        } catch (Exception e) {

        }

        not = view.findViewById(R.id.not);
        progressContainer = view.findViewById(R.id.progressContainer);
        bottomProgressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        adapter = new NewsfeedAdapter(itemsList, context, this, this, preferenceRepository);

        recyclerView.setAdapter(adapter);

        currentPage = 1;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();
                    if (loading) if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        getPosts(++currentPage);
                }
            }
        });

        getPosts(currentPage);


    }

    public void openReplyBottomSheet(int id, String authorName, String authorImage, boolean isAdmin, String postText, String date, Object postImage) {

        if (replyDialog != null) {

            currentReplyPage = 1;
            maxCountReply = -1;

            TextView authorNameView = replyDialog.findViewById(R.id.user_name);

            if (isAdmin) {
                int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

                Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
                img.setBounds(0, 0, sizeInPixel, sizeInPixel);
                authorNameView.setCompoundDrawables(null, null, img, null);
                authorNameView.setCompoundDrawablePadding(15);
            } else {
                authorNameView.setCompoundDrawables(null, null, null, null);
            }

            authorNameView.setText(authorName);
            ((TextView) replyDialog.findViewById(R.id.text)).setText(postText);
            ((TextView) replyDialog.findViewById(R.id.date)).setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", date)));

            replyNot.setVisibility(View.GONE);

            ImageView userPic = replyDialog.findViewById(R.id.picture);

            Glide.with(context)
                    .load(authorImage)
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.user_image)
                    .apply(new RequestOptions().override(200, 200))
                    .into(userPic);


            ImageView postPic = replyDialog.findViewById(R.id.postImage);

            if (postImage == null || postImage.equals("")) {
            } else {
                Glide.with(context)
                        .load(postImage)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(new RequestOptions().override(900, 900))
                        .into(postPic);
            }

            selectedCommentID = id;
            replyItems.clear();
            replyAdapter.notifyDataSetChanged();
            replyDialog.show();
            bottomSheetBehaviorReply.setState(BottomSheetBehavior.STATE_EXPANDED);
            loadReplies(selectedCommentID);


        } else {
            Toast.makeText(context, R.string.reply_load_error_message, Toast.LENGTH_SHORT).show();
        }

    }

    public void openCommentBottomSheet(String id, String authorName, String authorImage, boolean isAdmin, String postText, String date, String postImageUrl) {


        if (commentDialog != null) {
            currentCommentPage = 1;
            maxCountComment = -1;
            selectedPostID = id;

            commentItems.clear();
            commentAdapter.notifyDataSetChanged();
            commentDialog.show();
            commentNot.setVisibility(View.GONE);
            bottomSheetBehaviorComment.setState(BottomSheetBehavior.STATE_EXPANDED);

            if (authorName.equals("") && authorImage.equals("") && postText.equals("")) {
                loadComments(selectedPostID);
                loadPostDetails(selectedPostID);
            } else {

                initCommentHeader(authorName, authorImage, isAdmin, postText, date, postImageUrl);
                loadComments(selectedPostID);

            }
            ((NestedScrollView) commentDialog.findViewById(R.id.stickyScrollView)).fullScroll(ScrollView.FOCUS_UP);


        } else {
            Toast.makeText(context, "Couldn't load comments", Toast.LENGTH_SHORT).show();
        }


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

    public void initCommentHeader(String authorName, String authorImage, boolean isAdmin, String postText, String date, String postImageUrl) {


        TextView authorNameView = commentDialog.findViewById(R.id.user_name);
        TextView timeView = commentDialog.findViewById(R.id.date);

        String timeAgo = Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", date));

        assert authorNameView != null;
        authorNameView.setText(authorName);


        if (isAdmin) {
            int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);
            Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
            img.setBounds(0, 0, sizeInPixel, sizeInPixel);
            authorNameView.setCompoundDrawables(null, null, img, null);
            authorNameView.setCompoundDrawablePadding(15);
            timeView.setText(Html.fromHtml("<b>Admin</b> · " + timeAgo));

        } else {
            authorNameView.setCompoundDrawables(null, null, null, null);

            timeView.setText(timeAgo);
        }


        TextView tvMessage = commentDialog.findViewById(R.id.text);
        tvMessage.setText(postText);
        ImageView userPic = commentDialog.findViewById(R.id.picture);
        Glide.with(context)
                .load(authorImage)
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.user_image)
                .apply(new RequestOptions().override(200, 200))
                .into(userPic);

        ImageView postPic = commentDialog.findViewById(R.id.postImage);

        if (postImageUrl == null || postImageUrl.equals("")) {
        } else {
            Glide.with(context)
                    .load(postImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(900, 900))
                    .into(postPic);

            postPic.setOnClickListener(view -> {

                Intent intent = new Intent(context, ImagePreview.class);
                intent.putExtra("image", postImageUrl);
                context.startActivity(intent);
            });

        }

        RichLinkView linkPreview = commentDialog.findViewById(R.id.linkPreview);
        CardView cardLink = commentDialog.findViewById(R.id.cardLink);
        if (isJSONValid(postText)) {
            try {
                JSONObject object = new JSONObject(postText);
                assert linkPreview != null;
                linkPreview.setLink(object.getString("url"), new ViewListener() {
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
                    tvMessage.setVisibility(View.GONE);
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(Html.fromHtml(body));
                }
                assert cardLink != null;
                cardLink.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            tvMessage.setVisibility(View.VISIBLE);
            assert cardLink != null;
            cardLink.setVisibility(View.GONE);
            tvMessage.setText(postText);
        }


    }

    private void loadReplies(int comment_id) {

        isReplyLoading = true;

        if (!commentDialog.isShowing()) {
            Toast.makeText(context, "Can't load comments. Restart the app", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentReplyPage == 1) {
            replyDialog.findViewById(R.id.progressBarBottom).setVisibility(View.INVISIBLE);
            replyProgressContainer.setVisibility(View.VISIBLE);
        }

        if (currentReplyPage > 1)
            replyDialog.findViewById(R.id.progressBarBottom).setVisibility(View.VISIBLE);

        replyNot.setVisibility(View.GONE);
        NestedScrollView scrollView = replyDialog.findViewById(R.id.stickyScrollView);

        apiRepository.getReplies(preferenceRepository.getToken(), selectedPostID, comment_id, currentReplyPage, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                assert scrollView != null;
                scrollView.fling(0);
                isReplyLoading = false;
                replyDialog.findViewById(R.id.progressBarBottom).setVisibility(View.INVISIBLE);
                replyProgressContainer.setVisibility(View.GONE);

                JsonArray jsonArray = response.getAsJsonArray("data").get(0).getAsJsonObject().getAsJsonArray("replies");

                if (jsonArray.size() > 0)
                    replyNot.setVisibility(View.GONE);
                else {
                    replyNot.setVisibility(View.VISIBLE);
                    currentReplyPage++;
                }

                for (int i = 0; i < jsonArray.size(); i++) {

                    Gson gson = new Gson();
                    RepliesItem item = gson.fromJson(jsonArray.get(i), RepliesItem.class);

                    if (!item.getBody().trim().equals("")) {
                        replyItems.add(item);
                        replyAdapter.notifyItemInserted(replyItems.size());
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public void createReply() {

        if (replyDialog == null)
            return;

        replyInput.setEnabled(false);
        submitReply.setEnabled(false);

        JsonObject parameters = new JsonObject();
        JsonObject parametersPost = new JsonObject();

        parameters.addProperty("body", replyInput.getText().toString());
        parametersPost.add("comment", parameters);

        apiRepository.postReply(preferenceRepository.getToken(), selectedPostID, selectedCommentID, parametersPost, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if (response.has("data")) {
                    reloadRecyclerReply();
                    replyInput.setText("");
                    replyInput.setEnabled(true);
                    submitReply.setEnabled(true);
                } else
                    Toast.makeText(context, "Couldn't create comment", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public void deletePost(String id, final String type) {

        if (replyDialog == null)
            return;

        replyInput.setEnabled(false);
        submitReply.setEnabled(false);

        String url;

        if (type.equals("post"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts/" + id;
        else
            url = UrlUtils.BASE_URL_NEWSFEED + "comments/" + id;

        apiRepository.deleteItem(preferenceRepository.getToken(), url, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if (type.equals("post")) {
                    itemsList.clear();
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    getPosts(currentPage);
                } else if (type.equals("comment")) {
                    commentItems.clear();
                    commentAdapter.notifyDataSetChanged();
                    currentCommentPage = 1;
                    loadComments(selectedPostID);
                } else {
                    replyItems.clear();
                    replyAdapter.notifyDataSetChanged();
                    currentReplyPage = 1;
                    loadReplies(selectedCommentID);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public void loadComments(String post_id) {

        if (maxCountComment == commentItems.size()) {
            commentProgressContainer.setVisibility(View.GONE);
            commentDialog.findViewById(R.id.progressBarBottom).setVisibility(View.GONE);
            return;
        }

        isCommentLoading = true;
        selectedPostID = post_id;

        if (!commentDialog.isShowing()) {
            Toast.makeText(context, "Can't load comments. Restart the app", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentCommentPage == 1) {
            commentProgressContainer.setVisibility(View.VISIBLE);
            commentDialog.findViewById(R.id.progressBarBottom).setVisibility(View.INVISIBLE);
        }

        if (currentCommentPage > 1)
            commentDialog.findViewById(R.id.progressBarBottom).setVisibility(View.VISIBLE);

        replyNot.setVisibility(View.GONE);

        NestedScrollView scrollView = commentDialog.findViewById(R.id.stickyScrollView);


        apiRepository.getComments(preferenceRepository.getToken(), post_id, currentCommentPage, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {


                isCommentLoading = false;

                assert scrollView != null;
                scrollView.fling(0);
                commentProgressContainer.setVisibility(View.GONE);
                commentDialog.findViewById(R.id.progressBarBottom).setVisibility(View.INVISIBLE);

                maxCountComment = response.get("count").getAsInt();

                JsonArray jsonArray = response.getAsJsonArray("data");

                if (jsonArray.size() > 0) {
                    commentNot.setVisibility(View.GONE);
                    currentCommentPage++;
                } else {
                    commentNot.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < jsonArray.size(); i++) {

                    Gson gson = new Gson();
                    CommentItem item = gson.fromJson(jsonArray.get(i), CommentItem.class);
                    if (!item.getBody().trim().equals("")) {
                        commentItems.add(item);
                        commentAdapter.notifyItemInserted(commentItems.size());
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });


    }

    public void loadPostDetails(String post_id) {

        LinearLayout header = commentDialog.findViewById(R.id.header);

        skeletonCommentHeader = Skeleton.bind(header)
                .load(R.layout.skeleton_alert_comment_header)
                .color(R.color.ddd)
                .show();

        apiRepository.getPostDetails(preferenceRepository.getToken(), post_id, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                skeletonCommentHeader.hide();

                if (response != null && response.has("data") && !response.get("data").isJsonNull()) {
                    JsonObject ob = response.get("data").getAsJsonObject();
                    JsonObject author = ob.getAsJsonObject("author");
                    String authorName = author.get("full_name").getAsString();
                    String authorImage = author.get("compressed_image").getAsString();
                    boolean isAdmin = author.get("is_admin").getAsBoolean();
                    String postText = ob.get("body").getAsString();
                    String date = ob.get("created_at").getAsString();
                    String postImageUrl = ob.get("attachment").getAsString();

                    initCommentHeader(authorName, authorImage, isAdmin, postText, date, postImageUrl);

                } else {
                    Toast.makeText(context, "Post is not available", Toast.LENGTH_SHORT).show();
                    if (commentDialog.isShowing())
                        commentDialog.dismiss();
                }


            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }


    public void getPosts(int page) {

        loading = false;
        String url;

        if (type.equals("public"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?page=" + page;
        else if (type.equals("pending"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?status=pending&page=" + page;
        else if (type.equals("my"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts/my-posts/?page=" + page;
        else
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?type=" + type + "&page=" + page;

        if (page == 1)
            progressContainer.setVisibility(View.VISIBLE);

        if (page > 1)
            bottomProgressBar.setVisibility(View.VISIBLE);


        apiRepository.getNewsfeedPosts(preferenceRepository.getToken(), url, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                loading = true;
                progressContainer.setVisibility(View.GONE);
                bottomProgressBar.setVisibility(View.INVISIBLE);

                JsonArray jsonArray = response.getAsJsonArray("posts");
                if (jsonArray.size() == 0 && page == 1) {
                    not.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                } else {

                    if (page == 1)
                        not.setVisibility(View.GONE);

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject ob = jsonArray.get(i).getAsJsonObject();

                        NewsfeedItem item = new NewsfeedItem();

                        item.setAuthorUsername(ob.get("username").getAsString());
                        item.setAuthorFullName(ob.get("author_full_name").getAsString());
                        item.setAuthorImage(ob.get("author_compressed_image").getAsString());
                        item.setIsAdmin(ob.get("author_is_admin").getAsInt() != 0);
                        item.setBody(ob.get("body").getAsString());
                        item.setAttachment(ob.get("attachment").isJsonNull() ? "null" : ob.get("attachment").getAsString());
                        item.setAttachmentCompressed(ob.get("attachment_compressed_url").isJsonNull() ? "null" : ob.get("attachment_compressed_url").getAsString());
                        item.setCreatedAt(ob.get("created_at").getAsString());
                        item.setUpdatedAt(ob.get("created_at").getAsString());
                        item.setFavorited(ob.get("favorited").getAsInt() != 0);
                        item.setFavoriteCount(ob.get("favorites_count").getAsInt());
                        item.setCommentsCount(ob.get("comments_count").getAsInt());
                        item.setSlug(ob.get("slug").getAsString());
                        item.setType(ob.get("type").getAsString());

                        itemsList.add(item);
                        adapter.notifyItemInserted(itemsList.size());

                    }
                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                progressContainer.setVisibility(View.GONE);
                bottomProgressBar.setVisibility(View.INVISIBLE);
            }

        });

    }


    public void reloadRecyclerComment() {
        currentCommentPage = 1;
        maxCountComment = -1;
        commentItems.clear();
        commentAdapter.notifyDataSetChanged();
        loadComments(selectedPostID);

    }


    public void reloadRecyclerReply() {
        currentReplyPage = 1;
        maxCountReply = -1;
        replyItems.clear();
        replyAdapter.notifyDataSetChanged();
        loadReplies(selectedCommentID);

    }


    public void createComment() {

        if (commentDialog == null)
            return;

        commentInput.setEnabled(false);
        submitComment.setEnabled(false);

        JsonObject parameters = new JsonObject();
        JsonObject parametersPost = new JsonObject();

        parameters.addProperty("body", commentInput.getText().toString());
        parametersPost.add("comment", parameters);

        apiRepository.postComment(preferenceRepository.getToken(), selectedPostID, parametersPost, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if (response.has("data")) {
                    reloadRecyclerComment();
                    commentInput.setText("");
                    commentInput.setEnabled(true);
                    submitComment.setEnabled(true);
                } else
                    Toast.makeText(context, "Couldn't create comment", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }


    public void sendLike(String slug, boolean like) {

        apiRepository.postLike(preferenceRepository.getToken(), slug, like, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commentDialog != null)
            commentDialog.dismiss();
        if (replyDialog != null)
            replyDialog.dismiss();
    }


    @Override
    public void onSharePost(Object object) {
        NewsfeedItem model = (NewsfeedItem) object;
        shareWithContacts(model);
    }

    private void shareWithContacts(NewsfeedItem model) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.share_with_contact_view);

        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        assert bottomSheetInternal != null;
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    bottomSheet.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        bottomSheetDialog.setCanceledOnTouchOutside(false);
        RecyclerView rvContacts = bottomSheetDialog.findViewById(R.id.rvContacts);
        ImageView ivBack = bottomSheetDialog.findViewById(R.id.back);
        EditText etSearch = bottomSheetDialog.findViewById(R.id.etSearch);
        TextView tvCount = bottomSheetDialog.findViewById(R.id.tvCount);
        LinearLayout llSend = bottomSheetDialog.findViewById(R.id.llSend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        assert rvContacts != null;
        rvContacts.setLayoutManager(layoutManager);

        assert ivBack != null;
        ivBack.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }


}
