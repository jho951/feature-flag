package com.pluginpolicyengine.config.spring;

import com.pluginpolicyengine.api.FeatureFlagClient;
import com.pluginpolicyengine.core.FeatureFlagService;
import com.pluginpolicyengine.core.FlagStore;
import com.pluginpolicyengine.core.store.InMemoryFlagStore;
import com.pluginpolicyengine.config.spring.web.DefaultFeatureAccessPolicyEngine;
import com.pluginpolicyengine.config.spring.web.DefaultFeatureFlagContextResolver;
import com.pluginpolicyengine.config.spring.web.FeatureAccessPolicyEngine;
import com.pluginpolicyengine.config.spring.web.FeatureDeniedHandler;
import com.pluginpolicyengine.config.spring.web.FeatureFlagContextResolver;
import com.pluginpolicyengine.config.spring.web.FeatureGateInterceptor;
import com.pluginpolicyengine.config.spring.web.FeatureGateWebMvcConfigurer;
import com.pluginpolicyengine.config.spring.web.NotFoundFeatureDeniedHandler;
import com.pluginpolicyengine.store.file.JsonFileFlagStore;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot에서 기능 플래그 컴포넌트를 자동 구성합니다.
 */
@AutoConfiguration
@EnableConfigurationProperties(FeatureFlagProperties.class)
public class FeatureFlagAutoConfiguration {

	/**
	 * 저장소 설정이 없을 때 메모리 기반 저장소를 등록합니다.
	 *
	 * @return 메모리 기반 플래그 저장소
	 */
	@Bean
	@ConditionalOnProperty(prefix = "pluginpolicyengine", name = "store", havingValue = "MEMORY", matchIfMissing = true)
	public FlagStore inMemoryFlagStore() {
		return new InMemoryFlagStore();
	}

	/**
	 * FILE 백엔드 선택 시 JSON 파일 기반 저장소를 등록합니다.
	 *
	 * @param props 기능 플래그 설정 프로퍼티
	 * @return 파일 기반 플래그 저장소
	 */
	@Bean
	@ConditionalOnProperty(prefix = "pluginpolicyengine", name = "store", havingValue = "FILE")
	public FlagStore fileFlagStore(FeatureFlagProperties props) {
		if (props.getFilePath() == null || props.getFilePath().isBlank()) {
			throw new IllegalStateException("pluginpolicyengine.store=FILE 인데 pluginpolicyengine.file-path가 비어 있습니다.");
		}
		return new JsonFileFlagStore(props.getFilePath(), props.getCacheTtl());
	}

	/**
	 * 핵심 평가 서비스를 생성합니다.
	 *
	 * @param store 선택된 저장소 빈
	 * @return 기능 플래그 서비스
	 */
	@Bean
	public FeatureFlagService featureFlagService(FlagStore store) {
		return new FeatureFlagService(store);
	}

	/**
	 * API에서 사용하는 클라이언트 파사드를 생성합니다.
	 *
	 * @param svc 핵심 기능 플래그 서비스
	 * @return 기능 플래그 클라이언트
	 */
	@Bean
	public FeatureFlagClient featureFlagClient(FeatureFlagService svc) {
		return new FeatureFlagClient() {
			@Override
			public com.pluginpolicyengine.core.FlagDecision evaluate(String key, com.pluginpolicyengine.core.FlagContext ctx) {
				return svc.evaluate(key, ctx);
			}
		};
	}

	@Bean
	@ConditionalOnMissingBean
	public FeatureAccessPolicyEngine featureAccessPolicyEngine(FeatureFlagClient featureFlagClient) {
		return new DefaultFeatureAccessPolicyEngine(featureFlagClient);
	}

	@Bean
	@ConditionalOnMissingBean
	public FeatureFlagContextResolver featureFlagContextResolver() {
		return new DefaultFeatureFlagContextResolver();
	}

	@Bean
	@ConditionalOnMissingBean
	public FeatureDeniedHandler featureDeniedHandler() {
		return new NotFoundFeatureDeniedHandler();
	}

	@Bean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@ConditionalOnClass({HandlerInterceptor.class, WebMvcConfigurer.class})
	@ConditionalOnMissingBean
	public FeatureGateInterceptor featureGateInterceptor(
		FeatureFlagContextResolver contextResolver,
		FeatureAccessPolicyEngine policyEngine,
		FeatureDeniedHandler deniedHandler
	) {
		return new FeatureGateInterceptor(contextResolver, policyEngine, deniedHandler);
	}

	@Bean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@ConditionalOnClass(WebMvcConfigurer.class)
	@ConditionalOnMissingBean
	public FeatureGateWebMvcConfigurer featureGateWebMvcConfigurer(FeatureGateInterceptor interceptor) {
		return new FeatureGateWebMvcConfigurer(interceptor);
	}
}
