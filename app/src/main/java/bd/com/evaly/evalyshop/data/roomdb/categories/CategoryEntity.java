package bd.com.evaly.evalyshop.data.roomdb.categories;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "categories_table")
public class CategoryEntity {

    @ColumnInfo(name = "image_url")
    @SerializedName(value = "image_url", alternate = "category_image")
    private String imageUrl;

    @ColumnInfo(name = "name")
    @SerializedName(value = "name", alternate = "category_name")
    private String name;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "slug")
    @SerializedName(value = "slug", alternate = "category_slug")
    private String slug;

    @ColumnInfo(name = "drawable")
    private int drawable = 0;

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CategoryEntity) {
            CategoryEntity model = (CategoryEntity) other;
            return slug.equals(model.getSlug());
        } else {
            return false;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return
                "CategoryItem{" +
                        "image_url = '" + imageUrl + '\'' +
                        ",name = '" + name + '\'' +
                        ",slug = '" + slug + '\'' +
                        "}";
    }

}
