package com.example.brailleconverter;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadPostFromNewsPaper extends AsyncTask<Void, Void, List<String>>{
    public List<String> list = new ArrayList<>();
    public String link;
    public ReadPostFromNewsPaper(String link){
        this.link = link;
    }
    @Override
    protected List<String> doInBackground(Void... params) {
        Document document;
        try {
            document = Jsoup.connect(link).get();
            Elements paragraphs = document.select("p[class='Normal']");

            Element firstParagraph = paragraphs.first();
            Element lastParagraph = paragraphs.last();
            Element p;
            int i=1;
            p=firstParagraph;
            Log.i("TEXT" , "*  " +p.text());
            while (p!=lastParagraph){
                p=paragraphs.get(i);
                i++;
                Log.i("text " , p.select("p").text());
                list.add(p.select("p").text());
            }
            int n = list.size();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  list;
    }


    @Override
    protected void onPostExecute(List<String> result) {
        //if you had a ui element, you could display the title
        data();
    }
    public List<String> data(){
        return list;
    }
}
