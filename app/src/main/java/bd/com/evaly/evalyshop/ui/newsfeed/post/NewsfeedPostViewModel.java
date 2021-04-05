package bd.com.evaly.evalyshop.ui.newsfeed.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.gson.JsonObject;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.executor.MainThreadExecutor;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NewsfeedPostViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<NewsfeedPost>> postLiveData = new MutableLiveData<>();
    private LiveData<NewsfeedPostDataSource> dataSourceLiveData;
    private String type = "public";
    private NewsfeedPostDataFactory feedDataFactory;
    private MutableLiveData<NewsfeedPost> editPostLiveData = new MutableLiveData<>();

    @Inject
    public NewsfeedPostViewModel(ApiRepository apiRepository){
        this.apiRepository = apiRepository;
    }

    public void setType(String type) {
        this.type = type;

        MainThreadExecutor executor = new MainThreadExecutor();

        feedDataFactory = new NewsfeedPostDataFactory(type, apiRepository);

        feedDataFactory.create();

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setPageSize(10).build();

        postLiveData = new LivePagedListBuilder<Integer, NewsfeedPost>(feedDataFactory, pagedListConfig)
                .setFetchExecutor(executor)
                .build();

        dataSourceLiveData = feedDataFactory.getMutableLiveData();

        networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
                NewsfeedPostDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<NewsfeedPost>> getPostLiveData() {
        return postLiveData;
    }


    public void sendLike(String slug, boolean like) {

        apiRepository.postLike(CredentialManager.getToken(), slug, like, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    sendLike(slug, like);

            }
        });
    }

    public void refreshData() {

        if (postLiveData.getValue() != null)
            postLiveData.getValue().getDataSource().invalidate();

    }


    public void deletePost(String postId) {

        String url = UrlUtils.BASE_URL_NEWSFEED + "posts/" + postId;

        apiRepository.deleteItem(CredentialManager.getToken(), url, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public LiveData<NewsfeedPost> getEditPostLiveData() {
        return editPostLiveData;
    }

    public void setEditPostLiveData(NewsfeedPost editPostLiveData) {
        this.editPostLiveData.setValue(editPostLiveData);
    }
}
