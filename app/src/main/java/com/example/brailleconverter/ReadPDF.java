package com.example.brailleconverter;

import android.Manifest;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.time.LocalDate;


public class ReadPDF extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;
    public void Read_From_Storage(String url ){
        try {
            int cnt = url.lastIndexOf("primary:") + 8;
            url = url.substring(cnt);
            url = "/" + url;
            String stringFilePath = Environment.getExternalStorageDirectory().getPath() + url;
            File file = new File(stringFilePath);
            //url =  "/Download/22000-tu-vung-IELTS.pdf";
            String extractedText = "";
            PdfReader reader = new PdfReader(file.getPath());
            int n = reader.getNumberOfPages();
            for (int i = 0; i < 1; i++) {
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
            }
            Log.d("EEE" , extractedText);
            reader.close();
        } catch (Exception e) {
            //extractedTV.setText("Error found is : \n" + e);
            Log.e("Error" , e.toString() );
        }
    }

    public void Read_From_Assert(String url){
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_background);
    }


}
