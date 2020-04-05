package bd.com.evaly.evalyshop.data.roomdb.userInfo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info_table")
public class UserInfoEntity {

    @PrimaryKey
    @ColumnInfo
    @NonNull
    private String token;

    @ColumnInfo
    private String refreshToken;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
