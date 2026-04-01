package com.pluginpolicyengine.config.spring.web;

import java.io.IOException;

import com.pluginpolicyengine.core.FlagDecision;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 기능 접근 거부 시 HTTP 응답 정책을 적용하는 확장 포인트입니다.
 */
public interface FeatureDeniedHandler {
	/**
	 * 접근 거부 응답을 작성합니다.
	 *
	 * @param request 현재 HTTP 요청
	 * @param response 현재 HTTP 응답
	 * @param featureKey 평가한 기능 플래그 키
	 * @param decision 기능 접근 결정 결과
	 * @throws IOException 응답 작성 실패 시
	 */
	void handle(HttpServletRequest request, HttpServletResponse response, String featureKey, FlagDecision decision)
		throws IOException;
}
