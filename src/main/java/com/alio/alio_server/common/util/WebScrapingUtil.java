package com.alio.alio_server.common.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WebScrapingUtil {

    private static final int TIMEOUT = 10000;

    public String extractTextFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            doc.select("script, style, nav, footer, header, aside").remove();

            String text = doc.body().text();

            log.info("Extracted {} characters from URL: {}", text.length(), url);

            return text;
        } catch (IOException e) {
            log.error("Failed to scrape URL: {}", url, e);
            throw new RuntimeException("Failed to scrape URL: " + url, e);
        }
    }

    public List<NewsArticle> crawlRecruitmentNews() {
        List<NewsArticle> articles = new ArrayList<>();

        articles.addAll(crawlNaverNews("해외취업"));
        articles.addAll(crawlNaverNews("채용사기"));
        articles.addAll(crawlNaverNews("구인사기"));

        return articles;
    }

    private List<NewsArticle> crawlNaverNews(String keyword) {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            String searchUrl = "https://search.naver.com/search.naver?where=news&query=" +
                              java.net.URLEncoder.encode(keyword, "UTF-8") +
                              "&sort=1&start=1";

            Document doc = Jsoup.connect(searchUrl)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            Elements newsElements = doc.select("div.news_area");

            int count = 0;
            for (Element newsElement : newsElements) {
                if (count >= 10) break;

                try {
                    Element titleElement = newsElement.selectFirst("a.news_tit");
                    if (titleElement != null) {
                        String title = titleElement.text();
                        String url = titleElement.attr("href");

                        Element descElement = newsElement.selectFirst("div.news_dsc");
                        String description = descElement != null ? descElement.text() : "";

                        Element infoElement = newsElement.selectFirst("div.info_group");
                        String source = "";
                        if (infoElement != null) {
                            Element sourceElement = infoElement.selectFirst("a.info.press");
                            source = sourceElement != null ? sourceElement.text() : "";
                        }

                        articles.add(new NewsArticle(title, url, description, source, keyword));
                        count++;
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse news element", e);
                }
            }

            log.info("Crawled {} articles for keyword: {}", articles.size(), keyword);

        } catch (Exception e) {
            log.error("Failed to crawl Naver news for keyword: {}", keyword, e);
        }

        return articles;
    }

    public static class NewsArticle {
        public String title;
        public String url;
        public String description;
        public String source;
        public String keyword;

        public NewsArticle(String title, String url, String description, String source, String keyword) {
            this.title = title;
            this.url = url;
            this.description = description;
            this.source = source;
            this.keyword = keyword;
        }
    }
}
