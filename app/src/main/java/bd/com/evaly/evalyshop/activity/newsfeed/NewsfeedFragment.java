package bd.com.evaly.evalyshop.activity.newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.CommentAdapter;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedAdapter;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
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
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int currentPage;
    private ProgressBar bottomProgressBar;
    private SwipeRefreshLayout swipeLayout;


    private RequestQueue queue;




    private BottomSheetBehavior bottomSheetBehaviorComment;
    private BottomSheetDialog commentDialog;
    private String selectedPostID = "";
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<CommentItem> commentItems;
    private LinearLayout commentNot;
    private LinearLayout commentProgressContainer;



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
        getPosts(currentPage);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // comment bottom sheet

        commentDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        commentDialog.setContentView(R.layout.alert_comments);

        View bottomSheetInternalComment = commentDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheetBehaviorComment = BottomSheetBehavior.from(bottomSheetInternalComment);
        bottomSheetBehaviorComment.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehaviorComment.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {

                    bottomSheet.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetBehaviorComment.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        ScreenUtils screenUtils = new ScreenUtils(context);
        LinearLayout dialogLayout = commentDialog.findViewById(R.id.container2);
        dialogLayout.setMinimumHeight(screenUtils.getHeight());

        commentNot = commentDialog.findViewById(R.id.not);
        commentProgressContainer = commentDialog.findViewById(R.id.progressContainer);


        // comment recyclerView

        commentItems = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentItems, context);
        commentRecyclerView = commentDialog.findViewById(R.id.recyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        commentRecyclerView.setLayoutManager(manager);
        commentRecyclerView.setAdapter(commentAdapter);


        swipeLayout =  view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        try {
            swipeLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                    android.R.color.holo_red_dark,
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_orange_dark);
        } catch (Exception e){

        }

        userDetails=new UserDetails(context);
        not = view.findViewById(R.id.not);
        progressContainer = view.findViewById(R.id.progressContainer);
        bottomProgressBar = view.findViewById(R.id.progressBar);


        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        LinearLayoutManager manager2=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager2);
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
                            loading = false;
                            getPosts(++currentPage);




                        }
                    }
                }
            }
        });



        getPosts(currentPage);



    }



    public void openCommentBottomSheet(String id, String authorName, String authorImage, String postText, String date){


        if (commentDialog != null){


            ((TextView) commentDialog.findViewById(R.id.user_name)).setText(authorName);
            ((TextView) commentDialog.findViewById(R.id.text)).setText(postText);
            ((TextView) commentDialog.findViewById(R.id.date)).setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", date)));

            ImageView userPic = commentDialog.findViewById(R.id.picture);

            Glide.with(context)
                    .load(authorImage)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(200, 200))
                    .into(userPic);


            selectedPostID = id;

            commentItems.clear();
            commentAdapter.notifyDataSetChanged();

            commentDialog.show();

            bottomSheetBehaviorComment.setState(BottomSheetBehavior.STATE_EXPANDED);

            loadComments(selectedPostID);

        } else
        {
            Toast.makeText(context, "Couldn't load comments", Toast.LENGTH_SHORT).show();
        }


    }






    public void loadComments(String post_id){


        if (!commentDialog.isShowing()){
            Toast.makeText(context, "Can't load comments. Restart the app", Toast.LENGTH_SHORT).show();
            return;
        }

        commentProgressContainer.setVisibility(View.VISIBLE);

        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+post_id+"/comments?page=1";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());



                commentProgressContainer.setVisibility(View.GONE);

                try {


                    JSONArray jsonArray = response.getJSONArray("data");

                    if (jsonArray.length() > 0)
                        not.setVisibility(View.GONE);
                    else
                        not.setVisibility(View.VISIBLE);


                    for (int i=0; i < jsonArray.length(); i++) {

                        Gson gson = new Gson();
                        CommentItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), CommentItem.class);
                        commentItems.add(item);
                        commentAdapter.notifyItemInserted(commentItems.size());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                commentProgressContainer.setVisibility(View.GONE);
                Toast.makeText(context, "Couldn't load comments.", Toast.LENGTH_SHORT).show();


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

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }




    public void getPosts(int page){

        String url;

        if (type.equals("public"))
            url = UrlUtils.BASE_URL_NEWSFEED+"posts?page="+page;
        else
            url = UrlUtils.BASE_URL_NEWSFEED+"posts?type="+type+"&page="+page;

        if (page == 1)
            progressContainer.setVisibility(View.VISIBLE);


        if (page>1)
            bottomProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());


                loading = true;

                progressContainer.setVisibility(View.GONE);
                bottomProgressBar.setVisibility(View.GONE);


                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    if(jsonArray.length()==0){
                        not.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else{
                        not.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);

                            NewsfeedItem item = new NewsfeedItem();

                            JSONObject author = ob.getJSONObject("author");
                            item.setAuthorUsername(author.getString("username"));
                            item.setAuthorFullName(author.getString("full_name"));
                            item.setAuthoeBio(author.getString("bio"));
                            item.setAuthorImage(author.getString("compressed_image"));
                            item.setAuthorFollowing(author.getBoolean("following"));

                            item.setBody(ob.getString("body"));

                            try {
                                item.setAttachment(ob.getString("attachment"));
                                item.setAttachmentCompressed(ob.getString("attachment_compressed_url"));
                            } catch (Exception e){
                                item.setAttachment(null);
                                item.setAttachmentCompressed(null);
                            }


                            item.setCreatedAt(ob.getString("created_at"));
                            item.setFavorited(ob.getBoolean("favorited"));
                            item.setFavoriteCount(ob.getInt("favorites_count"));
                            item.setCommentsCount(ob.getInt("comments_count"));
                            item.setSlug(ob.getString("slug"));
                            item.setTags(ob.getJSONArray("tag_list").toString());
                            item.setUpdatedAt(ob.getString("updated_at"));
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
                bottomProgressBar.setVisibility(View.GONE);

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
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }


}
