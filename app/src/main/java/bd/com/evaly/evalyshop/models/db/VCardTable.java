package bd.com.evaly.evalyshop.models.db;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
