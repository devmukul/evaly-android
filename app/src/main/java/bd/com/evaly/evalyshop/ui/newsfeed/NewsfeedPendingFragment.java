package bd.com.evaly.evalyshop.ui.newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.NewsfeedPendingAdapter;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NewsfeedPendingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    SkeletonScreen skeletonCommentHeader;
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
    private int currentPage;
    private ProgressBar bottomProgressBar;
    private SwipeRefreshLayout swipeLayout;

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

        } catch (Exception e) {

        }

    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        userDetails = new UserDetails(context);

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


        adapter = new NewsfeedPendingAdapter(itemsList, context, this);

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

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            getPosts(++currentPage);

                        }
                    }
                }
            }
        });

        getPosts(currentPage);

    }

    public void action(String id, final String type, int position) {

        JsonObject parameter = new JsonObject();
        JsonObject parameterPost = new JsonObject();

        if (type.equals("reject"))
            parameter.addProperty("status", "archieved");
        else if (type.equals("approve"))
            parameter.addProperty("status", "active");

        parameterPost.add("post", parameter);

        itemsList.remove(position);
        adapter.notifyItemRemoved(position);

        NewsfeedApiHelper.actionPendingPost(CredentialManager.getToken(), id, type, parameterPost, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    action(id, type, position);
            }
        });


    }


    private void getPosts(int page) {

        loading = false;

        String url = UrlUtils.BASE_URL_NEWSFEED + "posts?status=pending&page=" + page;

        if (page == 1)
            progressContainer.setVisibility(View.VISIBLE);

        if (page > 1)
            bottomProgressBar.setVisibility(View.VISIBLE);


        NewsfeedApiHelper.getNewsfeedPosts(CredentialManager.getToken(), url, new ResponseListenerAuth<JsonObject, String>() {
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

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getPosts(page);
            }
        });


    }


    @Override
    public void onDestroy() {

        super.onDestroy();


    }


}
