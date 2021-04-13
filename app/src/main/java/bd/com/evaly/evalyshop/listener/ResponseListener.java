package bd.com.evaly.evalyshop.listener;

public interface ResponseListener<T, V> {
    void onDataFetched(T response, int statusCode);
    void onFailed(V errorBody, int errorCode);
}