package loozer.billgen.db;

/**
 * Created by Loozer on 5/1/2016.
 */
public class DbSchema{
    public static final class ProductTable {
        public static final String NAME = "products";
        public static final class Cols {
            public static final String TYPE = "type";
            public static final String DATE = "date";
            public static final String QTY = "quantity";
            public static final String RATE = "rate";
            public static final String PRICE ="price";
        }
    }
}
