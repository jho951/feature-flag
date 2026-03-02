package com.featureflag.config.spring;

import com.featureflag.api.FeatureFlagClient;
import com.featureflag.core.FeatureFlagService;
import com.featureflag.core.FlagStore;
import com.featureflag.core.store.InMemoryFlagStore;
import com.featureflag.store.file.JsonFileFlagStore;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
	@ConditionalOnProperty(prefix = "featureflag", name = "store", havingValue = "MEMORY", matchIfMissing = true)
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
	@ConditionalOnProperty(prefix = "featureflag", name = "store", havingValue = "FILE")
	public FlagStore fileFlagStore(FeatureFlagProperties props) {
		if (props.getFilePath() == null || props.getFilePath().isBlank()) {
			throw new IllegalStateException("featureflag.store=FILE 인데 featureflag.file-path가 비어 있습니다.");
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
			public boolean isEnabled(String key, com.featureflag.core.FlagContext ctx) {
				return svc.isEnabled(key, ctx);
			}

			@Override
			public String variant(String key, com.featureflag.core.FlagContext ctx, String fallbackVariant) {
				return svc.variant(key, ctx, fallbackVariant);
			}
		};
	}
}
