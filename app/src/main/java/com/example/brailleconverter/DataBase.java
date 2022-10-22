package com.example.brailleconverter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "Data_Base_Vietnamese";
    private static int DB_VERSION = 1;

    public DataBase(Context context) {
        super(context, DB_NAME , null , DB_VERSION);
    }
    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    public static SQLiteDatabase initDatabase(Activity activity, String databaseName){
        try {
            String outFileName = activity.getApplicationInfo().dataDir + "/databases/" + databaseName;
            //String outFileName = Environment.getRootDirectory().getAbsolutePath().toString() + "/app/Data_Base_Vietnamese.db";
            File f = new File(outFileName);
            if(!f.exists()) {
                InputStream e = activity.getAssets().open(databaseName);
                File folder = new File(activity.getApplicationInfo().dataDir + "/databases/");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                FileOutputStream myOutput = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];

                int length;
                while ((length = e.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                e.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activity.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE braille(id text primary key, character text not null, " +
                " value text not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
