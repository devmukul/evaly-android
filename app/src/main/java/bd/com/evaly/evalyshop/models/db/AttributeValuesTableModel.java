package bd.com.evaly.evalyshop.models.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "attribute_values")
public class AttributeValuesTableModel {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "attribute_id")
    public String attribute_id;
}
