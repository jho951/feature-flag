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

@AutoConfiguration
@EnableConfigurationProperties(FeatureFlagProperties.class)
public class FeatureFlagAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "featureflag", name = "store", havingValue = "MEMORY", matchIfMissing = true)
	public FlagStore inMemoryFlagStore() {
		return new InMemoryFlagStore();
	}

	@Bean
	@ConditionalOnProperty(prefix = "featureflag", name = "store", havingValue = "FILE")
	public FlagStore fileFlagStore(FeatureFlagProperties props) {
		if (props.getFilePath() == null || props.getFilePath().isBlank()) {
			throw new IllegalStateException("featureflag.store=FILE 인데 featureflag.file-path가 비어 있습니다.");
		}
		return new JsonFileFlagStore(props.getFilePath(), props.getCacheTtl());
	}

	@Bean
	public FeatureFlagService featureFlagService(FlagStore store) {
		return new FeatureFlagService(store);
	}

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
