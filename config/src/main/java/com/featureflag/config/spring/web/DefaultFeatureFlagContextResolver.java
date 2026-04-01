package com.pluginpolicyengine.config.spring.web;

import com.pluginpolicyengine.core.FlagContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 최소한의 공통 규칙만 제공하는 기본 컨텍스트 해석기입니다.
 */
public class DefaultFeatureFlagContextResolver implements FeatureFlagContextResolver {
	@Override
	public FlagContext resolve(HttpServletRequest request) {
		FlagContext.Builder builder = FlagContext.builder();

		String userId = request.getHeader("X-User-Id");
		if (userId != null && !userId.isBlank()) {
			builder.userId(userId);
		}

		String region = request.getHeader("X-Region");
		if (region != null && !region.isBlank()) {
			builder.attr("region", region);
		}

		return builder.build();
	}
}
