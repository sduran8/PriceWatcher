package edu.utep.cs.cs4330.pricewatcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemDatabase extends SQLiteOpenHelper {

//--ATTRIBUTES--------------------------------------------------------------------------------------

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "todoDB";
    private static final String TODO_TABLE = "items";

    private static final String KEY_ID = "_id";
    private static final String KEY_ITEM = "item";
    private static final String KEY_CURR_PRICE = "curr_price";
    private static final String KEY_INIT_PRICE = "init_price";
    private static final String KEY_PRICE_CHANGE = "price_change";
    private static final String KEY_URL = "url";

//--CONSTRUCTOR-------------------------------------------------------------------------------------

    public ItemDatabase(Context context){
        super (context, DB_NAME, null, DB_VERSION);
    }

//--ONCREATE----------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE " + TODO_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ITEM + " TEXT, "
                + KEY_CURR_PRICE + " DOUBLE,"
                + KEY_INIT_PRICE + " DOUBLE,"
                + KEY_PRICE_CHANGE + " INTEGER,"
                + KEY_URL + " TEXT" + ")";
        db.execSQL(table);
    }

//--METHODS-----------------------------------------------------------------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(database);
    }

    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM, item.getItem());
        values.put(KEY_CURR_PRICE, item.getCurrentPrice());
        values.put(KEY_INIT_PRICE, item.getInitialPrice());
        values.put(KEY_PRICE_CHANGE, item.getPriceChange());
        values.put(KEY_URL, item.getUrl());
        long id = db.insert(TODO_TABLE, null, values);
        item.setId((int) id);
        db.close();
    }

    public void update(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM, item.getItem());
        values.put(KEY_CURR_PRICE, item.getCurrentPrice());
        values.put(KEY_INIT_PRICE, item.getInitialPrice());
        values.put(KEY_PRICE_CHANGE, item.getPriceChange());
        values.put(KEY_URL, item.getUrl());
        db.update(TODO_TABLE, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TODO_TABLE, null, new String[]{});
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TODO_TABLE, KEY_ID + " = ?", new String[] { Integer.toString(id) } );
        db.close();
    }

    public List<Item> allItems() {
        List<Item> todoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TODO_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double curr_price = cursor.getInt(2);
                double init_price = cursor.getInt(3);
                int percentChange = cursor.getInt(4);
                String url = cursor.getString(5);
                Item task = new Item(id, name, curr_price, init_price, percentChange, url);
                todoList.add(task);
            } while (cursor.moveToNext());
        }
        return todoList;
    }
}
