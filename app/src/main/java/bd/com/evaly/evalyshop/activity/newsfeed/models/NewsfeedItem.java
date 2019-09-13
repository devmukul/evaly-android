package bd.com.evaly.evalyshop.activity.newsfeed.models;

import java.util.ArrayList;

public class NewsfeedItem {



//    "author": {
//        "username": "01685026771",
//                "full_name": "Osman Goni Nahid",
//                "bio": "",
//                "image": "https://static.productionready.io/images/smiley-cyrus.jpg",
//                "following": false
//    },
//            "body": "lorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsum",
//            "created_at": "2019-09-12T14:22:48.313392+00:00",
//            "description": "lorem ipsumlorem ipsumlorem ipsumlorem ipsum",
//            "favorited": false,
//            "favorites_count": 0,
//            "comments_count": 1,
//            "slug": "lorem-ipsum-1-g31jdx",
//            "tagList": [],
//            "title": "lorem ipsum 1",
//            "updated_at": "2019-09-12T14:22:48.313438+00:00"

    private String authorUsername, authoeBio, authorImage, authorBio, body, createdAt, description, slug, title, updatedAt, tags;
    private boolean authorFollowing, favorited;
    private int favoriteCount, commentsCount;


    public NewsfeedItem(){

    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthoeBio() {
        return authoeBio;
    }

    public void setAuthoeBio(String authoeBio) {
        this.authoeBio = authoeBio;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isAuthorFollowing() {
        return authorFollowing;
    }

    public void setAuthorFollowing(boolean authorFollowing) {
        this.authorFollowing = authorFollowing;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
