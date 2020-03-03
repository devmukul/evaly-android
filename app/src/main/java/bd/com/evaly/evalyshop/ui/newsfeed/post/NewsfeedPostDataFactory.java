package bd.com.evaly.evalyshop.ui.newsfeed.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class NewsfeedPostDataFactory extends DataSource.Factory {

    private MutableLiveData<NewsfeedPostDataSource> mutableLiveData;
    private NewsfeedPostDataSource feedDataSource;
    private String type;


    public NewsfeedPostDataFactory(String type) {
        this.type = type;
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

        feedDataSource = new NewsfeedPostDataSource(type);
        mutableLiveData.postValue(feedDataSource);

        return feedDataSource;
    }
}
