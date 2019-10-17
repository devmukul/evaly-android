package bd.com.evaly.evalyshop.listener;

public interface ProductListener {
    void onSuccess(int count);
    void buyNow(String product_slug);
}