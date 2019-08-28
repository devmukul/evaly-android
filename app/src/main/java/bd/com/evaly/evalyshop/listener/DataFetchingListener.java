package bd.com.evaly.evalyshop.listener;

public interface DataFetchingListener <T>{
    void onDataFetched(T response);
    void onFailed(int status);
}
