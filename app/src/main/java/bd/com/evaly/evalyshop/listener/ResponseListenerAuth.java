package bd.com.evaly.evalyshop.listener;

public interface ResponseListenerAuth<T, V> {
    void onDataFetched(T response, int statusCode);
    void onFailed(V errorBody, int errorCode);
    void onAuthError(boolean logout);
}