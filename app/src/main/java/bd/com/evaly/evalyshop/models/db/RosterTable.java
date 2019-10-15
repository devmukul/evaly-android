package bd.com.evaly.evalyshop.models.db;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "roster_table")
public class RosterTable implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String id;
    @ColumnInfo(name = "roster_name")
    public String rosterName;
    @ColumnInfo(name = "last_message")
    public String lastMessage;
    @ColumnInfo(name = "status")
    public int status;
    @ColumnInfo(name = "unread_count")
    public int unreadCount;
    @ColumnInfo(name = "time")
    public long time;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "nick_name")
    public String nick_name;
    @ColumnInfo(name = "image_url")
    public String imageUrl;


}
