package com.pluginpolicyengine.config.spring.web;

import java.lang.annotation.*;

/**
 * HTTP 요청 진입점에서 기능 공개 여부를 판단할 때 사용하는 어노테이션입니다.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface FeatureGate {
	/**
	 * 평가할 기능 플래그 키입니다.
	 *
	 * @return 기능 플래그 키
	 */
	String value();
}
