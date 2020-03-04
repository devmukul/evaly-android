package bd.com.evaly.evalyshop.ui.newsfeed.comment.pagedList;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class CommentDataFactory extends DataSource.Factory {

    private MutableLiveData<CommentDataSource> mutableLiveData;
    private CommentDataSource commentDataSource;
    private String postId;

    public CommentDataFactory(String postID) {
        this.mutableLiveData = new MutableLiveData<>();
        this.postId = postID;
    }

    @Override
    public DataSource create() {
        commentDataSource = new CommentDataSource(postId);
        mutableLiveData.postValue(commentDataSource);
        return commentDataSource;
    }

    public MutableLiveData<CommentDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
