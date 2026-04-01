package com.pluginpolicyengine.config.spring.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import com.pluginpolicyengine.core.FlagContext;
import com.pluginpolicyengine.core.FlagDecision;

class FeatureGateInterceptorTest {

	@Test
	void allowsRequestWhenFeatureIsEnabled() throws Exception {
		FeatureGateInterceptor interceptor = new FeatureGateInterceptor(
			request -> FlagContext.builder().userId("user-1").build(),
			(featureKey, context) -> new FlagDecision(true, "on", "TARGET_ALLOW", java.util.Map.of("key", featureKey)),
			new NotFoundFeatureDeniedHandler()
		);

		boolean allowed = interceptor.preHandle(
			new MockHttpServletRequest(),
			new MockHttpServletResponse(),
			new HandlerMethod(new TestController(), TestController.class.getMethod("gated"))
		);

		assertTrue(allowed);
	}

	@Test
	void deniesRequestWhenFeatureIsDisabled() throws Exception {
		FeatureGateInterceptor interceptor = new FeatureGateInterceptor(
			request -> FlagContext.builder().build(),
			(featureKey, context) -> new FlagDecision(false, "off", "FLAG_DISABLED", java.util.Map.of("key", featureKey)),
			new NotFoundFeatureDeniedHandler()
		);
		MockHttpServletResponse response = new MockHttpServletResponse();

		boolean allowed = interceptor.preHandle(
			new MockHttpServletRequest(),
			response,
			new HandlerMethod(new TestController(), TestController.class.getMethod("gated"))
		);

		assertFalse(allowed);
		assertEquals(404, response.getStatus());
		assertEquals("Feature disabled: features.public-user-api", response.getErrorMessage());
	}

	static class TestController {
		@FeatureGate("features.public-user-api")
		public void gated() {
		}
	}
}
