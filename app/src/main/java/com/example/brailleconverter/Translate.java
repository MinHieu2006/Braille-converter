package com.example.brailleconverter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Translate {
    public String translate(Activity context , String txt){
        String result = "";
        SQLiteDatabase database ;
        database = DataBase.initDatabase(context , "Data_Base_Vietnamese.db");
        for(char i : txt.toCharArray()){
            Cursor cursor = database.rawQuery("SELECT value  FROM braille WHERE character = "  + "\"" + i + "\"" + ";" , null);
            cursor.moveToFirst();
            result = result + cursor.getString(0) + " ";
        }
        return result;
    }
}
