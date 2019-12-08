package bd.com.evaly.evalyshop.models.newsfeed;

public class FeedShareModel {
    private String f_slug;
    private String f_body;
    private String f_attachment_compressed_url;
    private String f_comments_count;
    private String f_favorites_count;
    private String f_author_full_name;

    public FeedShareModel(String f_slug, String f_body, String f_attachment_compressed_url, String f_comments_count, String f_favorites_count, String f_author_full_name) {
        this.f_slug = f_slug;
        this.f_body = f_body;
        this.f_attachment_compressed_url = f_attachment_compressed_url;
        this.f_comments_count = f_comments_count;
        this.f_favorites_count = f_favorites_count;
        this.f_author_full_name = f_author_full_name;
    }

    public String getF_slug() {
        return f_slug;
    }

    public void setF_slug(String f_slug) {
        this.f_slug = f_slug;
    }

    public String getF_body() {
        return f_body;
    }

    public void setF_body(String f_body) {
        this.f_body = f_body;
    }

    public String getF_attachment_compressed_url() {
        return f_attachment_compressed_url;
    }

    public void setF_attachment_compressed_url(String f_attachment_compressed_url) {
        this.f_attachment_compressed_url = f_attachment_compressed_url;
    }

    public String getF_comments_count() {
        return f_comments_count;
    }

    public void setF_comments_count(String f_comments_count) {
        this.f_comments_count = f_comments_count;
    }

    public String getF_favorites_count() {
        return f_favorites_count;
    }

    public void setF_favorites_count(String f_favorites_count) {
        this.f_favorites_count = f_favorites_count;
    }

    public String getF_author_full_name() {
        return f_author_full_name;
    }

    public void setF_author_full_name(String f_author_full_name) {
        this.f_author_full_name = f_author_full_name;
    }
}
