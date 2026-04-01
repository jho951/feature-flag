package com.pluginpolicyengine.config.spring.web;

import java.util.Objects;

import com.pluginpolicyengine.api.FeatureFlagClient;
import com.pluginpolicyengine.core.FlagContext;
import com.pluginpolicyengine.core.FlagDecision;

/**
 * {@link FeatureFlagClient}를 사용해 기능 공개 여부를 판단하는 기본 구현입니다.
 */
public class DefaultFeatureAccessPolicyEngine implements FeatureAccessPolicyEngine {
	private final FeatureFlagClient featureFlagClient;

	public DefaultFeatureAccessPolicyEngine(FeatureFlagClient featureFlagClient) {
		this.featureFlagClient = Objects.requireNonNull(featureFlagClient, "featureFlagClient");
	}

	@Override
	public FlagDecision evaluate(String featureKey, FlagContext context) {
		return featureFlagClient.evaluate(featureKey, context);
	}
}
