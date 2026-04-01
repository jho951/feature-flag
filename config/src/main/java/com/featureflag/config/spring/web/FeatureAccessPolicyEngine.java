package com.pluginpolicyengine.config.spring.web;

import com.pluginpolicyengine.core.FlagContext;
import com.pluginpolicyengine.core.FlagDecision;

/**
 * 기능 공개 여부를 판단하는 공통 정책 엔진입니다.
 */
public interface FeatureAccessPolicyEngine {
	/**
	 * 지정한 기능 키의 공개 여부를 평가합니다.
	 *
	 * @param featureKey 기능 플래그 키
	 * @param context 평가 컨텍스트
	 * @return 기능 접근 결정 결과
	 */
	FlagDecision evaluate(String featureKey, FlagContext context);
}
