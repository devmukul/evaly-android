package bd.com.evaly.evalyshop.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.Logger;

public class DbHelperCart extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="evaly_cart_3.db";
    public static final String TABLE_NAME="cart";
    public static final String COL_1="ID";
    public static final String COL_2="PRODUCT_SLUG";
    public static final String COL_3="NAME";
    public static final String COL_4="IMAGE";
    public static final String COL_5="PRICE";
    public static final String COL_6="TIME";
    public static final String COL_7="FROM_SHOP_DATA";
    public static final String COL_8="QUANTITY";
    public static final String COL_9="SHOP_SLUG";


    public DbHelperCart(Context context){
        super(context,DATABASE_NAME,null,5);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,PRODUCT_SLUG TEXT, NAME TEXT, IMAGE TEXT, PRICE INT, TIME LONG, FROM_SHOP_DATA STRING, QUANTITY INT, SHOP_SLUG STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String slug,String name,String image,int price,long time,String shopData, int quantity, String shop_slug){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_2,slug);
        values.put(COL_3,name);
        values.put(COL_4,image);
        values.put(COL_5,price+"");
        values.put(COL_6,time);
        values.put(COL_7,shopData);
        values.put(COL_8,quantity);
        values.put(COL_9,shop_slug);

        long result=db.insert(TABLE_NAME,null,values);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME+" ORDER BY SHOP_SLUG, time desc",null);

        Logger.d(res.toString());

        return res;
    }


    public void updateQuantity(String id, int qn){

            SQLiteDatabase db=this.getWritableDatabase();
            String strSQL = "UPDATE "+TABLE_NAME+" SET QUANTITY = "+qn+" WHERE ID = '"+id+"'";
            db.execSQL(strSQL);
            db.close();

    }

    public int size(){

        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return (int) count;

    }

    public void deleteData(String id){

        SQLiteDatabase db=this.getWritableDatabase();
        String where="ID=?";
        db.delete(TABLE_NAME, where, new String[]{id});

    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}
