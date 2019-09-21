package bd.com.evaly.evalyshop.activity.newsfeed;

import android.content.Context;
import android.content.Intent;
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

public class NewsfeedPendingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String type;
    private RecyclerView recyclerView;
    private NewsfeedPendingAdapter adapter;
    private ArrayList<NewsfeedItem> itemsList;
    private Context context;
    private NewsfeedActivity activity;
    private UserDetails userDetails;
    private LinearLayout not, progressContainer;

    // newfeed scroller
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int currentPage;

    private ProgressBar bottomProgressBar;
    private SwipeRefreshLayout swipeLayout;
    private RequestQueue queue;




    public NewsfeedPendingFragment() {
        // Required empty public constructor
    }

    public static NewsfeedPendingFragment newInstance(String type) {
        NewsfeedPendingFragment fragment = new NewsfeedPendingFragment();
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


        adapter = new NewsfeedPendingAdapter(itemsList, context, this);


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
        RequestQueue queue= Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }



    SkeletonScreen skeletonCommentHeader;


    public void loadPostDetails(String post_id){


        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+post_id;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                skeletonCommentHeader.hide();

                try {

                    JSONObject ob = response.getJSONObject("data");

                    JSONObject author = ob.getJSONObject("author");

                    String authorName = author.getString("full_name");
                    String authorImage = author.getString("compressed_image");
                    String postText = ob.getString("body");
                    String date = ob.getString("created_at");
                    String postImageUrl = ob.getString("attachment");


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

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
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




    @Override
    public void onDestroy()
    {

        super.onDestroy();


    }


}
