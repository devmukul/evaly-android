package bd.com.evaly.evalyshop.ui.newsfeed.post;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.UrlUtils;

public class NewsfeedPostDataSource extends PageKeyedDataSource<Integer, NewsfeedPost> {


    private MutableLiveData<NetworkState> networkState = new MutableLiveData();
    private String type = "public";
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;

    public NewsfeedPostDataSource(String type, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.type = type;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, NewsfeedPost> callback) {

        networkState.postValue(NetworkState.LOADING_FIRST);

        String url;
        int currentPage = 1;

        if (type == null)
            type = "public";

        if (type.equals("public"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?page=" + currentPage;
        else if (type.equals("pending"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?status=pending&page=" + currentPage;
        else if (type.equals("my"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts/my-posts/?page=" + currentPage;
        else
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?type=" + type + "&page=" + currentPage;

        apiRepository.getNewsfeedPostsList(preferenceRepository.getToken(), url, new ResponseListenerAuth<CommonDataResponse<List<NewsfeedPost>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<NewsfeedPost>> response, int statusCode) {
                callback.onResult(response.getPosts(), null, 2);
                // callback.onResult(response.getPosts(), currentPage, response.getCount(), null, 2);
                networkState.postValue(NetworkState.LOADED);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorBody));
            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    loadInitial(params, callback);

            }
        });


    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, NewsfeedPost> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, NewsfeedPost> callback) {


        String url;

        Integer currentPage = params.key;

        if (type.equals("public"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?page=" + currentPage;
        else if (type.equals("pending"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?status=pending&page=" + currentPage;
        else if (type.equals("my"))
            url = UrlUtils.BASE_URL_NEWSFEED + "posts/my-posts/?page=" + currentPage;
        else
            url = UrlUtils.BASE_URL_NEWSFEED + "posts?type=" + type + "&page=" + currentPage;

        apiRepository.getNewsfeedPostsList(preferenceRepository.getToken(), url, new ResponseListenerAuth<CommonDataResponse<List<NewsfeedPost>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<NewsfeedPost>> response, int statusCode) {

                Integer nextKey = (params.key == response.getCount()) ? null : params.key + 1;
                callback.onResult(response.getPosts(), nextKey);
                networkState.postValue(NetworkState.LOADED);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorBody));
            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    loadAfter(params, callback);
            }
        });


    }


}
