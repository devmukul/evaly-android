package bd.com.evaly.evalyshop.ui.chat.viewmodel;

public interface ImageUploadView {
    void onImageUploadSuccess(String img, String imgSm);

    void onImageUploadFailed(String msg);

}
