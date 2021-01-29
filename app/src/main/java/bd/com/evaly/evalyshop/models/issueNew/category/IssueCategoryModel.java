package bd.com.evaly.evalyshop.models.issueNew.category;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IssueCategoryModel implements Serializable {

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

    @SerializedName("deleted_at")
    private Object deletedAt;

    @SerializedName("created_by")
    private CreatedBy createdBy;

    @SerializedName("slug")
    private String slug;

    public boolean isIsActive(){
        return isActive;
    }

    public Object getUpdatedAt(){
        return updatedAt;
    }

    public String getName(){
        return name;
    }

    public Object getUpdatedBy(){
        return updatedBy;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public int getId(){
        return id;
    }

    public Object getDeletedAt(){
        return deletedAt;
    }

    public CreatedBy getCreatedBy(){
        return createdBy;
    }

    public String getSlug(){
        return slug;
    }
}