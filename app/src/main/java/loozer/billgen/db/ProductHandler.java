package loozer.billgen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import loozer.billgen.Product;
import loozer.billgen.db.DbSchema.ProductTable;

/**
 * Created by Loozer on 5/1/2016.
 */
public class ProductHandler {
    private static ProductHandler handler= null;
    private List<Product> mProducts;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private ProductHandler(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DbHelper(mContext).getWritableDatabase();
        mProducts = new ArrayList<>();
    }

    private boolean addProduct(Product p){
        ContentValues values = getContentValues(p);
        mDatabase.insert(ProductTable.NAME,null,values);
        return true;
    }

    public static ProductHandler getHandler(Context c){
        if(handler== null){
            handler = new ProductHandler(c);
        }
        return handler;
    }

    public List<Product> getProducts() {
        mProducts = new ArrayList<Product>();
        ProductCursorWrapper cursor = queryProducts(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mProducts.add(cursor.getProduct());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mProducts;
    }

    public List<Product> getmProducts(String fromDate,String toDate){
        mProducts = new ArrayList<Product>();
        ProductCursorWrapper cursor = queryProducts(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mProducts.add(cursor.getProduct());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mProducts;
    }

    public void addToDB(List<Product> productList){
        for(Product p:productList){
            addProduct(p);
        }
    }

    private ContentValues getContentValues(Product p) {
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.TYPE, p.getType().toString());
        values.put(ProductTable.Cols.DATE, getDate());
        values.put(ProductTable.Cols.QTY, p.getQty());
        values.put(ProductTable.Cols.RATE, p.getRate());
        values.put(ProductTable.Cols.PRICE, p.getPrice());
        return values;
    }

    private String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return format.format(date);
    }

    private ProductCursorWrapper queryProducts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ProductTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ProductCursorWrapper(cursor);
    }
}
