package bd.com.evaly.evalyshop.models.express;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "express_service_list")
public class ExpressServiceModel implements Serializable {

    @SerializedName("image")
    private String image;

    @SerializedName("service_type")
    private String serviceType;

    @SerializedName(value = "name")
    private String name;

    @SerializedName("app_name")
    private String appName;

    @SerializedName("app_logo")
    private String appLogo;

    @SerializedName("app_bg_color")
    private String appBgColor;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("modified_at")
    private String modifiedAt;

    @NonNull
    @PrimaryKey
    @SerializedName("slug")
    private String slug;

    public ExpressServiceModel() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return
                "ExpressServiceModel{" +
                        "image = '" + image + '\'' +
                        ",service_type = '" + serviceType + '\'' +
                        ",name = '" + name + '\'' +
                        ",modified_by = '" + modifiedBy + '\'' +
                        ",modified_at = '" + modifiedAt + '\'' +
                        ",slug = '" + slug + '\'' +
                        "}";
    }

    public String getAppBgColor() {
        return appBgColor;
    }

    public void setAppBgColor(String appBgColor) {
        this.appBgColor = appBgColor;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(String appLogo) {
        this.appLogo = appLogo;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ExpressServiceModel) {
            ExpressServiceModel newModel = (ExpressServiceModel) obj;
            return (slug.equals(newModel.getSlug()) &&
                    name.equals(newModel.getName()) &&
                    (appName == null ? "" : appName).equals(newModel.getAppName()) &&
                    appBgColor.equals(newModel.getAppBgColor()) &&
                    appLogo.equals(newModel.getAppLogo()));

        } else
            return false;
    }
}