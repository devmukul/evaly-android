package bd.com.evaly.evalyshop.listener;

public interface ResponseListener<T> {
    void onDataFetched(T response, int statusCode);
    void onFailed(int errorCode);
}