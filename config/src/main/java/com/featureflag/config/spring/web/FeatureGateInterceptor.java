package com.pluginpolicyengine.config.spring.web;

import java.io.IOException;
import java.util.Objects;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.pluginpolicyengine.core.FlagContext;
import com.pluginpolicyengine.core.FlagDecision;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * {@link FeatureGate} 선언을 해석해 요청 진입점에서 기능 공개 여부를 차단합니다.
 */
public class FeatureGateInterceptor implements HandlerInterceptor {
	private final FeatureFlagContextResolver contextResolver;
	private final FeatureAccessPolicyEngine policyEngine;
	private final FeatureDeniedHandler deniedHandler;

	public FeatureGateInterceptor(
		FeatureFlagContextResolver contextResolver,
		FeatureAccessPolicyEngine policyEngine,
		FeatureDeniedHandler deniedHandler
	) {
		this.contextResolver = Objects.requireNonNull(contextResolver, "contextResolver");
		this.policyEngine = Objects.requireNonNull(policyEngine, "policyEngine");
		this.deniedHandler = Objects.requireNonNull(deniedHandler, "deniedHandler");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		FeatureGate gate = findGate(handlerMethod);
		if (gate == null) {
			return true;
		}

		FlagContext context = contextResolver.resolve(request);
		FlagDecision decision = policyEngine.evaluate(gate.value(), context);
		if (decision.enabled()) {
			return true;
		}

		deniedHandler.handle(request, response, gate.value(), decision);
		return false;
	}

	private FeatureGate findGate(HandlerMethod handlerMethod) {
		FeatureGate methodGate = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), FeatureGate.class);
		if (methodGate != null) {
			return methodGate;
		}
		return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), FeatureGate.class);
	}
}
