package com.pluginpolicyengine.config.spring.web;

import java.util.Objects;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 기능 게이트 인터셉터를 MVC 체인에 등록합니다.
 */
public class FeatureGateWebMvcConfigurer implements WebMvcConfigurer {
	private final FeatureGateInterceptor featureGateInterceptor;

	public FeatureGateWebMvcConfigurer(FeatureGateInterceptor featureGateInterceptor) {
		this.featureGateInterceptor = Objects.requireNonNull(featureGateInterceptor, "featureGateInterceptor");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(featureGateInterceptor);
	}
}
