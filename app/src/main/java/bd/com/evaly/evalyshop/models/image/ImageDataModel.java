package bd.com.evaly.evalyshop.models.image;

import com.google.gson.annotations.SerializedName;

public class ImageDataModel {

    @SerializedName("url")
    private String url;

    @SerializedName("url_sm")
    private String urlSm;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlSm() {
        return urlSm;
    }

    public void setUrlSm(String urlSm) {
        this.urlSm = urlSm;
    }
}
