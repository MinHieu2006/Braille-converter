package com.example.brailleconverter;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//import com.itextpdf.text.List;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ReadPDF extends AppCompatActivity {
    public List<String> list = new ArrayList<String>();
    private static final int STORAGE_PERMISSION_CODE = 100;
    public List<String> Read_From_Storage(String url ){

        try {
            int cnt = url.lastIndexOf("primary:") + 8;
            url = url.substring(cnt);
            url = "/" + url;
            String stringFilePath = Environment.getExternalStorageDirectory().getPath() + url;
            File file = new File(stringFilePath);
            String extractedText = "";
            PdfReader reader = new PdfReader(file.getPath());
            int n = reader.getNumberOfPages();
            // WARNING
            // Need to stop or wait about 1-2 pages instead of read all text from pdf.
            for (int i = 0; i < 1; i++) {
                //extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                list.add(PdfTextExtractor.getTextFromPage(reader, i + 1).trim());
            }
            Log.d("EEE" , extractedText);
            reader.close();
        } catch (Exception e) {
            //extractedTV.setText("Error found is : \n" + e);
            Log.e("Error" , e.toString() );
        }
        return list;
    }

    public void Read_From_Assert(Context context){
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String name : files){
            Log.i("File" , name);
        }
    }


}
