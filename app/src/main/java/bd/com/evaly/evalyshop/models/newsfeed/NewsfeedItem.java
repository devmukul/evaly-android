package bd.com.evaly.evalyshop.models.newsfeed;

public class NewsfeedItem {

    private String authorUsername, authorFullName, authoeBio, authorImage, authorBio, body, createdAt, description, slug, title, updatedAt, tags, attachment, attachmentCompressed, type, authorImageCompressed;
    private boolean authorFollowing, favorited;
    private int favoriteCount, commentsCount, userID;
    private boolean isAdmin;

    public NewsfeedItem(){

    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    public String getAttachment() {
        return attachment.replace("\n", "");
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentCompressed() {
        return attachmentCompressed.replace("\n", "");
    }

    public void setAttachmentCompressed(String attachmentCompressed) { this.attachmentCompressed = attachmentCompressed; }


    public String getAuthorImageCompressed() {
        return authorImageCompressed.replace("\n", "");
    }

    public void setAuthorImageCompressed(String authorImageCompressed) { this.authorImageCompressed = authorImageCompressed; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
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
        return authorImage.replace("\n", "");
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
