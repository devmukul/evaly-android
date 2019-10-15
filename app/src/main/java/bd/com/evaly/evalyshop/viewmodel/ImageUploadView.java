package bd.com.evaly.evalyshop.viewmodel;

public interface ImageUploadView {
    void onImageUploadSuccess(String img, String imgSm);

    void onImageUploadFailed(String msg);

}
