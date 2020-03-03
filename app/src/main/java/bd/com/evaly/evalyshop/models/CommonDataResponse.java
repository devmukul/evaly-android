package bd.com.evaly.evalyshop.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonDataResponse<T> {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("data")
    @Expose
    private T data;


    @SerializedName("posts")
    @Expose
    private T posts;

    @SerializedName("meta")
    @Expose
    private JsonObject meta;

    public JsonObject getMeta() {
        return meta;
    }

    public void setMeta(JsonObject meta) {
        this.meta = meta;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getPosts() {
        return posts;
    }

    public void setPosts(T posts) {
        this.posts = posts;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
