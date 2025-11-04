package com.brick.brick_server.domain.admin.service.coin;

import com.brick.brick_server.domain.admin.domain.Article;
import com.brick.brick_server.domain.admin.domain.Coin;
import com.brick.brick_server.domain.admin.domain.repository.ArticleRepository;
import com.brick.brick_server.domain.admin.domain.repository.CoinRepository;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleWithFluctuation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleRegistrar {

    private final ArticleRepository articleRepository;
    private final CoinRepository coinRepository;

    public void registerArticles(List<ArticleWithFluctuation> articles) {
        for (ArticleWithFluctuation a : articles) {
            articleRepository.save(Article.builder()
                    .title(a.title())
                    .content(a.content())
                    .date(LocalDate.now().toString())
                    .time(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .publish(false)
                    .build()
            );

            for (Map<String, Integer> change : a.fluctuation()) {
                for (Map.Entry<String, Integer> entry : change.entrySet()) {
                    String coinName = entry.getKey();
                    int percent = entry.getValue();

                    Optional<Coin> latest = coinRepository.findTopByNameOrderByDateDesc(coinName);
                    String newDate = LocalDate.now().toString();

                    // price는 이전 값을 기반으로 증감 계산
                    latest.ifPresent(prev -> {
                        try {
                            double oldPrice = Double.parseDouble(prev.getPrice());
                            double updatedPrice = oldPrice * (1 + percent / 100.0);
                            String newPrice = String.format("%.2f", updatedPrice);

                            coinRepository.save(Coin.builder()
                                    .name(coinName)
                                    .price(newPrice)
                                    .date(newDate)
                                    .build());

                        } catch (NumberFormatException e) {
                            // 예외 발생 시 로그 또는 무시
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }
}