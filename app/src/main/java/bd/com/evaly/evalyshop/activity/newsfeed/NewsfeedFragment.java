package bd.com.evaly.evalyshop.activity.newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedAdapter;
import bd.com.evaly.evalyshop.models.NewsfeedItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NewsfeedFragment extends Fragment {

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        userDetails=new UserDetails(context);
        not = view.findViewById(R.id.not);
        progressContainer = view.findViewById(R.id.progressContainer);
        bottomProgressBar = view.findViewById(R.id.progressBar);


        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new NewsfeedAdapter(itemsList, context);
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
        RequestQueue queue= Volley.newRequestQueue(context);
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
