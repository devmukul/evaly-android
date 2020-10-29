package bd.com.evaly.evalyshop.recommender.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "recommender_table", primaryKeys = {"slug", "type"})
public class RsEntity {

    @NonNull
    @ColumnInfo(name = "slug")
    private String slug;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "last_opened")
    private long lastOpened;

    @ColumnInfo(name = "open_count")
    private int openCount;

    @ColumnInfo(name = "time_spent")
    private long timeSpent;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    public RsEntity(@NonNull String type, @NonNull String slug, String name, String imageUrl, long lastOpened, int openCount, long timeSpent) {
        this.slug = slug;
        this.name = name;
        this.imageUrl = imageUrl;
        this.lastOpened = lastOpened;
        this.openCount = openCount;
        this.timeSpent = timeSpent;
        this.type = type;
    }

    @NonNull
    public String getSlug() {
        return slug;
    }

    public void setSlug(@NonNull String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(long lastOpened) {
        this.lastOpened = lastOpened;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RsEntity{" +
                "slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", lastOpened=" + lastOpened +
                ", openCount=" + openCount +
                ", timeSpent=" + timeSpent +
                ", type='" + type + '\'' +
                '}';
    }
}
