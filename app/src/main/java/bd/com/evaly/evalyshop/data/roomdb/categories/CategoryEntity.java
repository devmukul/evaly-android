package bd.com.evaly.evalyshop.data.roomdb.categories;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity (tableName = "categories_table")
public class CategoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    private String imageUrl;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "slug")
    @SerializedName("slug")
    private String slug;


    @ColumnInfo(name = "drawable")
    private int drawable = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setSlug(String slug){
        this.slug = slug;
    }

    public String getSlug(){
        return slug;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public String toString(){
        return
                "CategoryItem{" +
                        "image_url = '" + imageUrl + '\'' +
                        ",name = '" + name + '\'' +
                        ",slug = '" + slug + '\'' +
                        "}";
    }

}
