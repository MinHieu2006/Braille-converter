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

public class Newspaper extends AsyncTask<Void, Void, List<Post>>{
    // Number of post
    public int number = 10;
    public List<Post> list = new ArrayList<>();
    @Override
    protected List<Post> doInBackground(Void... params) {
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
            //Log.i("TEXT" , "*  " +p.text());
            Post tmp = new Post(p.select("a").attr("href") , p.select("a").attr("title"));
            while (p!=lastParagraph){
                if(list.size() >= number) break;
                p=paragraphs.get(i);
                Post post = new Post( p.select("a").attr("href") , p.select("a").attr("title"));
                list.add(post);
                i++;
            }
            int n = list.size();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  list;
    }


    @Override
    protected void onPostExecute(List<Post> result) {
        //if you had a ui element, you could display the title
        data();
    }
    public List<Post> data(){
        return list;
    }
}
