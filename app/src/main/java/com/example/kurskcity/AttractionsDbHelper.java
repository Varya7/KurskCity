package com.example.kurskcity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AttractionsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BD.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private SQLiteDatabase database;
    private String databasePath;

    public AttractionsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);

        if (!dbFile.exists()) {
            try {
                copyDatabaseFromAssets();
            } catch (IOException e) {
                throw new RuntimeException("Error copying database", e);
            }
        }
    }

    private void copyDatabaseFromAssets() throws IOException {
        InputStream inputStream = context.getAssets().open("databases/" + DATABASE_NAME);
        File dbFile = context.getDatabasePath(DATABASE_NAME);

        // Создаем папки если их нет
        dbFile.getParentFile().mkdirs();

        OutputStream outputStream = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // База уже создана из assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // При необходимости обновления БД
    }

    public void openDatabase() throws SQLException {
        database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public List<Attraction> getAllAttractions() {
        List<Attraction> attractions = new ArrayList<>();

        try {
            openDatabase();

            Cursor cursor = database.query(
                    "attactions",
                    new String[]{"_id", "name", "description", "image", "categories"},
                    null, null, null, null, "name ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
                    String categories = cursor.getString(cursor.getColumnIndexOrThrow("categories"));

                    attractions.add(new Attraction(id, name, description, image, categories));
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }

        return attractions;
    }

    public List<Attraction> getAttractionsForMainPage(int limit) {
        List<Attraction> attractions = new ArrayList<>();

        try {
            openDatabase();

            Cursor cursor = database.query(
                    "attactions",
                    new String[]{"_id", "name", "description", "image", "categories"},
                    null, null, null, null, "name ASC", String.valueOf(limit)
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
                    String categories = cursor.getString(cursor.getColumnIndexOrThrow("categories"));

                    attractions.add(new Attraction(id, name, description, image, categories));
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }

        return attractions;
    }
}