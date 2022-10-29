package com.example.brailleconverter;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Newspaper extends AsyncTask<Void, Void, String>{
    @Override
    protected String doInBackground(Void... params) {
        String url = "https://vnexpress.net/";

        Document document;
        try {
            document = Jsoup.connect(url).get();
            Elements paragraphs = document.select("p[class='description']");

            Element firstParagraph = paragraphs.first();
            Element lastParagraph = paragraphs.last();
            Element p;
            int i=1;
            p=firstParagraph;
            Log.i("TEXT" , "*  " +p.text());
            while (p!=lastParagraph){
                p=paragraphs.get(i);
                Log.i("TEXT" , "*  " + p.select("a").attr("href"));
                i++;
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  "";
    }


    @Override
    protected void onPostExecute(String result) {
        //if you had a ui element, you could display the title
        Log.i("text " ,result);
    }
}
