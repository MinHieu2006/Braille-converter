package com.example.brailleconverter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Translate {
    Boolean isNum = false;
    public String translate(Activity context , String txt){
        String result = "";
        SQLiteDatabase database ;
        database = DataBase.initDatabase(context , "Data_Base_Vietnamese.db");
        for(char i : txt.toCharArray()){
            Cursor cursor = database.rawQuery("SELECT value  FROM braille WHERE character = "  + "\"" + i + "\"" + ";" , null);
            cursor.moveToFirst();
            String t = cursor.getString(0);
            result = result + Process(cursor.getString(0)) + " ";
        }
        return result;
    }
    private String Process(String t){
        if(t.charAt(0) >= '0' && t.charAt(0) <= '9'){
            if(isNum){
                return t;
            } else{
                isNum = true;
                return "3456 " + t;
            }
        } else{
            isNum = false;
        }
        return "";
    }
}
