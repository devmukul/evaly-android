package bd.com.evaly.evalyshop.ui.newsfeed.comment.pagedList;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;\

public class CommentDataSource extends PageKeyedDataSource<Integer, CommentItem> {


    private MutableLiveData networkState = new MutableLiveData();

    private String postId;

    public CommentDataSource(String postId) {
        this.postId = postId;
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, CommentItem> callback) {


        networkState.postValue(NetworkState.LOADING_FIRST);

        int currentPage = 1;

        NewsfeedApiHelper.getCommentList(CredentialManager.getToken(), postId, currentPage, new ResponseListenerAuth<CommonDataResponse<List<CommentItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CommentItem>> response, int statusCode) {
                callback.onResult(response.getData(), null, 2);
                networkState.postValue(NetworkState.LOADED);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorBody));

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, CommentItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, CommentItem> callback) {


        networkState.postValue(NetworkState.LOADING_FIRST);


        NewsfeedApiHelper.getCommentList(CredentialManager.getToken(), postId, params.key, new ResponseListenerAuth<CommonDataResponse<List<CommentItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CommentItem>> response, int statusCode) {

                Integer nextKey = (params.key == response.getCount()) ? null : params.key + 1;
                callback.onResult(response.getData(), nextKey);

                if (nextKey != null)
                    networkState.postValue(NetworkState.LOADED);
                else
                    networkState.postValue(new NetworkState(NetworkState.Status.SUCCESS, "Completed"));
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorBody));

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }


}
