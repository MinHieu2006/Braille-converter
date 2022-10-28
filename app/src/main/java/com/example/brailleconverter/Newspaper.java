package com.example.brailleconverter;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Newspaper {
    public void get_data() throws IOException{
        Document document = Jsoup.connect("https://vnexpress.net/bo-truong-noi-vu-kho-tang-luong-co-so-tu-1-1-2023-4528858.html").timeout(6000).get();
        Elements elements = document.select("div#sidebar-1");
        for (Element e: elements.select("fck_detail ")){
            String text = e.select("p.Normal").text();
            Log.d("AAA" , text);
        }
    }
}
