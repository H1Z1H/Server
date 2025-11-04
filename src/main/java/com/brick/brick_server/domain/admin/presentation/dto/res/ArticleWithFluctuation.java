package com.brick.brick_server.domain.admin.presentation.dto.res;

import java.util.List;
import java.util.Map;

public record ArticleWithFluctuation(
        String title,
        String content,
        List<Map<String, Integer>> fluctuation
) {}
