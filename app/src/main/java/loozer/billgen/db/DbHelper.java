package loozer.billgen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import loozer.billgen.db.DbSchema.ProductTable;

/**
 * Created by Loozer on 5/1/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "products.db";
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ProductTable.NAME+ "(" +
                        " _id integer primary key autoincrement, " +
                        ProductTable.Cols.TYPE  + ", " +
                        ProductTable.Cols.QTY   + ", " +
                        ProductTable.Cols.DATE  + ", " +
                        ProductTable.Cols.RATE  + ", " +
                        ProductTable.Cols.PRICE +
                        ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
