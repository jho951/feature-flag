package com.pluginpolicyengine.config.spring.web;

import java.io.IOException;

import com.pluginpolicyengine.core.FlagDecision;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 외부 공개 기능이 닫혀 있을 때 기본적으로 404를 응답합니다.
 */
public class NotFoundFeatureDeniedHandler implements FeatureDeniedHandler {
	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		String featureKey,
		FlagDecision decision
	) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feature disabled: " + featureKey);
	}
}
