package com.brick.brick_server.domain.admin.service.implementation;

import com.brick.brick_server.domain.admin.domain.Article;
import com.brick.brick_server.domain.admin.domain.Coin;
import com.brick.brick_server.domain.admin.domain.repository.ArticleRepository;
import com.brick.brick_server.domain.admin.domain.repository.CoinRepository;
import com.brick.brick_server.domain.admin.domain.repository.UserCoinRepository;
import com.brick.brick_server.domain.admin.exception.ArticleNotFoundException;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleContentResponse;
import com.brick.brick_server.domain.admin.presentation.dto.res.CoinFluctuationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReader {
    private final ArticleRepository articleRepository;
    private final CoinRepository coinRepository;
    private final UserCoinRepository userCoinRepository;


    public List<Article> getAllArticles() {
        return articleRepository.findAllByPublishTrue();
    }

    public List<Article> getAllAdminArticles() {
        return articleRepository.findAllByPublishFalse();
    }

    public ArticleContentResponse getArticleDetail(Long articleId) {
        String content = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new)
                .getContent();

        return ArticleContentResponse.from(content);
    }

    public List<CoinFluctuationResponse> getAllCoinFluctuations() {
        List<Coin> coins = coinRepository.findLatestAndPreviousForAllCoins();

        Map<String, List<Coin>> coinGrouped = coins.stream()
                .collect(Collectors.groupingBy(Coin::getName));

        return coinGrouped.entrySet().stream().map(entry -> {
            String coinName = entry.getKey();
            List<Coin> coinHistory = entry.getValue().stream()
                    .sorted(Comparator.comparing(Coin::getDate))
                    .toList();

            // 파싱 후 정수로 변환
            String previousPriceRaw = coinHistory.size() > 1 ? coinHistory.get(0).getPrice() : coinHistory.get(0).getPrice();
            String currentPriceRaw = coinHistory.get(coinHistory.size() - 1).getPrice();

            long previousPriceInt = (long) Double.parseDouble(previousPriceRaw);
            long currentPriceInt = (long) Double.parseDouble(currentPriceRaw);

            double fluctuation = calculateFluctuation(previousPriceRaw, currentPriceRaw);

            List<Long> totalUserHoldings = userCoinRepository.findNowAmountByCoinNameOrderByIdDesc(coinName);
            Long totalUserHolding = totalUserHoldings.isEmpty() ? 0L : totalUserHoldings.get(0);

            return new CoinFluctuationResponse(
                    coinName,
                    fluctuation,
                    String.valueOf(previousPriceInt),
                    String.valueOf(currentPriceInt),
                    totalUserHolding
            );
        }).toList();
    }

    private double calculateFluctuation(String prev, String curr) {
        try {
            double prevPrice = Double.parseDouble(prev);
            double currPrice = Double.parseDouble(curr);
            if (prevPrice == 0) return 0;
            return ((currPrice - prevPrice) / prevPrice) * 100;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
