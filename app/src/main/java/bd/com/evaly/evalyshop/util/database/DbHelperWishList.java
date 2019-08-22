package bd.com.evaly.evalyshop.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelperWishList extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="evaly_wishlist.db";
    public static final String TABLE_NAME="wishlist";
    public static final String COL_1="ID";
    public static final String COL_2="PRODUCT_SLUG";
    public static final String COL_3="NAME";
    public static final String COL_4="IMAGE";
    public static final String COL_5="PRICE";
    public static final String COL_6="TIME";

    public DbHelperWishList(Context context){
        super(context,DATABASE_NAME,null,3);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,PRODUCT_SLUG TEXT,NAME TEXT,IMAGE TEXT,PRICE TEXT,TIME LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String slug,String name,String image,int price,long time){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_2,slug);
        values.put(COL_3,name);
        values.put(COL_4,image);
        values.put(COL_5,price+"");
        values.put(COL_6,time);
        long result=db.insert(TABLE_NAME,null,values);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME+" order by time desc",null);
        return res;
    }

    public void deleteData(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        String where="ID=?";
        db.delete(TABLE_NAME, where, new String[]{id}) ;
    }


    public void deleteDataBySlug(String slug){
        SQLiteDatabase db=this.getWritableDatabase();
        String where="PRODUCT_SLUG=?";
        db.delete(TABLE_NAME, where, new String[]{slug}) ;
    }


    public boolean isSlugExist(String slug) {
        SQLiteDatabase sqldb = this.getWritableDatabase();

        try {
            String Query = "Select * from " + TABLE_NAME + " where " + COL_2 + " = '" + slug + "'";
            Cursor cursor = sqldb.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        } catch (Exception e){

            return false;

        }
    }

    public int size(){

        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return (int) count;

    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}
