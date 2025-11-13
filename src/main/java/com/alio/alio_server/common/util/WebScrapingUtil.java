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
        java.util.Set<String> seenUrls = new java.util.HashSet<>();  // URL 중복 체크

        try {
            String searchUrl = "https://m.search.naver.com/search.naver?where=m_news&query=" +
                              java.net.URLEncoder.encode(keyword, "UTF-8") +
                              "&sort=1";

            log.info("검색 URL: {}", searchUrl);

            Document doc = Jsoup.connect(searchUrl)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9")
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .get();

            Elements newsElements = doc.select("ul.list_news li.bx");
            log.info("ul.list_news li.bx 선택자로 {}개 발견", newsElements.size());

            if (newsElements.isEmpty()) {
                newsElements = doc.select("ul.news_list > li");
                log.info("ul.news_list > li 선택자로 {}개 발견", newsElements.size());
            }

            if (newsElements.isEmpty()) {
                newsElements = doc.select("div.news_wrap");
                log.info("div.news_wrap 선택자로 {}개 발견", newsElements.size());
            }

            if (newsElements.isEmpty()) {
                newsElements = doc.select("div[class*='news']");
                log.info("div[class*='news'] 선택자로 {}개 발견", newsElements.size());
            }

            if (newsElements.isEmpty()) {
                log.warn("뉴스 요소를 찾을 수 없습니다. HTML 구조 확인 필요.");
                log.warn("Body의 첫 2000자: {}",
                    doc.body().html().substring(0, Math.min(2000, doc.body().html().length())));
            }

            int count = 0;
            for (Element newsElement : newsElements) {
                if (count >= 10) break;

                try {
                    if (count == 0) {
                        log.info("첫 번째 뉴스 요소 HTML 구조:\n{}",
                            newsElement.html().substring(0, Math.min(2000, newsElement.html().length())));
                    }

                    Elements allLinks = newsElement.select("a[href]");
                    log.debug("발견된 링크 수: {}", allLinks.size());

                    Element titleElement = newsElement.selectFirst("a.news_tit");
                    if (titleElement == null) {
                        titleElement = newsElement.selectFirst("a.news_link");
                    }
                    if (titleElement == null) {
                        titleElement = newsElement.selectFirst("a[href*='news.naver.com']");
                    }
                    if (titleElement == null) {
                        titleElement = newsElement.selectFirst("a[href*='n.news.naver.com']");
                    }
                    if (titleElement == null) {
                        titleElement = newsElement.selectFirst("div.news_tit a");
                    }
                    if (titleElement == null) {
                        titleElement = newsElement.selectFirst("a.tit");
                    }
                    if (titleElement == null && !allLinks.isEmpty()) {
                        for (Element link : allLinks) {
                            String href = link.attr("href");
                            String text = link.text();
                            if ((href.contains("news.naver.com") || href.contains("n.news.naver.com"))
                                && !text.isEmpty() && text.length() > 10) {
                                titleElement = link;
                                break;
                            }
                        }
                    }

                    if (titleElement != null) {
                        String title = titleElement.text();
                        String url = titleElement.attr("abs:href");  // 절대 URL로 변환

                        log.debug("제목 발견: {}, URL: {}", title, url);

                        Element descElement = newsElement.selectFirst("div.news_dsc");
                        if (descElement == null) {
                            descElement = newsElement.selectFirst("div.dsc_wrap");
                        }
                        if (descElement == null) {
                            descElement = newsElement.selectFirst("div.api_subject_bsc");
                        }
                        if (descElement == null) {
                            descElement = newsElement.selectFirst("p.dsc");
                        }
                        if (descElement == null) {
                            descElement = newsElement.selectFirst("div[class*='dsc']");
                        }
                        String description = descElement != null ? descElement.text() : title;

                        String source = "";
                        Element sourceElement = newsElement.selectFirst("a.info.press");
                        if (sourceElement == null) {
                            sourceElement = newsElement.selectFirst("span.press");
                        }
                        if (sourceElement == null) {
                            sourceElement = newsElement.selectFirst("div.info_group a");
                        }
                        if (sourceElement == null) {
                            sourceElement = newsElement.selectFirst("span.info");
                        }
                        if (sourceElement == null) {
                            sourceElement = newsElement.selectFirst("a[class*='press']");
                        }
                        source = sourceElement != null ? sourceElement.text() : "출처 미상";

                        if (!url.isEmpty() && !title.isEmpty()) {
                            if (seenUrls.contains(url)) {
                                log.debug("중복 URL 스킵: {}", url);
                                continue;
                            }

                            seenUrls.add(url);
                            articles.add(new NewsArticle(title, url, description, source, keyword));
                            count++;
                            log.info("뉴스 추가 성공: {} - {}", title, url);
                        } else {
                            log.warn("제목 또는 URL이 비어있음. title={}, url={}", title, url);
                        }
                    } else {
                        log.warn("제목 요소를 찾을 수 없음. 사용 가능한 링크: {}",
                            allLinks.stream().map(l -> l.attr("class")).toList());
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse news element: {}", e.getMessage(), e);
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
