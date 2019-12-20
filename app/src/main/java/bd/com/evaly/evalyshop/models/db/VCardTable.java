package bd.com.evaly.evalyshop.models.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "vcard")
public class VCardTable implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "nick_name")
    public String nickName;
    @ColumnInfo(name = "image_url")
    String imageUrl;

}
