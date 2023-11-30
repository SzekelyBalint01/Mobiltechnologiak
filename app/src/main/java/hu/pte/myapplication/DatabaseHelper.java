package hu.pte.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "notes";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Jegyzet hozzáadása az adatbázishoz
    public long insertNote(String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, content);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Jegyzet lekérése az azonosító alapján
    @SuppressLint("Range")
    public String getNoteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CONTENT};
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        String content = null;
        if (cursor != null && cursor.moveToFirst()) {
            content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            cursor.close();
        }

        db.close();

        // Ellenőrzés a log-okba
        Log.d("DatabaseHelper", "Loaded note content for id " + id + ": " + content);

        return content;
    }

    // Összes jegyzet lekérése
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_CONTENT};
        return db.query(TABLE_NAME, columns, null, null, null, null, null);
    }

    // Jegyzet frissítése
    public int updateNote(int id, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, content);
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        int count = db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows > 0; // Return true if at least one row was deleted
    }
    public String getNoteIdColumnName() {
        return COLUMN_ID;
    }
}
