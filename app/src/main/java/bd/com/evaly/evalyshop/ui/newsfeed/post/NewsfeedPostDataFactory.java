package bd.com.evaly.evalyshop.ui.newsfeed.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import bd.com.evaly.evalyshop.rest.ApiRepository;

public class NewsfeedPostDataFactory extends DataSource.Factory {

    private MutableLiveData<NewsfeedPostDataSource> mutableLiveData;
    private NewsfeedPostDataSource feedDataSource;
    private String type;
    private ApiRepository apiRepository;


    public NewsfeedPostDataFactory(String type, ApiRepository apiRepository) {
        this.type = type;
        this.apiRepository = apiRepository;
        this.mutableLiveData = new MutableLiveData<NewsfeedPostDataSource>();
    }

    public NewsfeedPostDataSource getFeedDataSource() {
        return feedDataSource;
    }



    public LiveData<NewsfeedPostDataSource> getMutableLiveData() {

        return mutableLiveData;
    }

    @Override
    public DataSource create() {

        feedDataSource = new NewsfeedPostDataSource(type, apiRepository);
        mutableLiveData.postValue(feedDataSource);

        return feedDataSource;
    }
}
