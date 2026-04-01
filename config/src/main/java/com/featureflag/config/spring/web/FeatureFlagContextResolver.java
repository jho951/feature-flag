package com.pluginpolicyengine.config.spring.web;

import com.pluginpolicyengine.core.FlagContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTP 요청을 {@link FlagContext}로 변환하는 확장 포인트입니다.
 */
@FunctionalInterface
public interface FeatureFlagContextResolver {
	/**
	 * 현재 요청에서 기능 플래그 평가 컨텍스트를 구성합니다.
	 *
	 * @param request 현재 HTTP 요청
	 * @return 평가 컨텍스트
	 */
	FlagContext resolve(HttpServletRequest request);
}
