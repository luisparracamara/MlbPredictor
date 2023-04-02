package com.example.mlbpredictor.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class BaseballReference {

    public Document getHtml(String url) {
        Document html = null;

        try {
            html = Jsoup.connect(url).get();

        }catch (Exception e) {
            throw new RuntimeException("No se pudo establecer comunicación con la página web");
        }

        return html;
    }

}
