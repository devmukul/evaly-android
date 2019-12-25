package bd.com.evaly.evalyshop.models.newsfeed;

public class CreatePostModel {
    private String title;
    private String type;
    private String body;
    private String attachment;

    public CreatePostModel(String title, String type, String body, String attachment) {
        this.title = title;
        this.type = type;
        this.body = body;
        this.attachment = attachment;
    }
}
