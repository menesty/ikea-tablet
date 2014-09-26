package org.menesty.ikea.tablet.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import org.menesty.ikea.tablet.domain.History;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.math.BigDecimal;
import java.util.*;


/**
 * Created by Menesty on
 * 9/1/14.
 * 17:41.
 */
public class HistoryReaderContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_HISTORY_ENTRIES =
            "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                    HistoryEntry._ID + " INTEGER PRIMARY KEY" + NOT_NULL + COMMA_SEP +
                    HistoryEntry.COLUMN_ACTION_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    HistoryEntry.COLUMN_PRICE + DOUBLE_TYPE + NOT_NULL + COMMA_SEP +
                    HistoryEntry.COLUMN_PRICE + DOUBLE_TYPE + NOT_NULL + COMMA_SEP +
                    HistoryEntry.COLUMN_CREATE_DATE + INTEGER_TYPE + NOT_NULL +
                    " );";

    public static final String SQL_CREATE_PRODUCT_ITEM_ENTRIES =
            "CREATE TABLE " + ProductItemEntry.TABLE_NAME + " (" +
                    ProductItemEntry._ID + " INTEGER PRIMARY KEY" + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.ART_NUMBER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.PRODUCT_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.SHORT_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.COUNT + DOUBLE_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.PRICE + DOUBLE_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.WEIGHT + DOUBLE_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.ORDER_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.HISTORY_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.PARAGON_INDEX + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    ProductItemEntry.CHECKED + BOOLEAN_TYPE + ");";

    public static final String SQL_DELETE_HISTORY_ENTRIES = "DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME;
    public static final String SQL_DELETE_PRODUCT_ITEM_ENTRIES = "DROP TABLE IF EXISTS " + ProductItemEntry.TABLE_NAME;


    public static abstract class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_ACTION_ID = "actionId";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_CREATE_DATE = "createDate";
        public static final String SEND = "send";
    }

    public static class ProductItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "product_item";
        public static final String ART_NUMBER = "artNumber";
        public static final String PRODUCT_NAME = "productName";
        public static final String SHORT_NAME = "shortName";
        public static final String COUNT = "count";
        public static final String PRICE = "price";
        public static final String WEIGHT = "weight";
        public static final String ORDER_ID = "orderId";
        public static final String CHECKED = "checked";
        public static final String HISTORY_ID = "historyId";
        public static final String PARAGON_INDEX = "paragonIndex";
    }

    public static History[] loadItems(SQLiteDatabase db) {
        String[] projection = {
                HistoryEntry._ID,
                HistoryEntry.COLUMN_ACTION_ID,
                HistoryEntry.COLUMN_PRICE,
                HistoryEntry.COLUMN_CREATE_DATE,
                HistoryEntry.SEND

        };

        Cursor cursor = db.query(HistoryEntry.TABLE_NAME, projection, null, null, null, null, HistoryEntry.COLUMN_CREATE_DATE + " desc");

        List<History> result = new ArrayList<History>();

        if (cursor.moveToFirst())
            do {
                int itemId = cursor.getInt(cursor.getColumnIndex(HistoryEntry._ID));
                String actionId = cursor.getString(cursor.getColumnIndex(HistoryEntry.COLUMN_ACTION_ID));
                double price = cursor.getDouble(cursor.getColumnIndex(HistoryEntry.COLUMN_PRICE));
                long createDate = cursor.getLong(cursor.getColumnIndex(HistoryEntry.COLUMN_CREATE_DATE));
                result.add(new History(itemId, actionId, price, new Date(createDate)));
            } while (cursor.moveToNext());


        return result.toArray(new History[result.size()]);
    }

    public static List<ProductItem[]> loadItems(History history, SQLiteDatabase db) {
        String[] projection = {
                ProductItemEntry.ART_NUMBER,
                ProductItemEntry.PRODUCT_NAME,
                ProductItemEntry.CHECKED,
                ProductItemEntry.WEIGHT,
                ProductItemEntry.COUNT,
                ProductItemEntry.PRICE,
                ProductItemEntry.SHORT_NAME,
                ProductItemEntry.ORDER_ID,
                ProductItemEntry.HISTORY_ID,
                ProductItemEntry.PARAGON_INDEX
        };

        Cursor cursor = db.query(ProductItemEntry.TABLE_NAME, projection, ProductItemEntry.HISTORY_ID + "=?",
                new String[]{history.getId() + ""}, null, null, null);


        Map<Integer, List<ProductItem>> map = new HashMap<Integer, List<ProductItem>>();

        if (cursor.moveToFirst()) {
            do {
                String artNumber = cursor.getString(cursor.getColumnIndex(ProductItemEntry.ART_NUMBER));
                String productName = cursor.getString(cursor.getColumnIndex(ProductItemEntry.PRODUCT_NAME));
                int checked = cursor.getInt(cursor.getColumnIndex(ProductItemEntry.CHECKED));
                double weight = cursor.getDouble(cursor.getColumnIndex(ProductItemEntry.WEIGHT));
                double count = cursor.getDouble(cursor.getColumnIndex(ProductItemEntry.COUNT));
                double price = cursor.getDouble(cursor.getColumnIndex(ProductItemEntry.PRICE));
                String shortName = cursor.getString(cursor.getColumnIndex(ProductItemEntry.SHORT_NAME));
                int orderId = cursor.getInt(cursor.getColumnIndex(ProductItemEntry.ORDER_ID));

                int index = cursor.getInt(cursor.getColumnIndex(ProductItemEntry.PARAGON_INDEX));

                List<ProductItem> data = map.get(index);

                if (data == null)
                    map.put(index, data = new ArrayList<ProductItem>());

                data.add(new ProductItem(artNumber, productName, shortName, count, price, weight, orderId, checked == 1));

            } while (cursor.moveToNext());
        }

        Collection<List<ProductItem>> data = map.values();
        List<ProductItem[]> result = new ArrayList<ProductItem[]>();

        for (List<ProductItem> item : data)
            result.add(item.toArray(new ProductItem[item.size()]));

        return result;
    }

    public static void save(SQLiteDatabase db, String actionId, List<ProductItem[]> items) {
        //if already exist just skip
        Cursor cursor = db.query(HistoryEntry.TABLE_NAME, new String[]{HistoryEntry.COLUMN_ACTION_ID},
                HistoryEntry.COLUMN_ACTION_ID + "=?", new String[]{actionId}, null, null, null);

        if (cursor.moveToFirst())
            return;

        BigDecimal price = BigDecimal.ZERO;

        for (ProductItem[] paragons : items)
            for (ProductItem item : paragons)
                price = price.add(BigDecimal.valueOf(item.price * item.count));

        ContentValues values = new ContentValues();
        values.put(HistoryEntry.COLUMN_ACTION_ID, actionId);
        values.put(HistoryEntry.COLUMN_PRICE, price.doubleValue());
        values.put(HistoryEntry.COLUMN_CREATE_DATE, new Date().getTime());

        long historyId = db.insert(HistoryEntry.TABLE_NAME, null, values);

        int index = 0;
        for (ProductItem[] paragons : items) {
            index++;
            for (ProductItem item : paragons) {
                values = new ContentValues();

                values.put(ProductItemEntry.ART_NUMBER, item.artNumber);
                values.put(ProductItemEntry.PRODUCT_NAME, item.productName);
                values.put(ProductItemEntry.CHECKED, item.checked ? 1 : 0);
                values.put(ProductItemEntry.WEIGHT, item.weight);
                values.put(ProductItemEntry.COUNT, item.count);
                values.put(ProductItemEntry.PRICE, item.price);
                values.put(ProductItemEntry.SHORT_NAME, item.shortName);
                values.put(ProductItemEntry.ORDER_ID, item.orderId);
                values.put(ProductItemEntry.HISTORY_ID, historyId);
                values.put(ProductItemEntry.PARAGON_INDEX, index);

                db.insert(ProductItemEntry.TABLE_NAME, null, values);
            }
        }
    }
}
