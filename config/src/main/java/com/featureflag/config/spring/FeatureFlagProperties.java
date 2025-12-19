package com.featureflag.config.spring;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "featureflag")
public class FeatureFlagProperties {

	/** v1: MEMORY or FILE */
	private Store store = Store.MEMORY;
	private String filePath;
	private Duration cacheTtl = Duration.ofSeconds(3);

	public enum Store { MEMORY, FILE }

	public Store getStore() { return store; }
	public void setStore(Store store) { this.store = store; }

	public String getFilePath() { return filePath; }
	public void setFilePath(String filePath) { this.filePath = filePath; }

	public Duration getCacheTtl() { return cacheTtl; }
	public void setCacheTtl(Duration cacheTtl) { this.cacheTtl = cacheTtl; }
}
