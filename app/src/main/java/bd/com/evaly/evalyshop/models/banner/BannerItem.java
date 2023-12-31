package bd.com.evaly.evalyshop.models.banner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "banner_table")
public class BannerItem implements Serializable {

    @PrimaryKey
    @NonNull
    public String slug;

    @ColumnInfo
    public String image, name, status, type, url;

    public BannerItem(){

    }

    public BannerItem(String image, String name, String slug, String status, String type, String url) {
        this.image = image;
        this.name = name;
        this.slug = slug;
        this.status = status;
        this.type = type;
        this.url = url;
    }


//    @Override
//    public boolean equals(Object other) {
//        if (other instanceof BannerItem) {
//            BannerItem model = (BannerItem) other;
//            return slug.equals(model.getSlug());
//        } else {
//            return false;
//        }
//    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
