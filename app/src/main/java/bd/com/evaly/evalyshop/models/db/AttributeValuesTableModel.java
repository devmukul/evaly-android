package bd.com.evaly.evalyshop.models.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
