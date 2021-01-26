package bd.com.evaly.evalyshop.models.issueNew.list;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Shop implements Serializable {

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("updated_at")
    private Object updatedAt;

    @SerializedName("name")
    private String name;

    @SerializedName("updated_by")
    private Object updatedBy;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private int id;

    @SerializedName("created_by")
    private CreatedBy createdBy;

    @SerializedName("slug")
    private String slug;

    @SerializedName("kam_user")
    private Object kamUser;

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Object getKamUser() {
        return kamUser;
    }

    public void setKamUser(Object kamUser) {
        this.kamUser = kamUser;
    }

    @Override
    public String toString() {
        return
                "Shop{" +
                        "is_active = '" + isActive + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",name = '" + name + '\'' +
                        ",updated_by = '" + updatedBy + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",id = '" + id + '\'' +
                        ",created_by = '" + createdBy + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",kam_user = '" + kamUser + '\'' +
                        "}";
    }
}