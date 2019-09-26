package bd.com.evaly.evalyshop.activity.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ImagePreview;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.CommentAdapter;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedAdapter;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedPendingAdapter;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.ReplyAdapter;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;

public class NewsfeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String type;
    private RecyclerView recyclerView;
    private NewsfeedAdapter adapter;
    private ArrayList<NewsfeedItem> itemsList;
    private Context context;
    private NewsfeedActivity activity;
    private UserDetails userDetails;
    private LinearLayout not, progressContainer;
    private RequestQueue queue;

    // newfeed scroller
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
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
    private boolean isCommentLoading = false;



    // reply bottom sheet items

    private BottomSheetBehavior bottomSheetBehaviorReply;
    private BottomSheetDialog replyDialog;
    private String selectedCommentID = "";
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
        activity = (NewsfeedActivity) getActivity();
        queue= Volley.newRequestQueue(context);

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
        selectedCommentID = "";

        getPosts(currentPage);

        try {

            ((NewsfeedActivity) getActivity()).getNotificationCount();

        } catch (Exception e){

        }



    }

    public UserDetails getUserDetails(){
        return userDetails;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        userDetails = new UserDetails(context);


        maxCountNewfeed = -1;
        maxCountComment = -1;
        maxCountReply = -1;

        // Reply bottom sheet

        currentReplyPage = 1;

        replyDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        replyDialog.setContentView(R.layout.alert_replies);

        View bottomSheetInternalReply = replyDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheetInternalReply.setPadding(0, 0, 0, 0);
        bottomSheetBehaviorReply = BottomSheetBehavior.from(bottomSheetInternalReply);
        bottomSheetBehaviorReply.setState(BottomSheetBehavior.STATE_EXPANDED);



        ScreenUtils screenUtils = new ScreenUtils(context);
        LinearLayout dialogLayout = replyDialog.findViewById(R.id.container2);
        dialogLayout.setMinimumHeight(screenUtils.getHeight());

        bottomSheetBehaviorReply.setPeekHeight(screenUtils.getHeight());
        replyNot = replyDialog.findViewById(R.id.not);
        replyProgressContainer = replyDialog.findViewById(R.id.progressContainer);


        // Reply recyclerView

        replyItems = new ArrayList<>();


        replyAdapter = new ReplyAdapter(replyItems, context, this);


        replyRecyclerView = replyDialog.findViewById(R.id.recyclerView);

        LinearLayoutManager managerReply=new LinearLayoutManager(context);
        replyRecyclerView.setLayoutManager(managerReply);
        replyRecyclerView.setAdapter(replyAdapter);


        bottomSheetBehaviorReply.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    currentReplyPage = 1;
                    selectedCommentID = "";
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

            nestedScrollViewReply.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (nestedScrollViewReply.getChildAt(0).getBottom()
                            <= (nestedScrollViewReply.getHeight() + nestedScrollViewReply.getScrollY())) {
                        Log.i("Nested", "BOTTOM SCROLL");

                        try {

                            if (!isReplyLoading)
                                loadReplies(selectedCommentID);

                        } catch (Exception e)
                        {
                            Log.e("load more error", e.toString());
                        }
                    }
                }
            });
        }



        // create Reply

        replyInput = replyDialog.findViewById(R.id.commentInput);
        uploadImage = replyDialog.findViewById(R.id.uploadImage);
        submitReply = replyDialog.findViewById(R.id.submitComment);
        reloadReply = replyDialog.findViewById(R.id.refresh);

        if (userDetails.getToken().equals("")){

            replyInput.setText("You need to login to post reply.");
            replyInput.setEnabled(false);
            submitReply.setEnabled(false);

        } else {
            replyInput.setEnabled(true);
            submitReply.setEnabled(true);
        }


        uploadImage.setOnClickListener(view1 -> Toast.makeText(context,"Photo reply is disabled now.", Toast.LENGTH_SHORT).show());
        reloadReply.setOnClickListener(view1 -> reloadRecyclerReply());

        submitReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPostID.equals(""))
                    Toast.makeText(context, "Couldn't post reply. Try again later.", Toast.LENGTH_SHORT).show();
                else if (replyInput.getText().toString().trim().equals(""))
                    Toast.makeText(context, "Write something first before submitting", Toast.LENGTH_SHORT).show();
                else
                    createReply();

            }
        });



        // comment bottom sheet

        currentCommentPage = 1;

        commentDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        commentDialog.setContentView(R.layout.alert_comments);

        View bottomSheetInternalComment = commentDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
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
        dialogLayoutReply.setMinimumHeight(screenUtils.getHeight());

        bottomSheetBehaviorComment.setPeekHeight(screenUtils.getHeight());
        commentNot = commentDialog.findViewById(R.id.not);
        commentProgressContainer = commentDialog.findViewById(R.id.progressContainer);


        // comment recyclerView

        commentItems = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentItems, context, this);
        commentRecyclerView = commentDialog.findViewById(R.id.recyclerView);
        LinearLayoutManager managerComment=new LinearLayoutManager(context);
        commentRecyclerView.setLayoutManager(managerComment);
        commentRecyclerView.setAdapter(commentAdapter);

        NestedScrollView nestedScrollViewComment = commentDialog.findViewById(R.id.stickyScrollView);

        if (nestedScrollViewComment != null) {

            nestedScrollViewComment.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    Log.d("nested", "BOTTOM SCROLL all");

                    if (nestedScrollViewComment.getChildAt(0).getBottom()
                            <= (nestedScrollViewComment.getHeight() + nestedScrollViewComment.getScrollY())) {
                        Log.d("nested inside", "BOTTOM SCROLL");

                        try {

                            if (!isCommentLoading)
                                loadComments(selectedPostID, false);

                        } catch (Exception e) {
                            Log.e("load more error", e.toString());
                        }
                    }
                }
            });
        }

        commentRecyclerView.setFocusable(false);
        nestedScrollViewComment.requestFocus();




        // create comment

        commentInput = commentDialog.findViewById(R.id.commentInput);
        uploadImage = commentDialog.findViewById(R.id.uploadImage);
        submitComment = commentDialog.findViewById(R.id.submitComment);
        reloadComment = commentDialog.findViewById(R.id.refresh);

        if (userDetails.getToken().equals("")){

            commentInput.setEnabled(false);
            commentInput.setText("You need to login to make comment.");
            submitComment.setEnabled(false);

        }


        uploadImage.setOnClickListener(view1 -> Toast.makeText(context,"Photo comment is disabled now.", Toast.LENGTH_SHORT).show());
        reloadComment.setOnClickListener(view1 -> reloadRecyclerComment());

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPostID.equals(""))
                    Toast.makeText(context, "Couldn't create comment. Try again later.", Toast.LENGTH_SHORT).show();
                else if (commentInput.getText().toString().trim().equals(""))
                    Toast.makeText(context, "Write something first before submitting", Toast.LENGTH_SHORT).show();
                else
                    createComment();

            }
        });


        // pull to refresh
        swipeLayout =  view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        try {
            swipeLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                    android.R.color.holo_red_dark,
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_orange_dark);
        } catch (Exception e){

        }

        not = view.findViewById(R.id.not);
        progressContainer = view.findViewById(R.id.progressContainer);
        bottomProgressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);


        adapter = new NewsfeedAdapter(itemsList, context, this);


        recyclerView.setAdapter(adapter);

        currentPage = 1;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            getPosts(++currentPage);

                        }
                    }
                }
            }
        });

        getPosts(currentPage);


    }



    public void openReplyBottomSheet(String id, String authorName, String authorImage, boolean isAdmin, String postText, String date, Object postImage){


        if (replyDialog != null){

            currentReplyPage = 1;
            maxCountReply = -1;


            TextView authorNameView = replyDialog.findViewById(R.id.user_name);

            if (isAdmin) {
                int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

                Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
                img.setBounds(0, 0, sizeInPixel, sizeInPixel);
                authorNameView.setCompoundDrawables(null, null, img, null);
                authorNameView.setCompoundDrawablePadding(15);
            }else {
                authorNameView.setCompoundDrawables(null, null, null, null);
            }

            authorNameView.setText(authorName);
            ((TextView) replyDialog.findViewById(R.id.text)).setText(postText);
            ((TextView) replyDialog.findViewById(R.id.date)).setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", date)));

            ImageView userPic = replyDialog.findViewById(R.id.picture);

            Glide.with(context)
                    .load(authorImage)
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(200, 200))
                    .into(userPic);


            ImageView postPic = replyDialog.findViewById(R.id.postImage);

            if (postImage == null || postImage.equals("")){} else {
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


        } else
        {
            Toast.makeText(context, "Couldn't load replies", Toast.LENGTH_SHORT).show();
        }

    }



    public void openCommentBottomSheet(String id, String authorName, String authorImage, boolean isAdmin, String postText, String date, String postImageUrl){


        if (commentDialog != null){

            currentCommentPage = 1;
            maxCountComment = -1;
            selectedPostID = id;

            commentItems.clear();
            commentAdapter.notifyDataSetChanged();
            commentDialog.show();
            bottomSheetBehaviorComment.setState(BottomSheetBehavior.STATE_EXPANDED);

            if (authorName.equals("") && authorImage.equals("") && postText.equals("")) {
                loadComments(selectedPostID, true);
                loadPostDetails(selectedPostID);
            }
            else {

                initCommentHeader(authorName, authorImage, isAdmin, postText, date, postImageUrl);
                loadComments(selectedPostID, false);

            }
            ((NestedScrollView) commentDialog.findViewById(R.id.stickyScrollView)).fullScroll(ScrollView.FOCUS_UP);


        } else
        {
            Toast.makeText(context, "Couldn't load comments", Toast.LENGTH_SHORT).show();
        }


    }

    public void initCommentHeader(String authorName, String authorImage, boolean isAdmin, String postText, String date, String postImageUrl){


        TextView authorNameView = commentDialog.findViewById(R.id.user_name);
        TextView timeView = commentDialog.findViewById(R.id.date);

        String timeAgo = Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", date));

        authorNameView.setText(authorName);


        if (isAdmin) {
            int sizeInPixel = context.getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);

            Drawable img = context.getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
            img.setBounds(0, 0, sizeInPixel, sizeInPixel);
            authorNameView.setCompoundDrawables(null, null, img, null);
            authorNameView.setCompoundDrawablePadding(15);
            timeView.setText(Html.fromHtml("<b>Admin</b> · " + timeAgo));

        } else  {
            authorNameView.setCompoundDrawables(null, null, null, null);

            timeView.setText(timeAgo);
        }


        ((TextView) commentDialog.findViewById(R.id.text)).setText(postText);
        ImageView userPic = commentDialog.findViewById(R.id.picture);
        Glide.with(context)
                .load(authorImage)
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .into(userPic);

        ImageView postPic = commentDialog.findViewById(R.id.postImage);

        if (postImageUrl == null || postImageUrl.equals("")){} else {
            Glide.with(context)
                    .load(postImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(900, 900))
                    .into(postPic);

            postPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ImagePreview.class);
                    intent.putExtra("image", postImageUrl);
                    context.startActivity(intent);
                }
            });

        }


    }




    public void loadReplies(String comment_id){


        isReplyLoading = true;

        if (!commentDialog.isShowing()){
            Toast.makeText(context, "Can't load comments. Restart the app", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentReplyPage == 1) {
            ((ProgressBar) replyDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.INVISIBLE);
            replyProgressContainer.setVisibility(View.VISIBLE);
        }

        if (currentReplyPage > 1)
            ((ProgressBar) replyDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.VISIBLE);

        replyNot.setVisibility(View.GONE);


        NestedScrollView scrollView = replyDialog.findViewById(R.id.stickyScrollView);

        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+selectedPostID+"/comments/"+ comment_id +"/replies?page="+ currentReplyPage;

        Log.d("json url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());

                scrollView.fling(0);

                isReplyLoading = false;

                ((ProgressBar) replyDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.INVISIBLE);
                replyProgressContainer.setVisibility(View.GONE);

                try {


                    JSONArray jsonArray = response.getJSONArray("data").getJSONObject(0).getJSONArray("replies");

                    if (jsonArray.length() > 0)
                        replyNot.setVisibility(View.GONE);
                    else {
                        replyNot.setVisibility(View.VISIBLE);
                        currentReplyPage++;

                    }

                    for (int i=0; i < jsonArray.length(); i++) {

                        Gson gson = new Gson();
                        RepliesItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), RepliesItem.class);

                        if (!item.getBody().trim().equals("")) {
                            replyItems.add(item);
                            replyAdapter.notifyItemInserted(replyItems.size());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                replyProgressContainer.setVisibility(View.GONE);
                ((ProgressBar) replyDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.INVISIBLE);


                // Toast.makeText(context, "Couldn't load replies.", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


    public void createReply(){

        if (replyDialog == null)
            return;

        replyInput.setEnabled(false);
        submitReply.setEnabled(false);

        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+selectedPostID+"/comments/"+selectedCommentID+"/replies";

        JSONObject parameters = new JSONObject();
        JSONObject parametersPost = new JSONObject();
        try {

            parameters.put("body", replyInput.getText().toString());
            parametersPost.put("comment", parameters);

        } catch (Exception e) {
        }

        Log.d("json body", parametersPost.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parametersPost,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());
                try {
                    if (response.has("data")) {
                        reloadRecyclerReply();
                        replyInput.setText("");
                        replyInput.setEnabled(true);
                        submitReply.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                Toast.makeText(context, "Couldn't create comment", Toast.LENGTH_SHORT).show();
                commentInput.setEnabled(true);
                submitComment.setEnabled(true);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }





    public void deletePost(String id, final String type){

        if (replyDialog == null)
            return;

        replyInput.setEnabled(false);
        submitReply.setEnabled(false);

        String url;

        if (type.equals("post"))
            url = UrlUtils.BASE_URL_NEWSFEED+"posts/"+id;
        else
            url = UrlUtils.BASE_URL_NEWSFEED+"comments/"+id;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (type.equals("post")) {

                    itemsList.clear();
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    getPosts(currentPage);

                } else if (type.equals("comment")){

                    commentItems.clear();
                    commentAdapter.notifyDataSetChanged();

                    currentCommentPage = 1;

                    loadComments(selectedPostID, false);
                } else {

                    replyItems.clear();
                    replyAdapter.notifyDataSetChanged();

                    currentReplyPage = 1;

                    loadReplies(selectedCommentID);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                Toast.makeText(context, "Couldn't delete post", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


    public void loadComments(String post_id, boolean fromRoute){

        if (maxCountComment == commentItems.size()) {

            commentProgressContainer.setVisibility(View.GONE);
            ((ProgressBar) commentDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.GONE);
            return;
        }

        isCommentLoading = true;
        selectedPostID = post_id;

        if (!commentDialog.isShowing()){
            Toast.makeText(context, "Can't load comments. Restart the app", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentCommentPage == 1) {
            commentProgressContainer.setVisibility(View.VISIBLE);
            ((ProgressBar) commentDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.INVISIBLE);
        }

        if (currentCommentPage > 1)
            ((ProgressBar) commentDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.VISIBLE);

        replyNot.setVisibility(View.GONE);

        NestedScrollView scrollView = commentDialog.findViewById(R.id.stickyScrollView);
        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+post_id+"/comments?page="+currentCommentPage;

        currentCommentPage++;

        Log.d("json url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                scrollView.fling(0);

                isCommentLoading = false;

                Log.d("json response", response.toString());
                commentProgressContainer.setVisibility(View.GONE);
                ((ProgressBar) commentDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.INVISIBLE);


                try {

                    maxCountComment = response.getInt("count");

                    JSONArray jsonArray = response.getJSONArray("data");

                    if (jsonArray.length() > 0)
                        commentNot.setVisibility(View.GONE);
                    else {
                        commentNot.setVisibility(View.VISIBLE);
                        currentCommentPage++;
                    }

                    for (int i=0; i < jsonArray.length(); i++) {

                        Gson gson = new Gson();
                        CommentItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), CommentItem.class);

                        if (!item.getBody().trim().equals("")) {
                            commentItems.add(item);
                            commentAdapter.notifyItemInserted(commentItems.size());

                            //commentAdapter.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());


                ((ProgressBar) commentDialog.findViewById(R.id.progressBarBottom)).setVisibility(View.INVISIBLE);

                commentProgressContainer.setVisibility(View.GONE);

                //Toast.makeText(context, "Couldn't load comments.", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }




    SkeletonScreen skeletonCommentHeader;


    public void loadPostDetails(String post_id){


        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+post_id;


        LinearLayout header = commentDialog.findViewById(R.id.header);

        skeletonCommentHeader = Skeleton.bind(header)
                .load(R.layout.skeleton_alert_comment_header)
                .color(R.color.ddd)
                .show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                skeletonCommentHeader.hide();

                try {

                    JSONObject ob = response.getJSONObject("data");

                    JSONObject author = ob.getJSONObject("author");

                    String authorName = author.getString("full_name");
                    String authorImage = author.getString("compressed_image");
                    boolean isAdmin = author.getBoolean("is_admin");
                    String postText = ob.getString("body");
                    String date = ob.getString("created_at");
                    String postImageUrl = ob.getString("attachment");

                    initCommentHeader(authorName, authorImage, isAdmin, postText, date, postImageUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }



    public void getPosts(int page){


        loading = false;
        String url;

        if (type.equals("public"))
            url = UrlUtils.BASE_URL_NEWSFEED+"posts?page="+page;
        else if(type.equals("pending"))
            url = UrlUtils.BASE_URL_NEWSFEED+"posts?status=pending&page="+page;
        else
            url = UrlUtils.BASE_URL_NEWSFEED+"posts?type="+type+"&page="+page;

        if (page == 1)
            progressContainer.setVisibility(View.VISIBLE);

        if (page>1)
            bottomProgressBar.setVisibility(View.VISIBLE);

        Log.d("json url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());

                loading = true;
                progressContainer.setVisibility(View.GONE);
                bottomProgressBar.setVisibility(View.INVISIBLE);

                try {
                    JSONArray jsonArray = response.getJSONArray("posts");
                    if(jsonArray.length()==0 && page == 1){
                        not.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else{
                        not.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);

                            NewsfeedItem item = new NewsfeedItem();

                            item.setAuthorUsername(ob.getString("username"));
                            item.setAuthorFullName(ob.getString("author_full_name"));
                            item.setAuthorImage(ob.getString("author_compressed_image"));
                            item.setIsAdmin(ob.getInt("author_is_admin") != 0);
                            item.setBody(ob.getString("body"));


                            try {
                                item.setAttachment(ob.getString("attachment"));
                                item.setAttachmentCompressed(ob.getString("attachment_compressed_url"));
                            } catch (Exception e){
                                item.setAttachment(null);
                                item.setAttachmentCompressed(null);
                            }

                            item.setCreatedAt(ob.getString("created_at"));
                            item.setUpdatedAt(ob.getString("created_at"));
                            item.setFavorited(ob.getInt("favorited") != 0);
                            item.setFavoriteCount(ob.getInt("favorites_count"));
                            item.setCommentsCount(ob.getInt("comments_count"));
                            item.setSlug(ob.getString("slug"));
                            item.setType(ob.getString("type"));

                            itemsList.add(item);
                            adapter.notifyItemInserted(itemsList.size());

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());


                progressContainer.setVisibility(View.GONE);
                bottomProgressBar.setVisibility(View.INVISIBLE);

            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    public void reloadRecyclerComment(){
        currentCommentPage = 1;
        maxCountComment = -1;
        commentItems.clear();
        commentAdapter.notifyDataSetChanged();
        loadComments(selectedPostID, false);

    }


    public void reloadRecyclerReply(){
        currentReplyPage = 1;
        maxCountReply = -1;
        replyItems.clear();
        replyAdapter.notifyDataSetChanged();
        loadReplies(selectedCommentID);

    }


    public void createComment(){

        if (commentDialog == null)
            return;

        commentInput.setEnabled(false);
        submitComment.setEnabled(false);

        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+selectedPostID+"/comments";

        JSONObject parameters = new JSONObject();
        JSONObject parametersPost = new JSONObject();
        try {

            parameters.put("body", commentInput.getText().toString());

            parametersPost.put("comment", parameters);



        } catch (Exception e) {
        }

        Log.d("json body", parametersPost.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parametersPost,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());
                try {
                    if (response.has("data")) {
                        reloadRecyclerComment();
                        commentInput.setText("");
                        commentInput.setEnabled(true);
                        submitComment.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                Toast.makeText(context, "Couldn't create comment", Toast.LENGTH_SHORT).show();
                commentInput.setEnabled(true);
                submitComment.setEnabled(true);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }



    public void sendLike(String slug, boolean like){

        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+slug+"/favorite";

        JSONObject parametersPost = new JSONObject();

        Log.d("json url", url);

        JsonObjectRequest request = new JsonObjectRequest((like ? Request.Method.DELETE : Request.Method.POST), url, parametersPost,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());
                try {
                    if (response.has("data")) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                Toast.makeText(context, "Couldn't like the status.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    @Override
    public void onDestroy()
    {

        super.onDestroy();

        commentDialog.dismiss();
        replyDialog.dismiss();



    }


}
