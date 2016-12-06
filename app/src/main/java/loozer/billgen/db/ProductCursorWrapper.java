package loozer.billgen.db;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import loozer.billgen.db.DbSchema.ProductTable;
import loozer.billgen.Product;

/**
 * Created by Loozer on 5/1/2016.
 */
public class ProductCursorWrapper extends CursorWrapper {
    public ProductCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Product getProduct() {
        String type = getString(getColumnIndex(ProductTable.Cols.TYPE));
        double qty = getDouble(getColumnIndex(ProductTable.Cols.QTY));
        double rate = getDouble(getColumnIndex(ProductTable.Cols.RATE));
        double price = getDouble(getColumnIndex(ProductTable.Cols.PRICE));

        Product p = new Product();
        p.setType((type.equals("Ghee")) ? Product.ProductName.Ghee : Product.ProductName.Butter);
        p.setQty(qty);
        p.setRate(rate);
        p.setPrice(price);
        return p;
    }
}